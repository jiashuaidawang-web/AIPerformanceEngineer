package com.aipe.evidence.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * 推理步骤值对象
 *
 * <p>对齐 WP014 Blueprint §4.1 ReasoningStep：step / action / result / confidence
 *
 * <p>持久化为 JSON String 存储在 evidence 表 reasoning_steps 列。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class ReasoningStep implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 步骤序号 */
    private final int step;

    /** 动作描述（自然语言） */
    private final String action;

    /** 步骤结果 */
    private final String result;

    /** 该步骤置信度 */
    private final double confidence;

    public ReasoningStep(int step, String action, String result, double confidence) {
        this.step = step;
        this.action = action != null ? action : "";
        this.result = result != null ? result : "";
        this.confidence = confidence;
    }

    public int getStep() { return step; }
    public String getAction() { return action; }
    public String getResult() { return result; }
    public double getConfidence() { return confidence; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReasoningStep that = (ReasoningStep) o;
        return step == that.step;
    }

    @Override
    public int hashCode() { return Objects.hash(step); }

    @Override
    public String toString() {
        return "ReasoningStep{" + step + ": " + action + " -> " + result + '}';
    }
}
