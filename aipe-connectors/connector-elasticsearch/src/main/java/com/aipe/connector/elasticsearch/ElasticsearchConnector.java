package com.aipe.connector.elasticsearch;

import com.aipe.common.domain.ObservationData;
import com.aipe.connector.elasticsearch.collector.*;
import com.aipe.connector.elasticsearch.config.ElasticsearchConfig;
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
 * Elasticsearch Connector - 通过 HTTP API 采集 ES 指标
 *
 * <p>采集指标:
 * <ul>
 *   <li>集群: 状态、节点数、分片数、未分配分片</li>
 *   <li>索引: 文档数、存储大小、查询/索引速率</li>
 *   <li>JVM: 堆使用、GC、线程</li>
 *   <li>搜索: 查询延迟、拒绝数、缓存</li>
 *   <li>OS: CPU、内存、负载</li>
 * </ul>
 *
 * <p>对接方式: HTTP REST API
 * <p>需要重启: ❌ 不需要
 * <p>客户改动: 开放 9200 端口
 */
public class ElasticsearchConnector extends AbstractConnector {

    private static final Logger log = LoggerFactory.getLogger(ElasticsearchConnector.class);

    private ElasticsearchConfig esConfig;
    private final List<ElasticsearchCollector> collectors = new CopyOnWriteArrayList<>();
    private String agentId;
    private String connectorId;

    @Override
    public String getConnectorId() {
        if (connectorId != null) return connectorId;
        if (esConfig != null) return "elasticsearch-" + esConfig.getHost() + "-" + esConfig.getPort();
        return "elasticsearch-unknown";
    }

    @Override
    public String getConnectorType() {
        return "ELASTICSEARCH";
    }

    @Override
    public String getTargetResource() {
        if (esConfig != null) return "elasticsearch-" + esConfig.getHost() + ":" + esConfig.getPort();
        return "elasticsearch-unknown";
    }

    @Override
    protected void onInit(ConnectorContext context) throws ConnectorException {
        if (context != null) {
            this.agentId = context.getAgentId();
            ConnectorConfig sdkConfig = context.getConfig();
            if (sdkConfig != null) {
                this.connectorId = sdkConfig.getConnectorId();
                this.esConfig = ElasticsearchConfig.fromConnectorConfig(sdkConfig);
            } else {
                this.esConfig = ElasticsearchConfig.defaultConfig();
            }
        } else {
            this.esConfig = ElasticsearchConfig.defaultConfig();
        }
        if (this.connectorId == null) this.connectorId = getConnectorId();
        initCollectors();
        log.info("ElasticsearchConnector initialized. agentId={}, host={}, port={}",
                agentId, esConfig.getHost(), esConfig.getPort());
    }

    private void initCollectors() {
        collectors.add(new ClusterHealthCollector());
        collectors.add(new NodeStatsCollector());
        collectors.add(new IndexStatsCollector());
        log.info("Initialized {} Elasticsearch collectors", collectors.size());
    }

    @Override
    public List<ObservationData> collect() throws ConnectorException {
        List<ObservationData> allResults = new ArrayList<>();

        for (ElasticsearchCollector collector : collectors) {
            try {
                List<ObservationData> data = collector.collect(esConfig, agentId, connectorId);
                if (data != null && !data.isEmpty()) allResults.addAll(data);
            } catch (Exception e) {
                log.error("Elasticsearch collector {} failed: {}", collector.getCollectorName(), e.getMessage(), e);
            }
        }
        return allResults;
    }

    @Override
    protected void onStart() { log.info("ElasticsearchConnector starting..."); }

    @Override
    protected void onStop() { log.info("ElasticsearchConnector stopping..."); }

    @Override
    protected void onDestroy() {
        collectors.clear();
        log.info("ElasticsearchConnector destroyed.");
    }
}
