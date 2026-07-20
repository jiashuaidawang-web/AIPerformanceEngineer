# AI Performance Engineer — WP 继续推进上下文（完全自包含版）

> **用途**：Clean / 新对话后，**只发这一份文档** + 说"推进 WP12"即可恢复全部上下文。
> **最后更新**：2026-07-20（WP011 完成 + WP012~018 Blueprint 全部就绪）

---

## 一、终极目标（不可偏离）

### 产品愿景

> **做成一款 B 端产品，面向所有企业，核心能力是全链路自动压测，帮助企业快速找到业务瓶颈。**

### 目标客户

| 维度 | 说明 |
|------|------|
| **谁** | 有 IT 系统的企业（互联网/电商/金融/政企/SaaS）|
| **痛点** | 性能问题发现慢、根因定位难、优化效果不确定、压测编排复杂 |
| **买单决策者** | CTO / 技术总监 / SRE 负责人 |

### 价值主张

> **用 AI 替代人肉排查，自动发现瓶颈、定位根因、沉淀知识、闭环验证。**

### 企业痛点 → 我们的价值

| 企业痛点 | 我们的价值 | 对应能力 |
|---------|-----------|---------|
| 性能问题发现慢（靠人肉排查）| 自动发现瓶颈（AI 推理）| AI Runtime 推理流水线 |
| 根因定位难（依赖专家经验）| 定位根因（Timeline + Evidence）| Evidence Engine + Knowledge |
| 优化效果不确定（缺乏闭环验证）| 沉淀知识（Knowledge 持续成长）| Knowledge Engine |
| 压测编排复杂（人工配置）| 闭环验证（Execution → New Observation）| Execution + Optimization |

### 通用性承诺

> **当前先在自己公司用起来（MVP 验证），但每一步设计都为 B 端通用产品留好扩展点。**

| 通用性维度 | 设计决策 | 当前状态 |
|-----------|---------|---------|
| 部署通用 | Java 跨平台 / Docker Compose / 后续 Helm Chart | ✅ Docker Compose 已就绪 |
| 数据源通用 | Connector SPI（新数据源只需实现接口）| ✅ 5 种 Connector 已支持 |
| 存储通用 | MySQL + ClickHouse 标准组合 | ✅ |
| IT 对象通用 | 统一 Resource 抽象（不绑定特定技术栈）| ✅ WP011 已完成 |
| 行业通用 | 业务 Domain 可配置（不硬编码电商/金融）| 🔲 待建设 |
| 多租户通用 | 预留 tenant_id / 企业隔离扩展点 | 🔲 待建设 |
| 安全通用 | 预留 RBAC / 审计 / 加密扩展点 | 🔲 待建设 |

---

## 二、AI World Evolution Chain（世界演化链）

```
Reality（真实世界）
    │
    ▼
Resource（世界中的对象）
    │
    ▼
Observation（对象产生事实）
    │
    ▼
Timeline（事实形成历史）
    │
    ▼
Evidence（AI 解释历史）
    │
    ▼
Verification（现实验证）
    │
    ▼
Knowledge（验证后的经验）
    │
    ▼
Recommendation（知识指导决策）
    │
    ▼
Execution & Optimization（执行优化）
    │
    ▼
New Observation（产生新的事实 → 闭环）
```

### 各层对应的 World Model 规范

| 层 | World Model 规范路径 | 对应 WP |
|----|---------------------|---------|
| Resource | `M2-003-world-model/M2-007 Unified Resource Model（WP011）.md` | WP011 ✅ |
| Observation | `M2-003-world-model/M2-006-Observation-Model-Specification.md` | WP012 🔲 |
| Relationship | `M2-003-world-model/M2-008 Relationship Model.md` | WP013 🔲 |
| Topology | `M2-003-world-model/M2-009 Topology Model.md` | WP013 🔲 |
| Timeline | `M2-003-world-model/M2-010 Timeline Model.md` | WP014 🔲 |
| Evidence | `M2-003-world-model/M2-011 Evidence Model.md` | WP015 🔲 |
| Knowledge | `M2-003-world-model/M2-012 Knowledge Model.md` | WP016 🔲 |
| Recommendation | `M2-003-world-model/M2-013 Optimization Model.md` | WP017 🔲 |
| Execution+Optimization | `M2-003-world-model/M2-013 Optimization Model.md` | WP018 🔲 |

---

## 三、当前进度

### 已完成

| 阶段 | 范围 | 状态 |
|------|------|------|
| M1 | Agent + Connector + Pipeline + Storage + Backend | ✅ |
| M2 Constitution | 项目宪法 + Architecture Laws | ✅ |
| M2 IM | IM-000~IM-011 | ✅ |
| M2 Standards | 四大基础标准 | ✅ |
| **M2 WP011** | Resource Domain（32 Java 文件，真机 MySQL 集成测试 PASS）| ✅ |

### M1 已有基础设施

