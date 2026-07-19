-- Storage Layer Schema
-- ClickHouse Metric Observation Table
CREATE TABLE IF NOT EXISTS metric_observation (
    id UUID DEFAULT generateUUIDv4(),
    timestamp DateTime DEFAULT now(),
    resource_id String,
    resource_type String DEFAULT 'HOST',
    metric_name String,
    metric_value Float64,
    labels String DEFAULT ''
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(timestamp)
ORDER BY (resource_id, metric_name, timestamp);

-- MySQL Metadata Tables
CREATE TABLE IF NOT EXISTS resource (
    id VARCHAR(64) PRIMARY KEY,
    resource_type VARCHAR(32) NOT NULL,
    host VARCHAR(256),
    port INT,
    labels JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS agent (
    id VARCHAR(64) PRIMARY KEY,
    server_id VARCHAR(64),
    status VARCHAR(16),
    last_heartbeat TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS connector (
    id VARCHAR(64) PRIMARY KEY,
    agent_id VARCHAR(64),
    connector_type VARCHAR(32),
    target_resource VARCHAR(256),
    status VARCHAR(16),
    config JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS observation_metadata (
    id VARCHAR(64) PRIMARY KEY,
    agent_id VARCHAR(64),
    connector_type VARCHAR(32),
    event_time BIGINT,
    receive_time BIGINT,
    metric_count INT,
    state VARCHAR(16)
);
