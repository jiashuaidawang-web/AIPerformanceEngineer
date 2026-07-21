-- ============================================================
# StressDemo 靶机 — MySQL 初始化脚本
# 执行: mysql -uroot -p < init.sql
# ============================================================

CREATE DATABASE IF NOT EXISTS stress_demo DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE stress_demo;

-- 产品表 (压测主表)
CREATE TABLE IF NOT EXISTS stress_product (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(256) NOT NULL,
    category    VARCHAR(64),
    price       DECIMAL(10,2),
    stock       INT DEFAULT 0,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入 10 万条测试数据 (用于全表扫描压测)
-- 注意: 如果数据已存在则跳过
INSERT IGNORE INTO stress_product (id, name, category, price, stock, created_at)
SELECT
    n AS id,
    CONCAT('Product_', LPAD(n, 6, '0')) AS name,
    ELT(1 + FLOOR(RAND() * 5), 'Electronics', 'Clothing', 'Food', 'Books', 'Home') AS category,
    ROUND(RAND() * 1000, 2) AS price,
    FLOOR(RAND() * 1000) AS stock,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY) AS created_at
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 + d.N * 1000 + e.N * 10000 + 1 AS n
    FROM
        (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
        (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
        (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c,
        (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) d,
        (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) e
    ORDER BY n
    LIMIT 100000
) numbers;
