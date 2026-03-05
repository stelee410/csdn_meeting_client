这份文档是专门为 **AI 辅助编程（Vibe Coding / Agentic Workflow）** 优化的深度技术规约。它将业务需求转化为机器可理解的逻辑规则、架构约束和领域模型，旨在让 AI 模型（如 Claude 3.5 Sonnet, Cursor Agent 等）能以极低的幻觉率直接生成符合生产标准的后端代码。

---

# 📄 文档二：AI 驱动开发（Vibe Coding）专项技术规约

**版本：** 2.0

**适用对象：** LLM / AI Coding Agents / 后端架构师

**权威性说明：** 本文档为开发阶段的**唯一事实来源**，优先级高于 README 与 human.prd.md。

---

## 1. 领域模型（Domain Model）定义

### 1.1 核心聚合根：`Meeting`

所有状态流转与内容变更必须通过此聚合根进行。

* **标识（Identity）**：`MeetingId` (Long)
* **状态枚举（States）**：
  * `DRAFT`(0) — 草稿/新建
  * `PENDING_REVIEW`(1) — 待审核
  * `PUBLISHED`(2) — 已发布
  * `IN_PROGRESS`(3) — 进行中
  * `ENDED`(4) — 已结束
  * `REJECTED`(5) — 已拒绝
  * `OFFLINE`(6) — 已下架
  * `DELETED`(7) — 已删除（逻辑删除）

* **关键字段**：
  * `title` VARCHAR(50) NOT NULL — 会议名称（最大 50 字）
  * `organizer` VARCHAR(100) — 主办方/公司名
  * `creatorId` BIGINT NOT NULL — 创建者 ID（复用 CSDN 账号体系）
  * `format` Enum(ONLINE/OFFLINE/HYBRID) — 会议形式
  * `scene` VARCHAR(50) — 会议场景（如"开发者会议"/"区域营销"）
  * `startTime` / `endTime` DATETIME — 会议时间（精确到分钟）
  * `venue` VARCHAR(255) — 举办地址，format 含 OFFLINE 时必填
  * `regions` JSON — 涉及区域，scene = 区域营销 时必填
  * `coverImage` VARCHAR(500) — 会议海报 URL（16:9）
  * `description` TEXT — 会议简介（富文本）
  * `tags` JSON — 会议标签列表（最多 5 个）
  * `targetAudience` JSON — 适合人群标签（职级 + 技术方向）
  * `isPremium` BOOLEAN DEFAULT FALSE — 是否已购买高阶数据权益（控制用户画像和简报高阶数据可见性）
  * `takedownReason` VARCHAR(500) — 下架原因（下架时必填）
  * `rejectReason` VARCHAR(500) — 拒绝原因（管理员拒绝时必填）

* **四级内容结构（Invariants）**：
  * `ScheduleDay`（日程日）→ `Session`（环节）→ `SubVenue`（分会场）→ `Topic`（议题）
  * 业务不变量 1：`ScheduleDay.date` 必须在 Meeting.startTime ~ endTime 范围内
  * 业务不变量 2：同一 `ScheduleDay` 内 `Session` 开始/结束时间不得重叠（不跨日）
  * 业务不变量 3：提交审核时，层级完整性必须满足（见 §3.3）
  * 业务不变量 4：保存草稿时，仅校验 `title` 非空，日程可为空

---

### 1.2 `MeetingTemplate`（活动模板）

* **用途**：运营端维护，办会方只读，用于快速创建会议草稿。
* **字段**：`id`, `name`（模板名称，如"技术沙龙"）, `scene`（预置会议场景）, `descriptionTemplate`（简介骨架 Markdown）, `defaultTags`（预置标签 JSON）, `targetAudience`（预置人群标签 JSON）, `sortOrder`（展示排序）, `isActive`（是否启用）
* **权限约束**：增删改 = 管理员；查询 = 所有人

---

### 1.3 `Registration`（报名实体）

* **状态枚举**：`PENDING`（待审核）, `APPROVED`（已通过）, `REJECTED`（已拒绝）
* **字段**：
  * `id`, `meetingId`, `userId`
  * `name` VARCHAR(100) — 报名人姓名
  * `phone` VARCHAR(20) — 脱敏存储
  * `email` VARCHAR(200)
  * `company` VARCHAR(200)
  * `position` VARCHAR(100) — 职位
  * `status` VARCHAR(20) DEFAULT 'PENDING'
  * `registeredAt` TIMESTAMP
  * `auditedAt` TIMESTAMP
  * `auditRemark` VARCHAR(500) — 拒绝时的备注原因
