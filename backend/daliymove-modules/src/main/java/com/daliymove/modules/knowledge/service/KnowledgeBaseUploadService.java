package com.daliymove.modules.knowledge.service;

import com.daliymove.common.exception.BusinessException;
import com.daliymove.common.exception.ErrorCode;
import com.daliymove.modules.file.FileHashService;
import com.daliymove.modules.file.FileStorageService;
import com.daliymove.modules.file.FileValidationService;
import com.daliymove.modules.knowledge.entity.KnowledgeBase;
import com.daliymove.modules.knowledge.enums.VectorStatus;
import com.daliymove.modules.knowledge.listener.VectorizeStreamProducer;
import com.daliymove.modules.knowledge.mapper.KnowledgeBaseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

/**
 * 知识库上传服务
 * - 处理知识库上传、解析的业务逻辑
 * - 向量化改为异步处理，通过 Redis Stream 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeBaseUploadService {

    private final KnowledgeBaseParseService parseService;
    private final KnowledgeBasePersistenceService persistenceService;
    private final FileStorageService storageService;
    private final KnowledgeBaseMapper knowledgeBaseMapper;
    private final FileValidationService fileValidationService;
    private final FileHashService fileHashService;
    private final VectorizeStreamProducer vectorizeStreamProducer;

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    /**
     * 上传知识库文件
     *
     * @param file 知识库文件
     * @param name 知识库名称（可选）
     * @param category 分类（可选）
     * @return 上传结果和存储信息
     */
    public Map<String, Object> uploadKnowledgeBase(MultipartFile file, String name, String category) {
        fileValidationService.validateFile(file, MAX_FILE_SIZE, "知识库");

        String fileName = file.getOriginalFilename();
        log.info("收到知识库上传请求: {}, 大小: {} bytes, category: {}", fileName, file.getSize(), category);

        String contentType = parseService.detectContentType(file);
        validateContentType(contentType, fileName);

        String fileHash = fileHashService.calculateHash(file);
        KnowledgeBase existingKb = knowledgeBaseMapper.findByFileHash(fileHash);
        if (existingKb != null) {
            log.info("检测到重复知识库: hash={}", fileHash);
            return persistenceService.handleDuplicateKnowledgeBase(existingKb, fileHash);
        }

        String content = parseService.parseContent(file);
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "无法从文件中提取文本内容，请确保文件格式正确");
        }

        String fileKey = storageService.uploadKnowledgeBase(file);
        String fileUrl = storageService.getFileUrl(fileKey);
        log.info("知识库已存储到存储服务: {}", fileKey);

        KnowledgeBase savedKb = persistenceService.saveKnowledgeBase(file, name, category, fileKey, fileUrl, fileHash);

        vectorizeStreamProducer.sendVectorizeTask(savedKb.getId(), content);

        log.info("知识库上传完成，向量化任务已入队: {}, kbId={}", fileName, savedKb.getId());

        return Map.of(
            "knowledgeBase", Map.of(
                "id", savedKb.getId(),
                "name", savedKb.getName(),
                "category", savedKb.getCategory() != null ? savedKb.getCategory() : "",
                "fileSize", savedKb.getFileSize(),
                "contentLength", content.length(),
                "vectorStatus", VectorStatus.PENDING.name()
            ),
            "storage", Map.of(
                "fileKey", fileKey,
                "fileUrl", fileUrl
            ),
            "duplicate", false
        );
    }

    /**
     * 验证文件类型
     */
    private void validateContentType(String contentType, String fileName) {
        fileValidationService.validateContentType(
            contentType,
            fileName,
            fileValidationService::isKnowledgeBaseMimeType,
            fileValidationService::isMarkdownExtension,
            "不支持的文件类型: " + contentType + "，支持的类型：PDF、DOCX、DOC、TXT、MD等"
        );
    }

    /**
     * 重新向量化知识库（手动重试）
     *
     * @param kbId 知识库ID
     */
    public void revectorize(Long kbId) {
        KnowledgeBase kb = knowledgeBaseMapper.selectById(kbId);
        if (kb == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "知识库不存在");
        }

        log.info("开始重新向量化知识库: kbId={}, name={}", kbId, kb.getName());

        String content = parseService.downloadAndParseContent(kb.getStorageKey(), kb.getOriginalFilename());
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "无法从文件中提取文本内容");
        }

        persistenceService.updateVectorStatusToPending(kbId);

        vectorizeStreamProducer.sendVectorizeTask(kbId, content);

        log.info("重新向量化任务已发送: kbId={}", kbId);
    }
}