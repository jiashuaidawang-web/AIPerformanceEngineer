-- ============================================================
-- AI Performance Engineer — 电商演示数据
-- 执行: mysql -uroot -p aipe_metadata < aipe_demo_data.sql
-- ============================================================

-- 清空测试数据
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE resource;
TRUNCATE TABLE relationship;
TRUNCATE TABLE evidence;
TRUNCATE TABLE knowledge;
TRUNCATE TABLE recommendation;
TRUNCATE TABLE execution;
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 1. Resource (电商微服务, 每个服务多实例)
-- ============================================================

-- 订单服务 (3 实例)
INSERT INTO resource (resource_id, resource_name, resource_type, resource_category, resource_status, business_system, cluster, namespace, environment, version, labels, attributes, created_at, updated_at, deleted) VALUES
('order-svc-192-168-1-0', '订单服务', 'SERVICE', 'BUSINESS', 'RUNNING', '电商系统', 'order-cluster', 'default', 'prod', 1, '{"app":"order-svc","ver":"2.1.0"}', '{"host":"192.168.1.0","port":"8080","protocol":"HTTP"}', NOW(), NOW(), 0),
('order-svc-192-168-1-1', '订单服务', 'SERVICE', 'BUSINESS', 'RUNNING', '电商系统', 'order-cluster', 'default', 'prod', 1, '{"app":"order-svc","ver":"2.1.0"}', '{"host":"192.168.1.1","port":"8080","protocol":"HTTP"}', NOW(), NOW(), 0),
('order-svc-192-168-1-2', '订单服务', 'SERVICE', 'BUSINESS', 'RUNNING', '电商系统', 'order-cluster', 'default', 'prod', 1, '{"app":"order-svc","ver":"2.1.0"}', '{"host":"192.168.1.2","port":"8080","protocol":"HTTP"}', NOW(), NOW(), 0);

-- 支付服务 (4 实例)
INSERT INTO resource (resource_id, resource_name, resource_type, resource_category, resource_status, business_system, cluster, namespace, environment, version, labels, attributes, created_at, updated_at, deleted) VALUES
('pay-svc-192-168-2-0', '支付服务', 'SERVICE', 'BUSINESS', 'RUNNING', '电商系统', 'pay-cluster', 'default', 'prod', 1, '{"app":"pay-svc","ver":"1.5.0"}', '{"host":"192.168.2.0","port":"8080"}', NOW(), NOW(), 0),
('pay-svc-192-168-2-1', '支付服务', 'SERVICE', 'BUSINESS', 'RUNNING', '电商系统', 'pay-cluster', 'default', 'prod', 1, '{"app":"pay-svc","ver":"1.5.0"}', '{"host":"192.168.2.1","port":"8080"}', NOW(), NOW(), 0),
('pay-svc-192-168-2-2', '支付服务', 'SERVICE', 'BUSINESS', 'RUNNING', '电商系统', 'pay-cluster', 'default', 'prod', 1, '{"app":"pay-svc","ver":"1.5.0"}', '{"host":"192.168.2.2","port":"8080"}', NOW(), NOW(), 0),
('pay-svc-192-168-2-3', '支付服务', 'SERVICE', 'BUSINESS', 'MAINTENANCE', '电商系统', 'pay-cluster', 'default', 'prod', 1, '{"app":"pay-svc","ver":"1.5.0"}', '{"host":"192.168.2.3","port":"8080"}', NOW(), NOW(), 0);

-- 库存服务 (2 实例)
INSERT INTO resource (resource_id, resource_name, resource_type, resource_category, resource_status, business_system, cluster, namespace, environment, version, labels, attributes, created_at, updated_at, deleted) VALUES
('inventory-svc-192-168-3-0', '库存服务', 'SERVICE', 'BUSINESS', 'RUNNING', '电商系统', 'inventory-cluster', 'default', 'prod', 1, '{"app":"inventory-svc","ver":"1.0.0"}', '{"host":"192.168.3.0","port":"8080"}', NOW(), NOW(), 0),
('inventory-svc-192-168-3-1', '库存服务', 'SERVICE', 'BUSINESS', 'RUNNING', '电商系统', 'inventory-cluster', 'default', 'prod', 1, '{"app":"inventory-svc","ver":"1.0.0"}', '{"host":"192.168.3.1","port":"8080"}', NOW(), NOW(), 0);

