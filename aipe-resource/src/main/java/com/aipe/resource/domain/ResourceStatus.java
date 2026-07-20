package com.aipe.resource.domain;

/**
 * 资源状态枚举
 *
 * <p>资源生命周期状态，对齐 WP011 Blueprint 状态机
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public enum ResourceStatus {

    /**
     * 运行中
     */
    RUNNING,

    /**
     * 已停止
     */
    STOPPED,

    /**
     * 维护中
     */
    MAINTENANCE,

    /**
     * 未知
     */
    UNKNOWN;

    /**
     * 判断是否可以流转到目标状态
     *
     * <p>合法流转规则（对齐 WP011 状态机）：
     * <ul>
     *   <li>RUNNING → MAINTENANCE / STOPPED</li>
     *   <li>MAINTENANCE → RUNNING / STOPPED</li>
     *   <li>STOPPED → RUNNING</li>
     *   <li>UNKNOWN → 任何状态</li>
     * </ul>
     *
     * @param target 目标状态
     * @return 是否允许流转
     */
    public boolean canTransitionTo(ResourceStatus target) {
        if (this == target) {
            return true;
        }
        switch (this) {
            case RUNNING:
                return target == MAINTENANCE || target == STOPPED;
            case MAINTENANCE:
                return target == RUNNING || target == STOPPED;
            case STOPPED:
                return target == RUNNING;
            case UNKNOWN:
                return true;
            default:
                return false;
        }
    }
}
