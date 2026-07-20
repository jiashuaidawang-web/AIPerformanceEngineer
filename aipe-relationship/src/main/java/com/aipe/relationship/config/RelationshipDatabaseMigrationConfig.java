package com.aipe.relationship.config;

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
 * 数据库迁移（MySQL 8.0 自动建表 + 兜底建索引）
 *
 * <p>对齐 WP011 DatabaseMigrationConfig 模式
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Configuration
public class RelationshipDatabaseMigrationConfig {

    private static final Logger log = LoggerFactory.getLogger(RelationshipDatabaseMigrationConfig.class);

    @Autowired
    private DataSource dataSource;

    @Bean
    public DataSourceInitializer relationshipTableInitializer() {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db/migration/V1__relationship.sql"));
        initializer.setDatabasePopulator(populator);
        return initializer;
    }

    @PostConstruct
    public void createIndexes() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        createIndexIfNotExists(jdbc, "idx_relationship_source", "relationship", "source_resource_id");
        createIndexIfNotExists(jdbc, "idx_relationship_target", "relationship", "target_resource_id");
        createIndexIfNotExists(jdbc, "idx_relationship_type", "relationship", "relationship_type");
        createIndexIfNotExists(jdbc, "idx_relationship_status", "relationship", "status");
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
