WP011-Unified-Resource-Model.md
AI Performance Engineer
WP011 Unified Resource Model Blueprint v1.0
Document Type:
Work Package Blueprint

Version:
v1.0

Status:
Frozen

Milestone:
M1 - Agent MVP

Priority:
P0

DependsOn:

WP007 Observation Pipeline
WP008 Storage Layer
WP009 Configuration & Deployment Manager
WP010 Scenario Manager
IM-003 MySQL Database Mapping
Law-001 Unified Resource Abstraction Rule
RequiredBy:

WP012 Topology Model
WP013 Evidence Engine
WP014 Performance Analysis Engine
WP018 Root Cause Engine
WP020 JMeter Integration
WP030 Knowledge System
EstimatedJavaFiles:
45

EstimatedWorkload:
8 Days

Blueprint Template:
01-Blueprint-Template.md v1.0

1. Goal（目标）
   1.1 Purpose
   建立 AI Performance Engineer 的统一资源抽象模型，将所有IT对象（物理机、应用、服务、中间件、数据库、集群等）统一抽象为Resource，解决资源业务归属、分类管理、关系映射的问题，彻底摒弃传统的机器维度视角，从业务域、应用、集群、中间件域维度统一管理资源，为拓扑发现、AI推理、性能分析提供统一的资源上下文。

核心解决问题：
用户告诉系统「订单系统压力测试」，系统能自动识别订单系统包含的所有资源：订单服务、订单MySQL集群、订单Redis集群等，而不是看零散的机器指标，AI分析时能直接得出「订单系统性能瓶颈在订单MySQL集群」的结论，而不是「192.168.1.1的CPU过高」。

1.2 Capability Added
Before：
零散的机器/指标维度，资源无业务归属，AI无法理解资源对应的业务域

After：
业务域→应用→集群→实例的统一资源维度，所有Observation、Scenario都关联到统一的Resource，AI能基于业务域维度分析性能问题

1.3 Core Principle
Resource是所有IT对象的唯一抽象，所有资源必须有明确的业务归属，禁止无归属的游离资源，所有资源操作必须通过Resource Model统一入口，严格符合Law-001 Unified Resource Abstraction Rule。

1.4 Scope
MVP支持：

所有IT对象统一抽象为Resource
业务域归属定义
资源分类（应用/中间件/基础设施等）
资源关系绑定
资源生命周期管理
和Scenario/Agent/Observer的关联
按业务域查询资源列表
不包含：

自动资源发现（后续WP012 Topology Model支持）
资源自动扩缩容
2. Acceptance Criteria（验收标准）
   2.1 Functional Acceptance
   必须支持：
   □ 所有IT对象统一抽象为Resource
   □ 业务域归属定义，所有资源必须有明确的业务归属
   □ 资源分类：应用/中间件/基础设施/平台等
   □ 资源关系绑定：上下游、依赖、包含等关系
   □ 资源状态管理：运行/停止/维护/未知等状态流转
   □ Scenario/Agent/Observer关联到Resource
   □ 按业务域查询资源列表
   □ 按资源类型/状态查询资源列表

2.2 Technical Acceptance
必须满足：
□ Resource唯一ID，全局唯一
□ 业务归属必填，禁止无业务归属的游离资源
□ 资源状态流转合法
□ 资源关系绑定准确
□ 字段完全对齐IM-003的resource表结构
□ 严格符合Law-001约束，所有资源操作走统一入口

2.3 Integration Acceptance
完整链路：
用户创建Scenario→绑定业务域→自动查询该域下所有Resource→Agent采集这些资源的指标→Observation关联到对应Resource→AI分析时按资源维度聚合→输出业务域维度的性能分析报告

3. Package List（包结构）
   plaintext
   复制
   com.aipe.resource
   ├── controller
   ├── model
   ├── service
   ├── repository
   ├── validator
   ├── binding
   ├── lifecycle
   └── support
   Package Responsibility
   Package	职责
   controller	API入口，对外提供资源管理接口
   model	领域对象：Resource实体、枚举等
   service	业务逻辑：资源查询、绑定、统计等
   repository	数据访问，操作MySQL resource表
   validator	资源校验：创建/更新/绑定等场景的合法性校验
   binding	资源关系绑定，联动图数据库
   lifecycle	资源生命周期管理：创建/更新/删除/状态流转等
   support	工具类、转换类等支撑逻辑
4. Class List（类清单）
   4.1 Resource
   Package:
   model

职责：
所有IT对象统一抽象的核心实体，字段完全对齐IM-003的resource表。

字段：

