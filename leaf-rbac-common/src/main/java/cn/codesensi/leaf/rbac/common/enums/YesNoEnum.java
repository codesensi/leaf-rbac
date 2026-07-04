package cn.codesensi.leaf.rbac.common.enums;

import cn.codesensi.leaf.rbac.common.constants.Const;
import lombok.Getter;

@Getter
public enum YesNoEnum implements BaseEnum {

    YES(Const.ONE_INT, "是"),
    NO(Const.ZERO_INT, "否");

    private final int code;

    private final String desc;

    YesNoEnum(int code, String desc) {
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

    // 辅助方法
    public static YesNoEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (YesNoEnum e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }

    public boolean isYes() {
        return this == YES;
    }

    public boolean isNo() {
        return this == NO;
    }
}
