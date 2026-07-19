# AI Performance Engineer Architecture Index


## 1. Project Vision


AI Performance Engineer


目标：

通过：

压测

↓

自动采集

↓

性能分析

↓

瓶颈定位

↓

优化建议

↓

再次验证

↓

知识沉淀


形成企业级AI性能工程平台。



---

# 2. Overall Architecture

             Control Plane


          aipe-backend


                |

                |

         Configuration


                |

                |

          Agent Runtime


                |

    ------------------------

    JVM Connector

    Linux Connector

    Redis Connector

    MySQL Connector


                |

                |

       Observation Pipeline


                |

                |

          Storage Layer


                |

                |

          AI Engine



---

# 3. Module Responsibility


## aipe-agent


职责：

运行在客户机器。


负责：

- Agent生命周期
- Connector加载
- 数据采集


禁止：

业务分析



---


## aipe-connectors


职责：

所有数据采集插件。


包含：

- JVM
- Linux
- Redis
- MySQL



---


## aipe-backend


职责：

控制面。


包含：

- Agent管理
- Scenario管理
- 配置管理
- 查询API



---


## aipe-common


职责：

公共模型。


包含：

- Observation
- Resource
- DTO
- Enum



---


# 4. Development Order


必须按照：

WP001

↓

WP002

↓

WP003

↓

WP004

↓

WP005

↓

WP006

↓

WP007

↓

WP008

↓

WP009

↓

WP010



禁止跳跃开发。



---

# 5. Coding Rules


必须读取：
DEVELOPMENT_RULES.md




必须读取：


对应WP Blueprint




禁止：

- Mock
- TODO
- 空实现
- 简化设计



---

# 6. Current MVP Boundary


第一阶段只实现：



Scenario

↓

JMeter

↓

Agent

↓

Connector

↓

Observation

↓

Storage




不提前实现：

- AI Root Cause
- Knowledge Graph
- Digital Twin


---

# 7. Domain Model Direction


核心对象：


Agent

Resource

Scenario

Observation

Timeline

Evidence

Topology

Knowledge



---

# 8. Future Roadmap


WP011+

进入：

Resource Model

Topology

AI Analysis

Evidence

Root Cause