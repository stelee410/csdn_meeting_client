#!/bin/bash
# =============================================================================
# CSDN Meeting - Database Patch Script
# =============================================================================
# Usage:  ./patch.sh <mysql-container-name-or-prefix>
# Sample: ./patch.sh csdn_meeting-meeting-p
#         ./patch.sh mysql
# =============================================================================

set -euo pipefail

# Force UTF-8: ensures the shell reads this script and writes temp files in UTF-8
export LANG=en_US.UTF-8
export LC_ALL=en_US.UTF-8

# ── Colors ───────────────────────────────────────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; BLUE='\033[0;34m'; NC='\033[0m'
log_info()    { echo -e "${BLUE}[patch]${NC} $1"; }
log_success() { echo -e "${GREEN}[patch]${NC} $1"; }
log_error()   { echo -e "${RED}[patch]${NC} $1" >&2; }

# ── Args ─────────────────────────────────────────────────────────────────────
CONTAINER_PATTERN="${1:-}"
if [ -z "$CONTAINER_PATTERN" ]; then
    echo "Usage: $0 <mysql-container-name-or-prefix>"
    echo "  e.g. $0 csdn_meeting-meeting-p"
    exit 1
fi

# ── DB config (matches application.yml) ──────────────────────────────────────
DB_NAME="csdn_meeting"
DB_USER="root"
DB_PASS="Aa123456"

# ── Find container ────────────────────────────────────────────────────────────
log_info "Looking for MySQL container matching: ${CONTAINER_PATTERN}"
CONTAINER_ID=$(docker ps --format '{{.ID}} {{.Names}}' \
    | grep "${CONTAINER_PATTERN}" | awk '{print $1}' | head -1)

if [ -z "$CONTAINER_ID" ]; then
    log_error "No running container matched '${CONTAINER_PATTERN}'"
    log_info "Running containers:"
    docker ps --format '  {{.Names}}\t{{.Image}}'
    exit 1
fi

CONTAINER_NAME=$(docker inspect --format '{{.Name}}' "$CONTAINER_ID" | sed 's/^\///')
log_success "Found: ${CONTAINER_NAME} (${CONTAINER_ID})"

# ── Test DB connection ────────────────────────────────────────────────────────
log_info "Testing database connection..."
if ! docker exec "$CONTAINER_ID" \
        mysql -u"$DB_USER" -p"$DB_PASS" -e "SELECT 1;" "$DB_NAME" >/dev/null 2>&1; then
    log_error "Cannot connect to MySQL. Please check the container is healthy."
    exit 1
fi
log_success "Database connection OK"

# ── Write SQL to local temp file ──────────────────────────────────────────────
# Using 'cat > file << HEREDOC' copies bytes verbatim from this script to the
# temp file. As long as patch.sh is saved as UTF-8 (standard for any modern
# editor / git), the Chinese characters in the INSERT values are preserved
# correctly without any re-encoding.
TMP_SQL=$(mktemp /tmp/csdn_patch_XXXXXX.sql)
trap 'rm -f "$TMP_SQL"' EXIT

cat > "$TMP_SQL" << 'EOF'
SET NAMES utf8mb4;

