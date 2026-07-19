# M1 Acceptance Test — AI Performance Engineer MVP

> 最后更新: 2026-07-19
> 原则：验证产品能力，不验证代码

---

## M1-Stage1：工程验证 ✅

**通过标准：工程可以稳定启动**

| TC | 测试项 | 步骤 | 预期 | 状态 |
|----|--------|------|------|------|
| TC001 | 全模块构建 | `mvn clean install` | BUILD SUCCESS，12 模块全部通过 | ✅ |
| TC002 | Backend 启动 | `java -jar aipe-backend/target/aipe-backend-1.0.0-SNAPSHOT.jar` | 8081 端口 Started，无 ERROR | ✅ |
| TC003 | Config Manager 启动 | `java -jar aipe-config-manager/...jar` | 8080 端口 Started，无 ERROR | ✅ |
| TC004 | Agent 启动 | `java -jar aipe-agent/...jar` | 3 个 Connector 全部加载 | ✅ |
| TC005 | Spring Bean 初始化 | 启动日志 | 无 Bean 创建失败、无循环依赖 | ✅ |

---

## M1-Stage2：Agent 生命周期验证 ⭐⭐⭐⭐⭐

**通过标准：Agent 注册、心跳、状态流转正确**

| TC | 测试项 | 步骤 | 预期 | 状态 |
|----|--------|------|------|------|
| TC010 | Agent 注册 | Agent 启动 | Agent 自动注册到 ConfigManager | ❌ |
| TC011 | Agent Online 可见 | `curl http://localhost:8080/api/v1/agents` | 返回 Agent 列表，含 agent-001 | ❌ |
| TC012 | Heartbeat 发送 | Agent 运行中 | 每 30 秒发送 Heartbeat，Backend 接收 | ❌ |
| TC013 | Agent Offline 检测 | Ctrl+C 停止 Agent | ConfigManager 90 秒后标记 OFFLINE | ❌ |
| TC014 | Agent 配置读取 | 观察启动日志 | 从 application.yml 读取 connector 配置 | ✅ |
| TC015 | Connector 加载 | 观察启动日志 | JVM/Linux/MySQL 全部注册成功 | ✅ |

---

## M1-Stage3：Connector 验证 ⭐⭐⭐⭐⭐

**通过标准：每个 Connector 返回真实数据，不是 Mock**

### JVM Connector

| TC | 测试项 | 验证方式 | 预期 | 状态 |
|----|--------|----------|------|------|
| TC020 | Heap Used | 日志或 API | `jvm.memory.heap.used > 0`（字节） | ⚠️ 日志可见 |
| TC021 | Heap Max | 日志或 API | `jvm.memory.heap.max > 0` | ⚠️ |
| TC022 | GC Count | 日志或 API | `jvm.gc.*.count >= 0` | ⚠️ |
| TC023 | GC Time | 日志或 API | `jvm.gc.*.time_ms >= 0` | ⚠️ |
| TC024 | Thread Count | 日志或 API | `jvm.thread.count > 0` | ⚠️ |
| TC025 | Daemon Thread | 日志或 API | `jvm.thread.daemon > 0` | ⚠️ |
| TC026 | Loaded Class | 日志或 API | `jvm.classloader.loaded_count > 0` | ⚠️ |

### Linux Connector

| TC | 测试项 | 验证方式 | 预期 | 状态 |
|----|--------|----------|------|------|
| TC030 | CPU 使用率 | 日志或 API | `linux.cpu.cpu0.usage_percent` 0-100 | ⚠️ macOS 无 /proc |
| TC031 | Memory Total | 日志或 API | `linux.memory.total > 0` | ⚠️ |
| TC032 | Memory Available | 日志或 API | `linux.memory.available > 0` | ⚠️ |
| TC033 | Disk Usage | 日志或 API | `linux.disk.*.usage_percent` 0-100 | ✅ 日志可见 |
| TC034 | Load Average | 日志或 API | `linux.load.1min >= 0` | ⚠️ |
| TC035 | TCP 状态 | 日志或 API | `linux.tcp.*_count >= 0` | ⚠️ |
| TC036 | 进程数 | 日志或 API | `linux.process.total > 0` | ⚠️ |

