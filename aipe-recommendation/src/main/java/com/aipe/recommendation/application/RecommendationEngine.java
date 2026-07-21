package com.aipe.recommendation.application;

import com.aipe.recommendation.domain.Priority;
import com.aipe.recommendation.domain.Recommendation;
import com.aipe.recommendation.domain.RecommendationBuilder;
import com.aipe.recommendation.domain.RecommendationId;
import com.aipe.recommendation.domain.RecommendationRepository;
import com.aipe.recommendation.domain.RecommendationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Recommendation 引擎（Application 层核心）
 *
 * <p>对齐 WP017 Blueprint §4.2 RecommendationEngine
 *
 * <p>核心职责：把 Knowledge 转化为针对具体 Resource 的优化建议。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Service
public class RecommendationEngine {

    private static final Logger log = LoggerFactory.getLogger(RecommendationEngine.class);

    @Autowired
    private RecommendationRepository recommendationRepository;

    /**
     * 从 Knowledge 生成针对 Resource 的 Recommendation
     *
     * <p>对齐 Blueprint §5 generateFromKnowledge(Knowledge, ResourceId)
     *
     * @param knowledgeId      来源 Knowledge ID
     * @param targetResourceId 目标 Resource ID
     * @param title            推荐标题
     * @param description      推荐描述
     * @param confidence       置信度
     * @param expectedOutcome  预期效果
     * @return 生成的 Recommendation（PENDING）
     */
    public Recommendation generateFromKnowledge(String knowledgeId, String targetResourceId,
                                                String title, String description,
                                                double confidence, String expectedOutcome) {
        // 计算优先级
        Priority priority = calculatePriority(confidence, 5, 3); // 默认 urgency=5, difficulty=3

        // 生成执行计划
        List<String> executionPlan = generateExecutionPlan(title, description);
        List<String> rollbackPlan = generateRollbackPlan(title);

        Recommendation recommendation = RecommendationBuilder.build(
                RecommendationId.generate(), knowledgeId, targetResourceId,
                title, description, priority, confidence, expectedOutcome,
                executionPlan, rollbackPlan, RecommendationStatus.PENDING);

        Recommendation saved = recommendationRepository.save(recommendation);
        log.info("Generated Recommendation: id={}, knowledgeId={}, targetResourceId={}, priority={}",
                saved.getRecommendationId(), knowledgeId, targetResourceId, priority);
        return saved;
    }

    /**
     * 计算优先级（置信度 × 紧急度 × 难度）
     *
     * <p>对齐 Blueprint §5 calculatePriority
     *
     * @param confidence 置信度 (0~100)
     * @param urgency    紧急度 (1~10)
     * @param difficulty 难度 (1~10，越高越难)
     * @return Priority
     */
    public Priority calculatePriority(double confidence, int urgency, int difficulty) {
        // 优先级分数 = 置信度 × 紧急度 / 难度
        double score = (confidence / 100.0) * urgency / Math.max(difficulty, 1) * 100;

        if (score >= 60.0) return Priority.HIGH;
        if (score >= 30.0) return Priority.MEDIUM;
        return Priority.LOW;
    }

    /**
     * 生成执行计划（步骤 + 回滚方案）
     *
     * <p>对齐 Blueprint §5 generateExecutionPlan
     */
    public List<String> generateExecutionPlan(String title, String description) {
        List<String> plan = new ArrayList<>();
        plan.add("1. 评估当前系统状态和基线指标");
        plan.add("2. 备份当前配置（快照）");
        plan.add("3. 执行优化操作: " + (title != null ? title : "配置调整"));
        plan.add("4. 验证优化效果（对比基线）");
        plan.add("5. 记录执行结果和指标变化");
        return plan;
    }

    /**
     * 生成回滚方案
     */
    public List<String> generateRollbackPlan(String title) {
        return Arrays.asList(
                "1. 停止当前优化操作",
                "2. 恢复备份的配置快照",
                "3. 验证系统恢复到基线状态",
                "4. 记录回滚原因和时间"
        );
    }

    /**
     * 批量生成 Recommendation（按优先级排序）
     *
     * <p>对齐 Blueprint §5 recommendBatch
     */
    public List<Recommendation> recommendBatch(List<String[]> knowledgeResourcePairs) {
        List<Recommendation> recommendations = new ArrayList<>();
        for (String[] pair : knowledgeResourcePairs) {
            if (pair.length >= 6) {
                try {
                    Recommendation rec = generateFromKnowledge(
                            pair[0], pair[1], pair[2], pair[3],
                            Double.parseDouble(pair[4]), pair[5]);
                    recommendations.add(rec);
                } catch (Exception e) {
                    log.warn("Failed to generate recommendation for knowledge={}: {}", pair[0], e.getMessage());
                }
            }
        }
        // 按优先级排序（HIGH > MEDIUM > LOW）
        recommendations.sort(Comparator.comparingInt(r -> {
            switch (r.getPriority()) {
                case HIGH: return 0;
                case MEDIUM: return 1;
                case LOW: return 2;
                default: return 3;
            }
        }));
        return recommendations;
    }

    /**
     * 审批通过
     */
    public boolean approve(RecommendationId id) {
        Recommendation rec = recommendationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recommendation not found: " + id));
        Recommendation approved = rec.approve();
        return recommendationRepository.updateStatus(approved.getRecommendationId(), RecommendationStatus.APPROVED);
    }

    /**
     * 审批拒绝
     */
    public boolean reject(RecommendationId id) {
        Recommendation rec = recommendationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recommendation not found: " + id));
        Recommendation rejected = rec.reject();
        return recommendationRepository.updateStatus(rejected.getRecommendationId(), RecommendationStatus.REJECTED);
    }

    /**
     * 标记已执行
     */
    public boolean markExecuted(RecommendationId id) {
        Recommendation rec = recommendationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recommendation not found: " + id));
        Recommendation executed = rec.markExecuted();
        return recommendationRepository.updateStatus(executed.getRecommendationId(), RecommendationStatus.EXECUTED);
    }

    /**
     * 按 Resource 查询
     */
    public List<Recommendation> findByResource(String resourceId) {
        return recommendationRepository.findByResource(resourceId);
    }

    /**
     * 按状态查询
     */
    public List<Recommendation> findByStatus(RecommendationStatus status) {
        return recommendationRepository.findByStatus(status);
    }

    /**
     * 高优先级查询
     */
    public List<Recommendation> findHighPriority(double minConfidence) {
        return recommendationRepository.findHighPriority(minConfidence);
    }
}
