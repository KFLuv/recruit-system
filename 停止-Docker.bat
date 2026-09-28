@echo off
chcp 65001 >nul
cd /d "E:\Desktop\人力招聘全流程数字化归档管理系统"

echo ========================================
echo   人力招聘系统 - Docker 停止容器
echo ========================================
echo.

echo 正在停止并删除容器...
docker-compose down

if errorlevel 1 (
    echo ❌ 停止失败
    pause
    exit /b 1
)

echo ✅ 容器已停止
echo.
echo ========================================
echo   提示：
echo   - 如需保留数据，仅删除容器
echo   - 如需彻底清理（删除数据），执行：
echo     docker-compose down -v
echo ========================================
pause
