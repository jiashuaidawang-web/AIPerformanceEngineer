package com.aipe.storage.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ResourceRepository {
    private static final Logger log = LoggerFactory.getLogger(ResourceRepository.class);

    public void saveResource(String resourceId, String resourceType, String host, String port) {
        log.debug("Saving resource: id={}, type={}", resourceId, resourceType);
    }

    public List<Map<String, Object>> findAllResources() {
        log.debug("Querying all resources");
        return java.util.Collections.emptyList();
    }
}
