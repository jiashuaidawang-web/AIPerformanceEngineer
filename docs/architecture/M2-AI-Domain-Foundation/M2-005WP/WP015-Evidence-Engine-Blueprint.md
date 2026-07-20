# AI Performance Engineer

# WP015 Evidence Engine Blueprint

Version: v1.0

Status: Draft

Milestone: M2 – Implementation

Priority: P0

DependsOn:

WP014 Timeline Engine

WP013 Relationship + Topology

IM-003 Persistence Mapping

M2-011 Evidence Model

RequiredBy:

WP016 Knowledge Engine

WP018 Execution + Optimization

EstimatedJavaFiles: 25

EstimatedWorkload: 5 Days

Blueprint Template: Blueprint Standard v1.0

---

## 1. Goal

建立 Evidence Engine（证据引擎），实现 AI 基于 Timeline + Topology + Observation 的可解释推理，生成带有置信度和推理链的 Evidence。Evidence 是 AI 对 历史异常 的解释（因果推理 + 根因定位 + 影响分析），具备可追溯、可解释、可验证三大特征。

核心解决问题：Observation 是"发生了什么"，Evidence 是"为什么发生 + 可信度多少 + 依据是什么"。WP015 让 AI 推理过程可解释、可追溯。

Before：告警 = 阈值触发（"CPU > 90%"），无上下文、无因果、无解释

After：Evidence = 可解释证据链（"Redis 连接池耗尽 → Order Service TPS 降低 → 置信度 97%"）

核心原则：Evidence Before Conclusion（AI Principle-001）— AI 必须先生成 Evidence 才能输出 Recommendation；Knowledge 必须来源于 Verified Evidence（Domain Law-001）。

Scope：

MVP 支持：Evidence 领域模型 + EvidenceEngine（异常检测 + 因果推理 + 置信度计算）+ EvidenceRepository（MySQL）+ Verification（对齐 IM-003）+ Evidence 查询 API + Topology 对齐 图遍历 + Observation 对齐 ClickHouse

不包含：Knowledge Engine（WP016）；自动 AI 推理（规则推理 + LLM 推理，MVP 仅用规则）；Recommendation Engine（WP016）

---

## 2. Acceptance Criteria

    2.1 Functional Acceptance

必须支持：Evidence 领域模型（含 evidenceId / type / title / description / rootResourceId / observationIds / confidence / reasoningSteps / status）

EvidenceEngine.generate(TimelineQuery) → Evidence（基于规则：连续超阈值 → Dependency Chain 检测 → 置信度计算）

EvidenceEngine.generateFromTopology(ResourceId) → Evidence（Topology 遍历 + 异常传播）

Evidence.calculateConfidence() → double（置信度 0~100）

Evidence.explain() → String（推理过程文字化，回答"为什么得到这个结论"）

EvidenceRepository（MySQL，对齐 IM-003 Evidence 表，save / findById / findByRootResource / findByStatus）

Evidence 查询 API + 查询 /api/v1/evidences?resource_id=xxx / /api/v1/evidences/{id}

    2.2 Technical Acceptance

必须满足：EvidenceRepository 接口在 Domain / 实现在 Infrastructure / 返回 Domain

Evidence 落 MySQL（对齐 IM-003 / IM-004：Evidence 是 元数据，不是 Fact）

Evidence 可引用 Observation / Timeline / Relationship（不可变 ID 列表）

推理步骤（reasoningSteps）持久化字段（JSON 序列化为 String）

Evidence 不可变（Immutable）- 一旦生成不修改，新的推理生成新的 Evidence

Confidence 范围 0~100，默认 50（不确定）

    2.3 Integration Acceptance

完整链路：

Timeline + Topology → EvidenceEngine.generate() → Evidence（置信度 + 推理路径）→ MySQL → API 返回

---

## 3. Package List

com.aipe.evidence

├── domain/                ← Evidence + EvidenceType + EvidenceStatus + Repository + EvidenceBuilder

├── application/           ← EvidenceEngine + ConfidenceCalculator + ReasoningChainProducer

