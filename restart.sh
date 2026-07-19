#!/bin/zsh
# ============================================================
# restart.sh — 重启服务
# 用法: ./restart.sh          # 重启全部
#       ./restart.sh agent    # 只重启 agent
# ============================================================

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"

TARGETS=("$@")
[ $# -eq 0 ] && TARGETS=(backend config agent)

for name in "${TARGETS[@]}"; do
    echo "=== Restarting $name ==="
    "$BASE_DIR/stop.sh" "$name"
    sleep 1
    "$BASE_DIR/start.sh" "$name"
done
