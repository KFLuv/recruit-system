@echo off
chcp 65001 >nul
title 招聘系统后端开发模式 (8080)
echo ========================================
echo   正在启动后端服务（开发模式 8080），请勿关闭本窗口
echo   配合前端 8081 使用，前端接口会代理到本后端
echo   启动成功后会显示: Tomcat started on 8080
echo ========================================
cd /d "E:\Desktop\人力招聘全流程数字化归档管理系统\backend"
E:\JDK11\bin\java.exe -jar target\recruitment-system-1.0.0-SNAPSHOT.jar --server.port=8080 --logging.file.path=./logs
pause
