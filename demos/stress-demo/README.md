# StressDemo — 标准综合压测靶机

> Spring Boot 3.3.7 + JDK 21 标准压测靶机，用于验证 AI Performance Engineer 全链路。

## 压测场景覆盖

| 场景 | 接口 | 说明 |
|------|------|------|
| DB 读（主键）| GET /api/products/{id} | 命中索引的标准查询 |
| DB 读（分页）| GET /api/products?page=1&size=100 | MyBatis Plus 分页 |
| DB 读（全表扫描）| GET /api/products/search?keyword=a | LIKE '%a%' 慢查询 |
| DB 写 | POST /api/products | 单条插入 |
| CPU 密集 | GET /api/compute/fibonacci/40 | 递归 Fibonacci |
| CPU 密集 | GET /api/compute/prime/10000 | 查找第 N 个素数 |
| 慢响应 | GET /api/slow?delay=2000 | Thread.sleep 模拟延迟 |
| 内存泄漏 | GET /api/memory/leak?mb=50 | 持续占用堆内存 |
| 缓存场景 | GET /api/cache/{key} | hit/miss 统计 |
| 系统状态 | GET /api/stats | 当前堆/线程/DB 状态 |

## 部署

### 1. 数据库初始化（一次性）

```bash
# 在 MySQL 云服务器上执行
mysql -uroot -p < db/init.sql
# 创建 stress_demo 库 + 10 万条产品数据
```

### 2. 构建 + 启动

```bash
mvn clean package -DskipTests
docker-compose up -d
```

### 3. 验证

```bash
curl http://localhost:8090/api/products/1
curl http://localhost:8090/api/compute/fibonacci/35
curl http://localhost:8090/api/stats
```

### 4. JMeter 压测计划

阶梯加压策略（见 `docker/jmeter-plan.jmx`）：

1. 基线阶段: 50 users × 3 min → 确认服务正常
2. 轻负载: 100 users × 5 min → 观测响应时间基线
3. 中负载: 300 users × 10 min → 寻找性能拐点
4. 重负载: 500 users × 10 min → CPU/内存瓶颈暴露
5. 峰值冲击: 800 users × 3 min → 系统极限
6. 恢复阶段: 50 users × 3 min → 验证恢复能力

> 目标：通过 AI Performance Engineer 平台观测压测产生的 Observation → Timeline → Evidence → Knowledge，
> 验证整套 AI 推理链路是否真正能识别出"全表扫描慢查询"和"CPU 密集型"等瓶颈。
