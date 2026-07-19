package com.aipe.connector.redis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisNodeInfo {
    private String host;
    private Integer port;
    private String role; // MASTER / SLAVE / SENTINEL
    private String mode; // STANDALONE / SENTINEL / CLUSTER
    private String redisVersion;
    private String masterHost;
    private Integer masterPort;
    private Boolean connected;
}
