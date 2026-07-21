package com.aipe.storage.query;

import com.aipe.storage.repository.MetricRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Query Service (M1 遗留 - 简化版)
 *
 * <p>WP012 之后 Observation 查询由 aipe-observation 模块接管
 */
public class QueryService {
    private static final Logger log = LoggerFactory.getLogger(QueryService.class);

    private final MetricRepository metricRepository;

    public QueryService(MetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    public List<Map<String, Object>> queryMetrics(String resourceId, String metricName, long startTime, long endTime) {
        return metricRepository.queryMetrics(resourceId, metricName, startTime, endTime);
    }

    public List<Map<String, Object>> queryByResource(String resourceId) {
        return metricRepository.queryMetrics(resourceId, null, 0, System.currentTimeMillis());
    }
}
