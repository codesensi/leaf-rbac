-- ----------------------------
-- Table structure for log_operate
-- ----------------------------
DROP TABLE IF EXISTS log_operate;
CREATE TABLE log_operate
(
    id              bigint NOT NULL,
    module          varchar(256) NULL DEFAULT NULL,
    type            smallint NULL DEFAULT NULL,
    descr           varchar(256) NULL DEFAULT NULL,
    status          smallint NULL DEFAULT NULL,
    error_msg       text NULL DEFAULT NULL,
    request_ip      varchar(256) NULL DEFAULT NULL,
    request_url     varchar(512) NULL DEFAULT NULL,
    request_area    varchar(256) NULL DEFAULT NULL,
    request_os      varchar(64) NULL DEFAULT NULL,
    request_device  varchar(64) NULL DEFAULT NULL,
    request_browser varchar(64) NULL DEFAULT NULL,
    method_name     varchar(1024) NULL DEFAULT NULL,
    duration_ms     bigint NULL DEFAULT NULL,
    params          text NULL,
    result          text NULL,
    creator         bigint NULL DEFAULT NULL,
    create_time     timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    updater         bigint NULL DEFAULT NULL,
    update_time     timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    del_flag        smallint NULL DEFAULT 0,
    PRIMARY KEY (id)
);

