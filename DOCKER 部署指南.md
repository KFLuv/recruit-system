# 人力招聘全流程数字化归档管理系统 - Docker 部署指南

## 一、快速启动（3 步完成）

### 步骤 1：确认 Docker 环境
```powershell
# 检查 Docker 是否运行
docker --version
docker-compose --version
```

### 步骤 2：进入项目目录
```powershell
cd "E:\Desktop\人力招聘全流程数字化归档管理系统"
```

### 步骤 3：一键启动
```powershell
# 构建并启动所有服务
docker-compose up -d --build

# 查看启动日志
docker-compose logs -f
```

**启动成功后访问：**
- 前端页面：`http://localhost`
- 后端 API：`http://localhost:8082`
- 数据库：`localhost:3306`

**默认账号：**
- 管理员：`admin` / 密码 `<your-password>`
- 只读用户：`readonly` / 密码 `<your-password>`

---

## 二、常用命令

### 停止服务
```powershell
docker-compose down
```

### 重启服务
```powershell
docker-compose restart
```

### 查看服务状态
```powershell
docker-compose ps
```

### 查看后端日志
```powershell
docker-compose logs backend
```

### 查看前端日志
```powershell
docker-compose logs frontend
```

### 查看数据库日志
```powershell
docker-compose logs mysql
```

### 进入后端容器
```powershell
docker-compose exec backend sh
```

### 进入数据库容器执行 SQL
```powershell
docker-compose exec mysql mysql -uroot -p<your-password> recruitment_db
```

---

## 三、端口映射说明

| 服务   | 容器端口 | 宿主机端口 | 说明           |
|--------|----------|------------|----------------|
| 前端   | 80       | 80         | Web 访问       |
| 后端   | 8082     | 8082       | API 接口       |
| 数据库 | 3306     | 3306       | MySQL 连接    |

---

## 四、数据持久化

所有数据存储在 Docker 卷（volumes）中：

```powershell
# 查看所有卷
docker volume ls

# 查看卷详情
docker volume inspect recruitment_mysql-data
```

**卷说明：**
- `recruitment_mysql-data` - MySQL 数据库文件
- `recruitment_backend-logs` - 后端日志
- `recruitment_backend-uploads` - 上传文件（简历、附件等）

**备份数据：**
```powershell
# 备份 MySQL 数据
docker run --rm \
  --volumes-from recruitment_mysql \
  -v "$(pwd):\backup" \
  alpine tar czf /backup/mysql-backup.tar.gz /var/lib/mysql

# 恢复 MySQL 数据
docker run --rm \
  --volumes-from recruitment_mysql \
  -v "$(pwd):\backup" \
  alpine tar xzf /backup/mysql-backup.tar.gz -C /
```

---

## 五、修改配置

### 1. 修改后端端口
编辑 `docker-compose.yml`，修改 `backend` 服务的端口映射：
```yaml
ports:
  - "8083:8082"  # 改为 8083:8082
```

### 2. 修改数据库密码
编辑 `docker-compose.yml`，修改 `mysql` 服务的环境变量：
```yaml
environment:
  MYSQL_ROOT_PASSWORD: 新的密码
  MYSQL_PASSWORD: 新的密码
```

**注意：** 修改后需要同时更新 `backend` 服务的环境变量：
```yaml
environment:
  SPRING_DATASOURCE_PASSWORD: 新的密码
```

### 3. 修改数据库名称
编辑 `docker-compose.yml`，修改 `mysql` 和 `backend` 配置：
```yaml
# mysql 服务
environment:
  MYSQL_DATABASE: 新的数据库名

# backend 服务
SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/新的数据库名
```

### 4. 修改应用端口（application.yml）
如果需要修改后端应用内部端口：
1. 编辑 `backend/src/main/resources/application.yml`
2. 修改 `server.port: 8082` 为新的端口
3. 重新构建：`docker-compose build --no-cache`

---

## 六、常见问题

### Q1: 启动失败，端口被占用
**错误信息：** `Port 80 already in use`
**解决方案：**
```powershell
# 查看占用端口的进程
netstat -ano | findstr :80

# 结束进程（替换 PID）
taskkill /PID 1234 /F
```

### Q2: 数据库连接失败
**错误信息：** `Communications link failure`
**检查项：**
1. MySQL 服务是否启动：`docker-compose ps`
2. 数据库密码是否正确
3. 等待 MySQL 完全启动（查看日志确认）

### Q3: 前端页面 404
**解决方案：**
1. 清理浏览器缓存
2. 检查后端是否启动：`curl http://localhost:8082`
3. 查看 nginx 日志：`docker-compose logs frontend`

### Q4: 上传文件无法访问
**解决方案：**
1. 检查 `backend-uploads` 卷是否挂载成功
2. 确认上传路径配置正确
3. 查看后端日志：`docker-compose logs backend`

### Q5: 容器不断重启
**解决方案：**
```powershell
# 查看错误日志
docker-compose logs backend

# 进入容器手动测试
docker-compose exec backend sh
java -jar app.jar
```

---

## 七、生产环境部署建议

