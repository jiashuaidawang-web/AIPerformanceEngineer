package com.aipe.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Observation 查询响应
 */
@Data
@AllArgsConstructor
public class ObservationQueryResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private String resourceId;
    private String metricName;
    private List<Map<String, Object>> data;
}
