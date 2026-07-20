IM-000 Implementation Overview
Version: 1.0
Status: Frozen
Document Level: Supreme Mapping
Authority: Highest
Applies To: All Implementation Mapping Documents, Java Code, Database, API, AI Engine, All Contributors, All AI Assistants

1. Purpose
   本文档是IM层的全局总纲，定义所有已冻结的世界模型对象在六个工程域（Java代码、MySQL、ClickHouse、Graph、REST API、AI Engine）的整体映射关系，明确每个对象的工程载体身份、跨域调用链路、映射优先级，是所有IM-XXX单项映射文档的最高参照，也是所有工程落地的最高指引。

2. Input Specification
   本规范引用以下已冻结文档，冲突时以对应上级文档为准：

AIPE-Constitution v1.0（项目最高法律）
AI Performance Engineer Manifesto v1.0（项目宣言）
M2-000 AI World Model Specification（总体世界模型）
M2-005 AI World Vocabulary（术语规范）
M2-006 ~ M2-013 所有已冻结的世界模型Specification
M1 所有已冻结的工程规范
3. Core Mapping Overview
   3.1 世界模型→工程域全局映射表
   世界模型对象	Java载体	MySQL载体	ClickHouse载体	Graph载体	REST API载体	AI Engine载体
   Observation	ObservationEntity / ObservationDTO	不存储，仅做缓存	observation_fact 表	作为Resource节点的属性存在	/api/v1/observations	作为推理的原始输入
   Resource	ResourceEntity / ResourceDTO	resource 表	不存储	作为图节点存在	/api/v1/resources	作为推理的目标对象
   Relationship	RelationshipEntity / RelationshipDTO	relationship 表	不存储	作为图边存在	/api/v1/relationships	作为拓扑推理的依据
   Topology	TopologyVO	不存储	不存储	动态生成不持久化	/api/v1/topology	作为全局优化的推理输入
   Timeline	TimelineVO / TimelineDTO	不存储	timeline_agg 表	作为Resource节点的时间线属性	/api/v1/timeline	作为趋势推理的依据
   Evidence	EvidenceEntity / EvidenceDTO	evidence 表	evidence_log 表	作为Evidence节点的属性	/api/v1/evidence	作为推理过程的产物
   Knowledge	KnowledgeEntity / KnowledgeDTO	knowledge 表	不存储	作为Knowledge节点存在	/api/v1/knowledge	作为长期记忆和推理依据
   Recommendation	RecommendationEntity / RecommendationDTO	recommendation 表	recommendation_log 表	作为Recommendation节点存在	/api/v1/recommendations	作为推理输出的建议
   Execution	ExecutionEntity / ExecutionDTO	execution 表	execution_log 表	作为Execution节点存在	/api/v1/executions	作为执行结果分析的输入
   Optimization	OptimizationEntity / OptimizationDTO	optimization 表	optimization_agg 表	作为Optimization节点存在	/api/v1/optimizations	作为优化经验学习的输入
   3.2 核心调用链路
   所有业务的唯一合法调用链路如下，与宪法定义的世界演化路径完全对齐：

plaintext
复制
Observation采集
↓
ObservationRepository(MySQL/ClickHouse)
↓
TimelineBuilder(ClickHouse查询)
↓
EvidenceEngine(AI推理)
↓
KnowledgeService(验证后沉淀)
↓
RecommendationEngine(AI生成建议)
↓
ExecutionService(执行建议)
↓
Observation采集(执行后)
↓
VerificationService(现实验证)
↓
OptimizationService(生成优化经验)
↓
KnowledgeService(沉淀为新的知识)
4. Java Package Mapping
   所有Java代码统一放在com.aipe包下，按领域划分如下：

包路径	说明	对应世界模型
com.aipe.domain.observation	Observation领域	Observation
com.aipe.domain.resource	Resource领域	Resource/Relationship/Topology
com.aipe.domain.timeline	Timeline领域	Timeline
com.aipe.domain.evidence	Evidence领域	Evidence
com.aipe.domain.knowledge	Knowledge领域	Knowledge
com.aipe.domain.recommendation	Recommendation领域	Recommendation/Execution/Optimization
com.aipe.repository	数据访问层	所有领域
com.aipe.service	业务层	所有领域
com.aipe.api	接入层	所有领域
com.aipe.engine	AI推理层	所有领域
com.aipe.connector	采集层	Observation
5. Mapping Priority
   所有映射工作按以下优先级推进：

P0（基础设施层，优先落地）：Observation、Resource、Relationship三个核心基础对象的映射，是所有上层逻辑的依赖
P1（核心业务层，次优先）：Timeline、Evidence、Knowledge三个推理链核心对象的映射
P2（应用层，最后落地）：Recommendation、Execution、Optimization三个执行优化对象的映射
6. Acceptance Criteria
   本总纲通过验收需满足以下条件：

所有已冻结的世界模型对象都有完整的工程域映射，无遗漏
跨域调用链路完全对齐宪法定义的世界演化路径，无冲突
所有映射优先级明确，可指导后续执行节奏
所有后续IM-XXX文档都有明确的参照锚点，不会出现冲突或歧义
7. Coding Rules
   所有后续工程实现必须遵守以下规则：

严格按照本总纲和IM-XXX单项映射文档实现，不允许私自修改映射关系
所有Java代码必须放在对应的包路径下，不允许跨包乱放
所有数据库表命名统一使用「对象名_后缀」格式，后缀规则：事实表用fact、聚合表用agg、日志表用log、主表用原名称
所有API路径统一使用「/api/v1/对象名」格式，遵循RESTful规范
所有代码必须可测试、可验证，禁止TODO、空实现、Hard Code
8. Freeze Statement
   本规范自发布之日起冻结，属于IM层最高规范，任何修改必须经过以下流程：

plaintext
复制
RFC
↓
Architecture Review
↓
Approval
↓
新版本发布
↓
重新冻结
未经过上述流程，任何人/IM文档不得修改本规范内容，所有后续IM-XXX文档必须对齐本总纲，不允许出现冲突。

9. Revision History
   版本	时间	修改内容	负责人
   1.0	2026-07-20	初始版本发布	AI Performance Engineer Team
10. Approval
    本规范已通过架构评审，正式生效。

11. Notes
    本规范完成后，后续IM-001~IM-13的单项映射文档必须引用本总纲，所有工程落地工作必须对齐本总纲的映射关系，正式进入M2.5 Implementation Mapping的全面落地阶段。