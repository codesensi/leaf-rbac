package cn.codesensi.leaf.rbac.bootstrap.config;

import cn.codesensi.leaf.rbac.common.properties.AppDbProperties;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * 动态数据源配置。
 * <p>
 * 根据 {@link AppDbProperties} 中当前选中的数据库类型动态创建 HikariCP 数据源，
 * 支持 MySQL、PostgreSQL、H2 等多种数据库的无缝切换。
 * 通过 {@link Binder} 将 {@code spring.datasource.hikari} 前缀的全局参数绑定到数据源实例，
 * 再将 {@code app.db} 下的连接信息（URL、用户名、密码、驱动）覆盖写入，实现集中式管理。
 *
 * @see AppDbProperties
 * @see HikariDataSource
 * @see Binder
 */
@Slf4j
@Configuration
public class DynamicDataSourceConfig {

    /**
     * 创建并返回主数据源 Bean。
     * <p>
     * 流程：
     * <ol>
     *   <li>从 {@link AppDbProperties} 获取当前数据库类型的连接配置</li>
     *   <li>创建空的 {@link HikariDataSource} 实例</li>
     *   <li>通过 {@link Binder} 绑定全局 HikariCP 参数（超时、池大小等）</li>
     *   <li>覆盖写入 URL、用户名、密码、驱动类名等连接信息</li>
     * </ol>
     * 若未找到对应配置，抛出 {@link IllegalStateException}。
     *
     * @param appDbProperties 自定义多数据源配置属性
     * @param environment     Spring 环境，用于读取 {@code spring.datasource.hikari.*} 全局参数
     * @return 配置完成的 HikariCP 数据源
     * @throws IllegalStateException 当 {@code appDbProperties.getCurrentConfig()} 返回 null 时抛出
     */
    @Bean
    @Primary
    public DataSource dataSource(AppDbProperties appDbProperties, Environment environment) {
        AppDbProperties.DataSourceConfig config = appDbProperties.getCurrentConfig();
        if (config == null) {
            throw new IllegalStateException("No configuration found for database type: " + appDbProperties.getType());
        }

        // 创建 HikariDataSource
        HikariDataSource dataSource = new HikariDataSource();

        // 绑定 spring.datasource.hikari 前缀的全局参数
        Binder.get(environment).bind("spring.datasource.hikari", Bindable.ofInstance(dataSource));

        // 设置动态连接信息（会覆盖 Hikari 中可能存在的同名属性）
        dataSource.setJdbcUrl(config.getUrl());
        dataSource.setUsername(config.getUsername());
        dataSource.setPassword(config.getPassword());
        dataSource.setDriverClassName(config.getDriverClassName());

        log.info("DataSource initialized: {}", appDbProperties.getType());
        return dataSource;
    }
}
