package com.aipe.backend.scheduler;

import com.aipe.backend.service.AgentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Agent 心跳超时检测定时任务
 */
@Component
@EnableScheduling
public class AgentHeartbeatScheduler {
    private static final Logger log = LoggerFactory.getLogger(AgentHeartbeatScheduler.class);

    @Autowired
    private AgentService agentService;

    @Value("${aipe.agent.heartbeat-timeout-seconds:90}")
    private int heartbeatTimeoutSeconds;

    /**
     * 每 30 秒检查一次心跳超时
     */
    @Scheduled(fixedRateString = "${aipe.agent.heartbeat-check-interval-seconds:30000}")
    public void checkHeartbeat() {
        agentService.checkHeartbeatTimeout(heartbeatTimeoutSeconds);
    }
}