字段	类型	说明
resourceId	String	全局唯一ID，UUID格式
resourceName	String	资源名称，同类型下唯一
resourceType	ResourceType	资源类型枚举：APPLICATION/SERVICE/INSTANCE/CLUSTER/DATABASE/MIDDLEWARE/HOST/REDIS/MQ等
resourceCategory	ResourceCategory	资源类别枚举：BUSINESS（业务）/INFRA（基础设施）/PLATFORM（平台）
resourceStatus	ResourceStatus	资源状态枚举：RUNNING/STOPPED/MAINTENANCE/UNKNOWN
businessDomain	String	业务归属域，必填，如：订单/支付/用户/物流等
labels	Map<String, String>	业务标签，存部门、负责人、环境等信息
attributes	Map<String, String>	资源扩展属性，存IP、端口、集群规格等个性化信息
version	int	乐观锁版本号，默认1
createdTime	LocalDateTime	创建时间
updatedTime	LocalDateTime	更新时间
4.2 ResourceRepository
Package:
repository

职责：
数据访问接口，操作MySQL resource表，完全对齐IM-003的MySQL规范。

4.3 ResourceLifecycleManager
Package:
lifecycle

职责：
资源生命周期管理统一入口，所有资源操作必须通过该入口，禁止其他模块直接操作资源数据，符合Law-001约束。

4.4 ResourceValidator
Package:
validator

职责：
资源操作合法性校验，所有资源操作前必须经过校验。

4.5 ResourceBindingService
Package:
binding

职责：
资源关系绑定管理，联动图数据库存储资源关系，完全对齐IM-005的图数据库规范。

4.6 ResourceDiscoveryService
Package:
service

职责：
资源发现统一入口，接收Connector/Agent上报的资源信息，校验后统一创建/更新Resource，禁止Connector/Agent直接创建/修改Resource，符合Law-001约束。

5. Method List（方法清单）
   ResourceLifecycleManager
   java
   复制
   // 创建资源，返回资源ID
   String createResource(Resource resource)
   // 更新资源基础信息
   void updateResource(Resource resource)
   // 更新资源状态
   void updateResourceStatus(String resourceId, ResourceStatus status)
   // 删除资源（逻辑删除）
   void deleteResource(String resourceId)
   // 绑定业务归属域
   void bindBusinessDomain(String resourceId, String businessDomain)
   ResourceValidator
   java
   复制
   // 校验资源创建合法性
   void validateCreate(Resource resource)
   // 校验资源更新合法性
   void validateUpdate(Resource resource)
   // 校验业务归属是否合法
   void validateBusinessDomain(String businessDomain)
   // 校验资源状态流转合法性
   void validateStatusTransition(ResourceStatus current, ResourceStatus target)
   ResourceBindingService
   java
   复制
   // 绑定资源关系
   void bindRelation(String sourceResourceId, String targetResourceId, String relationType)
   // 解绑资源关系
   void unbindRelation(String sourceResourceId, String targetResourceId, String relationType)
   // 查询资源的所有关联资源
   List<Resource> queryRelatedResources(String resourceId)
   // 查询业务域下的所有资源
   List<Resource> queryByBusinessDomain(String businessDomain)
   ResourceDiscoveryService
   java
   复制
   // 处理Connector上报的资源信息，统一创建/更新Resource
   String handleResourceReport(ResourceReport report)
   // 批量处理资源上报
   List<String> batchHandleResourceReport(List<ResourceReport> reports)
   ResourceRepository
   java
   复制
   // 保存资源
   void save(Resource resource)
   // 更新资源
   void update(Resource resource)
   // 根据ID查询资源
   Resource findById(String resourceId)
   // 根据类型查询资源列表
   List<Resource> findByType(ResourceType type)
   // 根据业务域查询资源列表
   List<Resource> findByBusinessDomain(String businessDomain)
   // 根据状态查询资源列表
   List<Resource> findByStatus(ResourceStatus status)
   // 分页查询资源列表
   Page<Resource> pageQuery(ResourceQuery query)
6. Dependency（依赖关系）
   6.1 Internal Dependency
   plaintext
   复制
   WP009 Configuration Manager
   ↓
   WP011 Unified Resource Model
   ↓
   WP010 Scenario Manager
   ↓
   WP007 Observation Pipeline
   ↓
   WP008 Storage Layer
   ↓
   WP012 Topology Model
   6.2 External Dependency
   依赖	用途
   MySQL	Resource结构化数据存储，对应IM-003的resource表
   图数据库	资源关系存储，对应IM-005的Relationship模型
   Redis	资源缓存，提升查询性能
   Connector/Agent	资源信息上报，统一由ResourceDiscoveryService处理
   6.3 Dependency Rule
   Resource是所有资源相关的唯一入口，禁止其他模块直接操作资源数据，所有资源操作必须通过ResourceLifecycleManager，所有资源上报必须通过ResourceDiscoveryService，严格符合Law-001约束。

7. Physical File List（物理文件清单）
   必须创建：

