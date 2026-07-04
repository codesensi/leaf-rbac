package cn.codesensi.leaf.rbac.common.enums;

import cn.codesensi.leaf.rbac.common.constants.Const;
import lombok.Getter;

@Getter
public enum EnableDisableEnum implements BaseEnum {

    ENABLE(Const.ONE_INT, "启用"),
    DISABLE(Const.ZERO_INT, "禁用");

    private final int code;
    private final String desc;

    EnableDisableEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public static EnableDisableEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (EnableDisableEnum e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }

    public boolean isEnable() {
        return this == ENABLE;
    }

    public boolean isDisable() {
        return this == DISABLE;
    }
}
