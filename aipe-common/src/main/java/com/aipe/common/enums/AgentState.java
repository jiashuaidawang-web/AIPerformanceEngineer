package com.aipe.common.enums;

/**
 * Agent 状态枚举
 *
 * <p>状态机: CREATED → INITIALIZING → READY → RUNNING → STOPPING → STOPPED / ERROR
 */
public enum AgentState {

    /** 对象创建 */
    CREATED("CREATED", "Agent已创建"),

    /** 加载配置中 */
    INITIALIZING("INITIALIZING", "Agent初始化中"),

    /** 等待启动 */
    READY("READY", "Agent就绪"),

    /** 正常运行 */
    RUNNING("RUNNING", "Agent运行中"),

    /** 正在关闭 */
    STOPPING("STOPPING", "Agent停止中"),

    /** 已停止 */
    STOPPED("STOPPED", "Agent已停止"),

    /** 异常 */
    ERROR("ERROR", "Agent异常");

    private final String code;
    private final String description;

    AgentState(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRunning() {
        return this == RUNNING;
    }

    public boolean isStopped() {
        return this == STOPPED || this == ERROR;
    }
}
