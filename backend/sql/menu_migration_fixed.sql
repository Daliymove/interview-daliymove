-- 菜单数据迁移 - 添加知识库模块和子页面（修复版）
-- 执行此脚本前请确保已备份数据库
-- 修复：使用变量动态获取父级菜单ID

USE enterprise_admin;

-- 获取简历管理菜单ID
SET @resume_parent_id = (SELECT id FROM sys_menu WHERE menu_code = 'resume' LIMIT 1);
-- 获取面试助手菜单ID
SET @interview_parent_id = (SELECT id FROM sys_menu WHERE menu_code = 'interview' LIMIT 1);

-- 知识库管理菜单（父级目录）
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `path`, `component`, `icon`, `menu_type`, `visible`, `status`, `sort`, `create_time`, `update_time`, `deleted`)
VALUES (13, '知识库管理', 'knowledge', 0, '/knowledge', 'Layout', 'carbon-folder', 1, 1, 1, 2, NOW(), NOW(), 0);

-- 知识库管理子菜单
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `path`, `component`, `icon`, `menu_type`, `visible`, `status`, `sort`, `create_time`, `update_time`, `deleted`)
VALUES (14, '知识库列表', 'knowledge-list', 13, '/knowledge/list', 'knowledge/index', 'carbon-document', 2, 1, 1, 1, NOW(), NOW(), 0);

INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `path`, `component`, `icon`, `menu_type`, `visible`, `status`, `sort`, `create_time`, `update_time`, `deleted`)
VALUES (15, '文档上传', 'knowledge-upload', 13, '/knowledge/upload', 'knowledge/Upload', 'carbon-upload', 2, 1, 1, 2, NOW(), NOW(), 0);

INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `path`, `component`, `icon`, `menu_type`, `visible`, `status`, `sort`, `create_time`, `update_time`, `deleted`)
VALUES (16, '知识问答', 'knowledge-query', 13, '/knowledge/query', 'knowledge/Query', 'carbon-chat', 2, 1, 1, 3, NOW(), NOW(), 0);

-- 简历上传页面（隐藏菜单，不在菜单中显示）
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `path`, `component`, `icon`, `menu_type`, `visible`, `status`, `sort`, `create_time`, `update_time`, `deleted`)
VALUES (17, '简历上传', 'resume-upload', @resume_parent_id, '/resume/upload', 'resume/Upload', NULL, 2, 0, 1, 1, NOW(), NOW(), 0);

-- 简历详情页面（隐藏菜单，不在菜单中显示）
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `path`, `component`, `icon`, `menu_type`, `visible`, `status`, `sort`, `create_time`, `update_time`, `deleted`)
VALUES (18, '简历详情', 'resume-detail', @resume_parent_id, '/resume/:id', 'resume/Detail', NULL, 2, 0, 1, 2, NOW(), NOW(), 0);

-- 面试会话页面（隐藏菜单，不在菜单中显示）
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `path`, `component`, `icon`, `menu_type`, `visible`, `status`, `sort`, `create_time`, `update_time`, `deleted`)
VALUES (19, '面试会话', 'interview-session', @interview_parent_id, '/interview/session/:id', 'interview/Session', NULL, 2, 0, 1, 1, NOW(), NOW(), 0);

-- 为超级管理员角色分配新菜单
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_time`)
VALUES 
(1, 13, NOW()),
(1, 14, NOW()),
(1, 15, NOW()),
(1, 16, NOW()),
(1, 17, NOW()),
(1, 18, NOW()),
(1, 19, NOW());

-- 为管理员角色分配新菜单
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_time`)
VALUES 
(2, 13, NOW()),
(2, 14, NOW()),
(2, 15, NOW()),
(2, 16, NOW()),
(2, 17, NOW()),
(2, 18, NOW()),
(2, 19, NOW());

-- 为普通用户角色分配新菜单（知识库模块）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `create_time`)
VALUES 
(3, 13, NOW()),
(3, 14, NOW()),
(3, 15, NOW()),
(3, 16, NOW()),
(3, 17, NOW()),
(3, 18, NOW()),
(3, 19, NOW());

-- 验证插入结果
SELECT '菜单插入结果:' AS '';
SELECT id, menu_name, menu_code, parent_id, path, component, menu_type, visible 
FROM sys_menu 
WHERE id >= 13 
ORDER BY id;

SELECT '角色菜单关联结果:' AS '';
SELECT rm.role_id, r.role_name, rm.menu_id, m.menu_name 
FROM sys_role_menu rm
JOIN sys_role r ON rm.role_id = r.id
JOIN sys_menu m ON rm.menu_id = m.id
WHERE rm.menu_id >= 13
ORDER BY rm.role_id, rm.menu_id;