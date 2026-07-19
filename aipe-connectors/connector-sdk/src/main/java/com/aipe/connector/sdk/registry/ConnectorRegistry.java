package com.aipe.connector.sdk.registry;

import com.aipe.connector.sdk.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectorRegistry {
    private static final Logger log = LoggerFactory.getLogger(ConnectorRegistry.class);
    private final ConcurrentHashMap<String, Connector> connectors = new ConcurrentHashMap<>();

    public void register(Connector connector) {
        if (connector == null) throw new IllegalArgumentException("Connector must not be null");
        connectors.putIfAbsent(connector.getConnectorId(), connector);
        log.info("Connector registered in SDK registry: id={}", connector.getConnectorId());
    }

    public void unregister(String connectorId) {
        Connector removed = connectors.remove(connectorId);
        if (removed != null) log.info("Connector unregistered: id={}", connectorId);
    }

    public Connector get(String connectorId) { return connectors.get(connectorId); }
    public List<Connector> getAll() { return new ArrayList<>(connectors.values()); }
    public int size() { return connectors.size(); }
    public void clear() { connectors.clear(); }
}
