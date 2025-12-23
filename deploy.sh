#!/bin/bash

# ================================
# 一键部署 ACG 系统脚本
# ================================

set -e

echo "====== 1. 创建宿主机数据目录 ======"
# 创建 acg-product-data 的子目录 cover 和 resource
mkdir -p ./acg-product-data/cover ./acg-product-data/resource
echo "目录创建完成: ./acg-product-data/cover ./acg-product-data/resource"

echo "====== 2. 修复目录权限 ======"
# 当前用户拥有权限，并设置可读写
sudo chown -R $USER:$USER ./acg-product-data
chmod -R 777 ./acg-product-data
echo "权限设置完成"

echo "====== 3. 启动 Docker Compose 服务 ======"
# 确保 docker-compose.yml 在当前目录
docker compose down
docker compose up -d --build
echo "服务启动完成"

echo "====== 4. 检查容器状态 ======"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo "====== 5. 检查上传目录挂载 ======"
docker exec -it acg-product-service ls -l /app/data

echo "====== 部署完成，请访问前端：http://localhost:5500 ======"

