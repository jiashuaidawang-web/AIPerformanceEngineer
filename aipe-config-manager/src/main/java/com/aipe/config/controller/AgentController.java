package com.aipe.config.controller;

import com.aipe.config.agent.AgentManager;
import com.aipe.config.config.ConfigurationManager;
import com.aipe.config.deployment.DeploymentManager;
import com.aipe.config.model.AgentInfo;
import com.aipe.config.model.Config;
import com.aipe.config.model.DeploymentRequest;
import com.aipe.config.model.AgentCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AgentController {
    private static final Logger log = LoggerFactory.getLogger(AgentController.class);

    private final AgentManager agentManager;
    private final ConfigurationManager configManager;
    private final DeploymentManager deploymentManager;

    public AgentController(AgentManager agentManager, ConfigurationManager configManager, DeploymentManager deploymentManager) {
        this.agentManager = agentManager;
        this.configManager = configManager;
        this.deploymentManager = deploymentManager;
    }

    @PostMapping("/agents/register")
    public String register(@RequestBody AgentInfo agentInfo) {
        return agentManager.registerAgent(agentInfo);
    }

    @PostMapping("/agents/{agentId}/heartbeat")
    public void heartbeat(@PathVariable String agentId, @RequestBody Object request) {
        agentManager.updateHeartbeat(agentId);
    }

    @GetMapping("/agents")
    public List<AgentInfo> listAgents() {
        return agentManager.listAgents();
    }

    @GetMapping("/agents/{agentId}")
    public AgentInfo getAgent(@PathVariable String agentId) {
        return agentManager.getAgent(agentId);
    }

    @DeleteMapping("/agents/{agentId}")
    public void removeAgent(@PathVariable String agentId) {
        agentManager.removeAgent(agentId);
    }

    @PostMapping("/configs")
    public Config saveConfig(@RequestBody Config config) {
        return configManager.saveConfig(config);
    }

    @PostMapping("/configs/{agentId}/publish")
    public void publishConfig(@PathVariable String agentId) {
        configManager.publishConfig(agentId);
    }

    @PostMapping("/deployment/deploy")
    public String deploy(@RequestBody DeploymentRequest request) {
        return deploymentManager.deployAgent(request);
    }

    @PostMapping("/agents/{agentId}/command")
    public void sendCommand(@PathVariable String agentId, @RequestBody AgentCommand command) {
        agentManager.sendCommand(agentId, command);
    }
}
