#!/bin/bash
# 监听所有子模块 Java 文件变化，自动增量编译，触发 DevTools 热重启
# 用法：./dev-watch.sh
# 依赖：fswatch（brew install fswatch）

ROOT="$(cd "$(dirname "$0")" && pwd)"

echo "==> 开始监听 Java 文件变化 (Ctrl+C 停止)..."
echo "==> 修改任意 .java 文件后将自动编译对应模块并触发热重启"

# 子模块 target/classes 目录（已通过 pom.xml additionalClasspathElements 加入运行时 classpath）
DOMAIN_CLASSES="$ROOT/csdn-meeting-domain/target/classes"
APP_CLASSES="$ROOT/csdn-meeting-application/target/classes"
INFRA_CLASSES="$ROOT/csdn-meeting-infrastructure/target/classes"
IFACE_CLASSES="$ROOT/csdn-meeting-interfaces/target/classes"

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
      -q --no-transfer-progress 2>&1
    if [ $? -eq 0 ]; then
      # 触发 DevTools 热重启：touch 子模块的某个 class，
      # 因为这些目录已在 additionalClasspathElements 中，DevTools 会扫描到变化并重启
      for DIR in "$DOMAIN_CLASSES" "$APP_CLASSES" "$INFRA_CLASSES" "$IFACE_CLASSES"; do
        if [ -d "$DIR" ]; then
          find "$DIR" -name "*.class" | head -1 | xargs touch 2>/dev/null
          break
        fi
      done
      echo "[$(date '+%H:%M:%S')] ✓ 编译成功，DevTools 正在热重启..."
    else
      echo "[$(date '+%H:%M:%S')] ✗ 编译失败，请检查错误"
    fi
done
