-- ============================================================
-- AI Performance Engineer — 全链路电商演示数据 (含故障场景)
-- 执行: mysql -h <HOST> -uroot -p aipe_metadata < aipe_full_demo.sql
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE resource;
TRUNCATE TABLE relationship;
TRUNCATE TABLE evidence;
TRUNCATE TABLE knowledge;
TRUNCATE TABLE recommendation;
TRUNCATE TABLE execution;
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 1. Resource (完整电商系统)
-- ============================================================

-- 业务服务 (Java 微服务, 每个多实例)
INSERT INTO resource (resource_id, resource_name, resource_type, resource_category, resource_status, business_system, cluster, namespace, environment, version, labels, attributes, created_at, updated_at, deleted) VALUES
-- 订单服务 (3 实例, 1 个 CPU 告警)
('order-svc-1-1','订单服务','SERVICE','BUSINESS','RUNNING','电商双11','order-cluster','default','prod',1,'{"app":"order-svc","ver":"2.1.0"}','{"host":"10.0.1.1","port":8080}',NOW(),NOW(),0),
('order-svc-1-2','订单服务','SERVICE','BUSINESS','RUNNING','电商双11','order-cluster','default','prod',1,'{"app":"order-svc","ver":"2.1.0"}','{"host":"10.0.1.2","port":8080}',NOW(),NOW(),0),
('order-svc-1-3','订单服务','SERVICE','BUSINESS','RUNNING','电商双11','order-cluster','default','prod',1,'{"app":"order-svc","ver":"2.1.0"}','{"host":"10.0.1.3","port":8080}',NOW(),NOW(),0),

-- 支付服务 (4 实例)
('pay-svc-2-1','支付服务','SERVICE','BUSINESS','RUNNING','电商双11','pay-cluster','default','prod',1,'{"app":"pay-svc","ver":"1.5.0"}','{"host":"10.0.2.1","port":8080}',NOW(),NOW(),0),
('pay-svc-2-2','支付服务','SERVICE','BUSINESS','RUNNING','电商双11','pay-cluster','default','prod',1,'{"app":"pay-svc","ver":"1.5.0"}','{"host":"10.0.2.2","port":8080}',NOW(),NOW(),0),
('pay-svc-2-3','支付服务','SERVICE','BUSINESS','RUNNING','电商双11','pay-cluster','default','prod',1,'{"app":"pay-svc","ver":"1.5.0"}','{"host":"10.0.2.3","port":8080}',NOW(),NOW(),0),
('pay-svc-2-4','支付服务','SERVICE','BUSINESS','MAINTENANCE','电商双11','pay-cluster','default','prod',1,'{"app":"pay-svc","ver":"1.5.0"}','{"host":"10.0.2.4","port":8080}',NOW(),NOW(),0),

-- 库存/商品/购物车/优惠券/搜索/推荐/物流/风控/秒杀/消息
('inventory-svc-1','库存服务','SERVICE','BUSINESS','RUNNING','电商双11','inventory-cluster','default','prod',1,'{"app":"inventory-svc"}','{"host":"10.0.3.1"}',NOW(),NOW(),0),
('product-svc-1','商品服务','SERVICE','BUSINESS','RUNNING','电商双11','product-cluster','default','prod',1,'{"app":"product-svc"}','{"host":"10.0.4.1"}',NOW(),NOW(),0),
('cart-svc-1','购物车服务','SERVICE','BUSINESS','RUNNING','电商双11','cart-cluster','default','prod',1,'{"app":"cart-svc"}','{"host":"10.0.5.1"}',NOW(),NOW(),0),
('coupon-svc-1','优惠券服务','SERVICE','BUSINESS','RUNNING','电商双11','coupon-cluster','default','prod',1,'{"app":"coupon-svc"}','{"host":"10.0.6.1"}',NOW(),NOW(),0),
('search-svc-1','搜索服务','SERVICE','BUSINESS','RUNNING','电商双11','search-cluster','default','prod',1,'{"app":"search-svc"}','{"host":"10.0.7.1"}',NOW(),NOW(),0),
('recommend-svc-1','推荐服务','SERVICE','BUSINESS','RUNNING','电商双11','recommend-cluster','default','prod',1,'{"app":"recommend-svc"}','{"host":"10.0.8.1"}',NOW(),NOW(),0),
('logistics-svc-1','物流服务','SERVICE','BUSINESS','RUNNING','电商双11','logistics-cluster','default','prod',1,'{"app":"logistics-svc"}','{"host":"10.0.9.1"}',NOW(),NOW(),0),
('risk-svc-1','风控服务','SERVICE','BUSINESS','RUNNING','电商双11','risk-cluster','default','prod',1,'{"app":"risk-svc"}','{"host":"10.0.10.1"}',NOW(),NOW(),0),
('seckill-svc-1','秒杀服务','SERVICE','BUSINESS','RUNNING','电商双11','seckill-cluster','default','prod',1,'{"app":"seckill-svc"}','{"host":"10.0.11.1"}',NOW(),NOW(),0),
('notify-svc-1','消息通知服务','SERVICE','BUSINESS','RUNNING','电商双11','notify-cluster','default','prod',1,'{"app":"notify-svc"}','{"host":"10.0.12.1"}',NOW(),NOW(),0),

