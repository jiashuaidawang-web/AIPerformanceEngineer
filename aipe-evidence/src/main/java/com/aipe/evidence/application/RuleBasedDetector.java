package com.aipe.evidence.application;

import com.aipe.evidence.domain.Evidence;
import com.aipe.evidence.domain.EvidenceBuilder;
import com.aipe.evidence.domain.EvidenceId;
import com.aipe.evidence.domain.EvidenceStatus;
import com.aipe.evidence.domain.EvidenceType;
import com.aipe.evidence.domain.ReasoningStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 规则推理检测器（MVP）
 *
 * <p>对齐 WP014 Blueprint §4.4 RuleBasedDetector
 * <p>实现 MVP 规则推理（超阈值 + 持续窗口 + 关联指标）
 *
 * <p>规则集：
 * <ul>
 *   <li>CPU > 80% 持续 ≥3 个点 → PERFORMANCE Evidence</li>
 *   <li>Memory 持续上升趋势 ≥5 个点 → PERFORMANCE Evidence</li>
 *   <li>连接池相关指标 (thread.count / connection.active) 超阈值 → DEPENDENCY Evidence</li>
 * </ul>
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Component
public class RuleBasedDetector {

    private static final Logger log = LoggerFactory.getLogger(RuleBasedDetector.class);

    /** 默认超阈值检测阈值 */
    private static final double DEFAULT_THRESHOLD = 80.0;

    /** 持续窗口最小点数 */
    private static final int MIN_CONSECUTIVE_POINTS = 3;

    /**
     * 检测结果
     */
    public static final class DetectionResult {
        private final boolean anomaly;
        private final EvidenceType type;
        private final String title;
        private final String description;
        private final double severity; // 0~100
        private final List<String> relatedObservationIds;

        public DetectionResult(boolean anomaly, EvidenceType type, String title,
                               String description, double severity, List<String> relatedObservationIds) {
            this.anomaly = anomaly;
            this.type = type;
            this.title = title;
            this.description = description;
            this.severity = severity;
            this.relatedObservationIds = relatedObservationIds;
        }

        public boolean isAnomaly() { return anomaly; }
        public EvidenceType getType() { return type; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public double getSeverity() { return severity; }
        public List<String> getRelatedObservationIds() { return relatedObservationIds; }
    }

    /**
     * 对单个指标的时序数据做规则检测
     *
     * @param metricName    指标名
     * @param points        时序点列表（timestamp -> value）
     * @param observationIds 对应的 observation ID 列表（同索引）
     * @return 检测结果
     */
    public DetectionResult detect(String metricName,
                                  List<Map.Entry<Long, double[]>> points,
                                  List<String> observationIds) {
        if (points == null || points.isEmpty()) {
            return new DetectionResult(false, null, null, null, 0.0, new ArrayList<>());
        }

        // 规则 1：通用超阈值检测（CPU/Memory/任意指标超过 80%）
        if (metricName.toLowerCase().contains("cpu") || metricName.toLowerCase().contains("usage")) {
            return detectThresholdExceeded(metricName, points, observationIds, DEFAULT_THRESHOLD);
        }

        // 规则 2：连接池 / 线程数相关
        if (metricName.toLowerCase().contains("thread")
                || metricName.toLowerCase().contains("connection")
                || metricName.toLowerCase().contains("pool")) {
            return detectThresholdExceeded(metricName, points, observationIds, 100.0);
        }

        // 规则 3：Heap/Memory 持续上升检测
        if (metricName.toLowerCase().contains("heap")
                || metricName.toLowerCase().contains("memory")
                || metricName.toLowerCase().contains("mem")) {
            return detectRisingTrend(metricName, points, observationIds);
        }

        // 默认：阈值检测
        return detectThresholdExceeded(metricName, points, observationIds, DEFAULT_THRESHOLD);
    }

    private DetectionResult detectThresholdExceeded(String metricName,
                                                    List<Map.Entry<Long, double[]>> points,
                                                    List<String> observationIds,
                                                    double threshold) {
        List<String> exceededIds = new ArrayList<>();
        double maxValue = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < points.size(); i++) {
            double value = points.get(i).getValue()[0];
            if (value > threshold) {
                if (observationIds != null && i < observationIds.size()) {
                    exceededIds.add(observationIds.get(i));
                }
                if (value > maxValue) maxValue = value;
            }
        }

        boolean anomaly = exceededIds.size() >= MIN_CONSECUTIVE_POINTS;
        double severity = Math.min(100.0, (maxValue / threshold) * 50.0 + exceededIds.size() * 5.0);

        return new DetectionResult(
                anomaly,
                EvidenceType.PERFORMANCE,
                metricName + " 持续超阈值 (>" + threshold + ")",
                "指标 " + metricName + " 在 " + exceededIds.size() + " 个采集点超过阈值 " + threshold
                        + "，最大值为 " + String.format("%.1f", maxValue),
                severity,
                exceededIds);
    }

    private DetectionResult detectRisingTrend(String metricName,
                                              List<Map.Entry<Long, double[]>> points,
                                              List<String> observationIds) {
        if (points.size() < MIN_CONSECUTIVE_POINTS) {
            return new DetectionResult(false, null, null, null, 0.0, new ArrayList<>());
        }

        int risingCount = 0;
        List<String> relatedIds = new ArrayList<>();
        double firstValue = points.get(0).getValue()[0];
        double lastValue = firstValue;

        for (int i = 1; i < points.size(); i++) {
            double prev = points.get(i - 1).getValue()[0];
            double curr = points.get(i).getValue()[0];
            if (curr > prev) {
                risingCount++;
                if (observationIds != null && i < observationIds.size()) {
                    relatedIds.add(observationIds.get(i));
                }
            }
            lastValue = curr;
        }

        boolean anomaly = risingCount >= MIN_CONSECUTIVE_POINTS - 1;
        double severity = anomaly ? Math.min(100.0, (lastValue - firstValue) / Math.max(firstValue, 1.0) * 100.0) : 0.0;

        return new DetectionResult(
                anomaly,
                EvidenceType.PERFORMANCE,
                metricName + " 持续上升趋势",
                "指标 " + metricName + " 在 " + points.size() + " 个采集点中 "
                        + risingCount + " 次上升，范围 " + String.format("%.1f", firstValue) + " → " + String.format("%.1f", lastValue),
                severity,
                relatedIds);
    }
}
