-- ============================================================
-- WP016: Knowledge Engine - Persistence Migration（MySQL 兼容版）
-- ============================================================
-- Knowledge 是元数据 → 落 MySQL（对齐 IM-003 / Persistence Law-001）
-- 索引由 KnowledgeMySqlMigrationConfig.createIndexes 兜底建
-- ============================================================

CREATE TABLE IF NOT EXISTS knowledge (
    pk_id               BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '内部主键（支持同知识多版本）',
    id                  VARCHAR(64) NOT NULL COMMENT '业务主键 UUID（knowledgeId）',
    title               VARCHAR(256) NOT NULL COMMENT '知识标题',
    description         TEXT COMMENT '知识描述',
    knowledge_type      VARCHAR(32) NOT NULL COMMENT 'Bottleneck/Dependency/Deployment/Business/Optimization/AI',
    evidence_id         VARCHAR(64) NOT NULL COMMENT '来源 Evidence（Domain Law-001）',
    verification_id     VARCHAR(64) COMMENT '验证记录',
    confidence          DOUBLE NOT NULL DEFAULT 50.0 COMMENT '最终可信度 0~100',
    applicable_conditions JSON COMMENT '适用条件',
    recommendation      JSON COMMENT '推荐方案',
    success_rate        DOUBLE NOT NULL DEFAULT 0.0 COMMENT '历史成功率 0~100',
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version             INT NOT NULL DEFAULT 1 COMMENT '版本号（每新版本新记录）',
    INDEX idx_knowledge_id (id),
    INDEX idx_evidence (evidence_id),
    INDEX idx_type (knowledge_type),
    INDEX idx_confidence (confidence),
    UNIQUE uk_knowledge_version (id, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
