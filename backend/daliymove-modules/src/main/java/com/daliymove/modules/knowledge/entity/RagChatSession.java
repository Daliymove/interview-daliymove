package com.daliymove.modules.knowledge.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * RAG 聊天会话实体类
 * - 一个会话可以关联多个知识库
 * - 包含会话的基本信息和状态
 */
@Data
@TableName("rag_chat_session")
public class RagChatSession {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 会话标题（可自动生成或用户自定义） */
    @TableField("title")
    private String title;

    /** 会话状态：ACTIVE-活跃会话，ARCHIVED-已归档 */
    @TableField("status")
    private String status;

    /** 消息数量（冗余字段，方便查询） */
    @TableField("message_count")
    private Integer messageCount;

    /** 是否置顶 */
    @TableField("is_pinned")
    private Boolean isPinned;

    /** 创建时间，自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间（最后一次消息时间），自动填充 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}