* **业务规则**：仅 `Meeting.status = PUBLISHED` 时可对报名进行审核操作

---

### 1.4 `MeetingFavorite`（收藏）

* 复用 CSDN 现有收藏体系，`收藏类型 = 会议`；若现有体系不支持，则新建 `t_meeting_favorite`。
* **字段**：`id`, `userId`, `meetingId`, `createdAt`
* **业务规则**：取消收藏后「我收藏的会议」列表实时更新；UK(`userId`, `meetingId`)

---

### 1.5 `MeetingRights`（高阶数据权益）

* **权益类型枚举**：`DATA_PREMIUM`（数据高阶权益包）
* **字段**：`id`, `meetingId`, `rightsType`, `status`(ACTIVE/INACTIVE), `activeTime`, `orderNo`
* **业务规则**：
  * 以会议为粒度购买；会议发布后即可购买
  * 购买后：`Meeting.isPremium = true`；会议详情用户画像 + 会议简报高阶数据**同时解锁**
  * 权益价格由运营后台配置（或通过预留运维接口下发）

---

### 1.6 `MeetingBill`（账单记录）

> **本期不提供前端账单列表与发票申请功能，仅建立数据结构记录流水。**

* **费用类型枚举**：`PROMOTION`（推广费）, `DATA_RIGHTS`（数据权益费）
* **字段**：`billId`, `meetingId`, `feeType`, `amount` DECIMAL(10,2), `payStatus`(PAID/UNPAID), `invoiceStatus`(NONE/APPLIED/ISSUED), `createdAt`

---

### 1.7 `PromotionConfig`（推广配置）

* **字段**：
  * `configId`, `meetingId`
  * `userIntents` JSON — 用户意图（多选枚举）
  * `behaviorPeriod` VARCHAR(10) — 7d/15d/1m/2m/3m
  * `targetBehaviors` JSON — ["SEARCH","CREATE"]
  * `targetRegions` JSON — 城市 ID 列表（精确到市级）
  * `targetIndustries` JSON — 行业枚举列表
  * `channels` JSON — ["SMS","EMAIL","PRIVATE_MSG","PUSH"]
  * `payMode` VARCHAR(20) — CPM/CPC/CPA
  * `estimatedReach` BIGINT — 预计覆盖人数
  * `estimatedImpressions` BIGINT — 预计曝光
  * `estimatedClicks` BIGINT — 预计点击
  * `basePrice` DECIMAL(10,2) — 原价
  * `orderStatus` VARCHAR(20) — PENDING/PAID
  * `orderCreatedAt` DATETIME — 订单生成时间（用于 85 折倒计时）
* **业务规则**：仅 `Meeting.status = PUBLISHED` 时可创建/修改推广配置；订单生成后 30 分钟内支付享 85 折。

---

## 2. 接口契约（API Contracts）

### 2.1 AI 智能解析（Stateless）

* **Endpoint**: `POST /api/meetings/actions/ai-parse`
* **Input**: `MultipartFile`（pdf/docx < 20MB；jpg/png < 10MB）
* **Logic**：
  1. 文件病毒扫描（同步，失败返回 400）
  2. 提取文本（PDF → text；图片 → OCR）
  3. 调用 LLM 解析为结构化 JSON（Schema: MeetingDTO 字段集）
  4. 对回填内容进行敏感词过滤，含敏感词时标记 `sensitiveFields`
  5. 返回：`{ traceId, filledFields[], data: MeetingDTO, sensitiveFields[] }`
* **错误处理**：15 秒超时 / 格式不支持 → `422 Unprocessable Entity`；病毒扫描失败 → `400`

### 2.2 智能标签推荐（异步）

* **Endpoint**: `POST /api/meetings/actions/suggest-tags`
* **Input**: `{ "title": "xxx", "description": "xxx" }`
* **Output**: `{ "tags": ["Java", "后端开发", "微服务"] }` — 3-5 个推荐标签
* **Logic**: 调用 NLP 接口（异步），前端主动轮询或 WebSocket 推送均可

### 2.3 活动模板管理

* `GET /api/meeting-templates` — 查询模板列表（分页，支持 isActive 过滤）
* `GET /api/meeting-templates/{id}` — 获取模板详情
* `POST /api/meeting-templates` — 创建模板（**管理员权限**）
* `PUT /api/meeting-templates/{id}` — 更新模板（**管理员权限**）
* `DELETE /api/meeting-templates/{id}` — 删除/下线模板（**管理员权限**，逻辑删除）

