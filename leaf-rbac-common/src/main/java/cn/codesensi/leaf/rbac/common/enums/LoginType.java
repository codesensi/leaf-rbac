package cn.codesensi.leaf.rbac.common.enums;

import lombok.Getter;

/**
 * 登录类型
 * 0-未知
 * 1-账号密码
 * 2-手机号
 */
@Getter
public enum LoginType {
    OTHER(0, "未知"),
    ACCOUNT(1, "账号密码"),
    PHONE(2, "手机号"),
    ;

    /**
     * 登录类型编码
     */
    private final Integer code;

    /**
     * 登录类型说明
     */
    private final String msg;

    LoginType(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
