#!/usr/bin/env bash
# 启动 csdn-meeting-client 本地开发环境
#
# 用法：
#   ./dev.sh            启动完整开发环境（MySQL + Spring Boot + 热重载监听）
#   ./dev.sh app        仅编译并启动 Spring Boot（不管 MySQL）
#   ./dev.sh db         仅启动 MySQL 容器
#   ./dev.sh watch      仅启动热重载文件监听（需先运行 app）
#   ./dev.sh stop       停止 MySQL 容器
#   ./dev.sh build      仅编译，不启动

set -e

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

# ── 颜色 ──────────────────────────────────────────────────────────────
BOLD='\033[1m'; RESET='\033[0m'
GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; RED='\033[0;31m'

log()   { echo -e "${CYAN}[dev]${RESET} $*"; }
ok()    { echo -e "${GREEN}[dev]${RESET} $*"; }
warn()  { echo -e "${YELLOW}[dev]${RESET} $*"; }
error() { echo -e "${RED}[dev]${RESET} $*"; exit 1; }

# ── 环境检查 ───────────────────────────────────────────────────────────
check_java() {
  command -v java &>/dev/null || error "未找到 java，请安装 JDK 8+"
  JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1-2)
  ok "Java: $JAVA_VER"
}

check_mvn() {
  command -v mvn &>/dev/null || error "未找到 mvn，请安装 Maven 3.6+"
  MVN_VER=$(mvn -version 2>&1 | head -1 | awk '{print $3}')
  ok "Maven: $MVN_VER"
}

check_docker() {
  command -v docker &>/dev/null || { warn "未找到 docker，跳过 MySQL 容器管理"; return 1; }
  docker info &>/dev/null 2>&1 || { warn "Docker 未运行，跳过 MySQL 容器管理"; return 1; }
  return 0
}

# ── MySQL 容器 ─────────────────────────────────────────────────────────
start_db() {
  if ! check_docker; then
    warn "请确保本地 MySQL 3306 已启动（用户: root / 密码: root / 库: csdn_meeting）"
    return
  fi

  log "启动 MySQL 容器..."
  docker compose -f docker-compose-dev.yml up -d

  log "等待 MySQL 就绪..."
  local retries=30
  until docker exec csdn-meeting-mysql-dev mysqladmin ping -h localhost -u root -proot --silent 2>/dev/null; do
    retries=$((retries - 1))
    [ $retries -le 0 ] && error "MySQL 启动超时，请检查 docker-compose-dev.yml"
    sleep 1
    echo -n "."
  done
  echo ""
  ok "MySQL 已就绪（localhost:3306）"
}

stop_db() {
  log "停止 MySQL 容器..."
  docker compose -f docker-compose-dev.yml down
  ok "MySQL 容器已停止"
}

# ── Maven 编译 ─────────────────────────────────────────────────────────
build() {
  log "编译项目（跳过测试）..."
  mvn clean package -DskipTests --no-transfer-progress -q
  ok "编译完成"
}

# ── 启动 Spring Boot ───────────────────────────────────────────────────
start_app() {
  log "启动 Spring Boot（profile: dev，端口: 8080）..."
  echo ""
  # -am: 同时编译所有依赖模块，Spring Boot 直接从各模块 target/classes 加载
  # 这样 DevTools 能监听所有模块的类变化，dev-watch.sh 的热重载才真正生效
  mvn spring-boot:run \
    -pl csdn-meeting-start \
    -Dspring-boot.run.profiles=dev \
    --no-transfer-progress
}

# ── 热重载监听 ─────────────────────────────────────────────────────────
start_watch() {
  if ! command -v fswatch &>/dev/null; then
    warn "未找到 fswatch，热重载监听不可用"
    warn "安装方法：brew install fswatch"
    return
  fi
  log "启动热重载文件监听..."
  exec "$ROOT/dev-watch.sh"
}

# ── 打印启动信息 ───────────────────────────────────────────────────────
print_info() {
  echo ""
  echo -e "${BOLD}  开发环境地址：${RESET}"
  echo -e "  ${GREEN}▶  API        ${RESET}http://localhost:8080"
  echo -e "  ${GREEN}▶  Swagger UI ${RESET}http://localhost:8080/swagger-ui/index.html"
  echo -e "  ${GREEN}▶  健康检查   ${RESET}http://localhost:8080/actuator/health"
  echo ""
  echo -e "  数据库: localhost:3306 / csdn_meeting (root/root)"
  echo ""
  echo -e "  ${BOLD}热重载：${RESET}新开一个终端，运行 ${CYAN}./dev.sh watch${RESET}"
  echo -e "  按 ${BOLD}Ctrl+C${RESET} 停止服务"
  echo ""
}

# ── 入口 ───────────────────────────────────────────────────────────────
main() {
  local cmd="${1:-}"

  case "$cmd" in
    db)
      start_db
      ;;
    stop)
      stop_db
      ;;
    build)
      check_java
      check_mvn
      build
      ;;
    app)
      check_java
      check_mvn
      print_info
      start_app
      ;;
    watch)
      start_watch
      ;;
    "")
      check_java
      check_mvn
      start_db
      build
      print_info
      start_app
      ;;
    *)
      echo "用法: ./dev.sh [db|app|watch|stop|build]"
      echo ""
      echo "  （无参数）  启动完整环境（MySQL + 编译 + Spring Boot）"
      echo "  db          仅启动 MySQL 容器"
      echo "  app         仅编译并启动 Spring Boot（需 MySQL 已就绪）"
      echo "  watch       启动热重载文件监听（需 app 已运行）"
      echo "  build       仅编译，不启动"
      echo "  stop        停止 MySQL 容器"
      exit 1
      ;;
  esac
}

main "$@"
