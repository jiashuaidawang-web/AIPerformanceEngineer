#!/bin/zsh
# ============================================================
# restart.sh — 重启服务
# 用法: ./restart.sh          # 重启全部
#       ./restart.sh agent    # 只重启 agent
#       ./restart.sh backend  # 只重启 backend
#       ./restart.sh config   # 只重启 config-manager
# ============================================================

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"

TARGETS=("$@")
[ $# -eq 0 ] && TARGETS=(agent backend config)

for name in "${TARGETS[@]}"; do
    echo "=== Restarting $name ==="
    "$BASE_DIR/stop.sh" "$name"
    sleep 1
    "$BASE_DIR/start.sh" "$name"
done