-- 中间件 (不装 Agent, 通过网络采集)
('redis-order','订单Redis集群','REDIS','PLATFORM','RUNNING','电商双11','redis-cluster','default','prod',1,'{"app":"redis","ver":"7.0"}','{"host":"10.0.100.1","port":6379,"max_connections":100}',NOW(),NOW(),0),
('redis-session','会话Redis','REDIS','PLATFORM','RUNNING','电商双11','redis-cluster','default','prod',1,'{"app":"redis","ver":"7.0"}','{"host":"10.0.100.2","port":6379}',NOW(),NOW(),0),
('rocketmq-1','RocketMQ Broker','MQ','PLATFORM','RUNNING','电商双11','mq-cluster','default','prod',1,'{"app":"rocketmq","ver":"5.0"}','{"host":"10.0.101.1","port":9876}',NOW(),NOW(),0),
('sharding-mysql-1','订单MySQL分片1','DATABASE','PLATFORM','RUNNING','电商双11','mysql-cluster','default','prod',1,'{"app":"mysql","ver":"8.0"}','{"host":"10.0.102.1","port":3306,"shard":"user_id_0"}',NOW(),NOW(),0),
('sharding-mysql-2','订单MySQL分片2','DATABASE','PLATFORM','RUNNING','电商双11','mysql-cluster','default','prod',1,'{"app":"mysql","ver":"8.0"}','{"host":"10.0.102.2","port":3306,"shard":"user_id_1"}',NOW(),NOW(),0),
('elasticsearch-1','商品搜索ES','DATABASE','PLATFORM','RUNNING','电商双11','es-cluster','default','prod',1,'{"app":"elasticsearch","ver":"8.0"}','{"host":"10.0.103.1","port":9200}',NOW(),NOW(),0),

-- 基础设施
('nginx-lb','Nginx负载均衡','NGINX','INFRA','RUNNING','电商双11','lb-cluster','default','prod',1,'{"app":"nginx","ver":"1.24"}','{"host":"10.0.0.1","port":443}',NOW(),NOW(),0),
('zookeeper-1','ZooKeeper','MIDDLEWARE','PLATFORM','RUNNING','电商双11','zk-cluster','default','prod',1,'{"app":"zookeeper","ver":"3.8"}','{"host":"10.0.200.1",port:2181}',NOW(),NOW(),0),
('nacos-1','Nacos配置中心','MIDDLEWARE','PLATFORM','RUNNING','电商双11','nacos-cluster','default','prod',1,'{"app":"nacos","ver":"2.2"}','{"host":"10.0.201.1","port":8848}',NOW(),NOW(),0);

-- ============================================================
-- 2. Relationship (服务依赖拓扑)
-- ============================================================

INSERT INTO relationship (id, relationship_type, source_resource_id, target_resource_id, direction, confidence, discovered_by, status, labels, discovered_at, updated_at) VALUES
-- 订单服务依赖
('rel-01','CALLS','order-svc-1-1','pay-svc-2-1','SINGLE',100,'AI','ACTIVE','{}',NOW(),NOW()),
('rel-02','CALLS','order-svc-1-1','inventory-svc-1','SINGLE',100,'AI','ACTIVE','{}',NOW(),NOW()),
('rel-03','DEPENDS_ON','order-svc-1-1','redis-order','SINGLE',100,'AI','ACTIVE','{}',NOW(),NOW()),
('rel-04','DEPENDS_ON','order-svc-1-1','sharding-mysql-1','SINGLE',100,'AI','ACTIVE','{}',NOW(),NOW()),
('rel-05','DEPENDS_ON','order-svc-1-1','rocketmq-1','SINGLE',100,'AI','ACTIVE','{}',NOW(),NOW()),

-- 支付服务依赖
('rel-06','DEPENDS_ON','pay-svc-2-1','sharding-mysql-2','SINGLE',100,'AI','ACTIVE','{}',NOW(),NOW()),
('rel-07','DEPENDS_ON','pay-svc-2-1','redis-order','SINGLE',100,'AI','ACTIVE','{}',NOW(),NOW()),

