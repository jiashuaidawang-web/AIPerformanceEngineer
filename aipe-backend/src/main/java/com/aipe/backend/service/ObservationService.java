package com.aipe.backend.service;

import com.aipe.backend.clickhouse.ClickHouseClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Observation 服务
 *
 * <p>负责接收 Agent 上报的观测数据并持久化到 ClickHouse。
 */
@Service
public class ObservationService {
    private static final Logger log = LoggerFactory.getLogger(ObservationService.class);

    @Autowired
    private ClickHouseClient clickHouseClient;

    /**
     * 批量保存 Observation
     */
    public void saveObservations(List<Map<String, Object>> observations) {
        saveObservations(observations, "HOST");
    }

    /**
     * 批量保存 Observation (带 resourceType)
     */
    public void saveObservations(List<Map<String, Object>> observations, String resourceType) {
        if (observations == null || observations.isEmpty()) return;

        for (Map<String, Object> obs : observations) {
            try {
                String resourceId = obs.getOrDefault("resource_id", "unknown").toString();
                String metricName = obs.get("metric_name").toString();
                double metricValue = Double.parseDouble(obs.get("metric_value").toString());
                long timestamp = obs.get("timestamp") != null ?
                        Long.parseLong(obs.get("timestamp").toString()) : System.currentTimeMillis();
                String tags = obs.getOrDefault("tags", "{}").toString();

                clickHouseClient.insertObservation(resourceId, metricName, metricValue, timestamp, tags, resourceType);
            } catch (Exception e) {
                log.warn("Failed to save observation: {}", e.getMessage());
            }
        }
        log.info("Saved {} observations to ClickHouse", observations.size());
    }

    /**
     * 查询 Observation
     */
    public List<Map<String, Object>> query(String resourceId, String metricName, long startTime, long endTime, int limit) {
        return clickHouseClient.query(resourceId, metricName, startTime, endTime, limit);
    }

    /**
     * 查询最新 Observation
     */
    public List<Map<String, Object>> queryLatest(String resourceId, int limit) {
        return clickHouseClient.queryLatest(resourceId, limit);
    }
}
