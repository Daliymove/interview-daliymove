package com.daliymove.modules.resume.dto;

import java.util.List;

/**
 * 简历分析响应DTO
 */
public record ResumeAnalysisResponse(
    int overallScore,
    ScoreDetail scoreDetail,
    String summary,
    List<String> strengths,
    List<Suggestion> suggestions,
    String originalText
) {
    
    /**
     * 各维度评分详情
     */
    public record ScoreDetail(
        int contentScore,
        int structureScore,
        int skillMatchScore,
        int expressionScore,
        int projectScore
    ) {}
    
    /**
     * 改进建议
     */
    public record Suggestion(
        String category,
        String priority,
        String issue,
        String recommendation
    ) {}
}