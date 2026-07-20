# AI Performance Engineer — WP 继续推进上下文

> **用途**：Clean / 新对话后，读取此文档即可恢复全部上下文，继续 WP012~WP018 高质量推进。
> **最后更新**：2026-07-20（WP011 完成后）

---

## 一、终极目标（不可偏离）

### 产品愿景

> **做成一款 B 端产品，面向所有企业，核心能力是全链路自动压测，帮助企业快速找到业务瓶颈。**

### 目标客户画像

| 维度 | 说明 |
|------|------|
| **谁** | 有 IT 系统的企业（互联网/电商/金融/政企/SaaS） |
| **痛点** | 性能问题发现慢、根因定位难、优化效果不确定、压测编排复杂 |
| **买单决策者** | CTO / 技术总监 / SRE 负责人 |
| **使用团队** | 性能工程团队 / SRE / 后端开发 |

### 价值主张（一句话）

> **用 AI 替代人肉排查，自动发现瓶颈、定位根因、沉淀知识、闭环验证。**

### 企业痛点 → 我们的价值

| 企业痛点 | 我们的价值 | 对应能力 |
|---------|-----------|---------|
| 性能问题发现慢（靠人肉排查）| 自动发现瓶颈（AI 推理）| AI Runtime 推理流水线 |
| 根因定位难（依赖专家经验）| 定位根因（Timeline + Evidence）| Evidence Engine + Knowledge |
| 优化效果不确定（缺乏闭环验证）| 沉淀知识（Knowledge 持续成长）| Knowledge Engine |
| 压测编排复杂（人工配置）| 闭环验证（Execution → New Observation）| Execution + Optimization |

### 通用性承诺（设计原则，不是当前实现）

> **当前先在自己公司用起来（MVP 验证），但每一步设计都为 B 端通用产品留好扩展点。**

| 通用性维度 | 设计决策 | 当前状态 |
|-----------|---------|---------|
| **部署通用** | Java 跨平台 / Docker Compose / 后续 Helm Chart | ✅ Docker Compose 已就绪 |
| **数据源通用** | Connector SPI（新数据源只需实现接口）| ✅ 5 种 Connector 已支持 |
| **存储通用** | MySQL + ClickHouse 标准组合 | ✅ |
| **IT 对象通用** | 统一 Resource 抽象（不绑定特定技术栈）| ✅ WP011 已完成 |
| **行业通用** | 业务 Domain 可配置（不硬编码电商/金融）| 🔲 待建设 |
| **多租户通用** | 预留 tenant_id / 企业隔离扩展点 | 🔲 待建设 |
| **安全通用** | 预留 RBAC / 审计 / 加密扩展点 | 🔲 待建设 |

---

## 二、当前进度（已完成）

### 时间线

| 阶段 | 范围 | 状态 |
|------|------|------|
| **M1** | Agent + Connector + Pipeline + Storage + Backend | ✅ 已完成 |
| **M2 Constitution** | 项目宪法 + Architecture Laws | ✅ 已完成 |
| **M2 IM** | IM-000~IM-011（Implementation Mapping）| ✅ 已完成 |
| **M2 Standards** | 四大基础标准（Laws/Specification/Blueprint/Gate）| ✅ 已完成 |
| **M2 WP011** | Resource Domain（统一资源抽象）| ✅ 已完成 |

### M1 已具备能力（基础设施）

| 模块 | 能力 |
|------|------|
| `aipe-agent` | Agent Bootstrap + Heartbeat + 配置热更新 |
| `aipe-connectors` | Connector SDK + JVM/Linux/Redis/MySQL 4 种采集器 |
| `aipe-observation` | Observation Pipeline（序列化/验证/批处理/队列）|
| `aipe-storage` | MySQL（元数据）+ ClickHouse（时序）双存储 |
| `aipe-backend` | Gateway（接收 Agent 数据 + Observation 查询）|

### WP011 已具备能力（Domain 层起点）