| 模块 | 能力 |
|------|------|
| `aipe-agent` | Agent Bootstrap + Heartbeat + 配置热更新 |
| `aipe-connectors` | Connector SDK + JVM/Linux/Redis/MySQL 4 种采集器 |
| `aipe-observation` | Observation Pipeline（序列化/验证/批处理/队列）|
| `aipe-storage` | MySQL（元数据）+ ClickHouse（时序）双存储 |
| `aipe-backend` | Gateway（接收 Agent 数据 + Observation 查询）|

---

## 四、WP012~WP018 路线图 + Blueprint 路径

| WP | 核心交付 | Blueprint 路径 |
|----|---------|---------------|
| WP012 | Observation 领域模型 + Repository + Pipeline 对接 | `M2-005WP/WP012-Observation-Engine-Blueprint.md` |
| WP013 | Relationship + Topology（关系管理 + 拓扑投影引擎）| `M2-005WP/WP013-Relationship-Topology-Blueprint.md` |
| WP014 | Timeline 运行时构建 + 统计特征 + 趋势检测 | `M2-005WP/WP014-Timeline-Engine-Blueprint.md` |
| WP015 | Evidence 推理引擎（异常检测 + 因果推理 + 可解释）| `M2-005WP/WP015-Evidence-Engine-Blueprint.md` |
| WP016 | Knowledge 沉淀（Verified Evidence → Knowledge + 版本管理）| `M2-005WP/WP016-Knowledge-Engine-Blueprint.md` |
| WP017 | Recommendation 推荐（Knowledge → Recommendation + 优先级）| `M2-005WP/WP017-Recommendation-Engine-Blueprint.md` |
| WP018 | Execution + Optimization（执行 + 效果评估 + 闭环 → Knowledge 更新）| `M2-005WP/WP018-Execution-Optimization-Blueprint.md` |

---

## 五、架构规范（不可违反）

### 5.1 DDD 分层

```
com.aipe.{domain}
├── domain/                ← 纯 POJO（禁止 Spring/MyBatis 注解）
├── application/           ← 事务编排（唯一事务 Owner）
├── infrastructure/        ← 持久化（PO + Mapper + RepositoryImpl + Converter）
└── api/                   ← REST（Controller + DTO + ExceptionHandler）
```

### 5.2 Package 命名（对齐 IM-001）

- **Root Package**: `com.aipe`（不是 `com.ai.performance`）
- **一级 Package（固定，禁止新增）**：`api` / `application` / `domain` / `repository` / `infrastructure` / `connector` / `ai` / `common` / `config` / `scheduler` / `event` / `security` / `integration`
- **禁止新增一级 Package**（需 RFC）

### 5.3 Repository 规则

| 规则 | 说明 |
|------|------|
| 接口位置 | Domain 层 |
| 实现位置 | Infrastructure 层 |
| **返回值** | **Domain 对象，禁止返回 PO/DO/Map/ResultSet** |
| 一对一 | 一个 Aggregate Root 只有一个 Repository |
| 事务 | **Repository 禁止开启事务** |

### 5.4 事务规则

- `@Transactional` **只在 Application Service**
- Domain / Repository / Controller **禁止事务**

### 5.5 依赖方向

```
API ↓ Application ↓ Domain ↓ Repository ↓ Infrastructure
```

**禁止反向依赖，禁止循环依赖。**

### 5.6 命名规则

| 类型 | 命名 |
|------|------|
| Entity | `Resource` / `Observation` / `Evidence` |
| 值对象 | `ResourceId` / `ResourceName`（final + 不可变）|
| Repository | `XxxRepository` |
| Service | `XxxApplicationService` |
| Controller | `XxxController` |
| DTO | `XxxRequest` / `XxxResponse` |

**禁止**：Model / Bean / Data / Temp / Manager / Util / Helper

### 5.7 核心法则（Frozen）

| 法则 | 内容 |
|------|------|
| Law-001 | Everything Is Resource |
| Law-002 | Observation Belongs to Resource |
| Domain Law-001 | One Concept, One Domain Object |
| Gateway Law-001 | Repository Returns Domain, Never PO |
| Gateway Law-002 | One Aggregate, One Repository |
| Persistence Law-001 | Right Data, Right Storage |
| Persistence Law-002 | ClickHouse Stores Facts, Never Knowledge |
| Persistence Law-004 | Timeline Is Computed, Never Stored |
| Architecture Law-004 | Topology Is A View |
| Architecture Law-005 | Relationship Is First-Class Citizen |
| Architecture Law-007 | Controller Is A Protocol Translator |
| AI Runtime Law-001 | AI Never Reads Database Directly |

**法则全文**：`docs/architecture/M2-AI-Domain-Foundation/M2-001-Architecture-Laws/Law-000.md`

---

## 六、技术栈（不可漂移）

| 组件 | 版本 |
|------|------|
| Java | 1.8 |
| Spring Boot | 2.7.18 |
| MyBatis Plus | 3.5.3.1 |
| Druid | 1.2.20 |
| ClickHouse JDBC | 0.4.6 |
| Jedis | 4.4.6 |
| Hutool | 5.8.22 |
| Lombok | 1.18.30 |
| MySQL | 5.7+ / 8.0 |
| ClickHouse | 21+ |

