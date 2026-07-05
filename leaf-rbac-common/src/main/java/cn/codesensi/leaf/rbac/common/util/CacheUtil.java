package cn.codesensi.leaf.rbac.common.util;

import cn.codesensi.leaf.rbac.common.constants.CacheConst;
import cn.hutool.extra.spring.SpringUtil;

/**
 * 缓存工具类
 */
public class CacheUtil {

    /**
     * 获取基础缓存前缀
     */
    public static String getBasePrefix() {
        String applicationName = SpringUtil.getApplicationName();
        String activeProfile = SpringUtil.getActiveProfile();
        return applicationName.concat(":")
                .concat(activeProfile)
                .concat(":");
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
