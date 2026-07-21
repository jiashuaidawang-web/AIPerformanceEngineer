package com.aipe.knowledge.config;

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

/**
 * MySQL 数据库迁移（knowledge 表 + 兜底建索引）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Configuration
public class KnowledgeMySqlMigrationConfig {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeMySqlMigrationConfig.class);

    @Autowired
    private DataSource mysqlDataSource;

    @Bean
    public DataSourceInitializer knowledgeTableInitializer() {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(mysqlDataSource);
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db/migration/V1__knowledge.sql"));
        initializer.setDatabasePopulator(populator);
        return initializer;
    }

    @PostConstruct
    public void createIndexes() {
        JdbcTemplate jdbc = new JdbcTemplate(mysqlDataSource);
        createIndexIfNotExists(jdbc, "idx_knowledge_evidence", "knowledge", "evidence_id");
        createIndexIfNotExists(jdbc, "idx_knowledge_type", "knowledge", "knowledge_type");
        createIndexIfNotExists(jdbc, "idx_knowledge_confidence", "knowledge", "confidence");
        log.info("Knowledge database migration completed");
    }

    private void createIndexIfNotExists(JdbcTemplate jdbc, String indexName, String tableName, String columnName) {
        try {
            Integer count = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.STATISTICS " +
                            "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND INDEX_NAME = ?",
                    Integer.class, tableName, indexName);
            if (count == null || count == 0) {
                jdbc.execute("CREATE INDEX " + indexName + " ON " + tableName + "(" + columnName + ")");
                log.info("Created index: {}", indexName);
            }
        } catch (Exception e) {
            log.warn("Index creation skipped {}: {}", indexName, e.getMessage());
        }
    }
}
