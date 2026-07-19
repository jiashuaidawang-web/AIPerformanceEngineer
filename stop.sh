#!/bin/zsh
# ============================================================
# stop.sh — 停止服务
# 用法: ./stop.sh          # 停止全部
#       ./stop.sh agent    # 只停止 agent
#       ./stop.sh backend  # 只停止 backend
#       ./stop.sh config   # 只停止 config-manager
# ============================================================

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
PID_DIR="$BASE_DIR/.pids"

stop_one() {
    local name=$1
    local pidfile="$PID_DIR/${name}.pid"
    if [ ! -f "$pidfile" ]; then
        echo "[$name] not running (no pidfile)"
        return
    fi
    local pid=$(cat "$pidfile")
    if kill -0 "$pid" 2>/dev/null; then
        echo "[$name] stopping (PID $pid)..."
        kill "$pid"
        # Wait for process to exit
        local count=0
        while kill -0 "$pid" 2>/dev/null && [ $count -lt 15 ]; do
            sleep 1
            count=$((count + 1))
        done
        if kill -0 "$pid" 2>/dev/null; then
            echo "[$name] force killing (PID $pid)..."
            kill -9 "$pid"
        fi
        echo "[$name] stopped"
    else
        echo "[$name] not running (stale pidfile)"
    fi
    rm -f "$pidfile"
}

TARGETS=("$@")
[ $# -eq 0 ] && TARGETS=(agent backend config)

for name in "${TARGETS[@]}"; do
    stop_one "$name"
done
