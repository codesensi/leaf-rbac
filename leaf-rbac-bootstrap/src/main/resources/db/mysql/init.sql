SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`          bigint       NOT NULL COMMENT '用户ID',
    `username`    varchar(128) NOT NULL COMMENT '用户名称',
    `password`    varchar(512) NOT NULL COMMENT '用户密码',
    `nickname`    varchar(64) NULL DEFAULT NULL COMMENT '用户昵称',
    `id_no`       varchar(64) NULL DEFAULT NULL COMMENT '用户身份证号码',
    `email`       varchar(64) NULL DEFAULT NULL COMMENT '用户邮箱',
    `phone`       varchar(11) NULL DEFAULT NULL COMMENT '用户手机号码',
    `gender`      tinyint(1)   NULL DEFAULT 0 COMMENT '用户性别:0-保密,1-男,2-女',
    `avatar`      varchar(512) NULL DEFAULT NULL COMMENT '用户头像地址',
    `type`        tinyint(1)   NULL DEFAULT 0 COMMENT '用户类型:0-系统用户',
    `status`      tinyint(1)   NULL DEFAULT 0 COMMENT '用户状态:0-启用,1-禁用',
    `remark`      varchar(512) NULL DEFAULT NULL COMMENT '备注',
    `sys_flag`    tinyint(1)    NULL DEFAULT 0 COMMENT '系统内置标识:0-否,1-是',
    `creator`     bigint NULL DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     bigint NULL DEFAULT NULL COMMENT '更新人',
    `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`    tinyint(1)   NULL DEFAULT 0 COMMENT '逻辑删除标识:0-未删除,1-已删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `idx_username` (`username` ASC) USING BTREE,
    INDEX         `idx_status` (`status` ASC) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '用户信息表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `id_no`, `email`, `phone`, `gender`, `avatar`, `status`, `remark`, `sys_flag`, `creator`, `updater`, `del_flag`)
    VALUES (1, 'sadmin', '$2a$10$U.k0b43Pwg./Jg2QQl4bMOukItbYg4aYhKsciMamtHWvp3JEF2ism', '超级管理员', '110101200001010001', 'sadmin@leaf.com', '18900000000', 0, 'https://api.dicebear.com/7.x/bottts/svg?seed=sadmin', 0, '超级管理员', 1, NULL,NULL, 0);


-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `id`          bigint      NOT NULL COMMENT '角色ID',
    `name`        varchar(64) NOT NULL COMMENT '角色名称',
    `code`        varchar(64) NOT NULL COMMENT '角色编码',
    `pid`         bigint NULL DEFAULT 0 COMMENT '父角色ID',
    `sort`        int NULL DEFAULT 0 COMMENT '角色排序',
    `status`      tinyint(1)   NULL DEFAULT 0 COMMENT '角色状态:0-启用,1-禁用',
    `remark`      varchar(512) NULL DEFAULT NULL COMMENT '备注',
    `sys_flag`    tinyint(1)    NULL DEFAULT 0 COMMENT '系统内置标识:0-自定义,1-内置',
    `creator`     bigint NULL DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     bigint NULL DEFAULT NULL COMMENT '更新人',
    `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`    tinyint(1)   NULL DEFAULT 0 COMMENT '逻辑删除标识:0-未删除,1-已删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `idx_code` (`code` ASC) USING BTREE,
    INDEX         `idx_status` (`status` ASC) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '角色信息表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` (`id`, `name`, `code`, `pid`, `sort`, `status`, `remark`, `sys_flag`, `creator`, `updater`, `del_flag`)
    VALUES (1, '超级管理员', 'sadmin', 0, 0, 0, '超级管理员角色', 1, NULL, NULL, 0);


-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`
(
    `id`          bigint NOT NULL COMMENT '主键ID',
    `user_id`     bigint NOT NULL COMMENT '用户ID',
    `role_id`     bigint NOT NULL COMMENT '角色ID',
    `creator`     bigint NULL DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     bigint NULL DEFAULT NULL COMMENT '更新人',
    `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`    tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除标识:0-未删除,1-已删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `idx_user_id` (`user_id` ASC) USING BTREE,
    INDEX         `idx_role_id` (`role_id` ASC) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '用户角色关联表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`, `creator`, `updater`, `del_flag`)
    VALUES (1, 1, 1, NULL, NULL, 0);


