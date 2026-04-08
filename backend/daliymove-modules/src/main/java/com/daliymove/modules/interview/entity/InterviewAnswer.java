package com.daliymove.modules.interview.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 面试答案实体类
 * - 存储面试过程中的问答对和评估结果
 * - 包含问题内容、用户答案、得分和反馈
 */
@Data
@TableName("interview_answer")
public class InterviewAnswer {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的会话ID（外键）
     * 注意：此字段是 interview_session 表的主键 id（BIGINT），而非 interview_session.session_id（UUID）
     */
    @TableField("session_id")
    private Long sessionId;

    /** 问题索引 */
    @TableField("question_index")
    private Integer questionIndex;

    /** 问题内容 */
    @TableField("question")
    private String question;

    /** 问题类别 */
    @TableField("category")
    private String category;

    /** 用户答案 */
    @TableField("user_answer")
    private String userAnswer;

    /** 得分（0-100） */
    @TableField("score")
    private Integer score;

    /** 反馈评价 */
    @TableField("feedback")
    private String feedback;

    /** 参考答案 */
    @TableField("reference_answer")
    private String referenceAnswer;

    /** 关键点列表（JSON格式） */
    @TableField("key_points_json")
    private String keyPointsJson;

    /** 回答时间，自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime answeredAt;
}