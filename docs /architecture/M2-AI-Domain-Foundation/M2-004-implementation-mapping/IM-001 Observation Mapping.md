IM-001 Observation Mapping
Version: 1.0
Status: Frozen
Document Level: Core Mapping
Authority: High
Applies To: Observation-related Java Code, Database, API, AI Engine, All Contributors, All AI Assistants

1. Purpose
   本文档定义Observation对象从世界模型到工程实现的全链路映射，明确Observation在Java代码、MySQL、ClickHouse、Graph、REST API、AI Engine六个工程域的具体载体、实现规范、调用链路，是所有Observation相关工程落地的唯一依据。

本规范严格对齐IM-000总纲和M2-006 Observation Model Specification，不允许冲突。

2. Input Specification
   本规范引用以下已冻结文档，冲突时以对应上级文档为准：

IM-000 Implementation Overview（IM层总纲）
M2-006 Observation Model Specification（Observation世界模型定义）
M2-007 Unified Resource Model Specification（Resource定义，Observation的归属对象）
M2-010 Timeline Model Specification（Timeline定义，Observation的组织方式）
AIPE-Constitution v1.0（项目最高法律）
3. Terms & Definition
   术语	定义
   ObservationEntity	Observation在Java代码中的实体类，对应ClickHouse的存储结构
   ObservationDTO	Observation在API层的传输对象，用于前后端数据交互
   ObservationVO	Observation在业务层的视图对象，用于Timeline构建等场景
   ObservationRepository	Observation的数据访问层，负责ClickHouse和MySQL的读写操作
   ObservationService	Observation的业务层，负责Observation的采集、校验、转发逻辑
   ObservationController	Observation的接入层，负责对外提供API接口
   ObservationFactory	Observation的工厂类，负责将采集到的原始数据转换为标准Observation对象
   ObservationCollector	Observation的采集器，负责从Connector获取原始数据，调用ObservationFactory生成标准Observation
4. Scope
   本规范覆盖以下范围：

Observation的Java类设计
Observation的MySQL/ClickHouse存储设计
Observation的Graph属性设计
Observation的REST API设计
Observation的Repository/Service层设计
Observation的AI Engine输入设计
Observation的完整调用链路
不包含以下内容：

具体业务场景的采集逻辑（属于Connector层设计）
Observation的分析逻辑（属于Evidence层设计）
5. Normative References
   IM-000 Implementation Overview
   M2-006 Observation Model Specification
   M2-007 Unified Resource Model Specification
   M2-010 Timeline Model Specification
   AIPE-Constitution v1.0
6. Conventions
   所有类命名采用大驼峰命名法
   所有方法命名采用小驼峰命名法
   所有数据库表字段采用下划线命名法
   所有API路径遵循RESTful规范
   所有时间字段统一使用UTC时间戳
7. Java Class Mapping
   7.1 核心类设计
   7.1.1 ObservationEntity
   java
   复制
   package com.aipe.domain.observation.entity;

/**
* Observation实体类，对应ClickHouse observation_fact表结构
  */
  public class ObservationEntity {
  private String id; // 唯一标识
  private String resourceId; // 归属的Resource ID
  private String resourceType; // Resource类型
  private String metricName; // 指标名称，如cpu_usage、qps
  private Double metricValue; // 指标值
  private String unit; // 指标单位，如%、次/秒
  private Long collectTime; // 采集时间戳
  private Long createTime; // 写入时间戳
  private Map<String, String> tags; // 附加标签，如集群、机房信息
  private String collectorId; // 采集器ID
  private String dataSource; // 数据来源，如jvm_collector、redis_collector
  // getter/setter/toString方法
  }
  7.1.2 ObservationDTO
  java
  复制
  package com.aipe.domain.observation.dto;

/**
* Observation API传输对象
  */
  public class ObservationDTO {
  private String id;
  private String resourceId;
  private String resourceType;
  private String metricName;
  private Double metricValue;
  private String unit;
  private Long collectTime;
  private Map<String, String> tags;
  // getter/setter/toString方法
  }
  7.1.3 ObservationVO
  java
  复制
  package com.aipe.domain.observation.vo;

