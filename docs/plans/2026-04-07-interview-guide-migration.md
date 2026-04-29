# Interview Guide 功能迁移实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 将 interview-guide 项目的核心功能迁移到 interview-daliymove 项目，包括简历分析、模拟面试、知识库管理三大模块。

**Architecture:** 采用分阶段迁移策略，先迁移后端实体和数据访问层，再迁移服务层和控制器层，最后迁移前端页面。后端使用 MyBatis-Plus 替代 JPA，前端使用 Vue 3 替代 React，保持业务逻辑不变。

**Tech Stack:**
- 后端: Spring Boot 3.2 + Java 17 + MyBatis-Plus + MySQL 8.0 + Redis + Sa-Token
- 前端: Vue 3.5 + TypeScript + Naive UI + Pinia
- AI集成: Spring AI 2.0 (阿里云DashScope)
- 文件存储: MinIO (替代RustFS)

---

## 迁移策略

### 阶段划分
1. **Phase 1: 数据库与实体层** - 创建MySQL表结构，迁移Entity，创建Mapper
2. **Phase 2: 通用与基础设施层** - 迁移common模块，迁移infrastructure模块
3. **Phase 3: 简历模块** - 迁移resume模块的Service和Controller
4. **Phase 4: 面试模块** - 迁移interview模块的Service和Controller
5. **Phase 5: 知识库模块** - 迁移knowledgebase模块的Service和Controller
6. **Phase 6: 前端页面** - 迁移React页面到Vue组件

### 模块依赖关系
```
resume → interview (面试依赖简历分析结果)
knowledgebase (独立模块)
```

---

## Phase 1: 数据库与实体层

### Task 1.1: 创建MySQL初始化脚本

**Files:**
- Create: `backend/sql/interview_tables.sql`

**Step 1: 分析源项目的Entity结构**

从源项目提取以下Entity的表结构：
- ResumeEntity
- ResumeAnalysisEntity
- InterviewSessionEntity
- InterviewAnswerEntity
- KnowledgeBaseEntity
- RagChatSessionEntity
- RagChatMessageEntity

**Step 2: 编写MySQL建表脚本**

```sql
-- 简历表
CREATE TABLE IF NOT EXISTS `resume` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '简历ID',
  `file_hash` VARCHAR(64) NOT NULL UNIQUE COMMENT '文件哈希值（SHA-256）',
  `original_filename` VARCHAR(255) NOT NULL COMMENT '原始文件名',
  `file_size` BIGINT COMMENT '文件大小（字节）',
  `content_type` VARCHAR(100) COMMENT '文件类型',
  `storage_key` VARCHAR(500) COMMENT 'MinIO存储的文件Key',
  `storage_url` VARCHAR(1000) COMMENT 'MinIO存储的文件URL',
  `resume_text` TEXT COMMENT '解析后的简历文本',
  `uploaded_at` DATETIME NOT NULL COMMENT '上传时间',
  `last_accessed_at` DATETIME COMMENT '最后访问时间',
  `access_count` INT DEFAULT 0 COMMENT '访问次数',
  `analyze_status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '分析状态：PENDING/PROCESSING/COMPLETED/FAILED',
  `analyze_error` VARCHAR(500) COMMENT '分析错误信息',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_file_hash` (`file_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历表';

