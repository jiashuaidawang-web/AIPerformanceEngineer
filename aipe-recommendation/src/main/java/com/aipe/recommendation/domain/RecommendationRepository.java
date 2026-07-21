package com.aipe.recommendation.domain;

import java.util.List;
import java.util.Optional;

/**
 * Recommendation 仓储接口（Domain 层）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public interface RecommendationRepository {

    Recommendation save(Recommendation recommendation);

    Optional<Recommendation> findById(RecommendationId id);

    Optional<Recommendation> findById(String id);

    List<Recommendation> findByResource(String resourceId);

    List<Recommendation> findByStatus(RecommendationStatus status);

    List<Recommendation> findByKnowledgeId(String knowledgeId);

    List<Recommendation> findHighPriority(double minConfidence);

    List<Recommendation> findAll();

    boolean updateStatus(RecommendationId id, RecommendationStatus newStatus);
}
