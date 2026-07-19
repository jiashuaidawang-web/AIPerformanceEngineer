# AI Performance Engineer — MVP 运行说明

> 版本：1.0.0-SNAPSHOT | 更新：2026-07-19

---

## 1. 项目结构

```
AIPerformanceEngineer/
├── pom.xml                     # 父 POM (JDK 8, Spring Boot 2.7.18)
├── docker-compose.yml          # 基础设施：MySQL 8 + ClickHouse
│
├── aipe-common/                # 公共领域模型 + 枚举
├── aipe-connectors/            # Connector 父 POM
│   ├── connector-sdk/          # Connector SPI 接口
│   ├── connector-jvm/          # JVM JMX 采集
│   ├── connector-linux/        # Linux /proc 采集
│   ├── connector-redis/        # Redis Jedis 采集
│   └── connector-mysql/        # MySQL JDBC 采集
├── aipe-observation/           # Observation Pipeline 处理链
├── aipe-agent/                 # Agent 运行时
├── aipe-storage/               # 存储层路由
├── aipe-config-manager/        # 控制中心 (Agent/Config/Deploy)
└── aipe-backend/               # 后端服务 (Scenario/Query)
```

---

## 2. 技术栈

| 组件 | 版本 |
|------|------|
| JDK | 1.8 |
| Spring Boot | 2.7.18 |
| Maven | 3.9+ |
| MySQL | 8.0 |
| ClickHouse | 23.8 |
| Redis | (任意 5.x+) |
| Jedis | 4.4.6 |
| Lombok | 1.18.30 |

---

## 3. 基础设施

依赖外部服务（已运行）：

| 服务 | 地址 | 库/Schema |
|------|------|-----------|
| MySQL 8.0 | 100.97.74.45:3306 | `wushi` (root / astock_root) |
| ClickHouse 23.8 | 100.97.74.45:8123 | `wushi` (default / pamirs@123) |
| Redis (可选) | 自备 | — |

> 各模块 `application.yml` 已配置好上述连接信息。

---

## 4. 数据库初始化

### 4.1 MySQL（元数据表）

```bash
# 登录 MySQL
mysql -u root -pastock_root

# 执行 schema
SOURCE aipe-backend/src/main/resources/aipe-schema.sql;
```

或命令行直接导入：
```bash
mysql -u root -pastock_root wushi < aipe-backend/src/main/resources/aipe-schema.sql
```

创建的表：`resource`, `agent`, `connector`, `observation_metadata`, `config_version`, `deployment_record`, `audit_log`

### 4.2 ClickHouse（时序数据表）

```bash
# 使用 clickhouse-client 连接并执行
clickhouse-client --host 100.97.74.45 --port 8123 --user default --password pamirs@123 --database wushi --query "
CREATE TABLE IF NOT EXISTS metric_observation (
    id UUID DEFAULT generateUUIDv4(),
    timestamp DateTime DEFAULT now(),
    resource_id String,
    resource_type String DEFAULT 'HOST',
    metric_name String,
    metric_value Float64,
    labels String DEFAULT ''
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(timestamp)
ORDER BY (resource_id, metric_name, timestamp);
"
```

---

## 5. 项目构建

```bash
cd /Users/null/IdeaProjects/github/AIPerformanceEngineer

# 全量构建（约 8 秒）
mvn clean install

# 验证结果：12 个模块全部 BUILD SUCCESS
```

---

## 6. 启动顺序

### 启动 aipe-backend（后端服务，端口 8081）

```bash
java -jar aipe-backend/target/aipe-backend-1.0.0-SNAPSHOT.jar
```

### 启动 aipe-config-manager（控制中心，端口 8080）

```bash
java -jar aipe-config-manager/target/aipe-config-manager-1.0.0-SNAPSHOT.jar
```

### 启动 aipe-agent（采集 Agent）

```bash
java -jar aipe-agent/target/aipe-agent-1.0.0-SNAPSHOT.jar
```

Agent 启动后会读取 `application.yml` 中的 Connector 配置，真实采集 JVM + Linux 指标。

---

