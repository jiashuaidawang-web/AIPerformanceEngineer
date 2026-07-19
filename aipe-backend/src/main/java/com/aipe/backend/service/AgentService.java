package com.aipe.backend.service;

import com.aipe.backend.dto.AgentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Agent 服务
 *
 * <p>MVP 阶段使用内存存储，后续替换为 MySQL 持久化。
 */
@Service
public class AgentService {
    private static final Logger log = LoggerFactory.getLogger(AgentService.class);

    private final Map<String, AgentInfo> agents = new ConcurrentHashMap<>();

    public String registerAgent(AgentInfo agentInfo) {
        if (agentInfo.getAgentId() == null || agentInfo.getAgentId().isEmpty()) {
            agentInfo.setAgentId("agent-" + System.currentTimeMillis());
        }
        agentInfo.setStatus("ONLINE");
        agentInfo.setRegisteredAt(LocalDateTime.now());
        agentInfo.setLastHeartbeat(LocalDateTime.now());
        agents.put(agentInfo.getAgentId(), agentInfo);
        return agentInfo.getAgentId();
    }

    public void updateHeartbeat(String agentId) {
        AgentInfo agent = agents.get(agentId);
        if (agent != null) {
            agent.setLastHeartbeat(LocalDateTime.now());
            agent.setStatus("ONLINE");
            log.debug("Heartbeat updated: agentId={}", agentId);
        }
    }

    public AgentInfo getAgent(String agentId) {
        return agents.get(agentId);
    }

    public List<AgentInfo> listAgents() {
        return new ArrayList<>(agents.values());
    }

    /**
     * 检查心跳超时的 Agent（供定时任务调用）
     */
    public void checkHeartbeatTimeout(int timeoutSeconds) {
        LocalDateTime now = LocalDateTime.now();
        for (AgentInfo agent : agents.values()) {
            if (agent.getStatus().equals("ONLINE") && agent.getLastHeartbeat() != null) {
                long secondsSinceLastHeartbeat = java.time.temporal.ChronoUnit.SECONDS.between(
                        agent.getLastHeartbeat(), now);
                if (secondsSinceLastHeartbeat > timeoutSeconds) {
                    agent.setStatus("OFFLINE");
                    log.warn("Agent {} is OFFLINE (last heartbeat {}s ago)",
                            agent.getAgentId(), secondsSinceLastHeartbeat);
                }
            }
        }
    }
}
