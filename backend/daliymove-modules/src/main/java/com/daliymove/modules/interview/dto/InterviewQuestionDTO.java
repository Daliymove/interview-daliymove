package com.daliymove.modules.interview.dto;

/**
 * 面试问题DTO
 * - 包含问题内容、类型、类别
 * - 支持追问标记
 */
public record InterviewQuestionDTO(
    int questionIndex,
    String question,
    QuestionType type,
    String category,
    String userAnswer,
    Integer score,
    String feedback,
    boolean isFollowUp,
    Integer parentQuestionIndex
) {
    /**
     * 问题类型枚举
     */
    public enum QuestionType {
        PROJECT,
        JAVA_BASIC,
        JAVA_COLLECTION,
        JAVA_CONCURRENT,
        MYSQL,
        REDIS,
        SPRING,
        SPRING_BOOT
    }

    /**
     * 创建新问题（未回答状态）
     */
    public static InterviewQuestionDTO create(int index, String question, QuestionType type, String category) {
        return new InterviewQuestionDTO(index, question, type, category, null, null, null, false, null);
    }

    /**
     * 创建新问题（支持追问标记）
     */
    public static InterviewQuestionDTO create(
            int index,
            String question,
            QuestionType type,
            String category,
            boolean isFollowUp,
            Integer parentQuestionIndex) {
        return new InterviewQuestionDTO(index, question, type, category, null, null, null, isFollowUp, parentQuestionIndex);
    }

    /**
     * 添加用户回答
     */
    public InterviewQuestionDTO withAnswer(String answer) {
        return new InterviewQuestionDTO(
            questionIndex, question, type, category, answer, score, feedback, isFollowUp, parentQuestionIndex);
    }

    /**
     * 添加评分和反馈
     */
    public InterviewQuestionDTO withEvaluation(int score, String feedback) {
        return new InterviewQuestionDTO(
            questionIndex, question, type, category, userAnswer, score, feedback, isFollowUp, parentQuestionIndex);
    }
}