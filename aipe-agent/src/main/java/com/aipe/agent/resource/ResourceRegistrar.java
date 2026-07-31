package com.aipe.agent.resource;

import com.aipe.agent.config.AgentConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 资源注册器 - Agent 自动注册资源到 Resource Engine
 * 使用 host_type 作为唯一标识避免重复注册
 */
public class ResourceRegistrar {

    private static final Logger log = LoggerFactory.getLogger(ResourceRegistrar.class);

    private final String resourceEngineUrl;
    private final RestTemplate restTemplate;
    private final String hostPrefix;

    public ResourceRegistrar(AgentConfig config) {
        this.resourceEngineUrl = config.getBackendUrl() + "/api/v1/resources";
        this.restTemplate = new RestTemplate();
        // 使用 Agent ID 作为前缀，确保不同机器的资源 ID 唯一
        this.hostPrefix = config.getAgentId() + "-";
    }

    /**
     * 注册资源（如果不存在则创建）
     */
    public void registerResource(String baseResourceId, String resourceType, String resourceName) {
        String resourceId = hostPrefix + baseResourceId;

        try {
            // 先检查资源是否存在
            try {
                ResponseEntity<Map> response = restTemplate.getForEntity(
                        resourceEngineUrl + "/" + resourceId, Map.class);
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    Map<String, Object> body = response.getBody();
                    if (body.containsKey("data") && body.get("data") != null) {
                        log.debug("Resource already exists: {}", resourceId);
                        return;
                    }
                }
            } catch (Exception e) {
                // 资源不存在，继续创建
            }

            // 创建资源
            Map<String, Object> request = new HashMap<>();
            request.put("resourceName", resourceName);
            request.put("resourceType", resourceType);
            request.put("environment", "prod");
            request.put("businessSystem", "Agent Auto-Discovery");

            Map<String, String> attributes = new HashMap<>();
            attributes.put("discoveredBy", "agent");
            attributes.put("agentId", hostPrefix.substring(0, hostPrefix.length() - 1));
            request.put("attributes", attributes);

            ResponseEntity<Map> response = restTemplate.postForEntity(resourceEngineUrl, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Auto-registered resource: id={}, type={}", resourceId, resourceType);
            } else {
                log.warn("Failed to register resource {}: HTTP {}", resourceId, response.getStatusCode());
            }
        } catch (Exception e) {
            log.warn("Error registering resource {}: {}", resourceId, e.getMessage());
        }
    }

    /**
     * 批量注册多个资源（仅在启动时调用一次）
     */
    public void registerResources(String[][] resources) {
        for (String[] resource : resources) {
            String baseResourceId = resource[0];
            String resourceType = resource[1];
            String resourceName = resource.length > 2 ? resource[2] : resourceType + "-" + baseResourceId;
            registerResource(baseResourceId, resourceType, resourceName);
        }
    }
}
