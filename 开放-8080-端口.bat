@echo off
echo ===================================
echo  添加 Spring Boot 8080 端口防火墙规则
echo ===================================
echo.

netsh advfirewall firewall add rule name="Spring Boot 8080" dir=in action=allow protocol=TCP localport=8080

if %errorlevel% equ 0 (
    echo.
    echo ✅ 规则添加成功！
    echo.
    echo 现在局域网内其他设备可以访问：
    echo http://localhost:8080
    echo.
    pause
) else (
    echo ❌ 规则添加失败，请以管理员身份运行此文件
    echo.
    pause
)
