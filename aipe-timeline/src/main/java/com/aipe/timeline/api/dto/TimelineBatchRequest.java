package com.aipe.timeline.api.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 多指标 Timeline 批量请求 DTO
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class TimelineBatchRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String resourceId;
    private List<String> metricNames;
    private Long startTime;
    private Long endTime;
    private Integer limit = 10000;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public List<String> getMetricNames() {
        return metricNames;
    }

    public void setMetricNames(List<String> metricNames) {
        this.metricNames = metricNames;
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
