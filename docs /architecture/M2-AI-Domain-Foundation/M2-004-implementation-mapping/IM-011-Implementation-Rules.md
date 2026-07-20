IM-011-Coding-Rule-Mapping.md
AI Performance Engineer
M2-004 Implementation Mapping
IM-011 Coding Rule Mapping
Version: v1.0
Status: Draft
Milestone: M2 – AI Domain Foundation

1. 设计原则
   全链路对齐：所有编码规则严格对齐IM-001~IM-010的全部规范，无冲突、无遗漏
   可落地核查：每条规则都有明确的合格/不合格标准，可直接作为Code Review依据
   无冗余实用：每条规则都有明确的落地价值，不搞虚的、花里胡哨的要求
   全流程覆盖：覆盖编码、分层、存储、测试、协作全流程，无盲区
2. 通用编码规范
   2.1 命名规范
   类型	要求	示例
   类名	严格对齐IM-002领域实体、枚举、接口名称，禁止自定义	正确：Resource、EvidenceRepository、ResourceStatus
   错误：Asset、EvidenceDao、ResourceState
   方法名	严格对齐IM-002领域服务、IM-006 API的方法名，禁止随意命名	正确：createResource、generateEvidence、queryTopology
   错误：addAsset、doAiReason、getTopo
   变量名	使用M2-005官方术语，禁止使用自定义术语	正确：resourceId、observationList、confidence
   错误：assetId、observeList、credibility
   包结构	严格对齐IM-001的包结构，禁止随意新增包路径	正确：com.aipe.domain.resource.entity
   错误：com.aipe.domain.asset.model
   2.2 代码风格
   所有实体类必须使用Lombok注解：@Data、@Builder、@NoArgsConstructor、@AllArgsConstructor
   所有实体类必须实现Serializable接口，必须包含无参构造函数
   所有字段不允许为NULL，必须有默认值，在声明时直接初始化
   代码缩进统一4个空格，禁止用Tab
   单行代码长度不超过120个字符，超过必须换行
   方法长度不超过50行，超过必须拆分
   2.3 异常处理
   禁止吞异常，所有异常必须统一封装为BusinessException或SystemException返回
   业务异常使用IM-006定义的错误码，系统异常统一使用5000错误码
   异常信息必须包含明确的定位信息：操作人、操作对象、异常原因
   禁止在循环中捕获异常，避免隐藏批量错误
   2.4 日志规范
   日志格式统一包含：traceId、模块名、操作人、操作内容、耗时、结果
   写操作必须打印INFO级别日志，异常操作必须打印ERROR级别日志
   ERROR级别日志必须触发告警，告警信息包含异常堆栈、影响范围
   禁止打印敏感信息，比如密码、token、用户隐私数据
3. 分层架构编码规范
   3.1 依赖约束（编译级别强制）
   层级	允许依赖	禁止依赖
   domain层	仅内部其他domain包、common层	所有外层包（service/api/infrastructure/connector等）
   service层	domain层、repository接口、common层	api层、infrastructure实现层、connector层
   api层	service层、common层	domain层、repository层、infrastructure层
   infrastructure层	所有层	无，但不允许实现业务逻辑
   connector/agent层	domain层、common层	service层、api层、互相依赖
   3.2 各层编码要求
   domain层：只定义实体、枚举、接口、事件，不实现任何业务逻辑
   service层：只实现跨领域业务编排，事务注解只加在写操作方法上，禁止包含远程调用、IO操作，避免大事务
   api层：只做参数校验、请求转发、响应封装，禁止实现业务逻辑
   infrastructure层：只做存储适配、工具实现、配置管理，禁止实现业务逻辑
   connector层：只做数据采集、格式转换、批量上报，禁止实现业务逻辑
4. 领域层编码规范
   4.1 实体编码要求
   所有字段必须在声明时初始化默认值，不允许为NULL
   枚举类必须实现getValue()方法，不允许直接用枚举名称做业务判断
   禁止在实体类中写业务逻辑、校验逻辑，所有校验调用对应Validator实现
   跨实体的关联关系通过ID关联，禁止直接引用其他实体对象
   4.2 领域服务编码要求
   接口定义在com.aipe.domain.{实体包}.service包下，实现类定义在com.aipe.service.impl包下
   方法签名严格对齐IM-002定义，禁止修改参数、返回值类型
   所有方法必须添加事务注解@Transactional(rollbackFor = Exception.class)
   4.3 校验器编码要求
   校验器定义在com.aipe.domain.{实体包}.validator包下，独立实现
   校验失败必须抛出BusinessException，附带明确的错误信息
   禁止在Service、实体类中写校验逻辑
