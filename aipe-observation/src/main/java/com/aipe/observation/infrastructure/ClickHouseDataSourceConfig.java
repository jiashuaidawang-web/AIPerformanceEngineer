package com.aipe.observation.infrastructure;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * ClickHouse 数据源配置（Druid 连接池）
 *
 * <p>Observation 的 Primary Storage 是 ClickHouse（IM-004 / Persistence Law-001）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Configuration
public class ClickHouseDataSourceConfig {

    /**
     * ClickHouse 数据源（使用 Druid 连接池）
     */
    @Bean(name = "clickhouseDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.clickhouse")
    public DataSource clickhouseDataSource() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }
}
