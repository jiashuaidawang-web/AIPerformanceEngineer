IM-002-Domain-Model-Mapping.md
AI Performance Engineer
M2-004 Implementation Mapping
IM-002 Domain Model Mapping
Version: v1.0
Status: Draft
Milestone: M2 – AI Domain Foundation

1. 设计目标
   将13个世界模型Specification一一映射为可落地的Java领域实体
   明确每个实体的字段、类型、约束、核心方法，Rowboat可直接编码
   所有实体严格遵循三大基础法则和Constitution v1.0的约束
   建立实体之间的关联关系，对齐AI World Evolution Chain
2. Resource 领域实体（M2-007）
   遵循Law-001 Resource Ownership Principle，所有对象统一抽象为Resource

2.1 Resource实体
java
复制
package com.aipe.domain.resource.entity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resource implements Serializable {
// 主键，全局唯一
private String resourceId;
// 资源名称
private String resourceName;
// 资源类型，枚举：APPLICATION, SERVICE, JVM, REDIS, MYSQL, LINUX, KUBERNETES, API, MQ
private ResourceType resourceType;
// 资源类别，枚举：BUSINESS, INFRASTRUCTURE, PLATFORM
private ResourceCategory resourceCategory;
// 资源状态，枚举：RUNNING, STOPPED, MAINTENANCE, UNKNOWN
private ResourceStatus resourceStatus;
// 资源标签，用于分类和查询
private List<ResourceLabel> labels;
// 资源属性，KV结构，存储额外信息
private Map<String, String> attributes;
// 资源关系，关联其他ResourceId
private List<String> relationshipIds;
// 创建时间
private LocalDateTime createdAt;
// 更新时间
private LocalDateTime updatedAt;
// 版本号
private Integer version;
}
2.2 ResourceType枚举
java
复制
public enum ResourceType {
APPLICATION, SERVICE, JVM, REDIS, MYSQL, LINUX, KUBERNETES, API, MQ, CONNECTOR, AGENT
}
2.3 Resource核心方法
java
复制
public interface ResourceDiscoveryService {
// 统一创建Resource，禁止直接new Resource()
Resource createResource(CreateResourceCommand command);
// 合并Resource
Resource mergeResource(String resourceId, MergeResourceCommand command);
// 更新状态
void updateStatus(String resourceId, ResourceStatus status);
// 根据ID查询
Resource findById(String resourceId);
// 根据类型查询
List<Resource> findByType(ResourceType type);
}
3. Observation 领域实体（M2-006）
   遵循Law-002 Observation Belongs to Resource，Observation必须属于Resource

3.1 Observation实体
java
复制
package com.aipe.domain.observation.entity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Observation implements Serializable {
// 全局唯一ID
private String observationId;
// 所属资源ID，必填，禁止孤立Observation
private String resourceId;
// 观察类型，枚举：METRIC, EVENT, LOG, STATUS
private ObservationType observationType;
// 指标名称
private String metricName;
// 指标值
private String metricValue;
// 单位
private String unit;
// 发生时间
private LocalDateTime timestamp;
// 采集来源，枚举：CONNECTOR, AGENT, MANUAL
private ObservationSource source;
// 标签，用于额外分类
private List<ObservationLabel> labels;
// 数据版本
private Integer version;
}
3.2 Observation核心方法
java
复制
public interface ObservationPipeline {
// 采集Observation，禁止直接创建，必须通过Pipeline
Observation collectObservation(CollectObservationCommand command);
// 校验Observation有效性
boolean validate(Observation observation);
// 批量写入
void batchInsert(List<Observation> observations);
// 查询时间窗口内的Observation
List<Observation> queryByTimeWindow(String resourceId, LocalDateTime startTime, LocalDateTime endTime);
}
4. Relationship 领域实体（M2-008）
   遵循Law-000 Single Source of Truth，关系是连接世界的核心

