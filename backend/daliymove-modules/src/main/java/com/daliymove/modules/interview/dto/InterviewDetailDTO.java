package com.daliymove.modules.interview.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 面试详情DTO
 * - 包含面试会话的完整信息
 * - 包含所有问题和答案详情
 */
public record InterviewDetailDTO(
    Long id,
    String sessionId,
    Integer totalQuestions,
    String status,
    String evaluateStatus,
    String evaluateError,
    Integer overallScore,
    String overallFeedback,
    LocalDateTime createdAt,
    LocalDateTime completedAt,
    List<Object> questions,
    List<String> strengths,
    List<String> improvements,
    List<Object> referenceAnswers,
    List<AnswerDetailDTO> answers
) {
    /**
     * 答案详情DTO
     * - 单个问题的回答详情
     */
    public record AnswerDetailDTO(
        Integer questionIndex,
        String question,
        String category,
        String userAnswer,
        Integer score,
        String feedback,
        String referenceAnswer,
        List<String> keyPoints,
        LocalDateTime answeredAt
    ) {}
}