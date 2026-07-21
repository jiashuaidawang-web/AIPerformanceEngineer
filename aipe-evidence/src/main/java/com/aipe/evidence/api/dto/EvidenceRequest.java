package com.aipe.evidence.api.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Evidence 生成请求 DTO
 *
 * <p>对齐 WP014 Blueprint §7 POST /api/v1/evidences/generate
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class EvidenceRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "resourceId is required")
    private String resourceId;

    private String metricName;

    @NotNull(message = "startTime is required")
    private Long startTime;

    @NotNull(message = "endTime is required")
    private Long endTime;

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public String getMetricName() { return metricName; }
    public void setMetricName(String metricName) { this.metricName = metricName; }
    public Long getStartTime() { return startTime; }
    public void setStartTime(Long startTime) { this.startTime = startTime; }
    public Long getEndTime() { return endTime; }
    public void setEndTime(Long endTime) { this.endTime = endTime; }
}
