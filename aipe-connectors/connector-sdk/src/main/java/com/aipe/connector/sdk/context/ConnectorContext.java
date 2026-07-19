package com.aipe.connector.sdk.context;

import com.aipe.connector.sdk.config.ConnectorConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ConnectorContext implements Serializable {
    private static final long serialVersionUID = 1L;
    private String agentId;
    private String serverId;
    private Long collectIntervalMs;
    private Long collectTimeoutMs;
    private Map<String, String> properties;
    private ConnectorConfig config;
    private ObservationEmitter observationSender;
}
