package com.aipe.timeline.api.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Timeline 查询请求 DTO
 *
 * <p>对齐 WP014 Blueprint §7: GET /api/v1/timelines
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class TimelineRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "resourceId is required")
    private String resourceId;

    private String metricName;

    /** 开始时间（毫秒 or 秒，自动识别） */
    @NotNull(message = "startTime is required")
    private Long startTime;

    /** 结束时间（毫秒 or 秒，自动识别） */
    @NotNull(message = "endTime is required")
    private Long endTime;

    private Integer limit = 10000;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
