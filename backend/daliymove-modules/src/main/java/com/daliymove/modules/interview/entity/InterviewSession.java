package com.daliymove.modules.interview.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 面试会话实体类
 * - 存储面试会话信息
 * - 关联简历，支持异步评估状态追踪
 * - 包含面试问题、答案评估和总体反馈
 */
@Data
@TableName("interview_session")
public class InterviewSession {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 会话ID（UUID） */
    @TableField("session_id")
    private String sessionId;

    /** 关联的简历ID */
    @TableField("resume_id")
    private Long resumeId;

    /** 问题总数 */
    @TableField("total_questions")
    private Integer totalQuestions;

    /** 当前问题索引 */
    @TableField("current_question_index")
    private Integer currentQuestionIndex;

    /**
     * 会话状态
     * 使用 SessionStatus 枚举常量：
     * - CREATED: 已创建
     * - IN_PROGRESS: 进行中
     * - COMPLETED: 已完成
     * - EVALUATED: 已评估
     */
    @TableField("status")
    private String status;

    /** 问题列表（JSON格式） */
    @TableField("questions_json")
    private String questionsJson;

    /** 总分（0-100） */
    @TableField("overall_score")
    private Integer overallScore;

    /** 总体评价 */
    @TableField("overall_feedback")
    private String overallFeedback;

    /** 优势列表（JSON格式） */
    @TableField("strengths_json")
    private String strengthsJson;

    /** 改进建议列表（JSON格式） */
    @TableField("improvements_json")
    private String improvementsJson;

    /** 参考答案列表（JSON格式） */
    @TableField("reference_answers_json")
    private String referenceAnswersJson;

    /** 创建时间，自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间，自动填充 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 完成时间 */
    @TableField("completed_at")
    private LocalDateTime completedAt;

    /** 评估状态：PENDING-待处理，PROCESSING-处理中，COMPLETED-完成，FAILED-失败 */
    @TableField("evaluate_status")
    private String evaluateStatus;

    /** 评估错误信息（失败时记录） */
    @TableField("evaluate_error")
    private String evaluateError;

    /**
     * 会话状态枚举
     */
    public enum SessionStatus {
        CREATED,
        IN_PROGRESS,
        COMPLETED,
        EVALUATED
    }
}