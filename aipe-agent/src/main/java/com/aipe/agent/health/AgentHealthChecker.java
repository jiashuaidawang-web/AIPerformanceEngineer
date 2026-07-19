package com.aipe.agent.health;

import com.aipe.agent.runtime.AgentRuntime;
import com.aipe.agent.runtime.AgentRuntime.AgentStatus;
import com.aipe.common.enums.AgentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Agent 健康检查器
 *
 * <p>周期性输出 Agent 运行状态日志。
 * 包含状态、Connector 数量、队列积压等关键指标。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class AgentHealthChecker {

    private static final Logger log = LoggerFactory.getLogger(AgentHealthChecker.class);
    private static final long CHECK_INTERVAL_MS = 30000L;

    private final AgentRuntime runtime;
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public AgentHealthChecker(AgentRuntime runtime) {
        this.runtime = runtime;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "agent-health-checker");
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * 启动健康检查
     */
    public void start() {
        if (running.get()) {
            log.warn("AgentHealthChecker already running");
            return;
        }
        running.set(true);
        scheduler.scheduleAtFixedRate(this::doCheck, CHECK_INTERVAL_MS, CHECK_INTERVAL_MS, TimeUnit.MILLISECONDS);
        log.info("AgentHealthChecker started, interval={}ms", CHECK_INTERVAL_MS);
    }

    /**
     * 执行健康检查
     */
    private void doCheck() {
        try {
            AgentStatus status = runtime.getStatus();
            if (status == null) {
                log.warn("[HealthCheck] AgentStatus is null");
                return;
            }
            AgentState state = status.getState();
            String stateStr = state != null ? state.getCode() : "UNKNOWN";
            long uptimeSeconds = status.getStartedTime() != null
                    ? Duration.between(status.getStartedTime(), LocalDateTime.now()).getSeconds()
                    : 0;
            int runningConnectors = status.getRunningConnectorCount();
            int totalConnectors = status.getConnectorCount();

            if (state == AgentState.RUNNING) {
                log.info("[HealthCheck] state={}, agentId={}, uptime={}s, connectors={}/{}",
                        stateStr, status.getAgentId(), uptimeSeconds, runningConnectors, totalConnectors);
            } else if (state == AgentState.ERROR) {
                log.error("[HealthCheck] state={}, agentId={}, FAILED", stateStr, status.getAgentId());
            } else {
                log.info("[HealthCheck] state={}, agentId={}, transitioning", stateStr, status.getAgentId());
            }
        } catch (Exception e) {
            log.error("[HealthCheck] error during health check", e);
        }
    }

    /**
     * 停止健康检查
     */
    public void stop() {
        log.info("Stopping AgentHealthChecker...");
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
        log.info("AgentHealthChecker stopped");
    }

    public boolean isRunning() {
        return running.get();
    }
}
