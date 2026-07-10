package cn.codesensi.leaf.rbac.codegen;

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * mybatis-flex 代码生成
 */
public class Codegen {

    public static void main(String[] args) {
        // 配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        // tinyInt1isBit=false：让 JDBC 驱动将所有 TINYINT 都当作 Integer 类型处理
        dataSource.setJdbcUrl("jdbc:mysql://192.168.2.3:3306/leaf_rbac_dev?characterEncoding=utf-8&tinyInt1isBit=false");
        dataSource.setUsername("root");
        dataSource.setPassword("mysql_GzxMw2");

        // 创建配置内容
        GlobalConfig globalConfig = createGlobalConfigUseStyle();

        // 通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        // 生成代码
        generator.generate();
    }

    public static GlobalConfig createGlobalConfigUseStyle() {
        // 创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        // 设置根包
        globalConfig.getPackageConfig()
                .setSourceDir("./leaf-rbac-codegen/src/main/java")
                .setBasePackage("cn.codesensi.leaf.rbac.codegen");

        // 设置哪些字段不生成
        globalConfig.getStrategyConfig()
                .setIgnoreColumns("creator", "create_time", "updater", "update_time", "del_flag");

        // 设置生成 entity
        globalConfig.enableEntity()
                .setWithLombok(true)
                .setJdkVersion(21)
                .setSuperClass(BaseEntity.class)
                .setLombokAllArgsConstructorEnable(false)
                .setLombokNoArgsConstructorEnable(false);

        // 设置生成 mapper
        globalConfig.enableMapper();
        // 设置生成 serviceImpl
        globalConfig.enableServiceImpl();
        // 设置生成 service
        globalConfig.enableService();
        // 设置生成 controller
        globalConfig.enableController();

        return globalConfig;
    }

    @Data
    @Accessors(chain = true)
    public static class BaseEntity implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 创建人
         */
        private Long creator;

        /**
         * 创建时间
         */
        private LocalDateTime createTime;

        /**
         * 更新人
         */
        private Long updater;

        /**
         * 更新时间
         */
        private LocalDateTime updateTime;

        /**
         * 是否删除:0-否,1-是
         */
        private Integer isDelete;

    }
}