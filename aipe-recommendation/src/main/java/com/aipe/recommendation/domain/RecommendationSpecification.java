package com.aipe.recommendation.domain;

/**
 * Recommendation 规格校验
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class RecommendationSpecification {

    private RecommendationSpecification() {}

    public static void validateForCreate(Recommendation recommendation) {
        if (recommendation == null) throw new IllegalArgumentException("Recommendation cannot be null");
        recommendation.validate();
    }
}
