package cn.codesensi.leaf.rbac.common.enums;

import lombok.Getter;

/**
 * 性别枚举
 *
 * @author codesensi
 * @since 1.0
 */
@Getter
public enum GenderType {

    UNKNOWN(0, "保密"),
    MALE(1, "男"),
    FEMALE(2, "女"),
    ;

    /**
     * 性别编码
     */
    private final Integer code;

    /**
     * 性别说明
     */
    private final String msg;

    GenderType(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