COMMENT
ON TABLE log_operate IS '操作日志表';
COMMENT
ON COLUMN log_operate.id IS '日志ID';
COMMENT
ON COLUMN log_operate.module IS '操作所属模块';
COMMENT
ON COLUMN log_operate.type IS '操作类型:0-未知,1-新增,2-更新,3-查询,4-删除';
COMMENT
ON COLUMN log_operate.descr IS '操作描述';
COMMENT
ON COLUMN log_operate.status IS '操作状态:0-失败,1-成功';
COMMENT
ON COLUMN log_operate.error_msg IS '错误信息';
COMMENT
ON COLUMN log_operate.request_ip IS '请求IP';
COMMENT
ON COLUMN log_operate.request_url IS '请求的URL地址';
COMMENT
ON COLUMN log_operate.request_area IS '请求地区';
COMMENT
ON COLUMN log_operate.request_os IS '请求系统';
COMMENT
ON COLUMN log_operate.request_device IS '请求设备';
COMMENT
ON COLUMN log_operate.request_browser IS '请求浏览器';
COMMENT
ON COLUMN log_operate.method_name IS '被调用方法的全限定名（包名.类名.方法名）';
COMMENT
ON COLUMN log_operate.duration_ms IS '请求执行耗时:单位毫秒';
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
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu
(
    id             bigint NOT NULL,
    pid            bigint NULL DEFAULT 0,
    name           varchar(2048) NULL DEFAULT NULL,
    path           varchar(512) NULL DEFAULT NULL,
    param          varchar(256) NULL DEFAULT NULL,
    component      varchar(256) NULL DEFAULT NULL,
    title          varchar(256) NULL DEFAULT NULL,
    type           smallint NULL DEFAULT NULL,
    sort           integer NULL DEFAULT 0,
    icon           varchar(256) NULL DEFAULT NULL,
    perms          varchar(64) NULL DEFAULT NULL,
    is_link        smallint NULL DEFAULT 0,
    is_frame       smallint NULL DEFAULT 0,
    frame_src      varchar(2048) NULL DEFAULT NULL,
    is_show        smallint NULL DEFAULT 1,
    is_show_parent smallint NULL DEFAULT 1,
    status         smallint NULL DEFAULT 0,
    remark         varchar(512) NULL DEFAULT NULL,
    creator        bigint NULL DEFAULT NULL,
    create_time    timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    updater        bigint NULL DEFAULT NULL,
    update_time    timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    del_flag       smallint NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE INDEX idx_menu_status ON sys_menu (status);

COMMENT
ON TABLE sys_menu IS '路由菜单表';
COMMENT
ON COLUMN sys_menu.id IS '路由菜单ID';
COMMENT
ON COLUMN sys_menu.pid IS '父级路由菜单ID';
COMMENT
ON COLUMN sys_menu.name IS '路由名称(外链地址)';
COMMENT
ON COLUMN sys_menu.path IS '路由路径';
COMMENT
ON COLUMN sys_menu.param IS '路由参数';
COMMENT
ON COLUMN sys_menu.component IS '组件路径';
COMMENT
ON COLUMN sys_menu.title IS '菜单名称';
COMMENT
ON COLUMN sys_menu.type IS '菜单类型:1-目录,2-菜单,3-按钮';
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
-- Records of sys_menu
-- ----------------------------
INSERT INTO sys_menu (id, pid, name, path, param, component, title, type, sort, icon, perms, is_link, is_frame,
                      frame_src, is_show, is_show_parent, status, remark, creator, updater, del_flag)
VALUES (1904436679454760961, 0, NULL, '/system', NULL, NULL, '系统管理', 1, 1, 'ri:settings-3-line', NULL, 0, 0, NULL,
        1, 1, 0, '系统管理目录', 1, NULL, 0);
INSERT INTO sys_menu (id, pid, name, path, param, component, title, type, sort, icon, perms, is_link, is_frame,
                      frame_src, is_show, is_show_parent, status, remark, creator, updater, del_flag)
VALUES (1904436679454760962, 1904436679454760961, 'SystemUser', '/system/user/index', NULL, NULL, '用户管理', 2, 1,
        'ri:admin-line', NULL, 0, 0, NULL, 1, 1, 0, '用户管理菜单', 1, NULL, 0);
INSERT INTO sys_menu (id, pid, name, path, param, component, title, type, sort, icon, perms, is_link, is_frame,
                      frame_src, is_show, is_show_parent, status, remark, creator, updater, del_flag)
VALUES (1905268867616264203, 1904436679454760962, NULL, NULL, NULL, NULL, '查询用户分页列表', 3, 0, NULL,
        'sys:user:page', 0, 0, NULL, 1, 1, 0, '查询用户分页列表', 1, NULL, 0);
INSERT INTO sys_menu (id, pid, name, path, param, component, title, type, sort, icon, perms, is_link, is_frame,
                      frame_src, is_show, is_show_parent, status, remark, creator, updater, del_flag)
VALUES (1905268867616264204, 1904436679454760962, NULL, NULL, NULL, NULL, '保存用户信息', 3, 0, NULL, 'sys:user:save',
        0, 0, NULL, 1, 1, 0, '新增用户', 1, NULL, 0);
INSERT INTO sys_menu (id, pid, name, path, param, component, title, type, sort, icon, perms, is_link, is_frame,
                      frame_src, is_show, is_show_parent, status, remark, creator, updater, del_flag)
VALUES (1905268867616264205, 1904436679454760962, NULL, NULL, NULL, NULL, '更新用户信息', 3, 0, NULL, 'sys:user:update',
        0, 0, NULL, 1, 1, 0, '更新用户信息', 1, NULL, 0);
INSERT INTO sys_menu (id, pid, name, path, param, component, title, type, sort, icon, perms, is_link, is_frame,
                      frame_src, is_show, is_show_parent, status, remark, creator, updater, del_flag)
VALUES (1905268867616264206, 1904436679454760962, NULL, NULL, NULL, NULL, '获取用户详情', 3, 0, NULL, 'sys:user:detail',
        0, 0, NULL, 1, 1, 0, '获取用户详情', 1, NULL, 0);
INSERT INTO sys_menu (id, pid, name, path, param, component, title, type, sort, icon, perms, is_link, is_frame,
                      frame_src, is_show, is_show_parent, status, remark, creator, updater, del_flag)
VALUES (1905268867616264207, 1904436679454760962, NULL, NULL, NULL, NULL, '删除用户信息', 3, 0, NULL, 'sys:user:delete',
        0, 0, NULL, 1, 1, 0, '删除用户信息', 1, NULL, 0);
INSERT INTO sys_menu (id, pid, name, path, param, component, title, type, sort, icon, perms, is_link, is_frame,
                      frame_src, is_show, is_show_parent, status, remark, creator, updater, del_flag)
VALUES (1905268867620458496, 1904436679454760961, 'SystemRole', '/system/role/index', NULL, NULL, '角色管理', 2, 2,
        'ri:admin-fill', NULL, 0, 0, NULL, 1, 1, 0, '角色管理菜单', 1, NULL, 0);
INSERT INTO sys_menu (id, pid, name, path, param, component, title, type, sort, icon, perms, is_link, is_frame,
                      frame_src, is_show, is_show_parent, status, remark, creator, updater, del_flag)
VALUES (1905268867620458497, 1904436679454760961, 'SystemMenu', '/system/menu/index', NULL, NULL, '菜单管理', 2, 3,
        'ep:menu', NULL, 0, 0, NULL, 1, 1, 0, '菜单管理菜单', 1, NULL, 0);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role
(
    id          bigint      NOT NULL,
    name        varchar(64) NOT NULL,
    code        varchar(64) NOT NULL,
    pid         bigint NULL DEFAULT 0,
    sort        integer NULL DEFAULT 0,
    status      smallint NULL DEFAULT 0,
    remark      varchar(512) NULL DEFAULT NULL,
    creator     bigint NULL DEFAULT NULL,
    create_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    updater     bigint NULL DEFAULT NULL,
    update_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    del_flag    smallint NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE INDEX idx_role_code ON sys_role (code);
CREATE INDEX idx_role_status ON sys_role (status);

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
-- Records of sys_role
-- ----------------------------
INSERT INTO sys_role (id, name, code, pid, sort, status, remark, creator, updater, del_flag)
VALUES (1905266993131511808, '超级管理员', 'admin', 0, 0, 0, '超级管理员角色', 1, NULL, 0);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu
(
    id          bigint NOT NULL,
    role_id     bigint NOT NULL,
    menu_id     bigint NOT NULL,
    creator     bigint NULL DEFAULT NULL,
    create_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    updater     bigint NULL DEFAULT NULL,
    update_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    del_flag    smallint NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE INDEX idx_role_menu_role_id ON sys_role_menu (role_id);
CREATE INDEX idx_role_menu_menu_id ON sys_role_menu (menu_id);

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

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO sys_role_menu (id, role_id, menu_id, creator, updater, del_flag)
VALUES (1905266993135706121, 1905266993131511808, 1904436679454760961, 1, NULL, 0);
INSERT INTO sys_role_menu (id, role_id, menu_id, creator, updater, del_flag)
VALUES (1905266993139900416, 1905266993131511808, 1904436679454760962, 1, NULL, 0);
INSERT INTO sys_role_menu (id, role_id, menu_id, creator, updater, del_flag)
VALUES (1905597870256521216, 1905266993131511808, 1905268867616264203, 1, NULL, 0);
INSERT INTO sys_role_menu (id, role_id, menu_id, creator, updater, del_flag)
VALUES (1905597870256521217, 1905266993131511808, 1905268867616264204, 1, NULL, 0);
INSERT INTO sys_role_menu (id, role_id, menu_id, creator, updater, del_flag)
VALUES (1905597870256521218, 1905266993131511808, 1905268867616264205, 1, NULL, 0);
INSERT INTO sys_role_menu (id, role_id, menu_id, creator, updater, del_flag)
VALUES (1905597870256521219, 1905266993131511808, 1905268867616264206, 1, NULL, 0);
INSERT INTO sys_role_menu (id, role_id, menu_id, creator, updater, del_flag)
VALUES (1905597870256521220, 1905266993131511808, 1905268867616264207, 1, NULL, 0);
INSERT INTO sys_role_menu (id, role_id, menu_id, creator, updater, del_flag)
VALUES (1905597870256521221, 1905266993131511808, 1905268867620458496, 1, NULL, 0);
INSERT INTO sys_role_menu (id, role_id, menu_id, creator, updater, del_flag)
VALUES (1905597870256521222, 1905266993131511808, 1905268867620458497, 1, NULL, 0);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user
(
    id          bigint       NOT NULL,
    username    varchar(128) NOT NULL,
    password    varchar(512) NOT NULL,
    nickname    varchar(64) NULL DEFAULT NULL,
    id_no       varchar(64) NULL DEFAULT NULL,
    email       varchar(64) NULL DEFAULT NULL,
    phone       varchar(11) NULL DEFAULT NULL,
    gender      smallint NULL DEFAULT 0,
    avatar      varchar(512) NULL DEFAULT NULL,
    type        smallint NULL DEFAULT 0,
    status      smallint NULL DEFAULT 0,
    remark      varchar(512) NULL DEFAULT NULL,
    creator     bigint NULL DEFAULT NULL,
    create_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    updater     bigint NULL DEFAULT NULL,
    update_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    del_flag    smallint NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE INDEX idx_user_username ON sys_user (username);
CREATE INDEX idx_user_status ON sys_user (status);

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
ON COLUMN sys_user.gender IS '用户性别:0-保密,1-男,2-女';
COMMENT
ON COLUMN sys_user.avatar IS '用户头像地址';
COMMENT
ON COLUMN sys_user.type IS '用户类型:0-系统用户';
COMMENT
ON COLUMN sys_user.status IS '用户状态:0-启用,1-禁用';
COMMENT
ON COLUMN sys_user.remark IS '备注';
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
-- Records of sys_user
-- ----------------------------
INSERT INTO sys_user (id, username, password, nickname, id_no, email, phone, gender, avatar, type, status, remark,
                      creator, updater, del_flag)
VALUES (1, 'admin', '$2a$10$dw6y693PtRDktZluumVcH.XPQyHxWVZf35dszMFk3GLrASVdGJeNG', '超级管理员', '110101200001010001',
        'admin@leaf.com', '18900000000', 0, 'https://file.codesensi.cn:1443/s/wKjLnQ', 0, 0, '超级管理员', 1, NULL, 0);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role
(
    id          bigint NOT NULL,
    user_id     bigint NOT NULL,
    role_id     bigint NOT NULL,
    creator     bigint NULL DEFAULT NULL,
    create_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    updater     bigint NULL DEFAULT NULL,
    update_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    del_flag    smallint NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE INDEX idx_user_role_user_id ON sys_user_role (user_id);
CREATE INDEX idx_user_role_role_id ON sys_user_role (role_id);

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
-- Records of sys_user_role
-- ----------------------------
INSERT INTO sys_user_role (id, user_id, role_id, creator, updater, del_flag)
VALUES (1905266993135706112, 1, 1905266993131511808, 1, NULL, 0);
