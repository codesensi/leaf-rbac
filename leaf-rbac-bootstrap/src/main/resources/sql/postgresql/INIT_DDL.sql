-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user
(
    id          BIGINT       NOT NULL,
    username    VARCHAR(128) NOT NULL,
    password    VARCHAR(512) NOT NULL,
    nickname    VARCHAR(64) NULL DEFAULT NULL,
    id_card     VARCHAR(64) NULL DEFAULT NULL,
    email       VARCHAR(64) NULL DEFAULT NULL,
    phone       VARCHAR(11) NULL DEFAULT NULL,
    gender      VARCHAR(1) NULL DEFAULT 'U',
    avatar      VARCHAR(512) NULL DEFAULT NULL,
    type        SMALLINT NULL DEFAULT 0,
    status      SMALLINT NULL DEFAULT 0,
    remark      VARCHAR(512) NULL DEFAULT NULL,
    sys_flag    SMALLINT NULL DEFAULT 0,
    creator     BIGINT NULL DEFAULT NULL,
    create_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updater     BIGINT NULL DEFAULT NULL,
    update_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    del_flag    SMALLINT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE INDEX idx_u_username ON sys_user (username);
CREATE INDEX idx_u_status ON sys_user (status);

COMMENT
ON TABLE sys_user IS '用户信息表';
COMMENT
ON COLUMN sys_user.id IS '用户ID';
COMMENT
ON COLUMN sys_user.username IS '用户名称';
COMMENT
ON COLUMN sys_user.password IS '用户密码';
COMMENT
ON COLUMN sys_user.nickname IS '用户昵称';
COMMENT
ON COLUMN sys_user.id_no IS '用户身份证号码';
COMMENT
ON COLUMN sys_user.email IS '用户邮箱';
COMMENT
ON COLUMN sys_user.phone IS '用户手机号码';
COMMENT
ON COLUMN sys_user.gender IS '用户性别:U-未知,M-男,F-女';
COMMENT
ON COLUMN sys_user.avatar IS '用户头像地址';
COMMENT
ON COLUMN sys_user.type IS '用户类型:0-系统用户';
COMMENT
ON COLUMN sys_user.status IS '用户状态:0-启用,1-禁用';
COMMENT
ON COLUMN sys_user.remark IS '备注';
COMMENT
ON COLUMN sys_user.sys_flag IS '系统内置标识:0-非内置,1-内置';
COMMENT
ON COLUMN sys_user.creator IS '创建人';
COMMENT
ON COLUMN sys_user.create_time IS '创建时间';
COMMENT
ON COLUMN sys_user.updater IS '更新人';
COMMENT
ON COLUMN sys_user.update_time IS '更新时间';
COMMENT
ON COLUMN sys_user.del_flag IS '逻辑删除标识:0-未删除,1-已删除';


-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role
(
    id          BIGINT      NOT NULL,
    name        VARCHAR(64) NOT NULL,
    code        VARCHAR(64) NOT NULL,
    pid         BIGINT NULL DEFAULT 0,
    sort        INTEGER NULL DEFAULT 0,
    status      SMALLINT NULL DEFAULT 0,
    remark      VARCHAR(512) NULL DEFAULT NULL,
    sys_flag    SMALLINT NULL DEFAULT 0,
    creator     BIGINT NULL DEFAULT NULL,
    create_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updater     BIGINT NULL DEFAULT NULL,
    update_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    del_flag    SMALLINT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE INDEX idx_r_code ON sys_role (code);
CREATE INDEX idx_r_status ON sys_role (status);

COMMENT
ON TABLE sys_role IS '角色信息表';
COMMENT
ON COLUMN sys_role.id IS '角色ID';
COMMENT
ON COLUMN sys_role.name IS '角色名称';
COMMENT
ON COLUMN sys_role.code IS '角色编码';
COMMENT
ON COLUMN sys_role.pid IS '父角色ID';
COMMENT
ON COLUMN sys_role.sort IS '角色排序';
COMMENT
ON COLUMN sys_role.status IS '角色状态:0-启用,1-禁用';
COMMENT
ON COLUMN sys_role.remark IS '备注';
COMMENT
ON COLUMN sys_role.sys_flag IS '系统内置标识:0-非内置,1-内置';
COMMENT
ON COLUMN sys_role.creator IS '创建人';
COMMENT
ON COLUMN sys_role.create_time IS '创建时间';
COMMENT
ON COLUMN sys_role.updater IS '更新人';
COMMENT
ON COLUMN sys_role.update_time IS '更新时间';
COMMENT
ON COLUMN sys_role.del_flag IS '逻辑删除标识:0-未删除,1-已删除';


-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role
(
    id          BIGINT NOT NULL,
    user_id     BIGINT NOT NULL,
    role_id     BIGINT NOT NULL,
    creator     BIGINT NULL DEFAULT NULL,
    create_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updater     BIGINT NULL DEFAULT NULL,
    update_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    del_flag    SMALLINT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE INDEX idx_ur_user_id ON sys_user_role (user_id);
CREATE INDEX idx_ur_role_id ON sys_user_role (role_id);

COMMENT
ON TABLE sys_user_role IS '用户角色关联表';
COMMENT
ON COLUMN sys_user_role.id IS '主键ID';
COMMENT
ON COLUMN sys_user_role.user_id IS '用户ID';
COMMENT
ON COLUMN sys_user_role.role_id IS '角色ID';
COMMENT
ON COLUMN sys_user_role.creator IS '创建人';
COMMENT
ON COLUMN sys_user_role.create_time IS '创建时间';
COMMENT
ON COLUMN sys_user_role.updater IS '更新人';
COMMENT
ON COLUMN sys_user_role.update_time IS '更新时间';
COMMENT
ON COLUMN sys_user_role.del_flag IS '逻辑删除标识:0-未删除,1-已删除';


-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu
(
    id             BIGINT NOT NULL,
    pid            BIGINT NULL DEFAULT 0,
    name           VARCHAR(2048) NULL DEFAULT NULL,
    path           VARCHAR(512) NULL DEFAULT NULL,
    param          VARCHAR(256) NULL DEFAULT NULL,
    component      VARCHAR(256) NULL DEFAULT NULL,
    title          VARCHAR(256) NULL DEFAULT NULL,
    type           VARCHAR(1) NULL DEFAULT NULL,
    sort           INTEGER NULL DEFAULT 0,
    icon           VARCHAR(256) NULL DEFAULT NULL,
    perms          VARCHAR(64) NULL DEFAULT NULL,
    is_link        SMALLINT NULL DEFAULT 0,
    is_frame       SMALLINT NULL DEFAULT 0,
    frame_src      VARCHAR(2048) NULL DEFAULT NULL,
    is_show        SMALLINT NULL DEFAULT 1,
    is_show_parent SMALLINT NULL DEFAULT 1,
    status         SMALLINT NULL DEFAULT 0,
    remark         VARCHAR(512) NULL DEFAULT NULL,
    sys_flag       SMALLINT NULL DEFAULT 0,
    creator        BIGINT NULL DEFAULT NULL,
    create_time    TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updater        BIGINT NULL DEFAULT NULL,
    update_time    TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    del_flag       SMALLINT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE INDEX idx_m_status ON sys_menu (status);

COMMENT
ON TABLE sys_menu IS '路由菜单表';
COMMENT
ON COLUMN sys_menu.id IS '路由菜单ID';
COMMENT
ON COLUMN sys_menu.pid IS '父级路由菜单ID';
COMMENT
ON COLUMN sys_menu.name IS '路由名称/外链地址';
COMMENT
ON COLUMN sys_menu.path IS '路由路径';
COMMENT
ON COLUMN sys_menu.param IS '路由参数';
COMMENT
ON COLUMN sys_menu.component IS '组件路径';
COMMENT
ON COLUMN sys_menu.title IS '菜单名称';
COMMENT
ON COLUMN sys_menu.type IS '菜单类型:D-目录,M-菜单,B-按钮';
COMMENT
ON COLUMN sys_menu.sort IS '菜单排序';
COMMENT
ON COLUMN sys_menu.icon IS '菜单图标';
COMMENT
ON COLUMN sys_menu.perms IS '权限编码';
COMMENT
ON COLUMN sys_menu.is_link IS '是否外链:0-否,1-是';
COMMENT
ON COLUMN sys_menu.is_frame IS '是否内嵌iframe:0-否,1-是';
COMMENT
ON COLUMN sys_menu.frame_src IS '内嵌iframe地址';
COMMENT
ON COLUMN sys_menu.is_show IS '是否显示:0-否,1-是';
COMMENT
ON COLUMN sys_menu.is_show_parent IS '是否显示父级菜单:0-否,1-是';
COMMENT
ON COLUMN sys_menu.status IS '菜单状态:0-启用,1-禁用';
COMMENT
ON COLUMN sys_menu.remark IS '备注';
COMMENT
ON COLUMN sys_menu.sys_flag IS '系统内置标识:0-非内置,1-内置';
COMMENT
ON COLUMN sys_menu.creator IS '创建人';
COMMENT
ON COLUMN sys_menu.create_time IS '创建时间';
COMMENT
ON COLUMN sys_menu.updater IS '更新人';
COMMENT
ON COLUMN sys_menu.update_time IS '更新时间';
COMMENT
ON COLUMN sys_menu.del_flag IS '逻辑删除标识:0-未删除,1-已删除';


-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu
(
    id          BIGINT NOT NULL,
    role_id     BIGINT NOT NULL,
    menu_id     BIGINT NOT NULL,
    creator     BIGINT NULL DEFAULT NULL,
    create_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updater     BIGINT NULL DEFAULT NULL,
    update_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    del_flag    SMALLINT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE INDEX idx_rm_role_id ON sys_role_menu (role_id);
CREATE INDEX idx_rm_menu_id ON sys_role_menu (menu_id);

COMMENT
ON TABLE sys_role_menu IS '角色菜单关联表';
COMMENT
ON COLUMN sys_role_menu.id IS '主键ID';
COMMENT
ON COLUMN sys_role_menu.role_id IS '角色ID';
COMMENT
ON COLUMN sys_role_menu.menu_id IS '菜单ID';
COMMENT
ON COLUMN sys_role_menu.creator IS '创建人';
COMMENT
ON COLUMN sys_role_menu.create_time IS '创建时间';
COMMENT
ON COLUMN sys_role_menu.updater IS '更新人';
COMMENT
ON COLUMN sys_role_menu.update_time IS '更新时间';
COMMENT
ON COLUMN sys_role_menu.del_flag IS '逻辑删除标识:0-未删除,1-已删除';


-- Table structure for log_login
-- ----------------------------
DROP TABLE IF EXISTS log_login;
CREATE TABLE log_login
(
    id          BIGSERIAL NOT NULL,
    login_type  VARCHAR(32)  DEFAULT NULL,
    event_type  VARCHAR(32)  DEFAULT NULL,
    login_key   VARCHAR(64)  DEFAULT NULL,
    user_id     BIGINT       DEFAULT NULL,
    username    VARCHAR(128) DEFAULT NULL,
    status      SMALLINT     DEFAULT NULL,
    error_msg   TEXT         DEFAULT NULL,
    ip          VARCHAR(256) DEFAULT NULL,
    region      VARCHAR(256) DEFAULT NULL,
    os          VARCHAR(256) DEFAULT NULL,
    device      VARCHAR(64)  DEFAULT NULL,
    browser     VARCHAR(64)  DEFAULT NULL,
    duration_ms BIGINT       DEFAULT NULL,
    params      TEXT         DEFAULT NULL,
    creator     BIGINT       DEFAULT NULL,
    create_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updater     BIGINT       DEFAULT NULL,
    update_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    del_flag    SMALLINT     DEFAULT 0,
    PRIMARY KEY (id)
);

COMMENT
ON TABLE log_login IS '登录日志表';
COMMENT
ON COLUMN log_login.id IS '日志ID';
COMMENT
ON COLUMN log_login.login_type IS '登录方式:unknown-未知,account-账号密码,phone-手机号验证码,email-邮箱验证码';
COMMENT
ON COLUMN log_login.event_type IS '事件类型:unknown-未知,login-登录,logout-登出';
COMMENT
ON COLUMN log_login.login_key IS '登录标识(账号/手机号)';
COMMENT
ON COLUMN log_login.user_id IS '登录人ID';
COMMENT
ON COLUMN log_login.username IS '登录人账号';
COMMENT
ON COLUMN log_login.status IS '登录状态:0-失败,1-成功';
COMMENT
ON COLUMN log_login.error_msg IS '登录失败原因';
COMMENT
ON COLUMN log_login.ip IS '登录IP';
COMMENT
ON COLUMN log_login.region IS '登录地区';
COMMENT
ON COLUMN log_login.os IS '登录操作系统';
COMMENT
ON COLUMN log_login.device IS '登录设备类型';
COMMENT
ON COLUMN log_login.browser IS '登录浏览器';
COMMENT
ON COLUMN log_login.duration_ms IS '登录耗时(毫秒)';
COMMENT
ON COLUMN log_login.params IS '请求参数';
COMMENT
ON COLUMN log_login.creator IS '创建人';
COMMENT
ON COLUMN log_login.create_time IS '创建时间';
COMMENT
ON COLUMN log_login.updater IS '更新人';
COMMENT
ON COLUMN log_login.update_time IS '更新时间';
COMMENT
ON COLUMN log_login.del_flag IS '是否删除:0-否,1-是';


-- ----------------------------
-- Table structure for log_operate
-- ----------------------------
DROP TABLE IF EXISTS log_operate;
CREATE TABLE log_operate
(
    id          BIGINT NOT NULL,
    module      VARCHAR(256) NULL DEFAULT NULL,
    type        VARCHAR(32) NULL DEFAULT NULL,
    user_id     BIGINT NULL DEFAULT NULL,
    descr       VARCHAR(256) NULL DEFAULT NULL,
    status      SMALLINT NULL DEFAULT NULL,
    error_msg   TEXT NULL DEFAULT NULL,
    ip          VARCHAR(256) NULL DEFAULT NULL,
    url         VARCHAR(512) NULL DEFAULT NULL,
    region      VARCHAR(256) NULL DEFAULT NULL,
    os          VARCHAR(64) NULL DEFAULT NULL,
    device      VARCHAR(64) NULL DEFAULT NULL,
    browser     VARCHAR(64) NULL DEFAULT NULL,
    method      VARCHAR(1024) NULL DEFAULT NULL,
    duration_ms BIGINT NULL DEFAULT NULL,
    params      TEXT NULL DEFAULT NULL,
    result      TEXT NULL DEFAULT NULL,
    creator     BIGINT NULL DEFAULT NULL,
    create_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updater     BIGINT NULL DEFAULT NULL,
    update_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    del_flag    SMALLINT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

COMMENT
ON TABLE log_operate IS '操作日志表';
COMMENT
ON COLUMN log_operate.id IS '日志ID';
COMMENT
ON COLUMN log_operate.module IS '操作所属模块';
COMMENT
ON COLUMN log_operate.type IS '操作类型:unknown-未知,insert-新增,update-更新,query-查询,delete-删除';
COMMENT
ON COLUMN log_operate.user_id IS '操作人ID';
COMMENT
ON COLUMN log_operate.descr IS '操作描述';
COMMENT
ON COLUMN log_operate.status IS '操作状态:0-失败,1-成功';
COMMENT
ON COLUMN log_operate.error_msg IS '错误信息';
COMMENT
ON COLUMN log_operate.ip IS '请求IP';
COMMENT
ON COLUMN log_operate.url IS '请求URL';
COMMENT
ON COLUMN log_operate.region IS '请求地区';
COMMENT
ON COLUMN log_operate.os IS '请求系统';
COMMENT
ON COLUMN log_operate.device IS '请求设备';
COMMENT
ON COLUMN log_operate.browser IS '请求浏览器';
COMMENT
ON COLUMN log_operate.browser IS '请求浏览器';
COMMENT
ON COLUMN log_operate.method IS '被调用方法的全限定名(包名.类名.方法名)';
COMMENT
ON COLUMN log_operate.duration_ms IS '请求耗时(毫秒)';
COMMENT
ON COLUMN log_operate.params IS '请求参数';
COMMENT
ON COLUMN log_operate.result IS '响应结果';
COMMENT
ON COLUMN log_operate.creator IS '创建人';
COMMENT
ON COLUMN log_operate.create_time IS '创建时间';
COMMENT
ON COLUMN log_operate.updater IS '更新人';
COMMENT
ON COLUMN log_operate.update_time IS '更新时间';
COMMENT
ON COLUMN log_operate.del_flag IS '逻辑删除标识:0-未删除,1-已删除';


-- ----------------------------
-- Table structure for conf_region
-- ----------------------------
DROP TABLE IF EXISTS conf_region;
CREATE TABLE conf_region
(
    id          BIGINT NOT NULL,
    pcode       VARCHAR(16) NULL DEFAULT NULL,
    code        VARCHAR(16) NULL DEFAULT NULL,
    name        VARCHAR(128) NULL DEFAULT NULL,
    level       SMALLINT NULL DEFAULT NULL,
    full_path   VARCHAR(512) NULL DEFAULT NULL,
    creator     BIGINT NULL DEFAULT NULL,
    create_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updater     BIGINT NULL DEFAULT NULL,
    update_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    del_flag    SMALLINT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
);

CREATE INDEX idx_cr_code ON conf_region (code);

COMMENT
ON TABLE conf_region IS '行政区划配置表';
COMMENT
ON COLUMN conf_region.id IS '行政区划ID';
COMMENT
ON COLUMN conf_region.pcode IS '父级代码';
COMMENT
ON COLUMN conf_region.code IS '行政区划代码';
COMMENT
ON COLUMN conf_region.name IS '行政区划名称';
COMMENT
ON COLUMN conf_region.level IS '层级:1-省;2-市;3-县（区）';
COMMENT
ON COLUMN conf_region.full_path IS '物化路径: /110000/110100/110101/';
COMMENT
ON COLUMN conf_region.creator IS '创建人';
COMMENT
ON COLUMN conf_region.create_time IS '创建时间';
COMMENT
ON COLUMN conf_region.updater IS '更新人';
COMMENT
ON COLUMN conf_region.update_time IS '更新时间';
COMMENT
ON COLUMN conf_region.del_flag IS '逻辑删除标识:0-未删除,1-已删除';
