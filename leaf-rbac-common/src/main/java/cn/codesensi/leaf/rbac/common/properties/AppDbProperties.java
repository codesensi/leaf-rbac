package cn.codesensi.leaf.rbac.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 多数据源配置属性。
 * <p>
 * 映射配置文件 {@code app.db} 前缀下的数据源定义，支持 H2、MySQL、PostgreSQL
 * 三种数据库类型。通过 {@code app.db.type} 指定当前使用的数据库，
 * 运行时根据该字段动态获取对应的连接信息。
 * <p>
 * 配置示例：
 * <pre>{@code
 * app:
 *   db:
 *     type: mysql
 *     mysql:
 *       host: ${MYSQL_HOST:192.168.2.3}
 *       port: ${MYSQL_PORT:3306}
 *       database: ${MYSQL_DATABASE:leaf_rbac}
 *       username: ${MYSQL_USERNAME:root}
 *       password: ${MYSQL_PASSWORD:pass}
 *       driver-class-name: com.mysql.cj.jdbc.Driver
 * }</pre>
 *
 * @see cn.codesensi.leaf.rbac.bootstrap.config.DynamicDataSourceConfig
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.db")
public class AppDbProperties {

    /**
     * 当前选中的数据库类型（h2 / mysql / postgresql）
     */
    private String type;

    /**
     * H2 数据库连接配置
     */
    private DataSourceConfig h2;

    /**
     * MySQL 数据库连接配置
     */
    private DataSourceConfig mysql;

    /**
     * PostgreSQL 数据库连接配置
     */
    private DataSourceConfig postgresql;

    /**
     * 根据 {@link #type} 返回对应的数据源连接配置。
     *
     * @return 当前类型的配置对象；若 {@code type} 为 null 或不支持的类型则返回 null
     */
    public DataSourceConfig getCurrentConfig() {
        if (type == null) {
            return null;
        }
        return switch (type.toLowerCase()) {
            case "h2" -> h2;
            case "mysql" -> mysql;
            case "postgresql" -> postgresql;
            default -> null;
        };
    }

    /**
     * 获取当前选中数据库类型的完整 JDBC URL。
     * <p>
     * 优先返回配置中显式填写的 {@code url} 字段（适用于 H2 文件数据库，或直接给完整 URL 的
     * 写法）；否则根据该类型的 {@code host}/{@code port}/{@code database} 字段组装，
     * 并补充该数据库类型固定的连接参数。host/port/database 缺失时返回 null。
     *
     * @return 当前数据库类型的完整 JDBC URL；无法确定时返回 null
     */
    public String getCurrentUrl() {
        DataSourceConfig config = getCurrentConfig();
        if (config == null) {
            return null;
        }
        // 显式填写的 url 优先（H2 为文件路径，无法拆 host/port）
        if (config.getUrl() != null && !config.getUrl().isBlank()) {
            return config.getUrl();
        }
        if (type == null || config.getHost() == null || config.getPort() == null) {
            return null;
        }
        return switch (type.toLowerCase()) {
            case "mysql" -> "jdbc:mysql://" + config.getHost() + ":" + config.getPort()
                    + "/" + config.getDatabase()
                    + "?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&tinyInt1isBit=false";
            case "postgresql" -> "jdbc:postgresql://" + config.getHost() + ":" + config.getPort()
                    + "/" + config.getDatabase()
                    + "?ssl=false&sslmode=disable&client_encoding=UTF8&timezone=Asia/Shanghai";
            default -> null;
        };
    }

    /**
     * 单个数据源的连接配置。
     * <p>
     * 包含 JDBC URL、用户名、密码、驱动类名四个属性，
     * 由 {@link cn.codesensi.leaf.rbac.bootstrap.config.DynamicDataSourceConfig} 读取并注入到 HikariCP 数据源中。
     */
    @Data
    public static class DataSourceConfig {

        /**
         * JDBC 连接地址（完整 URL）。
         * <p>
         * 由 {@link AppDbProperties#getCurrentUrl()} 优先使用；对 H2（文件路径）必填，
         * 对 MySQL / PostgreSQL 也可直接填写完整 URL（与 host/port/database 二选一）。
         */
        private String url;

        /**
         * 数据库主机地址（MySQL / PostgreSQL 使用，配合 {@link #port} 组装 JDBC URL）
         */
        private String host;

        /**
         * 数据库端口（MySQL / PostgreSQL 使用）
         */
        private String port;

        /**
         * 数据库名称（MySQL / PostgreSQL 使用）
         */
        private String database;

        /**
         * 数据库登录用户名
         */
        private String username;

        /**
         * 数据库登录密码
         */
        private String password;

        /**
         * JDBC 驱动全限定类名
         */
        private String driverClassName;
    }
}
