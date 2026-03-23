# 企业管理后台

基于 Spring Boot 3 + Sa-Token + Vue 3 + Naive UI 构建的 RBAC 权限管理系统。

## 项目结构

```
├── backend/                # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/enterprise/admin/
│   │   │   │   ├── config/         # 配置类
│   │   │   │   ├── controller/     # 控制器
│   │   │   │   ├── entity/         # 实体类
│   │   │   │   ├── mapper/         # Mapper接口
│   │   │   │   ├── service/        # 服务层
│   │   │   │   ├── dto/            # 数据传输对象
│   │   │   │   ├── vo/             # 视图对象
│   │   │   │   └── common/         # 公共类
│   │   │   └── resources/
│   │   │       ├── application.yml # 配置文件
│   │   │       └── sql/            # SQL脚本
│   │   └── pom.xml
├── frontend/               # 前端项目
│   ├── src/
│   │   ├── api/           # API 接口封装
│   │   ├── components/    # 公共组件
│   │   │   └── NaiveProvider.vue  # Naive UI 全局 API 提供者
│   │   ├── composables/   # 组合式函数
│   │   ├── layouts/       # 布局组件
│   │   │   └── BasicLayout.vue    # 主布局 (侧边栏 + 顶栏)
│   │   ├── router/        # 路由配置
│   │   ├── stores/        # Pinia 状态管理
│   │   ├── types/         # TypeScript 类型定义
│   │   │   └── global.d.ts        # 全局类型声明
│   │   ├── utils/         # 工具函数
│   │   ├── views/         # 页面组件
│   │   │   ├── dashboard/         # 仪表盘
│   │   │   ├── login/             # 登录页
│   │   │   ├── system/            # 系统管理
│   │   │   │   ├── user/          # 用户管理
│   │   │   │   ├── role/          # 角色管理
│   │   │   │   ├── permission/    # 权限管理
│   │   │   │   └── menu/          # 菜单管理
│   │   │   └── error/             # 错误页面
│   │   ├── App.vue        # 根组件
│   │   └── main.ts        # 入口文件
│   ├── uno.config.ts      # UnoCSS 配置
│   ├── vite.config.ts     # Vite 配置
│   └── package.json
└── README.md
```

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
- Vue 3.5 - 渐进式 JavaScript 框架
- TypeScript 5.9 - JavaScript 的超集，提供类型安全
- Vite 5.4 - 下一代前端构建工具
- Naive UI 2.37 - Vue 3 组件库
- Vue Router 4.2 - 官方路由管理器
- Pinia 2.1 - Vue 状态管理库
- Axios - HTTP 请求库
- UnoCSS - 即时原子化 CSS 引擎
- @iconify-json/carbon - Carbon Design 图标集
- unplugin-auto-import - 自动导入 API
- unplugin-vue-components - 自动导入组件
- dayjs - 轻量级日期处理库

### UI 设计
- 主题色: `#18a058` (绿色)
- 图标: Carbon Design System 图标 (通过 UnoCSS preset-icons)
- 布局: 响应式侧边栏 + 顶部导航
- 风格: 简洁现代风格

## 功能特性

### RBAC权限模型
- **用户管理**: 用户增删改查、分配角色、状态管理
- **角色管理**: 角色增删改查、分配权限和菜单
- **权限管理**: 权限增删改查、支持菜单/按钮/接口三种类型
- **菜单管理**: 树形菜单管理、支持目录/菜单/按钮三种类型

### 前端特性
- **响应式布局**: 适配桌面和移动端
- **动态路由**: 基于权限动态生成菜单
- **主题定制**: 可配置的主题色和样式
- **按需加载**: 组件和 API 自动导入
- **TypeScript**: 完整的类型支持
- **现代 UI**: 简洁现代的设计风格

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

2. 修改配置文件 `backend/src/main/resources/application.yml`
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
mvn spring-boot:run
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

### ER关系图
```
User ---< UserRole >--- Role
                          |
                          +---< RolePermission >--- Permission
                          |
                          +---< RoleMenu >--- Menu
```

## 权限使用示例

### 后端权限校验
```java
// 权限校验
@SaCheckPermission("system:user:add")
public Result<Void> save(@RequestBody UserDTO dto) {
    // ...
}

// 角色校验
@SaCheckRole("ADMIN")
public Result<Void> adminOnly() {
    // ...
}
```

### 前端权限控制
```typescript
// 路由守卫会自动检查登录状态
// 根据用户权限动态加载菜单
```

## 项目构建

### 后端构建
```bash
cd backend
mvn clean package -DskipTests
java -jar target/enterprise-admin-1.0.0.jar
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
- 图标使用 Carbon Design (`i-carbon-*` 格式)

### 前端开发指南

#### UnoCSS 使用
项目使用 UnoCSS 作为原子化 CSS 解决方案，常用类名：

```html
<!-- 布局 -->
<div class="flex-center">居中布局</div>
<div class="flex-between">两端对齐</div>

<!-- 图标 (Carbon Design) -->
<span class="i-carbon-user"></span>
<span class="i-carbon-dashboard"></span>
<span class="i-carbon-settings"></span>

<!-- 主题色 -->
<span class="text-primary">主题色文字</span>
<button class="bg-primary">主题色按钮</button>
```

#### Naive UI 全局 API
通过 `NaiveProvider` 组件注入全局 API：

```typescript
// 在组件中使用
window.$message.success('操作成功')
window.$dialog.warning({ title: '警告', content: '...' })
window.$notification.info({ title: '提示', content: '...' })
```

#### 自动导入
项目配置了 unplugin-auto-import 和 unplugin-vue-components：

```typescript
// 无需手动导入
const count = ref(0)  // Vue API 自动导入
const router = useRouter()  // Vue Router API 自动导入
const store = useUserStore()  // Pinia store 自动导入

// Naive UI 组件自动导入
<n-button>按钮</n-button>
<n-card>卡片</n-card>
```

## License

MIT License