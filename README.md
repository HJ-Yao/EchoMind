# QQBot AI Agent 平台

基于 **QQ + NapCat + OneBot11 + SpringBoot + SpringAI + 阿里云百炼** 构建的 AI Agent 平台。

## 项目结构

```
QQbot-Project/
├── qqbot-server/          # SpringBoot 后端服务
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/com/qqbot/
│       ├── QqbotApplication.java         # 主启动类
│       ├── config/                        # 配置类
│       │   ├── AiConfig.java              # SpringAI + 百炼配置
│       │   └── ThreadPoolConfig.java      # 线程池配置
│       ├── websocket/
│       │   └── OneBotWebSocketClient.java # OneBot11 WebSocket 客户端
│       ├── bot/
│       │   └── MessageHandler.java        # 消息处理器
│       ├── ai/
│       │   └── AiService.java             # AI 对话服务
│       ├── onebot/
│       │   └── OneBotApi.java             # OneBot HTTP API 客户端
│       └── common/
│           ├── Result.java                # 统一响应
│           └── exception/                 # 异常处理
├── qqbot-frontend/        # Vue3 + Element Plus 前端
│   ├── src/
│   │   ├── api/            # API 服务层
│   │   ├── components/     # 组件
│   │   ├── router/         # 路由配置
│   │   ├── stores/         # Pinia 状态管理
│   │   ├── types/          # TypeScript 类型定义
│   │   ├── utils/          # 工具函数
│   │   └── views/          # 页面视图
│   └── vite.config.ts
├── nginx/                 # Nginx 配置
│   ├── nginx.conf
│   └── conf.d/qqbot.conf
├── docker-compose.yml     # Docker Compose 部署配置
└── deploy.sh              # 一键部署脚本
```

## 核心链路（MVP）

```
QQ消息 → NapCat → OneBot11 WebSocket → SpringBoot → SpringAI → 百炼(qwen-plus)
                                                              ↓
QQ用户 ← NapCat ← OneBot HTTP API ←──────────────────────────  AI回复
```

## 快速开始

### 前置条件

- JDK 17+
- Maven 3.6+
- Node.js 20+
- NapCat + QQ 已启动

### 后端启动

```bash
cd qqbot-server

# 配置环境变量
export DASHSCOPE_API_KEY="your-dashscope-api-key"
export QQBOT_WS_TOKEN="your-napcat-token"

# 编译运行
mvn clean package -DskipTests
java -jar target/qqbot-server.jar
```

### 前端启动

```bash
cd qqbot-frontend

# 安装依赖
npm install

# 开发模式
npm run dev

# 生产构建
npm run build
```

### Docker 部署

```bash
# 设置环境变量
export DASHSCOPE_API_KEY="your-key"
export QQBOT_WS_TOKEN="your-token"

# 构建并启动
chmod +x deploy.sh
./deploy.sh
```

## 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| 后端框架 | Spring Boot 3.4.x | Java 17 |
| AI 框架 | SpringAI 1.0.0 | OpenAI 兼容模式对接百炼 |
| 消息协议 | OneBot11 | WebSocket + HTTP API |
| 大模型 | 通义千问 (qwen-plus) | 阿里云百炼平台 |
| 前端框架 | Vue 3 + Element Plus | TypeScript |
| 状态管理 | Pinia | - |
| 构建工具 | Vite | - |
| 部署 | Docker Compose | Nginx + SpringBoot |

## 开发路线

- **Phase 1 (当前)**: QQ消息 → AI回复 核心链路
- **Phase 2**: PostgreSQL + 消息持久化
- **Phase 3**: AI Memory 上下文聊天
- **Phase 4**: pgvector + 长期记忆
- **Phase 5**: AI 人格模拟
- **Phase 6**: 管理后台完善
