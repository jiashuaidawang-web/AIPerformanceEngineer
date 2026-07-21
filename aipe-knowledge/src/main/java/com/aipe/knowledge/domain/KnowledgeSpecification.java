package com.aipe.knowledge.domain;

import java.util.Map;

/**
 * Knowledge 规格校验
 *
 * <p>对齐 Blueprint section 9.1: Knowledge 来源 Verified Evidence（evidenceId 必填）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class KnowledgeSpecification {

    private KnowledgeSpecification() {}

    public static void validateForCreate(Knowledge knowledge) {
        if (knowledge == null) throw new IllegalArgumentException("Knowledge cannot be null");
        knowledge.validate();
    }

    /**
     * 校验 Knowledge 可用于升级（对齐 M2-012 Knowledge Model ch4.5 Evolvable）
     */
    public static void validateForUpgrade(Knowledge existing, Map<String, String> changeSet) {
        if (existing == null) throw new IllegalArgumentException("Cannot upgrade null Knowledge");
        if (changeSet == null || changeSet.isEmpty()) {
            throw new IllegalArgumentException("ChangeSet cannot be empty for upgrade");
        }
    }
}
