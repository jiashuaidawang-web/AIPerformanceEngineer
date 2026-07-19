package com.aipe.config.agent;

import com.aipe.config.audit.AuditService;
import com.aipe.config.client.AgentCommandService;
import com.aipe.config.model.AgentCommand;
import com.aipe.config.model.AgentInfo;
import com.aipe.config.registry.AgentRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

public class AgentManager {
    private static final Logger log = LoggerFactory.getLogger(AgentManager.class);

    private final AgentRegistry registry;
    private final AgentCommandService commandService;
    private final AuditService auditService;

    public AgentManager(AgentRegistry registry, AgentCommandService commandService, AuditService auditService) {
        this.registry = registry;
        this.commandService = commandService;
        this.auditService = auditService;
    }

    public String registerAgent(AgentInfo agentInfo) {
        if (agentInfo.getAgentId() == null || agentInfo.getAgentId().isEmpty()) {
            agentInfo.setAgentId(generateAgentId());
        }
        agentInfo.setStatus("ONLINE");
        agentInfo.setRegisteredAt(LocalDateTime.now());
        registry.register(agentInfo);
        auditService.record("REGISTER_AGENT", agentInfo.getAgentId());
        log.info("Agent registered: {}", agentInfo.getAgentId());
        return agentInfo.getAgentId();
    }

    public void removeAgent(String agentId) {
        registry.unregister(agentId);
        auditService.record("REMOVE_AGENT", agentId);
        log.info("Agent removed: {}", agentId);
    }

    public AgentInfo getAgent(String agentId) {
        return registry.get(agentId);
    }

    public List<AgentInfo> listAgents() {
        return registry.getAll();
    }

    public void updateHeartbeat(String agentId) {
        AgentInfo agent = registry.get(agentId);
        if (agent != null) {
            agent.setLastHeartbeat(LocalDateTime.now());
            agent.setStatus("ONLINE");
        }
    }

    public void sendCommand(String agentId, AgentCommand command) {
        commandService.sendCommand(agentId, command);
        auditService.record("SEND_COMMAND", agentId + ":" + command.getType());
    }

    private String generateAgentId() {
        return "agent-" + System.currentTimeMillis();
    }
}
