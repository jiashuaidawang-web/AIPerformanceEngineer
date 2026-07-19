package com.aipe.config.heartbeat;
import com.aipe.config.registry.AgentRegistry;
import com.aipe.config.model.AgentInfo;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class AgentHeartbeatScheduler {
    private static final Logger log = LoggerFactory.getLogger(AgentHeartbeatScheduler.class);
    private static final long TIMEOUT_SECONDS = 90;
    private final AgentRegistry registry;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> { Thread t = new Thread(r, "heartbeat-scheduler"); t.setDaemon(true); return t; });
    public AgentHeartbeatScheduler(AgentRegistry registry) { this.registry = registry; }
    public void start() { scheduler.scheduleAtFixedRate(this::checkHeartbeats, 30, 30, TimeUnit.SECONDS); log.info("HeartbeatScheduler started"); }
    private void checkHeartbeats() {
        List<AgentInfo> agents = registry.getAll();
        LocalDateTime now = LocalDateTime.now();
        for (AgentInfo agent : agents) {
            if (agent.getLastHeartbeat() != null) {
                long secs = ChronoUnit.SECONDS.between(agent.getLastHeartbeat(), now);
                if (secs > TIMEOUT_SECONDS) { agent.setStatus("OFFLINE"); log.warn("Agent {} offline ({}s)", agent.getAgentId(), secs); }
            }
        }
    }
    public void stop() { scheduler.shutdown(); }
}