plaintext
复制
aipe-resource/
├── src/main/java/com/aipe/resource/
│   ├── controller/ResourceController.java
│   ├── model/Resource.java
│   ├── model/ResourceType.java
│   ├── model/ResourceCategory.java
│   ├── model/ResourceStatus.java
│   ├── service/ResourceDiscoveryService.java
│   ├── service/ResourceBindingService.java
│   ├── service/ResourceQueryService.java
│   ├── repository/ResourceRepository.java
│   ├── validator/ResourceValidator.java
│   ├── lifecycle/ResourceLifecycleManager.java
│   └── support/ResourceConverter.java
├── src/main/resources/db/migration/V11__resource.sql
└── src/main/resources/application.yml
8. Sequence Diagram（时序图）
   资源创建流程
   plaintext
   复制
   Connector/Agent
   ↓ 上报资源信息
   ResourceDiscoveryService
   ↓ 转发资源信息
   ResourceValidator
   ↓ 校验通过
   ResourceLifecycleManager
   ↓ 调用存储接口
   ResourceRepository
   ↓ 写入数据
   MySQL
   ↓ 返回成功
   ResourceLifecycleManager
   ↓ 返回资源ID
   Connector/Agent
   Scenario绑定资源流程
   plaintext
   复制
   User
   ↓ 创建Scenario，绑定业务域
   ScenarioManager
   ↓ 查询业务域下所有资源
   ResourceBindingService
   ↓ 查询资源列表
   ResourceRepository
   ↓ 返回资源列表
   ResourceBindingService
   ↓ 返回资源绑定结果
   ScenarioManager
   ↓ 返回Scenario创建结果
   User
9. State Machine（状态机）
   Resource生命周期
   plaintext
   复制
   CREATED
   ↓
   RUNNING
   ↓          ↓
   MAINTENANCE  STOPPED
   ↓          ↓
   RUNNING      DELETED
   异常状态：FAILED

10. Implementation Constraints（实现约束）
    10.1 Must Implement
    必须实现：

所有IT对象必须抽象为Resource，禁止其他模块单独定义资源模型
所有Resource必须有明确的业务归属，businessDomain字段必填，禁止无业务归属的游离资源
所有资源操作必须通过ResourceLifecycleManager统一入口，禁止直接操作Resource数据库
所有Connector/Agent上报的资源信息必须通过ResourceDiscoveryService统一处理，禁止Connector/Agent直接创建/修改Resource，符合Law-001约束
Resource字段完全对齐IM-003的resource表结构，禁止冗余字段
资源关系绑定必须联动图数据库，更新关系时必须同步更新MySQL resource表的relationship_ids字段，保证数据一致性
10.2 Forbidden
禁止：

无业务归属的游离资源
直接操作Resource数据库
不同模块重复定义资源模型
资源ID重复
Connector/Agent直接创建/修改Resource
资源状态非法流转
10.3 Engineering Rules
必须：

资源唯一化：全局唯一resourceId
归属必填化：所有资源必须有明确的业务归属
操作入口统一化：所有资源操作走ResourceLifecycleManager
关系绑定准确化：资源关系变更必须双写MySQL和图数据库
字段规范化：完全对齐IM-003的MySQL表结构
11. Test & Verification（测试与验证）
    11.1 Build
    bash
    复制
    mvn clean install
    11.2 Test Scenario
    创建订单业务域
    创建3个Resource：
    订单服务：类型APPLICATION，业务域订单
    订单MySQL集群：类型DATABASE，业务域订单
    订单Redis集群：类型REDIS，业务域订单
    创建订单压测Scenario，绑定订单业务域
    验证自动查询到这3个Resource
    Agent采集指标后Observation关联到对应Resource
    查询订单业务域下的资源列表，返回3个Resource
    11.3 Verification
    检查：

Resource创建成功，业务归属正确
Scenario绑定资源正确
Observation关联资源正确
资源状态流转合法
符合Law-001约束，所有资源操作走统一入口
11.4 Expected Result
返回Scenario绑定的所有Resource列表：

json
复制
{
"code": 0,
"message": "success",
"data": {
"scenarioId": "scenario-123",
"businessDomain": "订单",
"resources": [
{
"resourceId": "res-001",
"resourceName": "订单服务",
"resourceType": "APPLICATION",
"businessDomain": "订单",
"resourceStatus": "RUNNING"
},
{
"resourceId": "res-002",
"resourceName": "订单MySQL集群",
"resourceType": "DATABASE",
"businessDomain": "订单",
"resourceStatus": "RUNNING"
},
{
"resourceId": "res-003",
"resourceName": "订单Redis集群",
"resourceType": "REDIS",
"businessDomain": "订单",
"resourceStatus": "RUNNING"
}
]
}
}
11.5 Troubleshooting
资源创建失败：检查businessDomain是否为空，资源类型是否合法
Scenario绑定资源失败：检查业务域是否正确，资源是否属于该业务域
Observation关联资源失败：检查资源ID是否匹配，Observation的resourceId是否正确
资源状态流转失败：检查当前状态是否允许流转到目标状态