package com.aipe.agent.runtime;

import com.aipe.agent.config.AgentConfig;
import com.aipe.agent.connector.ConnectorManager;
import com.aipe.agent.connector.ConnectorRegistry;
import com.aipe.agent.event.AgentEventBus;
import com.aipe.agent.health.AgentHealthChecker;
import com.aipe.agent.heartbeat.HeartbeatSender;
import com.aipe.agent.lifecycle.AgentLifecycleManager;
import com.aipe.agent.observation.HttpObservationSender;
import com.aipe.agent.resource.ResourceRegistrar;
import com.aipe.agent.scheduler.SchedulerManager;
import com.aipe.common.enums.AgentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Agent 核心运行上下文
 */
public class AgentRuntime {

    private static final Logger log = LoggerFactory.getLogger(AgentRuntime.class);

    private final AgentConfig config;
    private final ConnectorRegistry connectorRegistry;
    private final ConnectorManager connectorManager;
    private final SchedulerManager schedulerManager;
    private final HttpObservationSender observationSender;
    private final AgentEventBus eventBus;
    private final AgentHealthChecker healthChecker;
    private final AgentLifecycleManager lifecycleManager;
    private final HeartbeatSender heartbeatSender;
    private final ResourceRegistrar resourceRegistrar;
    private final AtomicReference<AgentState> state = new AtomicReference<>(AgentState.CREATED);
    private LocalDateTime startedTime;

    public AgentRuntime(AgentConfig config) {
        this.config = config;
        this.connectorRegistry = new ConnectorRegistry();
        this.observationSender = new HttpObservationSender(config);
        this.eventBus = new AgentEventBus();
        this.schedulerManager = new SchedulerManager(config.getSchedulerPoolSize());
        this.connectorManager = new ConnectorManager(connectorRegistry, schedulerManager, observationSender, eventBus, config);
        this.healthChecker = new AgentHealthChecker(this);
        this.lifecycleManager = new AgentLifecycleManager(this);
        this.heartbeatSender = new HeartbeatSender(config);
        this.resourceRegistrar = new ResourceRegistrar(config);
        log.info("AgentRuntime instantiated for agentId={}", config.getAgentId());
    }

    /**
     * 启动 Agent Runtime
     */
    public synchronized void start() {
        if (state.get() == AgentState.RUNNING) {
            log.warn("Agent is already running, ignoring start request.");
            return;
        }
        log.info("Starting Agent Runtime...");
        state.set(AgentState.INITIALIZING);

        try {
            // 1. 初始化 Observation Sender
            observationSender.init();

            // 2. 初始化 Scheduler
            schedulerManager.init();

            // 3. 初始化 Event Bus
            eventBus.init();

            // 4. 初始化并启动 Connector Manager
            connectorManager.init();
            connectorManager.startAll();

            // 4.5 自动注册资源到 Resource Engine
            registerResources();

            // 5. 启动健康检查
            healthChecker.start();

            // 6. 启动心跳
            heartbeatSender.start();

            state.set(AgentState.RUNNING);
            startedTime = LocalDateTime.now();
            log.info("Agent Runtime started successfully. agentId={}, state={}", config.getAgentId(), state.get());
        } catch (Exception e) {
            log.error("Failed to start Agent Runtime", e);
            state.set(AgentState.ERROR);
            throw new RuntimeException("Agent start failed", e);
        }
    }

    /**
     * 自动注册资源
     */
    private void registerResources() {
        try {
            log.info("Auto-discovering and registering resources...");
            // 注册 Agent 发现的资源
            String[][] resources = {
                {"jvm-local", "JVM", "JVM进程"},
                {"linux-local", "LINUX", "Linux服务器"},
                {"mysql-node", "MYSQL", "MySQL数据库"},
                {"redis-node", "REDIS", "Redis缓存"},
            };
            resourceRegistrar.registerResources(resources);
            log.info("Resource auto-registration completed.");
        } catch (Exception e) {
            log.warn("Resource auto-registration failed: {}", e.getMessage());
        }
    }

    /**
     * 停止 Agent
     */
    public synchronized void stop() {
        if (state.get() == AgentState.STOPPING || state.get() == AgentState.STOPPED) {
            log.warn("Agent is already stopping or stopped.");
            return;
        }
        log.info("Stopping Agent Runtime...");
        state.set(AgentState.STOPPING);

        try {
            // 1. 停止心跳
            heartbeatSender.stop();

            // 2. 停止健康检查
            healthChecker.stop();

            // 3. 停止所有 Connector
            connectorManager.stopAll();

            // 4. 停止调度器
            schedulerManager.shutdown();

            // 5. 停止 Observation Sender
            observationSender.shutdown();

            state.set(AgentState.STOPPED);
            log.info("Agent Runtime stopped. agentId={}", config.getAgentId());
        } catch (Exception e) {
            log.error("Error during Agent stop", e);
            state.set(AgentState.ERROR);
        }
    }

    /**
     * 获取 Agent 状态信息
     */
    public AgentStatus getStatus() {
        AgentStatus status = new AgentStatus();
        status.setAgentId(config.getAgentId());
        status.setServerId(config.getServerId());
        status.setState(state.get());
        status.setStartedTime(startedTime);
        status.setConnectorCount(connectorRegistry.size());
        status.setRunningConnectorCount(connectorManager.getRunningCount());
        return status;
    }

    public AgentConfig getConfig() { return config; }
    public AgentState getState() { return state.get(); }
    public ConnectorRegistry getConnectorRegistry() { return connectorRegistry; }
    public ConnectorManager getConnectorManager() { return connectorManager; }
    public SchedulerManager getSchedulerManager() { return schedulerManager; }
    public HttpObservationSender getObservationSender() { return observationSender; }
    public AgentEventBus getEventBus() { return eventBus; }
    public AgentHealthChecker getHealthChecker() { return healthChecker; }
    public AgentLifecycleManager getLifecycleManager() { return lifecycleManager; }
    public HeartbeatSender getHeartbeatSender() { return heartbeatSender; }
    public LocalDateTime getStartedTime() { return startedTime; }

    /**
     * Agent 状态 DTO
     */
    public static class AgentStatus {
        private String agentId;
        private String serverId;
        private AgentState state;
        private LocalDateTime startedTime;
        private int connectorCount;
        private int runningConnectorCount;

        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
        public String getServerId() { return serverId; }
        public void setServerId(String serverId) { this.serverId = serverId; }
        public AgentState getState() { return state; }
        public void setState(AgentState state) { this.state = state; }
        public LocalDateTime getStartedTime() { return startedTime; }
        public void setStartedTime(LocalDateTime startedTime) { this.startedTime = startedTime; }
        public int getConnectorCount() { return connectorCount; }
        public void setConnectorCount(int connectorCount) { this.connectorCount = connectorCount; }
        public int getRunningConnectorCount() { return runningConnectorCount; }
        public void setRunningConnectorCount(int runningConnectorCount) { this.runningConnectorCount = runningConnectorCount; }
    }
}
