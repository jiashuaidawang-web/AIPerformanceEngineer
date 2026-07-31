package com.aipe.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 资源服务 - 自动注册 Agent 上报的资源
 */
@Service
public class ResourceService {

    private static final Logger log = LoggerFactory.getLogger(ResourceService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 检查资源是否存在
     */
    public boolean resourceExists(String resourceId) {
        String sql = "SELECT COUNT(*) FROM resource WHERE resource_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, resourceId);
        return count != null && count > 0;
    }

    /**
     * 自动注册资源（如果不存在）
     */
    public void autoRegisterResource(String resourceId, String resourceType) {
        if (resourceExists(resourceId)) {
            return;
        }

        String resourceName = generateResourceName(resourceId, resourceType);
        String sql = "INSERT INTO resource (resource_id, resource_name, resource_type, resource_category, status, version) VALUES (?, ?, ?, ?, 'RUNNING', 1)";

        String category = mapResourceTypeToCategory(resourceType);

        try {
            jdbcTemplate.update(sql, resourceId, resourceName, resourceType, category);
            log.info("Auto-registered resource: id={}, type={}", resourceId, resourceType);
        } catch (Exception e) {
            log.warn("Failed to auto-register resource {}: {}", resourceId, e.getMessage());
        }
    }

    /**
     * 列出所有资源
     */
    public List<Map<String, Object>> listResources() {
        String sql = "SELECT * FROM resource ORDER BY created_time DESC";
        return jdbcTemplate.queryForList(sql);
    }

    private String generateResourceName(String resourceId, String type) {
        // 生成友好的资源名称
        switch (type) {
            case "JVM":
                return "JVM-" + resourceId.substring(Math.max(0, resourceId.length() - 6));
            case "LINUX":
                return "Linux服务器-" + resourceId.substring(Math.max(0, resourceId.length() - 6));
            case "REDIS":
                return "Redis-" + resourceId.replace("redis-", "").replace("-6379", "");
            case "MYSQL":
                return "MySQL-" + resourceId.replace("mysql-", "").replace("-3306", "");
            case "KAFKA":
                return "Kafka-" + resourceId.replace("kafka-", "");
            case "ZOOKEEPER":
                return "ZooKeeper-" + resourceId.replace("zookeeper-", "");
            case "ROCKETMQ":
                return "RocketMQ-" + resourceId.replace("rocketmq-", "");
            case "ELASTICSEARCH":
                return "ES-" + resourceId.replace("elasticsearch-", "");
            default:
                return type + "-" + resourceId.substring(Math.max(0, resourceId.length() - 6));
        }
    }

    private String mapResourceTypeToCategory(String type) {
        switch (type) {
            case "REDIS":
            case "MYSQL":
                return "PLATFORM";
            case "KAFKA":
            case "ZOOKEEPER":
            case "ROCKETMQ":
            case "ELASTICSEARCH":
                return "PLATFORM";
            case "JVM":
            case "LINUX":
            case "SERVICE":
                return "BUSINESS";
            default:
                return "INFRA";
        }
    }
}
