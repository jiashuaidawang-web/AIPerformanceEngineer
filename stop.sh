#!/bin/zsh
# ============================================================
# stop.sh — 停止全部服务
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
        local count=0
        while kill -0 "$pid" 2>/dev/null && [ $count -lt 15 ]; do
            sleep 1
            count=$((count + 1))
        done
        if kill -0 "$pid" 2>/dev/null; then
            echo "[$name] force killing (PID $pid)..."
            kill -9 "$pid"
        fi
        echo "[$name] stopped ✓"
    else
        echo "[$name] not running (stale pidfile)"
    fi
    rm -f "$pidfile"
}

echo "Stopping all services..."
stop_one backend
stop_one config
stop_one agent
echo "All services stopped."
