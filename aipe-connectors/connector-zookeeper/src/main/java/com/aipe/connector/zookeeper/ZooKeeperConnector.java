package com.aipe.connector.zookeeper;

import com.aipe.common.domain.ObservationData;
import com.aipe.connector.zookeeper.collector.ZooKeeperCollector;
import com.aipe.connector.zookeeper.collector.FourLetterCommandCollector;
import com.aipe.connector.zookeeper.config.ZooKeeperConfig;
import com.aipe.connector.sdk.abstractapi.AbstractConnector;
import com.aipe.connector.sdk.config.ConnectorConfig;
import com.aipe.connector.sdk.context.ConnectorContext;
import com.aipe.connector.sdk.exception.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ZooKeeper Connector - 通过四字命令采集 ZK 指标
 *
 * <p>采集指标:
 * <ul>
 *   <li>mntr: 节点数、Watch 数、连接数、延迟、数据包</li>
 *   <li>stat: 版本、模式、连接数、节点数</li>
 *   <li>cons: 连接详情</li>
 *   <li>ruok: 健康状态</li>
 * </ul>
 *
 * <p>对接方式: 四字命令 (mntr, stat, cons, ruok)
 * <p>需要重启: ❌ 不需要
 * <p>客户改动: 开放 2181 端口
 */
public class ZooKeeperConnector extends AbstractConnector {

    private static final Logger log = LoggerFactory.getLogger(ZooKeeperConnector.class);

    private ZooKeeperConfig zkConfig;
    private final List<ZooKeeperCollector> collectors = new CopyOnWriteArrayList<>();
    private String agentId;
    private String connectorId;

    @Override
    public String getConnectorId() {
        if (connectorId != null) return connectorId;
        if (zkConfig != null) return "zookeeper-" + zkConfig.getHost() + "-" + zkConfig.getPort();
        return "zookeeper-unknown";
    }

    @Override
    public String getConnectorType() {
        return "ZOOKEEPER";
    }

    @Override
    public String getTargetResource() {
        if (zkConfig != null) return "zookeeper-" + zkConfig.getHost() + ":" + zkConfig.getPort();
        return "zookeeper-unknown";
    }

    @Override
    protected void onInit(ConnectorContext context) throws ConnectorException {
        if (context != null) {
            this.agentId = context.getAgentId();
            ConnectorConfig sdkConfig = context.getConfig();
            if (sdkConfig != null) {
                this.connectorId = sdkConfig.getConnectorId();
                this.zkConfig = ZooKeeperConfig.fromConnectorConfig(sdkConfig);
            } else {
                this.zkConfig = ZooKeeperConfig.defaultConfig();
            }
        } else {
            this.zkConfig = ZooKeeperConfig.defaultConfig();
        }
        if (this.connectorId == null) this.connectorId = getConnectorId();
        initCollectors();
        log.info("ZooKeeperConnector initialized. agentId={}, host={}, port={}",
                agentId, zkConfig.getHost(), zkConfig.getPort());
    }

    private void initCollectors() {
        collectors.add(new FourLetterCommandCollector());
        log.info("Initialized {} ZooKeeper collectors", collectors.size());
    }

    @Override
    public List<ObservationData> collect() throws ConnectorException {
        List<ObservationData> allResults = new ArrayList<>();

        for (ZooKeeperCollector collector : collectors) {
            try {
                List<ObservationData> data = collector.collect(zkConfig, agentId, connectorId);
                if (data != null && !data.isEmpty()) allResults.addAll(data);
            } catch (Exception e) {
                log.error("ZooKeeper collector {} failed: {}", collector.getCollectorName(), e.getMessage(), e);
            }
        }
        return allResults;
    }

    @Override
    protected void onStart() { log.info("ZooKeeperConnector starting..."); }

    @Override
    protected void onStop() { log.info("ZooKeeperConnector stopping..."); }

    @Override
    protected void onDestroy() {
        collectors.clear();
        log.info("ZooKeeperConnector destroyed.");
    }
}
