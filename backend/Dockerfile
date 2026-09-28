# 后端 Dockerfile - 多阶段构建
FROM maven:3.9.9-eclipse-temurin-11 AS builder

WORKDIR /build

# 复制 pom.xml 并下载依赖（利用 Docker 缓存）
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码并打包
COPY backend/ .
RUN mvn clean package -DskipTests -B

# 运行阶段 - 最小化镜像
FROM eclipse-temurin:11-jre-alpine

WORKDIR /app

# 从构建阶段复制 jar 包
COPY --from=builder /build/backend/target/recruitment-system-1.0.0-SNAPSHOT.jar app.jar

# 创建必要目录
RUN mkdir -p logs uploads

# 暴露端口
EXPOSE 8082

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8082/actuator/health || exit 1

# 启动命令
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
