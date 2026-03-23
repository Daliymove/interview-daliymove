-- 企业管理后台 - RBAC权限模型数据库设计
-- 支持数据库：MySQL 8.0+

-- 创建数据库
CREATE DATABASE IF NOT EXISTS enterprise_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE enterprise_admin;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- 2. 角色表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ----------------------------
-- 3. 权限表
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `permission_name` varchar(50) NOT NULL COMMENT '权限名称',
  `permission_code` varchar(100) NOT NULL COMMENT '权限编码',
  `resource_type` tinyint DEFAULT 1 COMMENT '资源类型：1-菜单，2-按钮，3-接口',
  `resource_url` varchar(255) DEFAULT NULL COMMENT '资源路径',
  `method` varchar(10) DEFAULT NULL COMMENT 'HTTP方法：GET/POST/PUT/DELETE',
  `parent_id` bigint DEFAULT 0 COMMENT '父级ID',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `sort` int DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- ----------------------------
-- 4. 菜单表
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `menu_code` varchar(50) NOT NULL COMMENT '菜单编码',
  `parent_id` bigint DEFAULT 0 COMMENT '父级菜单ID',
  `path` varchar(255) DEFAULT NULL COMMENT '路由路径',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标',
  `menu_type` tinyint DEFAULT 1 COMMENT '类型：1-目录，2-菜单，3-按钮',
  `visible` tinyint DEFAULT 1 COMMENT '是否可见：0-隐藏，1-显示',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `sort` int DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_menu_code` (`menu_code`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

-- ----------------------------
-- 5. 用户-角色关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ----------------------------
-- 6. 角色-权限关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- ----------------------------
-- 7. 角色-菜单关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

-- ----------------------------
-- 8. 部门表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `dept_name` varchar(50) NOT NULL COMMENT '部门名称',
  `parent_id` bigint DEFAULT 0 COMMENT '父级部门ID',
  `leader` varchar(50) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `sort` int DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- ----------------------------
-- 9. 操作日志表
-- ----------------------------
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `operation` varchar(100) DEFAULT NULL COMMENT '操作描述',
  `method` varchar(255) DEFAULT NULL COMMENT '方法名称',
  `params` text COMMENT '请求参数',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-失败，1-成功',
  `error_msg` text COMMENT '错误信息',
  `execute_time` int DEFAULT NULL COMMENT '执行时间(毫秒)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ----------------------------
-- 初始化数据
-- ----------------------------

-- 初始化部门
INSERT INTO `sys_dept` (`id`, `dept_name`, `parent_id`, `leader`, `status`, `sort`) VALUES
(1, '总公司', 0, 'admin', 1, 1),
(2, '技术部', 1, 'tech_leader', 1, 2),
(3, '产品部', 1, 'product_leader', 1, 3),
(4, '运营部', 1, 'operation_leader', 1, 4);

-- 初始化用户 (密码均为: 123456，使用BCrypt加密)
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `email`, `phone`, `status`, `dept_id`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin@example.com', '13800138000', 1, 1),
(2, 'tech_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '技术用户', 'tech@example.com', '13800138001', 1, 2),
(3, 'product_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '产品用户', 'product@example.com', '13800138002', 1, 3);

-- 初始化角色
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `description`, `status`) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '拥有所有权限', 1),
(2, '管理员', 'ADMIN', '管理用户和角色', 1),
(3, '普通用户', 'USER', '普通用户权限', 1);

-- 初始化用户角色关联
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1),
(2, 2),
(3, 3);

-- 初始化菜单
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `path`, `component`, `icon`, `menu_type`, `visible`, `status`, `sort`) VALUES
(1, '系统管理', 'system', 0, '/system', 'Layout', 'SettingOutlined', 1, 1, 1, 1),
(2, '用户管理', 'user', 1, '/system/user', 'system/user/index', 'UserOutlined', 2, 1, 1, 1),
(3, '角色管理', 'role', 1, '/system/role', 'system/role/index', 'TeamOutlined', 2, 1, 1, 2),
(4, '权限管理', 'permission', 1, '/system/permission', 'system/permission/index', 'LockOutlined', 2, 1, 1, 3),
(5, '菜单管理', 'menu', 1, '/system/menu', 'system/menu/index', 'MenuOutlined', 2, 1, 1, 4),
(6, '部门管理', 'dept', 1, '/system/dept', 'system/dept/index', 'ApartmentOutlined', 2, 1, 1, 5),
(7, '日志管理', 'log', 0, '/log', 'Layout', 'FileTextOutlined', 1, 1, 1, 2),
(8, '操作日志', 'operation-log', 7, '/log/operation', 'log/operation/index', 'ProfileOutlined', 2, 1, 1, 1),
(9, '用户新增', 'user-add', 2, NULL, NULL, NULL, 3, 1, 1, 1),
(10, '用户编辑', 'user-edit', 2, NULL, NULL, NULL, 3, 1, 1, 2),
(11, '用户删除', 'user-delete', 2, NULL, NULL, NULL, 3, 1, 1, 3);

-- 初始化权限
INSERT INTO `sys_permission` (`id`, `permission_name`, `permission_code`, `resource_type`, `resource_url`, `method`, `parent_id`, `status`, `sort`) VALUES
(1, '用户管理', 'system:user', 1, '/system/user', 'GET', 0, 1, 1),
(2, '用户新增', 'system:user:add', 2, '/api/user', 'POST', 1, 1, 1),
(3, '用户编辑', 'system:user:edit', 2, '/api/user', 'PUT', 1, 1, 2),
(4, '用户删除', 'system:user:delete', 2, '/api/user/*', 'DELETE', 1, 1, 3),
(5, '用户查询', 'system:user:query', 2, '/api/user/*', 'GET', 1, 1, 4),
(6, '角色管理', 'system:role', 1, '/system/role', 'GET', 0, 1, 2),
(7, '角色新增', 'system:role:add', 2, '/api/role', 'POST', 6, 1, 1),
(8, '角色编辑', 'system:role:edit', 2, '/api/role', 'PUT', 6, 1, 2),
(9, '角色删除', 'system:role:delete', 2, '/api/role/*', 'DELETE', 6, 1, 3),
(10, '角色查询', 'system:role:query', 2, '/api/role/*', 'GET', 6, 1, 4),
(11, '菜单管理', 'system:menu', 1, '/system/menu', 'GET', 0, 1, 3),
(12, '菜单新增', 'system:menu:add', 2, '/api/menu', 'POST', 11, 1, 1),
(13, '菜单编辑', 'system:menu:edit', 2, '/api/menu', 'PUT', 11, 1, 2),
(14, '菜单删除', 'system:menu:delete', 2, '/api/menu/*', 'DELETE', 11, 1, 3);

-- 初始化角色菜单关联 (超级管理员拥有所有菜单)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10), (1, 11),
(2, 1), (2, 2), (2, 3), (2, 5), (2, 9), (2, 10),
(3, 1), (3, 2);

-- 初始化角色权限关联 (超级管理员拥有所有权限)
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10), (1, 11), (1, 12), (1, 13), (1, 14),
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 10),
(3, 1), (3, 5);