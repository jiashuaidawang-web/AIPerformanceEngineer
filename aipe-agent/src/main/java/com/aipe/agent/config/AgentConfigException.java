package com.aipe.agent.config;

/**
 * Agent 配置异常
 *
 * <p>配置文件加载失败或配置非法时抛出。
 * 将导致 Agent 终止启动。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class AgentConfigException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AgentConfigException(String message) {
        super(message);
    }

    public AgentConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
