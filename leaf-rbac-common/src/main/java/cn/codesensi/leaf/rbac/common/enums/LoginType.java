package cn.codesensi.leaf.rbac.common.enums;

import lombok.Getter;

/**
 * 登录类型枚举
 * unknown-未知
 * account-账号密码登录
 * phone-手机验证码登录
 * email-邮箱验证码登录
 */
@Getter
public enum LoginType implements BaseEnum<String> {

    UNKNOWN("unknown", "未知"),
    ACCOUNT("account", "账号密码登录"),
    PHONE("phone", "手机验证码登录"),
    EMAIL("email", "邮箱验证码登录"),
    ;

    /**
     * 编码
     */
    private final String code;

    /**
     * 说明
     */
    private final String desc;

    /**
     * 枚举构造函数
     *
     * @param code 编码
     * @param desc 说明
     */
    LoginType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
