package com.aipe.recommendation.domain;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Recommendation 构造器
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class RecommendationBuilder {

    private RecommendationBuilder() {}

    public static Recommendation build(RecommendationId recommendationId,
                                       String knowledgeId,
                                       String targetResourceId,
                                       String title,
                                       String description,
                                       Priority priority,
                                       double confidence,
                                       String expectedOutcome,
                                       List<String> executionPlan,
                                       List<String> rollbackPlan,
                                       RecommendationStatus status) {
        Recommendation rec = new Recommendation(recommendationId, knowledgeId, targetResourceId,
                title, description, priority, confidence, expectedOutcome,
                executionPlan, rollbackPlan, status,
                LocalDateTime.now(), LocalDateTime.now(), 1);
        RecommendationSpecification.validateForCreate(rec);
        return rec;
    }

    public static Recommendation reconstruct(RecommendationId recommendationId,
                                             String knowledgeId,
                                             String targetResourceId,
                                             String title,
                                             String description,
                                             Priority priority,
                                             double confidence,
                                             String expectedOutcome,
                                             List<String> executionPlan,
                                             List<String> rollbackPlan,
                                             RecommendationStatus status,
                                             LocalDateTime createdAt,
                                             LocalDateTime updatedAt,
                                             int version) {
        return new Recommendation(recommendationId, knowledgeId, targetResourceId,
                title, description, priority, confidence, expectedOutcome,
                executionPlan, rollbackPlan, status, createdAt, updatedAt, version);
    }
}
