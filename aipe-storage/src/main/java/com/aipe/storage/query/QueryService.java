package com.aipe.storage.query;

import com.aipe.storage.repository.MetricRepository;
import com.aipe.storage.repository.ObservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class QueryService {
    private static final Logger log = LoggerFactory.getLogger(QueryService.class);

    private final MetricRepository metricRepository;
    private final ObservationRepository observationRepository;

    public QueryService(MetricRepository metricRepository, ObservationRepository observationRepository) {
        this.metricRepository = metricRepository;
        this.observationRepository = observationRepository;
    }

    public List<Map<String, Object>> queryMetrics(String resourceId, String metricName, long startTime, long endTime) {
        return metricRepository.queryMetrics(resourceId, metricName, startTime, endTime);
    }

    public List<Map<String, Object>> queryByResource(String resourceId) {
        return metricRepository.queryMetrics(resourceId, null, 0, System.currentTimeMillis());
    }
}
