package com.aipe.connector.linux.config;

import com.aipe.connector.sdk.config.ConnectorConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinuxConfig {
    private String connectorId;
    private String targetResource;
    private String procPath;
    private Boolean cpuEnabled;
    private Boolean memoryEnabled;
    private Boolean diskEnabled;
    private Boolean networkEnabled;
    private Boolean tcpEnabled;
    private Boolean loadEnabled;
    private Boolean processEnabled;
    private Long intervalMs;
    private Long timeoutMs;

    public static LinuxConfig defaultConfig() {
        return LinuxConfig.builder()
                .procPath("/proc")
                .targetResource("linux-local")
                .cpuEnabled(true)
                .memoryEnabled(true)
                .diskEnabled(true)
                .networkEnabled(true)
                .tcpEnabled(true)
                .loadEnabled(true)
                .processEnabled(true)
                .intervalMs(30000L)
                .timeoutMs(10000L)
                .build();
    }

    public static LinuxConfig fromConnectorConfig(ConnectorConfig config) {
        LinuxConfig lc = defaultConfig();
        lc.setConnectorId(config.getConnectorId());
        if (config.getTargetResource() != null) lc.setTargetResource(config.getTargetResource());
        if (config.getIntervalMs() != null) lc.setIntervalMs(config.getIntervalMs());
        if (config.getTimeoutMs() != null) lc.setTimeoutMs(config.getTimeoutMs());
        if (config.getProperties() != null) {
            String v;
            if ((v = config.getProperties().get("procPath")) != null) lc.setProcPath(v);
            if ((v = config.getProperties().get("cpuEnabled")) != null) lc.setCpuEnabled(Boolean.parseBoolean(v));
            if ((v = config.getProperties().get("memoryEnabled")) != null) lc.setMemoryEnabled(Boolean.parseBoolean(v));
            if ((v = config.getProperties().get("diskEnabled")) != null) lc.setDiskEnabled(Boolean.parseBoolean(v));
            if ((v = config.getProperties().get("networkEnabled")) != null) lc.setNetworkEnabled(Boolean.parseBoolean(v));
            if ((v = config.getProperties().get("tcpEnabled")) != null) lc.setTcpEnabled(Boolean.parseBoolean(v));
            if ((v = config.getProperties().get("loadEnabled")) != null) lc.setLoadEnabled(Boolean.parseBoolean(v));
            if ((v = config.getProperties().get("processEnabled")) != null) lc.setProcessEnabled(Boolean.parseBoolean(v));
        }
        return lc;
    }
}
