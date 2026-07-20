-- ============================================================
-- WP011: Resource Domain - ALTER COLUMNS（纯 SQL）
-- ============================================================
-- 由 Spring DataSourceInitializer 自动执行
-- 使用 SET + PREPARE 实现幂等（兼容 MySQL 5.x / 8.x）
-- ============================================================

-- 添加 resource_name 列
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
