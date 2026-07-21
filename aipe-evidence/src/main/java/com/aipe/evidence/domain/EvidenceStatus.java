package com.aipe.evidence.domain;

/**
 * Evidence 生命周期状态
 *
 * <p>对齐 M2-011 Evidence Model ch7 Schema
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public enum EvidenceStatus {

    /** 新生成（待验证） */
    NEW,

    /** 已验证（通过 Verification 可进入 Knowledge） */
    VERIFIED,

    /** 已拒绝（未通过验证） */
    REJECTED;

    public static EvidenceStatus parse(String value) {
        if (value == null || value.trim().isEmpty()) {
            return NEW;
        }
        try {
            return EvidenceStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return NEW;
        }
    }
}
