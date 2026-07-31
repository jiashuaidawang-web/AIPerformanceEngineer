# AI Performance Engineer - Docker Compose 部署指南

> **文档版本**: v1.0 | 2026-07-31
> **适用**: 开发/测试环境快速部署、POC 演示、单节点生产

---

## 目录

1. [系统要求](#1-系统要求)
2. [快速开始](#2-快速开始)
3. [服务架构](#3-服务架构)
4. [配置说明](#4-配置说明)
5. [数据持久化](#5-数据持久化)
6. [运维操作](#6-运维操作)
7. [故障排查](#7-故障排查)
8. [生产环境建议](#8-生产环境建议)

---

## 1. 系统要求

### 1.1 最低配置

| 资源 | 要求 | 说明 |
|------|------|------|
| CPU | 4 核 | 8 个微服务 + 数据库 |
| 内存 | 8 GB | 建议 16 GB |
| 磁盘 | 50 GB | 数据持久化 |
| 网络 | 出向 443 | Agent → Platform |

### 1.2 软件依赖

| 软件 | 版本 | 必须 |
|------|------|------|
| Docker | 20.10+ | ✅ |
| Docker Compose | 2.0+ | ✅ |
| JDK | 1.8+ (构建时) | ✅ |
| Maven | 3.6+ (构建时) | ✅ |

---

## 2. 快速开始

### 2.1 一键部署 (3 步)

```bash
# 1. 克隆项目
git clone https://github.com/jiashuaidawang-web/AIPerformanceEngineer.git
cd AIPerformanceEngineer

# 2. 构建所有模块
mvn clean package -DskipTests

# 3. 启动全部服务
cd docker
docker-compose up -d

# 4. 等待启动完成 (约 2 分钟)
docker-compose logs -f

# 5. 访问 Dashboard
open http://localhost
```

### 2.2 验证服务

```bash
# 查看所有服务状态
docker-compose ps

# 查看健康状态
docker-compose exec mysql mysqladmin ping
docker-compose exec clickhouse wget --spider -q http://localhost:8123/ping
docker-compose exec redis redis-cli ping

# 查看 API
curl http://localhost:8081/api/v1/agents
curl http://localhost:8082/api/v1/resources
curl http://localhost:8085/api/v1/timelines?resource_id=order-svc-001&metric_name=cpu.usage&start_time=0&end_time=9999999999999
```

---

## 3. 服务架构

### 3.1 服务清单

| 服务名 | 端口 | 镜像 | 说明 |
|--------|------|------|------|
| **基础设施** |
| mysql | 3306 | mysql:8.0.33 | 元数据存储 |
| clickhouse | 8123 | clickhouse:23.8 | 时序数据 |
| redis | 6379 | redis:7-alpine | 缓存 (可选) |
| **Domain Engine** |
| resource | 8082 | aipe/resource:1.0.0 | 资源管理 |
| observation | 8083 | aipe/observation:1.0.0 | 观测数据 |
| relationship | 8084 | aipe/relationship:1.0.0 | 关系管理 |
| timeline | 8085 | aipe/timeline:1.0.0 | 时序查询 |
| evidence | 8086 | aipe/evidence:1.0.0 | 证据推理 |
| knowledge | 8087 | aipe/knowledge:1.0.0 | 知识沉淀 |
| recommendation | 8088 | aipe/recommendation:1.0.0 | 优化推荐 |
| execution | 8089 | aipe/execution:1.0.0 | 执行记录 |
| **控制面** |
| backend | 8081 | aipe/backend:1.0.0 | API 网关 |
| config-manager | 8080 | aipe/config-manager:1.0.0 | Agent 管理 |
| **前端** |
| dashboard | 80 | aipe/dashboard:1.0.0 | Web UI |

### 3.2 网络架构

```
┌─────────────────────────────────────────────────────────────────────┐
│  Docker Compose Network (aipe-network)                               │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  Dashboard (Nginx :80)                                       │    │
│  │  ├─ /         → 前端静态资源                                  │    │
│  │  └─ /api/v1/* → 代理到各 Engine                               │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                              │                                       │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  Backend (8081)                                              │    │
│  │  ├─ /api/v1/observations/batch ← Agent 上报                  │    │
│  │  ├─ /api/v1/agents/register ← Agent 注册                     │    │
│  │  └─ /api/v1/agents/{id}/heartbeat ← 心跳                     │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                              │                                       │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  8 个 Domain Engine (:8082-:8089)                            │    │
│  │  ├─ resource, observation, relationship, timeline            │    │
│  │  ├─ evidence, knowledge, recommendation, execution          │    │
│  │  └─ 互相独立，可单独扩缩容                                    │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                              │                                       │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  存储层                                                      │    │
│  │  ├─ MySQL (:3306) ← 元数据                                  │    │
│  │  ├─ ClickHouse (:8123) ← 时序数据                           │    │
│  │  └─ Redis (:6379) ← 缓存 (可选)                             │    │
│  └─────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 4. 配置说明

### 4.1 环境变量 (.env)

```bash
# 平台配置
ENV=prod
TIMEZONE=Asia/Shanghai

# MySQL
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=aipe_metadata

# ClickHouse
CLICKHOUSE_USER=default
CLICKHOUSE_PASSWORD=

# Redis
REDIS_PASSWORD=

# Agent
AGENT_ID=agent-docker-001
SERVER_ID=docker-server
BACKEND_URL=http://backend:8081
REDIS_ENABLED=false
MYSQL_ENABLED=false
```

### 4.2 端口映射

| 宿主机端口 | 容器端口 | 服务 | 说明 |
|-----------|---------|------|------|
| 80 | 80 | dashboard | Dashboard 入口 |
| 8080 | 8080 | config-manager | 配置管理 API |
| 8081 | 8081 | backend | Backend API |
| 8082-8089 | 8082-8089 | 8 个 Engine | Domain 服务 |
| 3306 | 3306 | mysql | MySQL |
| 8123 | 8123 | clickhouse | ClickHouse HTTP |
| 9000 | 9000 | clickhouse | ClickHouse Native |
| 6379 | 6379 | redis | Redis |

### 4.3 数据卷

| 卷名 | 容器路径 | 说明 |
|------|---------|------|
| mysql-data | /var/lib/mysql | MySQL 数据 |
| clickhouse-data | /var/lib/clickhouse | ClickHouse 数据 |
| redis-data | /data | Redis 持久化 |

---

## 5. 数据持久化

### 5.1 备份

```bash
# MySQL 备份
docker-compose exec mysql mysqldump -uroot -proot aipe_metadata > backup_$(date +%Y%m%d).sql

# ClickHouse 备份
docker-compose exec clickhouse clickhouse-client --query="BACKUP DATABASE metric_observation TO Disk('backups', 'backup_$(date +%Y%m%d).zip')"

# 或导出数据
docker-compose exec clickhouse clickhouse-client --query="SELECT * FROM metric_observation.observation_fact FORMAT CSV" > observations_backup.csv
```

### 5.2 恢复

```bash
# MySQL 恢复
cat backup.sql | docker-compose exec -T mysql mysql -uroot -proot aipe_metadata

# ClickHouse 恢复
docker-compose exec clickhouse clickhouse-client --query="RESTORE DATABASE metric_observation FROM Disk('backups', 'backup.zip')"
```

---

## 6. 运维操作

### 6.1 启动/停止

```bash
# 启动全部
docker-compose up -d

# 启动单个服务
docker-compose up -d mysql clickhouse

# 停止全部
docker-compose down

# 停止并删除数据 (危险!)
docker-compose down -v

# 重启单个服务
docker-compose restart resource
```

### 6.2 查看日志

```bash
# 全部日志
docker-compose logs -f

# 单个服务日志
docker-compose logs -f resource
docker-compose logs -f backend --tail=100

# Agent 日志
docker-compose logs -f agent
```

### 6.3 扩缩容

```bash
# 横向扩容 (以 resource 为例)
docker-compose up -d --scale resource=3

# 注意: 需要负载均衡支持
```

### 6.4 更新部署

```bash
# 1. 拉取最新代码
git pull

# 2. 重新构建
mvn clean package -DskipTests

# 3. 重建镜像
docker-compose build

# 4. 滚动更新
docker-compose up -d --no-deps --build resource
docker-compose up -d --no-deps --build observation
# ... 逐个更新
```

---

## 7. 故障排查

### 7.1 服务无法启动

```bash
# 查看所有容器状态
docker-compose ps -a

# 查看启动日志
docker-compose logs mysql
docker-compose logs clickhouse

# 常见问题:
# 1. 端口被占用 → 修改端口映射
# 2. 内存不足 → 增加 Docker 内存限制
# 3. 磁盘空间不足 → 清理 docker system prune
```

### 7.2 健康检查失败

```bash
# 手动检查
docker-compose exec mysql mysqladmin ping
docker-compose exec clickhouse wget --spider -q http://localhost:8123/ping

# 查看健康状态
docker inspect aipe-mysql --format='{{.State.Health.Status}}'
docker inspect aipe-clickhouse --format='{{.State.Health.Status}}'
```

### 7.3 数据采集异常

```bash
# Agent 无法连接 Backend
docker-compose logs agent | grep ERROR

# ClickHouse 写入失败
docker-compose logs backend | grep ClickHouse

# 无数据
docker-compose exec clickhouse clickhouse-client --query="SELECT count() FROM metric_observation.observation_fact"
```

---

## 8. 生产环境建议

### 8.1 使用生产覆盖配置

```bash
docker-compose -f docker-compose.yml -f docker-compose.override.yml up -d
```

### 8.2 资源限制

```yaml
# docker-compose.override.yml
services:
  mysql:
    environment:
      innodb_buffer_pool_size: 4G
    deploy:
      resources:
        limits:
          memory: 8G

  clickhouse:
    deploy:
      resources:
        limits:
          memory: 16G
```

### 8.3 SSL/TLS

```nginx
# nginx.conf 添加
server {
    listen 443 ssl;
    ssl_certificate /etc/nginx/certs/fullchain.pem;
    ssl_certificate_key /etc/nginx/certs/privkey.pem;
    # ...
}
```

### 8.4 监控集成

```yaml
# 添加 Prometheus + Grafana
services:
  prometheus:
    image: prom/prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml

  grafana:
    image: grafana/grafana
    ports:
      - "3000:3000"
```

### 8.5 高可用 (K8s)

生产环境建议使用 Kubernetes:

```bash
# 一键部署到 K8s
kubectl apply -f k8s/

# 或使用 Helm
helm install aipe ./helm-chart
```

---

## 附录: 常用命令速查

```bash
# === 部署 ===
docker-compose up -d                    # 启动
docker-compose down                     # 停止
docker-compose down -v                  # 停止并删除数据
docker-compose build                    # 重建镜像

# === 日志 ===
docker-compose logs -f                  # 全部日志
docker-compose logs -f backend          # 单个服务
docker-compose logs --tail=100          # 最后 100 行

# === 状态 ===
docker-compose ps                       # 容器状态
docker stats                            # 资源使用

# === 调试 ===
docker-compose exec mysql mysql -uroot -proot aipe_metadata
docker-compose exec clickhouse clickhouse-client
docker-compose exec backend sh

# === 清理 ===
docker system prune                     # 清理未使用资源
docker volume prune                     # 清理未使用卷
```

---

*文档生成时间: 2026-07-31*
