package cn.codesensi.leaf.rbac.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 项目属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * 名称
     */
    private String name;

    /**
     * 版本
     */
    private String version;

    /**
     * 负责人
     */
    private String author;

    /**
     * 版权
     */
    private String copyright;

    /**
     * 演示模式
     */
    private Boolean demoMode;

    /**
     * 请求缓存限制 默认1M
     */
    private Integer requestCacheLimit = 1048576;
}
