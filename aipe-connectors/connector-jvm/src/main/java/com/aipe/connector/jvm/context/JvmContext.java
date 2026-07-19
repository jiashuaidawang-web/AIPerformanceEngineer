package com.aipe.connector.jvm.context;

import com.aipe.connector.jvm.config.JvmConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JVM Connector 运行时上下文
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JvmContext {
    private String agentId;
    private String connectorId;
    private JvmConfig config;
    private long startTime;
}
