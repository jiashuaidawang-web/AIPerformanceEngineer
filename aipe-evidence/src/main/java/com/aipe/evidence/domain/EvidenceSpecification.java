package com.aipe.evidence.domain;

/**
 * Evidence 规格校验
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class EvidenceSpecification {

    private EvidenceSpecification() {}

    public static void validateForCreate(Evidence evidence) {
        if (evidence == null) {
            throw new IllegalArgumentException("Evidence cannot be null");
        }
        evidence.validate();
        // 默认置信度不能是固定值 100（必须通过推理路径计算）
        if (evidence.getConfidence() == 100.0 && (evidence.getReasoningSteps() == null || evidence.getReasoningSteps().isEmpty())) {
            throw new IllegalArgumentException(
                    "Confidence 100 requires reasoning steps (Blueprint §9.1: Confidence must be dynamically calculated, not hardcoded)");
        }
    }
}
