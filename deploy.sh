#!/usr/bin/env bash
# 后端方案三：本地 mvn 打包 → 上传 JAR → 服务器 docker compose 重启 app（不重建镜像）
#
# 用法:
#   ./deploy.sh <SSH用户> <服务器IP或域名> [远程项目目录]
#   ./deploy.sh <用户@主机> [远程项目目录]
#
# 示例:
#   ./deploy.sh root 192.168.1.10
#   ./deploy.sh root 192.168.1.10 /root/workspace/csdn_meeting_client
#   ./deploy.sh root@example.com
#
# 环境变量:
#   SKIP_BUILD=1       跳过本地 mvn package，直接上传已有 JAR
#   SKIP_GIT_PUSH=1    跳过推送到 GitHub（默认会先 push 到 stelee410 仓库）
#   GIT_PUSH_REMOTE    推送使用的 remote 名（默认 deploy → github.com/stelee410/csdn_meeting_client）
#   GIT_PUSH_BRANCH    推送目标分支名（默认 main）
#   DEPLOY_REMOTE_DIR  未传第三参数时的默认远程目录（默认 /root/workspace/csdn_meeting_client）
#
# 说明:
#   - 前端仍按现有方式部署，本脚本只负责后端 JAR。
#   - 服务器需已 git pull 到包含「app 挂载 runtime/app.jar」的 docker-compose.yml，
#     并已配置好 .env；首次全栈启动请在服务器项目目录执行: docker compose up -d

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

DEFAULT_REMOTE_DIR="${DEPLOY_REMOTE_DIR:-/root/workspace/csdn_meeting_client}"
JAR_REL="csdn-meeting-start/target/csdn-meeting-client-start.jar"

usage() {
  echo "用法: $0 <SSH用户> <服务器IP或域名> [远程项目目录]"
  echo "  或: $0 <用户@主机> [远程项目目录]"
  echo ""
  echo "示例: $0 root 192.168.1.10"
  echo "      $0 root@192.168.1.10 /opt/csdn_meeting_client"
  echo ""
  echo "环境变量: SKIP_BUILD=1 跳过构建；SKIP_GIT_PUSH=1 跳过 GitHub 推送；DEPLOY_REMOTE_DIR 默认远程目录"
  exit 1
}

REMOTE_USER=""
REMOTE_HOST=""
REMOTE_DIR=""

if [[ $# -lt 1 ]]; then
  usage
fi

if [[ "$1" == *"@"* ]]; then
  REMOTE_USER="${1%%@*}"
  REMOTE_HOST="${1#*@}"
  REMOTE_DIR="${2:-$DEFAULT_REMOTE_DIR}"
else
  if [[ $# -lt 2 ]]; then
    usage
  fi
  REMOTE_USER="$1"
  REMOTE_HOST="$2"
  REMOTE_DIR="${3:-$DEFAULT_REMOTE_DIR}"
fi

if [[ -z "$REMOTE_USER" || -z "$REMOTE_HOST" ]]; then
  usage
fi

JAR_LOCAL="$SCRIPT_DIR/$JAR_REL"
JAR_REMOTE="$REMOTE_DIR/runtime/app.jar"

# ── 部署前：将已提交代码推送到 github.com/stelee410（与原先 deploy remote 逻辑一致）────────
if [[ "${SKIP_GIT_PUSH:-0}" != "1" ]]; then
  if [[ ! -d "$SCRIPT_DIR/.git" ]]; then
    echo "⚠️  当前目录不是 git 仓库，跳过 GitHub 推送"
  else
    GIT_PUSH_REMOTE="${GIT_PUSH_REMOTE:-deploy}"
    GIT_PUSH_BRANCH="${GIT_PUSH_BRANCH:-main}"
    if ! git -C "$SCRIPT_DIR" remote get-url "$GIT_PUSH_REMOTE" &>/dev/null; then
      echo "❌ 未找到 git remote '$GIT_PUSH_REMOTE'"
      echo "   请添加: git remote add deploy https://github.com/stelee410/csdn_meeting_client.git"
      echo "   或使用环境变量 GIT_PUSH_REMOTE 指定已指向 github.com/stelee410 的 remote"
      exit 1
    fi
    PUSH_URL=$(git -C "$SCRIPT_DIR" remote get-url "$GIT_PUSH_REMOTE")
    if [[ "$PUSH_URL" != *"github.com/stelee410"* ]]; then
      echo "⚠️  '$GIT_PUSH_REMOTE' 未指向 github.com/stelee410（当前: $PUSH_URL）"
      echo "   仍将继续 push；若不对请设置 GIT_PUSH_REMOTE 或按 Ctrl+C 取消"
      sleep 2
    fi
    if ! git -C "$SCRIPT_DIR" diff --quiet || ! git -C "$SCRIPT_DIR" diff --cached --quiet; then
      echo ""
      echo "⚠️  有未提交的改动，是否继续只推送当前已提交内容到 $GIT_PUSH_REMOTE/$GIT_PUSH_BRANCH？(y/N)"
      if [[ -r /dev/tty ]]; then
        read -r answer </dev/tty
      else
        read -r answer
      fi
      if [[ "$answer" != "y" && "$answer" != "Y" ]]; then
        echo "已取消"
        exit 0
      fi
    fi
    CURRENT_BRANCH=$(git -C "$SCRIPT_DIR" rev-parse --abbrev-ref HEAD)
    COMMIT=$(git -C "$SCRIPT_DIR" log --oneline -1)
    echo ""
    echo "🚀 推送到 GitHub ($GIT_PUSH_REMOTE)..."
    echo "   分支: $CURRENT_BRANCH → $GIT_PUSH_BRANCH"
    echo "   提交: $COMMIT"
    echo ""
    git -C "$SCRIPT_DIR" push "$GIT_PUSH_REMOTE" "HEAD:$GIT_PUSH_BRANCH" --force
    echo "✅ 代码已推送到 github.com/stelee410 对应仓库"
    echo ""
  fi
fi

if [[ "${SKIP_BUILD:-0}" != "1" ]]; then
  echo "📦 本地 Maven 打包（跳过测试）..."
  mvn -B package -DskipTests
else
  echo "⏭️  SKIP_BUILD=1，跳过本地构建"
fi

if [[ ! -f "$JAR_LOCAL" ]]; then
  echo "❌ 未找到构建产物: $JAR_LOCAL"
  exit 1
fi

REMOTE_BASE="$REMOTE_USER@$REMOTE_HOST"

echo "📤 确保远程目录存在: $REMOTE_BASE:$REMOTE_DIR/runtime"
ssh "$REMOTE_BASE" "mkdir -p '${REMOTE_DIR}/runtime'"

echo "📤 上传 JAR → $REMOTE_BASE:$JAR_REMOTE"
scp "$JAR_LOCAL" "$REMOTE_BASE:$JAR_REMOTE"

echo "🔄 远程: docker compose up（不构建镜像）并重启 app 以加载新 JAR..."
ssh "$REMOTE_BASE" "cd '${REMOTE_DIR}' && docker compose up -d --no-build && docker compose restart app"

echo ""
echo "✅ 后端部署完成（方案三：外挂 JAR + 重启容器）"
