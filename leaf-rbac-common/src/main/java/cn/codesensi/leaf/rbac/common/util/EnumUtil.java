package cn.codesensi.leaf.rbac.common.util;

import cn.codesensi.leaf.rbac.common.enums.BaseEnum;

import java.util.Objects;

/**
 * 枚举工具类
 *
 * @author zhailiang
 */
public class EnumUtil {

    /**
     * 根据 code 获取枚举
     */
    public static <T extends BaseEnum> T fromCode(Class<T> enumClass, Integer code) {
        if (code == null) return null;
        for (T e : enumClass.getEnumConstants()) {
            if (Objects.equals(e.getCode(), code)) return e;
        }
        return null;
    }

    /**
     * 根据 code 获取描述
     */
    public static String getDescByCode(Class<? extends BaseEnum> enumClass, Integer code) {
        BaseEnum e = fromCode((Class<BaseEnum>) enumClass, code);
        return e != null ? e.getDesc() : null;
    }
}
