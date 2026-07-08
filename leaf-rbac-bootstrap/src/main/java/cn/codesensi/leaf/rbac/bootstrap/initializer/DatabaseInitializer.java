package cn.codesensi.leaf.rbac.bootstrap.initializer;

import cn.codesensi.leaf.rbac.bootstrap.condition.LockFileMissingCondition;
import cn.codesensi.leaf.rbac.common.properties.AppDbProperties;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.CharsetUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;

/**
 * 数据库初始化配置。
 * <p>
 * 首次启动时自动完成以下流程：
 * <ol>
 *   <li><b>建库</b> — 通过 {@link DriverManager} 直连数据库服务，若目标数据库不存在则创建</li>
 *   <li><b>建表</b> — 通过 {@link DataSourceInitializer} 执行 {@code db/{type}/schema.sql} 初始化表结构</li>
 *   <li><b>初始化数据</b> — 执行 {@code db/{type}/data.sql} 写入初始数据</li>
 *   <li><b>写锁文件</b> — 初始化完成后创建锁文件，后续启动不再重复执行</li>
 * </ol>
 * 通过 {@link LockFileMissingCondition} 控制，锁文件存在时跳过整个初始化。
 *
 * @see LockFileMissingCondition
 * @see DataSourceInitializer
 * @see AppDbProperties
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
@Conditional(LockFileMissingCondition.class)
public class DatabaseInitializer {

    /**
     * 锁文件路径，用于标记初始化是否已完成
     */
    @Value("${app.lock-file:./data/app.lock}")
    private String lockFilePath;

    /**
     * 多数据源配置
     */
    private final AppDbProperties appDbProperties;

    /**
     * 使用 {@link DriverManager} 直连数据库服务，目标数据库不存在时自动创建。
     * <p>
     * 根据配置的数据库类型执行不同策略：
     * <ul>
     *   <li><b>H2</b> — 确保文件数据库的父目录存在</li>
     *   <li><b>MySQL</b> — 连接 {@code mysql} 默认库，执行 {@code CREATE DATABASE IF NOT EXISTS}</li>
     *   <li><b>PostgreSQL</b> — 连接 {@code postgres} 默认库，检查并创建目标库</li>
     * </ul>
     * 连接时自动补充 MySQL 8+ 所需的 {@code allowPublicKeyRetrieval=true} 参数。
     *
     */
    @PostConstruct
    public void createDatabaseIfNotExists() {
        AppDbProperties.DataSourceConfig config = appDbProperties.getCurrentConfig();
        if (config == null) {
            throw new IllegalStateException("Failed to obtain current database configuration");
        }

        String url = config.getUrl();
        String dbType = resolveDbTypeFromUrl(url);
        if (dbType == null) {
            throw new IllegalStateException("Unable to resolve database type from URL");
        }

        if ("h2".equals(dbType)) {
            ensureDirectoryForH2(url);
            return;
        }

        // MySQL / PostgreSQL 建库
        String dbName = extractDbName(url);
        String dbUrl = buildDbUrl(url, dbType);
        String username = config.getUsername();
        String password = config.getPassword();

        Properties props = new Properties();
        if (username != null) {
            props.setProperty("user", username);
        }
        if (password != null) {
            props.setProperty("password", password);
        }
        // MySQL 8+ caching_sha2_password 允许非 SSL 下获取公钥
        if ("mysql".equals(dbType)) {
            props.setProperty("allowPublicKeyRetrieval", "true");
        }

        try (Connection conn = DriverManager.getConnection(dbUrl, props)) {
            switch (dbType) {
                case "mysql" -> {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + dbName + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");
                        log.info("MySQL database [{}] created / already exists", dbName);
                    }
                }
                case "postgresql" -> {
                    if (databaseExists(conn, dbName)) {
                        log.info("PostgreSQL database [{}] already exists", dbName);
                    } else {
                        try (Statement stmt = conn.createStatement()) {
                            stmt.executeUpdate("CREATE DATABASE " + dbName + " WITH ENCODING='UTF8' LOCALE = 'C.UTF-8' TEMPLATE = template0");
                            log.info("PostgreSQL database [{}] created", dbName);
                        }
                    }
                }
                default -> throw new IllegalStateException("Unsupported database type: " + dbType);
            }
        } catch (SQLException e) {
            log.error("Failed to create database", e);
            throw new IllegalStateException("Failed to create database: " + e.getMessage());
        }
    }

    /**
     * 创建 {@link DataSourceInitializer} Bean，用于执行建表和初始化数据脚本。
     * <p>
     * 根据数据库类型从 classpath 加载对应的脚本文件：
     * <ul>
     *   <li>{@code db/{type}/schema.sql} — 表结构定义</li>
     *   <li>{@code db/{type}/data.sql} — 初始数据</li>
     * </ul>
     * 脚本不存在时仅打印警告，不中断启动。
     *
     * @param dataSource 已配置好的数据源
     * @return DataSourceInitializer，用于初始化数据库脚本
     * @throws IllegalStateException 无法从 URL 解析数据库类型时抛出
     */
    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setEnabled(true);

        String dbType = resolveDbTypeFromUrl(appDbProperties.getCurrentConfig().getUrl());
        if (dbType == null) {
            throw new IllegalStateException("Unable to resolve database type from URL");
        }

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.setSeparator(";");
        populator.setSqlScriptEncoding(CharsetUtil.UTF_8);

        // 加载 DDL 脚本（建表）
        ClassPathResource ddl = new ClassPathResource("db/" + dbType + "/INIT_DDL.sql");
        if (!ddl.exists()) {
            throw new IllegalStateException("Database DDL script not found: " + ddl.getDescription());
        }
        populator.addScript(ddl);
        log.info("Loading INIT_DDL.sql: {}", ddl.getDescription());
        // 加载 DML 脚本（数据）
        ClassPathResource dml = new ClassPathResource("db/" + dbType + "/INIT_DML.sql");
        if (!dml.exists()) {
            throw new IllegalStateException("Database DML script not found: " + dml.getDescription());
        }
        populator.addScript(dml);
        log.info("Loading INIT_DML.sql: {}", dml.getDescription());
        initializer.setDatabasePopulator(populator);
        return initializer;
    }

    /**
     * 创建锁文件的 {@link ApplicationRunner}。
     * <p>
     * 在所有初始化流程完成后执行，将锁文件写入磁盘。
     * 后续启动时，{@link LockFileMissingCondition} 检测到文件存在，跳过整个初始化阶段。
     * 锁文件路径由配置项 {@code app.lock-file} 指定。
     */
    @Bean
    public ApplicationRunner lockFileCreator() {
        return (ApplicationArguments args) -> {
            Path lockPath = Paths.get(lockFilePath);
            if (Files.exists(lockPath)) {
                return;
            }
            File lockFile = lockPath.toFile();
            File parentDir = lockFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean mkdirs = parentDir.mkdirs();
                if (mkdirs) {
                    log.info("Lock file directory created: {}", parentDir.getAbsolutePath());
                }
            }
            try (FileOutputStream fos = new FileOutputStream(lockFile)) {
                String content = "Database initialization completed at " + LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.NORM_DATETIME_MS_PATTERN);
                fos.write(content.getBytes(StandardCharsets.UTF_8));
            }
            log.info("Lock file created: {}", lockPath.toAbsolutePath());
        };
    }

    // ========== 辅助方法 ==========

    /**
     * 根据 JDBC URL 前缀解析数据库类型。
     *
     * @param url JDBC 连接地址
     * @return 数据库类型字符串（mysql / postgresql / h2），无法识别返回 null
     */
    private String resolveDbTypeFromUrl(String url) {
        return switch (url) {
            case String s when s.startsWith("jdbc:mysql:") -> "mysql";
            case String s when s.startsWith("jdbc:postgresql:") -> "postgresql";
            case String s when s.startsWith("jdbc:h2:") -> "h2";
            case null, default -> null;
        };
    }

    /**
     * 确保 H2 文件数据库的存储目录存在。
     * <p>
     * 从 {@code jdbc:h2:file/path} 格式的 URL 中提取文件路径，若父目录不存在则递归创建。
     *
     * @param url H2 数据库 JDBC 连接地址
     */
    private void ensureDirectoryForH2(String url) {
        if (url.startsWith("jdbc:h2:")) {
            String path = url.substring("jdbc:h2:".length());
            int paramIndex = path.indexOf(';');
            if (paramIndex != -1) {
                path = path.substring(0, paramIndex);
            }
            File dbFile = new File(path);
            File parentFile = dbFile.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                boolean created = parentFile.mkdirs();
                if (created) {
                    log.info("H2 data directory created: {}", parentFile.getAbsolutePath());
                }
            }
        }
    }

    /**
     * 从 JDBC URL 中提取目标数据库名称。
     * <p>
     * 先剥离 {@code ?} 之后的查询参数，防止 {@code serverTimezone=Asia/Shanghai} 等值中的 {@code /} 干扰。
     *
     * @param url JDBC 连接地址
     * @return 数据库名称
     */
    private String extractDbName(String url) {
        int qIdx = url.indexOf('?');
        String path = (qIdx != -1) ? url.substring(0, qIdx) : url;
        int start = path.lastIndexOf('/') + 1;
        return url.substring(start, qIdx != -1 ? qIdx : url.length());
    }

    /**
     * 构建连接数据库的 JDBC URL。
     * <p>
     * 将原 URL 中的数据库名称替换为对应数据库服务的内置管理库：
     * <ul>
     *   <li>MySQL → {@code mysql}</li>
     *   <li>PostgreSQL → {@code postgres}</li>
     * </ul>
     * 保留原 URL 中的查询参数，先剥离 {@code ?} 之后的内容再替换，避免参数值中的 {@code /} 干扰。
     *
     * @param url    原始 JDBC 连接地址
     * @param dbType 数据库类型（mysql / postgresql）
     * @return 连接数据库的 JDBC URL
     */
    private String buildDbUrl(String url, String dbType) {
        String defaultDb = "mysql".equals(dbType) ? "mysql" : "postgres";
        int qIdx = url.indexOf('?');
        String path = (qIdx != -1) ? url.substring(0, qIdx) : url;
        String query = (qIdx != -1) ? url.substring(qIdx) : "";
        int idx = path.lastIndexOf('/');
        String prefix = url.substring(0, idx + 1);
        return prefix + defaultDb + query;
    }

    /**
     * 检查 PostgreSQL 中指定数据库是否存在。
     *
     * @param conn   连接到 {@code postgres} 默认库的连接
     * @param dbName 要检查的数据库名称
     * @return 数据库已存在返回 true，否则返回 false
     * @throws SQLException 查询失败时抛出
     */
    private boolean databaseExists(Connection conn, String dbName) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM pg_database WHERE datname = ?")) {
            ps.setString(1, dbName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
