# 招聘系统 - 极简 Docker 启动
# 数据库指向你现有的 MySQL（localhost:3306）

$env:DB_URL = "jdbc:mysql://localhost:3306/recruitment_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true"
$env:DB_USER = "root"
$env:DB_PASS = "<your-password>"

# 1. 构建镜像（只需一次）
docker build -f simple.Dockerfile -t recruit-app .

# 2. 启动容器
docker run -d --name recruit --restart unless-stopped `
  -p 8082:8082 `
  -e DB_URL=$env:DB_URL `
  -e DB_USER=$env:DB_USER `
  -e DB_PASS=$env:DB_PASS `
  recruit-app

# 3. 查看状态
docker ps -f name=recruit

Write-Host ""
Write-Host "启动完成！浏览器访问：http://localhost:8082"
