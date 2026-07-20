-- ============================================================
-- WP013: Relationship + Topology - Persistence Migration（MySQL 8.0 简化版）
-- ============================================================
-- 由 Spring Boot DataSourceInitializer 自动执行
-- Relationship Primary Storage = MySQL（对齐 IM-003 / IM-005）
-- Topology 不持久化（Architecture Law-004）
-- ============================================================

CREATE TABLE IF NOT EXISTS relationship (
    id              VARCHAR(64) PRIMARY KEY COMMENT '业务主键 UUID',
    relationship_type VARCHAR(32) NOT NULL COMMENT 'DEPENDS_ON/CALLS/RUNS_ON/BELONGS_TO/...',
    source_resource_id VARCHAR(64) NOT NULL COMMENT '源 Resource',
    target_resource_id VARCHAR(64) NOT NULL COMMENT '目标 Resource',
    direction       VARCHAR(16) NOT NULL DEFAULT 'SINGLE' COMMENT 'SINGLE/BIDIRECTIONAL',
    confidence      DOUBLE NOT NULL DEFAULT 100.0 COMMENT '置信度 0~100',
    discovered_by   VARCHAR(64) NOT NULL DEFAULT 'MANUAL' COMMENT 'Discovery/AI/MANUAL',
    status          VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/INACTIVE/ARCHIVED',
    labels          JSON COMMENT '扩展属性',
    discovered_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 索引由 RelationshipDatabaseMigrationConfig.createIndexes 兜底（MySQL 5.7/8.0 兼容）
