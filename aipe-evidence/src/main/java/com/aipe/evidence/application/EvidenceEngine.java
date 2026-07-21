package com.aipe.evidence.application;

import com.aipe.evidence.domain.Evidence;
import com.aipe.evidence.domain.EvidenceBuilder;
import com.aipe.evidence.domain.EvidenceId;
import com.aipe.evidence.domain.EvidenceStatus;
import com.aipe.evidence.domain.EvidenceType;
import com.aipe.evidence.domain.EvidenceObservationPort;
import com.aipe.evidence.domain.EvidenceObservationPort.MetricPoint;
import com.aipe.evidence.domain.EvidenceRepository;
import com.aipe.evidence.domain.ReasoningStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Evidence 推理引擎（Application 层核心）
 *
 * <p>对齐 WP014 Blueprint §4.2 EvidenceEngine
 *
 * <p>核心方法：
 * <ul>
 *   <li>generateFromAnomaly: 异常 Timeline → Evidence</li>
 *   <li>calculateConfidence: 超阈值程度 × 时间窗口 × 关联指标数</li>
 *   <li>explain: 推理过程自然语言化</li>
 * </ul>
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Service
public class EvidenceEngine {

    private static final Logger log = LoggerFactory.getLogger(EvidenceEngine.class);

    /** 默认查询限制 */
    private static final int DEFAULT_LIMIT = 10000;

    @Autowired
    private EvidenceObservationPort observationPort;

    @Autowired
    private EvidenceRepository evidenceRepository;

    @Autowired
    private RuleBasedDetector ruleBasedDetector;

    @Autowired
    private ConfidenceCalculator confidenceCalculator;

    @Autowired
    private ReasoningChainProducer reasoningChainProducer;

    /**
     * From Timeline 异常 生成 Evidence（核心）
     *
     * <p>对齐 Blueprint §5: generateFromAnomaly(TimelineQuery) → Evidence
     *
     * @param resourceId  资源 ID
     * @param metricName  指标名
     * @param startTime   开始时间（毫秒）
     * @param endTime     结束时间（毫秒）
     * @return 生成的 Evidence（已持久化），若无异常返回 null
     */
    
    public Evidence generateFromAnomaly(String resourceId, String metricName,
                                       long startTime, long endTime) {
        if (resourceId == null || metricName == null) {
            throw new IllegalArgumentException("ResourceId and metricName are required");
        }

        List<MetricPoint> points = observationPort.queryMetricSeries(
                resourceId, metricName, startTime, endTime, DEFAULT_LIMIT);

        if (points.isEmpty()) {
            log.info("No observation data for resource={}, metric={}", resourceId, metricName);
            return null;
        }

        // 构建 RuleBasedDetector 输入
        List<java.util.Map.Entry<Long, double[]>> timeValuePairs = new ArrayList<>();
        List<String> obsIds = new ArrayList<>();
        for (MetricPoint p : points) {
            timeValuePairs.add(new java.util.AbstractMap.SimpleEntry<>(p.getTimestamp(), new double[]{p.getValue()}));
            obsIds.add(p.getObservationId());
        }

        // 规则检测
        RuleBasedDetector.DetectionResult detection =
                ruleBasedDetector.detect(metricName, timeValuePairs, obsIds);

        if (!detection.isAnomaly()) {
            log.debug("No anomaly detected for resource={}, metric={}", resourceId, metricName);
            return null;
        }

        // 计算置信度
        double confidence = confidenceCalculator.calculateConfidence(
                detection.getSeverity(),
                detection.getRelatedObservationIds().size(),
                points.size(),
                detection.getRelatedObservationIds().size());

        // 生成推理链
        List<ReasoningStep> reasoningSteps = reasoningChainProducer.buildPerformanceChain(
                metricName, detection.getSeverity(),
                detection.getRelatedObservationIds().size(), 80.0,
               (points.stream().mapToDouble(MetricPoint::getValue).max().orElse(0.0)),
                resourceId);

        // 装配 Evidence
        Evidence evidence = EvidenceBuilder.build(
                EvidenceId.generate(),
                detection.getType() != null ? detection.getType() : EvidenceType.PERFORMANCE,
                detection.getTitle(),
                detection.getDescription(),
                resourceId,
                detection.getRelatedObservationIds(),
                null,
                null,
                confidence,
                reasoningSteps,
                EvidenceStatus.NEW);

        Evidence saved = evidenceRepository.save(evidence);
        log.info("Generated Evidence: id={}, resource={}, metric={}, confidence={}",
                saved.getEvidenceId(), resourceId, metricName, confidence);
        return saved;
    }

