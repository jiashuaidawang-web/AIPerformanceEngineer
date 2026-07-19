package com.aipe.backend.clickhouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClickHouse 客户端
 *
 * <p>提供基本的查询和写入能力，用于 Observation 时序数据存取。
 */
@Component
public class ClickHouseClient {
    private static final Logger log = LoggerFactory.getLogger(ClickHouseClient.class);

    @Value("${spring.datasource.clickhouse.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.clickhouse.username:default}")
    private String username;

    @Value("${spring.datasource.clickhouse.password:}")
    private String password;

    /**
     * 获取连接
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    /**
     * 写入单条 Observation
     */
    public void insertObservation(String resourceId, String metricName, double metricValue, long timestamp, String tags) {
        String sql = String.format(
                "INSERT INTO metric_observation (timestamp, resource_id, metric_name, metric_value, labels) VALUES " +
                        "('%s', '%s', '%s', %s, '%s')",
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(timestamp)),
                escape(resourceId), escape(metricName), formatValue(metricValue), escape(tags)
        );
        execute(sql);
    }

    /**
     * 批量写入 Observation
     */
    public void batchInsert(List<Map<String, Object>> observations) {
        if (observations == null || observations.isEmpty()) return;

        StringBuilder sql = new StringBuilder(
                "INSERT INTO metric_observation (timestamp, resource_id, metric_name, metric_value, labels) VALUES "
        );

        for (int i = 0; i < observations.size(); i++) {
            Map<String, Object> obs = observations.get(i);
            if (i > 0) sql.append(", ");
            sql.append(String.format("('%s', '%s', '%s', %s, '%s')",
                    obs.get("timestamp").toString(),
                    escape(obs.get("resource_id").toString()),
                    escape(obs.get("metric_name").toString()),
                    formatValue(Double.parseDouble(obs.get("metric_value").toString())),
                    escape(obs.getOrDefault("tags", "{}").toString())
            ));
        }
        execute(sql.toString());
    }

    /**
     * 查询时间范围内的 Observation
     */
    public List<Map<String, Object>> query(String resourceId, String metricName, long startTime, long endTime, int limit) {
        List<Map<String, Object>> results = new ArrayList<>();
        // ClickHouse DateTime 最大值: 2147483647 (2038-01-19 03:14:07 UTC)
        long safeEndTime = Math.min(endTime, 2147483647000L);
        String sql = String.format(
                "SELECT timestamp, resource_id, metric_name, metric_value, labels " +
                        "FROM metric_observation " +
                        "WHERE resource_id = '%s' AND metric_name = '%s' " +
                        "AND timestamp >= toDateTime(%d) AND timestamp <= toDateTime(%d) " +
                        "ORDER BY timestamp DESC LIMIT %d",
                escape(resourceId), escape(metricName),
                startTime / 1000, safeEndTime / 1000, limit
        );

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("timestamp", rs.getString("timestamp"));
                row.put("resource_id", rs.getString("resource_id"));
                row.put("metric_name", rs.getString("metric_name"));
                row.put("metric_value", rs.getDouble("metric_value"));
                row.put("labels", rs.getString("labels"));
                results.add(row);
            }
        } catch (SQLException e) {
            log.error("ClickHouse query failed: {}", e.getMessage());
        }
        return results;
    }

    /**
     * 查询所有最新 Observation
     */
    public List<Map<String, Object>> queryLatest(String resourceId, int limit) {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = String.format(
                "SELECT timestamp, resource_id, metric_name, metric_value, labels " +
                        "FROM metric_observation " +
                        "WHERE resource_id = '%s' " +
                        "ORDER BY timestamp DESC LIMIT %d",
                escape(resourceId), limit
        );

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("timestamp", rs.getString("timestamp"));
                row.put("resource_id", rs.getString("resource_id"));
                row.put("metric_name", rs.getString("metric_name"));
                row.put("metric_value", rs.getDouble("metric_value"));
                row.put("labels", rs.getString("labels"));
                results.add(row);
            }
        } catch (SQLException e) {
            log.error("ClickHouse query failed: {}", e.getMessage());
        }
        return results;
    }

    private void execute(String sql) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            log.debug("ClickHouse execute success: {}", sql.substring(0, Math.min(100, sql.length())));
        } catch (SQLException e) {
            log.error("ClickHouse execute failed: {} - {}", sql.substring(0, Math.min(100, sql.length())), e.getMessage());
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("'", "\\'");
    }

    private String formatValue(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return "0";
        return String.valueOf(v);
    }
}
