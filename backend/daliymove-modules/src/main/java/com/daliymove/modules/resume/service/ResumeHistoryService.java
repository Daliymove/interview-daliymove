package com.daliymove.modules.resume.service;

import com.daliymove.common.exception.BusinessException;
import com.daliymove.common.exception.ErrorCode;
import com.daliymove.modules.export.PdfExportService;
import com.daliymove.modules.interview.entity.InterviewSession;
import com.daliymove.modules.interview.service.InterviewPersistenceService;
import com.daliymove.modules.resume.converter.ResumeConverter;
import com.daliymove.modules.resume.dto.ResumeAnalysisResponse;
import com.daliymove.modules.resume.dto.ResumeDetailDTO;
import com.daliymove.modules.resume.dto.ResumeListItemDTO;
import com.daliymove.modules.resume.entity.Resume;
import com.daliymove.modules.resume.entity.ResumeAnalysis;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 简历历史服务
 * - 简历历史查询和导出简历分析报告
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeHistoryService {

    private final ResumePersistenceService resumePersistenceService;
    private final InterviewPersistenceService interviewPersistenceService;
    private final PdfExportService pdfExportService;
    private final ObjectMapper objectMapper;
    private final ResumeConverter resumeConverter;

    /**
     * 获取所有简历列表
     *
     * @return 简历列表
     */
    public List<ResumeListItemDTO> getAllResumes() {
        List<Resume> resumes = resumePersistenceService.findAllResumes();

        return resumes.stream().map(resume -> {
            Integer latestScore = null;
            java.time.LocalDateTime lastAnalyzedAt = null;
            Optional<ResumeAnalysis> analysisOpt = resumePersistenceService.getLatestAnalysis(resume.getId());
            if (analysisOpt.isPresent()) {
                ResumeAnalysis analysis = analysisOpt.get();
                latestScore = analysis.getOverallScore();
                lastAnalyzedAt = analysis.getAnalyzedAt();
            }

            int interviewCount = interviewPersistenceService.findByResumeId(resume.getId()).size();

            return resumeConverter.toListItemDTO(
                    resume,
                    latestScore,
                    lastAnalyzedAt,
                    interviewCount
            );
        }).toList();
    }

    /**
     * 获取简历详情（包含分析历史）
     *
     * @param id 简历ID
     * @return 简历详情DTO
     */
    public ResumeDetailDTO getResumeDetail(Long id) {
        Optional<Resume> resumeOpt = resumePersistenceService.findById(id);
        if (resumeOpt.isEmpty()) {
            throw new BusinessException(ErrorCode.RESUME_NOT_FOUND);
        }

        Resume resume = resumeOpt.get();

        List<ResumeAnalysis> analyses = resumePersistenceService.findAnalysesByResumeId(id);
        List<ResumeDetailDTO.AnalysisHistoryDTO> analysisHistory = resumeConverter.toAnalysisHistoryDTOList(
                analyses,
                this::extractStrengths,
                this::extractSuggestions
        );

        List<InterviewSession> interviewSessions = interviewPersistenceService.findByResumeId(id);
        List<Object> interviewHistory = convertInterviewHistory(interviewSessions);

        return new ResumeDetailDTO(
                resume.getId(),
                resume.getOriginalFilename(),
                resume.getFileSize(),
                resume.getContentType(),
                resume.getStorageUrl(),
                resume.getUploadedAt(),
                resume.getAccessCount(),
                resume.getResumeText(),
                convertAnalyzeStatus(resume.getAnalyzeStatus()),
                resume.getAnalyzeError(),
                analysisHistory,
                interviewHistory
        );
    }

    /**
     * 从 JSON 提取 strengths
     *
     * @param entity 分析实体
     * @return 优点列表
     */
    private List<String> extractStrengths(ResumeAnalysis entity) {
        try {
            if (entity.getStrengthsJson() != null) {
                return objectMapper.readValue(
                        entity.getStrengthsJson(),
                        new TypeReference<List<String>>() {}
                );
            }
        } catch (JacksonException e) {
            log.error("解析 strengths JSON 失败", e);
        }
        return List.of();
    }

    /**
     * 从 JSON 提取 suggestions
     *
     * @param entity 分析实体
     * @return 建议列表
     */
    private List<Object> extractSuggestions(ResumeAnalysis entity) {
        try {
            if (entity.getSuggestionsJson() != null) {
                return objectMapper.readValue(
                        entity.getSuggestionsJson(),
                        new TypeReference<List<Object>>() {}
                );
            }
        } catch (JacksonException e) {
            log.error("解析 suggestions JSON 失败", e);
        }
        return List.of();
    }

    /**
     * 转换面试历史
     *
     * @param sessions 面试会话列表
     * @return 面试历史列表
     */
    private List<Object> convertInterviewHistory(List<InterviewSession> sessions) {
        return sessions.stream()
                .map(session -> (Object) new ResumeDetailDTO.InterviewHistoryDTO(
                        session.getId(),
                        session.getSessionId(),
                        session.getTotalQuestions(),
                        session.getOverallScore(),
                        session.getStatus(),
                        session.getCreatedAt()
                ))
                .toList();
    }

    /**
     * 转换分析状态字符串为枚举
     *
     * @param status 状态字符串
     * @return 状态枚举
     */
    private com.daliymove.common.enums.AsyncTaskStatus convertAnalyzeStatus(String status) {
        if (status == null) {
            return com.daliymove.common.enums.AsyncTaskStatus.PENDING;
        }
        try {
            return com.daliymove.common.enums.AsyncTaskStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return com.daliymove.common.enums.AsyncTaskStatus.PENDING;
        }
    }

    /**
     * 导出简历分析报告为PDF
     *
     * @param resumeId 简历ID
     * @return 导出结果
     */
    public ExportResult exportAnalysisPdf(Long resumeId) {
        Optional<Resume> resumeOpt = resumePersistenceService.findById(resumeId);
        if (resumeOpt.isEmpty()) {
            throw new BusinessException(ErrorCode.RESUME_NOT_FOUND);
        }

        Resume resume = resumeOpt.get();
        Optional<ResumeAnalysisResponse> analysisOpt = resumePersistenceService.getLatestAnalysisAsDTO(resumeId);
        if (analysisOpt.isEmpty()) {
            throw new BusinessException(ErrorCode.RESUME_ANALYSIS_NOT_FOUND);
        }

        try {
            byte[] pdfBytes = pdfExportService.exportResumeAnalysis(resume, analysisOpt.get());
            String filename = "简历分析报告_" + resume.getOriginalFilename() + ".pdf";

            return new ExportResult(pdfBytes, filename);
        } catch (Exception e) {
            log.error("导出PDF失败: resumeId={}", resumeId, e);
            throw new BusinessException(ErrorCode.EXPORT_PDF_FAILED, "导出PDF失败: " + e.getMessage());
        }
    }

    /**
     * PDF导出结果
     */
    public record ExportResult(byte[] pdfBytes, String filename) {}
}