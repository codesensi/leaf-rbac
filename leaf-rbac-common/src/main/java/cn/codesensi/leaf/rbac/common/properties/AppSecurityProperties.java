package cn.codesensi.leaf.rbac.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 项目安全配置属性 —— 映射 {@code app.security.*} 配置项。
 * <p>
 * 通过 {@link ConfigurationProperties} 绑定 {@code application.yml} 中 {@code app.security} 前缀下的所有配置，
 * 提供请求缓存限制、刷新令牌超时时间等。
 * 注入方式：
 * <pre>{@code
 * @Autowired
 * private AppSecurityProperties appSecurityProperties;
 * // 或构造注入
 * private final AppSecurityProperties appSecurityProperties;
 * }</pre>
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {

    /**
     * 请求体缓存的最大字节数。
     * <p>
     * 用于 {@link cn.codesensi.leaf.rbac.framework.filter.CacheRequestBodyFilter}，
     * 限制 {@link org.springframework.web.util.ContentCachingRequestWrapper} 可缓存的最大请求体大小，
     * 避免超大请求体（如文件上传）导致内存溢出。
     * 默认值：{@code 1048576}（1MB）。
     */
    private Integer requestCacheLimit = 1048576;

}
