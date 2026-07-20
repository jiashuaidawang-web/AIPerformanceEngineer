package com.aipe.observation.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Observation ClickHouse Mapper（JDBC 直连）
 *
 * <p>对齐 aipe-storage.ClickHouseClient 模式：通过原生 JDBC 操作 ClickHouse observation_fact 表
 * <p>Gateway Law-004：Repository Contains No Business Logic（Mapper 只做 SQL 执行）
 *
 * <p>ClickHouse 表结构（对齐 IM-004 / WP012 Blueprint §6.1）：
 * <pre>
 * observation_id String DEFAULT generateUUIDv4(),
 * resource_id String,
 * resource_type String DEFAULT 'UNKNOWN',
 * metric_name String,
 * metric_type Enum8('METRIC'=1,...),
 * metric_value Float64,
 * unit String DEFAULT '',
 * source String,
 * connector_id String DEFAULT '',
 * labels String DEFAULT '{}',
 * payload String DEFAULT '',
 * timestamp DateTime64(3),
 * received_at DateTime64(3) DEFAULT now64(3)
 * </pre>
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Component
public class ObservationMapper {

    private static final Logger log = LoggerFactory.getLogger(ObservationMapper.class);

    /** 表名（对齐 WP012 Blueprint §6.1） */
    public static final String TABLE = "observation_fact";

    /** 列名 */
    private static final String COLUMNS = "observation_id, resource_id, resource_type, metric_name, "
            + "metric_type, metric_value, unit, source, connector_id, labels, payload, timestamp, received_at";

    @Autowired
    private DataSource clickhouseDataSource;

