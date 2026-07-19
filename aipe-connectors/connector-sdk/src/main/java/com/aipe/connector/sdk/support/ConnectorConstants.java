package com.aipe.connector.sdk.support;

public final class ConnectorConstants {
    private ConnectorConstants() {}

    public static final String TYPE_JVM = "JVM";
    public static final String TYPE_LINUX = "LINUX";
    public static final String TYPE_REDIS = "REDIS";
    public static final String TYPE_MYSQL = "MYSQL";

    public static final long DEFAULT_INTERVAL_MS = 30000L;
    public static final long DEFAULT_TIMEOUT_MS = 10000L;
    public static final int DEFAULT_QUEUE_CAPACITY = 10000;
    public static final int DEFAULT_BATCH_SIZE = 100;

    public static final String PROP_HOST = "host";
    public static final String PROP_PORT = "port";
    public static final String PROP_USER = "user";
    public static final String PROP_PASSWORD = "password";
    public static final String PROP_PROC_PATH = "procPath";
}
