package com.daliymove.modules.interview.dto;

import java.util.List;

/**
 * 面试评估报告
 * - 包含总分、各类别得分
 * - 包含总体评价、优势和改进建议
 */
public record InterviewReportDTO(
    String sessionId,
    int totalQuestions,
    int overallScore,
    List<CategoryScore> categoryScores,
    List<QuestionEvaluation> questionDetails,
    String overallFeedback,
    List<String> strengths,
    List<String> improvements,
    List<ReferenceAnswer> referenceAnswers
) {
    /**
     * 类别得分
     */
    public record CategoryScore(
        String category,
        int score,
        int questionCount
    ) {}

    /**
     * 问题评估详情
     */
    public record QuestionEvaluation(
        int questionIndex,
        String question,
        String category,
        String userAnswer,
        int score,
        String feedback
    ) {}

    /**
     * 参考答案
     */
    public record ReferenceAnswer(
        int questionIndex,
        String question,
        String referenceAnswer,
        List<String> keyPoints
    ) {}
}