### 1. 环境变量分离
创建 `.env` 文件：
```bash
MYSQL_ROOT_PASSWORD=强密码
MYSQL_DATABASE=recruitment_db
MYSQL_USER=recruitment
MYSQL_PASSWORD=强密码
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/recruitment_db?...
```

在 `docker-compose.yml` 中引用：
```yaml
environment:
  MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
  SPRING_DATASOURCE_PASSWORD: ${MYSQL_PASSWORD}
```

### 2. 使用 SSL
修改 `application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://mysql:3306/recruitment_db?useSSL=true&serverTimezone=Asia/Shanghai
```

### 3. 限制资源
在 `docker-compose.yml` 中添加：
```yaml
backend:
  mem_limit: 512m
  cpus: 1.0
```

### 4. 日志轮转
在 `docker-compose.yml` 的 `mysql` 服务中添加：
```yaml
logging:
  driver: "json-file"
  options:
    max-size: "10m"
    max-file: "3"
```

---

## 八、与原生启动对比

| 对比项       | 原生启动                        | Docker 启动                    |
|--------------|---------------------------------|--------------------------------|
| 环境依赖     | 需安装 JDK11、Maven、Node.js    | 无需安装，Docker 内置          |
| 启动命令     | 2 个 bat 文件，手动执行         | 1 个命令 `docker-compose up`  |
| 端口冲突     | 需手动修改配置                  | 通过映射端口避免               |
| 数据持久化   | 需手动备份                      | Docker 卷自动持久化           |
| 部署一致性   | 不同机器可能有差异              | 完全一致                       |
| 回滚         | 困难                            | 快速（重新构建旧镜像）         |
| 清理         | 需手动删除文件和注册表          | `docker-compose down` 一键清理 |

---

## 九、卸载清理

### 完全卸载（删除所有数据）
```powershell
# 停止并删除容器
docker-compose down -v

# 删除镜像
docker rmi $(docker images -q recruitment*)

# 删除卷
docker volume rm recruitment_mysql-data
docker volume rm recruitment_backend-logs
docker volume rm recruitment_backend-uploads
```

### 保留数据卸载
```powershell
# 只删除容器，保留数据卷
docker-compose down
```

---

## 十、技术架构说明

```
┌─────────────┐     ┌──────────────┐     ┌─────────────┐
│  浏览器      │────▶│  Nginx       │────▶│  Spring Boot│
│  (前端)     │     │  (前端服务)   │     │  (后端 API)  │
│  端口 80     │     │  代理 /api    │     │  端口 8082   │
└─────────────┘     └──────────────┘     └─────────────┘
                                              │
                                              ▼
                                       ┌─────────────┐
                                       │  MySQL      │
                                       │  (数据库)    │
                                       │  端口 3306   │
                                       └─────────────┘
```

**网络结构：**
- 前端 → Nginx → 后端（通过 `/api` 代理）
- 后端 → MySQL（通过 Docker 内部网络）
- 所有服务在同一 Docker 网络，无需暴露内部端口

---

## 十一、迁移至服务器

### 1. 本地构建镜像
```powershell
# 构建所有服务
docker-compose build

# 导出镜像
docker save recruitment_frontend > frontend.tar
docker save recruitment_backend > backend.tar
docker save mysql:8.0 > mysql.tar
```

### 2. 传输到服务器
```powershell
# 使用 scp 或 pscp 传输
pscp *.tar root@服务器 IP:/tmp/
```

### 3. 服务器部署
```bash
# 加载镜像
docker load -i frontend.tar
docker load -i backend.tar
docker load -i mysql.tar

# 修改 docker-compose.yml（如需修改端口）
# 启动
docker-compose up -d
```

---

## 十二、维护脚本（可选）

创建 `deploy.bat`：
```batch
@echo off
cd /d "E:\Desktop\人力招聘全流程数字化归档管理系统"
echo ========================================
echo   正在启动招聘系统 Docker 容器...
echo ========================================
docker-compose up -d --build
echo.
echo 启动完成！
echo 前端访问：http://localhost
echo 后端 API:   http://localhost:8082
echo ========================================
pause
```

创建 `backup.bat`：
```batch
@echo off
cd /d "E:\Desktop\人力招聘全流程数字化归档管理系统"
echo 正在备份数据库...
docker run --rm \
  --volumes-from recruitment_mysql \
  -v "%cd%\backup:\backup" \
  alpine tar czf /backup/mysql-backup_%date:~-4,4%%date:~-7,2%%date:~-10,2%.tar.gz /var/lib/mysql
echo 备份完成！
```

---

## 十三、总结

**Docker 部署优势：**
✅ 一键启动，无需配置环境  
✅ 数据持久化，容器删除不丢数据  
✅ 端口隔离，避免冲突  
✅ 版本可控，随时回滚  
✅ 部署一致，开发/生产环境相同  

**适用场景：**
- 个人开发测试
- 团队协作环境
- 生产环境部署（配合生产优化）
- 多项目隔离运行

**不适用场景：**
- 需要深度定制系统级配置
- 性能要求极高的生产环境（需额外优化）
