package com.aipe.timeline.infrastructure;

import com.aipe.timeline.domain.ObservationQueryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;

/**
 * Observation 查询适配器（通过 JdbcTemplate 直读 ClickHouse observation_fact 表）
 *
 * <p>对齐 WP014 Blueprint §6 ClickHouse 查询 + 对齐 aipe-observation ObservationMapper.toSecondsDecimal 时间格式
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Component
public class ClickHouseObservationQueryAdapter implements ObservationQueryPort {

    private static final Logger log = LoggerFactory.getLogger(ClickHouseObservationQueryAdapter.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String OBSERVATION_TABLE = "observation_fact";

    private static final String COLUMNS = "observation_id, resource_id, resource_type, metric_name, "
            + "metric_type, metric_value, unit, source, connector_id, labels, payload, timestamp, received_at";

    private final RowMapper<ObservationData> rowMapper = (rs, rowNum) -> {
        long ts = 0L;
        try {
            java.sql.Timestamp t = rs.getTimestamp("timestamp");
            if (t != null) ts = t.getTime();
        } catch (Exception e) {
            try { ts = rs.getLong("timestamp"); } catch (Exception ignored) {}
        }
        return new ObservationData(
                ts,
                rs.getDouble("metric_value"),
                rs.getString("unit"),
                rs.getString("connector_id"),
                rs.getString("labels")
        );
    };

    @Override
    public List<ObservationData> queryByResourceAndTimeRange(String resourceId, String metricName,
                                                              long startTime, long endTime, int limit) {
        if (resourceId == null || resourceId.trim().isEmpty()) {
            return Collections.emptyList();
        }
        int safeLimit = limit > 0 ? Math.min(limit, 100_000) : 10000;

        String sql;
        Object[] params;
        if (metricName != null && !metricName.trim().isEmpty()) {
            sql = "SELECT " + COLUMNS + " FROM " + OBSERVATION_TABLE
                    + " WHERE resource_id = ? AND metric_name = ? "
                    + "AND timestamp >= toDateTime64(?, 3) AND timestamp <= toDateTime64(?, 3) "
                    + "ORDER BY timestamp ASC LIMIT ?";
            params = new Object[]{resourceId, metricName,
                    toSecondsDecimal(startTime), toSecondsDecimal(endTime), safeLimit};
        } else {
            sql = "SELECT " + COLUMNS + " FROM " + OBSERVATION_TABLE
                    + " WHERE resource_id = ? "
                    + "AND timestamp >= toDateTime64(?, 3) AND timestamp <= toDateTime64(?, 3) "
                    + "ORDER BY timestamp ASC LIMIT ?";
            params = new Object[]{resourceId,
                    toSecondsDecimal(startTime), toSecondsDecimal(endTime), safeLimit};
        }

        try {
            List<ObservationData> results = jdbcTemplate.query(sql, rowMapper, params);
            log.debug("Queried {} observations for resource={}, metric={}, range=[{}, {}]",
                    results.size(), resourceId, metricName, startTime, endTime);
            return results;
        } catch (Exception e) {
            log.error("ClickHouse query failed: resourceId={}, metric={}, error={}", resourceId, metricName, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> queryDistinctMetricNames(String resourceId, long startTime, long endTime) {
        if (resourceId == null || resourceId.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String sql = "SELECT DISTINCT metric_name FROM " + OBSERVATION_TABLE
                + " WHERE resource_id = ? "
                + "AND timestamp >= toDateTime64(?, 3) AND timestamp <= toDateTime64(?, 3)";
        try {
            return jdbcTemplate.queryForList(sql, String.class, resourceId,
                    toSecondsDecimal(startTime), toSecondsDecimal(endTime));
        } catch (Exception e) {
            log.error("ClickHouse distinct metric query failed: resourceId={}, error={}", resourceId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * 毫秒时间戳 → 秒.毫秒 BigDecimal（用于 toDateTime64(?, 3)）
     * 对齐 aipe-observation ObservationMapper.toSecondsDecimal
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
}
