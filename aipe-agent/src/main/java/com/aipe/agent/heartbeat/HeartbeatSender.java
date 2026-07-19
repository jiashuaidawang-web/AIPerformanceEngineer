package com.aipe.agent.heartbeat;

import com.aipe.agent.config.AgentConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Agent 心跳发送器
 *
 * <p>定期向 Backend 发送心跳，证明 Agent 存活。
 */
public class HeartbeatSender {
    private static final Logger log = LoggerFactory.getLogger(HeartbeatSender.class);
    private static final long HEARTBEAT_INTERVAL_SECONDS = 30L;

    private final AgentConfig config;
    private final RestTemplate restTemplate;
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public HeartbeatSender(AgentConfig config) {
        this.config = config;
        this.restTemplate = new RestTemplate();
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "heartbeat-sender");
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * 注册 Agent
     */
    public void register() {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("agentId", config.getAgentId());
            request.put("serverId", config.getServerId());
            request.put("hostname", "localhost");
            request.put("ip", "127.0.0.1");

            String url = config.getBackendUrl() + "/api/v1/agents/register";
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            log.info("Agent registered: {}", response);
        } catch (Exception e) {
            log.warn("Failed to register agent: {}", e.getMessage());
        }
    }

    /**
     * 启动心跳
     */
    public void start() {
        if (running.get()) return;
        running.set(true);

        // 先注册
        register();

        // 每 30 秒发送心跳
        scheduler.scheduleAtFixedRate(this::sendHeartbeat,
                HEARTBEAT_INTERVAL_SECONDS, HEARTBEAT_INTERVAL_SECONDS, TimeUnit.SECONDS);
        log.info("HeartbeatSender started, interval={}s", HEARTBEAT_INTERVAL_SECONDS);
    }

    private void sendHeartbeat() {
        try {
            String url = config.getBackendUrl() + "/api/v1/agents/" + config.getAgentId() + "/heartbeat";
            Map<String, Object> response = restTemplate.postForObject(url, null, Map.class);
            log.debug("Heartbeat sent: {}", response);
        } catch (Exception e) {
            log.warn("Failed to send heartbeat: {}", e.getMessage());
        }
    }

    public void stop() {
        running.set(false);
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(3, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            scheduler.shutdownNow();
        }
        log.info("HeartbeatSender stopped");
    }
}
