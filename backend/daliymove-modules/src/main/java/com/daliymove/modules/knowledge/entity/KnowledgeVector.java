package com.daliymove.modules.knowledge.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识库向量实体类（占位）
 * - TODO: MySQL不原生支持向量存储
 * - 实际向量数据通过Spring AI VectorStore存储
 * - 此实体用于可能的MySQL向量方案预留
 */
@Data
@TableName("knowledge_vector")
public class KnowledgeVector {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 知识库ID */
    @TableField("knowledge_base_id")
    private Long knowledgeBaseId;

    /** 文档片段ID（对应Spring AI Document ID） */
    @TableField("chunk_id")
    private String chunkId;

    /** 文档片段文本内容 */
    @TableField("content")
    private String content;

    /** 向量数据（BLOB存储，占位） */
    @TableField("embedding")
    private byte[] embedding;

    /** 创建时间，自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}