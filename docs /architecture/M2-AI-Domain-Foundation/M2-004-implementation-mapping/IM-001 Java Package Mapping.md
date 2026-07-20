IM-001-Java-Package-Mapping.md
AI Performance Engineer
M2-004 Implementation Mapping
IM-001 Java Package Mapping
Version: v1.0
Status: Draft
Milestone: M2 – AI Domain Foundation

1. 设计目标
   建立统一的Java包结构，和13个世界模型、三大架构法则、Constitution v1.0完全对齐
   明确模块边界，禁止跨模块反向依赖，落实所有架构约束
   输出Rowboat可直接遵循的编码说明书，避免理解偏差和返工
   预留扩展能力，未来新增模块、新增Resource类型时不需要修改现有包结构
2. 顶层包结构
   所有Java代码统一放在com.aipe根包下，顶层模块划分如下：

text
复制
com.aipe
├── domain                ← 核心领域层，对应所有世界模型，不依赖任何外层
├── connector             ← 采集器组件，负责数据采集，禁止维护领域数据
├── agent                 ← 代理组件，负责执行操作，禁止维护领域数据
├── pipeline              ← 数据处理流水线，负责数据清洗、转换、路由
├── storage               ← 存储抽象层，定义所有存储接口
├── ai                    ← AI引擎层，负责推理、学习、推荐
├── api                   ← REST API接入层，负责对外提供接口
├── service               ← 业务服务层，负责跨领域逻辑编排
├── repository            ← 数据访问层，实现领域仓储接口
├── infrastructure        ← 基础设施层，包含防腐层、工具、中间件适配
├── common                ← 公共组件层，包含词汇、治理、常量、事件
└── Application.java      ← 启动类
3. 公共组件包映射（common）
   落实M2-005 AI World Vocabulary、Constitution v1.0治理要求：

text
复制
com.aipe.common
├── vocabulary            ← M2-005 词汇管理
│   ├── VocabularyRegistry.java        ← 统一术语注册中心
│   ├── TermValidator.java              ← 术语校验器，禁止自定义术语
│   └── VocabularyConstants.java       ← 所有官方术语常量
│
├── governance            ← Constitution 治理
│   ├── RfcService.java                ← RFC流程管理
│   ├── ConstitutionValidator.java     ← 宪法约束校验
│   ├── SpecificationChecker.java      ← 规范一致性校验
│   └── VersionManager.java            ← 版本与冻结状态管理
│
├── event                 ← 领域事件
│   ├── DomainEvent.java               ← 领域事件基类
│   ├── EventPublisher.java            ← 事件发布接口
│   └── EventSubscriber.java           ← 事件订阅接口
│
├── util                  ← 工具类
│   ├── TimeUtils.java                  ← 时间处理工具
│   ├── JsonUtils.java                  ← JSON序列化工具
│   ├── ValidateUtils.java              ← 通用校验工具
│   └── HashUtils.java                  ← 哈希计算工具
│
└── constant              ← 全局常量
├── ApiConstants.java               ← API相关常量
├── StorageConstants.java           ← 存储相关常量
└── AiConstants.java                ← AI相关常量
4. 领域模块包映射（domain）
   严格对齐AI World Evolution Chain，每个包标注遵循的架构法则：

