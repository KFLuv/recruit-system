@echo off
chcp 65001 >nul
title 招聘系统后端打包版 (8082)
echo ========================================
echo   正在启动后端服务（打包版），请勿关闭本窗口
echo   启动成功后会显示: Tomcat started on 8082
echo   浏览器访问: http://localhost:8082 （前后端一体）
echo ========================================
cd /d "E:\Desktop\人力招聘全流程数字化归档管理系统\backend"
E:\JDK11\bin\java.exe -jar target\recruitment-system-1.0.0-SNAPSHOT.jar --server.port=8082 --logging.file.path=./logs
pause
