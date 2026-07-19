package com.aipe.storage.partition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PartitionManager {
    private static final Logger log = LoggerFactory.getLogger(PartitionManager.class);

    public void createPartition(Connection connection, String tableName, String partitionKey) {
        try (Statement stmt = connection.createStatement()) {
            String sql = String.format("ALTER TABLE %s ADD PARTITION IF NOT EXISTS p_%s", tableName, partitionKey);
            log.debug("Creating partition: {}", sql);
        } catch (SQLException e) {
            log.error("Failed to create partition for table {}: {}", tableName, e.getMessage());
        }
    }

    public void dropOldPartitions(Connection connection, String tableName, int retainDays) {
        log.info("Dropping partitions older than {} days for table {}", retainDays, tableName);
    }
}
