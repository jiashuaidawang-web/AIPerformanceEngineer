package com.aipe.resource.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 数据库迁移（MySQL 5.x/8.x 兼容）
 *
 * <p>先由 Spring 执行 ALTER 列（纯 SQL），再由 Java 代码执行索引和存储过程（兼容 MySQL 5.x）
 */
@Configuration
public class DatabaseMigrationConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrationConfig.class);

    @Autowired
    private DataSource dataSource;

    /**
     * 执行 ALTER 列（纯 SQL，Spring 执行）
     */
    @Bean
    public DataSourceInitializer alterColumnInitializer() {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db/migration/V2__alter_columns.sql"));
        initializer.setDatabasePopulator(populator);
        return initializer;
    }

    /**
     * 执行索引（Java 代码，兼容 MySQL 5.x）
     */
    @PostConstruct
    public void createIndexes() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        createIndexIfNotExists(jdbc, "idx_resource_business_system", "resource", "business_system");
        createIndexIfNotExists(jdbc, "idx_resource_type", "resource", "resource_type");
        createIndexIfNotExists(jdbc, "idx_resource_status", "resource", "status");
        createIndexIfNotExists(jdbc, "idx_resource_created", "resource", "created_at");
        createIndexIfNotExists(jdbc, "idx_resource_parent", "resource", "parent_resource_id");
        log.info("Database migration completed (indexes verified)");
    }

    private void createIndexIfNotExists(JdbcTemplate jdbc, String indexName, String tableName, String columnName) {
        try {
            Integer count = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.STATISTICS " +
                            "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND INDEX_NAME = ?",
                    Integer.class, tableName, indexName
            );
            if (count == null || count == 0) {
                jdbc.execute("CREATE INDEX " + indexName + " ON " + tableName + "(" + columnName + ")");
                log.info("Created index: {}", indexName);
            }
        } catch (Exception e) {
            log.warn("Index creation skipped {}: {}", indexName, e.getMessage());
        }
    }
}