    /**
     * 单条插入
     *
     * @param po ObservationPO
     */
    public void insert(ObservationPO po) {
        String sql = "INSERT INTO " + TABLE + " (" + COLUMNS + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindValue(ps, po);
            ps.executeUpdate();
            log.debug("Inserted observation: id={}", po.getObservationId());
        } catch (SQLException e) {
            log.error("ClickHouse insert failed: id={}, error={}", po.getObservationId(), e.getMessage(), e);
            throw new RuntimeException("ClickHouse insert failed: " + e.getMessage(), e);
        }
    }

    /**
     * 批量插入（对齐 Pipeline 的 batchSave）
     *
     * @param poList ObservationPO 列表
     */
    public void batchInsert(List<ObservationPO> poList) {
        if (poList == null || poList.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO " + TABLE + " (" + COLUMNS + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (ObservationPO po : poList) {
                bindValue(ps, po);
                ps.addBatch();
            }
            ps.executeBatch();
            log.debug("Batch inserted {} observations", poList.size());
        } catch (SQLException e) {
            log.error("ClickHouse batch insert failed: size={}, error={}", poList.size(), e.getMessage(), e);
            throw new RuntimeException("ClickHouse batch insert failed: " + e.getMessage(), e);
        }
    }

    /**
     * 按 resource_id 查询（按时间倒序）
     *
     * @param resourceId 资源 ID
     * @param limit      限制
     * @return ObservationPO 列表
     */
    public List<ObservationPO> selectByResourceId(String resourceId, int limit) {
        String sql = "SELECT " + COLUMNS + " FROM " + TABLE
                + " WHERE resource_id = ? ORDER BY timestamp DESC LIMIT ?";
        List<ObservationPO> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, resourceId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            log.error("ClickHouse selectByResourceId failed: resourceId={}, error={}", resourceId, e.getMessage(), e);
        }
        return result;
    }

    /**
     * 按 resource_id + 时间范围 查询（按时间升序，用于 Timeline）
     *
     * @param resourceId 资源 ID
     * @param startTime  开始（毫秒）
     * @param endTime    结束（毫秒）
     * @param limit      限制
     * @return ObservationPO 列表
     */
    public List<ObservationPO> selectByResourceAndTimeRange(String resourceId, long startTime, long endTime, int limit) {
        String sql = "SELECT " + COLUMNS + " FROM " + TABLE
                + " WHERE resource_id = ? AND timestamp >= toDateTime64(?, 3) AND timestamp <= toDateTime64(?, 3) "
                + "ORDER BY timestamp ASC LIMIT ?";
        List<ObservationPO> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, resourceId);
            ps.setBigDecimal(2, toSecondsDecimal(startTime));
            ps.setBigDecimal(3, toSecondsDecimal(endTime));
            ps.setInt(4, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            log.error("ClickHouse selectByResourceAndTimeRange failed: resourceId={}, error={}", resourceId, e.getMessage(), e);
        }
        return result;
    }

    /**
     * 按 resource_id + metric_name 查询
     *
     * @param resourceId 资源 ID
     * @param metricName 指标名
     * @param limit      限制
     * @return ObservationPO 列表
     */
    public List<ObservationPO> selectByResourceAndMetric(String resourceId, String metricName, int limit) {
        String sql = "SELECT " + COLUMNS + " FROM " + TABLE
                + " WHERE resource_id = ? AND metric_name = ? ORDER BY timestamp DESC LIMIT ?";
        List<ObservationPO> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, resourceId);
            ps.setString(2, metricName);
            ps.setInt(3, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            log.error("ClickHouse selectByResourceAndMetric failed: resourceId={}, metric={}, error={}",
                    resourceId, metricName, e.getMessage(), e);
        }
        return result;
    }

    /**
     * 按 resource_id + metric_name + 时间范围 查询
     *
     * @param resourceId 资源 ID
     * @param metricName 指标名
     * @param startTime  开始（毫秒）
     * @param endTime    结束（毫秒）
     * @param limit      限制
     * @return ObservationPO 列表
     */
    public List<ObservationPO> selectByMetricAndTimeRange(String resourceId, String metricName,
                                                          long startTime, long endTime, int limit) {
        String sql = "SELECT " + COLUMNS + " FROM " + TABLE
                + " WHERE resource_id = ? AND metric_name = ? "
                + "AND timestamp >= toDateTime64(?, 3) AND timestamp <= toDateTime64(?, 3) "
                + "ORDER BY timestamp ASC LIMIT ?";
        List<ObservationPO> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, resourceId);
            ps.setString(2, metricName);
            ps.setBigDecimal(3, toSecondsDecimal(startTime));
            ps.setBigDecimal(4, toSecondsDecimal(endTime));
            ps.setInt(5, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            log.error("ClickHouse selectByMetricAndTimeRange failed: resourceId={}, metric={}, error={}",
                    resourceId, metricName, e.getMessage(), e);
        }
        return result;
    }

    // ==================== 私有方法 ====================

    private Connection getConnection() throws SQLException {
        return clickhouseDataSource.getConnection();
    }

    /**
     * 绑定 PreparedStatement 参数
     */
    private void bindValue(PreparedStatement ps, ObservationPO po) throws SQLException {
        ps.setString(1, po.getObservationId());
        ps.setString(2, po.getResourceId());
        ps.setString(3, po.getResourceType() != null ? po.getResourceType() : "UNKNOWN");
        ps.setString(4, po.getMetricName());
        ps.setString(5, po.getMetricType() != null ? po.getMetricType() : "METRIC");
        ps.setDouble(6, po.getMetricValue());
        ps.setString(7, po.getUnit() != null ? po.getUnit() : "");
        ps.setString(8, po.getSource() != null ? po.getSource() : "JVM");
        ps.setString(9, po.getConnectorId() != null ? po.getConnectorId() : "");
        ps.setString(10, po.getLabels() != null ? po.getLabels() : "{}");
        ps.setString(11, po.getPayload() != null ? po.getPayload() : "");
        // DateTime64(3)：使用 toDateTime64(秒.毫秒, 3)
        ps.setBigDecimal(12, toSecondsDecimal(po.getTimestamp()));
        ps.setBigDecimal(13, toSecondsDecimal(po.getReceivedAt() > 0 ? po.getReceivedAt() : System.currentTimeMillis()));
    }

    /**
     * 毫秒时间戳 → 秒.毫秒 的 BigDecimal（用于 toDateTime64(?, 3)）
     */
    private java.math.BigDecimal toSecondsDecimal(long millis) {
        long seconds = millis / 1000;
        int ms = (int) (millis % 1000);
        if (ms < 0) {
            ms += 1000;
            seconds -= 1;
        }
        return new java.math.BigDecimal(seconds + "." + String.format("%03d", ms));
    }

    /**
     * ResultSet 行 → ObservationPO
     */
    private ObservationPO mapRow(ResultSet rs) throws SQLException {
        ObservationPO po = new ObservationPO();
        po.setObservationId(rs.getString("observation_id"));
        po.setResourceId(rs.getString("resource_id"));
        po.setResourceType(rs.getString("resource_type"));
        po.setMetricName(rs.getString("metric_name"));
        po.setMetricType(rs.getString("metric_type"));
        po.setMetricValue(rs.getDouble("metric_value"));
        po.setUnit(rs.getString("unit"));
        po.setSource(rs.getString("source"));
        po.setConnectorId(rs.getString("connector_id"));
        po.setLabels(rs.getString("labels"));
        po.setPayload(rs.getString("payload"));
        // DateTime64(3) → 毫秒时间戳
        Timestamp ts = rs.getTimestamp("timestamp");
        po.setTimestamp(ts != null ? ts.getTime() : 0L);
        Timestamp received = rs.getTimestamp("received_at");
        po.setReceivedAt(received != null ? received.getTime() : 0L);
        return po;
    }
}