### MySQL Connector

| TC | 测试项 | 验证方式 | 预期 | 状态 |
|----|--------|----------|------|------|
| TC040 | SHOW GLOBAL STATUS | 日志或 API | `mysql.status.threads_connected > 0` | ⚠️ 日志可见 |
| TC041 | SHOW PROCESSLIST | 日志或 API | `mysql.processlist.total > 0` | ⚠️ |
| TC042 | InnoDB Buffer Pool | 日志或 API | `mysql.bufferpool.*_pages_total > 0` | ⚠️ |
| TC043 | Row Lock Waits | 日志或 API | `mysql.lock.innodb_row_lock_waits >= 0` | ⚠️ |
| TC044 | Slow Queries | 日志或 API | `mysql.status.slow_queries >= 0` | ⚠️ |
| TC045 | 连接数 | 日志或 API | `mysql.connection.threads_connected > 0` | ⚠️ |

---

## M1-Stage4：Observation Pipeline ⭐⭐⭐⭐⭐

**通过标准：数据不落日志，落数据库，每 5 秒有新增**

| TC | 测试项 | 步骤 | 预期 | 状态 |
|----|--------|------|------|------|
| TC050 | Observation 落库 | 查询 ClickHouse `metric_observation` | 有 INSERT 记录 | ❌ |
| TC051 | Observation 字段完整 | 查询结果 | `resource_id`, `metric_name`, `metric_value`, `timestamp` 均有值 | ❌ |
| TC052 | 周期性新增 | 连续查询 | 每 5-30 秒有新增记录 | ❌ |
| TC053 | 数据连续性 | 1 分钟不中断 | 每秒都有 Observation | ❌ |
| TC054 | Tags 正确 | 查询结果 | `tags` 包含 source/collector 信息 | ❌ |

---

## M1-Stage5：End-to-End ⭐⭐⭐⭐⭐

**通过标准：JMeter 压测 → Observation 实时变化**

| TC | 测试项 | 步骤 | 预期 | 状态 |
|----|--------|------|------|------|
| TC060 | 压测期间 CPU 上升 | JMeter 100 TPS + 查询 ClickHouse | `cpu.usage` 较压测前上升 | ❌ |
| TC061 | 压测期间 GC 增加 | 压测 + 查询 | `gc.count` 增加 | ❌ |
| TC062 | 压测期间 Thread 增加 | 压测 + 查询 | `thread.count` 增加 | ❌ |
| TC063 | 数据可查询 | `curl http://localhost:8080/api/v1/observations` | 返回时间序列数据 | ❌ |
| TC064 | Scenario 关联 | 创建 Scenario + 压测 | Observation 关联 Scenario ID | ❌ |

---

## Summary

| Stage | 通过标准 | 当前状态 |
|-------|---------|---------|
| Stage1 工程验证 | 工程稳定启动 | ✅ **PASS** |
| Stage2 Agent 生命周期 | 注册/心跳/状态流转 | ❌ **FAIL** — 未实现 |
| Stage3 Connector 验证 | 每个 Connector 返回真实数据 | ⚠️ **PARTIAL** — 数据在日志，未入库 |
| Stage4 Observation Pipeline | 数据落 ClickHouse，5秒新增 | ❌ **FAIL** — 未实现 |
| Stage5 End-to-End | JMeter → Observation 变化 | ❌ **FAIL** — 未实现 |

### 未实现的关键能力

1. **Backend 没有接收接口** — Agent 发送到 8081 的数据 404
2. **数据不落库** — 当前只输出日志，ClickHouse 无数据
3. **Heartbeat 机制** — Agent 没有向 Backend 发送心跳
4. **Agent 注册** — ConfigManager 看不到 Agent
5. **Observation 查询 API** — 无法查询时序数据
6. **Scenario 管理** — 没有压测场景概念

### 下一步优先级

1. **补 Backend Observation 接收接口** — Agent 数据落 ClickHouse
2. **补 Heartbeat + Agent 注册** — Backend/ConfigManager 能看到 Agent
3. **补 Observation 查询 API** — 能查 ClickHouse 数据
4. **补 Scenario 管理** — 压测场景编排
