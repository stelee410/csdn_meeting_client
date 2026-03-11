#!/bin/bash
# 一键部署脚本：将当前代码推送到部署 repo
# 用法: ./deploy.sh [部署repo地址]
# 示例: ./deploy.sh https://gitee.com/yourname/csdn_meeting_deploy.git

set -e

DEPLOY_REMOTE="deploy"
DEPLOY_BRANCH="main"
DEPLOY_URL="${1:-}"

# ── 检查/添加 deploy remote ────────────────────────────────────────────
if git remote get-url "$DEPLOY_REMOTE" &>/dev/null; then
  CURRENT_URL=$(git remote get-url "$DEPLOY_REMOTE")
  echo "✅ 已有 remote '$DEPLOY_REMOTE' -> $CURRENT_URL"
else
  if [ -z "$DEPLOY_URL" ]; then
    echo "❌ 首次使用需要提供部署 repo 地址"
    echo "   用法: ./deploy.sh <部署repo地址>"
    echo "   示例: ./deploy.sh https://gitee.com/yourname/csdn_meeting_deploy.git"
    exit 1
  fi
  echo "➕ 添加 remote '$DEPLOY_REMOTE' -> $DEPLOY_URL"
  git remote add "$DEPLOY_REMOTE" "$DEPLOY_URL"
fi

# ── 检查是否有未提交的改动 ─────────────────────────────────────────────
if ! git diff --quiet || ! git diff --cached --quiet; then
  echo ""
  echo "⚠️  有未提交的改动，是否继续推送当前已提交的内容？(y/N)"
  read -r answer
  if [[ "$answer" != "y" && "$answer" != "Y" ]]; then
    echo "已取消"
    exit 0
  fi
fi

# ── 推送到部署 repo ────────────────────────────────────────────────────
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
COMMIT=$(git log --oneline -1)

echo ""
echo "🚀 正在推送到部署 repo..."
echo "   分支: $CURRENT_BRANCH → $DEPLOY_BRANCH"
echo "   提交: $COMMIT"
echo ""

git push "$DEPLOY_REMOTE" "HEAD:$DEPLOY_BRANCH" --force

echo ""
echo "✅ 推送成功！"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  请在服务器上执行："
echo ""
echo "  git pull && docker compose up -d --build"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
