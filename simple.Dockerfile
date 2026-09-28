# 招聘系统极简版 - 直接用已构建的 jar + 前端产物
# 只需拉取 1 个 JRE 镜像，不需要 Maven 编译
FROM eclipse-temurin:11-jre-alpine

WORKDIR /app

# 复制已构建的后端 jar（你的 target 目录已有）
COPY backend/target/recruitment-system-1.0.0-SNAPSHOT.jar app.jar

# 复制已构建的前端静态文件（dist 目录已有）
COPY frontend/dist/ ./static/

# 创建上传/日志目录
RUN mkdir -p uploads logs

EXPOSE 8082

# 启动（数据库连接用环境变量覆盖，指向你现有的 MySQL）
ENTRYPOINT ["sh", "-c", "java -jar app.jar --spring.datasource.url=${DB_URL} --spring.datasource.username=${DB_USER} --spring.datasource.password=${DB_PASS} --server.port=8082"]
