# 开发完整性对照：需求 vs 实现

> 基于《发起会议与会议信息管理产品需求说明书》与《我的会议与商业化产品需求说明书》两份 PRD 的边界进行对照。

## 一、发起会议与会议信息管理 PRD 对照

| 需求章节 | 功能点 | 实现状态 | 对应实现 |
|---------|-------|---------|---------|
| 2.1 智能化创建（AI 解析） | 上传文档/图片解析 | ✅ 已实现 | `POST /api/meetings/actions/ai-parse` |
| 2.2 活动模板创建 | 模板列表、详情、应用 | ✅ 已实现 | `MeetingTemplateController`：GET /api/meeting-templates, GET /{id} |
| 2.3 基础字段填写与智能标签 | 字段联动、智能标签推荐 | ✅ 已实现 | `POST /api/meetings/actions/suggest-tags` |
| 2.3 | 保存草稿 | ✅ 已实现 | `POST /api/meetings` (createDraft) |
| 2.3 | 提交审核 | ✅ 已实现 | `POST /api/meetings/{id}/submit` |
| 2.4 会议日程与分会场 | 日程日→环节→分会场→议题 四级结构 | ✅ 已实现 | CreateMeetingCommand/UpdateMeetingCommand 含 scheduleDays，MeetingDomainService 校验 |
| 2.4 | 日期范围、环节时间重叠校验 | ✅ 已实现 | MeetingDomainService.validateAgendaIntegrity |
| 2.4 | 议题主题必填、嘉宾可选 | ✅ 已实现 | Topic.title 必填，guests 可选 |
| 3.1 会议基本信息 | 名称、主办方、形式、场景、时间等 | ✅ 已实现 | Meeting 实体、CreateMeetingCommand/UpdateMeetingCommand |

**小结**：发起会议与会议信息管理 PRD 所涵盖功能均已实现。

---

## 二、我的会议与商业化 PRD 对照

| 需求章节 | 功能点 | 实现状态 | 对应实现 |
|---------|-------|---------|---------|
| 2.1.1 我报名的会议 | 列表、按日期倒序、默认已发布/进行中 | ✅ 已实现 | `GET /api/meetings/my-registered` (includeEnded) |
| 2.1.2 我创建的会议 | 列表、状态筛选、时间筛选 | ✅ 已实现 | `GET /api/meetings/my-created` |
| 2.1.2 | 提交审核、撤回、下架、删除 | ✅ 已实现 | submit, withdraw, takedown, delete |
| 2.1.3 我收藏的会议 | 列表、跳转详情 | ✅ 已实现 | `GET /api/meetings/my-favorites` |
| 2.2 会议详情与数据统计 | PV/UV/报名数/签到数/签到率 | ✅ 已实现 | `GET /api/meetings/{id}/statistics` |
| 2.2 | 用户画像（付费权益） | ✅ 已实现 | `GET /api/meetings/{id}/rights`, `POST /api/meetings/{id}/rights/purchase` |
| 2.3 会议简报下载 | PDF/Word 格式 | ✅ 已实现 | `GET /api/meetings/{id}/brief?format=pdf|word` |
| 2.3 | 高阶数据与权益联动 | ✅ 已实现 | MeetingBriefUseCase 根据 isPremium 控制 |
| 2.4 推广配置 | 实时估算、生成订单、查询配置 | ✅ 已实现 | PromotionController：estimate, createOrder, getPromotion |
| 2.5 参会人审核 | 双入口（列表+详情）、通过/拒绝 | ✅ 已实现 | `GET /api/meetings/{id}/registrations`, `RegistrationController` approve/reject |
| 2.5 | 审核后多渠道通知 | ✅ 已实现 | RegistrationAuditedEvent → NotificationClient |
| 权益价格配置 | 运营/运维配置 | ✅ 已实现 | `ConfigController` GET/POST /api/config/rights-price |

**小结**：我的会议与商业化 PRD 所涵盖功能均已实现。

---

## 三、与当前实现的关系说明

### 3.1 已覆盖范围
- 发起会议：AI 解析、活动模板、基础字段、智能标签、会议日程四级结构
- 我的会议三页签：我报名的、我收藏的、我创建的
- 会议状态流转：提交/撤回/下架/删除/审核通过/拒绝
- 会议详情、数据统计、用户画像权益
- 会议简报、推广配置、参会人审核、权益价格配置

