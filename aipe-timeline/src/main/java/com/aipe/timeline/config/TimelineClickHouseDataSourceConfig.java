package com.aipe.timeline.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * ClickHouse 数据源 + JdbcTemplate 配置（Timeline 直接读 observation_fact）
 *
 * <p>对齐 WP014 §6 ClickHouse WHERE + ORDER BY（MergeTree 排序性能）
 * <p>@Primary 注解确保 JdbcTemplate 注入到此 ClickHouse DataSource
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Configuration
public class TimelineClickHouseDataSourceConfig {

    /**
     * ClickHouse 数据源（Druid 连接池由 spring-boot-starter-jdbc 自动包装）
     */
    @Bean(name = "clickhouseDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.clickhouse")
    public DataSource clickhouseDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * JdbcTemplate（注入 clickhouseDataSource）
     */
    @Bean
    @Primary
    public JdbcTemplate jdbcTemplate(DataSource clickhouseDataSource) {
        return new JdbcTemplate(clickhouseDataSource);
    }
}
