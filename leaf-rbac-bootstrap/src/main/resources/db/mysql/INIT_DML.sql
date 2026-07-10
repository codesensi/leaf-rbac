SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `id_no`, `email`, `phone`, `gender`, `avatar`, `status`, `remark`, `sys_flag`, `creator`, `updater`, `del_flag`)
    VALUES (1, 'sadmin', '$2a$10$U.k0b43Pwg./Jg2QQl4bMOukItbYg4aYhKsciMamtHWvp3JEF2ism', '超级管理员', '110101200001010001', 'sadmin@leaf.com', '18900000000', 0, 'https://api.dicebear.com/7.x/bottts/svg?seed=sadmin', 0, '超级管理员', 1, NULL,NULL, 0);


-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` (`id`, `name`, `code`, `pid`, `sort`, `status`, `remark`, `sys_flag`, `creator`, `updater`, `del_flag`)
    VALUES (1, '超级管理员', 'sadmin', 0, 0, 0, '超级管理员角色', 1, NULL, NULL, 0);


-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`, `creator`, `updater`, `del_flag`)
    VALUES (1, 1, 1, NULL, NULL, 0);


-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` (`id`, `pid`, `name`, `path`, `param`, `component`, `title`, `type`, `sort`, `icon`, `perms`, `is_link`, `is_frame`, `frame_src`, `is_show`, `is_show_parent`, `status`, `remark`, `sys_flag`, `creator`, `updater`, `del_flag`)
    VALUES (1001, 0, NULL, '/dashboard', NULL, NULL, '工作台', 1, 0, 'ri:dashboard', NULL, 0, 0, NULL, 1, 1, 0, '工作台目录', 1, NULL, NULL, 0);
INSERT INTO `sys_menu` (`id`, `pid`, `name`, `path`, `param`, `component`, `title`, `type`, `sort`, `icon`, `perms`, `is_link`, `is_frame`, `frame_src`, `is_show`, `is_show_parent`, `status`, `remark`, `sys_flag`, `creator`, `updater`, `del_flag`)
    VALUES (1002, 0, NULL, '/system', NULL, NULL, '系统管理', 1, 1, 'ri:setting', NULL, 0, 0, NULL, 1, 1, 0, '系统管理目录', 1, NULL, NULL, 0);
INSERT INTO `sys_menu` (`id`, `pid`, `name`, `path`, `param`, `component`, `title`, `type`, `sort`, `icon`, `perms`, `is_link`, `is_frame`, `frame_src`, `is_show`, `is_show_parent`, `status`, `remark`, `sys_flag`, `creator`, `updater`, `del_flag`)
    VALUES (10020001, 1002, 'SystemUser', '/system/user/index', NULL, NULL, '用户管理', 2, 11, 'ri:user', NULL, 0, 0, NULL, 1, 1, 0, '用户管理菜单', 1, NULL, NULL, 0);
INSERT INTO `sys_menu` (`id`, `pid`, `name`, `path`, `param`, `component`, `title`, `type`, `sort`, `icon`, `perms`, `is_link`, `is_frame`, `frame_src`, `is_show`, `is_show_parent`, `status`, `remark`, `sys_flag`, `creator`, `updater`, `del_flag`)
    VALUES (100200010001, 10020001, NULL, NULL, NULL, NULL, '查询用户分页列表', 3, 0, NULL, 'sys:user:page', 0, 0, NULL, 1, 1, 0, '查询用户分页列表', 1, NULL, NULL, 0);
INSERT INTO `sys_menu` (`id`, `pid`, `name`, `path`, `param`, `component`, `title`, `type`, `sort`, `icon`, `perms`, `is_link`, `is_frame`, `frame_src`, `is_show`, `is_show_parent`, `status`, `remark`, `sys_flag`, `creator`, `updater`, `del_flag`)
    VALUES (100200010002, 10020001, NULL, NULL, NULL, NULL, '保存用户信息', 3, 0, NULL, 'sys:user:save', 0, 0, NULL, 1, 1, 0, '新增用户', 1, NULL, NULL, 0);
INSERT INTO `sys_menu` (`id`, `pid`, `name`, `path`, `param`, `component`, `title`, `type`, `sort`, `icon`, `perms`, `is_link`, `is_frame`, `frame_src`, `is_show`, `is_show_parent`, `status`, `remark`, `sys_flag`, `creator`, `updater`, `del_flag`)
    VALUES (100200010003, 10020001, NULL, NULL, NULL, NULL, '更新用户信息', 3, 0, NULL, 'sys:user:update', 0, 0, NULL, 1, 1, 0, '更新用户信息', 1, NULL, NULL, 0);
INSERT INTO `sys_menu` (`id`, `pid`, `name`, `path`, `param`, `component`, `title`, `type`, `sort`, `icon`, `perms`, `is_link`, `is_frame`, `frame_src`, `is_show`, `is_show_parent`, `status`, `remark`, `sys_flag`, `creator`, `updater`, `del_flag`)
    VALUES (100200010004, 10020001, NULL, NULL, NULL, NULL, '获取用户详情', 3, 0, NULL, 'sys:user:detail', 0, 0, NULL, 1, 1, 0, '获取用户详情', 1, NULL, NULL, 0);
INSERT INTO `sys_menu` (`id`, `pid`, `name`, `path`, `param`, `component`, `title`, `type`, `sort`, `icon`, `perms`, `is_link`, `is_frame`, `frame_src`, `is_show`, `is_show_parent`, `status`, `remark`, `sys_flag`, `creator`, `updater`, `del_flag`)
    VALUES (100200010005, 10020001, NULL, NULL, NULL, NULL, '删除用户信息', 3, 0, NULL, 'sys:user:delete', 0, 0, NULL, 1, 1, 0, '删除用户信息', 1, NULL, NULL, 0);
INSERT INTO `sys_menu` (`id`, `pid`, `name`, `path`, `param`, `component`, `title`, `type`, `sort`, `icon`, `perms`, `is_link`, `is_frame`, `frame_src`, `is_show`, `is_show_parent`, `status`, `remark`, `sys_flag`, `creator`, `updater`, `del_flag`)
    VALUES (10020002, 1002, 'SystemRole', '/system/role/index', NULL, NULL, '角色管理', 2, 12, 'ri:role', NULL, 0, 0, NULL, 1, 1, 0, '角色管理菜单', 1, NULL, NULL, 0);
INSERT INTO `sys_menu` (`id`, `pid`, `name`, `path`, `param`, `component`, `title`, `type`, `sort`, `icon`, `perms`, `is_link`, `is_frame`, `frame_src`, `is_show`, `is_show_parent`, `status`, `remark`, `sys_flag`, `creator`, `updater`, `del_flag`)
    VALUES (10020003, 1002, 'SystemMenu', '/system/menu/index', NULL, NULL, '菜单管理', 2, 13, 'ri:menu', NULL, 0, 0, NULL, 1, 1, 0, '菜单管理菜单', 1, NULL, NULL, 0);


SET FOREIGN_KEY_CHECKS = 1;
