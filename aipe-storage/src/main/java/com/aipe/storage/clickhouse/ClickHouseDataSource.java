package com.aipe.storage.clickhouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ClickHouseDataSource {
    private static final Logger log = LoggerFactory.getLogger(ClickHouseDataSource.class);

    private String host;
    private int port;
    private String database;
    private String user;
    private String password;

    public void init() {
        log.info("ClickHouseDataSource initialized: {}:{}/{}", host, port, database);
    }

    public Connection getConnection() throws SQLException {
        String url = String.format("jdbc:clickhouse://%s:%d/%s", host, port, database);
        return DriverManager.getConnection(url, user, password);
    }
}
