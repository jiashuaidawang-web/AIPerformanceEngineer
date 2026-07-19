package com.aipe.connector.redis.discovery;

import com.aipe.connector.redis.client.RedisConnection;
import com.aipe.connector.redis.config.RedisConfig;
import com.aipe.connector.redis.model.RedisNodeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RedisDiscovery {
    private static final Logger log = LoggerFactory.getLogger(RedisDiscovery.class);

    public RedisNodeInfo discover(RedisConnection connection, RedisConfig config) {
        RedisNodeInfo info = new RedisNodeInfo();
        info.setHost(config.getHost());
        info.setPort(config.getPort());

        // Get INFO to detect mode
        String replicationInfo = connection.info("Replication");
        String serverInfo = connection.info("Server");

        // Detect role
        if (replicationInfo.contains("role:master")) {
            info.setRole("MASTER");
        } else if (replicationInfo.contains("role:slave")) {
            info.setRole("SLAVE");
            // Parse master host
            for (String line : replicationInfo.split("\r\n")) {
                if (line.startsWith("master_host:")) {
                    info.setMasterHost(line.split(":")[1].trim());
                } else if (line.startsWith("master_port:")) {
                    try {
                        info.setMasterPort(Integer.parseInt(line.split(":")[1].trim()));
                    } catch (Exception e) {}
                }
            }
        } else if (replicationInfo.contains("role:sentinel")) {
            info.setRole("SENTINEL");
        }

        // Detect mode from config
        String mode = connection.info("Memory");
        if ("CLUSTER".equalsIgnoreCase(config.getMode())) {
            info.setMode("CLUSTER");
        } else if (info.getRole().equals("SENTINEL")) {
            info.setMode("SENTINEL");
        } else {
            info.setMode("STANDALONE");
        }

        // Extract version
        for (String line : serverInfo.split("\r\n")) {
            if (line.startsWith("redis_version:")) {
                info.setRedisVersion(line.split(":")[1].trim());
                break;
            }
        }

        log.info("Redis node discovered: role={}, mode={}, version={}", info.getRole(), info.getMode(), info.getRedisVersion());
        return info;
    }
}
