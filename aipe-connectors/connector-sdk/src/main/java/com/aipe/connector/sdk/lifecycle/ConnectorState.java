package com.aipe.connector.sdk.lifecycle;

/**
 * Connector 生命周期状态枚举
 *
 * <p>完整状态机: CREATED → INITIALIZED → STARTING → RUNNING → STOPPING → STOPPED / ERROR
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public enum ConnectorState {

    /** 已创建 */
    CREATED("CREATED", "Connector已创建"),

    /** 已初始化 */
    INITIALIZED("INITIALIZED", "Connector已初始化"),

    /** 启动中 */
    STARTING("STARTING", "Connector启动中"),

    /** 运行中 */
    RUNNING("RUNNING", "Connector运行中"),

    /** 停止中 */
    STOPPING("STOPPING", "Connector停止中"),

    /** 已停止 */
    STOPPED("STOPPED", "Connector已停止"),

    /** 异常 */
    ERROR("ERROR", "Connector异常");

    private final String code;
    private final String description;

    ConnectorState(String code, String description) {
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

    public boolean canStart() {
        return this == INITIALIZED || this == STOPPED;
    }

    public boolean canStop() {
        return this == RUNNING || this == STARTING;
    }
}
