package cn.codesensi.leaf.rbac.common.constants;

/**
 * 缓存常量
 */
public class CacheConst {

    /**
     * 验证码缓存前缀
     */
    public static final String CAPTCHA_PREFIX = "captcha:";

    /**
     * 短信缓存前缀
     */
    public static final String SMS_PREFIX = "sms:";

    /**
     * 图形缓存前缀
     */
    public static final String IMAGE_PREFIX = "image:";

    /**
     * 用户缓存
     */
    public static final String CACHE_USER = "user";

    /**
     * 过期时间：5分钟
     */
    public static final Long EXPIRE_5_MINUTES = 5L;
}