-- 商品服务 (4 实例)
INSERT INTO resource (resource_id, resource_name, resource_type, resource_category, resource_status, business_system, cluster, namespace, environment, version, labels, attributes, created_at, updated_at, deleted) VALUES
('product-svc-192-168-4-0', '商品服务', 'SERVICE', 'BUSINESS', 'RUNNING', '电商系统', 'product-cluster', 'default', 'prod', 1, '{"app":"product-svc","ver":"3.0.0"}', '{"host":"192.168.4.0","port":"8080"}', NOW(), NOW(), 0),
('product-svc-192-168-4-1', '商品服务', 'SERVICE', 'BUSINESS', 'RUNNING', '电商系统', 'product-cluster', 'default', 'prod', 1, '{"app":"product-svc","ver":"3.0.0"}', '{"host":"192.168.4.1","port":"8080"}', NOW(), NOW(), 0),
('product-svc-192-168-4-2', '商品服务', 'SERVICE', 'BUSINESS', 'RUNNING', '电商系统', 'product-cluster', 'default', 'prod', 1, '{"app":"product-svc","ver":"3.0.0"}', '{"host":"192.168.4.2","port":"8080"}', NOW(), NOW(), 0),
('product-svc-192-168-4-3', '商品服务', 'SERVICE', 'BUSINESS', 'RUNNING', '电商系统', 'product-cluster', 'default', 'prod', 1, '{"app":"product-svc","ver":"3.0.0"}', '{"host":"192.168.4.3","port":"8080"}', NOW(), NOW(), 0);

-- 购物车 (4 实例)
INSERT INTO resource (resource_id, resource_name, resource_type, resource_category, resource_status, business_system, cluster, namespace, environment, version, labels, attributes, created_at, updated_at, deleted) VALUES
('cart-svc-192-168-5-0', '购物车服务', 'SERVICE', 'BUSINESS', 'RUNNING', '电商系统', 'cart-cluster', 'default', 'prod', 1, '{"app":"cart-svc","ver":"1.2.0"}', '{"host":"192.168.5.0","port":"8080"}', NOW(), NOW(), 0),
('cart-svc-192-168-5-1', '购物车服务', 'SERVICE', 'BUSINESS', 'RUNNING', '电商系统', 'cart-cluster', 'default', 'prod', 1, '{"app":"cart-svc","ver":"1.2.0"}', '{"host":"192.168.5.1","port":"8080"}', NOW(), NOW(), 0),
('cart-svc-192-168-5-2', '购物车服务', 'SERVICE', 'BUSINESS', 'RUNNING', '电商系统', 'cart-cluster', 'default', 'prod', 1, '{"app":"cart-svc","ver":"1.2.0"}', '{"host":"192.168.5.2","port":"8080"}', NOW(), NOW(), 0),
('cart-svc-192-168-5-3', '购物车服务', 'SERVICE', 'BUSINESS', 'RUNNING', '电商系统', 'cart-cluster', 'default', 'prod', 1, '{"app":"cart-svc","ver":"1.2.0"}', '{"host":"192.168.5.3","port":"8080"}', NOW(), NOW(), 0);

-- 优惠券 (4 实例)
INSERT INTO resource (resource_id, resource_name, resource_type, resource_category, resource_status, business_system, cluster, namespace, environment, version, labels, attributes, created_at, updated_at, deleted) VALUES
('coupon-svc-192-168-6-0', '优惠券服务', 'SERVICE', 'BUSINESS', 'RUNNING', '电商系统', 'coupon-cluster', 'default', 'prod', 1, '{"app":"coupon-svc","ver":"2.0.0"}', '{"host":"192.168.6.0","port":"8080"}', NOW(), NOW(), 0),
('coupon-svc-192-168-6-1', '优惠券服务', 'SERVICE', 'BUSINESS', 'RUNNING', '电商系统', 'coupon-cluster', 'default', 'prod', 1, '{"app":"coupon-svc","ver":"2.0.0"}', '{"host":"192.168.6.1","port":"8080"}', NOW(), NOW(), 0),
('coupon-svc-192-168-6-2', '优惠券服务', 'SERVICE', 'BUSINESS', 'RUNNING', '电商系统', 'coupon-cluster', 'default', 'prod', 1, '{"app":"coupon-svc","ver":"2.0.0"}', '{"host":"192.168.6.2","port":"8080"}', NOW(), NOW(), 0),
('coupon-svc-192-168-6-3', '优惠券服务', 'SERVICE', 'BUSINESS', 'RUNNING', '电商系统', 'coupon-cluster', 'default', 'prod', 1, '{"app":"coupon-svc","ver":"2.0.0"}', '{"host":"192.168.6.3","port":"8080"}', NOW(), NOW(), 0);

