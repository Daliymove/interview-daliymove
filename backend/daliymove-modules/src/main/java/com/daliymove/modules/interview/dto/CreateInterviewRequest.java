package com.daliymove.modules.interview.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 创建面试会话请求
 * - 包含简历文本、题目数量、简历ID
 * - 支持强制创建新会话选项
 */
public record CreateInterviewRequest(
    @NotBlank(message = "简历文本不能为空")
    String resumeText,

    @Min(value = 3, message = "题目数量最少3题")
    @Max(value = 20, message = "题目数量最多20题")
    int questionCount,

    @NotNull(message = "简历ID不能为空")
    Long resumeId,

    Boolean forceCreate
) {}