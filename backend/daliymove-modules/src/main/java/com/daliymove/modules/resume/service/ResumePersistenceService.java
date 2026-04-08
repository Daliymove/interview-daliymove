package com.daliymove.modules.resume.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.daliymove.common.exception.BusinessException;
import com.daliymove.common.exception.ErrorCode;
import com.daliymove.modules.file.FileHashService;
import com.daliymove.modules.resume.converter.ResumeConverter;
import com.daliymove.modules.resume.dto.ResumeAnalysisResponse;
import com.daliymove.modules.resume.entity.Resume;
import com.daliymove.modules.resume.entity.ResumeAnalysis;
import com.daliymove.modules.resume.mapper.ResumeAnalysisMapper;
import com.daliymove.modules.resume.mapper.ResumeMapper;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 简历持久化服务
 * - 简历和评测结果的持久化
 * - 简历删除时删除所有关联数据
 * - 使用 MyBatis-Plus 进行数据库操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumePersistenceService {

    private final ResumeMapper resumeMapper;
    private final ResumeAnalysisMapper analysisMapper;
    private final ObjectMapper objectMapper;
    private final ResumeConverter resumeConverter;
    private final FileHashService fileHashService;

    /**
     * 检查简历是否已存在（基于文件内容hash）
     *
     * @param file 上传的文件
     * @return 如果存在返回已有的简历实体，否则返回空
     */
    public Optional<Resume> findExistingResume(MultipartFile file) {
        try {
            String fileHash = fileHashService.calculateHash(file);
            Optional<Resume> existing = resumeMapper.findByFileHash(fileHash);

            if (existing.isPresent()) {
                log.info("检测到重复简历: hash={}", fileHash);
                Resume resume = existing.get();
                resume.incrementAccessCount();
                resumeMapper.updateById(resume);
            }

            return existing;
        } catch (Exception e) {
            log.error("检查简历重复时出错: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 保存新简历
     *
     * @param file       上传的文件
     * @param resumeText 解析后的文本内容
     * @param storageKey 存储键
     * @param storageUrl 存储URL
     * @return 保存后的简历实体
     */
    @Transactional(rollbackFor = Exception.class)
    public Resume saveResume(MultipartFile file, String resumeText,
                             String storageKey, String storageUrl) {
        try {
            String fileHash = fileHashService.calculateHash(file);

            Resume resume = new Resume();
            resume.setFileHash(fileHash);
            resume.setOriginalFilename(file.getOriginalFilename());
            resume.setFileSize(file.getSize());
            resume.setContentType(file.getContentType());
            resume.setStorageKey(storageKey);
            resume.setStorageUrl(storageUrl);
            resume.setResumeText(resumeText);
            resume.setUploadedAt(LocalDateTime.now());
            resume.setAccessCount(0);

            resumeMapper.insert(resume);
            log.info("简历已保存: id={}, hash={}", resume.getId(), fileHash);

            return resume;
        } catch (Exception e) {
            log.error("保存简历失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.RESUME_UPLOAD_FAILED, "保存简历失败");
        }
    }

    /**
     * 保存简历评测结果
     *
     * @param resume   简历实体
     * @param analysis 分析结果
     * @return 保存后的分析实体
     */
    @Transactional(rollbackFor = Exception.class)
    public ResumeAnalysis saveAnalysis(Resume resume, ResumeAnalysisResponse analysis) {
        try {
            ResumeAnalysis entity = resumeConverter.toAnalysisEntity(analysis);
            entity.setResumeId(resume.getId());
            entity.setAnalyzedAt(LocalDateTime.now());

            entity.setStrengthsJson(objectMapper.writeValueAsString(analysis.strengths()));
            entity.setSuggestionsJson(objectMapper.writeValueAsString(analysis.suggestions()));

            analysisMapper.insert(entity);
            log.info("简历评测结果已保存: analysisId={}, resumeId={}, score={}",
                    entity.getId(), resume.getId(), analysis.overallScore());

            return entity;
        } catch (JacksonException e) {
            log.error("序列化评测结果失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.RESUME_ANALYSIS_FAILED, "保存评测结果失败");
        }
    }

    /**
     * 获取简历的最新评测结果
     *
     * @param resumeId 简历ID
     * @return 最新的评测结果
     */
    public Optional<ResumeAnalysis> getLatestAnalysis(Long resumeId) {
        ResumeAnalysis analysis = analysisMapper.findFirstByResumeIdOrderByAnalyzedAtDesc(resumeId);
        return Optional.ofNullable(analysis);
    }

    /**
     * 获取简历的最新评测结果（返回DTO）
     *
     * @param resumeId 简历ID
     * @return 最新的评测结果DTO
     */
    public Optional<ResumeAnalysisResponse> getLatestAnalysisAsDTO(Long resumeId) {
        return getLatestAnalysis(resumeId).map(this::entityToDTO);
    }

    /**
     * 获取所有简历列表
     *
     * @return 所有简历列表
     */
    public List<Resume> findAllResumes() {
        return resumeMapper.selectList(new LambdaQueryWrapper<Resume>()
                .orderByDesc(Resume::getUploadedAt));
    }

    /**
     * 获取简历的所有评测记录
     *
     * @param resumeId 简历ID
     * @return 评测记录列表
     */
    public List<ResumeAnalysis> findAnalysesByResumeId(Long resumeId) {
        return analysisMapper.findByResumeIdOrderByAnalyzedAtDesc(resumeId);
    }

    /**
     * 将实体转换为DTO
     *
     * @param entity 分析实体
     * @return 分析响应DTO
     */
    public ResumeAnalysisResponse entityToDTO(ResumeAnalysis entity) {
        try {
            List<String> strengths = objectMapper.readValue(
                    entity.getStrengthsJson() != null ? entity.getStrengthsJson() : "[]",
                    new TypeReference<List<String>>() {}
            );

            List<ResumeAnalysisResponse.Suggestion> suggestions = objectMapper.readValue(
                    entity.getSuggestionsJson() != null ? entity.getSuggestionsJson() : "[]",
                    new TypeReference<List<ResumeAnalysisResponse.Suggestion>>() {}
            );

            Resume resume = resumeMapper.selectById(entity.getResumeId());
            String resumeText = resume != null ? resume.getResumeText() : "";

            return new ResumeAnalysisResponse(
                    entity.getOverallScore(),
                    resumeConverter.toScoreDetail(entity),
                    entity.getSummary(),
                    strengths,
                    suggestions,
                    resumeText
            );
        } catch (JacksonException e) {
            log.error("反序列化评测结果失败: {}", e.getMessage());
            throw new BusinessException(ErrorCode.RESUME_ANALYSIS_FAILED, "获取评测结果失败");
        }
    }

    /**
     * 根据ID获取简历
     *
     * @param id 简历ID
     * @return 简历实体
     */
    public Optional<Resume> findById(Long id) {
        Resume resume = resumeMapper.selectById(id);
        return Optional.ofNullable(resume);
    }

    /**
     * 删除简历及其所有关联数据
     * 包括：简历分析记录、面试会话（会自动删除面试答案）
     *
     * @param id 简历ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteResume(Long id) {
        Resume resume = resumeMapper.selectById(id);
        if (resume == null) {
            throw new BusinessException(ErrorCode.RESUME_NOT_FOUND);
        }

        List<ResumeAnalysis> analyses = analysisMapper.findByResumeIdOrderByAnalyzedAtDesc(id);
        if (!analyses.isEmpty()) {
            analyses.forEach(a -> analysisMapper.deleteById(a.getId()));
            log.info("已删除 {} 条简历分析记录", analyses.size());
        }

        resumeMapper.deleteById(id);
        log.info("简历已删除: id={}, filename={}", id, resume.getOriginalFilename());
    }

    /**
     * 更新简历分析状态
     *
     * @param id     简历ID
     * @param status 分析状态
     * @param error  错误信息（可选）
     */
    public void updateAnalyzeStatus(Long id, String status, String error) {
        Resume resume = resumeMapper.selectById(id);
        if (resume != null) {
            resume.setAnalyzeStatus(status);
            resume.setAnalyzeError(error);
            resumeMapper.updateById(resume);
        }
    }
}