WP009 是从 MVP 技术能力进入 企业可交付产品能力 的关键 Work Package。

前面 WP001~WP008 已经完成：

Agent Runtime

↓

Connector Framework

↓

JVM/Linux/Redis/MySQL采集

↓

Observation Pipeline

↓

Storage Layer

但是企业客户不会接受：

每台服务器 SSH 上去修改配置文件，启动 Agent。

真实企业环境：

几百台应用服务器
多个 Kubernetes 集群
多套生产环境
多个 Redis/MySQL 集群
不同业务线
不同权限

所以必须增加：

Configuration & Deployment Manager

解决：

如何让一个企业管理成百上千个 Agent。

核心目标：

中央控制面(Control Plane)

              |

              |

       Agent Fleet

              |

    Connector Runtime
AI Performance Engineer
WP009-Configuration-Deployment-Manager Blueprint v1.0
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
- WP001 Agent Runtime
- WP002 Connector SDK
- WP007 Observation Pipeline
- WP008 Storage Layer


RequiredBy:
- WP010 Resource Model
- WP011 Topology Model
- WP018 Root Cause Engine
- Enterprise Deployment


EstimatedJavaFiles:
70


EstimatedWorkload:
12 Days


Blueprint Template:
01-Blueprint-Template.md v1.0
1. Goal（目标）
   1.1 Purpose

建立 AI Performance Engineer 企业级配置和部署管理能力。

负责：

Agent注册
Agent生命周期管理
Connector配置管理
配置下发
Agent状态管理
多环境管理
1.2 Capability Added

Before:

Agent

+
本地配置文件

+
手工启动

After:

Control Plane

        |

        |

Agent Registry

        |

        |

Remote Configuration

        |

        |

Agent Runtime
1.3 Core Principle

采用：

控制面 + 数据面设计。

类似：

Kubernetes：

API Server

    |

Controller

    |

Node Agent

AI Performance Engineer：

Performance Control Plane

    |

Agent Manager

    |

Performance Agent
1.4 Scope

包含：

Agent Registry
Agent Heartbeat
Config Center
Connector Config
Deployment Package
Version Management

不包含：

Kubernetes Operator
自动扩容
自动修复
2. Acceptance Criteria（验收标准）
   2.1 Functional Acceptance

必须支持：

□ Agent注册

□ Agent心跳

□ Agent状态查询

□ Connector配置下发

□ 动态启停Connector

□ 配置版本管理

□ 配置回滚
2.2 Technical Acceptance
□ Agent无需人工修改配置

□ 支持1000+ Agent

□ 配置安全传输

□ 配置灰度发布

□ 配置变更审计
2.3 Integration Acceptance

完整链路：

Admin Console

↓

Configuration Manager

↓

Agent Gateway

↓

Agent Runtime

↓

Connector Manager
3. Package List（包结构）
   com.aipe.config


├── controller

├── agent

├── registry

├── heartbeat

├── config

├── deployment

├── version

├── security

├── audit

├── client

└── support

Package Responsibility
Package	职责
controller	管理API
agent	Agent管理
registry	注册中心
heartbeat	心跳
config	配置
deployment	部署
version	版本
security	安全
audit	审计
4. Class List（类清单）
   4.1 AgentManager

Package:

agent

职责：

Agent生命周期管理。

负责：

创建Agent
删除Agent
查询状态
4.2 AgentRegistry

Package:

registry

职责：

保存Agent注册信息。

数据：

agentId

hostname

ip

version

status

lastHeartbeat
4.3 AgentHeartbeatService

Package:

heartbeat

职责：

处理Agent心跳。

4.4 AgentHeartbeatScheduler

Package:

heartbeat

职责：

检查Agent存活。

4.5 ConfigurationManager

Package:

config

职责：

配置管理。

管理：

Agent配置
Connector配置
4.6 ConnectorConfig

Package:

config

职责：

Connector运行配置。

例如：

Redis：

host:
port:
password:
interval:

MySQL：

jdbc:
username:
password:
4.7 ConfigPublisher

Package:

config

职责：

发布配置。

4.8 ConfigVersionManager

Package:

version

职责：

配置版本。

支持：

v1

v2

rollback
4.9 DeploymentManager

Package:

deployment

职责：

部署管理。

支持：

Linux Agent
Docker Agent
Kubernetes Agent
4.10 AgentCommandService

Package:

client

职责：

向Agent发送命令。

例如：

START_CONNECTOR

STOP_CONNECTOR

RELOAD_CONFIG
4.11 AuditService

Package:

audit

职责：

记录：

谁修改了什么。

5. Method List（方法清单）
   AgentManager
   registerAgent(AgentInfo agent)

注册Agent
removeAgent(String agentId)

删除Agent
getAgent(String agentId)

查询Agent
AgentHeartbeatService
heartbeat(HeartbeatRequest request)

