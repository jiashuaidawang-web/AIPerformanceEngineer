package com.aipe.observation.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * ClickHouse 自动建表（对齐 WP011 DatabaseMigrationConfig）
 *
 * <p>在应用启动时自动执行 observation_fact 表 DDL（对齐 WP012 Blueprint §6.1 / IM-004 ch6-9）
 * <p>MergeTree + toYYYYMM(timestamp) 分区 + (resource_id, metric_name, timestamp) 排序 + TTL 365 天
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Configuration
public class ClickHouseSchemaInitializer {

    private static final Logger log = LoggerFactory.getLogger(ClickHouseSchemaInitializer.class);

    @Autowired
    private DataSource clickhouseDataSource;

    /**
     * 建表 DDL（对齐 WP012 Blueprint §6.1）
     *
     * <p>注意：ClickHouse 23.8 不允许 DateTime64 列直接用于 TTL 表达式（Code:450 BAD_TTL_EXPRESSION），
     * 因此 TTL 写为：TTL toDateTime(timestamp) + INTERVAL 365 DAY。
     */
    private static final String CREATE_TABLE_DDL = "CREATE TABLE IF NOT EXISTS observation_fact (\n" +
            "    observation_id String DEFAULT generateUUIDv4(),\n" +
            "    resource_id String,\n" +
            "    resource_type String DEFAULT 'UNKNOWN',\n" +
            "    metric_name String,\n" +
            "    metric_type Enum8('METRIC'=1, 'LOG'=2, 'TRACE'=3, 'EVENT'=4, 'SNAPSHOT'=5),\n" +
            "    metric_value Float64,\n" +
            "    unit String DEFAULT '',\n" +
            "    source String,\n" +
            "    connector_id String DEFAULT '',\n" +
            "    labels String DEFAULT '{}',\n" +
            "    payload String DEFAULT '',\n" +
            "    timestamp DateTime64(3),\n" +
            "    received_at DateTime64(3) DEFAULT now64(3)\n" +
            ") ENGINE = MergeTree()\n" +
            "PARTITION BY toYYYYMM(timestamp)\n" +
            "ORDER BY (resource_id, metric_name, timestamp)\n" +
            "TTL toDateTime(timestamp) + INTERVAL 365 DAY;";

    @PostConstruct
    public void initSchema() {
        log.info("Initializing ClickHouse observation_fact schema...");
        try (Connection conn = clickhouseDataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_TABLE_DDL);
            log.info("ClickHouse observation_fact table created/verified");
        } catch (SQLException e) {
            log.error("Failed to create observation_fact table: {}", e.getMessage(), e);
            throw new RuntimeException("ClickHouse schema initialization failed", e);
        }
    }
}
