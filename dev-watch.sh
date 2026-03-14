#!/bin/bash
# 监听所有子模块 Java 文件变化，自动增量编译，触发 DevTools 热重启
# 用法：./dev-watch.sh
# 依赖：fswatch（brew install fswatch）

ROOT="$(cd "$(dirname "$0")" && pwd)"

echo "==> 开始监听 Java 文件变化 (Ctrl+C 停止)..."
echo "==> 修改任意 .java 文件后将自动编译对应模块并触发热重启"

fswatch -o \
  "$ROOT/csdn-meeting-domain/src" \
  "$ROOT/csdn-meeting-application/src" \
  "$ROOT/csdn-meeting-infrastructure/src" \
  "$ROOT/csdn-meeting-interfaces/src" \
  --include='\.java$' \
  --latency 0.5 | while read changed; do
    echo ""
    echo "[$(date '+%H:%M:%S')] 检测到文件变化，开始增量编译..."
    mvn compile \
      -pl csdn-meeting-domain,csdn-meeting-application,csdn-meeting-infrastructure,csdn-meeting-interfaces \
      -am -q --no-transfer-progress 2>&1
    if [ $? -eq 0 ]; then
      echo "[$(date '+%H:%M:%S')] ✓ 编译成功，DevTools 正在热重启..."
    else
      echo "[$(date '+%H:%M:%S')] ✗ 编译失败，请检查错误"
    fi
done
