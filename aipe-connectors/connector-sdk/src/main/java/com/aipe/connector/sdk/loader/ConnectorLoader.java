package com.aipe.connector.sdk.loader;

import com.aipe.connector.sdk.Connector;
import com.aipe.connector.sdk.config.ConnectorConfig;
import com.aipe.connector.sdk.exception.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class ConnectorLoader {
    private static final Logger log = LoggerFactory.getLogger(ConnectorLoader.class);

    public List<Connector> loadFromServiceLoader() {
        List<Connector> connectors = new ArrayList<>();
        try {
            ServiceLoader<Connector> loader = ServiceLoader.load(Connector.class);
            for (Connector connector : loader) {
                connectors.add(connector);
                log.info("Loaded connector via SPI: id={}", connector.getConnectorId());
            }
        } catch (Exception e) {
            log.error("Failed to load connectors via ServiceLoader", e);
        }
        return connectors;
    }

    public Connector createInstance(ConnectorConfig config) throws ConnectorException {
        log.info("Creating connector instance for type={}", config.getType());
        // WP003+ 将根据 type 路由到具体实现
        return null;
    }
}
