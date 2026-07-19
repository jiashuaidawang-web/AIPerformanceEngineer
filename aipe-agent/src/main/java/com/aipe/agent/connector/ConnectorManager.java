package com.aipe.agent.connector;

import com.aipe.agent.config.AgentConfig;
import com.aipe.agent.event.AgentEventBus;
import com.aipe.agent.observation.HttpObservationSender;
import com.aipe.agent.scheduler.SchedulerManager;
import com.aipe.common.domain.ObservationData;
import com.aipe.connector.sdk.context.ConnectorContext;
import com.aipe.common.enums.AgentState;
import com.aipe.connector.sdk.lifecycle.ConnectorState;
import com.aipe.connector.sdk.Connector;
import com.aipe.connector.sdk.ConnectorException;
import com.aipe.connector.sdk.config.ConnectorConfig;
import com.aipe.connector.jvm.JvmConnector;
import com.aipe.connector.linux.LinuxConnector;
import com.aipe.connector.redis.RedisConnector;
import com.aipe.connector.mysql.MySQLConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Connector 管理器
 *
 * <p>管理所有 Connector 的注册、初始化、启动、停止。
 * 通过 SchedulerManager 调度周期性采集任务。
 * 单个 Connector 异常不影响其他 Connector 和 Agent。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class ConnectorManager {

    private static final Logger log = LoggerFactory.getLogger(ConnectorManager.class);

    private final ConnectorRegistry registry;
    private final SchedulerManager schedulerManager;
    private final HttpObservationSender observationSender;
    private final AgentEventBus eventBus;
    private final AgentConfig config;
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final AtomicInteger runningCount = new AtomicInteger(0);

    public ConnectorManager(ConnectorRegistry registry,
                             SchedulerManager schedulerManager,
                             HttpObservationSender observationSender,
                             AgentEventBus eventBus,
                             AgentConfig config) {
        this.registry = registry;
        this.schedulerManager = schedulerManager;
        this.observationSender = observationSender;
        this.eventBus = eventBus;
        this.config = config;
    }

    /**
     * 初始化 - 注册配置中启用的 Connector
     */
    public void init() {
        if (initialized.get()) {
            log.warn("ConnectorManager already initialized");
            return;
        }
        log.info("Initializing ConnectorManager...");
        List<AgentConfig.ConnectorConfigItem> connectorConfigs = config.getConnectors();
        if (connectorConfigs == null || connectorConfigs.isEmpty()) {
            log.warn("No connectors configured");
            initialized.set(true);
            return;
        }

        for (AgentConfig.ConnectorConfigItem item : connectorConfigs) {
            if (item.getEnabled() == null || !item.getEnabled()) {
                log.info("Connector type={} is disabled, skipping", item.getType());
                continue;
            }
            try {
                Connector connector = createConnector(item);
                if (connector != null) {
                    // Build ConnectorConfig from AgentConfig item
                    ConnectorConfig connectorConfig = ConnectorConfig.builder()
                            .type(item.getType())
                            .enabled(item.getEnabled())
                            .intervalMs(item.getIntervalMs())
                            .timeoutMs(config.getSendTimeoutMs())
                            .properties(item.getProperties())
                            .build();

                    ConnectorContext context = ConnectorContext.builder()
                            .agentId(config.getAgentId())
                            .collectIntervalMs(item.getIntervalMs())
                            .collectTimeoutMs(config.getSendTimeoutMs())
                            .properties(item.getProperties())
                            .config(connectorConfig)
                            .build();
                    connector.init(context);
                    registry.register(connector);
                    eventBus.publish(AgentEventBus.CONNECTOR_REGISTERED, connector.getConnectorId());
                    log.info("Connector initialized: id={}, type={}", connector.getConnectorId(), connector.getConnectorType());
                }
            } catch (Exception e) {
                log.error("Failed to initialize connector type={}", item.getType(), e);
                eventBus.publish(AgentEventBus.CONNECTOR_FAILED, item.getType());
            }
        }
        initialized.set(true);
        log.info("ConnectorManager initialized. Registered {} connectors", registry.size());
    }

    /**
     * 启动所有已注册的 Connector
     */
    public void startAll() {
        List<Connector> connectors = registry.getAll();
        log.info("Starting {} connectors...", connectors.size());
        for (Connector connector : connectors) {
            try {
                connector.start();
                runningCount.incrementAndGet();
                schedulerManager.schedule(() -> executeCollect(connector),
                        getIntervalForConnector(connector));
                eventBus.publish(AgentEventBus.CONNECTOR_STARTED, connector.getConnectorId());
                log.info("Connector started: id={}", connector.getConnectorId());
            } catch (Exception e) {
                log.error("Failed to start connector: id={}", connector.getConnectorId(), e);
                connector.stop();
                eventBus.publish(AgentEventBus.CONNECTOR_FAILED, connector.getConnectorId());
            }
        }
        log.info("ConnectorManager: {}/{} connectors started", runningCount.get(), connectors.size());
    }

    /**
     * 执行采集
     */
    private void executeCollect(Connector connector) {
        try {
            List<ObservationData> observations = connector.collect();
            if (observations != null && !observations.isEmpty()) {
                observationSender.send(observations);
                eventBus.publish(AgentEventBus.OBSERVATION_COLLECTED, observations.size());
            }
        } catch (Exception e) {
            log.error("Collect failed for connector: id={}", connector.getConnectorId(), e);
            eventBus.publish(AgentEventBus.CONNECTOR_FAILED, connector.getConnectorId());
        }
    }

    /**
     * 停止所有 Connector
     */
    public void stopAll() {
        log.info("Stopping all connectors...");
        List<Connector> connectors = registry.getAll();
        for (Connector connector : connectors) {
            try {
                connector.stop();
                connector.destroy();
                eventBus.publish(AgentEventBus.CONNECTOR_STOPPED, connector.getConnectorId());
                log.info("Connector stopped: id={}", connector.getConnectorId());
            } catch (Exception e) {
                log.error("Error stopping connector: id={}", connector.getConnectorId(), e);
            }
        }
        runningCount.set(0);
        log.info("All connectors stopped");
    }

    /**
     * 返回运行中的 Connector 数量
     */
    public int getRunningCount() {
        return runningCount.get();
    }

    /**
     * 根据 Connector 类型创建实例
     * <p>MVP 阶段返回 null (Connector 在 WP003+ 具体实现时补全)。
     * 此处为工厂方法占位，通过反射或 SPI 加载（后续 WP 完善）。
     */
    private Connector createConnector(AgentConfig.ConnectorConfigItem item) {
        String type = item.getType();
        if (type == null) {
            return null;
        }
        log.info("Creating connector for type={}, interval={}ms", type, item.getIntervalMs());

        // WP003: JVM Connector (真实 JMX 采集)
        if ("JVM".equalsIgnoreCase(type)) {
            return new JvmConnector();
        }

        // WP005+: Linux/Redis/MySQL Connector 在对应 WP 实现时补全
        if ("LINUX".equalsIgnoreCase(type)) {
            return new LinuxConnector();
        }

        if ("REDIS".equalsIgnoreCase(type)) {
            return new RedisConnector();
        }

        if ("MYSQL".equalsIgnoreCase(type)) {
            return new MySQLConnector();
        }

        log.warn("Unknown connector type={}, skipping", type);
        return null;
    }

    /**
     * 创建占位 Connector（等待 WP003+ 完整实现）
     * <p>该类不执行实际采集，但保证 Agent 流程完整可运行。
     */
    private Connector createPlaceholderConnector(AgentConfig.ConnectorConfigItem item) {
        String type = item.getType();
        if (type == null) {
            return null;
        }
        final String connectorId = config.getAgentId() + "-" + type.toLowerCase();
        final String intervalMs = String.valueOf(item.getIntervalMs() != null ? item.getIntervalMs() : 30000L);

        return new Connector() {
            private volatile ConnectorState state = ConnectorState.CREATED;
            private ConnectorContext context;

            @Override
            public String getConnectorId() {
                return connectorId;
            }

            @Override
            public String getConnectorType() {
                return type;
            }

            @Override
            public String getTargetResource() {
                return "placeholder-target";
            }

            @Override
            public void init(ConnectorContext context) {
                this.context = context;
                this.state = ConnectorState.INITIALIZED;
                log.info("Placeholder connector initialized: id={}, type={}", connectorId, type);
            }

            @Override
            public List<ObservationData> collect() {
                log.debug("Placeholder connector {} collect triggered (no real data)", connectorId);
                return java.util.Collections.emptyList();
            }

            @Override
            public void start() {
                this.state = ConnectorState.RUNNING;
                log.info("Placeholder connector started: id={}", connectorId);
            }

            @Override
            public void stop() {
                this.state = ConnectorState.STOPPED;
            }

            @Override
            public void destroy() {
                this.state = ConnectorState.STOPPED;
            }

            @Override
            public ConnectorState getStatus() {
                return state;
            }
        };
    }

    private long getIntervalForConnector(Connector connector) {
        // 从配置中匹配 interval
        if (config.getConnectors() != null) {
            for (AgentConfig.ConnectorConfigItem item : config.getConnectors()) {
                if (item.getType() != null && item.getType().equals(connector.getConnectorType())) {
                    return item.getIntervalMs() != null ? item.getIntervalMs() : 30000L;
                }
            }
        }
        return 30000L;
    }

    public ConnectorRegistry getRegistry() {
        return registry;
    }

    public boolean isInitialized() {
        return initialized.get();
    }
}
