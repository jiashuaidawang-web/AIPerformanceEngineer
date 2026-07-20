-- ============================================================
-- WP011: Resource Domain - Persistence Migration（MySQL 8.0 简化版）
-- ============================================================
-- 由 Spring Boot DataSourceInitializer 自动执行
-- MySQL 8.0（Docker Compose）支持 ADD COLUMN IF NOT EXISTS
-- ============================================================

-- 添加 resource_name 列
ALTER TABLE resource
    ADD COLUMN IF NOT EXISTS resource_name VARCHAR(256) NULL COMMENT '资源名称' AFTER id,
    ADD COLUMN IF NOT EXISTS parent_resource_id VARCHAR(64) NULL COMMENT '父资源ID' AFTER resource_name,
    ADD COLUMN IF NOT EXISTS business_system VARCHAR(128) NULL COMMENT '业务系统' AFTER parent_resource_id,
    ADD COLUMN IF NOT EXISTS cluster VARCHAR(128) NULL COMMENT '集群标识' AFTER business_system,
    ADD COLUMN IF NOT EXISTS namespace VARCHAR(128) NULL COMMENT '命名空间' AFTER cluster,
    ADD COLUMN IF NOT EXISTS environment VARCHAR(64) NULL COMMENT '环境标识' AFTER namespace,
    ADD COLUMN IF NOT EXISTS status VARCHAR(16) NOT NULL DEFAULT 'RUNNING' COMMENT '资源状态' AFTER environment,
    ADD COLUMN IF NOT EXISTS version INT NOT NULL DEFAULT 1 COMMENT '乐观锁版本号' AFTER status,
    ADD COLUMN IF NOT EXISTS deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除' AFTER version;

-- 添加索引
CREATE INDEX IF NOT EXISTS idx_resource_business_system ON resource(business_system);
CREATE INDEX IF NOT EXISTS idx_resource_type ON resource(resource_type);
CREATE INDEX IF NOT EXISTS idx_resource_status ON resource(status);
CREATE INDEX IF NOT EXISTS idx_resource_created ON resource(created_at);
CREATE INDEX IF NOT EXISTS idx_resource_parent ON resource(parent_resource_id);
