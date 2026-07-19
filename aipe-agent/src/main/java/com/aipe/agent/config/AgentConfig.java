package com.aipe.agent.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Agent 配置模型
 *
 * <p>由 ConfigLoader 从 application.yml 加载。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Agent 唯一标识
     */
    private String agentId;

    /**
     * Server 标识
     */
    private String serverId;

    /**
     * 运行环境 (dev/test/prod)
     */
    private String environment;

    /**
     * Backend 服务地址
     */
    private String backendUrl;

    /**
     * Connector 配置列表
     */
    private List<ConnectorConfigItem> connectors;

    /**
     * 调度线程池大小
     */
    private Integer schedulerPoolSize;

    /**
     * 采集数据发送超时 (毫秒)
     */
    private Long sendTimeoutMs;

    /**
     * Connector 配置单项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConnectorConfigItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * Connector 类型 (JVM/LINUX/REDIS/MYSQL)
         */
        private String type;

        /**
         * 是否启用
         */
        private Boolean enabled;

        /**
         * 采集周期 (毫秒)
         */
        private Long intervalMs;

        /**
         * Connector 自定义参数
         */
        private java.util.Map<String, String> properties;
    }
}
