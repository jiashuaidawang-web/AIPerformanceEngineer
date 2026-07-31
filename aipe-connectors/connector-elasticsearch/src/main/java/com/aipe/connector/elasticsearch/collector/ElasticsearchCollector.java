package com.aipe.connector.elasticsearch.collector;

import com.aipe.common.domain.ObservationData;
import com.aipe.connector.elasticsearch.config.ElasticsearchConfig;
import com.aipe.connector.sdk.exception.ConnectorException;
import java.util.List;

public interface ElasticsearchCollector {
    List<ObservationData> collect(ElasticsearchConfig config, String agentId, String connectorId) throws ConnectorException;
    String getCollectorName();
}
