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
 *       url: jdbc:mysql://localhost:3306/db
 *       username: root
 *       password: pass
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
     * 单个数据源的连接配置。
     * <p>
     * 包含 JDBC URL、用户名、密码、驱动类名四个属性，
     * 由 {@link cn.codesensi.leaf.rbac.bootstrap.config.DynamicDataSourceConfig} 读取并注入到 HikariCP 数据源中。
     */
    @Data
    public static class DataSourceConfig {

        /**
         * JDBC 连接地址
         */
        private String url;

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