-- 简历分析结果表
CREATE TABLE IF NOT EXISTS `resume_analysis` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分析ID',
  `resume_id` BIGINT NOT NULL COMMENT '简历ID',
  `overall_score` INT COMMENT '总体评分（0-100）',
  `skill_match_score` INT COMMENT '技能匹配度评分',
  `experience_score` INT COMMENT '工作经验评分',
  `education_score` INT COMMENT '教育背景评分',
  `strengths` TEXT COMMENT '优势（JSON数组）',
  `weaknesses` TEXT COMMENT '不足（JSON数组）',
  `suggestions` TEXT COMMENT '改进建议（JSON数组）',
  `skill_analysis` TEXT COMMENT '技能详细分析',
  `experience_analysis` TEXT COMMENT '经验详细分析',
  `education_analysis` TEXT COMMENT '教育详细分析',
  `summary` TEXT COMMENT '综合评价',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_resume_id` (`resume_id`),
  FOREIGN KEY (`resume_id`) REFERENCES `resume`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历分析结果表';

-- 面试会话表
CREATE TABLE IF NOT EXISTS `interview_session` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '会话ID',
  `resume_id` BIGINT COMMENT '简历ID',
  `job_position` VARCHAR(100) COMMENT '面试职位',
  `question_count` INT DEFAULT 5 COMMENT '问题数量',
  `follow_up_count` INT DEFAULT 1 COMMENT '追问数量',
  `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/COMPLETED/EVALUATING',
  `evaluation_status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '评估状态：PENDING/PROCESSING/COMPLETED/FAILED',
  `report_key` VARCHAR(500) COMMENT '评估报告存储Key',
  `report_url` VARCHAR(1000) COMMENT '评估报告URL',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_resume_id` (`resume_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试会话表';

-- 面试回答表
CREATE TABLE IF NOT EXISTS `interview_answer` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '回答ID',
  `session_id` BIGINT NOT NULL COMMENT '会话ID',
  `question_index` INT NOT NULL COMMENT '问题索引',
  `follow_up_index` INT DEFAULT 0 COMMENT '追问索引（0表示主问题）',
  `question_text` TEXT NOT NULL COMMENT '问题内容',
  `answer_text` TEXT COMMENT '回答内容',
  `evaluation_score` INT COMMENT '评分（0-100）',
  `evaluation_feedback` TEXT COMMENT '评估反馈',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_session_id` (`session_id`),
  FOREIGN KEY (`session_id`) REFERENCES `interview_session`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试回答表';

-- 知识库表
CREATE TABLE IF NOT EXISTS `knowledge_base` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '知识库ID',
  `original_filename` VARCHAR(255) NOT NULL COMMENT '原始文件名',
  `file_size` BIGINT COMMENT '文件大小（字节）',
  `content_type` VARCHAR(100) COMMENT '文件类型',
  `storage_key` VARCHAR(500) COMMENT 'MinIO存储的文件Key',
  `storage_url` VARCHAR(1000) COMMENT 'MinIO存储的文件URL',
  `document_text` TEXT COMMENT '文档文本',
  `chunk_count` INT DEFAULT 0 COMMENT '分块数量',
  `vector_status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '向量化状态：PENDING/PROCESSING/COMPLETED/FAILED',
  `vector_error` VARCHAR(500) COMMENT '向量化错误信息',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库表';

-- RAG聊天会话表
CREATE TABLE IF NOT EXISTS `rag_chat_session` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '会话ID',
  `knowledge_base_id` BIGINT COMMENT '知识库ID（可选）',
  `title` VARCHAR(255) DEFAULT '新对话' COMMENT '会话标题',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_knowledge_base_id` (`knowledge_base_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='RAG聊天会话表';

-- RAG聊天消息表
CREATE TABLE IF NOT EXISTS `rag_chat_message` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
  `session_id` BIGINT NOT NULL COMMENT '会话ID',
  `role` VARCHAR(20) NOT NULL COMMENT '角色：user/assistant',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_session_id` (`session_id`),
  FOREIGN KEY (`session_id`) REFERENCES `rag_chat_session`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='RAG聊天消息表';
```

**Step 3: 执行SQL脚本验证**

Run: 在MySQL数据库中执行上述SQL脚本
Expected: 所有表创建成功

---

### Task 1.2: 迁移简历模块Entity

**Files:**
- Create: `backend/daliymove-modules/src/main/java/com/daliymove/modules/resume/entity/Resume.java`
- Create: `backend/daliymove-modules/src/main/java/com/daliymove/modules/resume/entity/ResumeAnalysis.java`

**Step 1: 创建Resume实体类**

```java
package com.daliymove.modules.resume.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 简历实体
 * - 存储简历基本信息
 * - 记录文件哈希用于去重
 * - 异步分析状态跟踪
 */
@Data
@TableName("resume")
public class Resume {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 文件内容的SHA-256哈希值，用于去重 */
    private String fileHash;

    /** 原始文件名 */
    private String originalFilename;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 文件类型 */
    private String contentType;

    /** MinIO存储的文件Key */
    private String storageKey;

    /** MinIO存储的文件URL */
    private String storageUrl;

    /** 解析后的简历文本 */
    private String resumeText;

    /** 上传时间 */
    private LocalDateTime uploadedAt;

    /** 最后访问时间 */
    private LocalDateTime lastAccessedAt;

    /** 访问次数 */
    private Integer accessCount;

    /** 分析状态：PENDING/PROCESSING/COMPLETED/FAILED */
    private String analyzeStatus;

    /** 分析错误信息 */
    private String analyzeError;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
```

**Step 2: 创建ResumeAnalysis实体类**

```java
package com.daliymove.modules.resume.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 简历分析结果实体
 * - 存储AI分析的完整结果
 * - 包含评分、优劣势、建议等
 */
@Data
@TableName("resume_analysis")
public class ResumeAnalysis {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 简历ID */
    private Long resumeId;

    /** 总体评分（0-100） */
    private Integer overallScore;

    /** 技能匹配度评分 */
    private Integer skillMatchScore;

    /** 工作经验评分 */
    private Integer experienceScore;

    /** 教育背景评分 */
    private Integer educationScore;

    /** 优势（JSON数组） */
    private String strengths;

    /** 不足（JSON数组） */
    private String weaknesses;

    /** 改进建议（JSON数组） */
    private String suggestions;

    /** 技能详细分析 */
    private String skillAnalysis;

    /** 经验详细分析 */
    private String experienceAnalysis;

    /** 教育详细分析 */
    private String educationAnalysis;

    /** 综合评价 */
    private String summary;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
```

---

### Task 1.3: 创建简历模块Mapper

**Files:**
- Create: `backend/daliymove-modules/src/main/java/com/daliymove/modules/resume/mapper/ResumeMapper.java`
- Create: `backend/daliymove-modules/src/main/java/com/daliymove/modules/resume/mapper/ResumeAnalysisMapper.java`

**Step 1: 创建ResumeMapper**

```java
package com.daliymove.modules.resume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daliymove.modules.resume.entity.Resume;
import org.apache.ibatis.annotations.Mapper;

/**
 * 简历Mapper接口
 * - 提供简历数据的CRUD操作
 * - 支持文件哈希查询（去重）
 */
@Mapper
public interface ResumeMapper extends BaseMapper<Resume> {

}
```

**Step 2: 创建ResumeAnalysisMapper**

```java
package com.daliymove.modules.resume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daliymove.modules.resume.entity.ResumeAnalysis;
import org.apache.ibatis.annotations.Mapper;

/**
 * 简历分析结果Mapper接口
 */
@Mapper
public interface ResumeAnalysisMapper extends BaseMapper<ResumeAnalysis> {

}
```

---

### Task 1.4: 迁移面试模块Entity

**Files:**
- Create: `backend/daliymove-modules/src/main/java/com/daliymove/modules/interview/entity/InterviewSession.java`
- Create: `backend/daliymove-modules/src/main/java/com/daliymove/modules/interview/entity/InterviewAnswer.java`

**Step 1: 创建InterviewSession实体**

```java
package com.daliymove.modules.interview.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 面试会话实体
 * - 存储面试会话基本信息
 * - 关联简历和职位信息
 * - 异步评估状态跟踪
 */
@Data
@TableName("interview_session")
public class InterviewSession {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 简历ID */
    private Long resumeId;

    /** 面试职位 */
    private String jobPosition;

    /** 问题数量 */
    private Integer questionCount;

    /** 追问数量 */
    private Integer followUpCount;

    /** 状态：ACTIVE/COMPLETED/EVALUATING */
    private String status;

    /** 评估状态：PENDING/PROCESSING/COMPLETED/FAILED */
    private String evaluationStatus;

    /** 评估报告存储Key */
    private String reportKey;

    /** 评估报告URL */
    private String reportUrl;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
```

**Step 2: 创建InterviewAnswer实体**

```java
package com.daliymove.modules.interview.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 面试回答实体
 * - 存储面试问题和回答
 * - 包含评估结果
 */
@Data
@TableName("interview_answer")
public class InterviewAnswer {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 会话ID */
    private Long sessionId;

    /** 问题索引 */
    private Integer questionIndex;

    /** 追问索引（0表示主问题） */
    private Integer followUpIndex;

    /** 问题内容 */
    private String questionText;

    /** 回答内容 */
    private String answerText;

    /** 评分（0-100） */
    private Integer evaluationScore;

    /** 评估反馈 */
    private String evaluationFeedback;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
```

---

### Task 1.5: 创建面试模块Mapper

**Files:**
- Create: `backend/daliymove-modules/src/main/java/com/daliymove/modules/interview/mapper/InterviewSessionMapper.java`
- Create: `backend/daliymove-modules/src/main/java/com/daliymove/modules/interview/mapper/InterviewAnswerMapper.java`

**Step 1: 创建InterviewSessionMapper**

```java
package com.daliymove.modules.interview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daliymove.modules.interview.entity.InterviewSession;
import org.apache.ibatis.annotations.Mapper;

/**
 * 面试会话Mapper接口
 */
@Mapper
public interface InterviewSessionMapper extends BaseMapper<InterviewSession> {

}
```

**Step 2: 创建InterviewAnswerMapper**

```java
package com.daliymove.modules.interview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daliymove.modules.interview.entity.InterviewAnswer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 面试回答Mapper接口
 */
@Mapper
public interface InterviewAnswerMapper extends BaseMapper<InterviewAnswer> {

}
```

---

### Task 1.6: 迁移知识库模块Entity

**Files:**
- Create: `backend/daliymove-modules/src/main/java/com/daliymove/modules/knowledge/entity/KnowledgeBase.java`
- Create: `backend/daliymove-modules/src/main/java/com/daliymove/modules/knowledge/entity/RagChatSession.java`
- Create: `backend/daliymove-modules/src/main/java/com/daliymove/modules/knowledge/entity/RagChatMessage.java`

**Step 1: 创建KnowledgeBase实体**

```java
package com.daliymove.modules.knowledge.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 知识库实体
 * - 存储上传的文档信息
 * - 向量化状态跟踪
 */
@Data
@TableName("knowledge_base")
public class KnowledgeBase {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 原始文件名 */
    private String originalFilename;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 文件类型 */
    private String contentType;

    /** MinIO存储的文件Key */
    private String storageKey;

    /** MinIO存储的文件URL */
    private String storageUrl;

    /** 文档文本 */
    private String documentText;

    /** 分块数量 */
    private Integer chunkCount;

    /** 向量化状态：PENDING/PROCESSING/COMPLETED/FAILED */
    private String vectorStatus;

    /** 向量化错误信息 */
    private String vectorError;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
```

**Step 2: 创建RagChatSession实体**

```java
package com.daliymove.modules.knowledge.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * RAG聊天会话实体
 */
@Data
@TableName("rag_chat_session")
public class RagChatSession {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 知识库ID（可选） */
    private Long knowledgeBaseId;

    /** 会话标题 */
    private String title;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
```

**Step 3: 创建RagChatMessage实体**

```java
package com.daliymove.modules.knowledge.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * RAG聊天消息实体
 */
@Data
@TableName("rag_chat_message")
public class RagChatMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 会话ID */
    private Long sessionId;

    /** 角色：user/assistant */
    private String role;

    /** 消息内容 */
    private String content;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
```

---

### Task 1.7: 创建知识库模块Mapper

**Files:**
- Create: `backend/daliymove-modules/src/main/java/com/daliymove/modules/knowledge/mapper/KnowledgeBaseMapper.java`
- Create: `backend/daliymove-modules/src/main/java/com/daliymove/modules/knowledge/mapper/RagChatSessionMapper.java`
- Create: `backend/daliymove-modules/src/main/java/com/daliymove/modules/knowledge/mapper/RagChatMessageMapper.java`

**Step 1: 创建Mapper接口**

```java
package com.daliymove.modules.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daliymove.modules.knowledge.entity.KnowledgeBase;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KnowledgeBaseMapper extends BaseMapper<KnowledgeBase> {

}
```

```java
package com.daliymove.modules.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daliymove.modules.knowledge.entity.RagChatSession;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RagChatSessionMapper extends BaseMapper<RagChatSession> {

}
```

```java
package com.daliymove.modules.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daliymove.modules.knowledge.entity.RagChatMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RagChatMessageMapper extends BaseMapper<RagChatMessage> {

}
```

---

## Phase 2: 通用与基础设施层

### Task 2.1: 迁移通用枚举类

**Files:**
- Create: `backend/daliymove-common/src/main/java/com/daliymove/common/enums/AsyncTaskStatus.java`

**Step 1: 创建异步任务状态枚举**

```java
package com.daliymove.common.enums;

/**
 * 异步任务状态枚举
 * - 用于简历分析、向量化等异步任务的状态跟踪
 */
public enum AsyncTaskStatus {
    /** 待处理 */
    PENDING,
    /** 处理中 */
    PROCESSING,
    /** 已完成 */
    COMPLETED,
    /** 已失败 */
    FAILED
}
```

---

### Task 2.2: 配置MyBatis-Plus自动填充

**Files:**
- Modify: `backend/daliymove-server/src/main/java/com/daliymove/server/config/MybatisPlusConfig.java`

**Step 1: 添加自动填充处理器**

查看现有配置，确保已包含：
- LocalDateTime自动填充策略
- INSERT和INSERT_UPDATE策略

---

## Phase 3: 简历模块迁移（详细步骤省略，后续补充）

**关键任务：**
- Task 3.1: 创建简历模块DTO
- Task 3.2: 创建简历模块Service（ResumeUploadService等）
- Task 3.3: 创建简历模块Controller
- Task 3.4: 配置文件存储服务（MinIO替代RustFS）
- Task 3.5: 配置Redis Stream消费者和生产者

---

## Phase 4: 面试模块迁移（详细步骤省略，后续补充）

**关键任务：**
- Task 4.1: 创建面试模块DTO
- Task 4.2: 创建面试模块Service
- Task 4.3: 创建面试模块Controller
- Task 4.4: 配置面试评估的Redis Stream

---

## Phase 5: 知识库模块迁移（详细步骤省略，后续补充）

**关键任务：**
- Task 5.1: 创建知识库模块DTO
- Task 5.2: 创建知识库模块Service
- Task 5.3: 创建知识库模块Controller（包括RAG聊天）
- Task 5.4: 配置向量存储（MySQL替代pgvector需要特殊处理）
- Task 5.5: 配置向量化任务的Redis Stream

---

## Phase 6: 前端页面迁移（详细步骤省略，后续补充）

**关键任务：**
- Task 6.1: 创建简历管理页面（HistoryPage → ResumeHistory.vue）
- Task 6.2: 创建简历上传页面（UploadPage → ResumeUpload.vue）
- Task 6.3: 创建简历详情页面（ResumeDetailPage → ResumeDetail.vue）
- Task 6.4: 创建面试历史页面（InterviewHistoryPage → InterviewHistory.vue）
- Task 6.5: 创建模拟面试页面（InterviewPage → Interview.vue）
- Task 6.6: 创建知识库管理页面（KnowledgeBaseManagePage → KnowledgeManage.vue）
- Task 6.7: 创建知识库上传页面（KnowledgeBaseUploadPage → KnowledgeUpload.vue）
- Task 6.8: 创建知识库问答页面（KnowledgeBaseQueryPage → KnowledgeQA.vue）

---

## 测试策略

### 后端测试
1. 单元测试：Service层核心逻辑
2. 集成测试：Controller层API测试
3. 异步任务测试：Redis Stream消费和生产

### 前端测试
1. 页面渲染测试
2. API调用测试
3. 流式响应测试（SSE）

---

## 风险点与注意事项

1. **向量存储差异**: pgvector → MySQL需要考虑替代方案
   - 方案A: 使用Spring AI的SimpleVectorStore（内存）
   - 方案B: 引入其他向量数据库（如Milvus）
   - 方案C: 先跳过向量功能，后续优化

2. **Redis Stream适配**: 源项目使用Redisson，目标项目已有Redis配置，需确保Stream功能可用

3. **文件存储**: RustFS → MinIO，需调整配置和API调用

4. **Sa-Token认证**: 源项目无用户认证，目标项目需要为迁移的模块添加用户关联

5. **异步任务监控**: 需实现任务状态的查询和轮询机制

---

## 实施建议

**优先级排序：**
1. 简历模块（核心功能，面试依赖）
2. 面试模块（核心功能）
3. 知识库模块（可选功能，向量存储技术复杂）

**实施顺序：**
- 先完成数据库和实体层（Phase 1），确保数据模型稳定
- 再完成简历模块（Phase 3），验证核心流程
- 最后完成面试和知识库模块

**验证方式：**
- 每个Phase完成后进行独立验证
- 先跑通单个模块的完整流程
- 再进行跨模块集成测试

---

## 后续优化

完成基础迁移后，可考虑：
1. 引入专业向量数据库
2. 添加API限流
3. 优化异步任务监控UI
4. 实现Docker部署配置
5. 添加用户认证和数据隔离

---

**注意：** 由于任务复杂度较高，建议采用分阶段实施，每完成一个阶段就进行验证和测试，确保系统稳定性。