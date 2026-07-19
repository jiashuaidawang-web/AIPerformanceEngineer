package com.aipe.connector.sdk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * Connector 上下文
 *
 * <p>Agent 通过此对象向 Connector 传递运行时参数。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectorContext implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Agent ID
     */
    private String agentId;

    /**
     * 采集周期 (毫秒)
     */
    private Long collectIntervalMs;

    /**
     * 采集超时 (毫秒)
     */
    private Long collectTimeoutMs;

    /**
     * Connector 自定义配置键值对
     */
    private Map<String, String> properties;
}
