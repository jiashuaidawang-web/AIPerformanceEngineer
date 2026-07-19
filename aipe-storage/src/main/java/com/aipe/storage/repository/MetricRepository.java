package com.aipe.storage.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class MetricRepository {
    private static final Logger log = LoggerFactory.getLogger(MetricRepository.class);

    public List<Map<String, Object>> queryMetrics(String resourceId, String metricName, long startTime, long endTime) {
        log.debug("Querying metrics: resourceId={}, metricName={}, range=[{}, {}]", resourceId, metricName, startTime, endTime);
        return java.util.Collections.emptyList();
    }
}