| 能力 | 文件数 |
|------|-------|
| Resource 聚合根 + DDD 分层 | 9（Domain）+ 5（Infra）+ 5（App）+ 7（API）+ 5（Config）= 32 文件 |
| REST API | CRUD + 发现 + 状态流转 |
| MySQL 真实集成 | 乐观锁 + 自动迁移 + Druid 连接池 |

---

## 三、待完成工作（WP012~WP018）

### 路线图

```
WP011 Resource ✅
    │
    ├── WP012 Observation Engine
    │      └── Observation 领域模型 + Repository + 与 Resource 关联
    │
    ├── WP013 Relationship Model
    │      └── Resource 间关系 + 图数据库（Neo4j）+ Topology 动态生成
    │
    ├── WP014 Timeline Model
    │      └── Timeline 构建（按 Resource + 时间聚合 Observation）
    │
    ├── WP015 Evidence Engine
    │      └── 异常检测 + 根因推理 + Evidence 生成
    │
    ├── WP016 Knowledge Engine
    │      └── Evidence → Knowledge（验证 + 版本演化 + 持久化）
    │
    ├── WP017 Recommendation Engine
    │      └── Knowledge → Recommendation（生成 + 优先级 + 执行计划）
    │
    └── WP018 Execution + Optimization
           └── Recommendation 执行 + 效果验证 + 闭环 → New Observation
```

### 各 WP 与终极目标的对应

| WP | 能力 | 对企业客户的价值 | 通用性贡献 |
|----|------|----------------|-----------|
| WP012 Observation | 统一事实模型 | 所有数据源统一度量 | Connector SPI 通用 |
| WP013 Relationship | 拓扑自动发现 | 故障传播分析 | 不绑定特定架构 |
| WP014 Timeline | 历史回放 | 问题时间线重建 | 时间序列通用 |
| WP015 Evidence | 异常发现 + 根因 | **核心价值**：AI 推理瓶颈 | 推理逻辑可配置 |
| WP016 Knowledge | 知识沉淀 | 经验不随人流失 | Knowledge 库可跨企业 |
| WP017 Recommendation | 优化建议 | 闭环验证效果 | 建议模板可配置 |
| WP018 Execution | 执行优化 | 自动/半自动优化 | 执行器 SPI 通用 |

---

## 四、架构规范（不可违反）

### DDD 分层（所有 WP 必须遵守）

```
com.aipe.{domain}
├── domain/                ← 纯 POJO（禁止 Spring/MyBatis 注解）
├── application/           ← 事务编排（唯一事务 Owner）
├── infrastructure/        ← 持久化（PO + Mapper + RepositoryImpl）
└── api/                   ← REST（Controller + DTO + ExceptionHandler）
```

### Package 命名（IM-001）

- Root Package: `com.aipe`（不是 `com.ai.performance`）
- 一级 Package: `api` / `application` / `domain` / `repository` / `infrastructure` / `connector` / `ai` / `common` / `config`
- 禁止新增一级 Package（需 RFC）

### Repository 规则（IM-009 / Gateway Laws）

- 接口在 Domain、实现在 Infrastructure
- **返回 Domain，禁止返回 PO/DO/Map**
- 一个 Aggregate Root 只有一个 Repository
- **Repository 禁止开启事务**

### 事务规则（IM-010 / Orchestration Laws）

- Application Layer 是唯一事务 Owner
- `@Transactional` 只放在 Application Service
- Domain / Repository / Controller **禁止事务**

### 依赖方向（IM-001 / Architecture Laws）

```
API ↓ Application ↓ Domain ↓ Repository ↓ Infrastructure
```

**禁止反向依赖，禁止循环依赖。**

### 命名规则（IM-011）

- Entity: Resource / Observation / Evidence / ...
- Repository: `XxxRepository`
- Service: `XxxApplicationService`
- Controller: `XxxController`
- DTO: `XxxDTO`
- **禁止**: Model / Bean / Data / Temp / Manager / Util / Helper

### 核心法则（Constitution / Laws，Frozen 不可改）

