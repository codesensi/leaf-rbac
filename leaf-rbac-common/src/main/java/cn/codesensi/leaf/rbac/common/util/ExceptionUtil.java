package cn.codesensi.leaf.rbac.common.util;

import java.util.function.Supplier;

/**
 * 异常工具类
 *
 * @author codesensi
 * @since 2026-07-15
 */
public class ExceptionUtil {

    /**
     * 尝试执行 supplier，失败时返回 null 而非抛异常
     *
     * @param supplier 待执行的逻辑
     * @param <T>      返回类型
     * @return supplier 执行结果，失败返回 null
     */
    public static <T> T tryGet(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception ignored) {
            return null;
        }
    }

}
