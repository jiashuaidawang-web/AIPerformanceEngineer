package com.aipe.connector.mysql.discovery;

import com.aipe.connector.mysql.client.MySQLConnection;
import com.aipe.connector.mysql.config.MySQLConfig;
import com.aipe.connector.mysql.model.MySQLNodeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;

public class MySQLInstanceDiscovery {
    private static final Logger log = LoggerFactory.getLogger(MySQLInstanceDiscovery.class);

    public MySQLNodeInfo discover(MySQLConnection connection, MySQLConfig config) {
        MySQLNodeInfo info = new MySQLNodeInfo();
        info.setHost(config.getHost());
        info.setPort(config.getPort());

        connection.query("SELECT VERSION() as version", rs -> {
            try { if (rs.next()) info.setVersion(rs.getString("version")); } catch (Exception e) {}
        });
        connection.query("SHOW VARIABLES LIKE 'server_id'", rs -> {
            try { if (rs.next()) info.setServerId(rs.getString("Value")); } catch (Exception e) {}
        });
        connection.query("SELECT @@hostname as hostname", rs -> {
            try { if (rs.next()) info.setHostname(rs.getString("hostname")); } catch (Exception e) {}
        });

        info.setConnected(true);
        log.info("MySQL discovered: version={}, host={}, port={}", info.getVersion(), info.getHost(), info.getPort());
        return info;
    }
}
