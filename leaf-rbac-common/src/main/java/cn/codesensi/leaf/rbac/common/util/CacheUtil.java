package cn.codesensi.leaf.rbac.common.util;

import cn.codesensi.leaf.rbac.common.constants.CacheConst;
import cn.hutool.extra.spring.SpringUtil;

/**
 * 缓存工具类
 */
public class CacheUtil {

    /**
     * 基础缓存前缀缓存 —— 应用启动后 applicationName 和 activeProfile 不会改变，
     * 首次获取后缓存，避免每次构建缓存 key 都通过 SpringUtil 查找容器。
     */
    private static volatile String basePrefix;

    /**
     * 获取基础缓存前缀
     */
    public static String getBasePrefix() {
        if (basePrefix != null) {
            return basePrefix;
        }
        synchronized (CacheUtil.class) {
            if (basePrefix == null) {
                String applicationName = SpringUtil.getApplicationName();
                String activeProfile = SpringUtil.getActiveProfile();
                basePrefix = applicationName.concat(":")
                        .concat(activeProfile)
                        .concat(":");
            }
            return basePrefix;
        }
    }

    /**
     * 获取图形验证码缓存前缀
     */
    public static String getCaptchaImagePrefix() {
        return getBasePrefix().concat(CacheConst.CAPTCHA_PREFIX.concat(CacheConst.IMAGE_PREFIX));
    }

    /**
     * 获取手机验证码缓存前缀
     */
    public static String getCaptchaSmsPrefix() {
        return getBasePrefix().concat(CacheConst.CAPTCHA_PREFIX.concat(CacheConst.SMS_PREFIX));
    }
}
