package cn.codesensi.leaf.rbac.common.util;

import cn.hutool.extra.spring.SpringUtil;

/**
 * 缓存工具类
 */
public class CacheUtil {

    /**
     * 获取缓存key前缀
     * 项目名称:部署环境
     *
     * @return 缓存key前缀
     */
    public static String getPrefix(String key) {
        String applicationName = SpringUtil.getApplicationName();
        String activeProfile = SpringUtil.getActiveProfile();
        return applicationName.concat(":")
                .concat(activeProfile)
                .concat(":")
                .concat(key);
    }
}
