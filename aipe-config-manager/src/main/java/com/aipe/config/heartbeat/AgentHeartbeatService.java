package com.aipe.config.heartbeat;
import com.aipe.config.agent.AgentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class AgentHeartbeatService {
    private static final Logger log = LoggerFactory.getLogger(AgentHeartbeatService.class);
    private final AgentManager agentManager;
    public AgentHeartbeatService(AgentManager agentManager) { this.agentManager = agentManager; }
    public void heartbeat(String agentId, Object request) { agentManager.updateHeartbeat(agentId); log.debug("Heartbeat: agentId={}", agentId); }
}