| 法则 | 内容 |
|------|------|
| Law-001 | Everything Is Resource |
| Law-002 | Observation Belongs to Resource |
| Domain Law-001 | One Concept, One Domain Object |
| Gateway Law-001 | Repository Returns Domain, Never PO |
| Gateway Law-002 | One Aggregate, One Repository |
| Persistence Law-001 | Right Data, Right Storage |
| Persistence Law-002 | ClickHouse Stores Facts, Never Knowledge |
| Architecture Law-007 | Controller Is A Protocol Translator |
| AI Runtime Law-001 | AI Never Reads Database Directly |

---

## 五、技术栈（不可漂移）

| 组件 | 版本 | 说明 |
|------|------|------|
| Java | 1.8 | 运行环境 |
| Spring Boot | 2.7.18 | 应用框架 |
| MyBatis Plus | 3.5.3.1 | ORM |
| Druid | 1.2.20 | 连接池 |
| ClickHouse JDBC | 0.4.6 | ClickHouse 驱动 |
| Jedis | 4.4.6 | Redis 客户端 |
| Hutool | 5.8.22 | 工具库 |
| Lombok | 1.18.30 | 代码简化 |
| MySQL | 5.7+ / 8.0 | 元数据存储 |
| ClickHouse | 21+ | 时序存储 |

**新增依赖需用户确认。**

### 数据库连接（集成测试用）

| 存储 | 地址 | 库 | 认证 |
|------|------|-----|------|
| MySQL | 124.223.20.245:3306 | aipe_metadata | root / astock_root |
| ClickHouse | 124.223.20.245:8123 | metric_observation | default / pamirs@123 |

### 实际表结构（resource 表，已迁移）

| 列名 | 类型 | 说明 |
|------|------|------|
| `resource_id` | VARCHAR(64) PK | 业务主键（UUID）|
| `resource_name` | VARCHAR(256) | 资源名称 |
| `resource_type` | VARCHAR(32) | 资源类型 |
| `resource_category` | VARCHAR(32) | 资源分类 |
| `resource_status` | VARCHAR(16) | 资源状态 |
| `parent_resource_id` | VARCHAR(64) | 父资源 ID |
| `business_system` | VARCHAR(128) | 业务系统 |
| `cluster` | VARCHAR(128) | 集群标识 |
| `namespace` | VARCHAR(128) | 命名空间 |
| `environment` | VARCHAR(64) | 环境标识 |
| `version` | BIGINT | 乐观锁版本号 |
| `labels` | JSON | 业务标签 |
| `attributes` | JSON | 扩展属性 |
| `enterprise_id` | VARCHAR(64) | 企业 ID |
| `description` | TEXT | 描述 |
| `created_by` | VARCHAR(64) | 创建人 |
| `created_at` | TIMESTAMP | 创建时间 |
| `updated_at` | TIMESTAMP | 更新时间 |
| `deleted` | TINYINT | 逻辑删除 |

**注意**：PO 的 `@TableId` 必须用 `resource_id`，`@Version` 映射 `resource_status`（不是 `status`）。

---

## 六、编码质量标准（M1 标准回忆 + M2 升级）

### 开发约束

| 条款 | 内容 |
|------|------|
| 1 | Blueprint 优先于代码 |
| 2 | 不允许降低设计等级 |
| 3 | 不允许 Mock 代替真实采集 |
| 4 | 不允许空实现 |
| 5 | 不允许为了编译删除功能 |
| 6 | 每个 WP 必须完成验收标准 |
| 7 | 每个 WP 完成必须提交状态报告 |

### 代码质量要求

| 要求 | 说明 |
|------|------|
| 真实实现 | 禁止 TODO / Mock / 空方法 / return null 占位 / 伪代码 |
| 真实采集 | JVM 用 JMX，Linux 读 /proc，Redis 用 INFO，MySQL 用 SHOW STATUS |
| SLF4J 日志 | 使用 SLF4J，不用 System.out |
| Lombok | 使用 @Data / @Builder / @Slf4j |
| 线程安全 | 使用 ConcurrentHashMap / CopyOnWriteArrayList |
| 资源关闭 | 使用 try-with-resources |
| Java 8 兼容 | 不用 Java 9+ 特性（如 Map.of）|

### 验收标准

