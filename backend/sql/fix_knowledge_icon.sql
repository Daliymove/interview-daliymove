-- 快速修复：更新知识库管理菜单图标
-- 如果已经执行过menu_migration.sql，可以直接运行此脚本更新图标

USE enterprise_admin;

-- 更新知识库管理菜单图标为 folder
UPDATE `sys_menu` 
SET `icon` = 'carbon-folder' 
WHERE `menu_code` = 'knowledge';

-- 验证更新结果
SELECT id, menu_name, menu_code, icon 
FROM sys_menu 
WHERE menu_code = 'knowledge';