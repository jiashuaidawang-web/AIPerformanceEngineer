package com.aipe.connector.mysql;

import com.aipe.common.domain.ObservationData;
import com.aipe.connector.mysql.client.MySQLConnection;
import com.aipe.connector.mysql.client.MySQLClientFactory;
import com.aipe.connector.mysql.collector.MySQLCollector;
import com.aipe.connector.mysql.collector.bufferpool.BufferPoolCollector;
import com.aipe.connector.mysql.collector.connection.ConnectionCollector;
import com.aipe.connector.mysql.collector.index.IndexCollector;
import com.aipe.connector.mysql.collector.lock.LockCollector;
import com.aipe.connector.mysql.collector.processlist.ProcessListCollector;
import com.aipe.connector.mysql.collector.server.ServerStatusCollector;
import com.aipe.connector.mysql.collector.slowquery.SlowQueryCollector;
import com.aipe.connector.mysql.collector.transaction.TransactionCollector;
import com.aipe.connector.mysql.config.MySQLConfig;
import com.aipe.connector.mysql.discovery.MySQLInstanceDiscovery;
import com.aipe.connector.mysql.model.MySQLNodeInfo;
import com.aipe.connector.mysql.permission.MySQLPermissionChecker;
import com.aipe.connector.sdk.abstractapi.AbstractConnector;
import com.aipe.connector.sdk.config.ConnectorConfig;
import com.aipe.connector.sdk.context.ConnectorContext;
import com.aipe.connector.sdk.exception.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

/**
 * MySQL Connector
 *
 * <p>使用 JDBC 真实连接 MySQL，通过 SQL 查询采集性能数据。
 * 支持：SHOW GLOBAL STATUS, SHOW PROCESSLIST, performance_schema, InnoDB status
 */
public class MySQLConnector extends AbstractConnector {

    private static final Logger log = LoggerFactory.getLogger(MySQLConnector.class);

    private MySQLConfig mysqlConfig;
    private MySQLConnection connection;
    private final List<MySQLCollector> collectors = new CopyOnWriteArrayList<>();
    private String agentId;
    private String connectorId;

    @Override
    public String getConnectorId() {
        if (connectorId != null) return connectorId;
        if (mysqlConfig != null && mysqlConfig.getConnectorId() != null) return mysqlConfig.getConnectorId();
        return "mysql-" + mysqlConfig.getHost() + "-" + mysqlConfig.getPort();
    }

    @Override
    public String getConnectorType() {
        return "MYSQL";
    }

    @Override
    public String getTargetResource() {
        if (mysqlConfig != null) return mysqlConfig.getTargetResource();
        return "mysql-" + mysqlConfig.getHost() + ":" + mysqlConfig.getPort();
    }

    @Override
    protected void onInit(ConnectorContext context) throws ConnectorException {
        if (context != null) {
            this.agentId = context.getAgentId();
            ConnectorConfig sdkConfig = context.getConfig();
            if (sdkConfig != null) {
                this.connectorId = sdkConfig.getConnectorId();
                this.mysqlConfig = MySQLConfig.fromConnectorConfig(sdkConfig);
            } else {
                this.mysqlConfig = MySQLConfig.defaultConfig();
            }
        } else {
            this.mysqlConfig = MySQLConfig.defaultConfig();
        }

        if (this.connectorId == null) this.connectorId = getConnectorId();
        initCollectors();
        log.info("MySQLConnector initialized. agentId={}, host={}, port={}", agentId, mysqlConfig.getHost(), mysqlConfig.getPort());
    }

    private void initCollectors() {
        collectors.add(new ServerStatusCollector());
        collectors.add(new ConnectionCollector());
        collectors.add(new ProcessListCollector());
        collectors.add(new SlowQueryCollector());
        collectors.add(new LockCollector());
        collectors.add(new BufferPoolCollector());
        collectors.add(new IndexCollector());
        collectors.add(new TransactionCollector());
        log.info("Initialized {} MySQL collectors", collectors.size());
    }

    @Override
    protected void onStart() throws ConnectorException {
        log.info("MySQLConnector starting... agentId={}", agentId);
        try {
            connection = MySQLClientFactory.create(mysqlConfig);
            connection.connect();

            // Check permissions
            MySQLPermissionChecker permissionChecker = new MySQLPermissionChecker();
            MySQLPermissionChecker.PermissionReport report = permissionChecker.checkAll(connection);
            log.info("MySQL permissions: select={}, showView={}, perfSchema={}",
                    report.isCanSelect(), report.isCanShowView(), report.isCanAccessPerformanceSchema());

            // Discovery
            MySQLInstanceDiscovery discovery = new MySQLInstanceDiscovery();
            MySQLNodeInfo nodeInfo = discovery.discover(connection, mysqlConfig);
            log.info("MySQL node discovered: version={}, host={}", nodeInfo.getVersion(), nodeInfo.getHost());

        } catch (Exception e) {
            throw new ConnectorException(getConnectorId(), "Failed to connect to MySQL", e);
        }
    }

    @Override
    public List<ObservationData> collect() throws ConnectorException {
        List<ObservationData> allResults = new ArrayList<>();
        if (connection == null || !connection.isConnected()) {
            log.warn("MySQL connection not available, attempting reconnect");
            try { connection.connect(); }
            catch (Exception e) { throw new ConnectorException(getConnectorId(), "Reconnect failed", e); }
        }

        for (MySQLCollector collector : collectors) {
            try {
                List<ObservationData> data = collector.collect(connection, agentId, connectorId);
                if (data != null && !data.isEmpty()) allResults.addAll(data);
            } catch (Exception e) {
                log.error("MySQL collector {} failed: {}", collector.getCollectorName(), e.getMessage(), e);
            }
        }

        return allResults;
    }

    @Override
    protected void onStop() { log.info("MySQLConnector stopping..."); }

    @Override
    protected void onDestroy() {
        if (connection != null) { connection.disconnect(); connection = null; }
        collectors.clear();
        log.info("MySQLConnector destroyed.");
    }

    public List<MySQLCollector> getCollectors() { return new ArrayList<>(collectors); }
    public MySQLConfig getMySQLConfig() { return mysqlConfig; }
}
