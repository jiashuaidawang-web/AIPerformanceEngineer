package com.aipe.evidence.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 双数据源配置：MySQL (Primary, MyBatis Plus) + ClickHouse (ObservationQueryPort)
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Configuration
public class EvidenceDataSourceConfig {

    /**
     * MySQL 数据源（Primary - 用于 MyBatis Plus Evidence 持久化）
     */
    @Bean(name = "mysqlDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.mysql")
    public DataSource mysqlDataSource() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }

    /**
     * ClickHouse 数据源（用于 ObservationQueryPort 直读 observation_fact）
     */
    @Bean(name = "clickhouseDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.clickhouse")
    public DataSource clickhouseDataSource() {
        return DataSourceBuilder.create().build();
    }
}
