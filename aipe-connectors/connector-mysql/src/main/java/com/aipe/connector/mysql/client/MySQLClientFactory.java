package com.aipe.connector.mysql.client;

import com.aipe.connector.mysql.config.MySQLConfig;

public class MySQLClientFactory {
    public static MySQLConnection create(MySQLConfig config) {
        return new MySQLConnection(config);
    }
    public static MySQLConnection createAndConnect(MySQLConfig config) throws Exception {
        MySQLConnection conn = new MySQLConnection(config);
        conn.connect();
        return conn;
    }
}
