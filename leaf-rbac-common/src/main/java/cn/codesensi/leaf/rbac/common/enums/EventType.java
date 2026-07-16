package cn.codesensi.leaf.rbac.common.enums;

import lombok.Getter;

/**
 * 事件类型
 * 0-未知
 * 1-登录
 * 2-登出
 */
@Getter
public enum EventType {
    UNKNOWN(0, "未知"),
    LOGIN(1, "登录"),
    LOGOUT(2, "登出"),
    ;

    /**
     * 事件类型编码
     */
    private final Integer code;

    /**
     * 事件类型说明
     */
    private final String msg;

    EventType(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