4.1 Relationship实体
java
复制
package com.aipe.domain.relationship.entity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Relationship implements Serializable {
// 全局唯一ID
private String relationshipId;
// 源资源ID
private String sourceResourceId;
// 目标资源ID
private String targetResourceId;
// 关系类型，枚举：DEPENDENCY, CALL, CONTAIN, COMMUNICATE, MANAGE
private RelationshipType relationshipType;
// 关系方向，枚举：UNIDIRECTIONAL, BIDIRECTIONAL
private RelationshipDirection direction;
// 关系状态，枚举：ACTIVE, INACTIVE, BREAKING
private RelationshipStatus status;
// 关系属性，存储额外信息
private Map<String, String> properties;
// 创建时间
private LocalDateTime createdAt;
// 更新时间
private LocalDateTime updatedAt;
}
4.2 Relationship核心方法
java
复制
public interface RelationshipDiscoveryService {
// 创建关系
Relationship createRelationship(CreateRelationshipCommand command);
// 查询资源的所有关系
List<Relationship> findByResourceId(String resourceId);
// 查询两个资源之间的关系
List<Relationship> findBetweenResources(String sourceId, String targetId);
// 根据类型查询
List<Relationship> findByType(RelationshipType type);
}
5. Topology 领域实体（M2-009）
   遵循Constitution Article 6，Topology动态生成，禁止持久化

5.1 Topology模型
java
复制
package com.aipe.domain.topology.model;

@Data
@Builder
public class TopologyView {
// 拓扑视图ID
private String topologyId;
// 视图名称
private String topologyName;
// 节点列表
private List<TopologyNode> nodes;
// 边列表
private List<TopologyEdge> edges;
// 生成时间
private LocalDateTime generatedAt;
// 视图层级，枚举：CLUSTER, SERVICE, INSTANCE, COMPONENT
private TopologyViewLevel level;
}
5.2 Topology核心方法
java
复制
public interface TopologyService {
// 投影生成拓扑视图
TopologyView generateTopology(TopologyQuery query);
// 根据资源ID查询关联拓扑
TopologyView getTopologyByResourceId(String resourceId);
// 拓扑路径查询
List<TopologyPath> findPath(String sourceResourceId, String targetResourceId);
}
6. Timeline 领域实体（M2-010）
   遵循Constitution Article 7，Timeline是Observation的时间有序组织

6.1 Timeline实体
java
复制
package com.aipe.domain.timeline.entity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Timeline implements Serializable {
// 全局唯一ID
private String timelineId;
// 所属资源ID
private String resourceId;
// 起始时间
private LocalDateTime startTime;
// 结束时间
private LocalDateTime endTime;
// Observation数量
private Integer observationCount;
// Observation列表，按时间排序
private List<Observation> observations;
// 版本号
private Integer version;
}
6.2 Timeline核心方法
java
复制
public interface TimelineService {
// 构建Timeline
Timeline buildTimeline(String resourceId, LocalDateTime startTime, LocalDateTime endTime);
// 追加Observation，只能追加，不能修改
Timeline appendObservation(String timelineId, Observation observation);
// 回放Timeline
TimelineWindow replay(String timelineId, LocalDateTime startTime, LocalDateTime endTime);
// 查询时间窗口内的Timeline
Timeline queryWindow(String resourceId, LocalDateTime startTime, LocalDateTime endTime);
}
7. Evidence 领域实体（M2-011）
   遵循Constitution Article 8，Evidence是AI推理的结果

7.1 Evidence实体
java
复制
package com.aipe.domain.evidence.entity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evidence implements Serializable {
// 全局唯一ID
private String evidenceId;
// 证据类型，枚举：PERFORMANCE, DEPENDENCY, DEPLOYMENT, BUSINESS, AI, COMPOSITE
private EvidenceType evidenceType;
// 证据标题
private String title;
// AI解释
private String description;
// 根资源ID
private String rootResourceId;
// 引用的Observation ID列表
private List<String> observationIds;
// 引用的Relationship ID列表
private List<String> relationshipIds;
// 引用的Timeline ID
private String timelineId;
// 可信度 0~100
private Integer confidence;
// 推理步骤
private List<String> reasoningSteps;
// 状态，枚举：NEW, VERIFIED, REJECTED
private EvidenceStatus status;
// 创建时间
private LocalDateTime createdAt;
// 版本号
private Integer version;
}
7.2 Evidence核心方法
java
复制
public interface EvidenceEngine {
// 生成Evidence
Evidence generateEvidence(GenerateEvidenceCommand command);
// 计算可信度
Integer calculateConfidence(Evidence evidence);
// 验证Evidence
void verifyEvidence(String evidenceId, VerificationResult result);
// 合并多个Evidence
Evidence mergeEvidence(List<String> evidenceIds);
}
8. Knowledge 领域实体（M2-012）
   遵循Constitution Article 9，Knowledge是验证后的Evidence

