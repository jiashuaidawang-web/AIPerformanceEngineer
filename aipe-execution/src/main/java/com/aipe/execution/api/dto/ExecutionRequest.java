package com.aipe.execution.api.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * Execution 创建请求 DTO
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class ExecutionRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "recommendationId is required")
    private String recommendationId;

    @NotBlank(message = "executor is required")
    private String executor;

    private String executionType = "MANUAL";

    /** 执行前快照 JSON（可选，不传则自动生成） */
    private String beforeSnapshot;

    public String getRecommendationId() { return recommendationId; }
    public void setRecommendationId(String recommendationId) { this.recommendationId = recommendationId; }
    public String getExecutor() { return executor; }
    public void setExecutor(String executor) { this.executor = executor; }
    public String getExecutionType() { return executionType; }
    public void setExecutionType(String executionType) { this.executionType = executionType; }
    public String getBeforeSnapshot() { return beforeSnapshot; }
    public void setBeforeSnapshot(String beforeSnapshot) { this.beforeSnapshot = beforeSnapshot; }
}
