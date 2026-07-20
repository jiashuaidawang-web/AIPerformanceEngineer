# AI Performance Engineer

# WP016 Knowledge Engine Blueprint

Version: v1.0

Status: Draft

Milestone: M2 – Implementation

Priority: P0

DependsOn:

WP015 Evidence Engine

IM-003 Persistence Mapping

M2-012 Knowledge Model

RequiredBy:

WP017 Recommendation Engine

WP018 Execution + Optimization（Knowledge 引用）

EstimatedJavaFile: 20

EstimatedWorkload: 3 Days

Blueprint Template: Blueprint Standard v1.0

---

## 1. Goal

建立 Knowledge Engine（知识引擎），把 Verified Evidence（经过验证的证据）沉淀为可复用、可版本化、可成长的 Knowledge。Knowledge 是 AI World 唯一允许长期保存的 AI 经验，来源必须是 Verified Evidence（Domain Law-001）。

核心解决问题：Evidence 是 单次推理结果，Knowledge 是 复用经验（类似操作手册）。WP016 解决"经验沉淀 + 版本管理 + 可复用引用"。

Before：每次推理都要从零开始，历史经验 不能复用

After：Verified Evidence → Knowledge（带 适用条件 + 推荐方案 + 历史），未来推理 可直接引用

核心原则：Knowledge Is Verified Evidence（Domain Law-001）— 永远不是 AI Memory，不是 Rule，不是 Vector；Reality Before Memory— AI 相信自己 不如相信现实，Evidence 是 AI 假设，Verification 是现实验证。

Scope：

MVP 支持：Knowledge 领域模型（含 knowledgeId / title / type / evidenceId（来源）/ verificationId / confidence / applicableConditions / recommendation / successRate + 6 种分类：Bottleneck/Dependency/Deployment/Business/Optimization/AI）+ KnowledgeRepository（MySQL 版本 管理）+ KnowledgeEngine.buildKnowledge(Verified Evidence)→ Knowledge + Knowledge.versioning() 升级版本 + Knowledge.recommend() 引用 + 版本管理 API + 跨环境 / 跨集群 共享 + 版本 API + 跨Resource 类型复用

不包含：向量库（ClickHouse 不是 Vector DB）；LLM Memory（禁止 LLM Memory 直接进入 Knowledge - Domain Law-001）；自动 AI 学习（需要 Reality 验证 - M2-012）；Knowledge Graph（未来版本 考虑）

---

## 2. Acceptance Criteria

    2.1 Functional Acceptance

必须支持：

Knowledge 领域模型（对齐 M2-012 Schema：knowledgeId / title / type / evidenceId / verificationId / confidence / applicableConditions / recommendation / successRate + 6 种分类：Bottleneck/Dependency/Deployment/Business/Optimization/AI）

KnowledgeEngine.buildKnowledge(Verified Evidence) → Knowledge（来源 Verified Evidence，提供默认 适用条件 和 推荐方案）

Knowledge.versioning() → Semantic Version（知识升级：Minor 经验补充 / Major 重大修正），保留所有历史版本

Knowledge.recommend(ResourceId) → 推荐方案引用（Knowledge 应用于新 Resource）

KnowledgeRepository（MySQL save / findById / findByType / findLatest / findByResource / version history + save / findVersions）

Knowledge 落 MySQL（对齐 IM-003）

Knowledge 版本升级 API + 查询 /api/v1/knowledge?resource_id=xxx / /api/v1/knowledge/{id} / /api/v1/knowledge/{id}/versions

    2.2 Technical Acceptance

必须满足：

KnowledgeRepository 接口在 Domain / 实现在 Infrastructure / 返回 Domain

Knowledge 落 MySQL（对齐 IM-003）

Knowledge 版本存储（对齐 IM-003 version 字段，每条 Knowledge version 持久化）

不可变（Immutable）- 版本升级 = 新 version（不是更新原记录，追加新版本）

Knowledge 版本 history 查询 /api/v1/knowledge/{id}/versions

    2.3 Integration Acceptance

完整链路：

Verified Evidence（WP015） → KnowledgeEngine.buildKnowledge() → Knowledge (v1) → MySQL → API → 未来 Resource 引用推荐

---

## 3. Package List

