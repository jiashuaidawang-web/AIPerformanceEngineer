#!/bin/zsh
# ============================================================
# status.sh — 查看服务状态
# ============================================================

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
PID_DIR="$BASE_DIR/.pids"

check_service() {
    local name=$1
    local port=$2
    local pidfile="$PID_DIR/${name}.pid"

    if [ -f "$pidfile" ]; then
        local pid=$(cat "$pidfile")
        if kill -0 "$pid" 2>/dev/null; then
            if [ -n "$port" ] && netstat -tlnp 2>/dev/null | grep -q ":$port "; then
                echo "[$name] RUNNING (PID $pid, port $port) ✓"
            else
                echo "[$name] RUNNING (PID $pid) ✓"
            fi
        else
            echo "[$name] STOPPED (stale pidfile)"
        fi
    else
        echo "[$name] STOPPED"
    fi
}

echo "Service Status:"
echo "==============="
check_service backend 8081
check_service config 8080
check_service agent ""
echo ""

# 检查 Agent 注册状态
if curl -s http://localhost:8081/api/v1/agents 2>/dev/null | grep -q "agent-"; then
    echo "Agent Registration:"
    curl -s http://localhost:8081/api/v1/agents | python3 -m json.tool 2>/dev/null || curl -s http://localhost:8081/api/v1/agents
fi
