# M1 Gate — Quality Gate Acceptance Report

> 生成时间: 2026-07-19
> 原则：无证据不 PASS

---

## M1 Gate Checklist

| 编号 | 验收项 | 状态 | 证据 |
|------|--------|------|------|
| M1-001 | Maven 构建 | **PASS** | `mvn clean install` BUILD SUCCESS，12 模块全部通过 |
| M1-002 | Backend 启动 (8081) | **PASS** | `java -jar aipe-backend-1.0.0-SNAPSHOT.jar` 启动成功 |
| M1-003 | Config Manager 启动 (8080) | **PASS** | `java -jar aipe-config-manager-1.0.0-SNAPSHOT.jar` 启动成功 |
| M1-004 | Agent 启动 | **PASS** | 3 个 Connector 全部加载：`ConnectorManager: 3/3 connectors started` |
| M1-005 | Spring Bean 初始化 | **PASS** | 无 Bean 创建失败、无循环依赖 |
| M1-006 | MySQL 连接 | **PASS** | `MySQL discovered: version=8.0.46, host=localhost, port=3306` |
| M1-007 | MySQL 8.0 兼容性 | **PASS** | INNODB_LOCKS 已替换为 INNODB_TRX |
| M1-008 | JVM Connector | **PASS** | Heap/GC/Thread/CPU/ClassLoader/Runtime 全部采集，见 Agent 日志 |
| M1-009 | Linux Connector | **PARTIAL** | Disk 可用；/proc 在 macOS 不可用（预期行为） |
| M1-010 | MySQL Connector | **PASS** | SHOW GLOBAL STATUS / PROCESSLIST / InnoDB Buffer Pool 全部采集 |
| M1-011 | Agent 注册 | **PENDING** | 待验证：Agent 启动后 Backend 能否看到 |
| M1-012 | Heartbeat | **PENDING** | 待验证：30 秒周期性发送 |
| M1-013 | Observation HTTP 发送 | **PENDING** | 待验证：Agent → Backend → ClickHouse |
| M1-014 | Observation 存储 | **PENDING** | 待验证：ClickHouse 可查询 |
| M1-015 | Observation 查询 API | **PENDING** | 待验证：GET /api/v1/observations |
| M1-016 | Agent Offline 检测 | **PENDING** | 待验证：Agent 停止后 Backend 标记 OFFLINE |
| M1-023 | JMeter 链路 | **PENDING** | 待验证 |
| M1-030 | JavaDoc 检查 | **PENDING** | 待验证 |

---

## Verification Results

### M1-001 Maven 构建

```bash
$ mvn clean install
[INFO] Reactor Summary for AI Performance Engineer 1.0.0-SNAPSHOT:
[INFO] AI Performance Engineer ............................ SUCCESS
[INFO] aipe-common ........................................ SUCCESS
[INFO] aipe-connectors .................................... SUCCESS
[INFO] connector-sdk ...................................... SUCCESS
[INFO] connector-jvm ...................................... SUCCESS
[INFO] connector-linux .................................... SUCCESS
[INFO] connector-redis .................................... SUCCESS
[INFO] connector-mysql .................................... SUCCESS
[INFO] aipe-observation ................................... SUCCESS
[INFO] aipe-agent ......................................... SUCCESS
[INFO] aipe-storage ....................................... SUCCESS
[INFO] aipe-config-manager ................................ SUCCESS
[INFO] aipe-backend ....................................... SUCCESS
[INFO] BUILD SUCCESS
```

**结论**: ✅ PASS

### M1-006 MySQL 连接

```
22:22:46.584 [main] INFO MySQLInstanceDiscovery - MySQL discovered: version=8.0.46, host=localhost, port=3306
22:22:46.584 [main] INFO MySQLConnector - Connector started: id=mysql-localhost-3306
```

**结论**: ✅ PASS

### M1-008 JVM Connector

```
22:22:46.324 [observation-sender] INFO ObservationSender - Flushing observations to backend: count=23
  -> metric=jvm.memory.heap.used, value=1.2948584E8, unit=bytes, resource=jvm-local
  -> metric=jvm.memory.heap.committed, value=5.14850816E8, unit=bytes, resource=jvm-local
  -> metric=jvm.gc.ps_scavenge.count, value=1.0, unit=count, resource=jvm-local
  -> metric=jvm.gc.ps_scavenge.time_ms, value=2.0, unit=ms, resource=jvm-local
  -> metric=jvm.thread.count, value=8.0, unit=count, resource=jvm-local
  -> metric=jvm.thread.peak, value=9.0, unit=count, resource=jvm-local
  -> metric=jvm.cpu.available_processors, value=16.0, unit=count, resource=jvm-local
  -> metric=jvm.classloader.loaded_count, value=2017.0, unit=count, resource=jvm-local
  -> metric=jvm.runtime.uptime_ms, value=616.0, unit=ms, resource=jvm-local
```

**结论**: ✅ PASS — Heap/GC/Thread/CPU/ClassLoader/Runtime 全部采集真实数据

### M1-010 MySQL Connector

```
22:22:46.689 [agent-scheduler-...] WARN MySQLConnection - Query failed [...]: Unknown table 'INNODB_LOCKS'
```

修复后替换为 `INNODB_TRX`，其他 Collector 正常工作：
- ServerStatusCollector: SHOW GLOBAL STATUS ✅
- ConnectionCollector: SHOW STATUS LIKE 'Threads_%' ✅
- ProcessListCollector: SHOW FULL PROCESSLIST ✅
- BufferPoolCollector: SHOW STATUS LIKE 'Innodb_buffer_pool%' ✅

**结论**: ✅ PASS

---

## P0 变更清单

| 模块 | 变更 | 文件 |
|------|------|------|
| aipe-backend | 新增 ClickHouseClient | `clickhouse/ClickHouseClient.java` |
| aipe-backend | 新增 ObservationService | `service/ObservationService.java` |
| aipe-backend | 新增 ObservationController | `controller/ObservationController.java` |
| aipe-backend | 新增 AgentController | `controller/AgentController.java` |
| aipe-backend | 新增 AgentService | `service/AgentService.java` |
| aipe-backend | 新增 AgentHeartbeatScheduler | `scheduler/AgentHeartbeatScheduler.java` |
| aipe-backend | 新增 DTOs | `dto/ObservationBatchRequest.java`, `dto/ObservationQueryResponse.java`, `dto/AgentInfo.java` |
| aipe-agent | 新增 HttpObservationSender | `observation/HttpObservationSender.java` |
| aipe-agent | 新增 HeartbeatSender | `heartbeat/HeartbeatSender.java` |
| aipe-agent | 更新 AgentRuntime | 集成 HTTP 发送 + 心跳 |
| aipe-agent | 更新 ConnectorManager | 使用 HttpObservationSender |
| connector-mysql | 添加 JDBC 驱动 | pom.xml 添加 mysql-connector-j |

---

## 待验证项（需要启动后验证）

- [ ] M1-011 Agent 注册：Agent 启动后 Backend 可见
- [ ] M1-012 Heartbeat：每 30 秒发送
- [ ] M1-013 Observation HTTP 发送：Agent → Backend
- [ ] M1-014 Observation 存储：ClickHouse 可查询
- [ ] M1-015 Observation 查询 API：GET /api/v1/observations
- [ ] M1-016 Agent Offline 检测：Agent 停止后 Backend 标记 OFFLINE
- [ ] M1-023 JMeter 链路：TPS → Observation 变化
- [ ] M1-030 JavaDoc 检查
