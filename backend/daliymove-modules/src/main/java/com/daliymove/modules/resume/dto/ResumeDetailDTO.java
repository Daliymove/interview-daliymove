package com.daliymove.modules.resume.dto;

import com.daliymove.common.enums.AsyncTaskStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 简历详情DTO
 */
public record ResumeDetailDTO(
    Long id,
    String filename,
    Long fileSize,
    String contentType,
    String storageUrl,
    LocalDateTime uploadedAt,
    Integer accessCount,
    String resumeText,
    AsyncTaskStatus analyzeStatus,
    String analyzeError,
    List<AnalysisHistoryDTO> analyses,
    List<Object> interviews
) {
    /**
     * 分析历史DTO
     */
    public record AnalysisHistoryDTO(
        Long id,
        Integer overallScore,
        Integer contentScore,
        Integer structureScore,
        Integer skillMatchScore,
        Integer expressionScore,
        Integer projectScore,
        String summary,
        LocalDateTime analyzedAt,
        List<String> strengths,
        List<Object> suggestions
    ) {}

    /**
     * 面试历史DTO
     */
    public record InterviewHistoryDTO(
        Long id,
        String sessionId,
        Integer totalQuestions,
        Integer overallScore,
        String status,
        LocalDateTime createdAt
    ) {}
}