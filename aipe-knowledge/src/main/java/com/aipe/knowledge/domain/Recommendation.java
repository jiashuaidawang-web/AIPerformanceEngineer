package com.aipe.knowledge.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * 推荐方案值对象
 *
 * <p>对齐 WP014 Blueprint §4.1 Recommendation：操作内容 + 预期效果
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class Recommendation implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 推荐操作内容 */
    private final String action;

    /** 预期效果 */
    private final String expectedEffect;

    /** 风险等级（LOW / MEDIUM / HIGH） */
    private final String riskLevel;

    public Recommendation(String action, String expectedEffect, String riskLevel) {
        this.action = action != null ? action : "";
        this.expectedEffect = expectedEffect != null ? expectedEffect : "";
        this.riskLevel = riskLevel != null ? riskLevel : "MEDIUM";
    }

    public String getAction() { return action; }
    public String getExpectedEffect() { return expectedEffect; }
    public String getRiskLevel() { return riskLevel; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recommendation that = (Recommendation) o;
        return Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() { return Objects.hash(action); }

    @Override
    public String toString() {
        return "Recommendation{action='" + action + "', effect='" + expectedEffect + "'}";
    }
}
