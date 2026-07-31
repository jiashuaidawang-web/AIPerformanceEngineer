package com.aipe.agent.bootstrap;

import com.aipe.agent.config.AgentConfig;
import com.aipe.agent.config.AgentConfigException;
import com.aipe.agent.config.ConfigLoader;
import com.aipe.agent.runtime.AgentRuntime;
import com.aipe.common.enums.AgentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Agent 启动入口
 *
 * <p>负责：加载配置 → 构建 AgentRuntime → 注册 Shutdown Hook → 启动。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class AgentBootstrap {

    private static final Logger log = LoggerFactory.getLogger(AgentBootstrap.class);

    private AgentRuntime runtime;

    /**
     * 启动 Agent
     */
    public void boot() {
        log.info("============================================");
        log.info("  AI Performance Engineer Agent Starting...");
        log.info("============================================");
        long startTime = System.currentTimeMillis();

        try {
            // Step 1: 加载配置
            ConfigLoader configLoader = new ConfigLoader();
            AgentConfig config = configLoader.load();
            log.info("[Bootstrap] Config loaded. agentId={}, environment={}, connectors={}",
                    config.getAgentId(), config.getEnvironment(),
                    config.getConnectors() != null ? config.getConnectors().size() : 0);

            // Step 2: 构建 Agent Runtime
            runtime = new AgentRuntime(config);

            // Step 3: 注册 Shutdown Hook
            registerShutdownHook();

            // Step 4: 启动 Runtime
            runtime.start();

            long elapsed = System.currentTimeMillis() - startTime;
            log.info("============================================");
            log.info("  Agent Started Successfully");
            log.info("  Agent ID     : {}", config.getAgentId());
            log.info("  Server ID    : {}", config.getServerId());
            log.info("  Runtime State: {}", runtime.getState().getCode());
            log.info("  Connectors   : {}", runtime.getConnectorRegistry().size());
            log.info("  Backend URL  : {}", config.getBackendUrl());
            log.info("  Boot Time    : {}ms", elapsed);
            log.info("============================================");
        } catch (AgentConfigException e) {
            log.error("[Bootstrap] Fatal: configuration error - {}", e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            log.error("[Bootstrap] Fatal: agent startup failed", e);
            if (runtime != null) {
                try {
                    runtime.stop();
                } catch (Exception ignored) {
                }
            }
            System.exit(1);
        }
    }

    /**
     * 注册 JVM Shutdown Hook，实现优雅停机
     */
    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("[Bootstrap] Shutdown hook triggered. Stopping Agent...");
            if (runtime != null) {
                try {
                    runtime.stop();
                    log.info("[Bootstrap] Agent stopped gracefully.");
                } catch (Exception e) {
                    log.error("[Bootstrap] Error during shutdown", e);
                }
            }
        }, "agent-shutdown-hook"));
        log.info("[Bootstrap] Shutdown hook registered");
    }

    /**
     * main 入口
     */
    public static void main(String[] args) {
        AgentBootstrap bootstrap = new AgentBootstrap();
        bootstrap.boot();

        // 保持主线程存活，防止 JVM 退出
        while (true) {
            try {
                Thread.sleep(60000); // 每分钟检查一次
                if (bootstrap.getStatus() == AgentState.ERROR) {
                    log.warn("Agent in ERROR state, exiting for restart");
                    System.exit(1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * 返回运行时状态（供外部检查）
     */
    public AgentState getStatus() {
        if (runtime != null) {
            return runtime.getState();
        }
        return null;
    }
}
