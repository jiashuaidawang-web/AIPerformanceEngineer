package com.aipe.connector.redis.client;

import com.aipe.connector.redis.config.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.time.Duration;

public class RedisConnection {
    private static final Logger log = LoggerFactory.getLogger(RedisConnection.class);

    private final RedisConfig config;
    private JedisPool jedisPool;

    public RedisConnection(RedisConfig config) {
        this.config = config;
    }

    public void connect() throws JedisConnectionException {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(4);
        poolConfig.setMaxIdle(2);
        poolConfig.setMinIdle(1);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setMaxWait(Duration.ofMillis(config.getTimeoutMs()));

        if (config.getPassword() != null && !config.getPassword().isEmpty()) {
            jedisPool = new JedisPool(poolConfig, config.getHost(), config.getPort(),
                    config.getTimeoutMs().intValue(), config.getPassword(), config.getDatabase());
        } else {
            jedisPool = new JedisPool(poolConfig, config.getHost(), config.getPort(),
                    config.getTimeoutMs().intValue(), null, config.getDatabase());
        }
        log.info("Redis connection pool created: {}:{}", config.getHost(), config.getPort());
    }

    public String info(String section) {
        try (Jedis jedis = jedisPool.getResource()) {
            return section != null ? jedis.info(section) : jedis.info();
        } catch (Exception e) {
            log.warn("Redis INFO {} failed: {}", section, e.getMessage());
            return "";
        }
    }

    public String clientList() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.clientList();
        } catch (Exception e) {
            log.warn("CLIENT LIST failed: {}", e.getMessage());
            return "";
        }
    }

    public String slowLogGet(long count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.slowlogGet(count).toString();
        } catch (Exception e) {
            log.warn("SLOWLOG GET failed: {}", e.getMessage());
            return "";
        }
    }

    public String clusterInfo() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.clusterInfo();
        } catch (Exception e) {
            log.warn("CLUSTER INFO failed: {}", e.getMessage());
            return "";
        }
    }

    public String clusterNodes() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.clusterNodes();
        } catch (Exception e) {
            log.warn("CLUSTER NODES failed: {}", e.getMessage());
            return "";
        }
    }

    public void disconnect() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
            log.info("Redis connection closed: {}:{}", config.getHost(), config.getPort());
        }
    }

    public boolean isConnected() {
        if (jedisPool == null || jedisPool.isClosed()) return false;
        try (Jedis jedis = jedisPool.getResource()) {
            return "PONG".equalsIgnoreCase(jedis.ping());
        } catch (Exception e) {
            return false;
        }
    }

    public RedisConfig getConfig() { return config; }
}