### 2.4 会议 CRUD 与状态流转

* `POST /api/meetings` — 创建草稿（仅校验 title；状态 = DRAFT）
* `PUT /api/meetings/{id}` — 更新会议信息（DRAFT/REJECTED 状态可编辑）
* `GET /api/meetings/{id}` — 查询会议详情（含四级日程结构）
* `POST /api/meetings/{id}/submit` — 提交审核（DRAFT/REJECTED → PENDING_REVIEW；校验四级日程完整性）
* `POST /api/meetings/{id}/withdraw` — 撤回审核（PENDING_REVIEW → DRAFT；仅办会方）
* `POST /api/meetings/{id}/approve` — 审核通过（**管理员**；PENDING_REVIEW → PUBLISHED；审计日志）
* `POST /api/meetings/{id}/reject` — 审核拒绝（**管理员**；PENDING_REVIEW → REJECTED；需传 `reason`；审计日志）
* `POST /api/meetings/{id}/takedown` — 主动下架（PUBLISHED/IN_PROGRESS → OFFLINE；需传 `reason`；审计日志）
* `DELETE /api/meetings/{id}` — 逻辑删除（DRAFT/ENDED/OFFLINE/REJECTED → DELETED）

### 2.5 我的会议（三页签）

* `GET /api/meetings/my-registered?includeEnded=false&page=&size=`
  — 我报名的会议（按会议日期倒序；默认 status IN [PUBLISHED,IN_PROGRESS]；includeEnded=true 时增加 ENDED）
* `GET /api/meetings/my-favorites?page=&size=`
  — 我收藏的会议（复用现有收藏服务，type=MEETING）
* `GET /api/meetings/my-created?status=&startDate=&endDate=&page=&size=`
  — 我创建的会议（支持多状态过滤和时间范围筛选）

### 2.6 数据统计与权益

* `GET /api/meetings/{id}/statistics`
  — **Header**: `Authorization: Bearer <Token>`
  — **Response Logic**:
  ```json
  {
    "basic": {
      "pv": 1000, "uv": 500, "registrations": 80, "checkins": 60, "checkinRate": 0.75,
      "trend7d": [{ "date": "2026-03-01", "pv": 100, "registrations": 10 }]
    },
    "advanced": "$REF(Logic: if Meeting.isPremium=true return 用户画像聚合数据 else return null)",
    "premiumRequired": "$REF(Logic: if Meeting.isPremium=false return true else return false)"
  }
  ```
* `GET /api/meetings/{id}/rights` — 查询权益状态（`{ rightsType, status, activeTime }`）
* `POST /api/meetings/{id}/rights/purchase` — 购买高阶权益（唤起 CSDN 统一收银台 → 支付成功回调后更新 `MeetingRights` 和 `Meeting.isPremium`）

### 2.7 会议简报

* `GET /api/meetings/{id}/brief?format=pdf|word`
  — 触发后端聚合数据并生成文件；高阶数据章节受 `Meeting.isPremium` 控制
  — 响应：`Content-Disposition: attachment; filename=brief.pdf`（触发浏览器下载）

### 2.8 推广配置

* `POST /api/meetings/{id}/promotion/estimate` — 实时估算（输入配置参数，调用 CSDN 广告系统接口，返回 estimatedReach/Impressions/Clicks/basePrice）
* `POST /api/meetings/{id}/promotion/order` — 生成推广订单（校验 Meeting.status = PUBLISHED；存 PromotionConfig；通知管理后台留档；返回 `{ configId, orderCreatedAt, discountDeadline }`）
* `GET /api/meetings/{id}/promotion` — 查询当前推广配置与状态

### 2.9 参会人审核

* `GET /api/meetings/{id}/registrations?status=PENDING&page=&size=` — 获取报名列表（支持状态筛选：PENDING/APPROVED/REJECTED）
* `POST /api/registrations/{regId}/approve` — 审核通过（`Registration.status = APPROVED`；异步触发多渠道通知：Push/短信/邮件/私信）
* `POST /api/registrations/{regId}/reject` — 审核拒绝（`Registration.status = REJECTED`；`auditRemark` 可选；异步触发多渠道通知）

---

## 3. 核心业务逻辑实现指引

### 3.1 会议状态机（State Machine）

AI 必须严格实现以下转换逻辑，禁止跳变：

