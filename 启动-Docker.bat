@echo off
chcp 65001 >nul
cd /d "E:\Desktop\人力招聘全流程数字化归档管理系统"

echo ========================================
echo   人力招聘系统 - Docker 一键启动
echo ========================================
echo.

echo [1/3] 检查 Docker 环境...
docker --version >nul 2>&1
if errorlevel 1 (
    echo ❌ 错误：未检测到 Docker，请先安装 Docker Desktop
    pause
    exit /b 1
)
echo ✅ Docker 正常
echo.

echo [2/3] 构建并启动容器...
docker-compose up -d --build

if errorlevel 1 (
    echo ❌ 启动失败，请查看错误信息
    docker-compose logs
    pause
    exit /b 1
)

echo ✅ 容器启动成功！
echo.

echo [3/3] 等待服务就绪...
timeout /t 15 /nobreak >nul
echo.

echo ========================================
echo   启动完成！
echo ========================================
echo   前端访问：http://localhost
echo   后端 API:   http://localhost:8082
echo   数据库：    localhost:3306
echo.
echo   默认账号：admin / <your-password>
echo ========================================
pause
