#!/bin/bash
# 导入 MySQL 数据库脚本

MYSQL_PATH="/mnt/c/Program Files/MySQL/MySQL Server 5.7/bin/mysql.exe"
SQL_FILE="/mnt/c/Users/24266/Desktop/招聘系统原型/database/init.sql"

echo "正在导入数据库..."
cat "$SQL_FILE" | $MYSQL_PATH -u root -p<your-password> recruitment_db

if [ $? -eq 0 ]; then
    echo "✅ 数据库导入成功！"
else
    echo "❌ 导入失败"
fi