com.aipe.knowledge

├── domain/                ← Knowledge + KnowledgeType + KnowledgeVersion + Repository + KnowledgePattern

├── application/           ← KnowledgeEngine + KnowledgeVerifier + KnowledgeRecommender + KnowledgeVersioning

├── infrastructure/        ← KnowledgeRepositoryImpl + KnowledgePO + Mapper + Converter

└── api/                   ← KnowledgeController + DTO + ExceptionHandler

Package Responsibility

Package	职责

domain	Knowledge 聚合根 + 枚举（KnowledgeType 6 类）+ KnowledgeVersion + Repository 接口 + KnowledgeBuilder

application	KnowledgeEngine（构建器）+ KnowledgeVerifier（验证器）+ KnowledgeRecommender（推荐引用）+ KnowledgeVersioning（版本管理）

infrastructure	KnowledgeRepositoryImpl（MySQL）+ KnowledgePO + Mapper + Converter

api	KnowledgeController + DTO + ExceptionHandler

---

## 4. Class List

    4.1 Knowledge（Domain 聚合根）

Package: domain

职责：经过 Verified Evidence 沉淀的可复用经验（带版本 + 适用条件 + 推荐方案 + 历史成功率），不可变（Immutable）

字段：

字段	类型	说明

knowledgeId	KnowledgeId	全局唯一标识（值对象）

title	String	知识标题

description	String	知识描述

knowledgeType	KnowledgeType	Bottleneck / Dependency / Deployment / Business / Optimization / AI

evidenceId	EvidenceId	来源 Evidence（必填 - Domain Law-001）

verificationId	VerificationId	验证记录

confidence	double	最终可信度 0~100

applicableConditions	Map<String, String>	适用条件（Resource 类型 / 指标名 / 环境 / …）

recommendation	Recommendation	推荐方案（操作 内容 + 预期效果）

successRate	double	历史成功率 0~100

createdAt	 LocalDateTime	创建时间

version	int	Schema 版本

    4.2 KnowledgeEngine（Application 核心）

职责：Knowledge 构建器：输入 Verified Evidence → 提供默认 适用条件 和 推荐方案 → 生成 Knowledge(v1)

核心方法：

- buildKnowledge(VerifiedEvidence) → Knowledge

- verifyKnowledge(KnowledgeId, new Evidence) → 升级 Knowledge + 新 version- recommendForResource(ResourceId, KnowledgeId) → 推荐方案引用

    4.3 KnowledgeVersioning（Application）

职责：版本管理 / 新增 version（追加新记录 + 旧记录 保留）

核心方法：

- createNewVersion(KnowledgeId, ChangeSet) → Knowledge(version+1)

- listVersions(KnowledgeId) → List<Knowledge>

    4.4 KnowledgeRepositoryImpl（Infrastructure）

职责：MySQL 实现 / 版本查询 / save / findById / findAllVersions / findByType / findByEvidenceId / findLatest

    4.5 KnowledgeRepository（Domain 接口）

职责：save / findById / findAllVersions / findByType / findByEvidenceId / findLatest / query

---

## 5. Method List

Knowledge（Domain 业务方法）

// 校验 Knowledge 是否合法（title + evidenceId + confidence + recommendation 必填）

void validate()

// 是否 高价值 Knowledge（confidence >= 80 + successRate >= 70）

boolean isHighValue()

// 是否可应用于 Resource（检查 applicableConditions + Resource 类型）

boolean isApplicableTo(Resource resource)

// 升级版本（返回 新 version Knowledge）

Knowledge upgrade(ChangeSet changeSet)

KnowledgeEngine（Application 核心）

// 从 Verified Evidence 构建 Knowledge（自动提取 适用条件 和 推荐方案）

Knowledge buildKnowledge(VerifiedEvidence verifiedEvidence)

// 验证 并升级 Knowledge（新 Evidence → 新 version）Knowledge verifyAndUpgrade(KnowledgeId id, Evidence newEvidence)

// 推荐 Knowledge 应用于 Resource（检查 适用条件 + 返回 推荐方案）Recommendation recommendForResource(ResourceId resourceId, KnowledgeId knowledgeId)

KnowledgeRepositoryImpl（Infrastructure）

Knowledge save(Knowledge knowledge)