-- 购物车依赖
('rel-08','CALLS','cart-svc-1','product-svc-1','SINGLE',100,'AI','ACTIVE','{}',NOW(),NOW()),
('rel-09','CALLS','cart-svc-1','coupon-svc-1','SINGLE',100,'AI','ACTIVE','{}',NOW(),NOW()),
('rel-10','DEPENDS_ON','cart-svc-1','redis-session','SINGLE',100,'AI','ACTIVE','{}',NOW(),NOW()),

-- 商品服务依赖
('rel-11','DEPENDS_ON','product-svc-1','elasticsearch-1','SINGLE',100,'AI','ACTIVE','{}',NOW(),NOW()),
('rel-12','DEPENDS_ON','product-svc-1','sharding-mysql-1','SINGLE',100,'AI','ACTIVE','{}',NOW(),NOW()),

-- Nginx → 订单服务
('rel-13','CALLS','nginx-lb','order-svc-1-1','SINGLE',100,'Discovery','ACTIVE','{}',NOW(),NOW()),

-- 秒杀服务 (双11核心)
('rel-14','CALLS','seckill-svc-1','order-svc-1-1','SINGLE',100,'AI','ACTIVE','{}',NOW(),NOW()),
('rel-15','DEPENDS_ON','seckill-svc-1','redis-order','SINGLE',100,'AI','ACTIVE','{}',NOW(),NOW());

-- ============================================================
-- 3. Evidence (AI 推理证据 — 含故障场景)
-- ============================================================

INSERT INTO evidence (id, evidence_type, title, description, root_resource_id, observation_ids, relationship_ids, timeline_id, confidence, reasoning_steps, status, created_at, updated_at, version) VALUES
('evid-001','PERFORMANCE','订单服务 10.0.1.2 CPU 持续超阈值 (双11压测)',
 '订单服务实例 10.0.1.2 在过去 30 分钟内 CPU 使用率持续超过 90%, 最大值为 98.5%。\n\n关键发现:\n1. Young GC 频率从 5次/分钟 飙升到 45次/分钟\n2. 线程池活跃线程达到上限 200/200\n3. 下游 Redis 连接池 100/100 打满\n\n根因分析:\n- 非代码问题 (CPU 不是代码热点)\n- 下游 Redis 响应慢 (RT 从 2ms → 500ms) 导致线程堆积\n- Redis 慢是因为连接池配置过小 (maxTotal=100)',
 'order-svc-1-2',
 '["obs-cpu-001","obs-gc-001","obs-thread-001","obs-redis-001"]',
 '["rel-03","rel-04"]',
 'TL-order-svc-1-2-cpu-20260726',
 95.0,
 '[{"step":1,"action":"查询订单服务 10.0.1.2 CPU Timeline","result":"30 个采集点全部 >90%, 持续 30 分钟","confidence":95},{"step":2,"action":"关联 GC 指标","result":"Young GC 45次/分 (正常 5次/分), 确认内存压力大","confidence":90},{"step":3,"action":"关联线程池指标","result":"活跃线程 200/200 打满, 线程在等待","confidence":92},{"step":4,"action":"检查下游 Redis","result":"Redis 连接池 100/100, RT 从 2ms→500ms","confidence":88},{"step":5,"action":"定位根因","result":"Redis 连接池配置过小 (maxTotal=100), 导致线程等待, CPU 空转","confidence":95}]',
 'NEW', NOW(), NOW(), 1),

('evid-002','DEPENDENCY','Redis 连接池耗尽导致订单服务 TPS 下降 (14:00 故障)',
 '14:00-14:15 期间订单服务 TPS 从 5000 降至 800。\n\n时间线:\n- 13:58:00 Redis 连接池达到 100/100\n- 13:58:30 订单服务 RT 从 50ms 升至 500ms\n- 13:59:00 支付服务开始超时 (等待订单回调)\n- 14:00:00 订单服务 TPS 降至 800\n- 14:02:00 用户投诉下单失败\n\n根因: Redis 集群主节点切换, 部分连接未释放, 连接池耗尽。',
 'order-svc-1-1',
 '["obs-tps-001","obs-rt-001","obs-redis-pool-001"]',
 '["rel-03"]',
 'TL-order-svc-1-1-tps-20260726',
 92.0,
 '[{"step":1,"action":"查询订单服务 TPS Timeline","result":"14:00 TPS 从 5000 骤降至 800 (-84%)","confidence":95},{"step":2,"action":"关联 Redis 连接池","result":"13:58 连接池打满 (100/100), 等待队列堆积","confidence":90},{"step":3,"action":"检查 Redis 集群事件","result":"13:57 主从切换, 部分连接未释放","confidence":85},{"step":4,"action":"确认因果链","result":"Redis 连接池满 → 订单服务等待 → RT 上升 → TPS 下降 → 支付超时","confidence":92}]',
 'VERIFIED', NOW(), NOW(), 1);

