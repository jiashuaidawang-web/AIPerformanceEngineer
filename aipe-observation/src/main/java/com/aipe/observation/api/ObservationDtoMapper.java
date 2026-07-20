package com.aipe.observation.api;

import com.aipe.observation.api.dto.ObservationRequest;
import com.aipe.observation.api.dto.ObservationResponse;
import com.aipe.observation.api.dto.TrendResponse;
import com.aipe.observation.application.ObservationApplicationService;
import com.aipe.observation.application.TrendAggregator;
import com.aipe.observation.domain.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DTO ↔ Domain 映射器
 *
 * <p>Orchestration Law-004：Mapper Is A Pure Transformer（只做数据转换，无业务逻辑）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Component
public class ObservationDtoMapper {

    private static final Logger log = LoggerFactory.getLogger(ObservationDtoMapper.class);

    /**
     * ObservationRequest → Observation（入库场景）
     *
     * <p>timestamp 为空时使用 System.currentTimeMillis()（对齐 IM-004）
     */
    public Observation toDomain(ObservationRequest request) {
        if (request == null) {
            return null;
        }
        long timestamp = request.getTimestamp() != null ? request.getTimestamp() : System.currentTimeMillis();
        return com.aipe.observation.domain.ObservationFactory.create(
                request.getResourceId(),
                ObservationApplicationService.parseType(request.getType()),
                ObservationApplicationService.parseSource(request.getSource()),
                request.getName(),
                request.getValue(),
                request.getUnit() != null ? request.getUnit() : "",
                timestamp,
                request.getConnectorId() != null ? request.getConnectorId() : "",
                request.getLabels(),
                request.getPayload()
        );
    }

    /**
     * Observation → ObservationResponse
     */
    public ObservationResponse toResponse(Observation observation) {
        if (observation == null) {
            return null;
        }
        ObservationResponse response = new ObservationResponse();
        response.setObservationId(observation.getObservationId() != null ? observation.getObservationId().getValue() : null);
        response.setResourceId(observation.getResourceId());
        response.setType(observation.getType() != null ? observation.getType().name() : null);
        response.setSource(observation.getSource() != null ? observation.getSource().name() : null);
        response.setName(observation.getName());
        response.setValue(observation.getValue());
        response.setUnit(observation.getUnit());
        response.setTimestamp(observation.getTimestamp());
        response.setConnectorId(observation.getConnectorId());
        response.setLabels(observation.getLabels());
        response.setPayload(observation.getPayload());
        return response;
    }

    /**
     * Observation 列表 → ObservationResponse 列表
     */
    public List<ObservationResponse> toResponseList(List<Observation> observations) {
        return observations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * TrendAggregator.TrendPoint 列表 → TrendResponse
     */
    public TrendResponse toTrendResponse(List<TrendAggregator.TrendPoint> trendPoints,
                                         String resourceId, String metricName, String interval) {
        TrendResponse response = new TrendResponse();
        response.setResourceId(resourceId);
        response.setMetricName(metricName);
        response.setInterval(interval);
        if (trendPoints != null) {
            List<TrendResponse.TrendPoint> points = trendPoints.stream()
                    .map(this::toTrendPoint)
                    .collect(Collectors.toList());
            response.setPoints(points);
        }
        return response;
    }

    private TrendResponse.TrendPoint toTrendPoint(TrendAggregator.TrendPoint tp) {
        TrendResponse.TrendPoint point = new TrendResponse.TrendPoint();
        point.setTimestamp(tp.getBucketTimestamp());
        point.setAvg(tp.getAvg());
        point.setMax(tp.getMax());
        point.setMin(tp.getMin());
        point.setCount(tp.getCount());
        return point;
    }

    /**
     * 旧协议 List<Map.Entry> → Observation（M1 兼容）
     *
     * <p>对齐 M1 aipe-backend ObservationService.saveObservations：
     * keys = resource_id / metric_name / metric_value / timestamp / tags
     */
    public Observation fromLegacyMap(Map<String, Object> legacy) {
        if (legacy == null) {
            return null;
        }
        String resourceId = getStr(legacy, "resource_id", "unknown");
        String metricName = getStr(legacy, "metric_name", "unknown");
        Double metricValue = getDouble(legacy, "metric_value", 0.0);
        long timestamp = getLong(legacy, "timestamp", System.currentTimeMillis());
        String tags = getStr(legacy, "tags", "{}");

        Map<String, String> labels = new HashMap<>();
        // tags 可能为 JSON 字符串（如 "{}"）；此处只做 key 级传递，不解析
        if (tags != null && !tags.isEmpty() && !"{}".equals(tags)) {
            labels.put("_tags", tags);
        }

        return com.aipe.observation.domain.ObservationFactory.create(
                resourceId,
                com.aipe.observation.domain.ObservationType.METRIC,
                com.aipe.observation.domain.ObservationSource.JVM,
                metricName,
                metricValue,
                "",
                timestamp,
                "",
                labels,
                null
        );
    }

    // ==================== 解析辅助（对齐 aipe-storage ClickHouseClient 安全转换） ====================

    private String getStr(Map<String, Object> map, String key, String defaultVal) {
        Object v = map.get(key);
        return v != null ? v.toString() : defaultVal;
    }

    private Double getDouble(Map<String, Object> map, String key, Double defaultVal) {
        Object v = map.get(key);
        if (v == null) return defaultVal;
        try {
            return Double.parseDouble(v.toString());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    private long getLong(Map<String, Object> map, String key, long defaultVal) {
        Object v = map.get(key);
        if (v == null) return defaultVal;
        try {
            return Long.parseLong(v.toString());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}
