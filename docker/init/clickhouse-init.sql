-- AI Performance Engineer - ClickHouse 初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS metric_observation;

-- 创建数据表
CREATE TABLE IF NOT EXISTS metric_observation.observation_fact (
    id UUID DEFAULT generateUUIDv4(),
    timestamp DateTime DEFAULT now(),
    resource_id String,
    resource_type String DEFAULT 'HOST',
    metric_name String,
    metric_value Float64,
    labels String DEFAULT ''
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(timestamp)
ORDER BY (resource_id, metric_name, timestamp)
TTL timestamp + INTERVAL 365 DAY;

-- 插入演示数据 (最近 24 小时)
INSERT INTO metric_observation.observation_fact (timestamp, resource_id, resource_type, metric_name, metric_value, labels)
SELECT
    now() - INTERVAL number MINUTE,
    'order-svc-001',
    'SERVICE',
    arrayJoin(['cpu.usage', 'memory.usage', 'tps', 'latency.p99'])[modulo(number, 4) + 1],
    arrayJoin([45.0 + rand() % 30, 60.0 + rand() % 20, 1200.0 + rand() % 500, 50.0 + rand() % 50])[modulo(number, 4) + 1],
    '{}'
FROM numbers(1440);
