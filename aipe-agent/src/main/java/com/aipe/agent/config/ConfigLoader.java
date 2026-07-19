package com.aipe.agent.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

/**
 * Agent 配置加载器
 *
 * <p>从 classpath:application.yml 加载 Agent 配置。
 * 加载失败时抛出 AgentConfigException。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class ConfigLoader {

    private static final Logger log = LoggerFactory.getLogger(ConfigLoader.class);
    private static final String DEFAULT_CONFIG_PATH = "application.yml";
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    /**
     * 加载 Agent 配置
     *
     * @return AgentConfig
     * @throws AgentConfigException 配置文件不存在或格式错误
     */
    public AgentConfig load() {
        return load(DEFAULT_CONFIG_PATH);
    }

    /**
     * 加载指定路径的配置文件
     *
     * @param path 配置文件路径
     * @return AgentConfig
     */
    public AgentConfig load(String path) {
        log.info("Loading agent config from: {}", path);
        try {
            ClassPathResource resource = new ClassPathResource(path);
            if (!resource.exists()) {
                throw new AgentConfigException("Config file not found: " + path);
            }
            try (InputStream is = resource.getInputStream()) {
                // YAML根节点为agent节点
                com.fasterxml.jackson.databind.JsonNode root = yamlMapper.readTree(is);
                com.fasterxml.jackson.databind.JsonNode agentNode = root.get("agent");
                AgentConfig config;
                if (agentNode != null) {
                    config = yamlMapper.treeToValue(agentNode, AgentConfig.class);
                } else {
                    // 尝试直接解析
                    config = yamlMapper.readValue(is, AgentConfig.class);
                }
                validate(config);
                log.info("Agent config loaded successfully. agentId={}, environment={}",
                        config.getAgentId(), config.getEnvironment());
                return config;
            }
        } catch (AgentConfigException e) {
            throw e;
        } catch (Exception e) {
            throw new AgentConfigException("Failed to load config from: " + path, e);
        }
    }

    /**
     * 校验配置合法性
     *
     * @param config AgentConfig
     */
    private void validate(AgentConfig config) {
        if (config.getAgentId() == null || config.getAgentId().trim().isEmpty()) {
            throw new AgentConfigException("agent.agentId must not be empty");
        }
        if (config.getServerId() == null || config.getServerId().trim().isEmpty()) {
            throw new AgentConfigException("agent.serverId must not be empty");
        }
        if (config.getSchedulerPoolSize() == null || config.getSchedulerPoolSize() <= 0) {
            config.setSchedulerPoolSize(4);
        }
        if (config.getSendTimeoutMs() == null || config.getSendTimeoutMs() <= 0) {
            config.setSendTimeoutMs(5000L);
        }
    }
}
