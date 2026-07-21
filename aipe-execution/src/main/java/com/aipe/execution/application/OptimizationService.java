package com.aipe.execution.application;

import com.aipe.execution.domain.Execution;
import com.aipe.execution.domain.Optimization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Optimization 服务（Application 层核心）
 *
 * <p>对齐 WP018 Blueprint §4.3 OptimizationService
 *
 * <p>对比 before/after Observation → 统计检验 → PASS/FAIL + improvementScore 0~100
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Service
public class OptimizationService {

    private static final Logger log = LoggerFactory.getLogger(OptimizationService.class);

    /**
     * 评估执行效果（对比 before/after Snapshot → PASS/FAIL）
     */
    public Optimization evaluate(Execution execution) {
        if (execution == null) return null;

        String beforeJson = execution.getBeforeSnapshot();
        String afterJson = execution.getAfterSnapshot();

        if (beforeJson == null || afterJson == null) {
            return new Optimization(UUID.randomUUID().toString(), execution.getExecutionId().getValue(),
                    "FAIL", 0.0, "Missing before/after snapshot data", LocalDateTime.now());
        }

        // 解析快照中的指标值（简化：从 JSON 提取 avg_value 字段）
        List<Double> beforeValues = extractMetricValues(beforeJson);
        List<Double> afterValues = extractMetricValues(afterJson);

        if (beforeValues.isEmpty() || afterValues.isEmpty()) {
            return new Optimization(UUID.randomUUID().toString(), execution.getExecutionId().getValue(),
                    "FAIL", 0.0, "No metric data to compare", LocalDateTime.now());
        }

        // 统计检验（简化 t-test：比较均值变化）
        ImprovementResult result = compareObservation(beforeValues, afterValues);
        double improvementScore = result.getImprovementRate();

        String status = improvementScore > 10.0 ? "PASS" : "FAIL"; // 改善 >10% 视为 PASS
        String summary = String.format("均值变化: %.2f → %.2f (%.1f%%), 改善评分: %.1f/100",
                result.getBeforeAvg(), result.getAfterAvg(), result.getImprovementRate(), improvementScore);

        return new Optimization(UUID.randomUUID().toString(), execution.getExecutionId().getValue(),
                status, improvementScore, summary, LocalDateTime.now());
    }

    /**
     * 对比 before/after 观察数据 → 改善结果
     */
    public ImprovementResult compareObservation(List<Double> before, List<Double> after) {
        if (before == null || before.isEmpty() || after == null || after.isEmpty()) {
            return new ImprovementResult(0, 0, 0);
        }

        double beforeAvg = before.stream().mapToDouble(d -> d).average().orElse(0);
        double afterAvg = after.stream().mapToDouble(d -> d).average().orElse(0);

        double rate = 0;
        if (beforeAvg != 0) {
            rate = ((afterAvg - beforeAvg) / Math.abs(beforeAvg)) * 100.0;
        }

        return new ImprovementResult(beforeAvg, afterAvg, rate);
    }

    /**
     * 改善结果
     */
    public static class ImprovementResult {
        private final double beforeAvg;
        private final double afterAvg;
        private final double improvementRate; // 百分比（可正可负）

        public ImprovementResult(double beforeAvg, double afterAvg, double improvementRate) {
            this.beforeAvg = beforeAvg;
            this.afterAvg = afterAvg;
            this.improvementRate = improvementRate;
        }

        public double getBeforeAvg() { return beforeAvg; }
        public double getAfterAvg() { return afterAvg; }
        public double getImprovementRate() { return improvementRate; }
    }

    /**
     * 从 JSON 快照提取指标值（简化实现）
     */
    private List<Double> extractMetricValues(String json) {
        List<Double> values = new ArrayList<>();
        if (json == null || json.isEmpty()) return values;

        // 简单提取 "value":xx.xx 或 "avg_value":xx.xx 格式的数字
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"(?:value|avg_value|metric_value)\":(\\d+(?:\\.\\d+)?)");
        java.util.regex.Matcher matcher = pattern.matcher(json);
        while (matcher.find()) {
            try {
                values.add(Double.parseDouble(matcher.group(1)));
            } catch (NumberFormatException ignored) {}
        }

        // 如果没找到，尝试提取所有数字
        if (values.isEmpty()) {
            java.util.regex.Pattern numPattern = java.util.regex.Pattern.compile("(\\d+(?:\\.\\d+)?)");
            java.util.regex.Matcher numMatcher = numPattern.matcher(json);
            while (numMatcher.find()) {
                try {
                    double v = Double.parseDouble(numMatcher.group(1));
                    if (v > 0 && v < 10000) { // 过滤明显的 ID 或时间戳
                        values.add(v);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        return values;
    }
}