Optional<Knowledge> findById(KnowledgeId id)

List<Knowledge> findAllVersions(KnowledgeId id)List<Knowledge> findByType(KnowledgeType type)

List<Knowledge> findByEvidenceId(EvidenceId evidenceId)

Optional<Knowledge> findLatest(KnowledgeId id)

List<Knowledge> query(KnowledgeQuery query)
    5.1 物理文件清单

aipe-knowledge/

├── pom.xml

├── src/main/java/com/aipe/knowledge/

│   ├── KnowledgeApplication.java

│   ├── domain/

│   │   ├── Knowledge.java                       ← 聚合根

│   │   ├── KnowledgeId.java                     ← 值对象

│   │   ├── KnowledgeType.java                  ← 枚举 6 类（Bottleneck/Dependency/Deployment/Business/Optimization/AI）

│   │   ├── KnowledgeVersion.java               ← 版本值对象

│   │   ├── Recommendation.java                 ← 推荐方案值对象

│   │   ├── KnowledgeRepository.java             ← 接口

│   │   ├── KnowledgeBuilder.java               ← 构造器

│   │   └── KnowledgeSpecification.java          ← 校验规格

│   ├── application/

│   │   ├── KnowledgeEngine.java                 ← 核心引擎

│   │   ├── KnowledgeVerifier.java              ← 验证器

│   │   ├── KnowledgeRecommender.java           ← 推荐引用器

│   │   └── KnowledgeVersioning.java            ← 版本管理器

│   ├── infrastructure/

│   │   ├── KnowledgeRepositoryImpl.java         ← MySQL 实现

│   │   ├── KnowledgePO.java                     ← 持久化对象

│   │   ├── KnowledgeMapper.java                ← MyBatis Mapper（MySQL）

│   │   └── KnowledgeConverter.java              ← PO ↔ Domain

│   └── api/
│       ├── KnowledgeController.java

│       ├── KnowledgeDtoMapper.java

│       ├── dto/

│       │   ├── KnowledgeRequest.java

│       │   ├── KnowledgeResponse.java

│       │   └── KnowledgeVersionResponse.java

│       └── exception/

│           └── KnowledgeExceptionHandler.java

---

## 6. MySQL Schema（对齐 IM-003）

