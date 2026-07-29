#!/bin/bash
# ==========================================
# QQBot AI Agent 平台 - 一键部署脚本
# 使用: chmod +x deploy.sh && ./deploy.sh
# ==========================================

set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
BACKEND_DIR="$PROJECT_DIR/qqbot-server"
FRONTEND_DIR="$PROJECT_DIR/qqbot-frontend"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_info() { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# 检查必需的环境变量
if [ -z "$DASHSCOPE_API_KEY" ]; then
    log_error "请设置环境变量: DASHSCOPE_API_KEY (百炼 API Key)"
    exit 1
fi

if [ -z "$QQBOT_WS_TOKEN" ]; then
    log_error "请设置环境变量: QQBOT_WS_TOKEN (NapCat WebSocket Token)"
    exit 1
fi

# 1. 构建后端
log_info "=== 第 1 步：构建后端 ==="
cd "$BACKEND_DIR"
mvn clean package -DskipTests
log_info "后端构建完成"

# 2. 构建前端
log_info "=== 第 2 步：构建前端 ==="
cd "$FRONTEND_DIR"
npm install --production=false
npm run build
log_info "前端构建完成"

# 3. Docker Compose 部署
log_info "=== 第 3 步：Docker 部署 ==="
cd "$PROJECT_DIR"
docker compose down --remove-orphans 2>/dev/null || true
docker compose build
docker compose up -d

# 4. 等待服务启动
log_info "=== 等待服务启动 ==="
sleep 10

# 读取端口配置（与 docker-compose.yml 保持一致）
NGINX_PORT="${NGINX_PORT:-9090}"
BACKEND_PORT="${BACKEND_PORT:-8080}"

# 5. 验证服务
log_info "=== 第 4 步：验证服务 ==="
if curl -s http://localhost:${BACKEND_PORT}/actuator/health | grep -q "UP"; then
    log_info "后端服务运行正常 (端口: ${BACKEND_PORT})"
else
    log_warn "后端服务可能未完全启动，请检查日志: docker compose logs qqbot-backend"
fi

if curl -s -o /dev/null -w "%{http_code}" http://localhost:${NGINX_PORT} | grep -q "200"; then
    log_info "Docker Nginx 运行正常 (端口: ${NGINX_PORT})"
else
    log_warn "Docker Nginx 可能未完全启动，请检查日志: docker compose logs nginx"
fi

log_info "=== 部署完成 ==="
log_info "管理后台: http://101.43.66.17:${NGINX_PORT}"
log_info "后端健康检查: http://101.43.66.17:${NGINX_PORT}/health"
log_info ""
log_info "提示：如需通过域名访问或对接宿主机 Nginx，请参考 nginx/host-nginx-sample.conf"