-- ============================================================
-- P1: Create table (skipped if already exists)
-- ============================================================
CREATE TABLE IF NOT EXISTS `t_activity_template` (
  `id`                    bigint(20)   NOT NULL AUTO_INCREMENT,
  `name`                  varchar(100) NOT NULL,
  `description`           varchar(500) DEFAULT NULL,
  `icon_emoji`            varchar(50)  DEFAULT NULL,
  `sort_weight`           int(11)      DEFAULT '0',
  `status`                tinyint(4)   DEFAULT '0' COMMENT '0=DRAFT 1=UNLISTED 2=LISTED',
  `use_count`             int(11)      DEFAULT '0',
  `default_meeting_type`  varchar(20)  DEFAULT NULL COMMENT 'SUMMIT/SALON/WORKSHOP',
  `default_form`          varchar(20)  DEFAULT NULL COMMENT 'ONLINE/OFFLINE/HYBRID',
  `default_scene`         varchar(50)  DEFAULT NULL,
  `default_scale`         varchar(20)  DEFAULT NULL,
  `default_duration`      varchar(20)  DEFAULT NULL,
  `default_recurrence`    varchar(20)  DEFAULT NULL COMMENT 'RECURRING/ONE_TIME',
  `default_title_prefix`  varchar(100) DEFAULT NULL,
  `default_host_company`  varchar(100) DEFAULT NULL,
  `default_department`    varchar(100) DEFAULT NULL,
  `default_contact`       varchar(100) DEFAULT NULL,
  `default_contact_title` varchar(100) DEFAULT NULL,
  `default_contact_phone` varchar(50)  DEFAULT NULL,
  `default_intro`         text,
  `cover_url`             varchar(500) DEFAULT NULL,
  `default_audience`      varchar(500) DEFAULT NULL,
  `default_tags`          varchar(500) DEFAULT NULL,
  `default_topic_skeleton`  text,
  `default_panel_skeleton`  text,
  `default_other_content`   text,
  `default_image_media`   varchar(500) DEFAULT NULL,
  `default_text_media`    varchar(500) DEFAULT NULL,
  `default_dev_type`      varchar(500) DEFAULT NULL,
  `default_industry`      varchar(50)  DEFAULT NULL,
  `default_products`      varchar(500) DEFAULT NULL,
  `default_regions`       varchar(500) DEFAULT NULL,
  `default_universities`  varchar(500) DEFAULT NULL,
  `default_location`      varchar(500) DEFAULT NULL,
  `create_time`  datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time`  datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted`   tinyint(4)   DEFAULT '0',
  `create_by`    varchar(50)  DEFAULT NULL,
  `update_by`    varchar(50)  DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_status`      (`status`),
  KEY `idx_sort_weight` (`sort_weight`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='activity template';

-- ============================================================
-- P2: Seed data  (INSERT IGNORE is idempotent)
-- ============================================================
INSERT IGNORE INTO t_activity_template
  (id, name, description, icon_emoji, sort_weight, status,
   default_meeting_type, default_form, default_scene,
   default_scale, default_duration, default_recurrence,
   default_intro, default_tags, default_audience)
VALUES
(1, '技术沙龙',
 '面向开发者的小型技术交流活动，适合分享实践经验与前沿技术',
 '🔧', 60, 2, 'SALON', 'OFFLINE', '技术交流', 'small', 'half_day', 'RECURRING',
 '## 活动简介\n\n本次技术沙龙将围绕 **[主题]** 展开深度交流，邀请业内专家分享最新技术实践与思考。\n\n## 议程安排\n\n- 19:00 嘉宾签到\n- 19:30 主题分享\n- 21:00 Q&A 交流\n- 21:30 自由交流',
 '技术沙龙,开发者交流', '后端工程师,前端工程师,架构师'),

(2, '技术峰会',
 '面向技术从业者的大型年度峰会，汇聚行业顶尖专家共探技术趋势',
 '🏔️', 50, 2, 'SUMMIT', 'OFFLINE', '行业峰会', 'xlarge', 'two_days', 'RECURRING',
 '## 峰会简介\n\n**[峰会名称]** 是面向 [技术领域] 从业者的年度技术盛会，汇聚业界顶尖技术专家，共同探讨行业趋势与技术创新。\n\n## 峰会亮点\n\n- 大咖主旨演讲\n- 多个技术专场\n- 圆桌讨论\n- 技术展区',
 '峰会,技术大会,年度盛会', 'CTO,技术总监,架构师,高级工程师'),

(3, '产品发布会',
 '新产品正式亮相的发布活动，展示核心功能与技术架构',
 '🚀', 40, 2, 'SUMMIT', 'HYBRID', '产品发布', 'medium', 'half_day', 'ONE_TIME',
 '## 发布会简介\n\n**[产品名称]** 发布会将正式揭开新产品的神秘面纱，带来全新的 [功能亮点] 体验。\n\n## 发布亮点\n\n- 产品核心功能演示\n- 技术架构解析\n- 用户案例分享\n- 现场体验互动',
 '产品发布,新品,发布会', '产品经理,开发者,企业用户'),

(4, '黑客马拉松',
 '24小时极客创新编程马拉松，组队完成创意项目并路演评奖',
 '💻', 30, 2, 'WORKSHOP', 'OFFLINE', '编程竞赛', 'medium', 'two_days', 'ONE_TIME',
 '## 活动介绍\n\n**[主题] Hackathon** 是一场以 [技术方向] 为主题的极客创新活动，参赛者将在 24 小时内组队完成创意项目。\n\n## 活动安排\n\n- Day1 10:00 开营仪式 & 组队\n- Day1 12:00 开始编码\n- Day2 10:00 停止编码\n- Day2 14:00 项目路演\n- Day2 16:00 颁奖典礼',
 'Hackathon,黑客马拉松,编程竞赛', '学生开发者,工程师,创业者'),

(5, '在线直播课',
 '系统讲解技术主题的在线直播学习课程',
 '📡', 20, 2, 'SALON', 'ONLINE', '在线教育', 'large', 'half_day', 'RECURRING',
 '## 课程简介\n\n本次直播课将系统讲解 **[技术主题]**，适合有一定基础的开发者进阶学习。\n\n## 课程内容\n\n1. 基础概念回顾\n2. 核心原理剖析\n3. 实战案例讲解\n4. 常见问题解答',
 '直播课,在线学习,技术进阶', '初级工程师,中级工程师,在校学生'),

(6, '企业开放日',
 '企业面向技术人才开放的招聘交流活动',
 '🏢', 10, 2, 'SALON', 'OFFLINE', '企业招聘', 'small', 'half_day', 'ONE_TIME',
 '## 活动介绍\n\n欢迎参加 **[公司名称] 技术开放日**！本次活动将带你深度了解我们的技术团队文化、技术栈与研发体系。\n\n## 活动环节\n\n- 公司及业务介绍\n- 技术架构分享\n- 研发文化体验\n- 工程师面对面交流\n- 参观办公环境',
 '企业开放日,校园招聘,社会招聘', '应届生,初级工程师,中级工程师');
EOF

# ── docker cp → execute inside container ─────────────────────────────────────
# docker cp transfers bytes verbatim, so the UTF-8 content is preserved.
# MySQL is told to read the file with --default-character-set=utf8mb4.
REMOTE_SQL="/tmp/csdn_patch_$$.sql"
log_info "Copying SQL file into container..."
docker cp "$TMP_SQL" "${CONTAINER_ID}:${REMOTE_SQL}"

log_info "Executing SQL patch..."
echo "---"
if docker exec "$CONTAINER_ID" sh -c \
        "mysql --default-character-set=utf8mb4 -u'${DB_USER}' -p'${DB_PASS}' '${DB_NAME}' < '${REMOTE_SQL}'"; then
    echo "---"
    log_success "SQL patch executed successfully"
else
    echo "---"
    log_error "SQL patch failed"
    docker exec "$CONTAINER_ID" rm -f "$REMOTE_SQL" 2>/dev/null || true
    exit 1
fi

docker exec "$CONTAINER_ID" rm -f "$REMOTE_SQL" 2>/dev/null || true

# ── Verify ────────────────────────────────────────────────────────────────────
log_info "Verifying results..."
COUNT=$(docker exec "$CONTAINER_ID" mysql -u"$DB_USER" -p"$DB_PASS" -N \
    -e "SELECT COUNT(*) FROM t_activity_template;" "$DB_NAME" 2>/dev/null)

echo ""
echo "  ┌──────────────────────────────────────────────────────"
printf "  │  t_activity_template: %s rows\n" "$COUNT"
docker exec "$CONTAINER_ID" \
    mysql --default-character-set=utf8mb4 -u"$DB_USER" -p"$DB_PASS" \
    -e "SELECT id, name, icon_emoji, sort_weight, status FROM t_activity_template ORDER BY id;" \
    "$DB_NAME" 2>/dev/null | sed 's/^/  │  /'
echo "  └──────────────────────────────────────────────────────"
echo ""

log_success "Patch complete!"
