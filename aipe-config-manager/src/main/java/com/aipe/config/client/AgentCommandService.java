package com.aipe.config.client;
import com.aipe.config.model.AgentCommand;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;

@Service
public class AgentCommandService {
    private static final Logger log = LoggerFactory.getLogger(AgentCommandService.class);
    public void sendCommand(String agentId, AgentCommand command) { log.info("Command to {}: type={}", agentId, command.getType()); }
}
