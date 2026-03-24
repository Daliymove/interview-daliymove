-- 企业管理后台 - RBAC权限模型数据库设计
-- 支持数据库：MySQL 8.0+

-- 创建数据库
CREATE DATABASE IF NOT EXISTS enterprise_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE enterprise_admin;

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

 Date: 24/03/2026 15:18:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '仪表盘', 'dashboard', 0, '/dashboard', 'dashboard/index', 'carbon-dashboard', 2, 1, 1, 0, '2026-03-24 06:25:16', '2026-03-24 06:25:16', 0);
INSERT INTO `sys_menu` VALUES (2, '简历管理', 'resume', 0, '/resume', 'resume/index', 'carbon-document', 2, 1, 1, 1, '2026-03-24 06:25:16', '2026-03-24 06:25:16', 0);
INSERT INTO `sys_menu` VALUES (3, '个人助手', 'assistant', 0, '/assistant', 'assistant/index', 'carbon-user-avatar', 2, 1, 1, 2, '2026-03-24 06:25:16', '2026-03-24 06:25:16', 0);
INSERT INTO `sys_menu` VALUES (4, '面试助手', 'interview', 0, '/interview', 'interview/index', 'carbon-chat-bot', 2, 1, 1, 3, '2026-03-24 06:25:16', '2026-03-24 06:25:16', 0);
INSERT INTO `sys_menu` VALUES (5, '系统管理', 'system', 0, '/system', 'Layout', 'carbon-settings', 1, 1, 1, 4, '2026-03-24 06:25:16', '2026-03-24 06:25:16', 0);
INSERT INTO `sys_menu` VALUES (6, '用户管理', 'user', 5, '/system/user', 'system/user/index', 'carbon-user-multiple', 2, 1, 1, 1, '2026-03-24 06:25:16', '2026-03-24 06:25:16', 0);
INSERT INTO `sys_menu` VALUES (7, '角色管理', 'role', 5, '/system/role', 'system/role/index', 'carbon-user-role', 2, 1, 1, 2, '2026-03-24 06:25:16', '2026-03-24 06:25:16', 0);
INSERT INTO `sys_menu` VALUES (8, '权限管理', 'permission', 5, '/system/permission', 'system/permission/index', 'carbon-locked', 2, 1, 1, 3, '2026-03-24 06:25:16', '2026-03-24 06:25:16', 0);
INSERT INTO `sys_menu` VALUES (9, '菜单管理', 'menu', 5, '/system/menu', 'system/menu/index', 'carbon-menu', 2, 1, 1, 4, '2026-03-24 06:25:16', '2026-03-24 06:25:16', 0);
INSERT INTO `sys_menu` VALUES (10, '部门管理', 'dept', 5, '/system/dept', 'system/dept/index', 'carbon-building', 2, 1, 1, 5, '2026-03-24 06:25:16', '2026-03-24 06:25:16', 0);
INSERT INTO `sys_menu` VALUES (11, '日志管理', 'log', 0, '/log', 'Layout', 'carbon-document', 1, 1, 1, 5, '2026-03-24 06:25:16', '2026-03-24 06:25:16', 0);
INSERT INTO `sys_menu` VALUES (12, '操作日志', 'operation-log', 11, '/log/operation', 'log/operation/index', 'carbon-activity', 2, 1, 1, 1, '2026-03-24 06:25:16', '2026-03-24 06:25:16', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色菜单关联表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色权限关联表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin@example.com', '13800138000', NULL, 1, 1, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_user` VALUES (2, 'tech_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '技术用户', 'tech@example.com', '13800138001', NULL, 1, 2, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_user` VALUES (3, 'product_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '产品用户', 'product@example.com', '13800138002', NULL, 1, 3, '2026-03-17 06:42:50', '2026-03-17 06:42:50', 0);
INSERT INTO `sys_user` VALUES (4, 'user-test', '$2a$10$/bdEpUXBwVJM8wON36QGPOw5cse9zFpPK1gz6EW6Jrdukz9QnOz5C', 'daliymove', 'www.daliymove@123.com', '13312231223', NULL, 1, NULL, '2026-03-18 10:42:44', '2026-03-18 10:42:44', 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (2, 2, 2, '2026-03-17 06:42:50');
INSERT INTO `sys_user_role` VALUES (3, 3, 3, '2026-03-17 06:42:50');
INSERT INTO `sys_user_role` VALUES (4, 4, 3, '2026-03-18 10:44:56');
INSERT INTO `sys_user_role` VALUES (5, 1, 1, '2026-03-24 15:10:55');

SET FOREIGN_KEY_CHECKS = 1;
