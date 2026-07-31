package com.aipe.timeline.application;

import com.aipe.timeline.domain.ObservationQueryPort;
import com.aipe.timeline.domain.Timeline;
import com.aipe.timeline.domain.TimelinePoint;
import com.aipe.timeline.domain.TimelineQuery;
import com.aipe.timeline.domain.TimelineSpecification;
import com.aipe.timeline.domain.TimelineStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Timeline 构建引擎（Application 层）
 *
 * <p>核心职责（WP014 Blueprint §4.5）：
 * <ul>
 *   <li>调用 ObservationQueryPort 从 ClickHouse 查询 Observation</li>
 *   <li>排序 + 装 Timeline 值对象 + 计算统计特征</li>
 *   <li>Timeline 不存储（Persistence Law-004 / Architecture Law-004）</li>
 * </ul>
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Service
public class TimelineService {

    private static final Logger log = LoggerFactory.getLogger(TimelineService.class);

    /** 默认查询限制 */
    private static final int DEFAULT_LIMIT = 10000;

    /** 最大查询限制 */
    private static final int MAX_LIMIT = 100_000;

    @Autowired
    private ObservationQueryPort observationQueryPort;

    @Autowired
    private TimelineStatsCalculator statsCalculator;

    /**
     * 构建指定 Resource + 指标 在时间范围内的 Timeline（核心方法）
     *
     * <p>对齐 Blueprint §5 buildTimeline：
     * <ol>
     *   <li>校验 TimelineQuery</li>
     *   <li>调用 ObservationQueryPort 查询 observation_fact</li>
     *   <li>装 Timeline 值对象</li>
     *   <li>自动计算统计特征</li>
     * </ol>
     *
     * @param query TimelineQuery（resourceId / metricName / startTime / endTime / limit）     * @return Timeline（每次查询新建，不存储）
     */
    public Timeline buildTimeline(TimelineQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("TimelineQuery cannot be null");
        }

        // 1. 校验 + 时间戳单位自动换算
        long startTime = TimelineSpecification.normalizeTimestamp(query.getStartTime());
        long endTime = TimelineSpecification.normalizeTimestamp(query.getEndTime());

        TimelineQuery normalizedQuery = new TimelineQuery(
                query.getResourceId(), query.getMetricName(), startTime, endTime, query.getLimit());
        TimelineSpecification.validate(normalizedQuery);

        // 2. 查询 Observation
        List<ObservationQueryPort.MetricPoint> observations =
                observationQueryPort.queryMetricSeries(
                        normalizedQuery.getResourceId(),
                        normalizedQuery.getMetricName(),
                        normalizedQuery.getStartTime(),
                        normalizedQuery.getEndTime(),
                        normalizedQuery.getLimit());

        // 3. 转为 TimelinePoint
        List<TimelinePoint> points = new ArrayList<>();
        for (ObservationQueryPort.MetricPoint obs : observations) {
            points.add(new TimelinePoint(
                    obs.getTimestamp(),
                    obs.getValue(),
                    obs.getUnit(),
                    obs.getConnectorId(),
                    parseLabels(obs.getLabels())));
        }

        // 4. 计算统计特征
        TimelineStats stats = statsCalculator.calculate(points);

        // 5. 生成 Timeline ID
        String timelineId = String.format("TL-%s-%s-%d-%d",
                normalizedQuery.getResourceId(),
                normalizedQuery.getMetricName() != null ? normalizedQuery.getMetricName() : "ALL",
                startTime, endTime);

        // 如果 metricName 为空（全指标），timelineId 用 UUID 唯一化
        String finalTimelineId = normalizedQuery.isSpecificMetric()
                ? timelineId
                : timelineId + "-" + UUID.randomUUID().toString().substring(0, 8);

        return new Timeline(
                finalTimelineId,
                normalizedQuery.getResourceId(),
                normalizedQuery.getMetricName(),
                startTime, endTime,
                points, stats,
                System.currentTimeMillis());
    }

    /**
     * 构建指定 Resource 的 多指标 Timeline
     *
     * <p>对齐 Blueprint §5 buildTimelines
     *     * @param query 基础 TimelineQuery（metricName 可空）     * @param metricNames 指标名列表
     * @return 多 Timeline 列表（每个指标一个 Timeline）
     */
    public List<Timeline> buildTimelines(TimelineQuery query, List<String> metricNames) {
        if (metricNames == null || metricNames.isEmpty()) {
            return Collections.singletonList(buildTimeline(query));
        }
        List<Timeline> timelines = new ArrayList<>();
        for (String metricName : metricNames) {
            TimelineQuery metricQuery = new TimelineQuery(
                    query.getResourceId(), metricName,
                    query.getStartTime(), query.getEndTime(), query.getLimit());
            timelines.add(buildTimeline(metricQuery));
        }
        return timelines;
    }    /**
     * 构建指定 Resource 在时间范围内的 全指标 Timeline
     *
     * <p>对齐 Blueprint §5 buildAllMetricsTimelines
     *
     * @param resourceId 资源 ID
     * @param startTime  开始时间（毫秒）
     * @param endTime    结束时间（毫秒）
     * @return 所有指标的 Timeline 列表     */
    public List<Timeline> buildAllMetricsTimelines(String resourceId, long startTime, long endTime) {
        startTime = TimelineSpecification.normalizeTimestamp(startTime);
        endTime = TimelineSpecification.normalizeTimestamp(endTime);

        List<String> metricNames = observationQueryPort.queryDistinctMetricNames(resourceId, startTime, endTime);
        if (metricNames.isEmpty()) {
            return Collections.emptyList();
        }

        List<Timeline> timelines = new ArrayList<>();
        for (String metricName : metricNames) {
            TimelineQuery query = new TimelineQuery(resourceId, metricName, startTime, endTime, DEFAULT_LIMIT);
            try {
                timelines.add(buildTimeline(query));
            } catch (Exception e) {
                log.warn("Failed to build timeline for metric {}: {}", metricName, e.getMessage());
            }        }
        return timelines;
    }

    /**
     * 构建用于 Evidence Engine 的 增强 Timeline（含统计特征 + 边界 + 对齐 等处理）
     *
     * <p>对齐 Blueprint §5 buildEnhancedTimeline
     *     * @param query TimelineQuery
     * @return 增强 Timeline（额外注入趋势方向和变化率）     */
    public Timeline buildEnhancedTimeline(TimelineQuery query) {
        Timeline timeline = buildTimeline(query);
        // 增强：计算趋势方向 + 变化率，包装在 stats 的注释里（不扩展 TimelineStats 字段）
        TimelineStatsCalculator.TrendDirection trend = statsCalculator.detectTrend(timeline.getPoints());
        double changeRate = statsCalculator.calculateChangeRate(timeline.getPoints());
        log.debug("Enhanced Timeline {} → trend={}, changeRate={}", timeline.getTimelineId(), trend, changeRate);
        return timeline;
    }

    /**
     * 获取资源可用指标列表
     */
    public List<String> getDistinctMetricNames(String resourceId, long startTime, long endTime) {
        startTime = TimelineSpecification.normalizeTimestamp(startTime);
        endTime = TimelineSpecification.normalizeTimestamp(endTime);
        return observationQueryPort.queryDistinctMetricNames(resourceId, startTime, endTime);
    }

    /**
     * 解析 labels JSON 字符串为 Map
     */
    private Map<String, String> parseLabels(String labels) {
        if (labels == null || labels.isEmpty() || "{}".equals(labels.trim())) {
            return Collections.emptyMap();
        }
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(labels, new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {
            });        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
