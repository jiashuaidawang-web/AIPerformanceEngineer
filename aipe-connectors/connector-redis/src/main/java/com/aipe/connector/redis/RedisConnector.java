package com.aipe.connector.redis;

import com.aipe.common.domain.ObservationData;
import com.aipe.connector.redis.client.RedisConnection;
import com.aipe.connector.redis.client.RedisConnectionFactory;
import com.aipe.connector.redis.collector.RedisCollector;
import com.aipe.connector.redis.collector.cluster.RedisClusterCollector;
import com.aipe.connector.redis.collector.client.RedisClientCollector;
import com.aipe.connector.redis.collector.info.RedisInfoCollector;
import com.aipe.connector.redis.collector.memory.RedisMemoryCollector;
import com.aipe.connector.redis.collector.replication.RedisReplicationCollector;
import com.aipe.connector.redis.collector.slowlog.RedisSlowLogCollector;
import com.aipe.connector.redis.config.RedisConfig;
import com.aipe.connector.redis.discovery.RedisDiscovery;
import com.aipe.connector.redis.model.RedisNodeInfo;
import com.aipe.connector.sdk.abstractapi.AbstractConnector;
import com.aipe.connector.sdk.config.ConnectorConfig;
import com.aipe.connector.sdk.context.ConnectorContext;
import com.aipe.connector.sdk.exception.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Redis Connector
 *
 * <p>使用 Jedis 真实连接 Redis，采集 INFO / CLIENT LIST / SLOWLOG / CLUSTER 指标。
 * 支持 STANDALONE / SENTINEL / CLUSTER 模式。
 */
public class RedisConnector extends AbstractConnector {

    private static final Logger log = LoggerFactory.getLogger(RedisConnector.class);

    private RedisConfig redisConfig;
    private RedisConnection connection;
    private final List<RedisCollector> collectors = new ArrayList<>();
    private String agentId;
    private String connectorId;

    @Override
    public String getConnectorId() {
        if (connectorId != null) return connectorId;
        if (redisConfig != null && redisConfig.getConnectorId() != null) return redisConfig.getConnectorId();
        return "redis-" + redisConfig.getHost() + "-" + redisConfig.getPort();
    }

    @Override
    public String getConnectorType() {
        return "REDIS";
    }

    @Override
    public String getTargetResource() {
        if (redisConfig != null) return redisConfig.getTargetResource();
        return "redis-" + redisConfig.getHost() + ":" + redisConfig.getPort();
    }

    @Override
    protected void onInit(ConnectorContext context) throws ConnectorException {
        if (context != null) {
            this.agentId = context.getAgentId();
            ConnectorConfig sdkConfig = context.getConfig();
            if (sdkConfig != null) {
                this.connectorId = sdkConfig.getConnectorId();
                this.redisConfig = RedisConfig.fromConnectorConfig(sdkConfig);
            } else {
                this.redisConfig = RedisConfig.defaultConfig();
            }
        } else {
            this.redisConfig = RedisConfig.defaultConfig();
        }

        if (this.connectorId == null) {
            this.connectorId = getConnectorId();
        }

        initCollectors();
        log.info("RedisConnector initialized. agentId={}, host={}, port={}", agentId, redisConfig.getHost(), redisConfig.getPort());
    }

    private void initCollectors() {
        collectors.add(new RedisInfoCollector());
        collectors.add(new RedisMemoryCollector());
        collectors.add(new RedisClientCollector());
        collectors.add(new RedisSlowLogCollector());
        collectors.add(new RedisClusterCollector());
        collectors.add(new RedisReplicationCollector());
        log.info("Initialized {} Redis collectors", collectors.size());
    }

    @Override
    protected void onStart() throws ConnectorException {
        log.info("RedisConnector starting... agentId={}", agentId);
        try {
            connection = RedisConnectionFactory.create(redisConfig);
            connection.connect();
            // Log discovery info
            RedisDiscovery discovery = new RedisDiscovery();
            RedisNodeInfo nodeInfo = discovery.discover(connection, redisConfig);
            log.info("Redis node discovered: role={}, mode={}, version={}", nodeInfo.getRole(), nodeInfo.getMode(), nodeInfo.getRedisVersion());
        } catch (Exception e) {
            throw new ConnectorException(getConnectorId(), "Failed to start Redis connection", e);
        }
    }

    @Override
    public List<ObservationData> collect() throws ConnectorException {
        List<ObservationData> allResults = new ArrayList<>();
        if (connection == null || !connection.isConnected()) {
            log.warn("Redis connection not available, attempting reconnect");
            try {
                connection.connect();
            } catch (Exception e) {
                throw new ConnectorException(getConnectorId(), "Connection lost and reconnect failed", e);
            }
        }

        for (RedisCollector collector : collectors) {
            try {
                List<ObservationData> data = collector.collect(connection, agentId, connectorId);
                if (data != null && !data.isEmpty()) {
                    allResults.addAll(data);
                }
            } catch (Exception e) {
                log.error("Redis collector {} failed: {}", collector.getCollectorName(), e.getMessage(), e);
            }
        }

        return allResults;
    }

    @Override
    protected void onStop() {
        log.info("RedisConnector stopping...");
    }

    @Override
    protected void onDestroy() {
        if (connection != null) {
            connection.disconnect();
            connection = null;
        }
        collectors.clear();
        log.info("RedisConnector destroyed.");
    }

    public List<RedisCollector> getCollectors() {
        return new ArrayList<>(collectors);
    }

    public RedisConfig getRedisConfig() {
        return redisConfig;
    }
}
