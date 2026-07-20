package com.aipe.observation.application;

import com.aipe.observation.domain.Observation;
import com.aipe.observation.domain.ObservationRepository;
import com.aipe.observation.domain.ObservationSource;
import com.aipe.observation.domain.ObservationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Observation 应用服务
 *
 * <p>负责 Observation 的完整业务编排：
 * <ul>
 *   <li>单条 / 批量入库</li>
 *   <li>按 Resource 查询</li>
 *   <li>按指标名查询</li>
 *   <li>时间桶趋势查询（对齐 Blueprint §5 queryTrend）</li>
 * </ul>
 *
 * <p>Orchestration Law-001：Application Is The Only Transaction Owner
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Service
public class ObservationApplicationService {

    private static final Logger log = LoggerFactory.getLogger(ObservationApplicationService.class);

    /** 默认查询限制 */
    private static final int DEFAULT_LIMIT = 1000;

    /** 最大查询限制 */
    private static final int MAX_LIMIT = 10000;

    @Autowired
    private ObservationPipeline pipeline;

    @Autowired
    private ObservationRepository observationRepository;

    @Autowired
    private ObservationValidator validator;

    /**
     * 处理单条 Observation 入库
     *
     * @param observation Observation 聚合根
     * @return 入库结果
     */
    @Transactional
    public ObservationIncomingResult processIncoming(Observation observation) {
        return pipeline.processIncoming(observation);
    }

    /**
     * 批量处理 Observation 入库
     *
     * @param observations Observation 列表
     * @return 批量入库结果
     */
    @Transactional
    public BatchIncomingResult batchProcessIncoming(List<Observation> observations) {
        if (observations == null || observations.isEmpty()) {
            return new BatchIncomingResult(0, 0, 0);
        }
        int total = observations.size();
        int success = 0;
        int failed = 0;

        // 先校验全部（快速失败，避免部分入库）
        try {
            validator.validateAll(observations);
        } catch (IllegalArgumentException e) {
            log.warn("Batch validation failed: {}", e.getMessage());
            return new BatchIncomingResult(total, 0, total);
        }

        try {
            observationRepository.batchSave(observations);
            success = total;
        } catch (Exception e) {
            log.error("Batch save failed: {}", e.getMessage(), e);
            failed = total;
        }

        return new BatchIncomingResult(total, success, failed);
    }

    /**
     * 查询 Resource 的 Observation 列表
     *
     * @param resourceId 资源 ID
     * @param limit      限制
     * @return Observation 列表
     */
    @Transactional(readOnly = true)
    public List<Observation> queryByResource(String resourceId, int limit) {
        int safeLimit = sanitizeLimit(limit);
        return observationRepository.findByResourceId(resourceId, safeLimit);
    }

    /**
     * 查询 Resource 指定指标在时间范围内的 Observation 列表
     *
     * @param resourceId 资源 ID
     * @param metricName 指标名
     * @param startTime  开始（毫秒）
     * @param endTime    结束（毫秒）
     * @param limit      限制
     * @return Observation 列表
     */
    @Transactional(readOnly = true)
    public List<Observation> queryByMetric(String resourceId, String metricName,
                                           long startTime, long endTime, int limit) {
        int safeLimit = sanitizeLimit(limit);
        return observationRepository.findByMetricAndTimeRange(resourceId, metricName, startTime, endTime, safeLimit);
    }

    /**
     * 时间桶趋势查询（聚合）
     *
     * @param resourceId 资源 ID
     * @param metricName 指标名
     * @param startTime  开始（毫秒）
     * @param endTime    结束（毫秒）
     * @param interval   时间桶（1m / 5m / 1h / 1d）
     * @return 趋势点列表
     */
    @Transactional(readOnly = true)
    public List<TrendAggregator.TrendPoint> queryTrend(String resourceId, String metricName,
                                                       long startTime, long endTime, String interval) {
        int safeLimit = sanitizeLimit(MAX_LIMIT);
        List<Observation> observations = observationRepository.findByMetricAndTimeRange(
                resourceId, metricName, startTime, endTime, safeLimit);
        long intervalMs = TrendAggregator.parseInterval(interval);
        return TrendAggregator.aggregate(observations, intervalMs);
    }

    private int sanitizeLimit(int limit) {
        if (limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    // ==================== 解析辅助（DTO → Domain 入参映射） ====================

    /**
     * 解析 Observation Type 字符串（null-safe）
     */
    public static ObservationType parseType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return ObservationType.METRIC;
        }
        try {
            return ObservationType.valueOf(type.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ObservationType.METRIC;
        }
    }

    /**
     * 解析 Observation Source 字符串（null-safe）
     */
    public static ObservationSource parseSource(String source) {
        if (source == null || source.trim().isEmpty()) {
            return ObservationSource.JVM;
        }
        try {
            return ObservationSource.valueOf(source.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ObservationSource.JVM;
        }
    }
}
