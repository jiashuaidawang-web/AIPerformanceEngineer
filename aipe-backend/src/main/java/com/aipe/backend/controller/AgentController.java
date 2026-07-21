package com.aipe.backend.controller;

import com.aipe.backend.dto.AgentInfo;
import com.aipe.backend.service.AgentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Agent 管理接口
 *
 * <p>处理 Agent 注册、心跳、状态查询。
 */
@RestController
@RequestMapping("/api/v1/agents")
public class AgentController {
    private static final Logger log = LoggerFactory.getLogger(AgentController.class);

    @Autowired
    private AgentService agentService;

    /**
     * Agent 注册
     * POST /api/v1/agents/register
     */
    @PostMapping("/register")
    public Map<String, String> register(@RequestBody AgentInfo agentInfo) {
        String agentId = agentService.registerAgent(agentInfo);
        log.info("Agent registered: {}", agentId);
        Map<String, String> result = new HashMap<>();
        result.put("agentId", agentId);
        result.put("status", "ok");
        return result;
    }

    /**
     * Agent 心跳
     * POST /api/v1/agents/{agentId}/heartbeat
     */
    @PostMapping("/{agentId}/heartbeat")
    public Map<String, String> heartbeat(@PathVariable String agentId) {
        agentService.updateHeartbeat(agentId);
        Map<String, String> result = new HashMap<>();
        result.put("status", "ok");
        return result;
    }

    /**
     * 查询 Agent 列表
     * GET /api/v1/agents
     */
    @GetMapping
    public List<AgentInfo> listAgents() {
        return agentService.listAgents();
    }

    /**
     * 查询单个 Agent
     * GET /api/v1/agents/{agentId}
     */
    @GetMapping("/{agentId}")
    public AgentInfo getAgent(@PathVariable String agentId) {
        return agentService.getAgent(agentId);
    }
}
