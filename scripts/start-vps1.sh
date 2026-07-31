#!/bin/bash
# VPS1 Service Startup Script

cd /root/AIPerformanceEngineer

MYSQL_URL="jdbc:mysql://124.223.220.245:3306/aipe_metadata?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false"
MYSQL_USER=root
MYSQL_PASS=astock_root

CH_URL="jdbc:clickhouse://124.223.220.245:8123/metric_observation"
CH_USER=default
CH_PASS=""

mkdir -p /var/log/aipe

echo "Starting Resource Engine :8082..."
nohup java -jar aipe-resource/target/aipe-resource-1.0.0-SNAPSHOT.jar \
  --server.port=8082 \
  --spring.datasource.mysql.url="$MYSQL_URL" \
  --spring.datasource.mysql.username=$MYSQL_USER \
  --spring.datasource.mysql.password=$MYSQL_PASS > /var/log/aipe/resource.log 2>&1 &

echo "Starting Relationship Engine :8084..."
nohup java -jar aipe-relationship/target/aipe-relationship-1.0.0-SNAPSHOT.jar \
  --server.port=8084 \
  --spring.datasource.mysql.url="$MYSQL_URL" \
  --spring.datasource.mysql.username=$MYSQL_USER \
  --spring.datasource.mysql.password=$MYSQL_PASS > /var/log/aipe/relationship.log 2>&1 &

echo "Starting Timeline Engine :8085..."
nohup java -jar aipe-timeline/target/aipe-timeline-1.0.0-SNAPSHOT.jar \
  --server.port=8085 \
  --spring.datasource.clickhouse.jdbc-url="$CH_URL" \
  --spring.datasource.clickhouse.username=$CH_USER > /var/log/aipe/timeline.log 2>&1 &

echo "Starting Knowledge Engine :8087..."
nohup java -jar aipe-knowledge/target/aipe-knowledge-1.0.0-SNAPSHOT.jar \
  --server.port=8087 \
  --spring.datasource.mysql.url="$MYSQL_URL" \
  --spring.datasource.mysql.username=$MYSQL_USER \
  --spring.datasource.mysql.password=$MYSQL_PASS > /var/log/aipe/knowledge.log 2>&1 &

echo "Starting Recommendation Engine :8088..."
nohup java -jar aipe-recommendation/target/aipe-recommendation-1.0.0-SNAPSHOT.jar \
  --server.port=8088 \
  --spring.datasource.mysql.url="$MYSQL_URL" \
  --spring.datasource.mysql.username=$MYSQL_USER \
  --spring.datasource.mysql.password=$MYSQL_PASS > /var/log/aipe/recommendation.log 2>&1 &

echo "Starting Execution Engine :8089..."
nohup java -jar aipe-execution/target/aipe-execution-1.0.0-SNAPSHOT.jar \
  --server.port=8089 \
  --spring.datasource.mysql.url="$MYSQL_URL" \
  --spring.datasource.mysql.username=$MYSQL_USER \
  --spring.datasource.mysql.password=$MYSQL_PASS > /var/log/aipe/execution.log 2>&1 &

echo "Starting Config Manager :8080..."
nohup java -jar aipe-config-manager/target/aipe-config-manager-1.0.0-SNAPSHOT.jar \
  --server.port=8080 \
  --spring.datasource.mysql.url="$MYSQL_URL" \
  --spring.datasource.mysql.username=$MYSQL_USER \
  --spring.datasource.mysql.password=$MYSQL_PASS > /var/log/aipe/config.log 2>&1 &

echo "Starting Alert Engine :8090..."
nohup java -jar aipe-alert/target/aipe-alert-1.0.0-SNAPSHOT.jar \
  --server.port=8090 > /var/log/aipe/alert.log 2>&1 &

echo "Starting Backend :8081..."
nohup java -jar aipe-backend/target/aipe-backend-1.0.0-SNAPSHOT.jar \
  --server.port=8081 \
  --spring.datasource.mysql.url="$MYSQL_URL" \
  --spring.datasource.mysql.username=$MYSQL_USER \
  --spring.datasource.mysql.password=$MYSQL_PASS \
  --spring.datasource.clickhouse.jdbc-url="$CH_URL" \
  --spring.datasource.clickhouse.username=$CH_USER > /var/log/aipe/backend.log 2>&1 &

echo "All engines starting... waiting 30s..."
sleep 30

echo "Running processes:"
ps aux | grep 'aipe-' | grep -v grep | wc -l
