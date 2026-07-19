package com.aipe.config.config;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;

@Service
public class ConfigPublisher {
    private static final Logger log = LoggerFactory.getLogger(ConfigPublisher.class);
    public void push(String agentId, String configVersion) { log.info("Pushing config: agentId={}, version={}", agentId, configVersion); }
}
