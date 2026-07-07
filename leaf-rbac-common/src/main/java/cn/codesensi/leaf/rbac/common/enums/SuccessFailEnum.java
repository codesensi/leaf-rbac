package cn.codesensi.leaf.rbac.common.enums;

import cn.codesensi.leaf.rbac.common.constants.AppConst;
import lombok.Getter;

@Getter
public enum SuccessFailEnum implements BaseEnum {

    SUCCESS(AppConst.ONE_INT, "成功"),
    FAIL(AppConst.ZERO_INT, "失败");

    private final int code;
    private final String desc;

    SuccessFailEnum(int code, String desc) {
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

    public static SuccessFailEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (SuccessFailEnum e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }

    public boolean isSuccess() {
        return this == SUCCESS;
    }

    public boolean isFail() {
        return this == FAIL;
    }
}
