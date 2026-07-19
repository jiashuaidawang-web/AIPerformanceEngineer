package com.aipe.connector.linux;

import com.aipe.common.domain.ObservationData;
import com.aipe.connector.linux.collector.LinuxCollector;
import com.aipe.connector.linux.collector.cpu.CpuCollector;
import com.aipe.connector.linux.collector.memory.MemoryCollector;
import com.aipe.connector.linux.collector.disk.DiskCollector;
import com.aipe.connector.linux.collector.network.NetworkCollector;
import com.aipe.connector.linux.collector.tcp.TcpCollector;
import com.aipe.connector.linux.collector.load.LoadCollector;
import com.aipe.connector.linux.collector.process.ProcessCollector;
import com.aipe.connector.linux.config.LinuxConfig;
import com.aipe.connector.linux.parser.ProcFileReader;
import com.aipe.connector.sdk.abstractapi.AbstractConnector;
import com.aipe.connector.sdk.config.ConnectorConfig;
import com.aipe.connector.sdk.context.ConnectorContext;
import com.aipe.connector.sdk.exception.ConnectorException;
import com.aipe.connector.sdk.lifecycle.ConnectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Linux Connector
 *
 * <p>读取 /proc 文件系统获取 OS 指标（CPU、内存、磁盘、网络、TCP、负载、进程）。
 */
public class LinuxConnector extends AbstractConnector {

    private static final Logger log = LoggerFactory.getLogger(LinuxConnector.class);

    private LinuxConfig linuxConfig;
    private ProcFileReader procFileReader;
    private final List<LinuxCollector> collectors = new ArrayList<>();
    private String agentId;
    private String connectorId;

    @Override
    public String getConnectorId() {
        if (connectorId != null) return connectorId;
        if (linuxConfig != null && linuxConfig.getConnectorId() != null) return linuxConfig.getConnectorId();
        return "linux-" + System.identityHashCode(this);
    }

    @Override
    public String getConnectorType() {
        return "LINUX";
    }

    @Override
    public String getTargetResource() {
        if (linuxConfig != null && linuxConfig.getTargetResource() != null) return linuxConfig.getTargetResource();
        return "linux-local";
    }

    @Override
    protected void onInit(ConnectorContext context) throws ConnectorException {
        if (context != null) {
            this.agentId = context.getAgentId();
            ConnectorConfig sdkConfig = context.getConfig();
            if (sdkConfig != null) {
                this.connectorId = sdkConfig.getConnectorId();
                this.linuxConfig = LinuxConfig.fromConnectorConfig(sdkConfig);
            } else {
                this.linuxConfig = LinuxConfig.defaultConfig();
            }
        } else {
            this.linuxConfig = LinuxConfig.defaultConfig();
        }

        if (this.connectorId == null) {
            this.connectorId = getConnectorId();
        }

        String procPath = linuxConfig.getProcPath();
        this.procFileReader = new ProcFileReader(procPath);
        initCollectors();
        log.info("LinuxConnector initialized. agentId={}, collectors={}", agentId, collectors.size());
    }

    private void initCollectors() {
        if (linuxConfig.getCpuEnabled()) collectors.add(new CpuCollector());
        if (linuxConfig.getMemoryEnabled()) collectors.add(new MemoryCollector());
        if (linuxConfig.getDiskEnabled()) collectors.add(new DiskCollector());
        if (linuxConfig.getNetworkEnabled()) collectors.add(new NetworkCollector());
        if (linuxConfig.getTcpEnabled()) collectors.add(new TcpCollector());
        if (linuxConfig.getLoadEnabled()) collectors.add(new LoadCollector());
        if (linuxConfig.getProcessEnabled()) collectors.add(new ProcessCollector());
        log.info("Initialized {} Linux collectors: {}", collectors.size(), collectorNames());
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
        for (LinuxCollector collector : collectors) {
            try {
                List<ObservationData> data = collector.collect(procFileReader, agentId, connectorId);
                if (data != null && !data.isEmpty()) {
                    allResults.addAll(data);
                }
            } catch (Exception e) {
                log.error("Linux collector {} failed: {}", collector.getCollectorName(), e.getMessage(), e);
            }
        }
        return allResults;
    }

    @Override
    protected void onStart() throws ConnectorException {
        log.info("LinuxConnector starting... agentId={}", agentId);
    }

    @Override
    protected void onStop() {
        log.info("LinuxConnector stopping...");
    }

    @Override
    protected void onDestroy() {
        collectors.clear();
        log.info("LinuxConnector destroyed.");
    }

    public List<LinuxCollector> getCollectors() {
        return new ArrayList<>(collectors);
    }

    public LinuxConfig getLinuxConfig() {
        return linuxConfig;
    }
}
