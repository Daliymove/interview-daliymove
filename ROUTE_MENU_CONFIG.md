# 路由和菜单配置说明

## 配置概述

本次配置为迁移的页面添加了路由和菜单配置，包括：

### 新增模块

#### 1. 知识库管理模块
- **知识库列表** - `/knowledge/list` - 知识库管理页面
- **文档上传** - `/knowledge/upload` - 上传知识库文档
- **知识问答** - `/knowledge/query` - RAG 问答页面

#### 2. 简历管理子页面
- **简历上传** - `/resume/upload` - 上传简历页面（隐藏菜单）
- **简历详情** - `/resume/:id` - 简历详情页面（隐藏菜单）

#### 3. 面试管理子页面
- **面试会话** - `/interview/session/:id` - 面试会话页面（隐藏菜单）

## 修改内容

### 前端修改

#### 1. 路由配置 (frontend/src/router/index.ts)

更新了 `componentMap`，添加了新页面的组件映射：

```typescript
const componentMap: Record<string, () => Promise<any>> = {
  // ... 原有配置
  
  // 简历模块
  'resume/index': () => import('@/views/resume/index.vue'),
  'resume/Upload': () => import('@/views/resume/Upload.vue'),
  'resume/Detail': () => import('@/views/resume/Detail.vue'),
  
  // 面试模块
  'interview/index': () => import('@/views/interview/index.vue'),
  'interview/Session': () => import('@/views/interview/Session.vue'),
  
  // 知识库模块
  'knowledge/index': () => import('@/views/knowledge/index.vue'),
  'knowledge/Upload': () => import('@/views/knowledge/Upload.vue'),
  'knowledge/Query': () => import('@/views/knowledge/Query.vue')
}
```

### 后端修改

#### 1. 数据库菜单配置 (backend/sql/menu_migration.sql)

创建了菜单数据迁移脚本，包含：

1. **知识库管理菜单**（ID: 13）
   - 知识库列表（ID: 14）
   - 文档上传（ID: 15）
   - 知识问答（ID: 16）

2. **隐藏菜单**（不显示在侧边栏）
   - 简历上传（ID: 17）
   - 简历详情（ID: 18）
   - 面试会话（ID: 19）

3. **角色权限分配**
   - 超级管理员（role_id=1）：所有新菜单
   - 管理员（role_id=2）：所有新菜单
   - 普通用户（role_id=3）：所有新菜单

## 菜单层级结构

```
├── 仪表盘
├── 简历管理
│   ├── 简历列表（主菜单）
│   ├── 简历上传（隐藏）
│   └── 简历详情（隐藏）
├── 知识库管理（新增）
│   ├── 知识库列表
│   ├── 文档上传
│   └── 知识问答
├── 面试助手
│   ├── 历史列表（主菜单）
│   └── 面试会话（隐藏）
├── 个人助手
└── 系统管理
    ├── 用户管理
    ├── 角色管理
    ├── 权限管理
    ├── 菜单管理
    └── 部门管理
```

## 执行步骤

### 1. 执行数据库迁移脚本

在 MySQL 中执行以下脚本：

```bash
mysql -u root -p enterprise_admin < backend/sql/menu_migration.sql
```

或者在 MySQL 客户端中执行：

```sql
source backend/sql/menu_migration.sql;
```

### 2. 重启后端服务

```bash
cd backend
# 根据项目实际情况重启服务
```

### 3. 清除前端缓存并重新登录

由于菜单是从后端动态加载的，需要：
1. 清除浏览器缓存或使用无痕模式
2. 重新登录系统

## 验证清单

### 路由验证
- [ ] `/knowledge` - 知识库列表页面正常显示
- [ ] `/knowledge/upload` - 文档上传页面正常显示
- [ ] `/knowledge/query` - 知识问答页面正常显示
- [ ] `/resume/upload` - 简历上传页面正常显示
- [ ] `/resume/:id` - 简历详情页面正常显示（替换 :id 为实际 ID）
- [ ] `/interview/session/:id` - 面试会话页面正常显示（替换 :id 为实际 ID）

### 菜单验证
- [ ] 侧边栏显示"知识库管理"菜单
- [ ] 知识库管理展开后显示三个子菜单
- [ ] 点击菜单项能够正常跳转
- [ ] 隐藏菜单（简历上传、简历详情、面试会话）不在侧边栏显示

### 权限验证
- [ ] 超级管理员能看到所有新菜单
- [ ] 管理员能看到所有新菜单
- [ ] 普通用户能看到知识库相关菜单

## 注意事项

1. **动态路由**：本系统使用后端动态路由，菜单由数据库配置决定
2. **隐藏菜单**：设置 `visible=0` 的菜单不会在侧边栏显示，但仍可通过路由访问
3. **权限控制**：新菜单已分配给所有角色，如需调整请修改 `sys_role_menu` 表
4. **组件映射**：前端 `componentMap` 中的键名必须与数据库中 `component` 字段值一致
5. **路由懒加载**：所有页面组件使用懒加载，提升首屏加载速度

## 菜单字段说明

| 字段 | 说明 | 示例值 |
|------|------|--------|
| menu_name | 菜单显示名称 | 知识库管理 |
| menu_code | 菜单唯一编码 | knowledge |
| parent_id | 父菜单ID，0表示顶级菜单 | 13 |
| path | 路由路径 | /knowledge/list |
| component | 组件路径（对应componentMap的键） | knowledge/index |
| icon | 图标类名（Carbon Design图标） | carbon-knowledge-app |
| menu_type | 1-目录，2-菜单，3-按钮 | 1 |
| visible | 0-隐藏，1-显示 | 1 |
| status | 0-禁用，1-启用 | 1 |
| sort | 排序值，越小越靠前 | 2 |

## 图标资源

使用的图标来自 Carbon Design Icons，通过 UnoCSS 配置：

- `carbon-knowledge-app` - 知识库图标
- `carbon-document` - 文档图标
- `carbon-upload` - 上传图标
- `carbon-chat` - 聊天图标

如需添加新图标，请参考 [Carbon Design Icons](https://carbon-elements.netlify.app/icons/examples/preview/)