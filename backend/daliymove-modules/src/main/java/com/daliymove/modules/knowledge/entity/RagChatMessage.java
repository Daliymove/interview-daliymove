package com.daliymove.modules.knowledge.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * RAG 聊天消息实体类
 * - 存储用户问题和 AI 回答
 * - 支持流式响应
 */
@Data
@TableName("rag_chat_message")
public class RagChatMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联的会话 ID */
    @TableField("session_id")
    private Long sessionId;

    /** 消息类型：USER-用户消息，ASSISTANT-AI 回答 */
    @TableField("type")
    private String type;

    /** 消息内容 */
    @TableField("content")
    private String content;

    /** 消息顺序（用于排序） */
    @TableField("message_order")
    private Integer messageOrder;

    /** 是否完成（流式响应时使用） */
    @TableField("completed")
    private Boolean completed;

    /** 创建时间，自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间（用于流式响应更新），自动填充 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}