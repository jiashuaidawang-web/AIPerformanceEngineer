#!/bin/zsh
# ============================================================
# start.sh — 启动服务
# 用法: ./start.sh          # 启动全部
#       ./start.sh agent    # 只启动 agent
#       ./start.sh backend  # 只启动 backend
#       ./start.sh config   # 只启动 config-manager
# ============================================================

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
PID_DIR="$BASE_DIR/.pids"
LOG_DIR="$BASE_DIR/logs"
mkdir -p "$PID_DIR" "$LOG_DIR"

declare -A APPS
APPS[agent]="aipe-agent-1.0.0-SNAPSHOT.jar"
APPS[backend]="aipe-backend-1.0.0-SNAPSHOT.jar"
APPS[config]="aipe-config-manager-1.0.0-SNAPSHOT.jar"

start_one() {
    local name=$1
    local jar=$2
    local pidfile="$PID_DIR/${name}.pid"
    if [ -f "$pidfile" ] && kill -0 $(cat "$pidfile") 2>/dev/null; then
        echo "[$name] already running (PID $(cat "$pidfile"))"
        return
    fi
    nohup java -jar "$BASE_DIR/$jar" > "$LOG_DIR/${name}.log" 2>&1 &
    echo $! > "$pidfile"
    echo "[$name] started (PID $!, log: $LOG_DIR/${name}.log)"
}

TARGETS=("$@")
[ $# -eq 0 ] && TARGETS=(agent backend config)

for name in "${TARGETS[@]}"; do
    if [ -z "${APPS[$name]}" ]; then
        echo "Unknown: $name (available: agent backend config)"
        continue
    fi
    start_one "$name" "${APPS[$name]}"
done
