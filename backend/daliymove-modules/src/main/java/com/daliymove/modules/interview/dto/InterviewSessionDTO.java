package com.daliymove.modules.interview.dto;

import java.util.List;

/**
 * 面试会话DTO
 * - 包含会话基本信息和问题列表
 * - 包含当前进度和状态
 */
public record InterviewSessionDTO(
    String sessionId,
    String resumeText,
    int totalQuestions,
    int currentQuestionIndex,
    List<InterviewQuestionDTO> questions,
    SessionStatus status
) {
    /**
     * 会话状态枚举
     */
    public enum SessionStatus {
        CREATED,
        IN_PROGRESS,
        COMPLETED,
        EVALUATED
    }
}