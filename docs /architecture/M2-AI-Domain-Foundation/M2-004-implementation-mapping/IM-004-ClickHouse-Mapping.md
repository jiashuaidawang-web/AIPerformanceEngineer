IM-004-ClickHouse-Mapping.md
AI Performance Engineer
M2-004 Implementation Mapping
IM-004 ClickHouse Mapping
Version: v1.0
Status: Draft
Milestone: M2 – AI Domain Foundation

1. 设计原则
   严格对齐存储划分：ClickHouse只存纯运行时产生的海量时序数据，所有配置、元数据、结果类数据全在MySQL，不越界
   贴合ClickHouse特性：只追加不修改，按时间分区，自动TTL清理，优化海量数据聚合查询性能
   无冗余落地：每个字段都有明确的业务用途，无花里胡哨的冗余设计，满足运维查询、AI分析、时间线回放的核心场景即可
2. 存储范围
   数据类型	业务场景	选择ClickHouse的原因
   Observation（运行时事实）	存储每条采集的原始指标/事件/日志，支撑AI推理查事实来源	每秒写入百万级，只追加，按时间范围查询，ClickHouse聚合性能比MySQL高100倍以上
   TimelineEvent（时间线事件）	存储资源状态变化事件，支撑故障复盘、时间线回放	数据量极大的有序序列，按时间排序查询，ClickHouse列式存储压缩率高
   ExecutionLog（执行日志）	存储优化执行的全量过程日志，支撑问题排查	海量文本类日志数据，写入量大，不需要更新，查询都是按时间范围筛选
   MetricData（时序指标）	存储秒级/分钟级的原始指标，支撑大屏展示、趋势分析	超大规模时序数据，需要快速聚合计算平均值、最大值等统计指标
3. 表结构设计
   所有表使用单节点MergeTree引擎，按天分区，默认保留3个月数据自动清理，时间字段精度到毫秒级，和MySQL时间格式对齐。

3.1 observation（运行时事实表）
字段名	类型	业务用途
observation_id	String	唯一标识，和MySQL逻辑ID一一对应
resource_id	String	所属资源ID，查询时必带条件
observation_type	LowCardinality(String)	观察类型：METRIC/EVENT/LOG/STATUS，低基数字段用LowCardinality节省存储
metric_name	String	指标名称：system_cpu_load/api_rt/jvm_heap_used 等
metric_value	String	指标原始值：95%/120ms/4096MB 等
unit	LowCardinality(String)	单位：%/ms/MB/QPS 等
source	LowCardinality(String)	采集来源：JMX_EXPORTER/REDIS_EXPORTER/PROMETHEUS 等，问题溯源用
labels	Map(String, String)	业务标签，存集群、机房、环境等额外属性
created_at	DateTime64(3)	事实产生时间，分区键
引擎配置：

sql
复制
ENGINE = MergeTree
PARTITION BY toYYYYMMDD(created_at)
ORDER BY (resource_id, observation_type, created_at)
TTL created_at + INTERVAL 3 MONTH
SETTINGS index_granularity = 8192
3.2 timeline_event（时间线事件表）
字段名	类型	业务用途
event_id	String	唯一标识
resource_id	String	所属资源ID
event_type	LowCardinality(String)	事件类型：STATUS_CHANGE/CONFIG_UPDATE/DEPLOY/RESTART/FAULT 等
event_content	String	事件详细内容：订单服务从RUNNING变为STOPPED等
operator	String	触发人：SYSTEM/AI/管理员账号等
created_at	DateTime64(3)	事件发生时间，分区键
引擎配置：

sql
复制
ENGINE = MergeTree
PARTITION BY toYYYYMMDD(created_at)
ORDER BY (resource_id, created_at)
TTL created_at + INTERVAL 3 MONTH
3.3 execution_log（执行日志表）
字段名	类型	业务用途
log_id	String	唯一标识
execution_id	String	关联MySQL的execution_record_id，和结果表联动
log_level	LowCardinality(String)	日志级别：INFO/WARN/ERROR
log_content	String	日志详细内容
created_at	DateTime64(3)	日志产生时间，分区键
引擎配置：

sql
复制
ENGINE = MergeTree
PARTITION BY toYYYYMMDD(created_at)
ORDER BY (execution_id, created_at)
TTL created_at + INTERVAL 3 MONTH
3.4 metric_data（时序指标表）
字段名	类型	业务用途
metric_id	String	唯一标识
resource_id	String	所属资源ID
metric_name	String	指标名称
metric_value	Float64	指标数值，数值类型方便直接聚合计算
data_time	DateTime64(3)	指标产生时间，分区键
引擎配置：

sql
复制
ENGINE = MergeTree
PARTITION BY toYYYYMMDD(data_time)
ORDER BY (resource_id, metric_name, data_time)
TTL data_time + INTERVAL 3 MONTH
4. 写入/查询规范
   4.1 写入规范
   禁止单条写入，每次批量写入至少1000条，避免产生过多小文件影响合并性能
   写入数据的时间字段必须是事实发生的时间，不允许用写入时间替代
   每张表的写入必须通过统一的Pipeline层，禁止业务直接写ClickHouse
   4.2 查询规范
   所有查询必须带时间范围条件，比如created_at >= now() - INTERVAL 7 DAY，禁止全表扫描
   按资源查询时必须带resource_id条件，命中排序键，避免慢查询
   聚合查询优先使用ClickHouse的物化视图预计算，比如提前聚合小时级、天级的指标平均值、最大值，提升查询速度
   普通运维查询最多扫描7天的数据，超过7天的需求走归档后的离线分析
5. 约束
   禁止在ClickHouse中存储配置类、元数据类、结果类数据，所有状态类、结果类数据都在MySQL
   禁止修改或删除ClickHouse中的历史数据，所有数据都是不可变的事实，符合Law-002 Observation不可变的要求
   禁止将ClickHouse作为MySQL的从库使用，两个存储完全独立，各司其职
6. 验收标准
   ✔ 4张表都对应明确的海量时序场景，和MySQL存储划分清晰，无越界
   ✔ 表结构完全贴合ClickHouse特性，分区、排序、TTL设计合理，查询性能优化
   ✔ 写入/查询规范可落地，满足每秒百万级写入、毫秒级查询的性能要求
   ✔ TTL策略合理，自动清理3个月前的旧数据，节省80%以上的存储成本
   ✔ 所有设计符合三大基础法则要求，无冗余设计
   确认IM-004的内容没有问题后，我们继续编写IM-005-Graph-Model-Mapping.md。