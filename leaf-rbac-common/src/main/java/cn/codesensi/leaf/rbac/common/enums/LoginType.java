package cn.codesensi.leaf.rbac.common.enums;

import lombok.Getter;

/**
 * 登录类型
 */
@Getter
public enum LoginType {
    UNKNOWN("unknown", "未知"),
    ACCOUNT("account", "账号密码登录"),
    PHONE("phone", "手机验证码登录"),
    EMAIL("email", "邮箱验证码登录"),
    ;

    /**
     * 登录类型
     */
    private final String type;

    /**
     * 登录类型说明
     */
    private final String message;

    LoginType(String type, String message) {
        this.type = type;
        this.message = message;
    }
}
