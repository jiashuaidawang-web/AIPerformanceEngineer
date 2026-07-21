package com.aipe.knowledge.domain;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Knowledge 构造器
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class KnowledgeBuilder {

    private KnowledgeBuilder() {}

    public static Knowledge build(KnowledgeId knowledgeId,
                                  String title,
                                  String description,
                                  KnowledgeType knowledgeType,
                                  String evidenceId,
                                  String verificationId,
                                  double confidence,
                                  Map<String, String> applicableConditions,
                                  Recommendation recommendation,
                                  double successRate) {
        Knowledge knowledge = new Knowledge(knowledgeId, title, description, knowledgeType,
                evidenceId, verificationId, confidence, applicableConditions,
                recommendation, successRate, LocalDateTime.now(), 1);
        KnowledgeSpecification.validateForCreate(knowledge);
        return knowledge;
    }

    public static Knowledge reconstruct(KnowledgeId knowledgeId,
                                        String title,
                                        String description,
                                        KnowledgeType knowledgeType,
                                        String evidenceId,
                                        String verificationId,
                                        double confidence,
                                        Map<String, String> applicableConditions,
                                        Recommendation recommendation,
                                        double successRate,
                                        LocalDateTime createdAt,
                                        int version) {
        return new Knowledge(knowledgeId, title, description, knowledgeType,
                evidenceId, verificationId, confidence, applicableConditions,
                recommendation, successRate, createdAt, version);
    }
}
