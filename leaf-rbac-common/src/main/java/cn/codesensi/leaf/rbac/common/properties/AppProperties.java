package cn.codesensi.leaf.rbac.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 项目配置属性 —— 映射 {@code app.*} 配置项。
 * <p>
 * 通过 {@link ConfigurationProperties} 绑定 {@code application.yml} 中 {@code app} 前缀下的所有配置，
 * 提供项目基本信息（名称、版本、负责人、版权）以及功能开关（演示模式、请求缓存限制等）。
 * 注入方式：
 * <pre>{@code
 * @Autowired
 * private AppProperties appProperties;
 * // 或构造注入
 * private final AppProperties appProperties;
 * }</pre>
 *
 * @author codesensi
 * @since 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * 项目名称，用于页面标题、日志标识等场景。
     */
    private String name;

    /**
     * 项目版本号，与 {@code pom.xml} 中的 {@code project.version} 一致，
     * 通过 {@code @project.version@} 占位符注入。
     */
    private String version;

    /**
     * 项目负责人/维护者标识。
     */
    private String author;

    /**
     * 版权年份，用于页面底部版权声明。
     */
    private String copyright;

    /**
     * 演示模式开关。
     * <ul>
     *   <li>{@code true} — 开启演示模式，新增/修改/删除等写操作将被拦截</li>
     *   <li>{@code false} — 正常模式，所有操作不受限制</li>
     * </ul>
     */
    private Boolean demoMode;

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