    /**
     * 扫描 Resource 所有指标，生成对应 Evidence 列表（批量）
     */
    
    public List<Evidence> generateAllAnomalyEvidences(String resourceId, long startTime, long endTime) {
        List<String> metricNames = observationPort.queryDistinctMetricNames(resourceId, startTime, endTime);
        List<Evidence> evidences = new ArrayList<>();
        for (String metricName : metricNames) {
            try {
                Evidence evidence = generateFromAnomaly(resourceId, metricName, startTime, endTime);
                if (evidence != null) {
                    evidences.add(evidence);
                }
            } catch (Exception e) {
                log.warn("Failed to generate evidence for metric={}: {}", metricName, e.getMessage());
            }
        }
        return evidences;
    }

    /**
     * 计算 Evidence 置信度（基于已有推理步骤的加权计算）
     *
     * @param evidence Evidence 聚合根
     * @return 置信度 (0~100)
     */
    public double calculateConfidence(Evidence evidence) {
        if (evidence == null) return 50.0;

        // 基础 50 + reasoning步骤平均置信 + observation数量加成
        double base = 50.0;
        double reasoningAvg = 0.0;
        if (evidence.getReasoningSteps() != null && !evidence.getReasoningSteps().isEmpty()) {
            for (ReasoningStep step : evidence.getReasoningSteps()) {
                reasoningAvg += step.getConfidence();
            }
            reasoningAvg /= evidence.getReasoningSteps().size();
        }
        double obsBonus = Math.min(15.0, evidence.getObservationIds().size() * 1.5);

        return Math.min(100.0, base * 0.4 + reasoningAvg * 0.4 + obsBonus);
    }

    /**
     * 解释 Evidence（返回推理过程自然语言描述）
     *
     * @param evidence Evidence 聚合根
     * @return 自然语言解释
     */
    public String explain(Evidence evidence) {
        if (evidence == null) return "无 Evidence";
        return evidence.explain();
    }

    /**
     * 验证 Evidence（通过 → VERIFIED；失败 → REJECTED）
     */
    
    public boolean verifyEvidence(EvidenceId evidenceId, boolean approved) {
        if (evidenceId == null) {
            throw new IllegalArgumentException("EvidenceId is required");
        }
        Evidence evidence = evidenceRepository.findById(evidenceId).orElse(null);
        if (evidence == null) {
            throw new IllegalArgumentException("Evidence not found: " + evidenceId);
        }
        EvidenceStatus newStatus = approved ? EvidenceStatus.VERIFIED : EvidenceStatus.REJECTED;
        boolean updated = evidenceRepository.updateStatus(evidenceId, newStatus);
        log.info("Evidence {} verified: newStatus={}", evidenceId, newStatus);
        return updated;
    }

    /**
     * 根据 ID 查询
     */

    public java.util.Optional<Evidence> findById(EvidenceId id) {
        return evidenceRepository.findById(id);
    }

    /**
     * 按 Resource 查询
     */

    public java.util.List<Evidence> findByRootResource(String resourceId) {
        return evidenceRepository.findByRootResource(resourceId);
    }

    /**
     * 高置信度 Evidence 查询
     */
    public java.util.List<Evidence> findHighConfidence(double minConfidence) {
        return evidenceRepository.findHighConfidence(minConfidence);
    }
}
