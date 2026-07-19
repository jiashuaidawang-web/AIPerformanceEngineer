package com.aipe.backend.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Agent 信息
 */
@Data
public class AgentInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String agentId;
    private String serverId;
    private String hostname;
    private String ip;
    private String status; // ONLINE / OFFLINE
    private LocalDateTime lastHeartbeat;
    private LocalDateTime registeredAt;
}
