import mysql.connector
import sys

try:
    conn = mysql.connector.connect(
        host='localhost',
        user='root',
        password=os.environ.get('DB_PASSWORD', ''),
        database='recruitment_db'
    )
    cursor = conn.cursor()
    
    with open('c:/Users/24266/Desktop/招聘系统原型/backend/src/main/resources/migration/rebuild_recruit_plan_table.sql', 'r', encoding='utf-8') as f:
        sql_script = f.read()
    
    # 执行 SQL 脚本
    for statement in sql_script.split(';'):
        statement = statement.strip()
        if statement:
            try:
                cursor.execute(statement)
            except mysql.connector.Error as err:
                print(f"Warning: {err}")
    
    conn.commit()
    print("✓ 数据库表结构已更新")
    
except mysql.connector.Error as err:
    print(f"✗ 数据库错误：{err}")
    sys.exit(1)
except Exception as e:
    print(f"✗ 错误：{e}")
    sys.exit(1)
finally:
    if 'cursor' in locals():
        cursor.close()
    if 'conn' in locals():
        conn.close()
