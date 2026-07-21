package com.aipe.evidence.domain;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Evidence 构造器
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class EvidenceBuilder {

    private EvidenceBuilder() {}

    public static Evidence build(EvidenceId evidenceId,
                                 EvidenceType evidenceType,
                                 String title,
                                 String description,
                                 String rootResourceId,
                                 List<String> observationIds,
                                 List<String> relationshipIds,
                                 String timelineId,
                                 double confidence,
                                 List<ReasoningStep> reasoningSteps,
                                 EvidenceStatus status) {
        Evidence evidence = new Evidence(
                evidenceId, evidenceType, title, description, rootResourceId,
                observationIds, relationshipIds, timelineId, confidence,
                reasoningSteps, status,
                LocalDateTime.now(), LocalDateTime.now(), 1);
        // 对齐 Blueprint §9.3: 生成时 validate() + Specification 校验
        EvidenceSpecification.validateForCreate(evidence);
        return evidence;
    }

    public static Evidence reconstruct(EvidenceId evidenceId,
                                       EvidenceType evidenceType,
                                       String title,
                                       String description,
                                       String rootResourceId,
                                       List<String> observationIds,
                                       List<String> relationshipIds,
                                       String timelineId,
                                       double confidence,
                                       List<ReasoningStep> reasoningSteps,
                                       EvidenceStatus status,
                                       LocalDateTime createdAt,
                                       LocalDateTime updatedAt,
                                       int version) {
        return new Evidence(
                evidenceId, evidenceType, title, description, rootResourceId,
                observationIds, relationshipIds, timelineId, confidence,
                reasoningSteps, status, createdAt, updatedAt, version);
    }
}