├── infrastructure/        ← EvidenceRepositoryImpl + EvidencePO + Mapper + Converter

└── api/                   ← EvidenceController + DTO + ExceptionHandler

Package Responsibility

Package	职责

domain	Evidence 聚合根 + 枚举（EvidenceType/Status）+ Repository 接口 + EvidenceBuilder

application	EvidenceEngine（核心引擎）+ ConfidenceCalculator（置信度计算）+ ReasoningChainProducer（推理链生成）

infrastructure	EvidenceRepositoryImpl（MySQL）+ EvidencePO + Mapper + Converter

api	EvidenceController + DTO + ExceptionHandler

---

## 4. Class List

    4.1 Evidence（Domain 聚合根）

Package: domain

职责：AI 推理结果（可解释证据链），不可变，引用 Observation

字段：

字段	类型	说明

evidenceId	EvidenceId	全局唯一标识（值对象）

evidenceType	EvidenceType	Performance / Dependency / Deployment / Business / AI / Composite

title	String	证据标题

description	String	AI 解释（自然语言）

rootResourceId	ResourceId	根 Resource

observationIds	List<String>	引用的 Observation ID

relationshipIds	List<String>	引用的 Relationship ID

timelineId	String	引用的 Timeline ID

confidence	double	AI 可信度 0~100

reasoningSteps	List<ReasoningStep>	推理步骤（JSON 序列化）

status	EvidenceStatus	NEW / VERIFIED / REJECTED

createdAt	 LocalDateTime	创建时间

version	int	Schema 版本

    4.2 EvidenceEngine（Application 核心）

职责：Evidence 推理引擎：输入 时间范围 + Resource → 超阈值检测 + Topology 遍历 + 置信度计算 + Evidence

核心方法：

- generateFromAnomaly(TimelineQuery): 异常 → Evidence

- generateImpactEvidence(ResourceId, Timepoint): 基于 Topology 传播分析 → Evidence

- calculateConfidence(Evidence): 置信度（超阈值程度 × 时间窗口 × 关联指标数）

- explain(Evidence): 推理过程文字化

    4.3 ConfidenceCalculator（Application）

职责：置信度计算：基础置信度 × 指标数 × 持续时间 × Topology 传播 + 人工修正

    4.4 EvidenceRepositoryImpl（Infrastructure）

职责：MySQL 实现，返回 Domain

    4.5 EvidenceRepository（Domain 接口）

职责：save / findById / findByRootResource / findByStatus / query

---

## 5. Method List

Evidence（Domain 业务方法）

// 校验 Evidence 是否合法（rootResourceId + title + confidence + reasoningSteps 必填）

void validate()

// 是否高置信度（confidence >= 80）

boolean isHighConfidence()

// 是否已验证（通过 Verification）

boolean isVerified()

// 是否引用 Observation

boolean referencesObservations()

// 产生自然语言解释

String explain()

EvidenceEngine（Application 核心）

// 从 Timeline 异常 生成 Evidence（超阈值检测 → 时序窗口判定 → 生成 Evidence）

Evidence generateFromAnomaly(TimelineQuery query)

// 从 Topology 传播 生成 Evidence（单 Resource 异常 → 图遍历 → 影响范围 → 生成 Evidence）

Evidence generateImpactEvidence(ResourceId rootResourceId, long timestamp)

// 生成 Composite Evidence（多个 Evidence 合并）

Evidence generateComposite(List<EvidenceId> evidenceIds)

// 计算置信度（基础 × 时间 × 关联指标数）

 double calculateConfidence(Evidence evidence)

// 解释 Evidence（返回推理过程自然语言描述）

String explain(Evidence evidence)

EvidenceRepositoryImpl（Infrastructure）

Evidence save(Evidence evidence)

Optional<Evidence> findById(EvidenceId id)

List<Evidence> findByRootResource(ResourceId resourceId)List<Evidence> findByStatus(EvidenceStatus status)

List<Evidence> query(EvidenceQuery query)
    5.1 物理文件清单

aipe-evidence/

├── pom.xml

├── src/main/java/com/aipe/evidence/

│   ├── EvidenceApplication.java