5. 存储层编码规范
   5.1 MySQL编码要求
   所有查询必须带is_deleted = 0条件，禁止返回已删除数据
   所有更新操作必须校验version字段，版本不一致抛出OptimisticLockException
   禁止SELECT *，必须明确指定查询字段
   禁止在索引字段上使用函数，避免索引失效
   禁止大事务，单次事务SQL条数不超过1000条
   禁止直接拼接SQL，必须使用MyBatis-Plus等ORM框架
   5.2 ClickHouse编码要求
   禁止实现update、delete方法，只支持写入和查询
   所有查询必须带时间范围条件，禁止全表扫描
   写入必须批量，单次批量写入不少于1000条，禁止单条写入
   聚合查询优先使用物化视图，避免每次全量计算
   5.3 图数据库编码要求
   只存储关系数据，禁止存储其他业务数据
   路径查询默认最大深度5层，禁止超过10层的深查
   新增/变更关系时必须同步更新MySQL resource表的relationship_ids字段
   5.4 Redis编码要求
   缓存过期时间默认5分钟，最长不超过10分钟
   数据变更时必须主动删除对应缓存，禁止更新缓存
   禁止缓存大对象，单个缓存对象大小不超过1MB
6. API层编码规范
   所有API必须使用统一响应格式，返回Result<T>结构
   参数校验使用@Valid注解，禁止手动校验
   权限校验在网关层统一实现，API层禁止单独校验权限
   写操作必须记录审计日志，包含操作人、操作时间、操作内容、影响范围
   API路径、HTTP方法严格对齐IM-006定义，禁止随意修改
7. AI引擎编码规范
   所有推理步骤必须记录到reasoning_steps字段，不允许为空
   可信度计算逻辑必须透明，禁止黑盒计算
   输入输出字段严格对齐IM-002的Evidence、Knowledge、Recommendation实体定义，不允许多字段、少字段
   推理过程日志必须存入ClickHouse的execution_log表，保留6个月
8. Connector编码规范
   只实现数据采集、格式转换、批量上报，禁止实现业务逻辑
   上报接口调用失败最多重试3次，重试间隔依次为1s/2s/4s
   本地缓冲数据最多保留7天，自动清理
   禁止调用非采集相关的API，仅允许调用Observation批量上报接口、心跳接口
9. 数据库SQL开发规范
   表名、字段名统一小写，下划线分隔
   SQL关键字统一大写，字段名、表名统一小写
   禁止使用子查询，必须改写为JOIN查询
   联合查询必须使用索引，避免全表扫描
   ClickHouse SQL必须带分区键条件，避免跨分区查询
10. Git协作规范
    分支规范：
    main：生产环境分支，只有管理员可以合并
    dev：开发环境分支，开发完成后合并
    feature/IM-xxx：功能开发分支，从dev分支创建，开发完成后合并回dev
    hotfix/xxx：紧急修复分支，从main分支创建，修复后合并到main和dev
    commit信息规范：类型: IM-编号 描述，例如：feat: IM-003 新增resource表建表脚本
    PR规范：必须至少有1个其他开发者Review通过后才能合并，禁止直接提交到main分支
11. 测试规范
    核心领域服务、AI引擎、Repository实现类单元测试覆盖率≥80%
    所有API必须有接口测试，覆盖率100%
    状态流转逻辑必须有正向、逆向测试用例
    并发场景必须有乐观锁测试用例
    测试用例必须独立运行，禁止依赖其他测试用例的执行结果
12. 验收标准
    ✔ 所有规则都能对应到IM-001~IM-010的全部规范，无冲突、无遗漏
    ✔ 每条规则都有明确的合格/不合格标准，可直接作为Code Review依据
    ✔ 覆盖编码、分层、存储、测试、协作全流程，无盲区
    ✔ 无冗余规则，每条都有明确的落地价值，Rowboat可直接落地