text
复制
com.aipe.domain
├── resource              ← M2-007 Unified Resource Model | 遵循Law-001
│   ├── entity            ← 领域实体
│   │   ├── Resource.java
│   │   ├── ResourceType.java
│   │   ├── ResourceCategory.java
│   │   ├── ResourceStatus.java
│   │   ├── ResourceLabel.java
│   │   └── ResourceAttribute.java
│   ├── repository        ← 仓储接口（实现类在repository层）
│   │   └── ResourceRepository.java
│   ├── service           ← 领域服务接口（实现类在service层）
│   │   ├── ResourceDiscoveryService.java
│   │   ├── ResourceLifecycleManager.java
│   │   └── ResourceRegistry.java
│   ├── validator         ← 校验器
│   │   └── ResourceValidator.java
│   └── dto               ← 领域内部数据传输对象
│       └── ResourceDTO.java
│
├── observation           ← M2-006 Observation Model | 遵循Law-002
│   ├── entity
│   │   ├── Observation.java
│   │   ├── ObservationType.java
│   │   ├── ObservationSource.java
│   │   └── ObservationLabel.java
│   ├── repository
│   │   └── ObservationRepository.java
│   ├── service
│   │   ├── ObservationPipeline.java
│   │   └── ObservationFactory.java
│   ├── validator
│   │   └── ObservationValidator.java
│   └── dto
│       └── ObservationDTO.java
│
├── relationship          ← M2-008 Relationship Model | 遵循Law-000
│   ├── entity
│   │   ├── Relationship.java
│   │   ├── RelationshipType.java
│   │   ├── RelationshipDirection.java
│   │   └── RelationshipStatus.java
│   ├── repository
│   │   └── RelationshipRepository.java
│   ├── service
│   │   ├── RelationshipDiscoveryService.java
│   │   └── RelationshipGraph.java
│   ├── validator
│   │   └── RelationshipValidator.java
│   └── dto
│       └── RelationshipDTO.java
│
├── topology              ← M2-009 Topology Model | 遵循Law-000
│   ├── model
│   │   ├── Topology.java
│   │   ├── TopologyNode.java
│   │   ├── TopologyEdge.java
│   │   └── TopologyView.java
│   ├── service
│   │   ├── ProjectionEngine.java
│   │   ├── GraphBuilder.java
│   │   ├── TopologyService.java
│   │   └── TopologyQuery.java
│   └── renderer
│       └── TopologyRenderer.java
│
├── timeline              ← M2-010 Timeline Model | 遵循Law-000
│   ├── entity
│   │   ├── Timeline.java
│   │   ├── TimelineWindow.java
│   │   └── TimelineEvent.java
│   ├── repository
│   │   └── TimelineRepository.java
│   ├── service
│   │   ├── TimelineBuilder.java
│   │   ├── TimelineService.java
│   │   ├── TimelineReplayEngine.java
│   │   └── TimelineQuery.java
│   └── validator
│       └── TimelineValidator.java
│
├── evidence              ← M2-011 Evidence Model | 遵循Constitution Article 8
│   ├── entity
│   │   ├── Evidence.java
│   │   ├── EvidenceType.java
│   │   └── EvidenceStatus.java
│   ├── repository
│   │   └── EvidenceRepository.java
│   ├── service
│   │   ├── EvidenceBuilder.java
│   │   ├── EvidenceEngine.java
│   │   ├── EvidenceReasoner.java
│   │   └── ConfidenceCalculator.java
│   ├── validator
│   │   └── EvidenceValidator.java
│   └── dto
│       └── EvidenceDTO.java
│
├── knowledge             ← M2-012 Knowledge Model | 遵循Constitution Article 9
│   ├── entity
│   │   ├── Knowledge.java
│   │   ├── KnowledgePattern.java
│   │   └── KnowledgeVersion.java
│   ├── repository
│   │   └── KnowledgeRepository.java
│   ├── service
│   │   ├── KnowledgeBuilder.java
│   │   ├── KnowledgeEngine.java
│   │   ├── RecommendationEngine.java
│   │   └── KnowledgeRegistry.java
│   └── validator
│       └── KnowledgeValidator.java
│
├── optimization          ← M2-013 Execution & Optimization Model | 遵循Constitution Article 10
│   ├── entity
│   │   ├── Recommendation.java
│   │   ├── ExecutionPlan.java
│   │   ├── ExecutionRecord.java
│   │   ├── OptimizationResult.java
│   │   └── RollbackRecord.java
│   ├── repository
│   │   └── OptimizationRepository.java
│   ├── service
│   │   ├── ExecutionService.java
│   │   └── OptimizationEngine.java
│   └── validator
│       └── OptimizationValidator.java
│
└── event                 ← 领域事件定义
├── ResourceCreatedEvent.java
├── ResourceUpdatedEvent.java
├── ObservationGeneratedEvent.java
├── EvidenceGeneratedEvent.java
└── KnowledgeVerifiedEvent.java
5. 基础设施层包映射（infrastructure）
   负责隔离外部系统，实现防腐层：

text
复制
com.aipe.infrastructure
├── anticorruption        ← 防腐层，隔离Connector/Agent/外部系统
│   ├── ConnectorAdapter.java          ← 采集器适配器
│   ├── AgentAdapter.java              ← 代理适配器
│   └── ExternalSystemAdapter.java     ← 外部系统适配器
│
├── storage               ← 存储实现
│   ├── mysql             ← MySQL存储实现
│   ├── clickhouse        ← ClickHouse存储实现
│   ├── graph             ← 图数据库存储实现
│   └── redis             ← Redis缓存实现
│
└── config                ← 配置类
├── StorageConfig.java
├── AiConfig.java
└── ConnectorConfig.java
6. 分层依赖约束
   所有代码必须严格遵循以下依赖关系，禁止反向依赖：

核心领域层（domain）：不依赖任何外层，仅内部依赖遵循AI World Evolution Chain顺序：
resource → observation → relationship → topology → timeline → evidence → knowledge → optimization
业务服务层（service）：可依赖domain、repository、common层，不可依赖api、connector、ai层
API接入层（api）：可依赖service、common层，不可直接依赖domain、repository层
AI引擎层（ai）：可依赖domain、repository、common层，不可被domain层依赖
采集/代理层（connector/agent/pipeline）：可依赖domain、storage、common层，不可互相依赖
基础设施层（infrastructure）：可依赖所有层，但仅提供技术支持，不实现业务逻辑
7. 实现要求
   所有领域实体必须实现Serializable接口，必须包含无参构造函数，统一使用Lombok注解
   所有仓储接口、服务接口必须定义在domain层，实现类放在对应外层
   所有校验器必须独立实现，不允许在实体类、Service中写校验逻辑
   所有外部系统调用必须经过防腐层适配，不允许直接调用外部接口
   所有领域事件必须继承DomainEvent基类，使用统一的事件发布接口
   所有包结构必须严格对齐本规范，新增类必须放到对应包中，不允许随意新增包路径
8. 验收标准
   ✔ 包结构与13个世界模型完全一一对应
   ✔ 所有模块都标注了遵循的架构法则/宪法条款
   ✔ 依赖关系符合要求，无反向依赖
   ✔ 所有核心类都有明确的职责定义，Rowboat可直接编码
   ✔ 预留了扩展能力，新增模块不需要修改现有包结构
   确认IM-001的内容没有问题后，我们继续编写IM-002-Domain-Model-Mapping.md。