-- ============================================================
-- 4. Knowledge (知识库)
-- ============================================================

INSERT INTO knowledge (pk_id, id, title, description, knowledge_type, evidence_id, verification_id, confidence, applicable_conditions, recommendation, success_rate, created_at, updated_at, version) VALUES
(1,'know-001','Redis 连接池耗尽处理方案',
 '当 Redis 连接池打满时:\n1. 检查连接泄漏 (未关闭的 Jedis 实例)\n2. 适当增大 maxTotal (建议 200+)\n3. 配置 maxWaitMillis (建议 2000ms)\n4. 启用连接池监控 (active/idle/await)\n5. 考虑 Redis 集群扩容',
 'DEPENDENCY','evid-002','verify-001',95.0,
 '{"resourceType":"REDIS","metricName":"connection.active","threshold":90}',
 '{"action":"增大 Redis 连接池上限并配置超时","expectedEffect":"TPS 恢复, 连接池使用率 <70%"}',
 85.0, NOW(), NOW(), 1),

(2,'know-002','JVM CPU 持续高位处理方案',
 '当 CPU 持续 >90% 超过 10 分钟:\n1. 执行 thread dump 分析热点方法\n2. 检查 GC 日志 (是否 Full GC)\n3. 检查下游依赖 (RT 是否飙升)\n4. 考虑扩容或限流降级\n5. 必要时生成 Heap Dump 分析内存',
 'BOTTLENECK','evid-001',NULL,80.0,
 '{"resourceType":"SERVICE","metricName":"cpu.usage","threshold":90}',
 '{"action":"扩容实例或执行限流降级","expectedEffect":"CPU 降至 70% 以下"}',
 70.0, NOW(), NOW(), 1),

(3,'know-003','双11秒杀场景扩容策略',
 '双11 秒杀场景下:\n1. 提前 2 小时扩容订单服务到 10 实例\n2. 扩容 Redis 集群 (增加分片)\n3. 预热 JVM (提前触发 JIT 编译)\n4. 限流阈值调高到平时的 5 倍\n5. 降级非核心服务 (推荐/评价)',
 'DEPLOYMENT',NULL,NULL,90.0,
 '{"scenario":"seckill","event":"double11"}',
 '{"action":"扩容订单服务到 10 实例 + Redis 增加分片","expectedEffect":"支撑 10万 QPS"}',
 90.0, NOW(), NOW(), 1);

-- ============================================================
-- 5. Recommendation (推荐)
-- ============================================================

INSERT INTO recommendation (id, knowledge_id, target_resource_id, title, description, priority, confidence, expected_outcome, execution_plan, rollback_plan, status, created_at, updated_at, version) VALUES
('rec-001','know-002','order-svc-1-2','扩容订单服务实例',
 '订单服务 10.0.1.2 CPU 持续 >90%, 建议扩容到 5 实例',
 'HIGH',95.0,'CPU 降至 70% 以下, 系统容量提升 66%',
 '["1. 评估当前资源使用率","2. 在 10.0.1.4/1.5 部署新实例","3. 注册到 Nginx","4. 验证健康状态","5. 观察 15 分钟"]',
 '["1. 从 Nginx 摘除新实例","2. 停止新实例服务","3. 恢复原有配置"]',
 'PENDING', NOW(), NOW(), 1),

('rec-002','know-001','redis-order','优化 Redis 连接池配置',
 'Redis 连接池频繁打满, 建议增大 maxTotal 并配置超时',
 'MEDIUM',88.0,'连接池使用率降至 70% 以下, TPS 恢复',
 '["1. 备份 Redis 客户端配置","2. 修改 maxTotal: 100 → 200","3. 配置 maxWaitMillis: 2000","4. 重启订单服务 (滚动)","5. 观察连接池指标"]',
 '["1. 恢复 maxTotal: 100","2. 移除超时配置"]',
 'APPROVED', NOW(), NOW(), 1);

-- ============================================================
-- 6. Execution (执行记录)
-- ============================================================

INSERT INTO execution (pk_id, id, recommendation_id, executor, execution_type, status, before_snapshot, after_snapshot, improvement_score, started_at, finished_at, rollback_info, created_at, updated_at, version) VALUES
(1,'exec-001','rec-002','张三','MANUAL','SUCCESS',
 '{"tps":800,"rt":2000,"cpu":95,"redis_connections":100}',
 '{"tps":4800,"rt":80,"cpu":55,"redis_connections":45}',
 4.5, '2026-07-26 14:30:00', '2026-07-26 14:45:00', NULL, NOW(), NOW(), 1);

