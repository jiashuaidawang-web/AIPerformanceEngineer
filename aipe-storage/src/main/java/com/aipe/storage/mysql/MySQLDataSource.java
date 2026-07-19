package com.aipe.storage.mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class MySQLDataSource {
    private static final Logger log = LoggerFactory.getLogger(MySQLDataSource.class);

    private DataSource dataSource;

    public void init() {
        log.info("MySQLDataSource initialized");
    }

    public DataSource getDataSource() { return dataSource; }
}
