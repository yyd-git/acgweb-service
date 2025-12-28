#!/bin/bash

# ==========================================================
# ACG 微服务系统一键部署与功能验证脚本
# ----------------------------------------------------------
# 功能说明：
# 1. 创建并初始化宿主机数据目录
# 2. 启动 Docker Compose 微服务集群
# 3. 校验 MySQL / Nacos / Gateway 状态
# 4. 验证服务注册、JWT 注册登录与鉴权访问
# ==========================================================

set -e

echo "=========================================================="
echo "        ACG 微服务系统 一键部署开始"
echo "=========================================================="

# ----------------------------------------------------------
# 1. 创建宿主机数据目录
# ----------------------------------------------------------
echo ""
echo "【1】创建宿主机数据目录"

mkdir -p ./acg-product-data/cover ./acg-product-data/resource
echo "目录创建完成："
echo " - ./acg-product-data/cover"
echo " - ./acg-product-data/resource"

# ----------------------------------------------------------
# 2. 修复目录权限
# ----------------------------------------------------------
echo ""
echo "【2】修复数据目录权限"

sudo chown -R $USER:$USER ./acg-product-data
chmod -R 777 ./acg-product-data
echo "目录权限设置完成"

# ----------------------------------------------------------
# 3. 启动 Docker Compose 服务
# ----------------------------------------------------------
echo ""
echo "【3】启动 Docker Compose 微服务集群"

docker compose down
docker compose up -d --build

echo "服务启动完成，等待容器初始化（30 秒）..."
sleep 30

# ----------------------------------------------------------
# 4. 容器运行状态检查
# ----------------------------------------------------------
echo ""
echo "【4】检查容器运行状态"

docker compose ps

echo ""
echo "当前运行中的容器："
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# ----------------------------------------------------------
# 5. MySQL 健康检查
# ----------------------------------------------------------
echo ""
echo "【5】检查 MySQL 服务状态"

MYSQL_STATUS=$(docker inspect --format='{{.State.Health.Status}}' acg-demo-mysql)
echo "MySQL 健康状态：$MYSQL_STATUS"

# ----------------------------------------------------------
# 6. Nacos 服务检测
# ----------------------------------------------------------
echo ""
echo "【6】检查 Nacos 服务是否可访问"

curl -s http://localhost:8848/nacos/ > /dev/null \
  && echo "✅ Nacos Web 控制台访问正常" \
  || echo "❌ Nacos 访问失败"

echo ""
echo "当前 Nacos 中已注册的服务列表："
curl -s "http://localhost:8848/nacos/v1/ns/service/list?pageNo=1&pageSize=20" | jq .

# ----------------------------------------------------------
# 7. 微服务注册验证（循环等待）
# ----------------------------------------------------------
echo ""
echo "【7】验证微服务是否成功注册到 Nacos"

services=("user-service" "acg-product-service" "acg-rating-service" "gateway-service")
MAX_WAIT=60    # 最长等待 60 秒
SLEEP_INTERVAL=3

for service in "${services[@]}"
do
  echo ""
  echo "服务：$service"
  elapsed=0
  while [ $elapsed -lt $MAX_WAIT ]
  do
    count=$(curl -s "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=$service" | jq '.hosts | length')
    if [ "$count" -gt 0 ]; then
      echo "✅ 已注册 ($count 实例)"
      break
    else
      echo "等待服务注册中..."
      sleep $SLEEP_INTERVAL
      elapsed=$((elapsed + SLEEP_INTERVAL))
    fi
  done
  if [ "$count" -eq 0 ]; then
    echo "⚠️ $service 未注册到 Nacos (超时 $MAX_WAIT 秒)"
  fi
done

# ----------------------------------------------------------
# 8. Gateway 健康检查
# ----------------------------------------------------------
echo ""
echo "【8】检查 Gateway 服务健康状态"

GATEWAY_HEALTH=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8090/actuator/health)
if [ "$GATEWAY_HEALTH" -eq 200 ]; then
  echo "✅ Gateway 健康"
else
  echo "⚠️ Gateway 健康检查失败 (HTTP $GATEWAY_HEALTH)"
fi

# ----------------------------------------------------------
# 9. 用户注册接口测试（JWT 白名单）
# ----------------------------------------------------------
echo ""
echo "【9】测试用户注册接口（JWT 白名单）"

REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8090/user/register \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin&password=123456")

echo "注册接口响应："
echo "$REGISTER_RESPONSE"
echo "（若用户已存在则忽略）"

# ----------------------------------------------------------
# 10. 用户登录接口测试（JWT 生成）
# ----------------------------------------------------------
echo ""
echo "【10】测试用户登录接口（JWT 生成）"

LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8090/user/login \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin&password=123456")

echo "登录接口响应："
echo "$LOGIN_RESPONSE"

TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.data // empty')

if [ -z "$TOKEN" ]; then
  echo "❌ JWT 获取失败，终止测试"
  exit 1
else
  echo "✅ JWT 获取成功"
fi

# ----------------------------------------------------------
# 11. JWT 鉴权接口访问测试（用户服务）
# ----------------------------------------------------------
echo ""
echo "【11】测试 JWT 鉴权访问（user-service）"

# 使用用户 ID 1 作为示例，可根据实际数据库修改
curl -i http://localhost:8090/user/1 \
  -H "Authorization: Bearer $TOKEN"

# ----------------------------------------------------------
# 12. 商品服务接口访问测试
# ----------------------------------------------------------
echo ""
echo "【12】测试商品服务接口（acg-product-service）"

curl -i http://localhost:8090/acg-product \
  -H "Authorization: Bearer $TOKEN"

# ----------------------------------------------------------
# 部署完成
# ----------------------------------------------------------
echo ""
echo "=========================================================="
echo "        ACG 微服务系统部署与测试完成 ✅"
echo "=========================================================="
echo "前端访问地址：http://localhost:5500"
echo "Nacos 控制台：http://localhost:8848/nacos"
echo ""

