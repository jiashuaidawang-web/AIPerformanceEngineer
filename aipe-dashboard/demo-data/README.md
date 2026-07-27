# 电商演示数据

> 一键初始化电商场景演示数据, 让你直观看到每个页面的含义。

## 场景说明

一个典型电商系统, 6 个微服务, 21 个实例:

| 服务 | 实例数 | IP 段 | 说明 |
|------|--------|-------|------|
| 订单服务 | 3 | 192.168.1.0~2 | 核心服务, 有 1 实例 CPU 告警 |
| 支付服务 | 4 | 192.168.2.0~3 | 1 实例维护中 |
| 库存服务 | 2 | 192.168.3.0~1 | |
| 商品服务 | 4 | 192.168.4.0~3 | |
| 购物车 | 4 | 192.168.5.0~3 | |
| 优惠券 | 4 | 192.168.6.0~3 | |
| Redis | 1 | 192.168.10.0 | 订单服务依赖 |
| MySQL | 1 | 192.168.10.1 | 订单服务依赖 |
| Nginx | 1 | 192.168.0.1 | 负载均衡 |

## 数据内容

| 表 | 记录数 | 说明 |
|----|--------|------|
| resource | 22 | 所有服务实例 + 基础设施 |
| relationship | 10 | 服务间依赖关系 |
| evidence | 2 | AI 推理证据 (CPU 超阈值 + Redis 连接池) |
| knowledge | 2 | 知识库 (沉淀的优化经验) |
| recommendation | 2 | 优化建议 (扩容 + Redis 调优) |
| execution | 1 | 执行记录 (Redis 优化已完成) |

## 执行方式

### 方式 1: mysql 客户端

```bash
mysql -h <MySQL_HOST> -uroot -p aipe_metadata < demo-data/ecommerce_demo.sql
```

### 方式 2: Docker

```bash
docker run --rm -v $(pwd)/demo-data/ecommerce_demo.sql:/data.sql mysql:8.0 \
  mysql -h <MySQL_HOST> -uroot -p aipe_metadata < /data.sql
```

## 各页面展示效果

执行后刷新 Dashboard, 你将看到:

### 1. 概览 (Dashboard)
- 资源总数: 22
- 今日 Observation: (实时)
- 活跃 Evidence: 2
- 待执行推荐: 1

### 2. 资源列表
- 订单服务 (3 实例) - 1 个告警
- 支付服务 (4 实例) - 1 个维护中
- 库存/商品/购物车/优惠券
- Redis / MySQL / Nginx

### 3. 资源详情 (点击订单服务)
- 基本信息: ID/类型/状态/版本
- CPU 趋势图 (ECharts)
- 统计: min=85%, max=98.5%, avg=94.2%

### 4. 时序 (Timeline)
- 选择资源 + 指标 + 时间范围
- 返回时序曲线 + 统计特征
- 阈值线标记 (80%)

### 5. 拓扑 (Topology)
- 力导向图展示服务依赖
- 订单 → 支付 → MySQL
- 订单 → 库存 → Redis
- Nginx → 订单

### 6. 证据 (Evidence)
- "订单服务 192.168.1.1 CPU 持续超阈值" (置信度 95%)
- "Redis 连接池耗尽导致 TPS 下降" (置信度 88%, 已验证)
- 点击查看推理步骤 (reasoningSteps)

### 7. 知识库 (Knowledge)
- "Redis 连接池耗尽处理方案" (成功率 85%)
- "JVM CPU 持续高位处理方案" (成功率 70%)

### 8. 推荐 (Recommendation)
- "扩容订单服务实例" (优先级 HIGH, 待审批)
- "优化 Redis 连接池配置" (优先级 MEDIUM, 已审批)

### 9. 执行 (Execution)
- 2026-07-26 14:30 张三执行 Redis 优化
- 效果: TPS 800→4500, CPU 95%→60%
- 评分: ★★★★☆
