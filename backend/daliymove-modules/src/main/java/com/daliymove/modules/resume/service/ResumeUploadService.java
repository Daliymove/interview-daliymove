package com.daliymove.modules.resume.service;

import com.daliymove.common.config.AppConfigProperties;
import com.daliymove.common.enums.AsyncTaskStatus;
import com.daliymove.common.exception.BusinessException;
import com.daliymove.common.exception.ErrorCode;
import com.daliymove.modules.file.FileStorageService;
import com.daliymove.modules.file.FileValidationService;
import com.daliymove.modules.resume.dto.ResumeAnalysisResponse;
import com.daliymove.modules.resume.entity.Resume;
import com.daliymove.modules.resume.listener.AnalyzeStreamProducer;
import com.daliymove.modules.resume.mapper.ResumeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

/**
 * 简历上传服务
 * - 处理简历上传、解析的业务逻辑
 * - AI 分析改为异步处理，通过 Redis Stream 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeUploadService {

    private final ResumeParseService parseService;
    private final FileStorageService storageService;
    private final ResumePersistenceService persistenceService;
    private final AppConfigProperties appConfig;
    private final FileValidationService fileValidationService;
    private final AnalyzeStreamProducer analyzeStreamProducer;
    private final ResumeMapper resumeMapper;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 上传并分析简历（异步）
     *
     * @param file 简历文件
     * @return 上传结果（分析将异步进行）
     */
    public Map<String, Object> uploadAndAnalyze(MultipartFile file) {
        fileValidationService.validateFile(file, MAX_FILE_SIZE, "简历");

        String fileName = file.getOriginalFilename();
        log.info("收到简历上传请求: {}, 大小: {} bytes", fileName, file.getSize());

        String contentType = parseService.detectContentType(file);
        validateContentType(contentType);

        Optional<Resume> existingResume = persistenceService.findExistingResume(file);
        if (existingResume.isPresent()) {
            return handleDuplicateResume(existingResume.get());
        }

        String resumeText = parseService.parseResume(file);
        if (resumeText == null || resumeText.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.RESUME_PARSE_FAILED, "无法从文件中提取文本内容，请确保文件不是扫描版PDF");
        }

        String fileKey = storageService.uploadResume(file);
        String fileUrl = storageService.getFileUrl(fileKey);
        log.info("简历已存储到RustFS: {}", fileKey);

        Resume savedResume = persistenceService.saveResume(file, resumeText, fileKey, fileUrl);
        savedResume.setAnalyzeStatus(AsyncTaskStatus.PENDING.name());
        resumeMapper.updateById(savedResume);

        analyzeStreamProducer.sendAnalyzeTask(savedResume.getId(), resumeText);

        log.info("简历上传完成，分析任务已入队: {}, resumeId={}", fileName, savedResume.getId());

        return Map.of(
                "resume", Map.of(
                        "id", savedResume.getId(),
                        "filename", savedResume.getOriginalFilename(),
                        "analyzeStatus", AsyncTaskStatus.PENDING.name()
                ),
                "storage", Map.of(
                        "fileKey", fileKey,
                        "fileUrl", fileUrl,
                        "resumeId", savedResume.getId()
                ),
                "duplicate", false
        );
    }

    /**
     * 验证文件类型
     *
     * @param contentType MIME类型
     */
    private void validateContentType(String contentType) {
        fileValidationService.validateContentTypeByList(
                contentType,
                appConfig.getAllowedTypes(),
                "不支持的文件类型: " + contentType
        );
    }

    /**
     * 处理重复简历
     *
     * @param resume 已存在的简历
     * @return 重复简历的处理结果
     */
    private Map<String, Object> handleDuplicateResume(Resume resume) {
        log.info("检测到重复简历，返回历史分析结果: resumeId={}", resume.getId());

        Optional<ResumeAnalysisResponse> analysisOpt = persistenceService.getLatestAnalysisAsDTO(resume.getId());

        return analysisOpt.map(analysis -> Map.of(
                "analysis", analysis,
                "storage", Map.of(
                        "fileKey", resume.getStorageKey() != null ? resume.getStorageKey() : "",
                        "fileUrl", resume.getStorageUrl() != null ? resume.getStorageUrl() : "",
                        "resumeId", resume.getId()
                ),
                "duplicate", true
        )).orElseGet(() -> Map.of(
                "resume", Map.of(
                        "id", resume.getId(),
                        "filename", resume.getOriginalFilename(),
                        "analyzeStatus", resume.getAnalyzeStatus() != null ? resume.getAnalyzeStatus() : AsyncTaskStatus.PENDING.name()
                ),
                "storage", Map.of(
                        "fileKey", resume.getStorageKey() != null ? resume.getStorageKey() : "",
                        "fileUrl", resume.getStorageUrl() != null ? resume.getStorageUrl() : "",
                        "resumeId", resume.getId()
                ),
                "duplicate", true
        ));
    }

    /**
     * 重新分析简历（手动重试）
     * 从数据库获取简历文本并发送分析任务
     *
     * @param resumeId 简历ID
     */
    @Transactional
    public void reanalyze(Long resumeId) {
        Resume resume = resumeMapper.selectById(resumeId);
        if (resume == null) {
            throw new BusinessException(ErrorCode.RESUME_NOT_FOUND, "简历不存在");
        }

        log.info("开始重新分析简历: resumeId={}, filename={}", resumeId, resume.getOriginalFilename());

        String resumeText = resume.getResumeText();
        if (resumeText == null || resumeText.trim().isEmpty()) {
            resumeText = parseService.downloadAndParseContent(resume.getStorageKey(), resume.getOriginalFilename());
            if (resumeText == null || resumeText.trim().isEmpty()) {
                throw new BusinessException(ErrorCode.RESUME_PARSE_FAILED, "无法获取简历文本内容");
            }
            resume.setResumeText(resumeText);
        }

        resume.setAnalyzeStatus(AsyncTaskStatus.PENDING.name());
        resume.setAnalyzeError(null);
        resumeMapper.updateById(resume);

        analyzeStreamProducer.sendAnalyzeTask(resumeId, resumeText);

        log.info("重新分析任务已发送: resumeId={}", resumeId);
    }
}