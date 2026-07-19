package com.aipe.connector.redis.client;

import com.aipe.connector.redis.config.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisConnectionFactory {
    private static final Logger log = LoggerFactory.getLogger(RedisConnectionFactory.class);

    public static RedisConnection create(RedisConfig config) {
        log.info("Creating RedisConnection: {}:{}", config.getHost(), config.getPort());
        return new RedisConnection(config);
    }

    public static RedisConnection createAndConnect(RedisConfig config) throws Exception {
        RedisConnection connection = create(config);
        connection.connect();
        return connection;
    }
}
