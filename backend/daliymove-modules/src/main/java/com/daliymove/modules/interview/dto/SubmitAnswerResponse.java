package com.daliymove.modules.interview.dto;

/**
 * 提交答案响应
 * - 包含下一题信息和当前进度
 */
public record SubmitAnswerResponse(
    boolean hasNextQuestion,
    InterviewQuestionDTO nextQuestion,
    int currentIndex,
    int totalQuestions
) {}