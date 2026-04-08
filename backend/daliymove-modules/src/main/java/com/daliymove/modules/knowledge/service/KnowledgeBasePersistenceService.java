package com.daliymove.modules.knowledge.service;

import com.daliymove.common.exception.BusinessException;
import com.daliymove.common.exception.ErrorCode;
import com.daliymove.modules.knowledge.entity.KnowledgeBase;
import com.daliymove.modules.knowledge.enums.VectorStatus;
import com.daliymove.modules.knowledge.mapper.KnowledgeBaseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 知识库持久化服务
 * - 处理所有需要事务的数据库操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeBasePersistenceService {

    private final KnowledgeBaseMapper knowledgeBaseMapper;

    /**
     * 处理重复知识库（更新访问计数）
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> handleDuplicateKnowledgeBase(KnowledgeBase kb, String fileHash) {
        log.info("检测到重复知识库，返回已有记录: kbId={}", kb.getId());

        kb.incrementAccessCount();
        knowledgeBaseMapper.updateById(kb);

        return Map.of(
            "knowledgeBase", Map.of(
                "id", kb.getId(),
                "name", kb.getName(),
                "fileSize", kb.getFileSize(),
                "contentLength", 0
            ),
            "storage", Map.of(
                "fileKey", kb.getStorageKey() != null ? kb.getStorageKey() : "",
                "fileUrl", kb.getStorageUrl() != null ? kb.getStorageUrl() : ""
            ),
            "duplicate", true
        );
    }

    /**
     * 保存新知识库元数据到数据库
     */
    @Transactional(rollbackFor = Exception.class)
    public KnowledgeBase saveKnowledgeBase(MultipartFile file, String name, String category,
                                            String storageKey, String storageUrl, String fileHash) {
        try {
            KnowledgeBase kb = new KnowledgeBase();
            kb.setFileHash(fileHash);
            kb.setName(name != null && !name.trim().isEmpty() ? name : extractNameFromFilename(file.getOriginalFilename()));
            kb.setCategory(category != null && !category.trim().isEmpty() ? category.trim() : null);
            kb.setOriginalFilename(file.getOriginalFilename());
            kb.setFileSize(file.getSize());
            kb.setContentType(file.getContentType());
            kb.setStorageKey(storageKey);
            kb.setStorageUrl(storageUrl);
            kb.setVectorStatus(VectorStatus.PENDING.name());
            kb.setAccessCount(0);
            kb.setQuestionCount(0);

            knowledgeBaseMapper.insert(kb);
            log.info("知识库已保存: id={}, name={}, category={}, hash={}", kb.getId(), kb.getName(), kb.getCategory(), fileHash);
            return kb;
        } catch (Exception e) {
            log.error("保存知识库失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "保存知识库失败");
        }
    }

    /**
     * 更新知识库向量化状态为 PENDING
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateVectorStatusToPending(Long kbId) {
        KnowledgeBase kb = knowledgeBaseMapper.selectById(kbId);
        if (kb == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "知识库不存在");
        }

        kb.setVectorStatus(VectorStatus.PENDING.name());
        kb.setVectorError(null);
        knowledgeBaseMapper.updateById(kb);

        log.info("知识库向量化状态已更新为 PENDING: kbId={}", kbId);
    }

    /**
     * 从文件名提取知识库名称（去除扩展名）
     */
    private String extractNameFromFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "未命名知识库";
        }
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0) {
            return filename.substring(0, lastDot);
        }
        return filename;
    }
}