package com.daliymove.modules.knowledge.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识库实体类
 * - 存储上传的知识库文件信息
 * - 支持文件哈希去重
 * - 支持向量化状态追踪
 * - 文件存储使用 RustFS
 */
@Data
@TableName("knowledge_base")
public class KnowledgeBase {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 文件内容的 SHA-256 哈希值，用于去重 */
    @TableField("file_hash")
    private String fileHash;

    /** 知识库名称（用户自定义或从文件名提取） */
    @TableField("name")
    private String name;

    /** 分类/分组（如"Java面试"、"项目文档"等） */
    @TableField("category")
    private String category;

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

    /** 上传时间 */
    @TableField("uploaded_at")
    private LocalDateTime uploadedAt;

    /** 最后访问时间 */
    @TableField("last_accessed_at")
    private LocalDateTime lastAccessedAt;

    /** 访问次数 */
    @TableField("access_count")
    private Integer accessCount;

    /** 问题数量（用户针对此知识库提问的次数） */
    @TableField("question_count")
    private Integer questionCount;

    /** 向量化状态：PENDING-待处理，PROCESSING-处理中，COMPLETED-完成，FAILED-失败 */
    @TableField("vector_status")
    private String vectorStatus;

    /** 向量化错误信息（失败时记录） */
    @TableField("vector_error")
    private String vectorError;

    /** 向量分块数量 */
    @TableField("chunk_count")
    private Integer chunkCount;

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

    /**
     * 增加问题次数并更新最后访问时间
     */
    public void incrementQuestionCount() {
        if (this.questionCount == null) {
            this.questionCount = 0;
        }
        this.questionCount++;
        this.lastAccessedAt = LocalDateTime.now();
    }
}