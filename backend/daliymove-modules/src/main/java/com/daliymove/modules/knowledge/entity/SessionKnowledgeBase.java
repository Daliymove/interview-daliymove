package com.daliymove.modules.knowledge.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会话知识库关联实体类
 * - 实现RagChatSession和KnowledgeBase的多对多关系
 */
@Data
@TableName("session_knowledge_base")
public class SessionKnowledgeBase {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 会话ID */
    @TableField("session_id")
    private Long sessionId;

    /** 知识库ID */
    @TableField("knowledge_base_id")
    private Long knowledgeBaseId;

    /** 创建时间，自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}