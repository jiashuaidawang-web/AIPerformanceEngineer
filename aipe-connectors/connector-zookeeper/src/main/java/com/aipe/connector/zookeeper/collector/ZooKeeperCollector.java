package com.aipe.connector.zookeeper.collector;

import com.aipe.common.domain.ObservationData;
import com.aipe.connector.zookeeper.config.ZooKeeperConfig;
import com.aipe.connector.sdk.exception.ConnectorException;
import java.util.List;

public interface ZooKeeperCollector {
    List<ObservationData> collect(ZooKeeperConfig config, String agentId, String connectorId) throws ConnectorException;
    String getCollectorName();
}
