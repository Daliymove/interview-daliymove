# AGENTS.md - AI 助手全局记忆

> 本文件用于存储项目的开发规范和全局记忆，AI 助手在开发过程中应始终遵循这些规范。

## 代码注释规范

### 核心原则

**所有开发代码都必须添加必要的注释，提高代码可读性和可维护性。**

### 后端注释规范（Java）

#### 1. 文件/类注释

每个 Java 文件都应该在文件顶部添加文件注释：

```java
/**
 * 类/文件主要功能和用途
 * - 功能1
 * - 功能2
 */
```

#### 2. 方法注释

**重要方法必须添加注释**，包括：
- 公共方法（public）
- 业务逻辑方法
- 复杂的私有方法

方法注释格式：

```java
/**
 * 方法简要描述
 * @param paramName 参数说明
 * @return 返回值说明
 */
public Result method(Type param) {
    // ...
}
```

#### 3. 字段注释

```java
/** 用户 Token，从 localStorage 初始化 */
private String token;
```

### 前端注释规范（TypeScript/Vue）

#### 1. 文件注释

每个文件都应该在顶部添加文件注释：

```typescript
/**
 * 文件主要功能和用途
 * - 功能1
 * - 功能2
 */
```

#### 2. 函数/方法注释

```typescript
/**
 * 函数简要描述
 * @param param 参数说明
 * @returns 返回值说明
 */
function functionName(param: Type): ReturnType {
    // ...
}
```

#### 3. 变量注释

```typescript
/** 用户 Token，从 localStorage 初始化 */
const token = ref<string>(localStorage.getItem('token') || '')
```

### 注释最佳实践

1. **注释应该解释"为什么"，而不是"是什么"**
2. **保持注释的及时更新**
3. **注释要简洁明了，使用列表形式**
4. **复杂逻辑和公共 API 必须注释**

## 其他开发规范

### 后端开发规范

1. 遵循阿里巴巴 Java 开发规范
2. Controller 只负责请求响应
3. Service 处理业务逻辑
4. Mapper 只做数据库操作
5. 使用事务注解 `@Transactional` 标记事务方法

### 前端开发规范

1. 使用 TypeScript
2. 组件使用 `<script setup>` 语法
3. 使用 Pinia 进行状态管理
4. API 统一放在 `src/api/` 目录
5. 使用 UnoCSS 原子化 CSS 类名

## 项目结构

详细的项目结构请参考 README.md

## 技术栈

详细的技术栈信息请参考 README.md

---

**重要提示**：AI 助手在开发任何新代码或修改现有代码时，都必须遵循以上注释规范，确保代码的可读性和可维护性。