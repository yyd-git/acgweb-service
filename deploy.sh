#!/bin/bash

# ==========================================================
# ACG 微服务系统一键部署与功能验证脚本（完整版，Nacos 配置调试增强）
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
# 3. 启动基础设施（MySQL / Nacos）
# ----------------------------------------------------------
echo ""
echo "【3】启动基础设施服务（MySQL / Nacos）"

docker compose down
docker compose up -d nacos mysql

echo "等待基础设施初始化（30 秒）..."
sleep 30

# ----------------------------------------------------------
# 4. 基础设施状态检查
# ----------------------------------------------------------
echo ""
echo "【4】检查基础设施状态"

docker compose ps

echo ""
echo "MySQL 健康状态："
MYSQL_STATUS=$(docker inspect --format='{{.State.Health.Status}}' acg-demo-mysql)
echo "MySQL Health = $MYSQL_STATUS"

echo ""
echo "检查 Nacos 访问状态"
curl -s http://localhost:8848/nacos/ > /dev/null \
  && echo "✅ Nacos Web 控制台访问正常" \
  || echo "❌ Nacos 访问失败"

# ----------------------------------------------------------
# 5. 批量推送 Nacos 配置（--data-urlencode 方式，稳定）
# ----------------------------------------------------------
echo ""
echo "【5】批量推送 Nacos 配置到配置中心（稳定方式）"

NACOS_ADDR="http://localhost:8848"
GROUP="DEFAULT_GROUP"
CONFIG_DIR="./nacos-config"

if [ ! -d "$CONFIG_DIR" ]; then
  echo "❌ Nacos 配置目录不存在：$CONFIG_DIR"
  exit 1
fi

for file in "$CONFIG_DIR"/*.yaml; do
  filename=$(basename "$file")
  echo "👉 推送配置：$filename"

  # 检查文件是否为空
  if [ ! -s "$file" ]; then
    echo "⚠️ 文件为空：$file，跳过"
    continue
  fi

  # 显示文件前 5 行作为调试
  echo "文件内容前 5 行预览："
  head -n 5 "$file"

  # 使用 curl 上传配置，并打印返回
  HTTP_RESPONSE=$(curl -s -w "\n%{http_code}" -o /tmp/nacos_response.txt -X POST "$NACOS_ADDR/nacos/v1/cs/configs" \
    -d "dataId=$filename" \
    -d "group=$GROUP" \
    --data-urlencode "content@$file")

  # 取 HTTP code
  HTTP_CODE=$(tail -n1 <<< "$HTTP_RESPONSE")
  # 取 body
  RESPONSE_BODY=$(cat /tmp/nacos_response.txt)

  if [ "$HTTP_CODE" -eq 200 ] && [ "$RESPONSE_BODY" == "true" ]; then
    echo "✅ 配置推送成功"
  else
    echo "❌ 配置推送失败"
  fi

  echo "HTTP 返回码：$HTTP_CODE"
  echo "返回内容：$RESPONSE_BODY"
  echo "----------------------------------------------------------"
done

echo "✅ Nacos 配置批量推送完成"

# ----------------------------------------------------------
# 6. 启动微服务集群
# ----------------------------------------------------------
echo ""
echo "【6】启动微服务集群"

docker compose up -d --build

echo "等待微服务启动（30 秒）..."
sleep 30

# ----------------------------------------------------------
# 7. 微服务注册验证（Nacos）
# ----------------------------------------------------------
echo ""
echo "【7】验证微服务是否成功注册到 Nacos"

services=(
  "user-service"
  "acg-product-service"
  "acg-rating-service"
  "gateway-service"
)

MAX_WAIT=60
SLEEP_INTERVAL=3

for service in "${services[@]}"; do
  echo ""
  echo "服务：$service"
  elapsed=0
  while [ $elapsed -lt $MAX_WAIT ]; do
    count=$(curl -s \
      "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=$service" \
      | jq '.hosts | length')

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
    echo "⚠️ $service 未注册到 Nacos（超时 $MAX_WAIT 秒）"
  fi
done

# ----------------------------------------------------------
# 9 ~ 12 接口测试（保持原逻辑）
# ----------------------------------------------------------
echo ""
echo "【9】测试用户注册接口（JWT 白名单）"
REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8090/user/register \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin&password=123456")
echo "注册接口响应："
echo "$REGISTER_RESPONSE"
echo "（若用户已存在则忽略）"

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

echo ""
echo "【11】测试 JWT 鉴权访问（user-service）"
curl -i http://localhost:8090/user/1 \
  -H "Authorization: Bearer $TOKEN"

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

