package cn.codesensi.leaf.rbac.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 项目缓存配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.cache")
public class AppCacheProperties {

    /**
     * 缓存默认基准时间，单位秒。2 天
     */
    private Long baseTtl = 2L * 24 * 60 * 60;

    /**
     * 缓存随机偏移上限，单位秒。1 天
     */
    private Long maxExtra = 24L * 60 * 60;

}
