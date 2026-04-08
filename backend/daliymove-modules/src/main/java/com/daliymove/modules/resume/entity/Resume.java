package com.daliymove.modules.resume.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 简历实体类
 * - 存储上传的简历文件信息
 * - 支持文件哈希去重
 * - 支持异步分析状态追踪
 * - 文件存储使用 RustFS（非 MinIO）
 */
@Data
@TableName("resume")
public class Resume {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 文件内容的 SHA-256 哈希值，用于去重 */
    @TableField("file_hash")
    private String fileHash;

    /** 原始文件名 */
    @TableField("original_filename")
    private String originalFilename;

    /** 文件大小（字节） */
    @TableField("file_size")
    private Long fileSize;

    /** 文件类型（MIME 类型） */
    @TableField("content_type")
    private String contentType;

    /** RustFS 存储的文件 Key */
    @TableField("storage_key")
    private String storageKey;

    /** RustFS 存储的文件 URL */
    @TableField("storage_url")
    private String storageUrl;

    /** 解析后的简历文本内容 */
    @TableField("resume_text")
    private String resumeText;

    /** 上传时间 */
    @TableField("uploaded_at")
    private LocalDateTime uploadedAt;

    /** 最后访问时间 */
    @TableField("last_accessed_at")
    private LocalDateTime lastAccessedAt;

    /** 访问次数 */
    @TableField("access_count")
    private Integer accessCount;

    /** 分析状态：PENDING-待处理，PROCESSING-处理中，COMPLETED-完成，FAILED-失败 */
    @TableField("analyze_status")
    private String analyzeStatus;

    /** 分析错误信息（失败时记录） */
    @TableField("analyze_error")
    private String analyzeError;

    /** 创建时间，自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间，自动填充 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 增加访问次数并更新最后访问时间
     */
    public void incrementAccessCount() {
        if (this.accessCount == null) {
            this.accessCount = 0;
        }
        this.accessCount++;
        this.lastAccessedAt = LocalDateTime.now();
    }
}