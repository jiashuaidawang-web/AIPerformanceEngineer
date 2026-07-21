-- ============================================================
-- AI Performance Engineer — MySQL 全量建表脚本
-- 数据库: aipe_metadata
-- 执行方式: mysql -uroot -p aipe_metadata < aipe_mysql_init.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS aipe_metadata DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE aipe_metadata;

-- ============================================================
-- WP011 Resource (基线表 — 若已存在 resource 表则用 ALTER 升级)
-- ============================================================
CREATE TABLE IF NOT EXISTS resource (
    id                  VARCHAR(64) PRIMARY KEY COMMENT '业务主键 UUID',
    resource_name       VARCHAR(256) NULL COMMENT '资源名称',
    resource_type       VARCHAR(32) NULL COMMENT '资源类型',
    resource_category   VARCHAR(32) NULL COMMENT '资源分类',
    resource_status     VARCHAR(16) NOT NULL DEFAULT 'RUNNING' COMMENT '资源状态',
    parent_resource_id  VARCHAR(64) NULL COMMENT '父资源 ID',
    business_system     VARCHAR(128) NULL COMMENT '业务系统（必填 — Law-001）',
    cluster             VARCHAR(128) NULL COMMENT '集群标识',
    namespace           VARCHAR(128) NULL COMMENT '命名空间',
    environment         VARCHAR(64) NULL COMMENT '环境标识（prod/staging/test）',
    version             BIGINT NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
    labels              JSON COMMENT '业务标签',
    attributes          JSON COMMENT '扩展属性',
    enterprise_id       VARCHAR(64) NULL COMMENT '企业 ID',
    description         TEXT COMMENT '描述',
    created_by          VARCHAR(64) NULL COMMENT '创建人',
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted             TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_resource_business_system (business_system),
    INDEX idx_resource_type (resource_type),
    INDEX idx_resource_status (resource_status),
    INDEX idx_resource_created (created_at),
    INDEX idx_resource_parent (parent_resource_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- WP013 Relationship (资源关系)
-- ============================================================
CREATE TABLE IF NOT EXISTS relationship (
    id                  VARCHAR(64) PRIMARY KEY COMMENT '业务主键 UUID',
    relationship_type   VARCHAR(32) NOT NULL COMMENT 'DEPENDS_ON/CALLS/RUNS_ON/BELONGS_TO/MEMBER_OF/PART_OF/USES/HOSTS/CONNECTS_ON/DEPLOYS_ON',
    source_resource_id  VARCHAR(64) NOT NULL COMMENT '源 Resource',
    target_resource_id  VARCHAR(64) NOT NULL COMMENT '目标 Resource',
    direction           VARCHAR(16) NOT NULL DEFAULT 'SINGLE' COMMENT 'SINGLE/BIDIRECTIONAL',
    confidence          DOUBLE NOT NULL DEFAULT 100.0 COMMENT '置信度 0~100',
    discovered_by       VARCHAR(64) NOT NULL DEFAULT 'MANUAL' COMMENT 'Discovery/AI/MANUAL',
    status              VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/INACTIVE/ARCHIVED',
    labels              JSON COMMENT '扩展属性',
    discovered_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_relationship_source (source_resource_id),
    INDEX idx_relationship_target (target_resource_id),
    INDEX idx_relationship_type (relationship_type),
    INDEX idx_relationship_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- WP015 Evidence (AI 推理证据)
-- ============================================================
CREATE TABLE IF NOT EXISTS evidence (
    id                  VARCHAR(64) PRIMARY KEY COMMENT '业务主键 UUID',
    evidence_type       VARCHAR(32) NOT NULL COMMENT 'Performance/Dependency/Deployment/Business/AI/Composite',
    title               VARCHAR(256) NOT NULL COMMENT '证据标题',
    description         TEXT COMMENT 'AI 解释（自然语言）',
    root_resource_id    VARCHAR(64) NOT NULL COMMENT '根 Resource',
    observation_ids     JSON COMMENT '引用的 Observation ID 列表',
    relationship_ids    JSON COMMENT '引用的 Relationship ID 列表',
    timeline_id         VARCHAR(64) COMMENT '引用的 Timeline ID',
    confidence          DOUBLE NOT NULL DEFAULT 50.0 COMMENT 'AI 可信度 0~100（默认 50 不确定）',
    reasoning_steps     JSON COMMENT '推理步骤（JSON）',
    status              VARCHAR(16) NOT NULL DEFAULT 'NEW' COMMENT 'NEW/VERIFIED/REJECTED',
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version             INT NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
    INDEX idx_evidence_root_resource (root_resource_id),
    INDEX idx_evidence_type (evidence_type),
    INDEX idx_evidence_status (status),
    INDEX idx_evidence_confidence (confidence)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- WP016 Knowledge (知识库, 版本化)
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
    INDEX idx_knowledge_evidence (evidence_id),
    INDEX idx_knowledge_type (knowledge_type),
    INDEX idx_knowledge_confidence (confidence),
    UNIQUE uk_knowledge_version (id, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- WP017 Recommendation (优化建议)
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
    INDEX idx_recommendation_knowledge (knowledge_id),
    INDEX idx_recommendation_target_resource (target_resource_id),
    INDEX idx_recommendation_status (status),
    INDEX idx_recommendation_priority (priority)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- WP018 Execution + Optimization (执行记录, 版本化)
-- ============================================================
CREATE TABLE IF NOT EXISTS execution (
    pk_id               BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '内部主键',
    id                  VARCHAR(64) NOT NULL COMMENT '业务主键 UUID',
    recommendation_id   VARCHAR(64) NOT NULL COMMENT '来源 Recommendation',
    executor            VARCHAR(64) NOT NULL COMMENT '执行人',
    execution_type      VARCHAR(16) NOT NULL DEFAULT 'MANUAL' COMMENT 'MANUAL/SEMI_AUTO/AUTO',
    status              VARCHAR(16) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/EXECUTING/SUCCESS/FAILED/ROLLED_BACK',
    before_snapshot     JSON COMMENT '执行前 Observation 快照',
    after_snapshot      JSON COMMENT '执行后 Observation 快照',
    improvement_score   DOUBLE NOT NULL DEFAULT 0.0 COMMENT '优化评分 0~100',
    started_at          TIMESTAMP NULL COMMENT '开始时间',
    finished_at         TIMESTAMP NULL COMMENT '完成时间',
    rollback_info       JSON COMMENT '回滚信息',
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version             INT NOT NULL DEFAULT 1,
    INDEX idx_execution_id (id),
    INDEX idx_execution_recommendation (recommendation_id),
    INDEX idx_execution_status (status),
    INDEX idx_execution_time_range (started_at, finished_at),
    UNIQUE uk_execution_version (id, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
