package com.daliymove.modules.knowledge.dto;

/**
 * 知识库统计信息DTO
 */
public record KnowledgeBaseStatsDTO(
    long totalCount,
    long totalQuestionCount,
    long totalAccessCount,
    long completedCount,
    long processingCount
) {
}