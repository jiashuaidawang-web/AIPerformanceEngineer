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

        try {
            // Version
            ResultSet rs = connection.executeQuery("SELECT VERSION() as version");
            if (rs.next()) info.setVersion(rs.getString("version"));
            rs.close();

            // Server ID
            try {
                ResultSet idRs = connection.executeQuery("SHOW VARIABLES LIKE 'server_id'");
                if (idRs.next()) info.setServerId(idRs.getString("Value"));
                idRs.close();
            } catch (Exception e) { /* ignore */ }

            // Hostname
            try {
                ResultSet hostRs = connection.executeQuery("SELECT @@hostname as hostname");
                if (hostRs.next()) info.setHostname(hostRs.getString("hostname"));
                hostRs.close();
            } catch (Exception e) { /* ignore */ }

            info.setConnected(true);
            log.info("MySQL discovered: version={}, host={}, port={}", info.getVersion(), info.getHost(), info.getPort());

        } catch (Exception e) {
            log.warn("MySQL discovery failed: {}", e.getMessage());
            info.setConnected(false);
        }

        return info;
    }
}