## 7. API 端点 (ape-config-manager)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/v1/agents/register | Agent 注册 |
| POST | /api/v1/agents/{id}/heartbeat | Agent 心跳 |
| GET | /api/v1/agents | Agent 列表 |
| DELETE | /api/v1/agents/{id} | 移除 Agent |
| POST | /api/v1/configs | 保存配置 |
| POST | /api/v1/configs/{id}/publish | 下发配置 |
| POST | /api/v1/deployment/deploy | 部署 Agent |

### 注册 Agent 示例

```bash
curl -X POST http://localhost:8080/api/v1/agents/register \
  -H "Content-Type: application/json" \
  -d '{"agentId":"agent-001","serverId":"server-001","hostname":"localhost","ip":"127.0.0.1"}'
```

---

## 8. Connector 配置 (aipe-agent/application.yml)

```yaml
agent:
  agentId: agent-001
  serverId: server-001
  environment: dev
  backendUrl: http://localhost:8080
  schedulerPoolSize: 4
  sendTimeoutMs: 5000
  connectors:
    - type: JVM        # JMX 真实采集
      enabled: true
      intervalMs: 30000
    - type: LINUX      # /proc 真实采集
      enabled: true
      intervalMs: 30000
    - type: REDIS      # Jedis 采集（需 Redis 运行）
      enabled: false
      intervalMs: 30000
      properties:
        host: localhost
        port: "6379"
    - type: MySQL      # JDBC 采集（需 MySQL 可连接）
      enabled: false
      intervalMs: 30000
      properties:
        host: localhost
        port: "3306"
        user: root
        password: root
```

> 配置 `enabled: true` 即开启对应采集。Redis / MySQL 需先启动对应服务。

---

## 9. 验证采集

Agent 启动后，周期采集日志：

```
[agent-scheduler-xxx] DEBUG Placeholder connector xxx collect triggered (no real data)
```

JVM + Linux Connector 真实采集日志：

```
[INFO] Connector started: id=agent-001-jvm
[INFO] Connector started: id=agent-001-linux
```

健康检查日志（每 30 秒）：

```
[INFO] [HealthCheck] state=RUNNING, agentId=agent-001, uptime=120s, connectors=2/2
```

---

## 10. 数据流

```
┌──────────────────────────────────────────────────────────────────┐
│                    AI Performance Engineer                        │
│                                                                  │
│  ┌─────────┐  JVM JMX   ┌─────────────┐                        │
│  │ ai-agent │───Linux──────────────────▶ aipe-observation        │
│  │  (采集)  │   Redis    │  (Pipeline)  │                        │
│  │          │   MySQL    └──────┬───────┘                        │
│  └─────────┘                   │                                 │
│                                ▼                                 │
│                    ┌─────────────────────┐                      │
│                    │     aipe-storage    │                      │
│                    │  (StorageRouter)    │                      │
│                    └──────────┬──────────┘                      │
│                               │                                  │
│              ┌────────────────┼────────────────┐                │
│              ▼                                 ▼                │
│    ┌──────────────────┐              ┌─────────────────┐       │
│    │   MySQL 8.0      │              │  ClickHouse 23.8 │       │
│    │ (元数据/配置)     │              │ (时序指标数据)   │       │
│    └──────────────────┘              └─────────────────┘       │
│                                                                  │
│  ┌────────────────┐                                              │
│  │ aipe-config    │ ◀── REST API ──▶ 前端/Dashboard            │
│  │ -manager:8080  │                                              │
│  │ (Agent Registry│                                              │
│  │  Config Center │                                              │
│  │  Deployment)   │                                              │
│  └────────────────┘                                              │
└──────────────────────────────────────────────────────────────────┘
```

---

## 常见问题

**Q: 启动报 `ClassNotFoundException: com.mysql.cj.jdbc.Driver`**
A: 确认 MySQL Connector/J 在 classpath。MVP 阶段 Agent 通过 JDBC URL 直连 MySQL。

**Q: Redis/MySQL 采集报连接失败**
A: 默认 `enabled: false`。开启前确保对应服务运行，且 application.yml 中 host/port/password 正确。

**Q: 怎么查看采集到的指标？**
A: MVP 阶段 ObservationSender 输出到日志。后续可对接 ClickHouse + Grafana 可视化。
