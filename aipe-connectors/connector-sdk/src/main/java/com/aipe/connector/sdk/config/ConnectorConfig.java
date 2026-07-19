package com.aipe.connector.sdk.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ConnectorConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    private String connectorId;
    private String type;
    private String targetResource;
    private Boolean enabled;
    private Long intervalMs;
    private Long timeoutMs;
    private Map<String, String> properties;
}
