#!/bin/bash
# ============================================================
# 一键启动 AI Performance Engineer 全部 8 个模块 (本地开发)
# ============================================================
# 使用方法:
#   chmod +x start-all.sh
#   ./start-all.sh
#
# 停止: Ctrl+C 或 ./stop-all.sh
# ============================================================

set -e

BASE_DIR="/Users/null/IdeaProjects/github/AIPerformanceEngineer"
LOG_DIR="$BASE_DIR/logs"
mkdir -p "$LOG_DIR"

# 模块列表 (按启动顺序)
MODULES=(
  "aipe-resource:8082"
  "aipe-observation:8083"
  "aipe-relationship:8084"
  "aipe-timeline:8085"
  "aipe-evidence:8086"
  "aipe-knowledge:8087"
  "aipe-recommendation:8088"
  "aipe-execution:8089"
)

PIDS=()

# 清理函数
cleanup() {
  echo ""
  echo "=== 停止所有模块 ==="
  for pid in "${PIDS[@]}"; do
    if kill -0 "$pid" 2>/dev/null; then
      kill "$pid" 2>/dev/null
      echo "  停止 PID $pid"
    fi
  done
  exit 0
}

trap cleanup INT TERM

echo "=== 构建项目 ==="
cd "$BASE_DIR"
mvn clean package -DskipTests -q 2>&1 | tail -3

echo ""
echo "=== 启动模块 ==="

for module_port in "${MODULES[@]}"; do
  module="${module_port%%:*}"
  port="${module_port##*:}"

  echo "  启动 $module (端口 $port)..."

  cd "$BASE_DIR/$module"
  nohup mvn spring-boot:run -q > "$LOG_DIR/${module}.log" 2>&1 &
  pid=$!
  PIDS+=("$pid")

  # 等待启动
  for i in $(seq 1 30); do
    if curl -s "http://localhost:$port/actuator/health" > /dev/null 2>&1; then
      echo "    ✅ $module 启动成功 (PID $pid)"
      break
    fi
    if [ $i -eq 30 ]; then
      echo "    ❌ $module 启动超时, 查看日志: tail -f $LOG_DIR/${module}.log"
    fi
    sleep 2
  done
done

echo ""
echo "=== 全部模块已启动 ==="
echo "  Resource:        http://localhost:8082"
echo "  Observation:     http://localhost:8083"
echo "  Relationship:    http://localhost:8084"
echo "  Timeline:        http://localhost:8085"
echo "  Evidence:        http://localhost:8086"
echo "  Knowledge:       http://localhost:8087"
echo "  Recommendation:  http://localhost:8088"
echo "  Execution:       http://localhost:8089"
echo ""
echo "  Dashboard:       http://localhost:5174"
echo ""
echo "  停止: Ctrl+C"
echo ""

# 等待
wait
