package com.aipe.backend.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Agent 批量上报 Observation 请求
 */
@Data
public class ObservationBatchRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String agentId;
    private String connectorType;
    private List<Map<String, Object>> observations;
}