/**
* Observation业务视图对象
  */
  public class ObservationVO {
  private String id;
  private String resourceName; // Resource名称，非ID
  private String resourceType;
  private String metricName;
  private Double metricValue;
  private String unit;
  private String collectTimeStr; // 格式化后的采集时间，如"2026-07-20 14:30:00"
  private Map<String, String> tags;
  // getter/setter/toString方法
  }
  7.1.4 ObservationFactory
  java
  复制
  package com.aipe.domain.observation.factory;

/**
* Observation工厂类，负责将原始采集数据转换为标准Observation
  */
  public class ObservationFactory {
  /**
  * 创建标准Observation实体
  * @param rawData 原始采集数据
  * @param resourceId 归属Resource ID
  * @param collectorId 采集器ID
  * @return 标准ObservationEntity
    */
    public ObservationEntity create(Map<String, Object> rawData, String resourceId, String collectorId) {
    // 实现逻辑：数据校验、字段转换、默认值填充
    }
    }
    7.2 数据访问层
    7.2.1 ObservationRepository
    java
    复制
    package com.aipe.repository.observation;

/**
* Observation数据访问层
  */
  public interface ObservationRepository {
  /**
  * 批量写入Observation到ClickHouse
    */
    void batchSave(List<ObservationEntity> observations);

  /**
  * 按Resource ID和时间范围查询Observation
    */
    List<ObservationEntity> findByResourceIdAndTimeRange(String resourceId, Long startTime, Long endTime);

  /**
  * 按指标名称和时间范围查询Observation
    */
    List<ObservationEntity> findByMetricAndTimeRange(String metricName, Long startTime, Long endTime);

  /**
  * 查询Resource最新的Observation
    */
    ObservationEntity findLatestByResourceId(String resourceId);
    }
    7.3 业务层
    7.3.1 ObservationService
    java
    复制
    package com.aipe.service.observation;

/**
* Observation业务层
  */
  public interface ObservationService {
  /**
  * 采集Observation
    */
    void collect();

  /**
  * 校验Observation数据合法性
    */
    Boolean validate(ObservationEntity observation);

  /**
  * 查询Observation列表
    */
    List<ObservationVO> query(ObservationQueryRequest request);

  /**
  * 查询Observation详情
    */
    ObservationVO detail(String id);
    }
    7.4 接入层
    7.4.1 ObservationController
    java
    复制
    package com.aipe.api.observation;

/**
* Observation接入层
  */
  @RestController
  @RequestMapping("/api/v1/observations")
  public class ObservationController {
  @Autowired
  private ObservationService observationService;

  @PostMapping("/query")
  public ApiResponse<List<ObservationVO>> query(@RequestBody ObservationQueryRequest request) {
  return ApiResponse.success(observationService.query(request));
  }

  @GetMapping("/{id}")
  public ApiResponse<ObservationVO> detail(@PathVariable String id) {
  return ApiResponse.success(observationService.detail(id));
  }
  }
8. Database Mapping
   8.1 ClickHouse Mapping
   8.1.1 observation_fact表设计
   sql
   复制
   CREATE TABLE observation_fact
   (
   id UUID,
   resource_id String,
   resource_type String,
   metric_name String,
   metric_value Float64,
   unit String,
   collect_time DateTime64(3),
   create_time DateTime64(3),
   tags Map(String, String),
   collector_id String,
   data_source String
   )
   ENGINE = MergeTree()
   PARTITION BY toYYYYMM(collect_time)
   ORDER BY (resource_id, metric_name, collect_time)
   TTL collect_time + INTERVAL 1 YEAR;
   8.1.2 索引说明
   主键：(resource_id, metric_name, collect_time)，支持按Resource、指标、时间的高效查询
   分区键：按月份分区，便于过期数据清理
   TTL：数据保留1年，符合宪法要求
   8.2 MySQL Mapping
   MySQL不存储Observation事实数据，仅存储Observation的元数据配置：

