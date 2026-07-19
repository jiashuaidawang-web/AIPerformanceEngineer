package com.aipe.config.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class AgentInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String agentId;
    private String serverId;
    private String hostname;
    private String ip;
    private String version;
    private String status;
    private LocalDateTime registeredAt;
    private LocalDateTime lastHeartbeat;
}
