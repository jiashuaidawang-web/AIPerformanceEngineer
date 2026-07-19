package com.aipe.connector.jvm;

import com.aipe.common.domain.ObservationData;
import com.aipe.connector.jvm.collector.JvmCollector;
import com.aipe.connector.jvm.collector.memory.MemoryCollector;
import com.aipe.connector.jvm.collector.gc.GcCollector;
import com.aipe.connector.jvm.collector.thread.ThreadCollector;
import com.aipe.connector.jvm.collector.cpu.CpuCollector;
import com.aipe.connector.jvm.collector.classloader.ClassLoaderCollector;
import com.aipe.connector.jvm.collector.runtime.RuntimeCollector;
import com.aipe.connector.jvm.config.JvmConfig;
import com.aipe.connector.jvm.context.JvmContext;
import com.aipe.connector.sdk.abstractapi.AbstractConnector;
import com.aipe.connector.sdk.config.ConnectorConfig;
import com.aipe.connector.sdk.context.ConnectorContext;
import com.aipe.connector.sdk.context.ObservationEmitter;
import com.aipe.connector.sdk.exception.ConnectorException;
import com.aipe.connector.sdk.lifecycle.ConnectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * JVM Connector 主控类
 *
 * <p>负责初始化所有 JVM 采集器，调度周期性采集，将采集结果发送至 Agent。
 * 使用真实 JDK/JMX API，禁止 Mock 数据。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class JvmConnector extends AbstractConnector {

    private static final Logger log = LoggerFactory.getLogger(JvmConnector.class);

    private JvmConfig jvmConfig;
    private final List<JvmCollector> collectors = new ArrayList<>();
    private String agentId;
    private String connectorId;

    @Override
    public String getConnectorId() {
        if (connectorId != null) return connectorId;
        if (jvmConfig != null && jvmConfig.getConnectorId() != null) return jvmConfig.getConnectorId();
        return "jvm-" + System.identityHashCode(this);
    }

    @Override
    public String getConnectorType() {
        return "JVM";
    }

    @Override
    public String getTargetResource() {
        if (jvmConfig != null && jvmConfig.getTargetResource() != null) return jvmConfig.getTargetResource();
        return "jvm-local";
    }

    @Override
    protected void onInit(ConnectorContext context) throws ConnectorException {
        log.info("JvmConnector initializing...");

        if (context != null) {
            this.agentId = context.getAgentId();
            ConnectorConfig sdkConfig = context.getConfig();
            if (sdkConfig != null) {
                this.connectorId = sdkConfig.getConnectorId();
                this.jvmConfig = JvmConfig.fromConnectorConfig(sdkConfig);
            } else {
                this.jvmConfig = JvmConfig.defaultConfig();
            }
        } else {
            this.jvmConfig = JvmConfig.defaultConfig();
        }

        if (this.connectorId == null) {
            this.connectorId = getConnectorId();
        }

        initCollectors();
        log.info("JvmConnector initialized. agentId={}, collectors={}", agentId, collectors.size());
    }

    private void initCollectors() {
        if (jvmConfig.getMemoryEnabled()) collectors.add(new MemoryCollector());
        if (jvmConfig.getGcEnabled()) collectors.add(new GcCollector());
        if (jvmConfig.getThreadEnabled()) collectors.add(new ThreadCollector());
        if (jvmConfig.getCpuEnabled()) collectors.add(new CpuCollector());
        if (jvmConfig.getClassloaderEnabled()) collectors.add(new ClassLoaderCollector());
        if (jvmConfig.getRuntimeEnabled()) collectors.add(new RuntimeCollector());
        log.info("Initialized {} JVM collectors: {}", collectors.size(), collectorNames());
    }

    private String collectorNames() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < collectors.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(collectors.get(i).getCollectorName());
        }
        return sb.toString();
    }

    @Override
    public List<ObservationData> collect() throws ConnectorException {
        List<ObservationData> allResults = new ArrayList<>();
        for (JvmCollector collector : collectors) {
            try {
                List<ObservationData> data = collector.collect(agentId, connectorId);
                if (data != null && !data.isEmpty()) {
                    allResults.addAll(data);
                }
            } catch (Exception e) {
                log.error("Collector {} failed: {}", collector.getCollectorName(), e.getMessage(), e);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Collected {} observations from {} collectors", allResults.size(), collectors.size());
        }
        return allResults;
    }

    @Override
    protected void onStart() throws ConnectorException {
        log.info("JvmConnector starting... agentId={}", agentId);
    }

    @Override
    protected void onStop() {
        log.info("JvmConnector stopping...");
    }

    @Override
    protected void onDestroy() {
        collectors.clear();
        log.info("JvmConnector destroyed.");
    }

    public List<JvmCollector> getCollectors() {
        return new ArrayList<>(collectors);
    }

    public JvmConfig getJvmConfig() {
        return jvmConfig;
    }
}