处理Agent心跳
ConfigurationManager
saveConfig(Config config)

保存配置
publishConfig(String agentId)

发布配置
rollback(String version)

回滚配置
ConfigPublisher
push(
agentId,
configVersion
)

推送配置
DeploymentManager
deployAgent(
DeploymentRequest request
)

部署Agent
AgentCommandService
sendCommand(
agentId,
command
)

发送控制命令
AuditService
record(
operation
)

记录操作
6. Dependency（依赖关系）
   6.1 Internal Dependency
   WP001 Agent Runtime

        |

WP009 Configuration Manager

        |

WP002 Connector SDK

        |

WP003~WP006 Connector
6.2 External Dependency
依赖	用途
MySQL	配置数据
Redis	缓存
WebSocket/gRPC	通信
JWT	认证
6.3 Dependency Rule

Agent：

不能主动访问数据库。

只能：

Agent

↓

Control Plane API
7. Physical File List（物理文件清单）
   aipe-config-manager/


src/main/java/com/aipe/config/controller/AgentController.java


src/main/java/com/aipe/config/agent/AgentManager.java


src/main/java/com/aipe/config/registry/AgentRegistry.java


src/main/java/com/aipe/config/heartbeat/AgentHeartbeatService.java


src/main/java/com/aipe/config/heartbeat/AgentHeartbeatScheduler.java


src/main/java/com/aipe/config/config/ConfigurationManager.java


src/main/java/com/aipe/config/config/ConnectorConfig.java


src/main/java/com/aipe/config/config/ConfigPublisher.java


src/main/java/com/aipe/config/version/ConfigVersionManager.java


src/main/java/com/aipe/config/deployment/DeploymentManager.java


src/main/java/com/aipe/config/client/AgentCommandService.java


src/main/java/com/aipe/config/audit/AuditService.java


src/main/resources/application.yml
8. Sequence Diagram（时序图）
   Agent注册
   Agent启动


↓

Register API


↓

AgentRegistry


↓

保存Agent信息


↓

返回AgentId


↓

Agent进入RUNNING
配置下发
Admin

↓

ConfigurationManager

↓

ConfigPublisher

↓

Agent Gateway

↓

Agent Runtime

↓

ConnectorManager

↓

Reload Connector
Connector动态启动
Command

↓

AgentCommandService

↓

Agent

↓

ConnectorManager

↓

RedisConnector.start()
9. State Machine（状态机）
   Agent生命周期
   CREATED


↓

REGISTERING


↓

ONLINE


↓

RUNNING


↓

OFFLINE


↓

REMOVED

异常：

ERROR
Configuration生命周期
DRAFT


↓

PUBLISHED


↓

DELIVERING


↓

APPLIED


↓

ACTIVE


↓

ROLLBACK
10. Implementation Constraints（实现约束）
    10.1 Must Implement

必须实现：

Agent注册协议：

{
"agentId":"",
"hostname":"",
"ip":"",
"version":""
}

Heartbeat：

周期：

30s

Config：

必须支持：

connector:
redis:
enabled:true
interval:5s

Command：

至少支持：

START_CONNECTOR

STOP_CONNECTOR

RELOAD_CONFIG
10.2 Forbidden

禁止：

每台机器手改配置

SSH批量执行

无版本配置

无审计

无回滚
10.3 Engineering Rules

必须：

配置版本化
灰度发布
权限控制
操作审计
Agent自动恢复
11. Test & Verification（测试与验证）
    11.1 Build
    mvn clean install
    11.2 Test Scenario

部署：

10个Agent

配置：

JVM Connector

Linux Connector

Redis Connector
11.3 Verification

检查：

Agent列表

Heartbeat状态

Connector状态

Config版本
11.4 Expected Result

控制台显示：

Agent:

online 10


Connector:

JVM RUNNING

Linux RUNNING

Redis RUNNING


Config:

version=3
11.5 Troubleshooting

Agent离线：

检查：

1. 网络

2. 心跳线程

3. Token

4. Gateway

5. Agent日志
   END

WP009-Configuration-Deployment-Manager v1.0


---

# 当前 MVP 架构完成度

现在：

             Control Plane


         WP009 Config Manager

                |
                |
                ↓


             Agent Fleet


                |
 ---------------------------------

JVM      Linux      Redis      MySQL

                |

      Observation Pipeline

                |

          Storage Layer

---

下一步：

## WP010 Resource Model

这是非常关键的一章。

因为现在系统知道：

- 有一个 JVM 指标
- 有一个 Redis 指标
- 有一个 MySQL 指标

但是不知道：

> 这些指标属于谁？


WP010 会建立：


Host

Application

Instance

Cluster

Database

Middleware

Resource Identity

Resource Relationship


它会成为后面：

**Topology（拓扑）、Evidence（证据链）、AI根因分析的核心地基。**