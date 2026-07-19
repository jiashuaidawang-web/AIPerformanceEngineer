package com.aipe.config.version;
import com.aipe.config.model.Config;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConfigVersionManager {
    private static final Logger log = LoggerFactory.getLogger(ConfigVersionManager.class);
    private final ConcurrentHashMap<String, Config> configs = new ConcurrentHashMap<>();
    public String createVersion(Config config) { String v = "v" + System.currentTimeMillis(); configs.put(config.getAgentId() + ":" + v, config); configs.put(config.getAgentId() + ":latest", config); return v; }
    public Config getLatest(String agentId) { return configs.get(agentId + ":latest"); }
    public void rollback(String agentId, String version) { Config c = configs.get(agentId + ":" + version); if (c != null) { configs.put(agentId + ":latest", c); log.info("Rolled back: agentId={}, v={}", agentId, version); } }
}