-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`
(
    `id`             bigint NOT NULL COMMENT '路由菜单ID',
    `pid`            bigint NULL DEFAULT 0 COMMENT '父级路由菜单ID',
    `name`           varchar(2048) NULL DEFAULT NULL COMMENT '路由名称(外链地址)',
    `path`           varchar(512) NULL DEFAULT NULL COMMENT '路由路径',
    `param`          varchar(256) NULL DEFAULT NULL COMMENT '路由参数',
    `component`      varchar(256) NULL DEFAULT NULL COMMENT '组件路径',
    `title`          varchar(256) NULL DEFAULT NULL COMMENT '菜单名称',
    `type`           tinyint(1)    NULL DEFAULT NULL COMMENT '菜单类型:1-目录,2-菜单,3-按钮',
    `sort`           int NULL DEFAULT 0 COMMENT '菜单排序:数字越小越靠前',
    `icon`           varchar(256) NULL DEFAULT NULL COMMENT '菜单图标',
    `perms`          varchar(64) NULL DEFAULT NULL COMMENT '权限编码',
    `is_link`        tinyint(1)    NULL DEFAULT 0 COMMENT '是否外链:0-否,1-是',
    `is_frame`       tinyint(1)    NULL DEFAULT 0 COMMENT '是否内嵌iframe:0-否,1-是',
    `frame_src`      varchar(2048) NULL DEFAULT NULL COMMENT '内嵌iframe地址',
    `is_show`        tinyint(1)    NULL DEFAULT 1 COMMENT '是否显示:0-否,1-是',
    `is_show_parent` tinyint(1)    NULL DEFAULT 1 COMMENT '是否显示父级菜单:0-否,1-是',
    `status`         tinyint(1)    NULL DEFAULT 0 COMMENT '菜单状态:0-启用,1-禁用',
    `remark`         varchar(512) NULL DEFAULT NULL COMMENT '备注',
    `sys_flag`       tinyint(1)    NULL DEFAULT 0 COMMENT '系统内置标识:0-自定义,1-内置',
    `creator`        bigint NULL DEFAULT NULL COMMENT '创建人',
    `create_time`    datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`        bigint NULL DEFAULT NULL COMMENT '更新人',
    `update_time`    datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`       tinyint(1)    NULL DEFAULT 0 COMMENT '逻辑删除标识:0-未删除,1-已删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX            `idx_status` (`status` ASC) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '路由菜单表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` (`id`, `pid`, `name`, `path`, `param`, `component`, `title`, `type`, `sort`, `icon`, `perms`, `is_link`, `is_frame`, `frame_src`, `is_show`, `is_show_parent`, `status`, `remark`, `sys_flag`, `creator`, `updater`, `del_flag`)
    VALUES (1001, 0, NULL, '/dashboard', NULL, NULL, '工作台', 1, 1, 'ri:dashboard', NULL, 0, 0, NULL, 1, 1, 0, '工作台目录', 1, NULL, NULL, 0);
INSERT INTO `sys_menu` (`id`, `pid`, `name`, `path`, `param`, `component`, `title`, `type`, `sort`, `icon`, `perms`, `is_link`, `is_frame`, `frame_src`, `is_show`, `is_show_parent`, `status`, `remark`, `sys_flag`, `creator`, `updater`, `del_flag`)
    VALUES (1002, 0, NULL, '/system', NULL, NULL, '系统管理', 1, 2, 'ri:setting', NULL, 0, 0, NULL, 1, 1, 0, '系统管理目录', 1, NULL, NULL, 0);
INSERT INTO `sys_menu` (`id`, `pid`, `name`, `path`, `param`, `component`, `title`, `type`, `sort`, `icon`, `perms`, `is_link`, `is_frame`, `frame_src`, `is_show`, `is_show_parent`, `status`, `remark`, `sys_flag`, `creator`, `updater`, `del_flag`)
    VALUES (10020001, 1002, 'SystemUser', '/system/user/index', NULL, NULL, '用户管理', 2, 1, 'ri:user', NULL, 0, 0, NULL, 1, 1, 0, '用户管理菜单', 1, NULL, NULL, 0);
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
    VALUES (10020002, 1002, 'SystemRole', '/system/role/index', NULL, NULL, '角色管理', 2, 2, 'ri:role', NULL, 0, 0, NULL, 1, 1, 0, '角色管理菜单', 1, NULL, NULL, 0);
INSERT INTO `sys_menu` (`id`, `pid`, `name`, `path`, `param`, `component`, `title`, `type`, `sort`, `icon`, `perms`, `is_link`, `is_frame`, `frame_src`, `is_show`, `is_show_parent`, `status`, `remark`, `sys_flag`, `creator`, `updater`, `del_flag`)
    VALUES (10020003, 1002, 'SystemMenu', '/system/menu/index', NULL, NULL, '菜单管理', 2, 3, 'ri:menu', NULL, 0, 0, NULL, 1, 1, 0, '菜单管理菜单', 1, NULL, NULL, 0);


