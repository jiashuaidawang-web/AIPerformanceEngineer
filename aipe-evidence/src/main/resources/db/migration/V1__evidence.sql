-- ============================================================
-- WP015: Evidence Engine - Persistence Migration（MySQL 兼容版）
-- ============================================================
-- Evidence 是元数据 → 落 MySQL（对齐 IM-003 / Persistence Law-001）
-- 索引由 EvidenceMySqlMigrationConfig.createIndexes 兜底建
-- ============================================================

CREATE TABLE IF NOT EXISTS evidence (
    id              VARCHAR(64) PRIMARY KEY COMMENT '业务主键 UUID',
    evidence_type   VARCHAR(32) NOT NULL COMMENT 'Performance/Dependency/Deployment/Business/AI/Composite',
    title           VARCHAR(256) NOT NULL COMMENT '证据标题',
    description     TEXT COMMENT 'AI 解释（自然语言）',
    root_resource_id VARCHAR(64) NOT NULL COMMENT '根 Resource',
    observation_ids JSON COMMENT '引用的 Observation ID 列表',
    relationship_ids JSON COMMENT '引用的 Relationship ID 列表',
    timeline_id     VARCHAR(64) COMMENT '引用的 Timeline ID',
    confidence      DOUBLE NOT NULL DEFAULT 50.0 COMMENT 'AI 可信度 0~100（默认 50 不确定）',
    reasoning_steps JSON COMMENT '推理步骤（JSON）',
    status          VARCHAR(16) NOT NULL DEFAULT 'NEW' COMMENT 'NEW/VERIFIED/REJECTED',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version         INT NOT NULL DEFAULT 1 COMMENT '乐观锁版本号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
