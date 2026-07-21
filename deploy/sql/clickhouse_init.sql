-- ============================================================
-- AI Performance Engineer — ClickHouse 全量建表脚本
-- 数据库: metric_observation
-- 执行方式: clickhouse-client --host <CH_HOST> --port 8123 -d metric_observation < aipe_clickhouse_init.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS metric_observation;

-- ============================================================
-- WP012 Observation (运行时事实 — ClickHouse Primary Storage)
-- ============================================================
CREATE TABLE IF NOT EXISTS metric_observation.observation_fact (
    observation_id  String      DEFAULT generateUUIDv4(),
    resource_id     String,
    resource_type   String      DEFAULT 'UNKNOWN',
    metric_name     String,
    metric_type     Enum8('METRIC'=1, 'LOG'=2, 'TRACE'=3, 'EVENT'=4, 'SNAPSHOT'=5),
    metric_value    Float64,
    unit            String      DEFAULT '',
    source          String,
    connector_id    String      DEFAULT '',
    labels          String      DEFAULT '{}',
    payload         String      DEFAULT '',
    timestamp       DateTime64(3),
    received_at     DateTime64(3) DEFAULT now64(3)
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(timestamp)
ORDER BY (resource_id, metric_name, timestamp)
TTL toDateTime(timestamp) + INTERVAL 365 DAY;
