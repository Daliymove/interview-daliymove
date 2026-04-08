/*
 Navicat Premium Data Transfer

 Source Server         : mysql-docker
 Source Server Type    : MySQL
 Source Server Version : 80045
 Source Host           : localhost:3306
 Source Schema         : enterprise_admin

 Target Server Type    : MySQL
 Target Server Version : 80045
 File Encoding         : 65001

 Date: 07/04/2026 19:20:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat_conversation
-- ----------------------------
DROP TABLE IF EXISTS `chat_conversation`;
CREATE TABLE `chat_conversation`  (
                                      `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '会话ID',
                                      `user_id` bigint(0) NOT NULL COMMENT '用户ID',
                                      `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '新对话' COMMENT '会话标题',
                                      `model_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'qwen-plus' COMMENT '模型类型',
                                      `system_prompt` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '系统提示词',
                                      `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态：0-删除，1-正常',
                                      `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      INDEX `idx_user_id`(`user_id`) USING BTREE,
                                      INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '聊天会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_conversation
-- ----------------------------
INSERT INTO `chat_conversation` VALUES (1, 1, '新对话', 'qwen-plus', NULL, 0, '2026-03-24 16:05:59', '2026-03-24 16:05:59');
INSERT INTO `chat_conversation` VALUES (2, 1, '新对话', 'qwen-plus', NULL, 0, '2026-03-24 16:06:14', '2026-03-24 16:06:14');
INSERT INTO `chat_conversation` VALUES (3, 1, '新对话', 'qwen-plus', NULL, 0, '2026-03-24 16:07:01', '2026-03-24 16:07:01');
INSERT INTO `chat_conversation` VALUES (4, 1, '新对话', 'qwen-plus', NULL, 0, '2026-03-24 16:25:27', '2026-03-24 16:25:27');
INSERT INTO `chat_conversation` VALUES (5, 1, '新对话', 'qwen-plus', NULL, 0, '2026-03-24 16:29:55', '2026-03-24 16:29:55');
INSERT INTO `chat_conversation` VALUES (6, 1, '测试1', 'qwen-plus', NULL, 0, '2026-03-24 16:30:24', '2026-03-24 16:30:24');
INSERT INTO `chat_conversation` VALUES (7, 1, '新对话', 'qwen-plus', NULL, 0, '2026-03-24 16:37:02', '2026-03-24 16:37:02');
INSERT INTO `chat_conversation` VALUES (8, 1, '新对话', 'qwen-plus', NULL, 0, '2026-03-24 16:37:31', '2026-03-24 16:37:31');
INSERT INTO `chat_conversation` VALUES (9, 1, '新对话', 'qwen-plus', NULL, 0, '2026-03-24 16:38:00', '2026-03-24 16:38:00');
INSERT INTO `chat_conversation` VALUES (10, 1, '新对话', 'qwen-plus', NULL, 0, '2026-03-24 16:42:33', '2026-03-24 16:42:33');
INSERT INTO `chat_conversation` VALUES (11, 1, '新对话', 'qwen-plus', NULL, 0, '2026-03-24 17:18:02', '2026-03-24 17:18:02');
INSERT INTO `chat_conversation` VALUES (12, 1, '新对话', 'qwen-plus', NULL, 0, '2026-03-25 08:13:05', '2026-03-25 08:13:05');
INSERT INTO `chat_conversation` VALUES (13, 1, '新对话', 'qwen-plus', NULL, 0, '2026-03-25 09:03:53', '2026-03-25 09:03:53');
INSERT INTO `chat_conversation` VALUES (14, 1, '新对话', 'qwen-plus', NULL, 0, '2026-03-26 16:58:43', '2026-03-26 16:58:43');
INSERT INTO `chat_conversation` VALUES (15, 1, '新对话', 'qwen-plus', NULL, 0, '2026-03-30 11:22:20', '2026-03-30 11:22:20');
INSERT INTO `chat_conversation` VALUES (16, 1, '新对话', 'qwen-plus', NULL, 0, '2026-03-30 11:25:54', '2026-03-30 11:25:54');
INSERT INTO `chat_conversation` VALUES (17, 1, 'Pnpm与npm的区别', 'qwen-plus', NULL, 1, '2026-03-30 11:27:57', '2026-03-30 11:27:57');
INSERT INTO `chat_conversation` VALUES (18, 1, '今日天气查询', 'qwen-plus', NULL, 1, '2026-03-30 11:33:34', '2026-03-30 11:33:34');
INSERT INTO `chat_conversation` VALUES (19, 1, '如何创建Spring AI项目', 'qwen-plus', NULL, 1, '2026-03-30 11:34:38', '2026-03-30 11:34:38');
INSERT INTO `chat_conversation` VALUES (20, 1, '新对话', 'qwen-plus', NULL, 0, '2026-03-30 11:34:52', '2026-03-30 11:34:52');
INSERT INTO `chat_conversation` VALUES (21, 1, '新对话', 'qwen-plus', NULL, 0, '2026-04-03 15:48:05', '2026-04-03 15:48:05');
INSERT INTO `chat_conversation` VALUES (22, 1, '新对话', 'qwen-plus', NULL, 0, '2026-04-03 15:48:07', '2026-04-03 15:48:07');
INSERT INTO `chat_conversation` VALUES (23, 1, '新对话', 'qwen-plus', NULL, 1, '2026-04-03 15:51:44', '2026-04-03 15:51:44');

-- ----------------------------
-- Table structure for chat_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message`  (
                                 `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '消息ID',
                                 `conversation_id` bigint(0) NOT NULL COMMENT '会话ID',
                                 `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色：user/assistant/system',
                                 `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
                                 `tokens` int(0) NULL DEFAULT NULL COMMENT '消耗token数',
                                 `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 INDEX `idx_conversation_id`(`conversation_id`) USING BTREE,
                                 INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '聊天消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_message
-- ----------------------------
INSERT INTO `chat_message` VALUES (36, 17, 'user', '帮我介绍下Pnpm和npm的区别', NULL, '2026-03-30 11:28:19');
INSERT INTO `chat_message` VALUES (37, 17, 'assistant', '当然可以！pnpm 和 npm 都是 Node.js 的包管理工具，用于安装、管理和运行 JavaScript/TypeScript 项目中的依赖包，但它们在**设计哲学、底层实现、性能和磁盘空间利用**等方面有显著区别。以下是核心对比（截至 2024 年主流版本，如 pnpm v8+ / npm v9+）：\n\n---\n\n### ✅ 1. **依赖存储机制（最核心区别）**\n\n| 特性 | npm | pnpm |\n|------|-----|------|\n| **node_modules 结构** | 扁平化（v3+）或嵌套（旧版），存在大量重复包拷贝（尤其 hoisted 时仍可能冗余） | **硬链接 + 符号链接（symlinks）**，所有包统一存于全局内容寻址存储（`~/.pnpm-store`），`node_modules` 中仅存轻量级符号链接 |\n| **磁盘占用** | 较高：同一包在多个项目中会被重复下载/复制 | 极低：相同版本包只存一份，多项目共享，节省 50%+ 磁盘空间 |\n| **安装速度** | 中等（需解压 + 拷贝文件） | 更快（尤其是重复依赖多时）：大部分操作是创建硬链接，毫秒级 |\n\n> 💡 举例：若 10 个项目都用 `lodash@4.17.21`，npm 会存 10 份；pnpm 只存 1 份物理文件，其余为硬链接。\n\n---\n\n### ✅ 2. **依赖隔离性与确定性**\n\n| 特性 | npm | pnpm |\n|------|-----|------|\n| **严格性（Strictness）** | 允许“幽灵依赖”（phantom dependencies）：即未显式声明但在代码中直接 `require(\'xxx\')` 的包（因 hoisting 被提升到顶层 `node_modules`） | **默认禁止幽灵依赖**：只允许 `dependencies`/`devDependencies` 中显式声明的包被导入，否则报错（可通过 `--strict-peer-dependencies false` 调整）→ 更强的可重现性和安全性 |\n| **`package-lock.json` vs `pnpm-lock.yaml`** | npm 使用 `package-lock.json`（JSON 格式） | pnpm 使用 `pnpm-lock.yaml`（YAML 格式），结构更清晰，支持更细粒度的依赖解析控制（如 `peerDependencies` 解析策略） |\n\n---\n\n### ✅ 3. **工作区（Monorepo）支持**\n\n| 特性 | npm | pnpm |\n|------|-----|------|\n| **原生 Workspace 支持** | npm v7+ 支持 `workspaces` 字段（基础功能） | ✅ **更成熟、更高效**：支持嵌套 workspace、独立 `node_modules`、`pnpm recursive` 命令（如 `pnpm -r build`）、符号链接自动管理，且无 hoisting 引发的隐式依赖问题 |\n\n> 🌟 pnpm 是目前 Monorepo（如 Turborepo、Nx 集成）场景下最受推荐的包管理器之一。\n\n---\n\n### ✅ 4. **其他亮点对比**\n\n| 维度 | npm | pnpm |\n|------|-----|------|\n| **安全性** | 提供 `npm audit`，但依赖图分析较弱 | 内置 `pnpm audit`，且因严格依赖隔离，更难出现意外依赖注入 |\n| **命令兼容性** | 命令标准（`npm install`, `npm run` 等） | ✅ **高度兼容 npm 命令**：`pnpm install` ≈ `npm install`，`pnpm run dev` 同理；还扩展了实用命令如：<br>• `pnpm why lodash`（查为何引入某包）<br>• `pnpm list --depth=0`（查看顶级依赖）<br>• `pnpm store status`（查看全局 store 状态） |\n| **社区 & 生态** | 最大生态，文档/教程最全，CI/CD 工具默认支持 | 快速增长（Vue/Vite/Svelte 官方推荐；Nuxt、Remix 等深度集成），GitHub Star > 40k（2024），已成企业级替代首选之一 |\n| **Windows 兼容性** | 无问题 | ✅ 通过硬链接（需启用开发者模式或管理员权限）；也可回退到复制模式（`pnpm config set link-workspace-packages false`） |\n\n---\n\n### ✅ 简单总结：怎么选？\n\n| 场景 | 推荐工具 | 原因 |\n|------|----------|------|\n| 🚀 新项目 / Monorepo / 对磁盘/速度敏感 | **pnpm** | 超快安装、极省空间、严格依赖、优秀 workspace 支持 |\n| 🧩 需要最大兼容性 / 团队习惯 / CI 环境受限（如老旧 Windows） | **npm** | 开箱即用，无需额外配置，生态支持最广 |\n| 🔐 追求安全与可重现构建（如金融/企业级项目） | **pnpm**（强烈推荐） | 无幽灵依赖、锁文件更精确、内容寻址保证完整性 |\n\n---\n\n✅ **迁移小贴士**：  \n从 npm 切换到 pnpm 很简单：\n```bash\n# 全局安装\nnpm install -g pnpm\n\n# 在项目根目录执行（自动读取 package.json + 生成 pnpm-lock.yaml）\npnpm install\n\n# 后续命令只需把 npm 换成 pnpm 即可\npnpm run dev\npnpm test\n```\n\n⚠️ 注意：首次使用 pnpm 时，建议运行 `pnpm store prune` 清理旧缓存，并检查是否有幽灵依赖导致报错（可临时用 `pnpm --no-strict-peer-dependencies install` 调试）。\n\n---\n\n如需，我还可以提供：\n- pnpm 配置最佳实践（`.pnpmrc` 示例）  \n- 如何在 Vue/Vite/Nx 等框架中集成 pnpm  \n- pnpm 与 Yarn PnP / Bun 的横向对比  \n\n欢迎继续提问 😊', NULL, '2026-03-30 11:28:51');
INSERT INTO `chat_message` VALUES (38, 18, 'user', '今天天气如何', NULL, '2026-03-30 11:33:41');
INSERT INTO `chat_message` VALUES (39, 18, 'assistant', '我无法实时获取天气信息，因为这需要访问当前的地理位置和网络天气服务（如气象局API），而我作为AI模型没有实时联网或定位功能。\n\n不过，你可以通过以下方式快速查看今天天气：\n\n✅ 手机：打开自带天气App，或使用微信/支付宝搜索“天气”  \n✅ 电脑：在浏览器中搜索“北京天气”（把“北京”换成你所在城市）  \n✅ 语音助手：对手机说“嘿 Siri / 小爱同学 / 小艺，今天天气怎么样？”\n\n如果你告诉我你所在的城市，我还可以帮你分析天气穿衣建议、是否需要带伞、适宜户外活动等 😊  \n需要的话，随时告诉我～', NULL, '2026-03-30 11:33:46');
INSERT INTO `chat_message` VALUES (40, 19, 'user', '帮我看看如何去创建一个Spring AI 的项目', NULL, '2026-04-03 15:38:00');
INSERT INTO `chat_message` VALUES (41, 19, 'assistant', '创建 Spring AI 项目非常直观。以下是基于 **Spring Boot 3.2+** 和 **Spring AI 1.0 GA** 的标准流程，包含依赖配置、核心代码与运行测试。\n\n---\n### 📦 前置要求\n- JDK 17+\n- Spring Boot 3.2+\n- Maven 3.8+ 或 Gradle 8+\n- AI 服务 API Key（如 OpenAI、通义千问、Ollama 本地部署等）\n\n---\n### 🛠 步骤 1：初始化项目\n1. 访问 [Spring Initializr](https://start.spring.io)\n2. 选择：\n   - Project: Maven / Gradle\n   - Language: Java\n   - Spring Boot: `3.3.x`（或最新稳定版）\n   - Dependencies: `Spring Web`\n3. 生成并解压项目\n\n---\n### 📥 步骤 2：添加 Spring AI 依赖\n以 **OpenAI** 为例，在 `pom.xml` 中添加：\n```xml\n<dependency>\n    <groupId>org.springframework.ai</groupId>\n    <artifactId>spring-ai-openai-spring-boot-starter</artifactId>\n    <version>1.0.0</version> <!-- 建议使用最新 GA 版本 -->\n</dependency>\n```\n> 💡 若使用其他模型（如 Ollama、Azure OpenAI、通义千问），只需替换对应的 `spring-ai-xxx-spring-boot-starter`。\n\n---\n### ⚙️ 步骤 3：配置 AI 服务\n在 `src/main/resources/application.yml` 中配置：\n```yaml\nspring:\n  ai:\n    openai:\n      api-key: ${OPENAI_API_KEY}  # 推荐通过环境变量注入\n      chat:\n        options:\n          model: gpt-3.5-turbo    # 或 gpt-4o 等\n```\n> 🔒 生产环境请勿硬编码 API Key，建议使用环境变量、Vault 或配置中心。\n\n---\n### 💻 步骤 4：编写核心代码\nSpring AI 1.0 推荐使用 `ChatClient`，代码更简洁且支持流式/函数调用：\n```java\n@RestController\n@RequestMapping(\"/ai\")\npublic class ChatController {\n\n    private final ChatClient chatClient;\n\n    // Spring 会自动注入 ChatClient.Builder\n    public ChatController(ChatClient.Builder chatClientBuilder) {\n        this.chatClient = chatClientBuilder.build();\n    }\n\n    @GetMapping(\"/chat\")\n    public String chat(@RequestParam String message) {\n        return chatClient.prompt()\n                .user(message)\n                .call()\n                .content();\n    }\n}\n```\n\n---\n### 🚀 步骤 5：运行与测试\n```bash\n# 启动项目\nmvn spring-boot:run\n\n# 测试接口\ncurl \"http://localhost:8080/ai/chat?message=用一句话解释什么是大语言模型\"\n```\n预期返回 AI 生成的文本内容。\n\n---\n### 📌 注意事项 & 进阶方向\n| 方向 | 说明 |\n|------|------|\n| **流式输出** | 使用 `.stream().content()` 返回 `Flux<String>`，适合 SSE 推送 |\n| **函数调用** | 通过 `@Tool` 注解暴露 Java 方法，让模型自主调用 |\n| **向量检索 (RAG)** | 使用 `VectorStore` + `DocumentReader` 实现知识库问答 |\n| **多模型切换** | 通过 `@Qualifier` 或配置 `spring.ai.chat.client.default-model` 动态切换 |\n| **版本兼容** | Spring AI 1.0 已 GA，依赖已同步至 Maven Central，无需额外仓库 |\n\n---\n### 📖 官方资源\n- 文档：https://docs.spring.io/spring-ai/reference/\n- 示例仓库：https://github.com/spring-projects/spring-ai-examples\n- 模型支持列表：OpenAI、Ollama、Azure OpenAI、通义千问、智谱、MiniMax 等\n\n如需特定模型（如 Ollama 本地部署）、流式响应、或 RAG 架构的完整示例，可告诉我你的具体场景，我会提供对应代码。', NULL, '2026-04-03 15:39:15');

-- ----------------------------
-- Table structure for interview_answer
-- ----------------------------
DROP TABLE IF EXISTS `interview_answer`;
CREATE TABLE `interview_answer`  (
                                     `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                     `session_id` bigint(0) NOT NULL COMMENT '关联的会话ID',
                                     `question_index` int(0) NULL DEFAULT NULL COMMENT '问题索引',
                                     `question` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '问题内容',
                                     `category` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '问题类别',
                                     `user_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '用户答案',
                                     `score` int(0) NULL DEFAULT NULL COMMENT '得分（0-100）',
                                     `feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '反馈评价',
                                     `reference_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '参考答案',
                                     `key_points_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '关键点列表（JSON格式）',
                                     `answered_at` datetime(0) NOT NULL COMMENT '回答时间',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     UNIQUE INDEX `uk_interview_answer_session_question`(`session_id`, `question_index`) USING BTREE,
                                     INDEX `idx_interview_answer_session_question`(`session_id`, `question_index`) USING BTREE,
                                     CONSTRAINT `fk_interview_answer_session` FOREIGN KEY (`session_id`) REFERENCES `interview_session` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '面试答案表 - 存储面试问答对和评估结果' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for interview_session
-- ----------------------------
DROP TABLE IF EXISTS `interview_session`;
CREATE TABLE `interview_session`  (
                                      `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                      `session_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '会话ID（UUID）',
                                      `resume_id` bigint(0) NOT NULL COMMENT '关联的简历ID',
                                      `total_questions` int(0) NULL DEFAULT NULL COMMENT '问题总数',
                                      `current_question_index` int(0) NULL DEFAULT 0 COMMENT '当前问题索引',
                                      `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'CREATED' COMMENT '会话状态：CREATED-已创建，IN_PROGRESS-进行中，COMPLETED-已完成，EVALUATED-已评估',
                                      `questions_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '问题列表（JSON格式）',
                                      `overall_score` int(0) NULL DEFAULT NULL COMMENT '总分（0-100）',
                                      `overall_feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '总体评价',
                                      `strengths_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '优势列表（JSON格式）',
                                      `improvements_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '改进建议列表（JSON格式）',
                                      `reference_answers_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '参考答案列表（JSON格式）',
                                      `created_at` datetime(0) NOT NULL COMMENT '创建时间',
                                      `updated_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '记录更新时间',
                                      `completed_at` datetime(0) NULL DEFAULT NULL COMMENT '完成时间',
                                      `evaluate_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '评估状态：PENDING-待处理，PROCESSING-处理中，COMPLETED-完成，FAILED-失败',
                                      `evaluate_error` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '评估错误信息（失败时记录）',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      UNIQUE INDEX `uk_interview_session_session_id`(`session_id`) USING BTREE,
                                      INDEX `idx_interview_session_resume_created`(`resume_id`, `created_at`) USING BTREE,
                                      INDEX `idx_interview_session_resume_status_created`(`resume_id`, `status`, `created_at`) USING BTREE,
                                      INDEX `idx_interview_session_status`(`status`) USING BTREE,
                                      CONSTRAINT `fk_interview_session_resume` FOREIGN KEY (`resume_id`) REFERENCES `resume` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '面试会话表 - 存储面试会话信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for knowledge_base
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_base`;
CREATE TABLE `knowledge_base`  (
                                   `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `file_hash` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件内容的SHA-256哈希值，用于去重',
                                   `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '知识库名称（用户自定义或从文件名提取）',
                                   `category` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类/分组（如Java面试、项目文档等）',
                                   `original_filename` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原始文件名',
                                   `file_size` bigint(0) NULL DEFAULT NULL COMMENT '文件大小（字节）',
                                   `content_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件类型（MIME类型）',
                                   `storage_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'RustFS存储的文件Key',
                                   `storage_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'RustFS存储的文件URL',
                                   `uploaded_at` datetime(0) NOT NULL COMMENT '上传时间',
                                   `last_accessed_at` datetime(0) NULL DEFAULT NULL COMMENT '最后访问时间',
                                   `access_count` int(0) NULL DEFAULT 0 COMMENT '访问次数',
                                   `question_count` int(0) NULL DEFAULT 0 COMMENT '问题数量（用户针对此知识库提问的次数）',
                                   `vector_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'PENDING' COMMENT '向量化状态：PENDING-待处理，PROCESSING-处理中，COMPLETED-完成，FAILED-失败',
                                   `vector_error` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '向量化错误信息（失败时记录）',
                                   `chunk_count` int(0) NULL DEFAULT 0 COMMENT '向量分块数量',
                                   `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
                                   `updated_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '记录更新时间',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   UNIQUE INDEX `uk_knowledge_base_file_hash`(`file_hash`) USING BTREE,
                                   INDEX `idx_knowledge_base_category`(`category`) USING BTREE,
                                   INDEX `idx_knowledge_base_vector_status`(`vector_status`) USING BTREE,
                                   INDEX `idx_knowledge_base_uploaded_at`(`uploaded_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '知识库表 - 存储上传的文档信息，使用RustFS存储文件' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for rag_chat_message
-- ----------------------------
DROP TABLE IF EXISTS `rag_chat_message`;
CREATE TABLE `rag_chat_message`  (
                                     `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                     `session_id` bigint(0) NOT NULL COMMENT '关联的会话ID',
                                     `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息类型：USER-用户消息，ASSISTANT-AI回答',
                                     `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
                                     `message_order` int(0) NOT NULL COMMENT '消息顺序（用于排序）',
                                     `created_at` datetime(0) NOT NULL COMMENT '创建时间',
                                     `updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新时间（用于流式响应更新）',
                                     `completed` tinyint(1) NULL DEFAULT 1 COMMENT '是否完成（流式响应时使用）',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     INDEX `idx_rag_message_session`(`session_id`) USING BTREE,
                                     INDEX `idx_rag_message_order`(`session_id`, `message_order`) USING BTREE,
                                     CONSTRAINT `fk_rag_message_session` FOREIGN KEY (`session_id`) REFERENCES `rag_chat_session` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'RAG聊天消息表 - 存储用户问题和AI回答' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for rag_chat_session
-- ----------------------------
DROP TABLE IF EXISTS `rag_chat_session`;
CREATE TABLE `rag_chat_session`  (
                                     `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                     `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '会话标题（可自动生成或用户自定义）',
                                     `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'ACTIVE' COMMENT '会话状态：ACTIVE-活跃，ARCHIVED-已归档',
                                     `created_at` datetime(0) NOT NULL COMMENT '创建时间',
                                     `updated_at` datetime(0) NULL DEFAULT NULL COMMENT '更新时间（最后一次消息时间）',
                                     `message_count` int(0) NULL DEFAULT 0 COMMENT '消息数量（冗余字段，方便查询）',
                                     `is_pinned` tinyint(1) NULL DEFAULT 0 COMMENT '是否置顶',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     INDEX `idx_rag_session_updated`(`updated_at`) USING BTREE,
                                     INDEX `idx_rag_session_status`(`status`) USING BTREE,
                                     INDEX `idx_rag_session_is_pinned`(`is_pinned`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'RAG聊天会话表 - 存储RAG聊天会话信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for resume
-- ----------------------------
DROP TABLE IF EXISTS `resume`;
CREATE TABLE `resume`  (
                           `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                           `file_hash` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件内容的SHA-256哈希值，用于去重',
                           `original_filename` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原始文件名',
                           `file_size` bigint(0) NULL DEFAULT NULL COMMENT '文件大小（字节）',
                           `content_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件类型（MIME类型）',
                           `storage_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'RustFS存储的文件Key',
                           `storage_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'RustFS存储的文件URL',
                           `resume_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '解析后的简历文本内容',
                           `uploaded_at` datetime(0) NOT NULL COMMENT '上传时间',
                           `last_accessed_at` datetime(0) NULL DEFAULT NULL COMMENT '最后访问时间',
                           `access_count` int(0) NULL DEFAULT 0 COMMENT '访问次数',
                           `analyze_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'PENDING' COMMENT '分析状态：PENDING-待处理，PROCESSING-处理中，COMPLETED-完成，FAILED-失败',
                           `analyze_error` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分析错误信息（失败时记录）',
                           `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
                           `updated_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '记录更新时间',
                           PRIMARY KEY (`id`) USING BTREE,
                           UNIQUE INDEX `uk_resume_file_hash`(`file_hash`) USING BTREE,
                           INDEX `idx_resume_analyze_status`(`analyze_status`) USING BTREE,
                           INDEX `idx_resume_uploaded_at`(`uploaded_at`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '简历表 - 存储上传的简历文件信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for resume_analysis
-- ----------------------------
DROP TABLE IF EXISTS `resume_analysis`;
CREATE TABLE `resume_analysis`  (
                                    `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                    `resume_id` bigint(0) NOT NULL COMMENT '关联的简历ID',
                                    `overall_score` int(0) NULL DEFAULT NULL COMMENT '总分（0-100）',
                                    `content_score` int(0) NULL DEFAULT NULL COMMENT '内容完整性评分（0-25）',
                                    `structure_score` int(0) NULL DEFAULT NULL COMMENT '结构清晰度评分（0-20）',
                                    `skill_match_score` int(0) NULL DEFAULT NULL COMMENT '技能匹配度评分（0-25）',
                                    `expression_score` int(0) NULL DEFAULT NULL COMMENT '表达专业性评分（0-15）',
                                    `project_score` int(0) NULL DEFAULT NULL COMMENT '项目经验评分（0-15）',
                                    `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '简历摘要',
                                    `strengths_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '优点列表（JSON格式）',
                                    `suggestions_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '改进建议列表（JSON格式）',
                                    `analyzed_at` datetime(0) NOT NULL COMMENT '评测时间',
                                    `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    INDEX `idx_resume_analysis_resume_id`(`resume_id`) USING BTREE,
                                    CONSTRAINT `fk_resume_analysis_resume` FOREIGN KEY (`resume_id`) REFERENCES `resume` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '简历分析结果表 - 存储AI对简历的分析结果' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
                             `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '部门ID',
                             `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '部门名称',
                             `parent_id` bigint(0) NULL DEFAULT 0 COMMENT '父级部门ID',
                             `leader` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '负责人',
                             `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
                             `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
                             `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
                             `sort` int(0) NULL DEFAULT 0 COMMENT '排序',
                             `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                             `deleted` tinyint(0) NULL DEFAULT 0 COMMENT '删除标记',
                             PRIMARY KEY (`id`) USING BTREE,
                             INDEX `idx_parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (1, '总公司', 0, 'admin', NULL, NULL, 1, 1, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_dept` VALUES (2, '技术部', 1, 'tech_leader', NULL, NULL, 1, 2, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_dept` VALUES (3, '产品部', 1, 'product_leader', NULL, NULL, 1, 3, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_dept` VALUES (4, '运营部', 1, 'operation_leader', NULL, NULL, 1, 4, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
                             `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
                             `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
                             `menu_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单编码',
                             `parent_id` bigint(0) NULL DEFAULT 0 COMMENT '父级菜单ID',
                             `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由路径',
                             `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组件路径',
                             `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标',
                             `menu_type` tinyint(0) NULL DEFAULT 1 COMMENT '类型：1-目录，2-菜单，3-按钮',
                             `visible` tinyint(0) NULL DEFAULT 1 COMMENT '是否可见：0-隐藏，1-显示',
                             `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
                             `sort` int(0) NULL DEFAULT 0 COMMENT '排序',
                             `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                             `deleted` tinyint(0) NULL DEFAULT 0 COMMENT '删除标记',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `uk_menu_code`(`menu_code`) USING BTREE,
                             INDEX `idx_parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '仪表盘', 'dashboard', 0, '/dashboard', 'dashboard/index', 'carbon-dashboard', 2, 1, 1, 0, '2026-03-24 06:25:16', '2026-03-24 06:25:16', 0);
INSERT INTO `sys_menu` VALUES (2, '简历管理', 'resume', 0, '/resume', 'resume/index', 'carbon-copy-file', 2, 1, 1, 1, '2026-03-24 06:25:16', '2026-04-03 07:45:07', 0);
INSERT INTO `sys_menu` VALUES (3, '问答助手', 'assistant', 0, '/assistant', 'assistant/index', 'carbon-chat-bot', 2, 1, 1, 2, '2026-03-24 06:25:16', '2026-04-03 07:49:35', 0);
INSERT INTO `sys_menu` VALUES (4, '面试助手', 'interview', 0, '/interview', 'interview/index', 'carbon-categories', 2, 1, 1, 3, '2026-03-24 06:25:16', '2026-04-03 07:47:53', 0);
INSERT INTO `sys_menu` VALUES (5, '系统管理', 'system', 0, '/system', 'Layout', 'carbon-settings', 1, 1, 1, 4, '2026-03-24 06:25:16', '2026-03-24 06:25:16', 0);
INSERT INTO `sys_menu` VALUES (6, '用户管理', 'user', 5, '/system/user', 'system/user/index', 'carbon-user-multiple', 2, 1, 1, 1, '2026-03-24 06:25:16', '2026-03-24 06:25:16', 0);
INSERT INTO `sys_menu` VALUES (7, '角色管理', 'role', 5, '/system/role', 'system/role/index', 'carbon-user-role', 2, 1, 1, 2, '2026-03-24 06:25:16', '2026-03-24 06:25:16', 0);
INSERT INTO `sys_menu` VALUES (8, '权限管理', 'permission', 5, '/system/permission', 'system/permission/index', 'carbon-locked', 2, 1, 1, 3, '2026-03-24 06:25:16', '2026-03-24 06:25:16', 0);
INSERT INTO `sys_menu` VALUES (9, '菜单管理', 'menu', 5, '/system/menu', 'system/menu/index', 'carbon-menu', 2, 1, 1, 4, '2026-03-24 06:25:16', '2026-03-24 06:25:16', 0);
INSERT INTO `sys_menu` VALUES (10, '部门管理', 'dept', 5, '/system/dept', 'system/dept/index', 'carbon-building', 2, 1, 1, 5, '2026-03-24 06:25:16', '2026-03-24 06:25:16', 0);
INSERT INTO `sys_menu` VALUES (11, '日志管理', 'log', 0, '/log', 'Layout', 'carbon-document', 1, 1, 1, 5, '2026-03-24 06:25:16', '2026-03-24 06:25:16', 0);
INSERT INTO `sys_menu` VALUES (12, '操作日志', 'operation-log', 11, '/log/operation', 'log/operation/index', 'carbon-activity', 2, 1, 1, 1, '2026-03-24 06:25:16', '2026-03-24 06:25:16', 0);
INSERT INTO `sys_menu` VALUES (13, '知识库管理', 'knowledge', 0, '/knowledge', 'Layout', 'carbon-folder', 1, 1, 1, 2, '2026-04-07 09:10:50', '2026-04-07 11:02:35', 0);
INSERT INTO `sys_menu` VALUES (14, '知识库列表', 'knowledge-list', 13, '/knowledge/list', 'knowledge/index', 'carbon-document', 2, 1, 1, 1, '2026-04-07 09:10:50', '2026-04-07 09:10:50', 0);
INSERT INTO `sys_menu` VALUES (15, '文档上传', 'knowledge-upload', 13, '/knowledge/upload', 'knowledge/Upload', 'carbon-upload', 2, 1, 1, 2, '2026-04-07 09:10:50', '2026-04-07 09:10:50', 0);
INSERT INTO `sys_menu` VALUES (16, '知识问答', 'knowledge-query', 13, '/knowledge/query', 'knowledge/Query', 'carbon-chat', 2, 1, 1, 3, '2026-04-07 09:10:50', '2026-04-07 09:10:50', 0);
INSERT INTO `sys_menu` VALUES (17, '简历上传', 'resume-upload', 2, '/resume/upload', 'resume/Upload', NULL, 2, 0, 1, 1, '2026-04-07 09:10:50', '2026-04-07 09:10:50', 0);
INSERT INTO `sys_menu` VALUES (18, '简历详情', 'resume-detail', 2, '/resume/:id', 'resume/Detail', NULL, 2, 0, 1, 2, '2026-04-07 09:10:50', '2026-04-07 09:10:50', 0);
INSERT INTO `sys_menu` VALUES (19, '面试会话', 'interview-session', 4, '/interview/session/:id', 'interview/Session', NULL, 2, 0, 1, 1, '2026-04-07 09:10:50', '2026-04-07 09:10:50', 0);

-- ----------------------------
-- Table structure for sys_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log`  (
                                      `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
                                      `user_id` bigint(0) NULL DEFAULT NULL COMMENT '用户ID',
                                      `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
                                      `operation` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作描述',
                                      `method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '方法名称',
                                      `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '请求参数',
                                      `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'IP地址',
                                      `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态：0-失败，1-成功',
                                      `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '错误信息',
                                      `execute_time` int(0) NULL DEFAULT NULL COMMENT '执行时间(毫秒)',
                                      `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      INDEX `idx_user_id`(`user_id`) USING BTREE,
                                      INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_operation_log
-- ----------------------------
INSERT INTO `sys_operation_log` VALUES (1, 1, 'admin', '分配角色菜单', 'com.enterprise.admin.controller.RoleController.assignMenus', '[1,[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]]', '0:0:0:0:0:0:0:1', 1, NULL, 170, '2026-03-18 11:54:34');
INSERT INTO `sys_operation_log` VALUES (3, 1, 'admin', '更新用户', 'com.enterprise.admin.controller.UserController.update', '[{\"id\":4,\"username\":\"user-test\",\"password\":\"123456\",\"nickname\":\"daliymove\",\"email\":\"www.daliymove@123.com\",\"phone\":\"133122312223\",\"status\":1}]', '0:0:0:0:0:0:0:1', 1, NULL, 96, '2026-03-18 12:02:20');
INSERT INTO `sys_operation_log` VALUES (4, 1, 'admin', '更新用户', 'com.enterprise.admin.controller.UserController.update', '[{\"id\":4,\"username\":\"user-test\",\"password\":\"123456\",\"nickname\":\"daliymove\",\"email\":\"www.daliymove@123.com\",\"phone\":\"13312231222\",\"status\":1}]', '0:0:0:0:0:0:0:1', 1, NULL, 107, '2026-03-18 12:02:27');
INSERT INTO `sys_operation_log` VALUES (5, 1, 'admin', '更新用户', 'com.enterprise.admin.controller.UserController.update', '[{\"id\":4,\"username\":\"user-test\",\"password\":\"123456\",\"nickname\":\"daliymove\",\"email\":\"www.daliymove@123.com\",\"phone\":\"13312231223\",\"status\":1}]', '0:0:0:0:0:0:0:1', 1, NULL, 211, '2026-03-18 14:30:48');
INSERT INTO `sys_operation_log` VALUES (6, 1, 'admin', '分配角色权限', 'com.daliymove.system.controller.RoleController.assignPermissions', '[3,[]]', '0:0:0:0:0:0:0:1', 1, NULL, 70, '2026-03-24 14:38:58');
INSERT INTO `sys_operation_log` VALUES (7, 1, 'admin', '分配用户角色', 'com.daliymove.system.controller.UserController.assignRoles', '[1,[1]]', '0:0:0:0:0:0:0:1', 1, NULL, 95, '2026-03-24 15:10:55');
INSERT INTO `sys_operation_log` VALUES (8, 1, 'admin', '分配角色菜单', 'com.daliymove.system.controller.RoleController.assignMenus', '[3,[1,2,3,4,11,12]]', '0:0:0:0:0:0:0:1', 1, NULL, 42, '2026-03-24 15:16:36');
INSERT INTO `sys_operation_log` VALUES (9, 1, 'admin', '新增用户', 'com.daliymove.system.controller.UserController.save', '[{\"username\":\"test\",\"password\":\"123456\",\"nickname\":\"test\",\"email\":\"\",\"phone\":\"\",\"status\":1}]', '0:0:0:0:0:0:0:1', 1, NULL, 233, '2026-03-24 16:24:45');
INSERT INTO `sys_operation_log` VALUES (10, 1, 'admin', '分配用户角色', 'com.daliymove.system.controller.UserController.assignRoles', '[5,[3]]', '0:0:0:0:0:0:0:1', 1, NULL, 33, '2026-03-24 16:24:51');

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
                                   `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '权限ID',
                                   `permission_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
                                   `permission_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限编码',
                                   `resource_type` tinyint(0) NULL DEFAULT 1 COMMENT '资源类型：1-菜单，2-按钮，3-接口',
                                   `resource_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '资源路径',
                                   `method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'HTTP方法：GET/POST/PUT/DELETE',
                                   `parent_id` bigint(0) NULL DEFAULT 0 COMMENT '父级ID',
                                   `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
                                   `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
                                   `sort` int(0) NULL DEFAULT 0 COMMENT '排序',
                                   `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                                   `deleted` tinyint(0) NULL DEFAULT 0 COMMENT '删除标记',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   UNIQUE INDEX `uk_permission_code`(`permission_code`) USING BTREE,
                                   INDEX `idx_parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES (1, '用户管理', 'system:user', 1, '/system/user', 'GET', 0, NULL, 1, 1, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_permission` VALUES (2, '用户新增', 'system:user:add', 2, '/api/user', 'POST', 1, NULL, 1, 1, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_permission` VALUES (3, '用户编辑', 'system:user:edit', 2, '/api/user', 'PUT', 1, NULL, 1, 2, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_permission` VALUES (4, '用户删除', 'system:user:delete', 2, '/api/user/*', 'DELETE', 1, NULL, 1, 3, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_permission` VALUES (5, '用户查询', 'system:user:query', 2, '/api/user/*', 'GET', 1, NULL, 1, 4, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_permission` VALUES (6, '角色管理', 'system:role', 1, '/system/role', 'GET', 0, NULL, 1, 2, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_permission` VALUES (7, '角色新增', 'system:role:add', 2, '/api/role', 'POST', 6, NULL, 1, 1, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_permission` VALUES (8, '角色编辑', 'system:role:edit', 2, '/api/role', 'PUT', 6, NULL, 1, 2, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_permission` VALUES (9, '角色删除', 'system:role:delete', 2, '/api/role/*', 'DELETE', 6, NULL, 1, 3, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_permission` VALUES (10, '角色查询', 'system:role:query', 2, '/api/role/*', 'GET', 6, NULL, 1, 4, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_permission` VALUES (11, '菜单管理', 'system:menu', 1, '/system/menu', 'GET', 0, NULL, 1, 3, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_permission` VALUES (12, '菜单新增', 'system:menu:add', 2, '/api/menu', 'POST', 11, NULL, 1, 1, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_permission` VALUES (13, '菜单编辑', 'system:menu:edit', 2, '/api/menu', 'PUT', 11, NULL, 1, 2, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_permission` VALUES (14, '菜单删除', 'system:menu:delete', 2, '/api/menu/*', 'DELETE', 11, NULL, 1, 3, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_permission` VALUES (15, '菜单查询', 'system:menu:query', 2, '/api/menu/*', 'GET', 11, NULL, 1, 4, '2026-03-17 06:42:50', '2026-03-18 02:26:15', 0);
INSERT INTO `sys_permission` VALUES (16, '权限管理', 'system:permission', 1, '/system/permission', 'GET', 0, NULL, 1, 4, '2026-03-17 06:42:50', '2026-03-18 02:33:25', 0);
INSERT INTO `sys_permission` VALUES (17, '权限新增', 'system:permission:add', 2, '/api/permission', 'POST', 16, NULL, 1, 1, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_permission` VALUES (18, '权限编辑', 'system:permission:edit', 2, '/api/permission', 'PUT', 16, NULL, 1, 2, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_permission` VALUES (19, '权限删除', 'system:permission:delete', 2, '/api/permission/*', 'DELETE', 16, NULL, 1, 3, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_permission` VALUES (20, '权限查询', 'system:permission:query', 2, '/api/permission/*', 'GET', 16, NULL, 1, 4, '2026-03-17 06:42:50', '2026-03-18 02:26:15', 0);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
                             `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
                             `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
                             `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
                             `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
                             `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
                             `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                             `deleted` tinyint(0) NULL DEFAULT 0 COMMENT '删除标记',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `uk_role_code`(`role_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'SUPER_ADMIN', '拥有所有权限', 1, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_role` VALUES (2, '管理员', 'ADMIN', '管理用户和角色', 1, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_role` VALUES (3, '普通用户', 'USER', '普通用户权限', 1, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
                                  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                  `role_id` bigint(0) NOT NULL COMMENT '角色ID',
                                  `menu_id` bigint(0) NOT NULL COMMENT '菜单ID',
                                  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  UNIQUE INDEX `uk_role_menu`(`role_id`, `menu_id`) USING BTREE,
                                  INDEX `idx_role_id`(`role_id`) USING BTREE,
                                  INDEX `idx_menu_id`(`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 74 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (39, 1, 1, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (40, 1, 2, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (41, 1, 3, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (42, 1, 4, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (43, 1, 5, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (44, 1, 6, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (45, 1, 7, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (46, 1, 8, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (47, 1, 9, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (48, 1, 10, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (49, 1, 11, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (50, 1, 12, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (51, 1, 13, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (52, 1, 14, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (53, 1, 15, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (54, 2, 1, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (55, 2, 2, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (56, 2, 3, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (57, 2, 4, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (58, 2, 5, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (59, 2, 6, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (60, 2, 7, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (61, 2, 9, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (62, 2, 13, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (63, 2, 14, '2026-03-24 06:25:16');
INSERT INTO `sys_role_menu` VALUES (68, 3, 1, '2026-03-24 15:16:36');
INSERT INTO `sys_role_menu` VALUES (69, 3, 2, '2026-03-24 15:16:36');
INSERT INTO `sys_role_menu` VALUES (70, 3, 3, '2026-03-24 15:16:36');
INSERT INTO `sys_role_menu` VALUES (71, 3, 4, '2026-03-24 15:16:36');
INSERT INTO `sys_role_menu` VALUES (72, 3, 11, '2026-03-24 15:16:36');
INSERT INTO `sys_role_menu` VALUES (73, 3, 12, '2026-03-24 15:16:36');
INSERT INTO `sys_role_menu` VALUES (81, 1, 16, '2026-04-07 09:13:20');
INSERT INTO `sys_role_menu` VALUES (82, 1, 17, '2026-04-07 09:13:20');
INSERT INTO `sys_role_menu` VALUES (83, 1, 18, '2026-04-07 09:13:20');
INSERT INTO `sys_role_menu` VALUES (84, 1, 19, '2026-04-07 09:13:20');

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
                                        `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                        `role_id` bigint(0) NOT NULL COMMENT '角色ID',
                                        `permission_id` bigint(0) NOT NULL COMMENT '权限ID',
                                        `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        UNIQUE INDEX `uk_role_permission`(`role_id`, `permission_id`) USING BTREE,
                                        INDEX `idx_role_id`(`role_id`) USING BTREE,
                                        INDEX `idx_permission_id`(`permission_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES (1, 1, 1, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (2, 1, 2, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (3, 1, 3, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (4, 1, 4, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (5, 1, 5, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (6, 1, 6, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (7, 1, 7, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (8, 1, 8, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (9, 1, 9, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (10, 1, 10, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (11, 1, 11, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (12, 1, 12, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (13, 1, 13, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (14, 1, 14, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (15, 2, 1, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (16, 2, 2, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (17, 2, 3, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (18, 2, 4, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (19, 2, 5, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (20, 2, 6, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (21, 2, 10, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (24, 1, 15, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (25, 1, 16, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (26, 1, 17, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (27, 1, 18, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (28, 1, 19, '2026-03-17 06:42:50');
INSERT INTO `sys_role_permission` VALUES (29, 1, 20, '2026-03-17 06:42:50');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
                             `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                             `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
                             `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
                             `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
                             `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
                             `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
                             `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
                             `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
                             `dept_id` bigint(0) NULL DEFAULT NULL COMMENT '部门ID',
                             `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                             `deleted` tinyint(0) NULL DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `uk_username`(`username`) USING BTREE,
                             INDEX `idx_dept_id`(`dept_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin@example.com', '13800138000', NULL, 1, 1, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_user` VALUES (2, 'tech_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '技术用户', 'tech@example.com', '13800138001', NULL, 1, 2, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_user` VALUES (3, 'product_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '产品用户', 'product@example.com', '13800138002', NULL, 1, 3, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_user` VALUES (4, 'user-test', '$2a$10$/bdEpUXBwVJM8wON36QGPOw5cse9zFpPK1gz6EW6Jrdukz9QnOz5C', 'daliymove', 'www.daliymove@123.com', '13312231223', NULL, 1, NULL, '2026-03-18 10:42:44', '2026-03-18 10:42:44', 0);
INSERT INTO `sys_user` VALUES (5, 'test', '$2a$10$jxtbUPetQpqrV7EuhHEedevNtu1NJVRsERGsC7dc1J1eJpsewqBY2', 'test', '', '', NULL, 1, NULL, '2026-03-24 16:24:46', '2026-03-24 16:24:46', 0);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
                                  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
                                  `role_id` bigint(0) NOT NULL COMMENT '角色ID',
                                  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  UNIQUE INDEX `uk_user_role`(`user_id`, `role_id`) USING BTREE,
                                  INDEX `idx_user_id`(`user_id`) USING BTREE,
                                  INDEX `idx_role_id`(`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (2, 2, 2, '2026-03-17 06:42:50');
INSERT INTO `sys_user_role` VALUES (3, 3, 3, '2026-03-17 06:42:50');
INSERT INTO `sys_user_role` VALUES (4, 4, 3, '2026-03-18 10:44:56');
INSERT INTO `sys_user_role` VALUES (5, 1, 1, '2026-03-24 15:10:55');
INSERT INTO `sys_user_role` VALUES (6, 5, 3, '2026-03-24 16:24:51');

SET FOREIGN_KEY_CHECKS = 1;
