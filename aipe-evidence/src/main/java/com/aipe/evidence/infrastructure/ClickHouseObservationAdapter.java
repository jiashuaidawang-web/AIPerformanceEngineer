package com.aipe.evidence.infrastructure;

import com.aipe.evidence.domain.EvidenceObservationPort;
import com.aipe.evidence.domain.EvidenceObservationPort.MetricPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;

/**
 * Evidence Observation ClickHouse 查询适配器
 *
 * <p>直读 ClickHouse observation_fact，通过 clickhouseDataSource 隔离数据源
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Component
public class ClickHouseObservationAdapter implements EvidenceObservationPort {

    private static final Logger log = LoggerFactory.getLogger(ClickHouseObservationAdapter.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ClickHouseObservationAdapter(@Qualifier("clickhouseDataSource") DataSource clickhouseDataSource) {
        this.jdbcTemplate = new JdbcTemplate(clickhouseDataSource);
    }

    private static final String OBSERVATION_TABLE = "observation_fact";
    private static final String COLUMNS = "observation_id, timestamp, metric_value, unit";

    private final RowMapper<MetricPoint> rowMapper = (rs, rowNum) -> {
        long ts = 0L;
        try {
            java.sql.Timestamp t = rs.getTimestamp("timestamp");
            if (t != null) ts = t.getTime();
        } catch (Exception e) {
            try { ts = rs.getLong("timestamp"); } catch (Exception ignored) {}
        }
        return new MetricPoint(
                rs.getString("observation_id"),
                ts,
                rs.getDouble("metric_value"),
                rs.getString("unit")
        );
    };

    @Override
    public List<MetricPoint> queryMetricSeries(String resourceId, String metricName,
                                                long startTime, long endTime, int limit) {
        if (resourceId == null || metricName == null) return Collections.emptyList();
        int safeLimit = Math.min(limit > 0 ? limit : 10000, 100_000);

        String sql = "SELECT " + COLUMNS + " FROM " + OBSERVATION_TABLE
                + " WHERE resource_id = ? AND metric_name = ? "
                + "AND timestamp >= toDateTime64(?, 3) AND timestamp <= toDateTime64(?, 3) "
                + "ORDER BY timestamp ASC LIMIT ?";

        try {
            return jdbcTemplate.query(sql, rowMapper, resourceId, metricName,
                    startTime / 1000, endTime / 1000, safeLimit);
        } catch (Exception e) {
            log.error("ClickHouse metric series query failed: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> queryDistinctMetricNames(String resourceId, long startTime, long endTime) {
        if (resourceId == null) return Collections.emptyList();
        String sql = "SELECT DISTINCT metric_name FROM " + OBSERVATION_TABLE
                + " WHERE resource_id = ? "
                + "AND timestamp >= toDateTime64(?, 3) AND timestamp <= toDateTime64(?, 3)";
        try {
            return jdbcTemplate.queryForList(sql, String.class, resourceId,
                    startTime / 1000, endTime / 1000);
        } catch (Exception e) {
            log.error("ClickHouse distinct metric query failed: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