| 标准 | 说明 |
|------|------|
| mvn clean install | 全模块 BUILD SUCCESS |
| Spring Boot 启动 | 无 Bean 创建失败 |
| 无循环依赖 | 依赖关系单向 |
| 无重要 WARN | 无 WARN 级别异常 |
| 真实 MySQL 集成 | 不允许 Mock，必须跑通真实环境 |

### 开发流程

1. 读取 Blueprint（严格按 Blueprint 实现）
2. 创建所有 Java 文件（不遗漏任何类）
3. 实现所有 Method（不空实现）
4. 执行 mvn clean install（编译验证）
5. 修复编译问题（直到通过）
6. 真实 MySQL 集成测试（6+ 场景）
7. 输出完成报告（每个 WP 提交报告）

---

## 七、用户偏好（Feedback）

| 偏好 | 说明 |
|------|------|
| **"先读项目再写代码"** | 给路径就通读，不假设 |
| **"对齐实际表结构"** | IM 文档列名可能和实际 DB 不一致，以实际为准 |
| **"真实集成测试"** | 不允许 Mock，必须跑通真实环境 |
| **"你偏好对话确认"** | 关键决策先问再动，不要自行发挥 |
| **"按优先级推进"** | GPT 给的优先级 + 用户确认 = 执行顺序 |
| **"质量保证"** | 每个 WP 必须 6+ 真实集成测试场景 PASS |

---

## 八、Clean 后恢复步骤

### 你需要做的

1. Clean 项目
2. 把此文档发给我
3. 告诉我"继续 WP012"

### 我会做的

1. 重新读取项目结构（pom.xml / 源文件 / DB schema）
2. 读取对应 WP 的 Blueprint（`M2-005WP/WP012-xxx.md`）
3. 读取对应 WP 的 IM 规范（`IM-002 Domain Model Mapping` 等）
4. 读取此上下文文档（恢复用户偏好 + 架构规范 + 技术栈）
5. 开始实现

### 恢复后第一件事

```bash
# 验证项目完整性
mvn clean install -DskipTests
# 验证 DB 连接（快速启动 Spring Boot）
# 开始 WP012 实现
```

---

## 九、关键设计决策记录（已确认，不可改）

| 决策 | 确认结果 |
|------|---------|
| WP011 代码放哪个模块 | 新建 `aipe-resource` 模块 |
| Observation→Resource 关联 | 放到 WP012（不在 WP011）|
| 测试数据库 | 真实 MySQL（不是 H2）|
| 包结构 | 严格 DDD（domain/application/infrastructure），不是 Blueprint 给的传统分层 |
| 关系绑定存储 | WP011 用 MySQL，WP013 再联动图数据库 |
| Repository 返回 | Domain（不是 PO），Infrastructure 做转换 |
| 版本管理 | MyBatis Plus @Version 自动管理，domain 不手动 version++ |
| 终极目标 | B 端通用产品（先自己公司验证，保留扩展点）|

---

## 十、风险 & 已知问题（Clean 后注意）

| 问题 | 解决方式 |
|------|---------|
| MySQL 5.x 不支持 `ADD COLUMN IF NOT EXISTS` | 使用 `SET @col_exists + PREPARE/EXECUTE` 方式 |
| Spring ResourceDatabasePopulator 不支持 DELIMITER | ALTER 列用 Spring 脚本，索引用 Java 代码创建 |
| MyBatis Plus 乐观锁需要 OptimisticLockerInnerInterceptor | 已加到 MyBatisPlusConfig |
| domain 手动 version++ 与 MyBatis Plus 冲突 | 删除 domain 里的 version++，交给 MyBatis Plus |
| IM 文档列名 vs 实际 DB 列名不一致 | 以实际 DB 为准（diagnose 后调整 PO）|
| Spring Boot 启动慢（~45 秒）| macOS DNS 解析慢，需 /etc/hosts 加 localhost 条目 |
| 中文 URL 编码问题 | curl 参数用 URL 编码（`%E6%B5%8B%E8%AF%95` = 测试）|

---

*此文档是 WP012~WP018 推进的"宪法级上下文"。Clean + 发给 AI 即可恢复。*
