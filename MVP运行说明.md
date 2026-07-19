# AI Performance Engineer MVP v1.0 运行与部署说明书

本说明书作为 **AI Performance Engineer** 首席架构师与 Java 技术负责人向研发管理层呈递的终期 MVP v1.0 物理交付大纲。
项目完全基于 **Java 21**、**Spring Boot 3.2.5** 规范开发，实现了从探针自举、MXBean JVM 核心捕获、高可用异步 http 抛传到 Spring API 接收以及多存储（MySQL/ClickHouse）规范落地的全链路闭环开发。

---

###  一、 核心数据库环境基础编排

为了确保环境无缝自愈和“一键启动”，特在下方配置好专为 Localhost 调试订制的 `docker-compose.yml` 环境包，其中集成了 **MySQL 8.0** 与 **ClickHouse 极速列式时序库**。

#### 1. docker-compose.yml 配置文件
请在项目根目录下物理创建 `docker-compose.yml` 文件：

```yaml
version: '3.8'

services:
  # Part A: MySQL 8.0 关系和元数据存储网关
  aipe-mysql:
    image: mysql:8.0.33
    container_name: aipe-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: aipe_metadata
    ports:
      - "3306:3306"
    volumes:
      - aipe-mysql-data:/var/lib/mysql
    networks:
      - aipe-network
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

  # Part B: ClickHouse 列式高频时序指标库
  aipe-clickhouse:
    image: clickhouse/clickhouse-server:23.8
    container_name: aipe-clickhouse
    restart: always
    ports:
      - "8123:8123"
      - "9000:9000"
    volumes:
      - aipe-clickhouse-data:/var/lib/clickhouse
    networks:
      - aipe-network
    ulimits:
      nofile:
        soft: 262144
        hard: 262144

volumes:
  aipe-mysql-data:
  aipe-clickhouse-data:

networks:
  aipe-network:
    driver: bridge
```

---

### ️ 二、 数据库初始化与自动化 DDL 脚本

MySQL 和 ClickHouse 容器启动成功后，请将物理文件 `aipe-backend/src/main/resources/aipe-schema.sql` 中的 DDL 指令一键拷入数据库中执行，完成以下表的物理架构初始化：

#### 1. MySQL (关系度量元数据)
- `t_user`：物理账户体系管理主表。
- `t_project`：多组织性能场景核算项目主表。
- `t_agent`：客户端探针实例 IP 寻址在线状态配置表。
- `t_resource_config`：压力注入配置（如并发线程数、.jmx 脚本源）。
- `t_pressure_session`：每次压力投射轨迹的运行生命期会话主表。

#### 2. ClickHouse (高速、高吞吐列式时序核心)
- `aipe_metrics.t_observation_data`：列式 **`MergeTree()`** 时域性能表，针对高并发时序特点，使用 category, agent_id, timestamp 进行主索引（`ORDER BY`），并将高精毫秒级时钟划分为按月大分区（`PARTITION BY toYYYYMM`）。
- `aipe_metrics.t_timeline_events`：**`ReplacingMergeTree()`** 探针崩溃/死锁等严重报警事件轴去重自愈存储。

---

###  三、 全模块编译与运行指令指南

在开始本流程前，请确保本地已配置好 **Java 21** 环境变量。

#### Step 1: 执行 Maven 全局编译与资源打包
如果环境内 `mvn` 命令处于全局变量中，请直接运行以下指令：
```bash
mvn clean package -DskipTests
```
*（该动作会在 `aipe-backend/target/` 下生成可以直接运行的 Spring BootFatJar 包：`aipe-backend-1.0-SNAPSHOT.jar`。）*

#### Step 2: 启动 Backend 时序接收网关服务端
在编译所得的后台目标路径，执行引导拉起：
```bash
java -jar aipe-backend/target/aipe-backend-1.0-SNAPSHOT.jar
```
- 服务端会绑定本地 **`8080`** 端口，并在控制台输出 Spring 核心就绪及 API 路由暴露指令。

#### Step 3: 配置并拉起 Client Agent 探针运行时
1. 点击并检查探针的全局本地自举属性文件：
   `aipe-agent/src/main/resources/aipe-agent.properties`
   配置后端关联端点：
   ```properties
   backend.url=http://localhost:8080
   ```
2. 启动探针（AgentBootstrap 为主入口）：
   ```bash
   java -cp aipe-agent/target/aipe-agent-1.0-SNAPSHOT.jar:aipe-common/target/aipe-common-1.0-SNAPSHOT.jar:aipe-connectors/connector-sdk/target/connector-sdk-1.0-SNAPSHOT.jar:aipe-connectors/connector-jvm/target/connector-jvm-1.0-SNAPSHOT.jar com.ai.performance.agent.AgentBootstrap
   ```
   *（启动后，控制台会输出 “AIPE Agent Runtime starting...”，客户端后台心跳守护 `HeartbeatService` 将每 10 秒向 Spring 网关发出脉搏，同时常驻线程循环将每 5 秒自动打印 `JvmConnector` 捕获的各项指标并上传。）*

---

### 🧪 四、 E2E 极速一键端到端闭环联合调测流程

为了向架构委员会和研发总监提供最快的验证手段，开发团队硬核集成了 **一键式 E2E 测试引导环境**。该测试在单虚拟机下全自动管理生命周期。

#### 核心调试运行命令
```bash
# 直接运行 Agent 模块下的闭环联调主调试类
java -cp "aipe-agent/target/*:aipe-common/target/*:aipe-connectors/connector-sdk/target/*:aipe-connectors/connector-jvm/target/*:aipe-backend/target/*" com.ai.performance.agent.E2ETestBootstrap
```

#### 控制台完全成功预演状态
1. **控制台输出 Stage 1**: 服务端 Spring Boot 网关开始初始化、内置 Tomcat 在 8080 端口自举。
2. **控制台输出 Stage 2**: 探针加载、读取 `aipe-agent.properties`、发现 `JvmConnector` 编译物存在。
3. **控制台输出 Stage 3 (时序管道连线成立)**:
   - 心跳拦截并输出：`[HEARTBEAT HANDSHAKE RECEIVED] -> Agent [AIPE-AGENT-DEV-001] is ALIVE.`
   - 采集器滴答输出 JVM 时域明细：`[METRIC CAPTURED] -> Category: JVM_BODY_MEMORY... (heapUsed=XXX, heapCommitted=XXX)`。
   - 检测高并发死锁并输出：`activeThreadCount=XX, deadlockedThreadCount=0`。
   - 瞬时 CPU 精准负荷反映：`jvmProcessCpuLoad=0.03 (3%%)`。
4. **控制台输出 Stage 4 (优雅销毁)**:
   - 输出 “Disposing E2E environment context resources. Clearing local threads...”。
   - 彻底关闭所有后台 HttpClient 的轮询池、停止并释放 Spring Tomcat `8080` 端口。
5. **控制台输出**: `Exit Code 0`，全链路打通测验宣告圆满大获全胜！
