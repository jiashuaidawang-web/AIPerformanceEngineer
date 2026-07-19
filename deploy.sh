#!/bin/zsh
# ============================================================
# AI Performance Engineer — 一键部署脚本 (VM)
# 功能：环境检查 → 建库建表 → 启动服务
# ============================================================

set -e

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
PID_DIR="$BASE_DIR/.pids"
LOG_DIR="$BASE_DIR/logs"
mkdir -p "$PID_DIR" "$LOG_DIR"

# ======================== 配置 ========================
MYSQL_HOST="localhost"
MYSQL_PORT="3306"
MYSQL_USER="root"
MYSQL_PASS="astock_root"
MYSQL_DB="aipe_metadata"

CK_HOST="localhost"
CK_PORT="8123"
CK_USER="default"
CK_PASS="pamirs@123"
CK_DB="metric_observation"

BACKEND_JAR="aipe-backend/target/aipe-backend-1.0.0-SNAPSHOT.jar"
CONFIG_JAR="aipe-config-manager/target/aipe-config-manager-1.0.0-SNAPSHOT.jar"
AGENT_JAR="aipe-agent/target/aipe-agent-1.0.0-SNAPSHOT.jar"

# ======================== 颜色 ========================
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_info()  { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# ======================== 环境检查 ========================
check_command() {
    if ! command -v "$1" &>/dev/null; then
        log_error "命令不存在: $1"
        exit 1
    fi
}

check_mysql() {
    log_info "检查 MySQL 连通性..."
    if mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u "$MYSQL_USER" -p"$MYSQL_PASS" -e "SELECT 1" &>/dev/null; then
        log_info "MySQL 连通 ✓"
        return 0
    else
        log_error "MySQL 连接失败: $MYSQL_HOST:$MYSQL_PORT"
        return 1
    fi
}

check_clickhouse() {
    log_info "检查 ClickHouse 连通性..."
    if docker exec astock-clickhouse clickhouse-client --query "SELECT 1" &>/dev/null; then
        log_info "ClickHouse 连通 ✓"
        return 0
    else
        log_error "ClickHouse 连接失败"
        return 1
    fi
}

# ======================== 建库建表 ========================
setup_mysql() {
    log_info "初始化 MySQL 数据库..."
    mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u "$MYSQL_USER" -p"$MYSQL_PASS" <<EOF
CREATE DATABASE IF NOT EXISTS $MYSQL_DB;
USE $MYSQL_DB;

CREATE TABLE IF NOT EXISTS resource (
    id VARCHAR(64) PRIMARY KEY,
    resource_type VARCHAR(32) NOT NULL,
    host VARCHAR(256),
    port INT,
    labels JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS agent (
    id VARCHAR(64) PRIMARY KEY,
    server_id VARCHAR(64),
    status VARCHAR(16),
    last_heartbeat TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS connector (
    id VARCHAR(64) PRIMARY KEY,
    agent_id VARCHAR(64),
    connector_type VARCHAR(32),
    target_resource VARCHAR(256),
    status VARCHAR(16),
    config JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (agent_id) REFERENCES agent(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS observation_metadata (
    id VARCHAR(64) PRIMARY KEY,
    agent_id VARCHAR(64),
    connector_type VARCHAR(32),
    event_time BIGINT NOT NULL,
    receive_time BIGINT NOT NULL,
    metric_count INT DEFAULT 0,
    state VARCHAR(16),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_agent_time (agent_id, event_time),
    INDEX idx_state (state)
);

CREATE TABLE IF NOT EXISTS config_version (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    agent_id VARCHAR(64) NOT NULL,
    config_version VARCHAR(64) NOT NULL,
    connectors JSON,
    properties JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_agent (agent_id)
);

CREATE TABLE IF NOT EXISTS deployment_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    deployment_id VARCHAR(64) NOT NULL,
    agent_id VARCHAR(64),
    agent_type VARCHAR(32),
    target VARCHAR(256),
    status VARCHAR(16),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_agent (agent_id)
);

CREATE TABLE IF NOT EXISTS audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operator VARCHAR(64) DEFAULT 'admin',
    operation VARCHAR(128) NOT NULL,
    target VARCHAR(256),
    detail TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_operation (operation),
    INDEX idx_created (created_at)
);
EOF
    log_info "MySQL 初始化完成 ✓"
}

setup_clickhouse() {
    log_info "初始化 ClickHouse 数据库..."
    docker exec astock-clickhouse clickhouse-client --query "CREATE DATABASE IF NOT EXISTS $CK_DB"
    docker exec astock-clickhouse clickhouse-client --database "$CK_DB" --query "
    CREATE TABLE IF NOT EXISTS metric_observation (
        id UUID DEFAULT generateUUIDv4(),
        timestamp DateTime DEFAULT now(),
        resource_id String,
        resource_type String DEFAULT 'HOST',
        metric_name String,
        metric_value Float64,
        labels String DEFAULT ''
    ) ENGINE = MergeTree()
    PARTITION BY toYYYYMM(timestamp)
    ORDER BY (resource_id, metric_name, timestamp);
    "
    log_info "ClickHouse 初始化完成 ✓"
}

# ======================== 启动服务 ========================
start_service() {
    local name=$1
    local jar=$2
    local port=$3
    local pidfile="$PID_DIR/${name}.pid"

    # 检查端口是否被占用
    if netstat -tlnp 2>/dev/null | grep -q ":$port "; then
        log_warn "端口 $port 已被占用，跳过启动 $name"
        return
    fi

    # 检查 JAR 是否存在
    if [ ! -f "$BASE_DIR/$jar" ]; then
        log_error "JAR 文件不存在: $jar"
        log_error "请先执行: mvn clean install"
        return
    fi

    nohup java -jar "$BASE_DIR/$jar" > "$LOG_DIR/${name}.log" 2>&1 &
    local pid=$!
    echo $pid > "$pidfile"
    log_info "$name 启动中 (PID $pid, port $port)..."
}

wait_for_port() {
    local port=$1
    local timeout=30
    local count=0
    while ! netstat -tlnp 2>/dev/null | grep -q ":$port "; do
        sleep 1
        count=$((count + 1))
        if [ $count -ge $timeout ]; then
            log_error "端口 $port 启动超时 (${timeout}s)"
            return 1
        fi
    done
    return 0
}

# ======================== 主流程 ========================
main() {
    echo "============================================"
    echo "  AI Performance Engineer — 一键部署"
    echo "============================================"
    echo ""

    # 1. 检查命令
    log_info "检查依赖命令..."
    check_command java
    check_command mysql
    check_command docker
    check_command netstat
    log_info "依赖检查通过 ✓"
    echo ""

    # 2. 检查连通性
    check_mysql || exit 1
    check_clickhouse || exit 1
    echo ""

    # 3. 建库建表
    setup_mysql
    setup_clickhouse
    echo ""

    # 4. 启动服务
    log_info "启动服务..."
    start_service backend "$BACKEND_JAR" 8081
    start_service config "$CONFIG_JAR" 8080
    start_service agent "$AGENT_JAR" 0
    echo ""

    # 5. 等待启动
    log_info "等待服务启动..."
    wait_for_port 8081 && log_info "Backend (8081) 启动成功 ✓"
    wait_for_port 8080 && log_info "Config Manager (8080) 启动成功 ✓"
    echo ""

    # 6. 验证
    sleep 3
    log_info "验证服务..."
    if curl -s http://localhost:8081/api/v1/agents &>/dev/null; then
        log_info "Backend API 正常 ✓"
    else
        log_warn "Backend API 未响应，请检查日志: $LOG_DIR/backend.log"
    fi

    if curl -s http://localhost:8080/api/v1/agents &>/dev/null; then
        log_info "Config Manager API 正常 ✓"
    else
        log_warn "Config Manager API 未响应，请检查日志: $LOG_DIR/config.log"
    fi

    echo ""
    echo "============================================"
    echo "  部署完成"
    echo "============================================"
    echo ""
    echo "服务地址:"
    echo "  Backend:         http://localhost:8081"
    echo "  Config Manager:  http://localhost:8080"
    echo "  Agent:           运行中 (采集周期 30s)"
    echo ""
    echo "常用命令:"
    echo "  查看日志:  tail -f $LOG_DIR/<name>.log"
    echo "  停止全部:  $BASE_DIR/stop.sh"
    echo "  重启服务:  $BASE_DIR/restart.sh"
    echo "  Agent注册: curl http://localhost:8081/api/v1/agents"
    echo "  Observation: curl 'http://localhost:8081/api/v1/observations/latest?resource_id=jvm-local&limit=10'"
    echo ""
}

main "$@"
