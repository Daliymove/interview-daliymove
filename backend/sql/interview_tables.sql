-- =====================================================
-- Interview Tables - MySQL DDL Script
-- Migration from PostgreSQL/JPA to MySQL/MyBatis-Plus
-- Storage: RustFS (not MinIO)
-- =====================================================

-- =====================================================
-- 1. 简历表 (resume)
-- 用于存储上传的简历文件信息
-- 支持文件哈希去重、异步分析状态追踪
-- =====================================================
DROP TABLE IF EXISTS `resume`;
CREATE TABLE `resume` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `file_hash` VARCHAR(64) NOT NULL COMMENT '文件内容的SHA-256哈希值，用于去重',
    `original_filename` VARCHAR(500) NOT NULL COMMENT '原始文件名',
    `file_size` BIGINT COMMENT '文件大小（字节）',
    `content_type` VARCHAR(100) COMMENT '文件类型（MIME类型）',
    `storage_key` VARCHAR(500) COMMENT 'RustFS存储的文件Key',
    `storage_url` VARCHAR(1000) COMMENT 'RustFS存储的文件URL',
    `resume_text` TEXT COMMENT '解析后的简历文本内容',
    `uploaded_at` DATETIME NOT NULL COMMENT '上传时间',
    `last_accessed_at` DATETIME COMMENT '最后访问时间',
    `access_count` INT DEFAULT 0 COMMENT '访问次数',
    `analyze_status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '分析状态：PENDING-待处理，PROCESSING-处理中，COMPLETED-完成，FAILED-失败',
    `analyze_error` VARCHAR(500) COMMENT '分析错误信息（失败时记录）',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_resume_file_hash` (`file_hash`),
    KEY `idx_resume_analyze_status` (`analyze_status`),
    KEY `idx_resume_uploaded_at` (`uploaded_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='简历表 - 存储上传的简历文件信息';

-- =====================================================
-- 2. 简历分析结果表 (resume_analysis)
-- 存储AI对简历的分析结果
-- 包含各维度评分、优点、改进建议
-- =====================================================
DROP TABLE IF EXISTS `resume_analysis`;
CREATE TABLE `resume_analysis` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `resume_id` BIGINT NOT NULL COMMENT '关联的简历ID',
    `overall_score` INT COMMENT '总分（0-100）',
    `content_score` INT COMMENT '内容完整性评分（0-25）',
    `structure_score` INT COMMENT '结构清晰度评分（0-20）',
    `skill_match_score` INT COMMENT '技能匹配度评分（0-25）',
    `expression_score` INT COMMENT '表达专业性评分（0-15）',
    `project_score` INT COMMENT '项目经验评分（0-15）',
    `summary` TEXT COMMENT '简历摘要',
    `strengths_json` TEXT COMMENT '优点列表（JSON格式）',
    `suggestions_json` TEXT COMMENT '改进建议列表（JSON格式）',
    `analyzed_at` DATETIME NOT NULL COMMENT '评测时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_resume_analysis_resume_id` (`resume_id`),
    CONSTRAINT `fk_resume_analysis_resume` FOREIGN KEY (`resume_id`) REFERENCES `resume` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='简历分析结果表 - 存储AI对简历的分析结果';

-- =====================================================
-- 3. 面试会话表 (interview_session)
-- 存储面试会话信息
-- 关联简历，支持异步评估状态追踪
-- =====================================================
DROP TABLE IF EXISTS `interview_answer`;
DROP TABLE IF EXISTS `interview_session`;
CREATE TABLE `interview_session` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `session_id` VARCHAR(36) NOT NULL COMMENT '会话ID（UUID）',
    `resume_id` BIGINT NOT NULL COMMENT '关联的简历ID',
    `total_questions` INT COMMENT '问题总数',
    `current_question_index` INT DEFAULT 0 COMMENT '当前问题索引',
    `status` VARCHAR(20) DEFAULT 'CREATED' COMMENT '会话状态：CREATED-已创建，IN_PROGRESS-进行中，COMPLETED-已完成，EVALUATED-已评估',
    `questions_json` TEXT COMMENT '问题列表（JSON格式）',
    `overall_score` INT COMMENT '总分（0-100）',
    `overall_feedback` TEXT COMMENT '总体评价',
    `strengths_json` TEXT COMMENT '优势列表（JSON格式）',
    `improvements_json` TEXT COMMENT '改进建议列表（JSON格式）',
    `reference_answers_json` TEXT COMMENT '参考答案列表（JSON格式）',
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    `completed_at` DATETIME COMMENT '完成时间',
    `evaluate_status` VARCHAR(20) COMMENT '评估状态：PENDING-待处理，PROCESSING-处理中，COMPLETED-完成，FAILED-失败',
    `evaluate_error` VARCHAR(500) COMMENT '评估错误信息（失败时记录）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_interview_session_session_id` (`session_id`),
    KEY `idx_interview_session_resume_created` (`resume_id`, `created_at`),
    KEY `idx_interview_session_resume_status_created` (`resume_id`, `status`, `created_at`),
    KEY `idx_interview_session_status` (`status`),
    CONSTRAINT `fk_interview_session_resume` FOREIGN KEY (`resume_id`) REFERENCES `resume` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试会话表 - 存储面试会话信息';

-- =====================================================
-- 4. 面试答案表 (interview_answer)
-- 存储面试过程中的问答对和评估结果
-- =====================================================
DROP TABLE IF EXISTS `interview_answer`;
CREATE TABLE `interview_answer` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `session_id` BIGINT NOT NULL COMMENT '关联的会话ID',
    `question_index` INT COMMENT '问题索引',
    `question` TEXT COMMENT '问题内容',
    `category` VARCHAR(100) COMMENT '问题类别',
    `user_answer` TEXT COMMENT '用户答案',
    `score` INT COMMENT '得分（0-100）',
    `feedback` TEXT COMMENT '反馈评价',
    `reference_answer` TEXT COMMENT '参考答案',
    `key_points_json` TEXT COMMENT '关键点列表（JSON格式）',
    `answered_at` DATETIME NOT NULL COMMENT '回答时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_interview_answer_session_question` (`session_id`, `question_index`),
    KEY `idx_interview_answer_session_question` (`session_id`, `question_index`),
    CONSTRAINT `fk_interview_answer_session` FOREIGN KEY (`session_id`) REFERENCES `interview_session` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试答案表 - 存储面试问答对和评估结果';

-- =====================================================
-- 5. 知识库表 (knowledge_base)
-- 存储上传的文档信息
-- 支持文件哈希去重、向量化状态追踪
-- 文件存储使用 RustFS
-- =====================================================
DROP TABLE IF EXISTS `rag_chat_message`;
DROP TABLE IF EXISTS `rag_chat_session`;
DROP TABLE IF EXISTS `knowledge_base`;
CREATE TABLE `knowledge_base` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `file_hash` VARCHAR(64) NOT NULL COMMENT '文件内容的SHA-256哈希值，用于去重',
    `name` VARCHAR(200) NOT NULL COMMENT '知识库名称（用户自定义或从文件名提取）',
    `category` VARCHAR(100) COMMENT '分类/分组（如Java面试、项目文档等）',
    `original_filename` VARCHAR(500) NOT NULL COMMENT '原始文件名',
    `file_size` BIGINT COMMENT '文件大小（字节）',
    `content_type` VARCHAR(100) COMMENT '文件类型（MIME类型）',
    `storage_key` VARCHAR(500) COMMENT 'RustFS存储的文件Key',
    `storage_url` VARCHAR(1000) COMMENT 'RustFS存储的文件URL',
    `uploaded_at` DATETIME NOT NULL COMMENT '上传时间',
    `last_accessed_at` DATETIME COMMENT '最后访问时间',
    `access_count` INT DEFAULT 0 COMMENT '访问次数',
    `question_count` INT DEFAULT 0 COMMENT '问题数量（用户针对此知识库提问的次数）',
    `vector_status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '向量化状态：PENDING-待处理，PROCESSING-处理中，COMPLETED-完成，FAILED-失败',
    `vector_error` VARCHAR(500) COMMENT '向量化错误信息（失败时记录）',
    `chunk_count` INT DEFAULT 0 COMMENT '向量分块数量',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_knowledge_base_file_hash` (`file_hash`),
    KEY `idx_knowledge_base_category` (`category`),
    KEY `idx_knowledge_base_vector_status` (`vector_status`),
    KEY `idx_knowledge_base_uploaded_at` (`uploaded_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库表 - 存储上传的文档信息，使用RustFS存储文件';

-- =====================================================
-- 6. RAG聊天会话表 (rag_chat_session)
-- 存储RAG聊天会话
-- 一个会话可以关联多个知识库
-- =====================================================
CREATE TABLE `rag_chat_session` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(200) NOT NULL COMMENT '会话标题（可自动生成或用户自定义）',
    `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '会话状态：ACTIVE-活跃，ARCHIVED-已归档',
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    `updated_at` DATETIME COMMENT '更新时间（最后一次消息时间）',
    `message_count` INT DEFAULT 0 COMMENT '消息数量（冗余字段，方便查询）',
    `is_pinned` TINYINT(1) DEFAULT 0 COMMENT '是否置顶',
    PRIMARY KEY (`id`),
    KEY `idx_rag_session_updated` (`updated_at`),
    KEY `idx_rag_session_status` (`status`),
    KEY `idx_rag_session_is_pinned` (`is_pinned`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='RAG聊天会话表 - 存储RAG聊天会话信息';

-- =====================================================
-- 7. RAG聊天消息表 (rag_chat_message)
-- 存储RAG聊天中的用户问题和AI回答
-- =====================================================
CREATE TABLE `rag_chat_message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `session_id` BIGINT NOT NULL COMMENT '关联的会话ID',
    `type` VARCHAR(20) NOT NULL COMMENT '消息类型：USER-用户消息，ASSISTANT-AI回答',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `message_order` INT NOT NULL COMMENT '消息顺序（用于排序）',
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    `updated_at` DATETIME COMMENT '更新时间（用于流式响应更新）',
    `completed` TINYINT(1) DEFAULT 1 COMMENT '是否完成（流式响应时使用）',
    PRIMARY KEY (`id`),
    KEY `idx_rag_message_session` (`session_id`),
    KEY `idx_rag_message_order` (`session_id`, `message_order`),
    CONSTRAINT `fk_rag_message_session` FOREIGN KEY (`session_id`) REFERENCES `rag_chat_session` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='RAG聊天消息表 - 存储用户问题和AI回答';

-- =====================================================
-- 初始化完成
-- =====================================================