│   ├── domain/

│   │   ├── Evidence.java                         ← 聚合根

│   │   ├── EvidenceId.java                       ← 值对象

│   │   ├── EvidenceType.java                     ← 枚举 6 类（Performance/Dependency/Deployment/Business/AI/Composite）

│   │   ├── EvidenceStatus.java                   ← 枚举（NEW/VERIFIED/REJECTED）

│   │   ├── ReasoningStep.java                    ← 推理步骤值对象（step / action / result / confidence）

│   │   ├── EvidenceRepository.java               ← 接口

│   │   ├── EvidenceBuilder.java                  ← 构造器

│   │   └── EvidenceSpecification.java            ← 校验规格

│   ├── application/

│   │   ├── EvidenceEngine.java                   ← 核心引擎

│   │   ├── ConfidenceCalculator.java            ← 置信度计算

│   │   ├── ReasoningChainProducer.java           ← 推理链生成

│   │   └── RuleBasedDetector.java               ← MVP 规则推理（超阈值 + 持续窗口 + 关联指标）

│   ├── infrastructure/

│   │   ├── EvidenceRepositoryImpl.java           ← MySQL 实现

│   │   ├── EvidencePO.java                       ← 持久化对象

│   │   ├── EvidenceMapper.java                  ← MyBatis Mapper（MySQL）

│   │   └── EvidenceConverter.java                ← PO ↔ Domain

│   └── api/
│       ├── EvidenceController.java

│       ├── EvidenceDtoMapper.java

│       ├── dto/

│       │   ├── EvidenceRequest.java

│       │   ├── EvidenceResponse.java

│       │   └── EvidenceQueryRequest.java

│       └── exception/

│           └── EvidenceExceptionHandler.java

---

## 6. MySQL Schema（对齐 IM-003）

