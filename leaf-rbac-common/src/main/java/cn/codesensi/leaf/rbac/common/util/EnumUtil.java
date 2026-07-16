package cn.codesensi.leaf.rbac.common.util;

import cn.codesensi.leaf.rbac.common.enums.BaseEnum;

/**
 * 枚举工具类
 *
 * @author zhailiang
 */
public class EnumUtil {

    /**
     * 根据 code 获取枚举（类型安全）
     */
    public static <E extends Enum<E> & BaseEnum<T>, T> E fromCode(Class<E> enumClass, T code) {
        if (code == null) return null;
        for (E e : enumClass.getEnumConstants()) {
            if (code.equals(e.getCode())) {
                return e;
            }
        }
        return null;
    }

    /**
     * 根据 code 获取描述
     */
    public static <E extends Enum<E> & BaseEnum<T>, T> String getDesc(Class<E> enumClass, T code) {
        E e = fromCode(enumClass, code);
        return e != null ? e.getDesc() : null;
    }
}
