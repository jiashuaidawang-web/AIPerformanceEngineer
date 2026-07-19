-- ============================================================
-- AI Performance Engineer — Backend Schema (MySQL)
-- ============================================================
-- 执行方式：mysql -u root -p aipe_metadata < aipe-schema.sql
-- 或复制到 MySQL 客户端执行
-- ============================================================

USE aipe_metadata;

-- 资产资源表
CREATE TABLE IF NOT EXISTS resource (
    id VARCHAR(64) PRIMARY KEY,
    resource_type VARCHAR(32) NOT NULL,
    host VARCHAR(256),
    port INT,
    labels JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Agent 表
CREATE TABLE IF NOT EXISTS agent (
    id VARCHAR(64) PRIMARY KEY,
    server_id VARCHAR(64),
    status VARCHAR(16),
    last_heartbeat TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Connector 表
CREATE TABLE IF NOT EXISTS connector (
    id VARCHAR(64) PRIMARY KEY,
    agent_id VARCHAR(64),
    connector_type VARCHAR(32),
    target_resource VARCHAR(256),
    status VARCHAR(16),
    config JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (agent_id) REFERENCES agent(id) ON DELETE CASCADE
);

-- 采集数据元数据表
CREATE TABLE IF NOT EXISTS observation_metadata (
    id VARCHAR(64) PRIMARY KEY,
    agent_id VARCHAR(64),
    connector_type VARCHAR(32),
    event_time BIGINT NOT NULL,
    receive_time BIGINT NOT NULL,
    metric_count INT DEFAULT 0,
    state VARCHAR(16),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_agent_time (agent_id, event_time),
    INDEX idx_state (state)
);

-- 配置版本表
CREATE TABLE IF NOT EXISTS config_version (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    agent_id VARCHAR(64) NOT NULL,
    config_version VARCHAR(64) NOT NULL,
    connectors JSON,
    properties JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_agent (agent_id)
);

-- 部署记录表
CREATE TABLE IF NOT EXISTS deployment_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    deployment_id VARCHAR(64) NOT NULL,
    agent_id VARCHAR(64),
    agent_type VARCHAR(32),
    target VARCHAR(256),
    status VARCHAR(16),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_agent (agent_id)
);

-- 审计日志表
CREATE TABLE IF NOT EXISTS audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operator VARCHAR(64) DEFAULT 'admin',
    operation VARCHAR(128) NOT NULL,
    target VARCHAR(256),
    detail TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_operation (operation),
    INDEX idx_created (created_at)
);

-- ============================================================
-- ClickHouse Schema (在 ClickHouse 中执行)
-- ============================================================
-- 时序数据表，在 ClickHouse 命令行或客户端中执行：
--
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
-- ============================================================
