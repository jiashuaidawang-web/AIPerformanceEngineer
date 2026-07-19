package com.aipe.connector.redis.collector;

import com.aipe.common.domain.ObservationData;
import com.aipe.connector.redis.client.RedisConnection;
import java.util.List;

public interface RedisCollector {
    List<ObservationData> collect(RedisConnection connection, String agentId, String connectorId);
    String getCollectorName();
}
