package com.aipe.agent.lifecycle;

import com.aipe.agent.runtime.AgentRuntime;
import com.aipe.common.enums.AgentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Agent 生命周期管理器
 *
 * <p>与 AgentRuntime 状态联动，提供生命周期钩子。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class AgentLifecycleManager {

    private static final Logger log = LoggerFactory.getLogger(AgentLifecycleManager.class);

    private final AgentRuntime runtime;

    public AgentLifecycleManager(AgentRuntime runtime) {
        this.runtime = runtime;
    }

    /**
     * Agent 初始化时调用
     */
    public void onInit() {
        log.info("Agent lifecycle: onInit");
        // 初始化阶段可补充：JVM预热、资源预检查等
        validateRuntime();
    }

    /**
     * Agent 启动时调用
     */
    public void onStart() {
        log.info("Agent lifecycle: onStart, agentId={}", runtime.getConfig().getAgentId());
        validateRuntime();
    }

    /**
     * Agent 停止时调用
     */
    public void onStop() {
        log.info("Agent lifecycle: onStop, agentId={}", runtime.getConfig().getAgentId());
    }

    /**
     * Agent 异常时调用
     *
     * @param throwable 异常
     */
    public void onError(Throwable throwable) {
        log.error("Agent lifecycle: onError, agentId={}", runtime.getConfig().getAgentId(), throwable);
    }

    /**
     * 校验 Runtime 合法性
     */
    private void validateRuntime() {
        if (runtime == null) {
            throw new IllegalStateException("AgentRuntime is null");
        }
        if (runtime.getConfig() == null) {
            throw new IllegalStateException("AgentConfig is null");
        }
        if (runtime.getConfig().getAgentId() == null || runtime.getConfig().getAgentId().trim().isEmpty()) {
            throw new IllegalStateException("AgentId is empty");
        }
        log.debug("AgentRuntime validation passed. agentId={}", runtime.getConfig().getAgentId());
    }

    /**
     * 返回当前 Agent 状态
     */
    public AgentState getCurrentState() {
        return runtime.getState();
    }
}
