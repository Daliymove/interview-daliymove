package com.daliymove.modules.knowledge.dto;

/**
 * 知识库查询响应
 */
public record QueryResponse(
    String answer,
    Long knowledgeBaseId,
    String knowledgeBaseName
) {}