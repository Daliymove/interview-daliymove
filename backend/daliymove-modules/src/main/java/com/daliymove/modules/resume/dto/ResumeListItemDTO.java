package com.daliymove.modules.resume.dto;

import java.time.LocalDateTime;

/**
 * 简历列表项DTO
 * - 用于简历列表展示
 */
public record ResumeListItemDTO(
    Long id,
    String filename,
    Long fileSize,
    LocalDateTime uploadedAt,
    Integer accessCount,
    Integer latestScore,
    LocalDateTime lastAnalyzedAt,
    Integer interviewCount,
    String analyzeStatus
) {}