package com.aipe.resource.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 数据源配置 - MySQL（Druid 连接池）
 *
 * <p>WP011 Resource Domain 使用 MySQL 作为 Primary Storage（对齐 IM-003 Persistence Mapping）
 */
@Configuration
public class DataSourceConfig {

    @Bean(name = "mysqlDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.mysql")
    public DataSource mysqlDataSource() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }
}
