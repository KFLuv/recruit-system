@echo off
chcp 65001 >nul
title 招聘系统前端开发 (8081)
echo ========================================
echo   正在启动前端开发服务器，请勿关闭本窗口
echo   启动成功后会显示: Local: http://localhost:8081/
echo   浏览器打开 http://localhost:8081 登录
echo   接口请求会代理到后端 8080
echo ========================================
cd /d "E:\Desktop\人力招聘全流程数字化归档管理系统\frontend"
npm run dev
pause