```sql
CREATE TABLE IF NOT EXISTS knowledge (
    id                  VARCHAR(64) PRIMARY KEY COMMENT '业务主键 UUID',
    title               VARCHAR(256) NOT NULL COMMENT '知识标题',
    description         TEXT COMMENT '知识描述',
    knowledge_type      VARCHAR(32) NOT NULL COMMENT 'Bottleneck/Dependency/...',
    evidence_id         VARCHAR(64) NOT NULL COMMENT '来源 Evidence（Domain Law-001）',
    verification_id     VARCHAR(64) COMMENT '验证记录',
    confidence          DOUBLE NOT NULL DEFAULT 50.0 COMMENT '最终可信度 0~100',
    applicable_conditions JSON COMMENT '适用条件',
    recommendation      JSON COMMENT '推荐方案',
    success_rate        DOUBLE NOT NULL DEFAULT 0.0 COMMENT '历史成功率 0~100',
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version             INT NOT NULL DEFAULT 1,
    INDEX idx_evidence (evidence_id),
    INDEX idx_type (knowledge_type),
    INDEX idx_confidence (confidence)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

对齐检查

IM-003 规范	本 WP 实现

knowledge_id PK	✅ id VARCHAR(64) PK

title	✅ title

knowledge_type	✅ knowledge_type 6 类枚举

evidence_id	✅ evidence_id（NOT NULL - Domain Law-001）verification_id	✅ verification_id

confidence	✅ confidence 0~100

recommendation	✅ recommendation JSON

success_rate	✅ success_rate 0~100

version	✅ version（每新版本 新记录）

落 MySQL	✅ 对齐 IM-003

禁止 落 ClickHouse	✅ 禁止

---

## 7. API 设计

方法	路径	说明	入参

POST	/api/v1/knowledge	从 Verified Evidence 	build Knowledge	EvidenceDecisionQuery（evidence_id + verified）

GET	/api/v1/knowledge/{id}	查询 Knowledge（含 版本 + 适用条件）	id

GET	/api/v1/knowledge?type=Bottleneck&min_confidence=80	按 类型 / 最低置信度 查询	type / min_confidence

GET	/api/v1/knowledge/{id}/versions	版本 历史	id

POST	/api/v1/knowledge/{id}/upgrade	升级 Knowledge（新 ChangeSet）	id + change_set

POST	/api/v1/knowledge/{id}/recommend?resource_id=xxx	推荐 Knowledge 应用于 Resource	id + resource_id

---
## 8. Sequence Diagram

Verified Evidence → Knowledge 构建

user KnowledgeController KnowledgeEngine EvidenceRepository KnowledgeRepository MySQL

---
## 9. Implementation Constraints

    9.1 Must Implement

必须实现：Knowledge 来源 Verified Evidence（对齐 Domain Law-001：evidenceId 必填 + status=VERIFIED）

必须实现：KnowledgeRepository 接口在 Domain / 实现在 Infrastructure / 返回 Domain

必须实现：Knowledge 落 MySQL（对齐 IM-003）

必须实现：Knowledge 不可变（升级 = 新 version 新记录）

必须实现：Knowledge 版本 history 查询

必须实现：Knowledge 适用于 Resource（applicableConditions + isApplicableTo）

    9.2 Forbidden

禁止：Knowledge 来源于 Observation（必须经过 Evidence + Verification - Domain Law-001）

禁止：Knowledge 无 Evidence（必须有来源）

禁止：Knowledge 改原记录（升级 = 新 version）

禁止：LLM Memory 直接进入 Knowledge（对齐 M2-012）

禁止：Knowledge 落 ClickHouse（Persistence Law-002）

    9.3 Engineering Rules

必须：Knowledge 构建时 validate()（title + evidenceId + confidence 必填）

必须：API 返回 KnowledgeResponse（含 knowledgeId + version + applicableConditions + recommendation）

必须：统一返回 ApiResponse

必须：集成测试必须覆盖 构建 + 版本升级 + 推荐 + Illegal Knowledge（无 Evidence）拒绝

---
## 10. Test & Verification

    10.1 Build

mvn clean package -pl aipe-knowledge -DskipTests

    10.2 Test Scenario

Verified Evidence → build Knowledge（knowledgeId + version 1 + evidenceId 引用）

Knowledge 升级（新 ChangeSet → 新 version 2 + 旧 version 1 保留）

Knowledge 推荐应用于 Resource（applicableConditions 匹配 → 返回 recommendation）

Knowledge 版本 history 查询（返回 [v1, v2]）

Illegal Knowledge（无 Evidence）→ 拒绝构建

跨 Resource 类型复用（Resource 类型匹配 + 适用条件 match → 推荐）

    10.3 Verification

检查：Knowledge 落 MySQL（不是 ClickHouse）

检查：Knowledge 原记录不受影响（升级 = 新记录）

检查：evidenceId 引用 合法 Evidence

检查：recommendation 可被 Resource 引用（isApplicableTo 返回 true）

检查：知识版本 history 完整（2 个 version）

    10.4 Expected Result

返回 Knowledge（version 1 + evidenceId + recommendation）

返回 KnowledgeHistory（version 1 + version 2）

符合 M2-012：Knowledge 来源于 Verified Evidence

符合 Domain Law-001：Knowledge Is Verified Evidence

    10.5 Troubleshooting

Knowledge 无法构建：检查 Evidence 状态（必须 VERIFIED）+ evidenceId 引用合法

Knowledge 升级失败：检查 version 冲突（乐观锁 +1）

Knowledge 推荐不匹配：检查 applicableConditions ≠ Resource 类型 + 指标名

---
## 11. Rowboat Coding Rules

Rowboat 必须遵守：

不得新增一级 Package（com.aipe.knowledge）

不得改变 Package 名称

Knowledge 必须来源 Verified Evidence（对齐 Domain Law-001）

Knowledge 落 MySQL（对齐 IM-003）

Knowledge 不可变（升级 = 新 version 新记录）

Repository 接口在 Domain / 实现在 Infrastructure / 返回 Domain

所有新增类必须放入规定 Package

任何违反 Persistence Law / Gateway Law / Architecture Law 的代码必须拒绝提交

Status: Draft