-- 基础设施
INSERT INTO resource (resource_id, resource_name, resource_type, resource_category, resource_status, business_system, cluster, namespace, environment, version, labels, attributes, created_at, updated_at, deleted) VALUES
('redis-order', '订单Redis集群', 'REDIS', 'PLATFORM', 'RUNNING', '电商系统', 'redis-cluster', 'default', 'prod', 1, '{"app":"redis","ver":"7.0"}', '{"host":"192.168.10.0","port":"6379"}', NOW(), NOW(), 0),
('mysql-order', '订单MySQL主库', 'DATABASE', 'PLATFORM', 'RUNNING', '电商系统', 'mysql-cluster', 'default', 'prod', 1, '{"app":"mysql","ver":"8.0"}', '{"host":"192.168.10.1","port":"3306"}', NOW(), NOW(), 0),
('nginx-lb', 'Nginx负载均衡', 'NGINX', 'INFRA', 'RUNNING', '电商系统', 'lb-cluster', 'default', 'prod', 1, '{"app":"nginx","ver":"1.24"}', '{"host":"192.168.0.1","port":"443"}', NOW(), NOW(), 0);

-- ============================================================
-- 2. Relationship (服务间依赖关系)
-- ============================================================

INSERT INTO relationship (id, relationship_type, source_resource_id, target_resource_id, direction, confidence, discovered_by, status, labels, discovered_at, updated_at) VALUES
-- 订单服务依赖
('rel-001', 'CALLS', 'order-svc-192-168-1-0', 'pay-svc-192-168-2-0', 'SINGLE', 100.0, 'AI', 'ACTIVE', '{}', NOW(), NOW()),
('rel-002', 'CALLS', 'order-svc-192-168-1-0', 'inventory-svc-192-168-3-0', 'SINGLE', 100.0, 'AI', 'ACTIVE', '{}', NOW(), NOW()),
('rel-003', 'DEPENDS_ON', 'order-svc-192-168-1-0', 'redis-order', 'SINGLE', 100.0, 'AI', 'ACTIVE', '{}', NOW(), NOW()),
('rel-004', 'DEPENDS_ON', 'order-svc-192-168-1-0', 'mysql-order', 'SINGLE', 100.0, 'AI', 'ACTIVE', '{}', NOW(), NOW()),

-- 支付服务依赖
('rel-005', 'DEPENDS_ON', 'pay-svc-192-168-2-0', 'mysql-order', 'SINGLE', 100.0, 'AI', 'ACTIVE', '{}', NOW(), NOW()),
('rel-006', 'DEPENDS_ON', 'pay-svc-192-168-2-0', 'redis-order', 'SINGLE', 100.0, 'AI', 'ACTIVE', '{}', NOW(), NOW()),

-- 购物车依赖
('rel-007', 'CALLS', 'cart-svc-192-168-5-0', 'product-svc-192-168-4-0', 'SINGLE', 100.0, 'AI', 'ACTIVE', '{}', NOW(), NOW()),
('rel-008', 'CALLS', 'cart-svc-192-168-5-0', 'coupon-svc-192-168-6-0', 'SINGLE', 100.0, 'AI', 'ACTIVE', '{}', NOW(), NOW()),
('rel-009', 'DEPENDS_ON', 'cart-svc-192-168-5-0', 'redis-order', 'SINGLE', 100.0, 'AI', 'ACTIVE', '{}', NOW(), NOW()),

-- Nginx → 订单服务
('rel-010', 'CALLS', 'nginx-lb', 'order-svc-192-168-1-0', 'SINGLE', 100.0, 'Discovery', 'ACTIVE', '{}', NOW(), NOW());

-- ============================================================
-- 3. Evidence (AI 推理证据)
-- ============================================================

INSERT INTO evidence (id, evidence_type, title, description, root_resource_id, observation_ids, relationship_ids, timeline_id, confidence, reasoning_steps, status, created_at, updated_at, version) VALUES
('evid-001', 'PERFORMANCE', '订单服务 192.168.1.1 CPU 持续超阈值',
 '订单服务实例 192.168.1.1 在过去 30 分钟内 CPU 使用率持续超过 90%, 最大值为 98.5%, 平均值为 94.2%。同时伴随 Young GC 频率从 5次/分钟 上升到 45次/分钟。',
 'order-svc-192-168-1-1',
 '["obs-001","obs-002","obs-003"]',
 '["rel-001","rel-002"]',
 'TL-order-svc-192-168-1-1-cpu-20260726',
 95.0,
 '[{"step":1,"action":"查询订单服务 192.168.1.1 的 CPU Timeline","result":"获取到 30 个采集点, 范围 85%~98.5%","confidence":90},{"step":2,"action":"检测是否持续超阈值 (80%)","result":"30 个点全部超过阈值, 持续时间 30 分钟","confidence":95},{"step":3,"action":"关联 GC 指标","result":"Young GC 频率同步上升 9 倍, 确认非外部流量导致","confidence":92},{"step":4,"action":"生成结论","result":"实例可能存在内存泄漏或代码热点, 建议 dump 分析","confidence":85}]',
 'NEW', NOW(), NOW(), 1),

