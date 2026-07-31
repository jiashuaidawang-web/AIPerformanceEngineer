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
 */
public class ResourceRegistrar {

    private static final Logger log = LoggerFactory.getLogger(ResourceRegistrar.class);

    private final String resourceEngineUrl;
    private final RestTemplate restTemplate;

    public ResourceRegistrar(AgentConfig config) {
        this.resourceEngineUrl = config.getBackendUrl() + "/v1/resources";
        this.restTemplate = new RestTemplate();
    }

    /**
     * 注册资源（如果不存在则创建）
     */
    public void registerResource(String resourceId, String resourceType, String resourceName) {
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
            attributes.put("resourceId", resourceId);
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
     * 批量注册多个资源
     */
    public void registerResources(String[][] resources) {
        for (String[] resource : resources) {
            String resourceId = resource[0];
            String resourceType = resource[1];
            String resourceName = resource.length > 2 ? resource[2] : resourceType + "-" + resourceId.substring(Math.max(0, resourceId.length() - 6));
            registerResource(resourceId, resourceType, resourceName);
        }
    }
}
