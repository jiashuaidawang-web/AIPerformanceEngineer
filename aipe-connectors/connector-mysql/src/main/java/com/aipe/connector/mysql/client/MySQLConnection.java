package com.aipe.connector.mysql.client;

import com.aipe.connector.mysql.config.MySQLConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * MySQL 连接管理
 *
 * <p>提供安全查询方法，自动关闭 Statement/ResultSet，避免资源泄漏。
 */
public class MySQLConnection {
    private static final Logger log = LoggerFactory.getLogger(MySQLConnection.class);

    private final MySQLConfig config;
    private Connection connection;

    public MySQLConnection(MySQLConfig config) {
        this.config = config;
    }

    public synchronized void connect() throws SQLException {
        if (connection == null || connection.isClosed() || !connection.isValid(1)) {
            String url = String.format("jdbc:mysql://%s:%d/%s?connectTimeout=%d&socketTimeout=%d&useSSL=false&serverTimezone=UTC&allowMultiQueries=false",
                    config.getHost(), config.getPort(), config.getDatabase(),
                    config.getTimeoutMs(), config.getTimeoutMs());
            connection = DriverManager.getConnection(url, config.getUser(), config.getPassword());
            log.info("MySQL connection established: {}:{}", config.getHost(), config.getPort());
        }
    }

    /**
     * 安全执行查询 — 自动关闭 Statement 和 ResultSet，避免资源泄漏。
     */
    public void query(String sql, ResultSetConsumer consumer) {
        ResultSet rs = null;
        Statement stmt = null;
        try {
            connect();
            stmt = connection.createStatement();
            stmt.setQueryTimeout((int)(config.getTimeoutMs() / 1000));
            rs = stmt.executeQuery(sql);
            consumer.accept(rs);
        } catch (SQLException e) {
            log.warn("Query failed [{}]: {}", sql, e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException ignored) {} }
            if (stmt != null) { try { stmt.close(); } catch (SQLException ignored) {} }
        }
    }

    public void disconnect() {
        if (connection != null) {
            try { connection.close(); } catch (SQLException e) { log.warn("Error closing connection", e); }
            connection = null;
        }
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(1);
        } catch (SQLException e) {
            return false;
        }
    }

    @FunctionalInterface
    public interface ResultSetConsumer {
        void accept(ResultSet rs) throws SQLException;
    }
}
