package com.aipe.connector.jvm.config;

import com.aipe.connector.sdk.config.ConnectorConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JvmConfig {
    private String connectorId;
    private String targetResource;
    private Boolean memoryEnabled;
    private Boolean gcEnabled;
    private Boolean threadEnabled;
    private Boolean cpuEnabled;
    private Boolean classloaderEnabled;
    private Boolean runtimeEnabled;
    private Long intervalMs;
    private Long timeoutMs;

    public static JvmConfig defaultConfig() {
        return JvmConfig.builder()
                .targetResource("jvm-local")
                .memoryEnabled(true)
                .gcEnabled(true)
                .threadEnabled(true)
                .cpuEnabled(true)
                .classloaderEnabled(true)
                .runtimeEnabled(true)
                .intervalMs(30000L)
                .timeoutMs(10000L)
                .build();
    }

    public static JvmConfig fromConnectorConfig(ConnectorConfig config) {
        JvmConfig jvmConfig = defaultConfig();
        jvmConfig.setConnectorId(config.getConnectorId());
        if (config.getTargetResource() != null) jvmConfig.setTargetResource(config.getTargetResource());
        if (config.getIntervalMs() != null) jvmConfig.setIntervalMs(config.getIntervalMs());
        if (config.getTimeoutMs() != null) jvmConfig.setTimeoutMs(config.getTimeoutMs());
        if (config.getProperties() != null) {
            String mem = config.getProperties().get("memoryEnabled");
            if (mem != null) jvmConfig.setMemoryEnabled(Boolean.parseBoolean(mem));
            String gc = config.getProperties().get("gcEnabled");
            if (gc != null) jvmConfig.setGcEnabled(Boolean.parseBoolean(gc));
            String thread = config.getProperties().get("threadEnabled");
            if (thread != null) jvmConfig.setThreadEnabled(Boolean.parseBoolean(thread));
            String cpu = config.getProperties().get("cpuEnabled");
            if (cpu != null) jvmConfig.setCpuEnabled(Boolean.parseBoolean(cpu));
            String cl = config.getProperties().get("classloaderEnabled");
            if (cl != null) jvmConfig.setClassloaderEnabled(Boolean.parseBoolean(cl));
            String rt = config.getProperties().get("runtimeEnabled");
            if (rt != null) jvmConfig.setRuntimeEnabled(Boolean.parseBoolean(rt));
        }
        return jvmConfig;
    }
}
