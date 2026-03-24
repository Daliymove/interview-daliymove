# Daliymove - 面试移动端后端服务

基于 Spring Boot 3 + Sa-Token 构建的 RBAC 权限管理系统，支持面试分析等功能扩展。

## 项目结构

```
├── backend/                        # 后端项目
│   ├── pom.xml                     # 父POM
│   ├── daliymove-common/           # 通用模块
│   │   └── src/main/java/com/daliymove/common/
│   │       ├── annotation/         # 注解（Log等）
│   │       ├── domain/             # 领域对象（LoginUser等）
│   │       ├── dto/                # 通用DTO（PageDTO等）
│   │       ├── exception/          # 异常处理（GlobalExceptionHandler）
│   │       └── response/           # 响应对象（Result、PageResult等）
│   ├── daliymove-system/           # 系统模块
│   │   └── src/main/java/com/daliymove/system/
│   │       ├── aspect/             # 切面（OperationLogAspect）
│   │       ├── config/             # 配置（SaTokenConfig）
│   │       ├── controller/         # 控制器
│   │       ├── dto/                # 数据传输对象
│   │       ├── entity/             # 实体类
│   │       ├── mapper/             # Mapper接口
│   │       ├── service/            # 服务层
│   │       └── vo/                 # 视图对象
│   ├── daliymove-modules/          # 业务模块（面试分析等）
│   │   └── src/main/java/com/daliymove/modules/
│   ├── daliymove-server/           # 启动模块
│   │   └── src/main/
│   │       ├── java/com/daliymove/server/
│   │       │   ├── DaliymoveApplication.java
│   │       │   └── config/         # 配置（MybatisPlus、Swagger）
│   │       └── resources/
│   │           ├── application.yml
│   │           └── mapper/         # XML映射文件
│   └── sql/                        # SQL脚本
├── frontend/                       # 前端项目
│   ├── src/
│   │   ├── api/                    # API 接口封装
│   │   ├── components/             # 公共组件
│   │   ├── composables/            # 组合式函数
│   │   ├── layouts/                # 布局组件
│   │   ├── router/                 # 路由配置
│   │   ├── stores/                 # Pinia 状态管理
│   │   ├── types/                  # TypeScript 类型定义
│   │   ├── utils/                  # 工具函数
│   │   └── views/                  # 页面组件
│   ├── vite.config.ts
│   └── package.json
└── README.md
```

## 模块说明

| 模块 | 说明 | 依赖 |
|------|------|------|
| daliymove-common | 通用模块：工具类、响应对象、异常处理、注解 | 无 |
| daliymove-system | 系统模块：用户鉴权、菜单管理、权限管理 | common |
| daliymove-modules | 业务模块：面试分析等功能（待开发） | system, common |
| daliymove-server | 启动模块：应用入口、配置文件 | system, modules, common |

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2.0
- Sa-Token 1.37.0 (JWT认证)
- MyBatis-Plus 3.5.5
- MySQL 8.0
- Redis
- Knife4j (Swagger API文档)

### 前端
- Vue 3.5
- TypeScript 5.9
- Vite 5.4
- Naive UI 2.37
- Vue Router 4.2
- Pinia 2.1
- Axios
- UnoCSS

## 功能特性

### RBAC权限模型
- 用户管理：用户增删改查、分配角色、状态管理
- 角色管理：角色增删改查、分配权限和菜单
- 权限管理：权限增删改查、支持菜单/按钮/接口三种类型
- 菜单管理：树形菜单管理、支持目录/菜单/按钮三种类型

### 安全特性
- Sa-Token JWT认证
- 权限注解校验
- 角色注解校验
- 密码BCrypt加密

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+

### 后端启动

1. 创建数据库并导入SQL
```bash
mysql -u root -p < backend/sql/init.sql
```

2. 修改配置文件 `backend/daliymove-server/src/main/resources/application.yml`
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/enterprise_admin
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
```

3. 启动后端
```bash
cd backend
mvn spring-boot:run -pl daliymove-server
```

后端启动后访问:
- API: http://localhost:8080/api
- API文档: http://localhost:8080/api/doc.html

### 前端启动

1. 安装依赖
```bash
cd frontend
npm install
```

2. 启动开发服务器
```bash
npm run dev
```

前端启动后访问: http://localhost:3000

### 默认账号
- 用户名: `admin`
- 密码: `123456`

## API文档

启动后端后访问 Knife4j 文档: http://localhost:8080/api/doc.html

### 主要API接口

| 模块 | 接口 | 方法 | 描述 |
|------|------|------|------|
| 认证 | /auth/login | POST | 登录 |
| 认证 | /auth/logout | POST | 登出 |
| 认证 | /auth/current | GET | 获取当前用户 |
| 用户 | /user/page | GET | 分页查询用户 |
| 用户 | /user | POST | 新增用户 |
| 用户 | /user | PUT | 更新用户 |
| 用户 | /user/{id} | DELETE | 删除用户 |
| 角色 | /role/page | GET | 分页查询角色 |
| 角色 | /role | POST | 新增角色 |
| 角色 | /role/{id}/permissions | POST | 分配权限 |
| 菜单 | /menu/tree | GET | 获取菜单树 |
| 菜单 | /menu/routers | GET | 获取用户路由 |
| 权限 | /permission/page | GET | 分页查询权限 |

## 数据库设计

### 核心表结构
- `sys_user` - 用户表
- `sys_role` - 角色表
- `sys_permission` - 权限表
- `sys_menu` - 菜单表
- `sys_user_role` - 用户角色关联表
- `sys_role_permission` - 角色权限关联表
- `sys_role_menu` - 角色菜单关联表
- `sys_dept` - 部门表
- `sys_operation_log` - 操作日志表

## 项目构建

### 后端构建
```bash
cd backend
mvn clean package -DskipTests
java -jar daliymove-server/target/daliymove-server-1.0.0.jar
```

### 前端构建
```bash
cd frontend
npm run build
# 构建产物在 dist/ 目录
```

## 开发规范

### 后端代码规范
- 遵循阿里巴巴Java开发规范
- Controller只负责请求响应
- Service处理业务逻辑
- Mapper只做数据库操作

### 前端代码规范
- 使用 TypeScript
- 组件使用 `<script setup>` 语法
- 使用 Pinia 进行状态管理
- API 统一放在 `src/api/` 目录
- 使用 UnoCSS 原子化 CSS 类名

## License

MIT License