```sql
CREATE TABLE IF NOT EXISTS evidence (
    id              VARCHAR(64) PRIMARY KEY COMMENT '业务主键 UUID',
    evidence_type   VARCHAR(32) NOT NULL COMMENT 'Performance/Dependency/...',
    title           VARCHAR(256) NOT NULL COMMENT '证据标题',
    description     TEXT COMMENT 'AI 解释（自然语言）',
    root_resource_id VARCHAR(64) NOT NULL COMMENT '根 Resource',
    observation_ids JSON COMMENT '引用的 Observation ID',
    relationship_ids JSON COMMENT '引用的 Relationship ID',
    timeline_id     VARCHAR(64) COMMENT '引用的 Timeline ID',
    confidence      DOUBLE NOT NULL DEFAULT 50.0 COMMENT 'AI 可信度 0~100',
    reasoning_steps JSON COMMENT '推理步骤',
    status          VARCHAR(16) NOT NULL DEFAULT 'NEW' COMMENT 'NEW/VERIFIED/REJECTED',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version         INT NOT NULL DEFAULT 1,
    INDEX idx_root_resource (root_resource_id),
    INDEX idx_type (evidence_type),
    INDEX idx_status (status),
    INDEX idx_confidence (confidence)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

对齐检查

IM-003 规范	本 WP 实现

evidence_id PK	✅ id VARCHAR(64) PK

evidence_type	✅ evidence_type

title	✅ title

root_resource_id	✅ root_resource_id（NOT NULL）

confidence	✅ confidence 0~100

timeline_id	✅ timeline_id

reasoning_steps	✅ reasoning_steps（JSON）

status（NEW/VERIFIED/REJECTED）✅ status 枚举

version 乐观锁	✅ version）

落 MySQL（不是 ClickHouse）✅ 元数据 → MySQL

禁止 落 ClickHouse	✅ 禁止

---

## 7. API 设计

方法	路径	说明	入参

POST	/api/v1/evidences/generate	触发 Evidence 生成	ResourceIdQuery（resource_id + time_range）

GET	/api/v1/evidences/{id}	查询 Evidence	id

GET	/api/v1/evidences?resource_id=xxx&status=VERIFIED	按 Resource / 状态查询	resource_id / status / type

GET	/api/v1/evidences?resource_id=xxx&min_confidence=80	按最低置信度查询	resource_id / min_confidence

POST	/api/v1/evidences/{id}/verify	验证 Evidence（通过 → VERIFIED；失败 → REJECTED）	id + decision + verifier

GET	/api/v1/evidences/{id}/explain	解释 Evidence（返回推理过程自然语言）	id

---
## 8. Sequence Diagram

异常 Timeline → Evidence 生成

user EvidenceController EvidenceEngine TimelineService ObservationRepository ConfidenceCalculator EvidenceRepository MySQL

---
## 9. Implementation Constraints

    9.1 Must Implement

必须实现：Evidence 引用 Observation / Timeline / Relationship（对齐 M2-011 Schema）

必须实现：EvidenceRepository 接口在 Domain / 实现在 Infrastructure / 返回 Domain

必须实现：confidence 计算 0~100（默认 50 不确定）

必须实现：Evidence 不可变（Immutable）- 生成后不改，新的推理新生成 Evidence

必须实现：Evidence 落 MySQL（对齐 IM-003，不是 ClickHouse）

必须实现：explain() 返回自然语言解释（回答"为什么"）

必须实现：ReasoningStep 持久化为 JSON

    9.2 Forbidden

禁止：Evidence 落 ClickHouse（Persistence Law-002 / Architecture Law-006）

禁止：Evidence 无 Observation 引用（必须引用事实 - Law-002）

禁止：Confidence 固定 100（必须动态计算）

禁止：Evidence 修改（Immutable）

禁止：直接输出结论（必须先生成 Evidence - AI Principle-001）

    9.3 Engineering Rules

必须：Evidence 生成时 validate()（rootResourceId + title + confidence 必填）

必须：API 返回 EvidenceResponse（含 evidenceId + confidence + reasoningSteps）

必须：统一返回 ApiResponse

必须：集成测试必须覆盖 异常检测 + 置信度计算 + explain + Illegal Evidence 拒绝

---
## 10. Test & Verification

    10.1 Build

mvn clean package -pl aipe-evidence -DskipTests

    10.2 Test Scenario

异常 Timeline 触发 Evidence 生成（CPU 超阈值 持续 5 分钟 → 生成 Performance Evidence）

Topology 传播 Evidence 生成（单 Resource 异常 → 图遍历 → 影响分析 Evidence）

Confidence 计算（基础 × 指标数 × 持续时间，手动计算对比）

explain 返回非空自然语言解释

Illegal Evidence（无 Observation 引用）→ 拒绝生成

Evidence 验证（通过 → VERIFIED；失败 → REJECTED）

    10.3 Verification

检查：Evidence 落 MySQL（不是 ClickHouse）

检查：confidence 自动计算（不是硬编码）

检查：explain() 返回推理过程自然语言

检查：Evidence 生成时引用 Observation（observationIds 非空）

检查：Evidence 不可变（更新后 → 新 version 或新 Evidence）

    10.4 Expected Result

返回 Evidence（confidence + reasoningSteps + observationIds）

返回 explain() 自然语言解释

符合 M2-011 6 类 Evidence

符合 AI Principle-001：Evidence Before Conclusion

    10.5 Troubleshooting

Evidence 无法生成：检查 Timeline 是否正常、Observation 是否有数据

Confidence 偏差大：检查超阈值程度 × 时间窗口权重配置

Explain 不自然：检查 ReasoningStep 模板 + 填充时序

Evidence 验证后 无状态变更：检查 evidenceRepository.update + version 冲突（乐观锁）

---

## 11. Rowboat Coding Rules

Rowboat 必须遵守：

不得新增一级 Package（com.aipe.evidence）

不得改变 Package 名称

Evidence 必须引用 Observation（对齐 Law-002）

Evidence 落 MySQL（对齐 IM-003）

Evidence 不可变（Immutable）- 生成后不修改

Confidence 动态计算（不是硬编码）

Repository 接口在 Domain / 实现在 Infrastructure / 返回 Domain所有新增类必须放入规定 Package

任何违反 Persistence Law / Gateway Law / Architecture Law 的代码必须拒绝提交

Status: Draft