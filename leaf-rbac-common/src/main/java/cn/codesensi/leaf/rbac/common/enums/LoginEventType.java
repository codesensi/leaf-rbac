package cn.codesensi.leaf.rbac.common.enums;

import lombok.Getter;

/**
 * 登录事件类型枚举
 */
@Getter
public enum LoginEventType implements BaseEnum<String> {

    UNKNOWN("unknown", "未知"),
    LOGIN("login", "登录"),
    LOGOUT("logout", "登出"),
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
    LoginEventType(String code, String desc) {
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
