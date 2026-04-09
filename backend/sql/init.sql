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

 Date: 09/04/2026 11:02:16
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '面试答案表 - 存储面试问答对和评估结果' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '面试会话表 - 存储面试会话信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of interview_session
-- ----------------------------
INSERT INTO `interview_session` VALUES (1, 'd121fe6a1fb54d01', 7, 16, 0, 'CREATED', '[{\"questionIndex\":0,\"question\":\"在IBMS改造项目中，你提到使用Spring Boot Starter封装第三方平台对接模块以实现自动配置和易于拆卸。请详细描述你是如何设计这个Starter的？其中自动配置类与条件装配注解（如@ConditionalOnProperty）是如何协同工作的？\",\"type\":\"PROJECT\",\"category\":\"项目架构 - 组件封装与自动配置\",\"userAnswer\":null,\"score\":null,\"feedback\":null,\"isFollowUp\":false,\"parentQuestionIndex\":null},{\"questionIndex\":1,\"question\":\"在外部化配置第三方平台信息时，如果多环境配置存在冲突或需要动态刷新，你会如何结合配置中心实现配置的优雅热更新而不重启服务？\",\"type\":\"PROJECT\",\"category\":\"项目架构 - 组件封装与自动配置（追问1）\",\"userAnswer\":null,\"score\":null,\"feedback\":null,\"isFollowUp\":true,\"parentQuestionIndex\":0},{\"questionIndex\":2,\"question\":\"在优化智慧社区平台“一张图”统计接口时，你使用了CompletableFuture配合自定义线程池来提升查询性能。请说明你是如何评估业务特征并设置该线程池核心参数（corePoolSize、maxPoolSize、workQueue）的？\",\"type\":\"PROJECT\",\"category\":\"项目实战 - 异步编程与性能调优\",\"userAnswer\":null,\"score\":null,\"feedback\":null,\"isFollowUp\":false,\"parentQuestionIndex\":null},{\"questionIndex\":3,\"question\":\"在多个CompletableFuture并行执行的过程中，如果其中一个子任务抛出异常或执行超时，你是如何设计异常捕获、降级或熔断策略以保证整体接口可用性的？\",\"type\":\"PROJECT\",\"category\":\"项目实战 - 异步编程与性能调优（追问1）\",\"userAnswer\":null,\"score\":null,\"feedback\":null,\"isFollowUp\":true,\"parentQuestionIndex\":2},{\"questionIndex\":4,\"question\":\"简历中提到针对“一张图”接口采用了子查询及内连接优化深度分页，并为数据域字段建立前缀索引。请从MySQL底层B+树和索引回表机制的角度，解释深度分页为什么会导致性能骤降？你的子查询优化方案具体是如何减少IO开销的？\",\"type\":\"MYSQL\",\"category\":\"MySQL - 索引原理与SQL调优\",\"userAnswer\":null,\"score\":null,\"feedback\":null,\"isFollowUp\":false,\"parentQuestionIndex\":null},{\"questionIndex\":5,\"question\":\"在建立前缀索引时，你是如何权衡前缀长度与区分度（Cardinality）的？如果后续业务要求对该字段进行精确等值查询，当前前缀索引策略是否仍然适用，为什么？\",\"type\":\"MYSQL\",\"category\":\"MySQL - 索引原理与SQL调优（追问1）\",\"userAnswer\":null,\"score\":null,\"feedback\":null,\"isFollowUp\":true,\"parentQuestionIndex\":4},{\"questionIndex\":6,\"question\":\"在人脸识别平台中，你通过优化Logstash同步SQL发现原表使用了MyISAM引擎和分区表，调整查询顺序后耗时大幅下降。请对比MyISAM和InnoDB在并发读写、锁机制和事务支持上的核心差异，并解释为什么先利用联合索引过滤年月日能显著提升执行计划效率？\",\"type\":\"MYSQL\",\"category\":\"MySQL - 存储引擎与执行计划优化\",\"userAnswer\":null,\"score\":null,\"feedback\":null,\"isFollowUp\":false,\"parentQuestionIndex\":null},{\"questionIndex\":7,\"question\":\"面对海量抓拍记录同步场景，除了调整SQL查询顺序，你还会考虑哪些架构或数据库层面的方案（如CDC、分库分表、读写分离）来进一步提升同步吞吐量并避免对业务主库造成过大压力？\",\"type\":\"MYSQL\",\"category\":\"MySQL - 存储引擎与执行计划优化（追问1）\",\"userAnswer\":null,\"score\":null,\"feedback\":null,\"isFollowUp\":true,\"parentQuestionIndex\":6},{\"questionIndex\":8,\"question\":\"在智慧社区项目中，对于访问频繁且更新频率低的字典数据，你采用了双写方案将数据缓存至Redis。请说明你具体是如何在业务代码中实现数据库与Redis双写的？在并发写入或网络瞬断情况下，如何保证缓存与数据库的最终一致性？\",\"type\":\"REDIS\",\"category\":\"Redis - 缓存架构与数据一致性\",\"userAnswer\":null,\"score\":null,\"feedback\":null,\"isFollowUp\":false,\"parentQuestionIndex\":null},{\"questionIndex\":9,\"question\":\"如果双写过程中Redis写入失败但MySQL已成功，你的补偿机制或重试策略是什么？对比应用层双写，你是否评估过基于Canal监听MySQL binlog异步更新缓存的方案，各自的优缺点是什么？\",\"type\":\"REDIS\",\"category\":\"Redis - 缓存架构与数据一致性（追问1）\",\"userAnswer\":null,\"score\":null,\"feedback\":null,\"isFollowUp\":true,\"parentQuestionIndex\":8},{\"questionIndex\":10,\"question\":\"项目涉及大量设备状态管理与高频告警处理，请结合Redis底层数据结构，说明你会如何选择合适的数据结构来存储“设备实时在线状态”或“设备最近N条告警流水”？如果Redis内存使用率达到maxmemory限制，你配置的淘汰策略是什么，依据是什么？\",\"type\":\"REDIS\",\"category\":\"Redis - 数据结构选型与内存管理\",\"userAnswer\":null,\"score\":null,\"feedback\":null,\"isFollowUp\":false,\"parentQuestionIndex\":null},{\"questionIndex\":11,\"question\":\"在海量设备接入的高并发场景下，如果Redis发生主从切换或AOF重写延迟，可能导致部分设备状态丢失或告警乱序，你会如何设计架构或引入消息队列来规避此类数据不一致风险？\",\"type\":\"REDIS\",\"category\":\"Redis - 数据结构选型与内存管理（追问1）\",\"userAnswer\":null,\"score\":null,\"feedback\":null,\"isFollowUp\":true,\"parentQuestionIndex\":10},{\"questionIndex\":12,\"question\":\"简历中注明熟悉JVM常见概念，并在项目中处理过大数据量异步导入。请简述Java中强引用、软引用、弱引用和虚引用的核心区别及典型应用场景。在设备信息批量导入导致JVM频繁触发Full GC时，你通常会遵循怎样的排查路径来定位内存问题？\",\"type\":\"JAVA_BASIC\",\"category\":\"Java基础 - 引用类型与JVM排查\",\"userAnswer\":null,\"score\":null,\"feedback\":null,\"isFollowUp\":false,\"parentQuestionIndex\":null},{\"questionIndex\":13,\"question\":\"在使用@Transactional注解控制多Sheet导入事务时，如果方法内部通过try-catch捕获了异常但没有重新抛出，事务是否会回滚？请从Spring AOP代理拦截和异常传播机制的角度解释原因。\",\"type\":\"JAVA_BASIC\",\"category\":\"Java基础 - 引用类型与JVM排查（追问1）\",\"userAnswer\":null,\"score\":null,\"feedback\":null,\"isFollowUp\":true,\"parentQuestionIndex\":12},{\"questionIndex\":14,\"question\":\"熟练掌握Java集合是后端开发的基础。请详细说明HashMap在JDK 1.8中的底层数据结构是什么？当发生哈希冲突且链表长度达到阈值时，为什么会转换为红黑树？这个树化阈值为什么设定为8，而退化回链表的阈值是6？\",\"type\":\"JAVA_COLLECTION\",\"category\":\"Java集合 - HashMap源码与数据结构\",\"userAnswer\":null,\"score\":null,\"feedback\":null,\"isFollowUp\":false,\"parentQuestionIndex\":null},{\"questionIndex\":15,\"question\":\"HashMap在多线程并发环境下进行put操作可能会导致数据覆盖或链表成环（JDK1.7），在并发场景中你会优先选择ConcurrentHashMap还是Collections.synchronizedMap，请结合底层分段锁或CAS+synchronized机制说明原因。\",\"type\":\"JAVA_COLLECTION\",\"category\":\"Java集合 - HashMap源码与数据结构（追问1）\",\"userAnswer\":null,\"score\":null,\"feedback\":null,\"isFollowUp\":true,\"parentQuestionIndex\":14}]', NULL, NULL, NULL, NULL, NULL, '2026-04-09 10:19:50', '2026-04-09 10:19:50', NULL, NULL, NULL);

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
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '简历表 - 存储上传的简历文件信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of resume
-- ----------------------------
INSERT INTO `resume` VALUES (7, '376fa3b70985299c388f7a2ac97cb6da953cb8a38b30bfde55f7c1ad7df94a77', '社招简历1.pdf', 133577, 'application/pdf', 'resumes/2026/04/09/5f84af0d_SheZhaoJianLi1.pdf', 'http://localhost:9000/simple/resumes/2026/04/09/5f84af0d_SheZhaoJianLi1.pdf', '个⼈信息\n\n- /男/1998.03/武汉\n\n- 电话/微信： ，邮箱：fr @foxmail.com\n\n求职意向\n期望职位：Java后端开发\n期望城市：成都\n\n教育经历\n\n学校 学历，专业 时间\n\n武汉⼤学 本科，通信⼯程 2017.09 - 2021.06\n\n⼯作经历/实习经历\nXX公司（2022 年 1⽉ ~ ⾄今 ）\n\n职位：Java 后端开发⼯程师\n⼯作内容：主要负责公司⼤数据平台、智慧社区平台、IBMS平台开发⼯作\n\nXX公司（2021年6⽉～2021年12⽉）\n\n职位：需求⼯程师\n⼯作内容：公司智慧警务平台、智慧社区平台需求分析与原型设计；开发进度跟进；客户拜访交流\n\n技能清单\n计算机基础 ：熟练掌握计算机⽹络、数据结构和算法、操作系统，了解计算机组成原理\nLinux : 熟练使⽤ Linux，有 Linux 下开发部署项⽬的实际经验\nJava：熟练掌握 Java 基础、集合、并发、熟悉 JVM 常⻅概念\n开发⼯具 ：熟练使⽤ Maven/Gradle、Git、IDEA 、Docker 等开发⼯具\n数据库：熟练掌握 MySQL 数据库以及常⻅优化⼿段，熟练掌握Redis\n搜索引擎 ：熟悉 Elasticsearch 的使⽤\n框架：熟练掌握 Spring、Spring MVC、 SpringBoot、MyBatis、Spring Security 等基础开发框架\n前端：熟练掌握 HTML5、CSS、Javascript、vue ，有 Vue 的实际开发经验\nDevops : 熟悉 Jenkins，熟悉 CI/CD 流程\n\n项⽬经历\n\n旧项⽬IBMS改造/物联⽹平台V0.1\n\n2023-01~⾄今  软件开发部  Java后端开发⼯程师\n\n项⽬描述 : 将IBMS（智能建筑管理系统）从C#语⾔迁移到java语⾔，主要功能点有设备接⼊、规则引擎、国\n标视频接⼊、OpenApi 开放接⼝、第三⽅平台对接。\n\n技术栈 : Springboot、MySQL、Redis、MQTT\n\n⼯作内容 :\n\n1. 搭建了IBMS开发框架：使⽤Gitlab、Jenkins、Sonar等⼯具搭建CI/CD流⽔线，⼤幅提升了测试部署效\n率；负责代码审查与合并⼯作，保证了项⽬质量；积极解决同事的开发问题，推进了项⽬进度。\n\n2. 开发了可扩展的第三⽅平台对接模块：采⽤模版模式定义了三⽅平台对接的标准化步骤，规范了对接三\n⽅平台的流程，提⾼了代码规范性和可维护性；使⽤Spring Boot Starter封装对接的第三⽅平台，使得\n相关模块能够⾃动配置，并且易于拆卸；使⽤spring 外部化配置第三⽅平台信息，有效减少硬编码，提\n升了代码可维护性；搭建EasyMock服务，⽤于模拟第三⽅平台接⼝，⽅便了在⽹络隔离情况下的接⼝\n对接⼯作\n\n3. 开发了设备接⼊模块的后端功能和前端⻚⾯：使⽤异步编程优化了设备导⼊功能，提升了设备信息导⼊\n速度；通过事务控制保证了多sheet导⼊多个表数据情况下的数据正确性；通过合理的抽象和封装，使\n得模块的可复⽤性和可维护性得到了⼤幅提升\n\n智慧社区平台\n\n2022-05~2022-12  软件开发部   Java后端开发⼯程师\n\n项⽬描述 : 智慧平安社区综合管理平台，提供辖区内告警研判处置、数据汇聚处理、⼯单流转、物联感知设备\n管理、特殊⼈群⾏踪监测、可视指挥调度等功能。\n\n技术栈：Springboot、Springcloud、Oracle、Mybatis、Redis、Kafka、Nginx\n\n⼯作内容 :\n\n1. 主导信访⽭调模块的开发⼯作：主要负责⽭盾纠纷的新增、处置、流转、办理以及相关数据统计分析等\n功能点；在有限的时间内，通过合理的设计和实现，成功地完成了这些功能的开发，并保证了项⽬的如\n期交付\n\n2. 优化了⼀张图相关统计接⼝：使⽤CompletableFuture提⾼了相关接⼝查询性能，并为其设置了合理的\n线程池，避免了可能发⽣的过多创建线程问题；优化相关sql及表结构，有效提升数据库查询速率：对于\n访问频繁⾼并且更新频率低的类似字典数据，采⽤双写⽅案将相关数据缓存⼊redis；对于对于访问频率\n⾼、更新频率⼤，并且每条记录字段多的数据，开启mybatis⼆级缓存；采⽤⼦查询及内连接优化深度\n分⻚，有效减少回表次数，有效提升深度分⻚场景下查询速率；为数据域字段建⽴前缀索引，有效提升\n数据域过滤速率\n\n3. 开发了告警⽣成相关业务逻辑，采⽤责任链模式与策略模式有效降低代码耦合度：告警⽣成功能涉及⼏\n⼗种设备、⼈脸抓拍、数据研判等类型告警、在这⾥我们通过kafka接收到相关告警后，先对数据进⾏预\n处理；之后将数据分为⼈、房屋、设备等类型的告警、采⽤策略模式依据数据分类选取不同处理逻辑；\n之后通过责任链分别处理数据落库、重复告警忽略、告警⽣成、关联告警⽣成、发送相关告警消息给责\n任⼈等逻辑\n\nXX⼈脸识别平台\n\n2022-01~2022-06  软件开发部  Java后端开发⼯程师\n\n项⽬描述 : ⼀个⼩型⼈脸识别平台，提供3000路以内⼈脸摄像头抓拍解析处理识别搜索能⼒\n\n技术栈：Spring、Elasticsearch、MySQL、Kafka\n\n⼯作内容 :\n\n1. 针对⼈脸检索缓慢问题，使⽤Elasticsearch改造相关接⼝：采⽤Spring @Conditional相关注解条件装\n配新增加的Elasticsearch模块相关bean，程序启动时依据条件控制是否装配Elasticsearch模块，前端通\n过传参控制是否使⽤Elasticsearch查询，有效兼容了旧接⼝；为⼈脸抓拍记录等数据建⽴Elasticsearch\n索引，有效提升了检索速率；为Elasticsearch ⽂档设置索引⽣命周期策略，有效节省了存储空间、有效\n提升了查询速率；通过限制前端禁⽌跳⻚和最⼤分⻚深度提升系统稳定性和可靠性，保障了查询速度；\n通过以上措施有效提升了⼈脸检索相关接⼝性能，相关接⼝响应时间从20s以内优化到平均延时\n200ms，最⼤延时2s\n\n2. 优化Logstash数据同步sql，有效提升相关sql性能：观测Logstash⽇志发现⼈脸抓拍记录表数据同步\nsql每次查询耗时1s以上， 查看sql执⾏计划发现该sql已⾛索引，查询耗时200ms才正常，查看建表DDL\n后发现该表使⽤了MyISAM存储引擎，并且使⽤了分区表，分区键使⽤了联合索引年、⽉、⽇，遂调整\nsql查询语句，先查询联合索引年、⽉、⽇，调整后sql查询耗时成功降低到200ms以内\n\n	个人信息\n	求职意向\n	教育经历\n	工作经历/实习经历\n	技能清单\n	项目经历\n	旧项目IBMS改造/物联网平台V0.1\n	智慧社区平台\n	XX人脸识别平台', '2026-04-09 09:15:41', NULL, 0, 'COMPLETED', NULL, '2026-04-09 09:15:41', '2026-04-09 09:15:41');

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
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '简历分析结果表 - 存储AI对简历的分析结果' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of resume_analysis
-- ----------------------------
INSERT INTO `resume_analysis` VALUES (2, 7, 74, 13, 10, 16, 7, 28, '简历具备985名校背景与真实的政企/IoT项目经验，技术栈完整且已具备初步的架构封装意识，但在技术名词规范、业务价值量化表达及分布式架构深度上仍需打磨。', '[\"985院校通信工程背景，基础素质与工程学习能力优秀\",\"项目贴合政企/IoT实际业务，具备ES检索调优、Kafka消息处理、异步编排等实战落地经验\",\"已具备架构思维，能主动运用设计模式与Spring Boot Starter进行模块抽象与标准化封装\"]', '[{\"category\":\"格式\",\"priority\":\"高\",\"issue\":\"技术名词大小写及拼写严重不规范，影响技术专业性\",\"recommendation\":\"【名词纠错清单】Springboot→Spring Boot, springcloud→Spring Cloud, Mybatis→MyBatis, spring→Spring, vue→Vue, mysql→MySQL, Logstash→Logstash。技术栈展示必须严格遵循官方文档命名规范，避免全小写或连写。\"},{\"category\":\"项目\",\"priority\":\"高\",\"issue\":\"核心项目描述缺乏量化指标，未严格遵循STAR法则，业务闭环与技术深度结合不足\",\"recommendation\":\"【深度重写示例-智慧社区接口优化】原句：“优化了一张图相关统计接口...采用CompletableFuture...双写方案将相关数据缓存入redis...” 优化句：“针对社区数据大屏聚合查询耗时>3s痛点，基于 CompletableFuture 异步编排多源数据查询，自定义隔离线程池防资源耗尽；采用 Caffeine+Redis 多级缓存架构替代 MyBatis 二级缓存，结合 SQL 执行计划分析与前缀索引优化，将核心接口 P99 延迟降至 200ms 内，QPS 提升 4 倍。”\"},{\"category\":\"项目\",\"priority\":\"高\",\"issue\":\"部分技术选型在分布式/高并发场景下存在隐患或方案陈旧\",\"recommendation\":\"【方案优化建议】1. 缓存架构：分布式环境下 MyBatis 二级缓存易导致脏读，建议替换为 Caffeine 本地缓存 + Redis 分布式缓存；2. 数据同步：Logstash 定时轮询同步易造成DB压力与延迟，建议替换为 Canal 监听 MySQL Binlog + Kafka 实现 ES 实时增量同步；3. 设备导入：明确使用 EasyExcel 流式解析 + 任务表异步分片处理，替代模糊的“异步编程”，并补充百万级数据导入耗时指标（如从 15min 优化至 2min）。\"},{\"category\":\"表达\",\"priority\":\"中\",\"issue\":\"描述存在大量冗余词汇与主观表述，缺乏强动词引导与技术穿透力\",\"recommendation\":\"删除所有“负责...开发”、“大幅/有效提升了”等冗余词汇。统一采用“强动词（主导/重构/设计/落地）+ 核心技术方案 + 解决的业务痛点 + 量化结果”结构。单条 Bullet Point 严格控制在 2 行以内，确保面试官 3 秒内抓取高价值技术点。\"},{\"category\":\"技能\",\"priority\":\"中\",\"issue\":\"技能清单描述过于宽泛，缺乏对底层原理与高阶组件的精准界定\",\"recommendation\":\"将“熟悉JVM常见概念”具象化为“掌握JVM内存模型与GC算法，具备使用 Arthas/MAT 排查线上 OOM 与 CPU 飙高问题的实战经验”；补充微服务治理（Sentinel/Nacos）、分布式一致性（Seata/可靠消息最终一致性）、全链路监控等关键词，精准对齐中高级Java后端岗位JD要求。\"}]', '2026-04-09 09:17:45', '2026-04-09 09:17:45');

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
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;

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
INSERT INTO `sys_menu` VALUES (19, '面试会话', 'interview-session', 4, '/interview/session', 'interview/Session', NULL, 2, 0, 1, 1, '2026-04-07 09:10:50', '2026-04-09 02:16:26', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 85 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色菜单关联表' ROW_FORMAT = Dynamic;

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