| 方法 | 源状态 | 目标状态 | 触发方 | 附加约束 |
|-----|-------|---------|------|---------|
| `submit()` | DRAFT / REJECTED | PENDING_REVIEW | 办会方 | 四级日程完整性校验（§3.3） |
| `withdraw()` | PENDING_REVIEW | DRAFT | 办会方 | 若管理员正在审核中则拒绝 |
| `approve()` | PENDING_REVIEW | PUBLISHED | 管理员 | 审计日志必记 |
| `reject(reason)` | PENDING_REVIEW | REJECTED | 管理员 | rejectReason 必填；审计日志必记 |
| `resubmit()` | REJECTED | PENDING_REVIEW | 办会方 | 重新进入审核队列 |
| `autoStart()` | PUBLISHED | IN_PROGRESS | Schedule Task | 定时任务按 startTime 触发 |
| `autoEnd()` | IN_PROGRESS | ENDED | Schedule Task | 触发后启动简报数据聚合异步任务；审计日志 |
| `takedown(reason)` | PUBLISHED / IN_PROGRESS | OFFLINE | 办会方/管理员 | takedownReason 必填；审计日志必记 |
| `delete()` | DRAFT/ENDED/OFFLINE/REJECTED | DELETED | 办会方 | 逻辑删除；status = DELETED |

### 3.2 AI 文件解析降级策略

1. 文件格式不支持 → 返回 400，提示支持格式。
2. 解析 > 15 秒超时 → 返回 422，提示切换为手动创建。
3. AI 服务不可用 → 返回 422，提示暂时不可用并保留空白表单。
4. 解析成功但部分字段未提取 → 成功字段自动回填，失败字段留空并标记 `unfilledFields`。

### 3.3 四级日程完整性校验

提交审核时（`Meeting.submit()`），`MeetingDomainService` 执行以下校验，任一失败抛 `BusinessException(AGENDA_INVALID, "...")` 并定位到具体缺失项：

1. `ScheduleDay` 数量 ≥ 1
2. 每个 `ScheduleDay` 下 `Session` 数量 ≥ 1
3. 同一 `ScheduleDay` 内 `Session` 开始/结束时间无重叠
4. 每个 `Session` 下 `SubVenue` 数量 ≥ 1
5. 每个 `(Session, SubVenue)` 组合下 `Topic` 数量 ≥ 1，且 `topic_title` 非空
6. 所有 `ScheduleDay.date` ∈ [`Meeting.startTime.date`, `Meeting.endTime.date`]

### 3.4 推广费用实时计算与订单

* 调用 CSDN 广告系统 API 传入圈选条件 → 获取预估数据。
* 生成订单时记录 `PromotionConfig.orderCreatedAt`（UTC）。
* 支付回调时计算：`now - orderCreatedAt < 30min` → `actualPrice = basePrice × 0.85`；否则按原价。
* 订单生成后立即通知管理后台（通过消息队列或 HTTP 回调）。

### 3.5 自动化简报引擎（Reporting）

* **触发**：`Meeting.autoEnd()` 执行后，发布领域事件 `MeetingEndedEvent`，由异步任务监听处理。
* **数据聚合**：
  * 基础数据：`t_meeting_stats`（PV/UV）+ `t_registration`（报名/签到数）
  * 热门议题：`t_meeting_agenda_item`（topic 层级，按点击/评分排序）
  * 推广数据：`t_promotion_stats`
  * 高阶画像（条件）：`Meeting.isPremium = true` 时调用 CSDN 用户画像库接口，传入 `registrationUserIds`，获取聚合画像（公司/职级/行业/技术栈等）
* **输出**：Markdown 模板渲染 → 转 PDF（iText）/ Word（Apache POI），存储至对象存储，提供下载链接。

### 3.6 参会人审核通知

* 审核操作（通过/拒绝）为同步操作，更新 `Registration.status`。
* 通知触发为异步：发布 `RegistrationAuditedEvent`，由消息消费者对接 CSDN 消息/通知中心，发送 Push、短信、邮件、私信（站内信）。
* 通知文案模板含会议名称和审核结果，通过配置/运营确定。

---

## 4. 技术栈限制与规范

* **架构模式**：严格遵守 `csdn-meeting-client` DDD 分层（domain / application / infrastructure / interfaces）。
* **持久化**：Spring Data JPA；PO 与 Entity 分离，必须使用 `MapStruct` 进行转化。
* **错误处理**：统一 `GlobalExceptionHandler`：
  * 业务异常：`BusinessException(ErrorCode, message)`，返回具体错误码和字段定位
  * AI 解析失败：`422 Unprocessable Entity`
  * 权限不足：`403 Forbidden`
  * 文件校验失败：`400 Bad Request`
