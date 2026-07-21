package com.aipe.evidence.application;

import com.aipe.evidence.domain.Evidence;
import com.aipe.evidence.domain.EvidenceType;
import com.aipe.evidence.domain.ReasoningStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 推理链生产者
 *
 * <p>对齐 WP014 Blueprint §4.4 ReasoningChainProducer
 * <p>根据异常检测结果生成结构化推理步骤
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Component
public class ReasoningChainProducer {

    private static final Logger log = LoggerFactory.getLogger(ReasoningChainProducer.class);

    /**
     * 生成性能异常的推理链
     *
     * @param metricName          指标名
     * @param severity            异常严重度
     * @param consecutiveCount    持续异常点数
     * @param threshold           阈值
     * @param maxValue            实际最大值
     * @param rootResourceId      根 Resource
     * @return 推理步骤列表
     */
    public List<ReasoningStep> buildPerformanceChain(String metricName, double severity,
                                                     int consecutiveCount, double threshold,
                                                     double maxValue, String rootResourceId) {
        List<ReasoningStep> steps = new ArrayList<>();

        steps.add(new ReasoningStep(1,
                "查询 Resource " + rootResourceId + " 的 " + metricName + " Timeline",
                "获取到时序数据，共 " + consecutiveCount + " 个异常点",
                90.0));

        steps.add(new ReasoningStep(2,
                "检测 " + metricName + " 是否持续超阈值 (" + threshold + ")",
                "最大值为 " + String.format("%.1f", maxValue) + "，超过阈值 " + String.format("%.1f%%", (maxValue / threshold - 1) * 100),
                Math.min(95.0, 70.0 + severity * 0.25)));

        steps.add(new ReasoningStep(3,
                "判定异常类型",
                "持续超阈值 " + consecutiveCount + " 个采集点，判定为 " + EvidenceType.PERFORMANCE + " 异常",
                85.0));

        steps.add(new ReasoningStep(4,
                "生成解释：指标 " + metricName + " 在时间窗口内持续在高位运行",
                "根因定位：Resource " + rootResourceId + " 可能存在性能瓶颈",
                70.0));

        log.debug("Built {} reasoning steps for metric={}", steps.size(), metricName);
        return steps;
    }

    /**
     * 生成依赖异常的推理链
     */
    public List<ReasoningStep> buildDependencyChain(String metricName, double severity,
                                                   int consecutiveCount, String rootResourceId,
                                                   String targetResource) {
        List<ReasoningStep> steps = new ArrayList<>();

        steps.add(new ReasoningStep(1,
                "查询 Resource " + rootResourceId + " 的依赖关系",
                "发现依赖 Resource: " + targetResource,
                90.0));

        steps.add(new ReasoningStep(2,
                "检测依赖指标 " + metricName,
                "依赖项异常，连续 " + consecutiveCount + " 次",
                85.0));

        steps.add(new ReasoningStep(3,
                "判定为 DEPENDENCY 异常",
                "Resource " + targetResource + " 可能影响 " + rootResourceId,
                75.0));

        return steps;
    }

    /**
     * 通用推理链（未知类型兜底）
     */
    public List<ReasoningStep> buildGenericChain(String resourceId, String title) {
        List<ReasoningStep> steps = new ArrayList<>();
        steps.add(new ReasoningStep(1,
                "查询 Resource " + resourceId + " 的时序和拓扑数据",
                "获取到相关数据",
                80.0));
        steps.add(new ReasoningStep(2,
                "分析异常模式",
                title,
                60.0));
        return steps;
    }
}
