package com.daliymove.modules.resume.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 简历分析结果实体类
 * - 存储 AI 对简历的分析结果
 * - 包含各维度评分
 * - 包含优点和改进建议
 */
@Data
@TableName("resume_analysis")
public class ResumeAnalysis {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联的简历 ID */
    @TableField("resume_id")
    private Long resumeId;

    /** 总分（0-100） */
    @TableField("overall_score")
    private Integer overallScore;

    /** 内容完整性评分（0-25） */
    @TableField("content_score")
    private Integer contentScore;

    /** 结构清晰度评分（0-20） */
    @TableField("structure_score")
    private Integer structureScore;

    /** 技能匹配度评分（0-25） */
    @TableField("skill_match_score")
    private Integer skillMatchScore;

    /** 表达专业性评分（0-15） */
    @TableField("expression_score")
    private Integer expressionScore;

    /** 项目经验评分（0-15） */
    @TableField("project_score")
    private Integer projectScore;

    /** 简历摘要 */
    private String summary;

    /** 优点列表（JSON 格式） */
    @TableField("strengths_json")
    private String strengthsJson;

    /** 改进建议列表（JSON 格式） */
    @TableField("suggestions_json")
    private String suggestionsJson;

    /** 评测时间 */
    @TableField("analyzed_at")
    private LocalDateTime analyzedAt;

    /** 创建时间，自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}