package com.aipe.evidence.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 置信度计算器
 *
 * <p>对齐 WP014 Blueprint §4.3 ConfidenceCalculator
 *
 * <p>计算公式（MVP）：
 * <pre>
 * confidence = BASE(50)
 *            + severity_factor (0~25)
 *            + duration_factor (0~15)
 *            + indicators_factor (0~10)
 * </pre>
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Component
public class ConfidenceCalculator {

    private static final Logger log = LoggerFactory.getLogger(ConfidenceCalculator.class);

    /** 基础置信度（不确定） - Blueprint §2.2 默认 50 */
    private static final double BASE_CONFIDENCE = 50.0;

    /** 异常严重度权重 */
    private static final double SEVERITY_WEIGHT = 0.25;

    /** 持续时间权重 */
    private static final double DURATION_WEIGHT = 0.15;

    /** 关联指标数权重 */
    private static final double INDICATORS_WEIGHT = 0.10;

    /**
     * 计算 Evidence 置信度
     *
     * @param severity        异常严重度 (0~100)
     * @param consecutiveCount 持续异常点数
     * @param totalPoints     总点数
     * @param relatedObservationIds 关联 Observation 数
     * @return 置信度 (0~100)
     */
    public double calculateConfidence(double severity, int consecutiveCount, int totalPoints, int relatedObservationIds) {
        // 严重度贡献 (0~25)
        double severityContribution = Math.min(25.0, severity * SEVERITY_WEIGHT);

        // 持续时间贡献 (0~15)：持续异常比例越高，置信度越高
        double durationRatio = totalPoints > 0 ? (double) consecutiveCount / totalPoints : 0.0;
        double durationContribution = Math.min(15.0, durationRatio * 100.0 * DURATION_WEIGHT);

        // 关联指标贡献 (0~10)：引用 Observation 越多，证据越充分
        double indicatorsContribution = Math.min(10.0, relatedObservationIds * 2.0);

        double confidence = BASE_CONFIDENCE + severityContribution + durationContribution + indicatorsContribution;

        return Math.max(0.0, Math.min(100.0, confidence));
    }

    /**
     * 简化的置信度计算（最小输入）
     */
    public double calculateConfidence(double severity, int consecutiveCount) {
        return calculateConfidence(severity, consecutiveCount, Math.max(consecutiveCount, 1), consecutiveCount);
    }
}
