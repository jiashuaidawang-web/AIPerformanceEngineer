-- ============================================================
-- WP011: Resource Domain - Persistence Migration
-- 升级 resource 表以对齐 IM-003 Persistence Mapping 规范
-- ============================================================
-- 由 Spring Boot DataSourceInitializer 自动执行
-- 兼容 MySQL 5.x / 8.x（幂等，可重复执行）
-- ============================================================

-- 添加 resource_name 列（如果不存在）
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'resource' AND COLUMN_NAME = 'resource_name');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE resource ADD COLUMN resource_name VARCHAR(256) NULL AFTER id', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 parent_resource_id 列
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'resource' AND COLUMN_NAME = 'parent_resource_id');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE resource ADD COLUMN parent_resource_id VARCHAR(64) NULL AFTER resource_name', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 business_system 列
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'resource' AND COLUMN_NAME = 'business_system');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE resource ADD COLUMN business_system VARCHAR(128) NULL AFTER parent_resource_id', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 cluster 列
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'resource' AND COLUMN_NAME = 'cluster');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE resource ADD COLUMN cluster VARCHAR(128) NULL AFTER business_system', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 namespace 列
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'resource' AND COLUMN_NAME = 'namespace');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE resource ADD COLUMN namespace VARCHAR(128) NULL AFTER cluster', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 environment 列
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'resource' AND COLUMN_NAME = 'environment');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE resource ADD COLUMN environment VARCHAR(64) NULL AFTER namespace', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 status 列
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'resource' AND COLUMN_NAME = 'status');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE resource ADD COLUMN status VARCHAR(16) NOT NULL DEFAULT ''RUNNING'' AFTER environment', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 version 列
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'resource' AND COLUMN_NAME = 'version');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE resource ADD COLUMN version INT NOT NULL DEFAULT 1 AFTER status', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 deleted 列（逻辑删除）
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'resource' AND COLUMN_NAME = 'deleted');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE resource ADD COLUMN deleted TINYINT NOT NULL DEFAULT 0', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加索引（MySQL 5.x 兼容，使用存储过程幂等）
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS create_index_if_not_exists()
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'resource' AND INDEX_NAME = 'idx_resource_business_system') THEN
        CREATE INDEX idx_resource_business_system ON resource(business_system);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'resource' AND INDEX_NAME = 'idx_resource_type') THEN
        CREATE INDEX idx_resource_type ON resource(resource_type);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'resource' AND INDEX_NAME = 'idx_resource_status') THEN
        CREATE INDEX idx_resource_status ON resource(status);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'resource' AND INDEX_NAME = 'idx_resource_created') THEN
        CREATE INDEX idx_resource_created ON resource(created_at);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'resource' AND INDEX_NAME = 'idx_resource_parent') THEN
        CREATE INDEX idx_resource_parent ON resource(parent_resource_id);
    END IF;
END //
DELIMITER ;

CALL create_index_if_not_exists();
DROP PROCEDURE IF EXISTS create_index_if_not_exists();