**新增任何依赖必须先征得用户同意。**

### 数据库连接

| 存储 | 地址 | 数据库 | 认证 |
|------|------|--------|------|
| MySQL | 124.223.20.245:3306 | aipe_metadata | root / astock_root |
| ClickHouse | 124.223.20.245:8123 | metric_observation | default / pamirs@123 |

### MySQL resource 表（已迁移）

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

**关键**：`@TableId(value="resource_id")`，`@Version` 映射 `resource_status`。

---

## 七、编码质量标准

### 开发约束

1. Blueprint 优先于代码
2. 不允许降低设计等级
3. 不允许 Mock 代替真实采集
4. 不允许空实现
5. 不允许为了编译删除功能
6. 每个 WP 必须完成验收标准
7. 每个 WP 完成必须提交状态报告

### 代码质量

- 真实实现：禁止 TODO / Mock / 空方法 / return null / 伪代码
- SLF4J 日志：禁止 System.out
- Lombok：@Data / @Builder / @Slf4j
- 线程安全：ConcurrentHashMap / CopyOnWriteArrayList
- 资源关闭：try-with-resources
- Java 8 兼容：不用 Map.of 等 9+ 特性

### 验收标准

- `mvn clean install` BUILD SUCCESS
- Spring Boot 启动：无 Bean 创建失败
- 无循环依赖
- 无重要 WARN
- 真实 MySQL/ClickHouse 集成（不允许 Mock）

---

## 八、用户偏好

| 偏好 | 说明 |
|------|------|
| **"先读项目再写代码"** | 给路径就通读，不假设 |
| **"对齐实际表结构"** | IM 文档列名以实际 DB 为准 |
| **"真实集成测试"** | 不允许 Mock |
| **"偏好对话确认"** | 关键决策先问再动 |
| **"质量保证"** | 每个 WP 必须 6+ 真实集成测试场景 PASS |

---

## 九、项目文件结构（Clean 后参考）

```
AIPerformanceEngineer/
├── pom.xml
├── README.md
├── docker-compose.yml
├── start.sh / stop.sh / restart.sh
├── docs/architecture/M2-AI-Domain-Foundation/
│   ├── M2-000-governance/AIPE-Constitution-v1.0.md
│   ├── M2-001-Architecture-Laws/Law-000.md
│   ├── M2-003-world-model/M2-006~M2-013
│   ├── M2-004-implementation-mapping/IM-000~IM-011
│   ├── M2-005WP/WP012~WP018 Blueprint + 本文档
│   ├── Blueprint Standard/Blueprint-Standard-v1.0.md
│   └── Gate Standard/Gate-Standard-v1.0.md
├── aipe-agent/           ← M1 ✅
├── aipe-backend/         ← M1 ✅
├── aipe-common/          ← M1 ✅
├── aipe-config-manager/  ← M1 ✅
├── aipe-connectors/      ← M1 ✅
├── aipe-observation/     ← M1 ✅ + WP012 升级
├── aipe-resource/        ← WP011 ✅（32 文件）
├── aipe-storage/         ← M1 ✅
├── aipe-relationship/    ← WP013
├── aipe-timeline/        ← WP014
├── aipe-evidence/        ← WP015
├── aipe-knowledge/       ← WP016
├── aipe-recommendation/  ← WP017
└── aipe-execution/       ← WP018
```

---

## 十、关键设计决策（已确认）

| 决策 | 确认结果 |
|------|---------|
| WP011 代码放哪个模块 | 新建 `aipe-resource` 模块 |
| Observation→Resource 关联 | 放到 WP012 |
| 测试数据库 | 真实 MySQL（不是 H2）|
| 包结构 | 严格 DDD（domain/application/infrastructure）|
| Repository 返回 | Domain（不是 PO）|
| 版本管理 | MyBatis Plus @Version 自动管理 |
| 终极目标 | B 端通用产品（先自己公司验证）|

---

## 十一、风险 & 已知问题

| 问题 | 解决方式 |
|------|---------|
| MySQL 5.x 不支持 ADD COLUMN IF NOT EXISTS | SET @col_exists + PREPARE/EXECUTE |
| Spring Populator 不支持 DELIMITER | ALTER 列用 Spring 脚本，索引用 Java |
| MyBatis Plus 乐观锁需要 OptimisticLockerInnerInterceptor | 已加到 MyBatisPlusConfig |
| domain 手动 version++ 冲突 | 删除 domain 里 version++ |
| IM 文档列名 vs 实际 DB | 以实际 DB 为准 |
| Spring Boot 启动慢（~45s）| macOS DNS：/etc/hosts 加 localhost |
| 中文 URL 编码 | curl 参数 URL 编码 |

---

*此文档是 WP012~WP018 推进的"宪法级上下文"。Clean + 发给 AI + 说"推进 WP12"即可恢复。*