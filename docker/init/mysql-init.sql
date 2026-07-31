-- AI Performance Engineer - MySQL 初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS aipe_metadata CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE aipe_metadata;

-- Resource 表
CREATE TABLE IF NOT EXISTS resource (
    resource_id VARCHAR(64) PRIMARY KEY,
    resource_name VARCHAR(255),
    resource_type VARCHAR(32),
    resource_category VARCHAR(32),
    business_system VARCHAR(128),
    parent_resource_id VARCHAR(64),
    cluster VARCHAR(64),
    namespace VARCHAR(64),
    environment VARCHAR(32),
    status VARCHAR(32),
    version INT DEFAULT 1,
    labels JSON,
    attributes JSON,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_type (resource_type),
    INDEX idx_status (status),
    INDEX idx_business (business_system)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Relationship 表
CREATE TABLE IF NOT EXISTS relationship (
    id VARCHAR(64) PRIMARY KEY,
    relationship_type VARCHAR(32),
    source_resource_id VARCHAR(64),
    target_resource_id VARCHAR(64),
    direction VARCHAR(16),
    confidence FLOAT,
    status VARCHAR(32),
    properties JSON,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_source (source_resource_id),
    INDEX idx_target (target_resource_id),
    INDEX idx_type (relationship_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Evidence 表
CREATE TABLE IF NOT EXISTS evidence (
    id VARCHAR(64) PRIMARY KEY,
    evidence_type VARCHAR(32),
    title VARCHAR(255),
    description TEXT,
    root_resource_id VARCHAR(64),
    observation_ids JSON,
    relationship_ids JSON,
    timeline_id VARCHAR(64),
    confidence FLOAT,
    status VARCHAR(32),
    reasoning_steps JSON,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INT DEFAULT 1,
    INDEX idx_root (root_resource_id),
    INDEX idx_status (status),
    INDEX idx_type (evidence_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Knowledge 表
CREATE TABLE IF NOT EXISTS knowledge (
    pk_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    knowledge_id VARCHAR(64) NOT NULL,
    title VARCHAR(255),
    description TEXT,
    knowledge_type VARCHAR(32),
    evidence_id VARCHAR(64),
    confidence FLOAT,
    success_rate FLOAT,
    applicable_conditions JSON,
    recommended_action TEXT,
    expected_effect TEXT,
    version INT DEFAULT 1,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_id_version (knowledge_id, version),
    INDEX idx_type (knowledge_type),
    INDEX idx_evidence (evidence_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Recommendation 表
CREATE TABLE IF NOT EXISTS recommendation (
    id VARCHAR(64) PRIMARY KEY,
    knowledge_id VARCHAR(64),
    target_resource_id VARCHAR(64),
    title VARCHAR(255),
    description TEXT,
    priority VARCHAR(16),
    confidence FLOAT,
    status VARCHAR(32),
    execution_plan JSON,
    rollback_plan JSON,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_knowledge (knowledge_id),
    INDEX idx_target (target_resource_id),
    INDEX idx_status (status),
    INDEX idx_priority (priority)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Execution 表
CREATE TABLE IF NOT EXISTS execution (
    pk_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    execution_id VARCHAR(64) NOT NULL,
    recommendation_id VARCHAR(64),
    executor VARCHAR(64),
    execution_type VARCHAR(32),
    status VARCHAR(32),
    improvement_score FLOAT,
    before_snapshot JSON,
    after_snapshot JSON,
    execution_report TEXT,
    started_at TIMESTAMP NULL,
    finished_at TIMESTAMP NULL,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INT DEFAULT 1,
    UNIQUE KEY uk_id_version (execution_id, version),
    INDEX idx_recommendation (recommendation_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Agent 表
CREATE TABLE IF NOT EXISTS agent (
    agent_id VARCHAR(64) PRIMARY KEY,
    server_id VARCHAR(64),
    hostname VARCHAR(128),
    ip VARCHAR(64),
    status VARCHAR(32),
    last_heartbeat TIMESTAMP NULL,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_server (server_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入演示数据
INSERT INTO resource (resource_id, resource_name, resource_type, resource_category, business_system, status) VALUES
('order-svc-001', '订单服务', 'SERVICE', 'BUSINESS', '电商系统', 'RUNNING'),
('product-svc-001', '商品服务', 'SERVICE', 'BUSINESS', '电商系统', 'RUNNING'),
('pay-svc-001', '支付服务', 'SERVICE', 'BUSINESS', '电商系统', 'RUNNING'),
('redis-order', '订单Redis', 'REDIS', 'PLATFORM', '电商系统', 'RUNNING'),
('mysql-order', '订单MySQL', 'DATABASE', 'PLATFORM', '电商系统', 'RUNNING')
ON DUPLICATE KEY UPDATE updated_time = CURRENT_TIMESTAMP;
