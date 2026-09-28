@echo off
chcp 65001 >nul
cd /d "E:\Desktop\人力招聘全流程数字化归档管理系统"

echo ========================================
echo   人力招聘系统 - 纯 Docker 命令启动
echo   (绕过 docker-compose 问题)
echo ========================================
echo.

echo [1/5] 停止旧容器...
docker rm -f recruitment-app 2>nul
echo ✅ 清理完成

echo.
echo [2/5] 构建镜像（首次需 3-5 分钟）...
docker build -f simple.Dockerfile -t recruitment-app:latest .
if errorlevel 1 (
    echo ❌ 构建失败
    pause
    exit /b 1
)
echo ✅ 镜像构建成功

echo.
echo [3/5] 验证后端 jar 包...
if exist backend\target\recruitment-system-1.0.0-SNAPSHOT.jar (
    echo ✅ jar 包存在
) else (
    echo ❌ jar 包缺失，请先运行 mvn package
    pause
    exit /b 1
)

echo.
echo [4/5] 验证前端静态文件...
if exist frontend\dist\index.html (
    echo ✅ 前端文件存在
) else (
    echo ❌ 前端文件缺失，请先运行 npm run build
    pause
    exit /b 1
)

echo.
echo [5/5] 启动容器...
docker run -d ^
  --name recruitment-app ^
  -p 8082:8082 ^
  --env SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/recruitment_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai^
  &allowPublicKeyRetrieval=true ^
  --env SPRING_DATASOURCE_USERNAME=root ^
  --env SPRING_DATASOURCE_PASSWORD=<your-password> ^
  recruitment-app:latest

if errorlevel 1 (
    echo ❌ 启动失败
    pause
    exit /b 1
)

echo ✅ 容器启动成功！

echo.
echo ========================================
echo   启动完成！
echo ========================================
echo.
echo   前端地址：http://localhost:8082
echo   后端 API:   http://localhost:8082/api
echo   默认账号：admin / <your-password>
echo.
echo ========================================
echo.

docker ps -f name=recruitment-app
echo.
pause
