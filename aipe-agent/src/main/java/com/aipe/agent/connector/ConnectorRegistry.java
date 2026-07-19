package com.aipe.agent.connector;

import com.aipe.connector.sdk.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Connector 注册中心
 *
 * <p>保存所有已注册的 Connector 实例，线程安全的 ConcurrentHashMap。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class ConnectorRegistry {

    private static final Logger log = LoggerFactory.getLogger(ConnectorRegistry.class);

    private final ConcurrentHashMap<String, Connector> connectors = new ConcurrentHashMap<>();

    /**
     * 注册 Connector
     *
     * @param connector Connector 实例
     */
    public void register(Connector connector) {
        if (connector == null) {
            throw new IllegalArgumentException("Connector must not be null");
        }
        String connectorId = connector.getConnectorId();
        if (connectorId == null || connectorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Connector ID must not be empty");
        }
        Connector existing = connectors.putIfAbsent(connectorId, connector);
        if (existing != null) {
            log.warn("Connector already registered with id={}, ignoring registration", connectorId);
            return;
        }
        log.info("Connector registered: id={}, type={}", connectorId, connector.getConnectorType());
    }

    /**
     * 注销 Connector
     *
     * @param connectorId Connector 唯一标识
     */
    public void unregister(String connectorId) {
        if (connectorId == null) {
            return;
        }
        Connector removed = connectors.remove(connectorId);
        if (removed != null) {
            log.info("Connector unregistered: id={}", connectorId);
        }
    }

    /**
     * 根据 ID 获取 Connector
     *
     * @param connectorId Connector 唯一标识
     * @return Connector 或 null
     */
    public Connector get(String connectorId) {
        return connectors.get(connectorId);
    }

    /**
     * 返回所有已注册 Connector
     *
     * @return Connector 列表（不可修改快照）
     */
    public List<Connector> getAll() {
        return new ArrayList<>(connectors.values());
    }

    /**
     * 按类型获取 Connector
     *
     * @param type Connector 类型
     * @return 该类型的 Connector 列表
     */
    public List<Connector> getByType(String type) {
        if (type == null) {
            return new ArrayList<>();
        }
        return connectors.values().stream()
                .filter(c -> type.equals(c.getConnectorType()))
                .collect(Collectors.toList());
    }

    /**
     * 返回已注册数量
     *
     * @return 数量
     */
    public int size() {
        return connectors.size();
    }

    /**
     * 清空注册表
     */
    public void clear() {
        connectors.clear();
        log.info("Connector registry cleared");
    }
}