* **日志**：以下关键操作必须记录 Log4j2 **审计日志**：审核通过/拒绝、主动下架/强制下架、权益购买、推广订单生成。
* **文件安全**：上传文件进行病毒扫描；AI 回填内容进行敏感词过滤，含敏感词字段标红提示。
* **权益价格配置**：提供 `/api/config/rights-price`（GET 查询 / POST 更新，管理员权限）接口，前端从此接口获取最新价格展示。

---

## 5. 数据库 DDL（SQL Schema）

```sql
-- 会议主表
CREATE TABLE `t_meeting` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT,
    `title`           VARCHAR(50) NOT NULL COMMENT '会议名称，最大50字',
    `organizer`       VARCHAR(100) COMMENT '主办方/公司名',
    `creator_id`      BIGINT NOT NULL COMMENT '创建者ID（复用CSDN账号）',
    `format`          VARCHAR(20) COMMENT 'ONLINE/OFFLINE/HYBRID',
    `scene`           VARCHAR(50) COMMENT '会议场景',
    `start_time`      DATETIME COMMENT '会议开始时间（精确到分钟）',
    `end_time`        DATETIME COMMENT '会议结束时间（精确到分钟）',
    `venue`           VARCHAR(255) COMMENT '举办地址（线下时必填）',
    `regions`         JSON COMMENT '涉及区域（区域营销时必填）',
    `cover_image`     VARCHAR(500) COMMENT '会议海报URL，16:9',
    `description`     TEXT COMMENT '会议简介（富文本）',
    `tags`            JSON COMMENT '会议标签列表，最多5个',
    `target_audience` JSON COMMENT '适合人群标签（职级+技术方向）',
    `status`          TINYINT DEFAULT 0 COMMENT '0-草稿 1-待审 2-已发布 3-进行中 4-已结束 5-已拒绝 6-已下架 7-已删除',
    `is_premium`      BOOLEAN DEFAULT FALSE COMMENT '是否已购买高阶数据权益',
    `takedown_reason` VARCHAR(500) COMMENT '下架原因',
    `reject_reason`   VARCHAR(500) COMMENT '拒绝原因',
    `created_at`      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 会议日程四级结构（树状存储）
-- level: 1=ScheduleDay 2=Session 3=SubVenue 4=Topic
-- extra字段按level存储不同扩展信息：
--   level=1: { "schedule_date": "2026-03-01", "day_label": "Day1" }
--   level=2: { "start_time": "09:00", "end_time": "12:00", "session_name": "上午" }
--   level=3: { "sub_venue_name": "主会场" }
--   level=4: { "guests": [], "topic_intro": "", "involved_products": "" }
CREATE TABLE `t_meeting_agenda_item` (
    `id`         BIGINT PRIMARY KEY AUTO_INCREMENT,
    `meeting_id` BIGINT NOT NULL,
    `parent_id`  BIGINT COMMENT 'NULL表示ScheduleDay；其余指向父级节点',
    `level`      TINYINT NOT NULL COMMENT '1-日程日 2-环节 3-分会场 4-议题',
    `title`      VARCHAR(200) COMMENT '节点标题（议题时为topic_title，最大100字）',
    `sort_order` INT DEFAULT 0 COMMENT '同级排序',
    `extra`      JSON COMMENT '扩展字段，按level存储对应元数据',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_meeting_id` (`meeting_id`),
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 活动模板表（运营维护，办会方只读）
CREATE TABLE `t_meeting_template` (
    `id`                   BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name`                 VARCHAR(100) NOT NULL COMMENT '模板名称，如"技术沙龙"',
    `scene`                VARCHAR(50) COMMENT '预置会议场景',
    `description_template` TEXT COMMENT '简介骨架（Markdown格式）',
    `default_tags`         JSON COMMENT '预置标签列表',
    `target_audience`      JSON COMMENT '预置适合人群标签',
    `sort_order`           INT DEFAULT 0 COMMENT '展示排序',
    `is_active`            BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    `created_at`           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at`           TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 报名记录表
CREATE TABLE `t_registration` (
    `id`            BIGINT PRIMARY KEY AUTO_INCREMENT,
    `meeting_id`    BIGINT NOT NULL,
    `user_id`       BIGINT NOT NULL,
    `name`          VARCHAR(100) COMMENT '报名人姓名',
    `phone`         VARCHAR(20) COMMENT '手机号（脱敏存储）',
    `email`         VARCHAR(200),
    `company`       VARCHAR(200) COMMENT '公司',
    `position`      VARCHAR(100) COMMENT '职位',
    `status`        VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED',
    `registered_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `audited_at`    TIMESTAMP COMMENT '审核时间',
    `audit_remark`  VARCHAR(500) COMMENT '拒绝备注',
    INDEX `idx_meeting_status` (`meeting_id`, `status`),
    UNIQUE KEY `uk_meeting_user` (`meeting_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 会议收藏表（若现有收藏体系不支持会议类型则新建）
CREATE TABLE `t_meeting_favorite` (
    `id`         BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id`    BIGINT NOT NULL,
    `meeting_id` BIGINT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_user_meeting` (`user_id`, `meeting_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 会议数据高阶权益记录表
CREATE TABLE `t_meeting_rights` (
    `id`          BIGINT PRIMARY KEY AUTO_INCREMENT,
    `meeting_id`  BIGINT NOT NULL,
    `rights_type` VARCHAR(50) COMMENT 'DATA_PREMIUM（含用户画像+简报高阶数据）',
    `status`      VARCHAR(20) COMMENT 'ACTIVE/INACTIVE',
    `active_time` DATETIME COMMENT '权益生效时间',
    `order_no`    VARCHAR(100) COMMENT '关联支付订单号',
    `created_at`  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_meeting_id` (`meeting_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 账单明细表（本期不提供前端，仅建表记录流水）
CREATE TABLE `t_meeting_bill` (
    `id`             BIGINT PRIMARY KEY AUTO_INCREMENT,
    `meeting_id`     BIGINT NOT NULL,
    `fee_type`       VARCHAR(50) COMMENT 'PROMOTION/DATA_RIGHTS',
    `amount`         DECIMAL(10,2) COMMENT '金额',
    `pay_status`     VARCHAR(20) COMMENT 'PAID/UNPAID',
    `invoice_status` VARCHAR(20) DEFAULT 'NONE' COMMENT 'NONE/APPLIED/ISSUED',
    `created_at`     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 推广配置表
CREATE TABLE `t_promotion_config` (
    `id`                    BIGINT PRIMARY KEY AUTO_INCREMENT,
    `meeting_id`            BIGINT NOT NULL,
    `user_intents`          JSON COMMENT '用户意图多选列表',
    `behavior_period`       VARCHAR(10) COMMENT '7d/15d/1m/2m/3m',
    `target_behaviors`      JSON COMMENT '["SEARCH","CREATE"]',
    `target_regions`        JSON COMMENT '目标地域城市ID列表（精确到市级）',
    `target_industries`     JSON COMMENT '目标行业枚举列表',
    `channels`              JSON COMMENT '["SMS","EMAIL","PRIVATE_MSG","PUSH"]',
    `pay_mode`              VARCHAR(20) COMMENT 'CPM/CPC/CPA',
    `estimated_reach`       BIGINT COMMENT '预计覆盖人数',
    `estimated_impressions` BIGINT COMMENT '预计曝光',
    `estimated_clicks`      BIGINT COMMENT '预计点击',
    `base_price`            DECIMAL(10,2) COMMENT '原价',
    `order_status`          VARCHAR(20) COMMENT 'PENDING/PAID',
    `order_created_at`      DATETIME COMMENT '订单生成时间（85折倒计时起点）',
    `created_at`            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_meeting_id` (`meeting_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 推广效果统计表（用于简报数据聚合）
CREATE TABLE `t_promotion_stats` (
    `id`            BIGINT PRIMARY KEY AUTO_INCREMENT,
    `meeting_id`    BIGINT NOT NULL,
    `stat_date`     DATE,
    `impressions`   INT DEFAULT 0,
    `clicks`        INT DEFAULT 0,
    `registrations` INT DEFAULT 0,
    `created_at`    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_meeting_date` (`meeting_id`, `stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

**AI 提示语补丁 (Prompt Suffix):**

> "在编写代码时，请始终参考以上 PRD 规范。优先确保 `csdn-meeting-domain` 层的业务规则完整性。状态机以第 3.1 节定义为准；DDL 以第 5 节为准；接口路径以第 2 节为准。如与 README 或 human.prd.md 存在冲突，以本文档为最高优先级。"
