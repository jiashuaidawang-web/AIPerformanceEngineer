package com.aipe.config.registry;
import com.aipe.config.model.AgentInfo;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AgentRegistry {
    private static final Logger log = LoggerFactory.getLogger(AgentRegistry.class);
    private final ConcurrentHashMap<String, AgentInfo> agents = new ConcurrentHashMap<>();
    public void register(AgentInfo agentInfo) { agents.put(agentInfo.getAgentId(), agentInfo); log.info("Agent registered: {}", agentInfo.getAgentId()); }
    public void unregister(String agentId) { agents.remove(agentId); }
    public AgentInfo get(String agentId) { return agents.get(agentId); }
    public List<AgentInfo> getAll() { return new ArrayList<>(agents.values()); }
    public int size() { return agents.size(); }
}
