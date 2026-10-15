# 人力招聘全流程数字化归档管理系统

一个覆盖 **招聘计划 → 审批流转 → 候选人管理 → 录用归档** 全流程的企业级招聘管理平台。前后端分离开发、一体打包部署，支持多级审批、编制预算管控、操作日志审计、飞书消息通知与多角色权限（管理员 / 只读）。

## 功能特性

- **登录注册与权限**
  - 登录页支持自助注册，注册时可选账号等级：管理员（可操作）或 只读（仅可查看）
  - 只读账号后端强制拦截所有写操作（创建 / 编辑 / 删除 / 审批等），前端自动隐藏操作按钮
  - 右上角显示当前账号名称与角色标签
- **招聘计划管理**
  - 计划的创建、编辑、提交审批、删除（软删除 → 已删除区 → 确认移除后真正删除）
  - 自动生成计划编号（如 `PL20260001`），修改记录可追溯编号
  - 批量提交 / 批量作废
  - 计划版本管理：每次修改自动生成快照，支持查看历史版本
- **审批管理**
  - 多级审批流转（二级单位 → 集团 HR → 集团领导），支持审批通过 / 驳回并填写意见
  - 待审批列表与审批记录，含计划编号、审批时间
- **编制预算管控**
  - 提交审批前自动校验：该单位本年度编制人数上限与预算上限，超限时阻断并给出明细提示
- **候选人管理**
  - 候选人新增、编辑、状态流转（初筛 / 笔试 / 面试 / 拟录用 / 录用 / 淘汰）
  - 自动生成候选人编号，支持 Excel 模板批量导入
- **操作日志审计**
  - 基于 AOP 的操作日志记录：操作人、IP、模块、操作描述、业务编号、操作时间
  - 新增计划 / 候选人、修改、审批、飞书消息发送等操作均写入日志
- **飞书消息通知**
  - 审批通知、拟录用通知等通过飞书机器人发送并记录日志
- **文件归档**
  - 文件上传归档管理，与岗位 / 计划关联，支持下载
- **智能助手**
  - 内置 AI 聊天助手，可快速查询系统使用说明

## 技术栈

| 端 | 技术 |
|---|---|
| 后端 | Java 11 · Spring Boot 2.x · Spring Data JPA · MySQL · Lombok · AOP |
| 前端 | Vue 3 · Vite 5 · Element Plus 2.7 · Pinia · Vue Router · Axios |
| 部署 | Maven 打包为可执行 Jar · NSSM 注册 Windows 服务 |

## 快速开始

### 环境要求

- JDK 11
- Maven 3.6+
- Node.js 16+
- MySQL 5.7+（连接信息见 `backend/src/main/resources/application.yml`）

### 开发模式

```bash
# 1. 启动后端（默认 8080）
cd backend
mvn spring-boot:run

# 2. 启动前端（默认 8081，自动代理 /api 到 8080）
cd frontend
npm install
npm run dev
```

浏览器访问 http://localhost:8081

### 生产打包

```bash
cd frontend && npm run build   # 构建前端产物到 backend/src/main/resources/static
cd backend && mvn clean package -DskipTests
```

运行打包产物（默认端口 8082）：

```bash
java -jar backend/target/recruitment-system-1.0.0-SNAPSHOT.jar
```

浏览器访问 http://localhost:8082

### 演示账号

| 账号 | 密码 | 角色 |
|---|---|---|
| admin | <your-password> | 管理员 |
| （可在登录页自助注册） | - | 管理员 / 只读 |

## 文档

| 文档 | 回答什么 |
|---|---|
| [docs/PRD.md](docs/PRD.md) | **需求怎么定义** —— 背景与问题、指标口径、范围（含明确不做）、功能需求与验收标准 |
| [docs/产品文档.md](docs/产品文档.md) | **做什么** —— 定位、用户、核心产品决策 |
| [docs/设计文档.md](docs/设计文档.md) | **怎么实现** —— 状态机、三级删除、审批链、权限防御 |
| [docs/产品决策记录.md](docs/产品决策记录.md) | **为什么这么定** —— 当时的权衡与代价 |
| [docs/使用指南.md](docs/使用指南.md) | **怎么跑起来** —— 部署方式与验证清单 |

## 项目结构

```
├── backend/                        # Spring Boot 后端
│   └── src/main/java/com/recruitment/
│       ├── aspect/                 # AOP 操作日志切面
│       ├── controller/             # REST API 控制器
│       ├── dto/                    # 数据传输对象
│       ├── entity/                 # JPA 实体
│       ├── filter/                 # 登录认证过滤器
│       ├── repository/             # 数据访问层
│       ├── service/                # 业务逻辑层
│       └── config/                 # 拦截器 / 静态资源等配置
├── frontend/                       # Vue 3 前端
│   └── src/
│       ├── api/                    # 接口封装
│       ├── components/             # 通用组件（AI 助手等）
│       ├── router/                 # 路由
│       ├── stores/                 # Pinia 状态管理
│       └── views/                  # 页面（登录/计划/候选人/审批/日志等）
└── 启动前端.bat / 启动后端.bat     # Windows 一键启动脚本
```

## Windows 服务部署（NSSM）

```bat
nssm install RecruitSystem "path\to\jdk11\bin\java.exe"
nssm set RecruitSystem AppParameters "-jar path\to\recruitment-system.jar --logging.file.path=./logs"
nssm set RecruitSystem AppDirectory "path\to\service-dir"
nssm start RecruitSystem
```

支持崩溃自动重启、日志自动轮转（10MB）。

## 联系方式

本项目为个人项目，仅用于学习与技术展示。
