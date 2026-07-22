SET NAMES utf8mb4;


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
    `id_card`     varchar(64) NULL DEFAULT NULL COMMENT '用户身份证号码',
    `email`       varchar(64) NULL DEFAULT NULL COMMENT '用户邮箱',
    `phone`       varchar(11) NULL DEFAULT NULL COMMENT '用户手机号码',
    `gender`      varchar(1) NULL DEFAULT 'U' COMMENT '用户性别:U-未知,M-男,F-女',
    `avatar`      varchar(512) NULL DEFAULT NULL COMMENT '用户头像地址',
    `status`      tinyint(1)   NULL DEFAULT 0 COMMENT '用户状态:0-启用,1-禁用',
    `remark`      varchar(512) NULL DEFAULT NULL COMMENT '备注',
    `sys_flag`    tinyint(1)    NULL DEFAULT 0 COMMENT '系统内置标识:0-非内置,1-内置',
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
    `sys_flag`    tinyint(1)    NULL DEFAULT 0 COMMENT '系统内置标识:0-非内置,1-内置',
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
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`
(
    `id`             bigint NOT NULL COMMENT '路由菜单ID',
    `pid`            bigint NULL DEFAULT 0 COMMENT '父级路由菜单ID',
    `name`           varchar(2048) NULL DEFAULT NULL COMMENT '路由名称/外链地址',
    `path`           varchar(512) NULL DEFAULT NULL COMMENT '路由路径',
    `param`          varchar(256) NULL DEFAULT NULL COMMENT '路由参数',
    `component`      varchar(256) NULL DEFAULT NULL COMMENT '组件路径',
    `title`          varchar(256) NULL DEFAULT NULL COMMENT '菜单名称',
    `type`           varchar(1) NULL DEFAULT NULL COMMENT '菜单类型:D-目录,M-菜单,B-按钮',
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
    `sys_flag`       tinyint(1)    NULL DEFAULT 0 COMMENT '系统内置标识:0-非内置,1-内置',
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
    `id`          bigint NOT NULL COMMENT '日志ID',
    `login_type`  varchar(32) NULL DEFAULT NULL COMMENT '登录方式:unknown-未知,account-账号密码,phone-手机号验证码,email-邮箱验证码',
    `event_type`  varchar(32) NULL DEFAULT NULL COMMENT '事件类型:unknown-未知,login-登录,logout-登出',
    `login_key`   varchar(64) NULL DEFAULT NULL COMMENT '登录标识(账号/手机号)',
    `user_id`     bigint NULL DEFAULT NULL COMMENT '登录人ID',
    `username`    varchar(128) NULL DEFAULT NULL COMMENT '登录人账号',
    `status`      tinyint(1)    NULL DEFAULT NULL COMMENT '登录状态:0-失败,1-成功',
    `error_msg`   text NULL DEFAULT NULL COMMENT '登录失败原因',
    `ip`          varchar(256) NULL DEFAULT NULL COMMENT '登录IP',
    `region`      varchar(256) NULL DEFAULT NULL COMMENT '登录地区',
    `os`          varchar(256) NULL DEFAULT NULL COMMENT '登录操作系统',
    `device`      varchar(64) NULL DEFAULT NULL COMMENT '登录设备类型',
    `browser`     varchar(64) NULL DEFAULT NULL COMMENT '登录浏览器',
    `duration_ms` bigint NULL DEFAULT NULL COMMENT '登录耗时(毫秒)',
    `params`      text NULL DEFAULT NULL COMMENT '请求参数',
    `creator`     bigint NULL DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     bigint NULL DEFAULT NULL COMMENT '更新人',
    `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`    tinyint(1)    NULL DEFAULT 0 COMMENT '是否删除:0-否,1-是',
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
    `id`          bigint NOT NULL COMMENT '日志ID',
    `module`      varchar(256) NULL DEFAULT NULL COMMENT '操作所属模块',
    `type`        varchar(32) NULL DEFAULT NULL COMMENT '操作类型:unknown-未知,insert-新增,update-更新,query-查询,delete-删除',
    `user_id`     bigint NULL DEFAULT NULL COMMENT '操作人ID',
    `descr`       varchar(256) NULL DEFAULT NULL COMMENT '操作描述',
    `status`      tinyint(1)    NULL DEFAULT NULL COMMENT '操作状态:0-失败,1-成功',
    `error_msg`   text NULL DEFAULT NULL COMMENT '错误信息',
    `ip`          varchar(256) NULL DEFAULT NULL COMMENT '请求IP',
    `url`         varchar(512) NULL DEFAULT NULL COMMENT '请求URL',
    `region`      varchar(256) NULL DEFAULT NULL COMMENT '请求地区',
    `os`          varchar(64) NULL DEFAULT NULL COMMENT '请求操作系统',
    `device`      varchar(64) NULL DEFAULT NULL COMMENT '请求设备',
    `browser`     varchar(64) NULL DEFAULT NULL COMMENT '请求浏览器',
    `method`      varchar(1024) NULL DEFAULT NULL COMMENT '被调用方法的全限定名(包名.类名.方法名)',
    `duration_ms` bigint NULL DEFAULT NULL COMMENT '请求耗时(毫秒)',
    `params`      text NULL DEFAULT NULL COMMENT '请求参数',
    `result`      text NULL DEFAULT NULL COMMENT '响应结果',
    `creator`     bigint NULL DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     bigint NULL DEFAULT NULL COMMENT '更新人',
    `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`    tinyint(1)    NULL DEFAULT 0 COMMENT '逻辑删除标识:0-未删除,1-已删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '操作日志表'
  ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for conf_region
-- ----------------------------
DROP TABLE IF EXISTS `conf_region`;
CREATE TABLE `conf_region`
(
    `id`          bigint NOT NULL COMMENT '行政区划ID',
    `pcode`       VARCHAR(16) NULL DEFAULT NULL COMMENT '父级代码',
    `code`        VARCHAR(16) NULL DEFAULT NULL COMMENT '行政区划代码',
    `name`        VARCHAR(128) NULL DEFAULT NULL COMMENT '行政区划名称',
    `level`       tinyint(1) NULL DEFAULT NULL COMMENT '层级:1-省;2-市;3-县（区）',
    `full_path`   VARCHAR(512) NULL DEFAULT NULL COMMENT '物化路径: /110000/110100/110101/',
    `creator`     bigint NULL DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     bigint NULL DEFAULT NULL COMMENT '更新人',
    `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`    tinyint(1)    NULL DEFAULT 0 COMMENT '逻辑删除标识:0-未删除,1-已删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `idx_code` (`code` ASC) USING BTREE
)ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '行政区划配置表'
  ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for conf_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `conf_dict_type`;
CREATE TABLE `conf_dict_type`
(
    `id`          bigint NOT NULL COMMENT '字典类型ID',
    `type`        VARCHAR(128) NULL DEFAULT NULL COMMENT '字典类型',
    `name`        VARCHAR(128) NULL DEFAULT NULL COMMENT '字典名称',
    `status`      tinyint(1)    NULL DEFAULT 0 COMMENT '字典状态:0-启用,1-禁用',
    `remark`      varchar(512) NULL DEFAULT NULL COMMENT '备注',
    `creator`     bigint NULL DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     bigint NULL DEFAULT NULL COMMENT '更新人',
    `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`    tinyint(1)    NULL DEFAULT 0 COMMENT '逻辑删除标识:0-未删除,1-已删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `idx_type` (`type` ASC) USING BTREE
)ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '字典类型配置表'
  ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for conf_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `conf_dict_data`;
CREATE TABLE `conf_dict_data`
(
    `id`          bigint NOT NULL COMMENT '字典数据ID',
    `type`        VARCHAR(128) NULL DEFAULT NULL COMMENT '字典类型',
    `code`        VARCHAR(128) NULL DEFAULT NULL COMMENT '字典编码',
    `value`       VARCHAR(128) NULL DEFAULT NULL COMMENT '字典键值',
    `status`      tinyint(1)    NULL DEFAULT 0 COMMENT '字典状态:0-启用,1-禁用',
    `sort`        int NULL DEFAULT 0 COMMENT '字典排序:数字越小越靠前',
    `is_default`  tinyint(1)    NULL DEFAULT 0 COMMENT '是否默认:0-否,1-是',
    `remark`      varchar(512) NULL DEFAULT NULL COMMENT '备注',
    `creator`     bigint NULL DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     bigint NULL DEFAULT NULL COMMENT '更新人',
    `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`    tinyint(1)    NULL DEFAULT 0 COMMENT '逻辑删除标识:0-未删除,1-已删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX         `idx_type` (`type` ASC) USING BTREE,
    INDEX         `idx_code` (`code` ASC) USING BTREE
)ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '字典数据配置表'
  ROW_FORMAT = DYNAMIC;