('evid-002', 'DEPENDENCY', 'Redis 连接池耗尽导致订单服务 TPS 下降',
 '订单服务在 14:00-14:15 期间 TPS 从 5000 下降到 800, 同时 Redis 连接池活跃连接达到上限 100。根因是 Redis 集群主节点切换导致连接泄漏。',
 'order-svc-192-168-1-0',
 '["obs-010","obs-011"]',
 '["rel-003"]',
 'TL-order-svc-192-168-1-0-tps-20260726',
 88.0,
 '[{"step":1,"action":"查询订单服务 TPS Timeline","result":"14:00 开始 TPS 骤降 84%","confidence":95},{"step":2,"action":"关联 Redis 连接池指标","result":"连接池打满, 等待队列堆积","confidence":90},{"step":3,"action":"检查 Redis 集群事件","result":"14:02 主从切换, 部分连接未释放","confidence":85}]',
 'VERIFIED', NOW(), NOW(), 1);

-- ============================================================
-- 4. Knowledge (知识库)
-- ============================================================

INSERT INTO knowledge (pk_id, id, title, description, knowledge_type, evidence_id, verification_id, confidence, applicable_conditions, recommendation, success_rate, created_at, updated_at, version) VALUES
(1, 'know-001', 'Redis 连接池耗尽处理方案', '当 Redis 连接池打满时, 应: 1) 检查连接泄漏 2) 适当增大 maxTotal 3) 配置连接超时 4) 启用连接池监控',
 'DEPENDENCY', 'evid-002', 'verify-001', 95.0,
 '{"resourceType":"REDIS","metricName":"connection.active"}',
 '{"action":"增大 Redis 连接池上限并配置超时","expectedEffect":"TPS 恢复, 连接池使用率 <70%"}',
 85.0, NOW(), NOW(), 1),

(2, 'know-002', 'JVM CPU 持续高位处理方案', '当 CPU 持续 >90% 超过 10 分钟, 应: 1) 执行 thread dump 分析热点 2) 检查 GC 日志 3) 考虑扩容或降级',
 'BOTTLENECK', 'evid-001', NULL, 80.0,
 '{"resourceType":"SERVICE","metricName":"cpu.usage","threshold":90}',
 '{"action":"扩容实例或执行限流降级","expectedEffect":"CPU 降至 70% 以下"}',
 70.0, NOW(), NOW(), 1);

-- ============================================================
-- 5. Recommendation (推荐)
-- ============================================================

INSERT INTO recommendation (id, knowledge_id, target_resource_id, title, description, priority, confidence, expected_outcome, execution_plan, rollback_plan, status, created_at, updated_at, version) VALUES
('rec-001', 'know-002', 'order-svc-192-168-1-1', '扩容订单服务实例', '订单服务 192.168.1.1 CPU 持续 >90%, 建议扩容到 5 实例',
 'HIGH', 95.0, 'CPU 使用率降至 70% 以下, 系统容量提升 66%',
 '["1. 评估当前资源使用率","2. 备份当前配置","3. 在 192.168.1.4 部署新实例","4. 注册到 Nginx 负载均衡","5. 验证新实例健康状态"]',
 '["1. 从 Nginx 摘除新实例","2. 停止 192.168.1.4 上的服务","3. 恢复原有配置"]',
 'PENDING', NOW(), NOW(), 1),

('rec-002', 'know-001', 'redis-order', '优化 Redis 连接池配置', 'Redis 连接池频繁打满, 建议增大 maxTotal 并配置超时',
 'MEDIUM', 88.0, '连接池使用率降至 70% 以下, TPS 恢复',
 '["1. 备份 Redis 配置","2. 修改 maxTotal: 100 → 200","3. 配置 maxWaitMillis: 2000","4. 重启 Redis 客户端"]',
 '["1. 恢复 maxTotal: 100","2. 移除超时配置"]',
 'APPROVED', NOW(), NOW(), 1);

-- ============================================================
-- 6. Execution (执行记录)
-- ============================================================

INSERT INTO execution (pk_id, id, recommendation_id, executor, execution_type, status, before_snapshot, after_snapshot, improvement_score, started_at, finished_at, rollback_info, created_at, updated_at, version) VALUES
(1, 'exec-001', 'rec-002', '张三', 'MANUAL', 'SUCCESS',
 '{"tps":800,"cpu":95,"redis_connections":100}',
 '{"tps":4500,"cpu":60,"redis_connections":45}',
 4.5, '2026-07-26 14:30:00', '2026-07-26 14:45:00', NULL, NOW(), NOW(), 1);

