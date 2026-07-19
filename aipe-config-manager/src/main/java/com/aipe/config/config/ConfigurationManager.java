package com.aipe.config.config;
import com.aipe.config.version.ConfigVersionManager;
import com.aipe.config.model.Config;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;

@Service
public class ConfigurationManager {
    private static final Logger log = LoggerFactory.getLogger(ConfigurationManager.class);
    private final ConfigVersionManager versionManager;
    private final ConfigPublisher publisher;
    public ConfigurationManager(ConfigVersionManager versionManager, ConfigPublisher publisher) { this.versionManager = versionManager; this.publisher = publisher; }
    public Config saveConfig(Config config) { String v = versionManager.createVersion(config); config.setVersion(v); log.info("Config saved: v={}", v); return config; }
    public void publishConfig(String agentId) { Config latest = versionManager.getLatest(agentId); if (latest != null) { publisher.push(agentId, latest.getVersion()); } }
    public void rollback(String agentId, String version) { versionManager.rollback(agentId, version); }
}
