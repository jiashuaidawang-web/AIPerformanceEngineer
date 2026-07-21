package com.aipe.knowledge.application;

import com.aipe.knowledge.domain.Knowledge;
import com.aipe.knowledge.domain.KnowledgeBuilder;
import com.aipe.knowledge.domain.KnowledgeId;
import com.aipe.knowledge.domain.KnowledgeRepository;
import com.aipe.knowledge.domain.KnowledgeSpecification;
import com.aipe.knowledge.domain.KnowledgeType;
import com.aipe.knowledge.domain.Recommendation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Knowledge 引擎（Application 层核心）
 *
 * <p>对齐 WP016 Blueprint §4.2 KnowledgeEngine
 *
 * <p>核心职责：把 Verified Evidence 沉淀为可复用 Knowledge。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Service
public class KnowledgeEngine {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeEngine.class);

    @Autowired
    private KnowledgeRepository knowledgeRepository;

    /**
     * 从 Verified Evidence 构建 Knowledge（自动提取适用条件和推荐方案）
     *
     * <p>对齐 Blueprint §5 buildKnowledge(VerifiedEvidence)
     *
     * @param title               知识标题
     * @param description         知识描述
     * @param knowledgeType       知识类型
     * @param evidenceId          来源 Evidence ID（必填 - Domain Law-001）
     * @param verificationId      验证记录 ID
     * @param confidence          最终可信度
     * @param successRate         历史成功率
     * @param resourceType        适用 Resource 类型
     * @param metricName          适用指标名
     * @param recommendationAction 推荐操作
     * @param expectedEffect      预期效果
     * @return 构建的 Knowledge（version 1）
     */
    public Knowledge buildKnowledge(String title, String description, KnowledgeType knowledgeType,
                                   String evidenceId, String verificationId, double confidence,
                                   double successRate, String resourceType, String metricName,
                                   String recommendationAction, String expectedEffect) {
        Map<String, String> conditions = new HashMap<>();
        if (resourceType != null) conditions.put("resourceType", resourceType);
        if (metricName != null) conditions.put("metricName", metricName);

        Recommendation recommendation = new Recommendation(recommendationAction, expectedEffect, "MEDIUM");

        Knowledge knowledge = KnowledgeBuilder.build(
                KnowledgeId.generate(), title, description, knowledgeType,
                evidenceId, verificationId, confidence,
                conditions, recommendation, successRate);

        Knowledge saved = knowledgeRepository.save(knowledge);
        log.info("Built Knowledge: id={}, title={}, evidenceId={}", saved.getKnowledgeId(), title, evidenceId);
        return saved;
    }

    /**
     * 验证并升级 Knowledge（新 Evidence → 新 version）
     *
     * <p>对齐 Blueprint §5 verifyAndUpgrade
     */
    public Knowledge verifyAndUpgrade(KnowledgeId knowledgeId, Map<String, String> changeSet,
                                      String newEvidenceId, double newConfidence) {
        Knowledge latest = knowledgeRepository.findLatest(knowledgeId)
                .orElseThrow(() -> new IllegalArgumentException("Knowledge not found: " + knowledgeId));

        KnowledgeSpecification.validateForUpgrade(latest, changeSet);

        // 升级：新 version 新记录
        Knowledge upgraded = latest.upgrade(changeSet,
                new Recommendation(
                        changeSet.getOrDefault("recommendationAction", "暂无"),
                        changeSet.getOrDefault("expectedEffect", "暂无"),
                        "MEDIUM"));

        // 用新 Evidence + 新 confidence 覆盖
        Recommendation rec = upgraded.getRecommendation();
        Map<String, String> conditions = new HashMap<>(upgraded.getApplicableConditions());
        if (newEvidenceId != null) conditions.put("latestEvidenceId", newEvidenceId);

        Knowledge newVersion = KnowledgeBuilder.build(
                knowledgeId, upgraded.getTitle(), upgraded.getDescription(),
                upgraded.getKnowledgeType(),
                newEvidenceId != null ? newEvidenceId : upgraded.getEvidenceId(),
                upgraded.getVerificationId(),
                newConfidence > 0 ? newConfidence : upgraded.getConfidence(),
                conditions, rec,
                upgraded.getSuccessRate());

        // 强制 version = latest.version + 1
        Knowledge versioned = KnowledgeBuilder.reconstruct(
                newVersion.getKnowledgeId(), newVersion.getTitle(), newVersion.getDescription(),
                newVersion.getKnowledgeType(), newVersion.getEvidenceId(), newVersion.getVerificationId(),
                newVersion.getConfidence(), newVersion.getApplicableConditions(),
                newVersion.getRecommendation(), newVersion.getSuccessRate(),
                newVersion.getCreatedAt(), latest.getVersion() + 1);

        Knowledge saved = knowledgeRepository.save(versioned);
        log.info("Upgraded Knowledge: id={}, newVersion={}", knowledgeId, latest.getVersion() + 1);
        return saved;
    }

    /**
     * 推荐 Knowledge 应用于 Resource（检查适用条件 + 返回推荐方案）
     *
     * <p>对齐 Blueprint §5 recommendForResource
     *
     * @param knowledgeId  知识 ID
     * @param resourceType Resource 类型
     * @param metricName   指标名（可为 null）
     * @return 推荐方案（如果不适用返回 null）
     */
    public Recommendation recommendForResource(KnowledgeId knowledgeId, String resourceType, String metricName) {
        Knowledge knowledge = knowledgeRepository.findLatest(knowledgeId).orElse(null);
        if (knowledge == null) {
            log.warn("Knowledge not found: {}", knowledgeId);
            return null;
        }

        if (!knowledge.isApplicableTo(resourceType, metricName)) {
            log.info("Knowledge {} not applicable to resourceType={}, metricName={}",
                    knowledgeId, resourceType, metricName);
            return null;
        }

        return knowledge.getRecommendation();
    }

    /**
     * 查询 Knowledge 最新版本
     */
    public java.util.Optional<Knowledge> findLatest(KnowledgeId id) {
        return knowledgeRepository.findLatest(id);
    }

    /**
     * 查询 Knowledge 所有版本 history
     */
    public List<Knowledge> listVersions(KnowledgeId id) {
        return knowledgeRepository.findAllVersions(id);
    }

    /**
     * 按类型查询
     */
    public List<Knowledge> findByType(KnowledgeType type) {
        return knowledgeRepository.findByType(type);
    }

    /**
     * 按最低置信度查询
     */
    public List<Knowledge> findByMinConfidence(double minConfidence) {
        return knowledgeRepository.findByMinConfidence(minConfidence);
    }
}