### 3.2 边界外依赖（不在上述两份 PRD 内）
- **会议报名提交（用户侧）**：参会者提交报名表单（姓名、手机、公司等）创建 Registration 记录。该流程属于《会议详情与报名》PRD 范畴，当前实现中：
  - `POST /api/meetings/{id}/join` 使用 Participant，非 Registration
  - 若需完整报名流程，需新增 `POST /api/meetings/{id}/register` 类接口
- **收藏/取消收藏**：PRD 写明「复用 CSDN 现有收藏体系」，若收藏由 CSDN 统一收藏服务提供，则本系统仅需提供 `GET /api/meetings/my-favorites` 查询接口，当前已满足。

### 3.3 可进一步优化
1. **Swagger 文档**：为所有 Controller 补充 `@Operation` 描述，便于前后端协作。
2. **userId 获取方式**：部分接口通过参数传 userId，PRD 建议后续对接 CSDN 统一认证，从 Token 中获取。

---

## 四、占位实现清单（Stub / 待对接）

以下服务当前为占位实现，业务流程已打通，但未对接真实外部服务。上线前需替换为实际调用。

### 4.1 Infrastructure 层 Client（`csdn-meeting-infrastructure/.../client/`）

| 文件 | 说明 | 待对接 |
|------|------|--------|
| **AIServiceClient** | AI 解析 | 对接外部 LLM API |
| **VirusScanClient** | 文件病毒扫描 | 对接外部病毒扫描服务 |
| **SensitiveWordFilter** | 敏感词过滤 | 接入敏感词库 |
| **NLPTagClient** | 智能标签推荐 | 对接外部 NLP API |
| **NotificationClient** | 报名审核通知 | CSDN 消息中心（Push/短信/邮件/私信） |
| **AdminNotificationClient** | 管理员通知 | 真实 HTTP/消息队列 |
| **AdSystemClient** | 推广估算 | CSDN 广告系统 API |
| **UserProfileClient** | 用户画像 | 对接 CSDN 用户画像库 |
| **PaymentClient** | 收银台、支付回调 | CSDN 统一收银台 |

### 4.2 其他占位逻辑

| 位置 | 说明 |
|------|------|
| **ReportEngine** | 会议简报 PDF/Word 导出，当前返回伪字节；生产需用 iText、Apache POI 等真实库 |
| **MeetingStatisticsUseCase** | 数据统计依赖 `t_meeting_stats` 未建表，使用模拟值 |
| **MeetingBriefUseCase** | 热门议题（基于议题点击/评分）为占位 |
| **MeetingRightsPurchaseUseCase** | 支付回调为 stub，直接模拟成功 |
| **MeetingPublishPushService** | 会议发布后站内信推送，已改造为内部存储 |
| **MeetingAnalyticsService** | 埋点上报（视图切换、筛选、标签订阅、会议点击等）未对接 CSDN 数据分析平台 |

### 4.3 权限与认证 TODO

| 位置 | 说明 |
|------|------|
| **MeetingController.ensureAdmin()** | 管理员校验，待接入真实权限系统 |
| **ConfigController.ensureAdmin()** | 同上 |
| **UserSubscribeController.getCurrentUserId()** | 用户身份从请求头 `X-User-Id` 读取，待对接 CSDN 统一认证/JWT |
| **MeetingController 会议列表** | 待对接认证上下文自动获取当前登录用户 ID |

### 4.4 汇总

| 类型 | 数量 | 备注 |
|------|------|------|
| Client 占位 | 9 个 | AI、病毒扫描、敏感词、NLP、通知、广告、用户画像、支付等 |
| 业务逻辑占位 | 6 处 | 简报导出、统计表、热门议题、支付回调、推送、埋点 |
| 认证/权限 TODO | 4 处 | 管理员校验、用户身份获取 |

---

## 五、结论

在《发起会议与会议信息管理》与《我的会议与商业化》两份 PRD 定义的开发边界内，**后端功能已实现完整**。会议报名创建、收藏增删等若落在《会议详情与报名》等其他 PRD 中，需按对应 PRD 单独评估与实现。

**上线前**需完成第四章节所列占位实现的真实服务对接。
