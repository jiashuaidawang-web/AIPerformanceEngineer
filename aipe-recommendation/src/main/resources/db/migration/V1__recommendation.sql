-- ============================================================
-- WP017: Recommendation Engine - Persistence Migration（MySQL 兼容版）
-- ============================================================
-- Recommendation 是元数据 → 落 MySQL（对齐 IM-003 / Persistence Law-001）
-- ============================================================

CREATE TABLE IF NOT EXISTS recommendation (
    id                  VARCHAR(64) PRIMARY KEY COMMENT '业务主键 UUID',
    knowledge_id        VARCHAR(64) NOT NULL COMMENT '来源 Knowledge（Domain Law-001）',
    target_resource_id  VARCHAR(64) NOT NULL COMMENT '目标 Resource',
    title               VARCHAR(256) NOT NULL,
    description         TEXT,
    priority            VARCHAR(16) NOT NULL DEFAULT 'MEDIUM' COMMENT 'HIGH/MEDIUM/LOW',
    confidence          DOUBLE NOT NULL DEFAULT 50.0,
    expected_outcome    TEXT COMMENT '预期效果',
    execution_plan      JSON COMMENT '执行计划',
    rollback_plan       JSON COMMENT '回滚方案',
    status              VARCHAR(16) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED/EXECUTED',
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version             INT NOT NULL DEFAULT 1,
    INDEX idx_knowledge (knowledge_id),
    INDEX idx_target_resource (target_resource_id),
    INDEX idx_status (status),
    INDEX idx_priority (priority)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
