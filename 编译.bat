@echo off
chcp 65001 >nul
echo ========================================
echo   招聘系统 - 后端编译打包
echo ========================================
echo.

set JAVA_HOME=E:\JDK11
set PATH=%JAVA_HOME%\bin;%PATH%

cd /d "e:\Desktop\人力招聘全流程数字化归档管理系统\backend"

call E:\apache-maven-3.9.16\bin\mvn.cmd clean package -DskipTests

echo.
echo ========================================
echo 编译结束，jar 在 backend\target 目录
echo ========================================
pause