sql
复制
CREATE TABLE observation_meta
(
id BIGINT AUTO_INCREMENT PRIMARY KEY,
metric_name VARCHAR(128) NOT NULL COMMENT '指标名称',
metric_desc VARCHAR(512) NOT NULL COMMENT '指标描述',
unit VARCHAR(32) NOT NULL COMMENT '指标单位',
collect_interval INT NOT NULL DEFAULT 60 COMMENT '采集间隔，单位秒',
is_enable TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用采集',
create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
UNIQUE KEY uk_metric_name (metric_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Observation元数据配置表';
9. Graph Mapping
   Observation不作为独立节点存储在Graph中，仅作为Resource节点的动态属性存在：

存储方式：Resource节点的latest_observation属性，存储该Resource最新的Observation摘要信息
属性字段：包含metric_name、metric_value、unit、collect_time四个字段
更新规则：每次采集到新的Observation后，自动更新对应Resource节点的属性
不存储全量Observation数据，避免Graph体积过大
10. REST API Mapping
    API路径	方法	说明	请求参数	响应格式
    /api/v1/observations/query	POST	查询Observation列表	resourceId、metricName、startTime、endTime、pageNum、pageSize	{code, msg, data: {list, total}}
    /api/v1/observations/{id}	GET	查询Observation详情	路径参数id	{code, msg, data: ObservationVO}
    /api/v1/observations/latest	GET	查询Resource最新Observation	resourceId	{code, msg, data: ObservationVO}
11. AI Engine Mapping
    Observation是AI Engine的唯一合法原始输入，所有AI推理都必须基于真实的Observation数据：

输入方式：AI Engine从ClickHouse查询需要的Observation数据，作为推理的原始事实
输入格式：按Resource ID和时间范围查询得到的ObservationEntity列表
禁止行为：不允许使用Mock的Observation、AI生成的Observation、未经验证的Observation作为推理输入
特殊要求：AI Engine需要访问最近30天的全量Observation数据，用于趋势分析和异常检测
12. Sequence
    12.1 采集链路
    plaintext
    复制
    Connector
    ↓ 原始采集数据
    ObservationCollector
    ↓ 调用工厂类
    ObservationFactory
    ↓ 生成标准ObservationEntity
    ObservationService
    ↓ 校验数据
    ObservationRepository
    ↓ 批量写入
    ClickHouse observation_fact表
    ↓ 更新Resource节点属性
    Graph Database
    12.2 查询链路
    plaintext
    复制
    前端请求
    ↓
    ObservationController
    ↓
    ObservationService
    ↓
    ObservationRepository
    ↓ 查询ClickHouse
    ObservationEntity列表
    ↓ 转换为ObservationVO
    返回前端
    12.3 AI推理输入链路
    plaintext
    复制
    AI Engine
    ↓ 发起查询请求
    ObservationService
    ↓
    ObservationRepository
    ↓ 查询ClickHouse
    ObservationEntity列表
    ↓ 返回AI Engine
    AI Engine开始推理
13. Acceptance Criteria
    本规范通过验收需满足以下条件：

所有Java类设计符合规范，字段、方法、包路径完全对齐
ClickHouse/MySQL表设计符合规范，索引、分区、TTL合理
REST API设计符合规范，路径、参数、响应格式统一
调用链路完整，无缺失环节，完全对齐IM-000总纲要求
所有约束条件可落地，无矛盾或模糊描述
14. Coding Rules
    所有实现本规范的代码必须遵守以下规则：

严格按照本规范实现类、接口、数据库表，不允许私自修改字段、方法、表结构
Observation数据必须写入ClickHouse，不允许写入MySQL存储全量数据
所有Observation数据必须包含resourceId字段，不允许出现无归属的Observation
所有API返回的ObservationVO必须包含格式化的时间字符串，不允许直接返回时间戳
所有代码必须可测试、可验证，禁止TODO、空实现、Hard Code
15. Freeze Statement
    本规范自发布之日起冻结，属于IM层核心规范，任何修改必须经过以下流程：

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
未经过上述流程，任何人/AI不得修改本规范内容，所有Observation相关的工程实现必须对齐本规范。

16. Revision History
    版本	时间	修改内容	负责人
    1.0	2026-07-20	初始版本发布	AI Performance Engineer Team
17. Approval
    本规范已通过架构评审，正式生效。

18. Notes
    本规范完成后，后续IM-002 Resource Mapping、IM-003 Relationship & Topology Mapping等单项映射文档必须引用本规范，所有Observation相关的工程落地工作必须对齐本规范的映射关系。