8.1 Knowledge实体
java
复制
package com.aipe.domain.knowledge.entity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Knowledge implements Serializable {
// 全局唯一ID
private String knowledgeId;
// 标题
private String title;
// 知识类型，枚举：BOTTLENECK, DEPENDENCY, DEPLOYMENT, BUSINESS, OPTIMIZATION, AI
private KnowledgeType knowledgeType;
// 来源Evidence ID
private String evidenceId;
// 验证记录ID
private String verificationId;
// 最终可信度
private Integer confidence;
// 适用条件
private String applicableConditions;
// 推荐方案
private String recommendation;
// 历史成功率
private Double successRate;
// 创建时间
private LocalDateTime createdAt;
// 语义版本号
private String version;
}
8.2 Knowledge核心方法
java
复制
public interface KnowledgeEngine {
// 构建Knowledge
Knowledge buildKnowledge(BuildKnowledgeCommand command);
// 验证Knowledge
void verify(String knowledgeId, VerificationResult result);
// 推荐方案
Recommendation generateRecommendation(String knowledgeId);
// 搜索Knowledge
List<Knowledge> search(KnowledgeQuery query);
// 升级版本
Knowledge upgradeVersion(String knowledgeId, String newVersion);
}
9. Execution & Optimization 领域实体（M2-013）
   遵循Constitution Article 10，Execution改变现实

9.1 Optimization相关实体
java
复制
// Recommendation实体
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation implements Serializable {
private String recommendationId;
private String knowledgeId;
private String title;
private String description;
private String targetResourceId;
private String expectedResult;
private String riskLevel;
private LocalDateTime createdAt;
}

// ExecutionRecord实体
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionRecord implements Serializable {
private String executionId;
private String recommendationId;
private String executor;
private LocalDateTime executionTime;
private String executionParams;
private String result;
private ExecutionStatus status;
}

// OptimizationResult实体
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationResult implements Serializable {
private String optimizationId;
private String executionId;
private String targetResourceId;
private String metricName;
private String beforeValue;
private String afterValue;
private String improvement;
private VerificationResult verificationResult;
private LocalDateTime createdAt;
}
9.2 Optimization核心方法
java
复制
public interface OptimizationEngine {
// 生成推荐
Recommendation generateRecommendation(String knowledgeId, String resourceId);
// 执行优化
ExecutionRecord execute(ExecuteOptimizationCommand command);
// 验证优化结果
OptimizationResult verifyOptimization(String executionId, VerificationCommand command);
// 回滚优化
RollbackRecord rollback(String executionId);
}
10. 领域事件定义
    所有事件继承DomainEvent基类，实现事件驱动：

java
复制
// 资源创建事件
@Data
public class ResourceCreatedEvent extends DomainEvent {
private String resourceId;
private ResourceType resourceType;
}

// Observation生成事件
@Data
public class ObservationGeneratedEvent extends DomainEvent {
private String observationId;
private String resourceId;
}

// Evidence生成事件
@Data
public class EvidenceGeneratedEvent extends DomainEvent {
private String evidenceId;
private EvidenceType evidenceType;
}

// Knowledge验证事件
@Data
public class KnowledgeVerifiedEvent extends DomainEvent {
private String knowledgeId;
private VerificationResult result;
}
11. 验收标准
    ✔ 13个世界模型都有对应的领域实体映射
    ✔ 所有实体字段、类型、约束都明确，Rowboat可直接编码
    ✔ 所有实体都标注了遵循的架构法则/宪法条款
    ✔ 核心方法签名完整，接口和实现分离
    ✔ 实体关联关系对齐AI World Evolution Chain
    ✔ 领域事件定义完整，支持事件驱动架构
    确认IM-002的内容没有问题后，我们继续编写IM-003-Database-Schema-Mapping.md。

