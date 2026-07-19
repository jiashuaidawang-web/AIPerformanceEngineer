package com.aipe.connector.mysql.client;

import com.aipe.connector.mysql.config.MySQLConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLConnection {
    private static final Logger log = LoggerFactory.getLogger(MySQLConnection.class);

    private final MySQLConfig config;
    private Connection connection;

    public MySQLConnection(MySQLConfig config) {
        this.config = config;
    }

    public void connect() throws SQLException {
        String url = String.format("jdbc:mysql://%s:%d/%s?connectTimeout=%d&socketTimeout=%d&useSSL=false&serverTimezone=UTC",
                config.getHost(), config.getPort(), config.getDatabase(),
                config.getTimeoutMs(), config.getTimeoutMs());
        this.connection = DriverManager.getConnection(url, config.getUser(), config.getPassword());
        log.info("MySQL connection established: {}:{}", config.getHost(), config.getPort());
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
        Statement stmt = connection.createStatement();
        stmt.setQueryTimeout((int)(config.getTimeoutMs() / 1000));
        return stmt.executeQuery(sql);
    }

    public void disconnect() {
        if (connection != null) {
            try { connection.close(); } catch (SQLException e) { log.warn("Error closing connection", e); }
        }
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid((int)(config.getTimeoutMs() / 1000));
        } catch (SQLException e) {
            return false;
        }
    }

    public MySQLConfig getConfig() { return config; }
}