-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`
(
    `id`          bigint NOT NULL COMMENT '主键ID',
    `role_id`     bigint NOT NULL COMMENT '角色ID',
    `menu_id`     bigint NOT NULL COMMENT '菜单ID',
    `creator`     bigint NULL DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     bigint NULL DEFAULT NULL COMMENT '更新人',
    `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`    tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除标识:0-未删除,1-已删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `idx_role_id` (`role_id` ASC) USING BTREE,
    INDEX         `idx_menu_id` (`menu_id` ASC) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '角色菜单关联表'
  ROW_FORMAT = DYNAMIC;


-- Table structure for log_login
-- ----------------------------
DROP TABLE IF EXISTS `log_login`;
CREATE TABLE `log_login`
(
    `id`              bigint NOT NULL COMMENT '日志ID',
    `login_type`      tinyint(1)    NULL DEFAULT NULL COMMENT '登录方式:0-未知,1-账号密码,2-手机号',
    `event_type`      tinyint(1)    NULL DEFAULT NULL COMMENT '事件类型:0-未知,1-登录,2-登出',
    `login_key`       varchar(64) NULL DEFAULT NULL COMMENT '登录标识:账号/手机号',
    `user_id`         bigint NULL DEFAULT NULL COMMENT '登录人ID',
    `username`        varchar(128) NULL DEFAULT NULL COMMENT '登录人账号',
    `status`          tinyint(1)    NULL DEFAULT NULL COMMENT '登录状态:0-失败,1-成功',
    `error_msg`       text NULL DEFAULT NULL COMMENT '登录失败原因',
    `request_ip`      varchar(256) NULL DEFAULT NULL COMMENT '请求来源IP地址',
    `request_area`    varchar(256) NULL DEFAULT NULL COMMENT '登录地区',
    `request_os`      varchar(256) NULL DEFAULT NULL COMMENT '登录系统',
    `request_device`  varchar(64) NULL DEFAULT NULL COMMENT '登录设备',
    `request_browser` varchar(64) NULL DEFAULT NULL COMMENT '登录浏览器',
    `duration_ms`     bigint NULL DEFAULT NULL COMMENT '请求执行耗时:单位毫秒',
    `params`          text NULL DEFAULT NULL COMMENT '请求参数',
    `creator`         bigint NULL DEFAULT NULL COMMENT '创建人',
    `create_time`     datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`         bigint NULL DEFAULT NULL COMMENT '更新人',
    `update_time`     datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`        tinyint(1)    NULL DEFAULT 0 COMMENT '是否删除:0-否,1-是',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '登录日志表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for log_operate
-- ----------------------------
DROP TABLE IF EXISTS `log_operate`;
CREATE TABLE `log_operate`
(
    `id`              bigint NOT NULL COMMENT '日志ID',
    `module`          varchar(256) NULL DEFAULT NULL COMMENT '操作所属模块',
    `type`            tinyint(1)    NULL DEFAULT NULL COMMENT '操作类型:0-未知,1-新增,2-更新,3-查询,4-删除',
    `user_id`         bigint NULL DEFAULT NULL COMMENT '操作人ID',
    `descr`           varchar(256) NULL DEFAULT NULL COMMENT '操作描述',
    `status`          tinyint(1)    NULL DEFAULT NULL COMMENT '操作状态:0-失败,1-成功',
    `error_msg`       text NULL DEFAULT NULL COMMENT '错误信息',
    `request_ip`      varchar(256) NULL DEFAULT NULL COMMENT '请求IP',
    `request_url`     varchar(512) NULL DEFAULT NULL COMMENT '请求的URL地址',
    `request_area`    varchar(256) NULL DEFAULT NULL COMMENT '请求地区',
    `request_os`      varchar(64) NULL DEFAULT NULL COMMENT '请求系统',
    `request_device`  varchar(64) NULL DEFAULT NULL COMMENT '请求设备',
    `request_browser` varchar(64) NULL DEFAULT NULL COMMENT '请求浏览器',
    `method_name`     varchar(1024) NULL DEFAULT NULL COMMENT '被调用方法的全限定名（包名.类名.方法名）',
    `duration_ms`     bigint NULL DEFAULT NULL COMMENT '请求执行耗时:单位毫秒',
    `params`          text NULL DEFAULT NULL COMMENT '请求参数',
    `result`          text NULL DEFAULT NULL COMMENT '响应结果',
    `creator`         bigint NULL DEFAULT NULL COMMENT '创建人',
    `create_time`     datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`         bigint NULL DEFAULT NULL COMMENT '更新人',
    `update_time`     datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`        tinyint(1)    NULL DEFAULT 0 COMMENT '逻辑删除标识:0-未删除,1-已删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '操作日志表'
  ROW_FORMAT = DYNAMIC;


SET FOREIGN_KEY_CHECKS = 1;
