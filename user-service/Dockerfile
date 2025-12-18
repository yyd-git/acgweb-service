# ==============================
# Stage 1: Build the application
# ==============================
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# 复制 pom.xml 先下载依赖，提高构建效率
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源码并打包
COPY src ./src
RUN mvn clean package -DskipTests

# ==============================
# Stage 2: Run the application
# ==============================
FROM eclipse-temurin:17-jre-alpine

# 创建普通用户（更安全）
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# 从 builder 中复制 jar 文件
COPY --from=builder /app/target/*.jar /app/app.jar

# 暴露用户服务端口
EXPOSE 8081

USER appuser

# 启动 Spring Boot 应用
ENTRYPOINT ["java", "-jar", "app.jar"]

