为了确保项目能够保质保量完成，我们将采用敏捷开发（Agile）模式，将整个后端开发划分为 **4 个 Sprint（冲刺阶段）**。每个 Sprint 为期 1-2 周，旨在快速交付可运行的领域模型和接口。

---

# 📅 文档三：开发阶段与 Sprint 迭代计划

## 1. 项目结构映射（目录与职责）

依据 `csdn-meeting-client` 实际 Maven 多模块结构，各层与路径对应关系如下：

```
csdn-meeting-client/
├── csdn-meeting-domain/              # 领域层
│   └── src/main/java/com/csdn/meeting/domain/
│       ├── entity/                   # 聚合根、实体
│       ├── valueobject/              # 值对象
│       ├── event/                    # 领域事件
│       ├── repository/               # 仓储接口（契约）
│       └── service/                  # 领域服务
├── csdn-meeting-application/         # 应用层
│   └── src/main/java/com/csdn/meeting/application/
│       ├── dto/                      # 命令、DTO
│       └── service/                  # 应用服务（用例编排）
├── csdn-meeting-infrastructure/      # 基础设施层
│   └── src/main/java/com/csdn/meeting/infrastructure/
│       ├── po/                       # JPA 持久化对象
│       ├── repository/               # Spring Data JPA 接口
│       ├── repository/impl/          # 仓储实现（实现 domain 契约）
│       ├── client/                   # 外部系统客户端（AI/广告/通知）
│       ├── task/                     # 定时任务
│       ├── report/                   # 简报引擎
│       └── config/                   # 配置类
├── csdn-meeting-interfaces/          # 接口层
│   └── src/main/java/com/csdn/meeting/interfaces/
│       ├── controller/               # REST 控制器
│       ├── dto/                      # 接口 DTO（ApiResponse 等）
│       └── exception/                # 全局异常处理
└── csdn-meeting-start/               # 启动层
    └── src/main/resources/           # application.yml, log4j2.xml
```

**依赖方向**：`interfaces` → `application` → `domain` ← `infrastructure`

---

## 2. 现有资产与改造点

| 已有资产 | 路径 | 改造/扩展点 |
|---------|------|------------|
| Meeting 实体 | `domain/entity/Meeting.java` | 扩展状态枚举为 8 个；新增 organizer/format/scene/venue/regions/coverImage/tags/targetAudience/isPremium/takedownReason/rejectReason 字段；补充 submit/withdraw/approve/reject/takedown/delete 方法 |
| MeetingPO | `infrastructure/po/MeetingPO.java` | 同上字段同步 |
| MeetingDomainService | `domain/service/MeetingDomainService.java` | 扩展：四级日程完整性校验、状态流转业务规则 |
| MeetingRepository | `domain/repository/MeetingRepository.java` | 增加按状态/创建人/时间范围查询 |
| MeetingController | `interfaces/controller/MeetingController.java` | 补充：submit/withdraw/approve/reject/takedown 接口；我的会议三页签；统计/推广/报名审核等 |
| GlobalExceptionHandler | `interfaces/exception/GlobalExceptionHandler.java` | 补充：BusinessException、422（AI 解析）、400（日程校验字段级定位）、403 |

**API 路径约定**：统一使用 `/api` 前缀（不带 v1）。

---

## 3. Sprint 1：核心领域模型与「发起会议」基础 (奠基阶段)

**目标**：在现有 DDD 结构上，将 Meeting 升级为完整聚合根，实现完整字段、四级日程与草稿流程；建立活动模板库基础。

### 领域层 (csdn-meeting-domain)

| 任务 | 文件路径 | 说明 |
|-----|---------|------|
| 扩展 Meeting 实体 | `domain/entity/Meeting.java` | 增加所有字段（organizer/format/scene/venue/regions/coverImage/description/tags/targetAudience/isPremium/takedownReason/rejectReason）；将 MeetingStatus 扩展为 8 个枚举值 |
| 四级议程实体 | `domain/entity/ScheduleDay.java`, `Session.java`, `SubVenue.java`, `Topic.java` | 可建独立实体或嵌套在 Meeting 内；与 `t_meeting_agenda_item` 树状结构对应 |
| 状态流转方法 | `domain/entity/Meeting.java` | `submit()` DRAFT→PENDING_REVIEW（含日程完整性校验）；`withdraw()` PENDING_REVIEW→DRAFT；预留 approve/reject/takedown/delete |
| 活动模板实体 | `domain/entity/MeetingTemplate.java` | 字段：name/scene/descriptionTemplate/defaultTags/targetAudience/sortOrder/isActive |
| 领域服务扩展 | `domain/service/MeetingDomainService.java` | 新增 `validateAgendaIntegrity(Meeting)` 方法（校验四级完整性，见 agent.prd §3.3）；新增 `generateMeetingId()` |
| 仓储接口 | `domain/repository/MeetingTemplateRepository.java` | 新建；支持 findAllActive、findById |

### 基础设施层 (csdn-meeting-infrastructure)

| 任务 | 文件路径 | 说明 |
|-----|---------|------|
| 会议表 PO | `infrastructure/po/MeetingPO.java` | 同步 Meeting 新增字段；状态改为 TINYINT；`@PrePersist`/`@PreUpdate` |
| 日程树 PO | `infrastructure/po/MeetingAgendaItemPO.java` | 新建；`id, meeting_id, parent_id, level, title, sort_order, extra(JSON)` |
| 模板 PO | `infrastructure/po/MeetingTemplatePO.java` | 新建 |
| JPA 仓储 | `infrastructure/repository/MeetingAgendaItemJpaRepository.java` | 新建；`findByMeetingIdOrderBySortOrder()`；`findByParentId()` |
| JPA 仓储 | `infrastructure/repository/MeetingTemplateJpaRepository.java` | 新建；`findByIsActiveTrue()` |
| 仓储实现 | `infrastructure/repository/impl/MeetingRepositoryImpl.java` | 扩展：级联读写四级日程；按状态/创建人查询 |
| 仓储实现 | `infrastructure/repository/impl/MeetingTemplateRepositoryImpl.java` | 新建 |

### 接口层 (csdn-meeting-interfaces)

| 任务 | 文件路径 | 说明 |
|-----|---------|------|
| 创建/更新草稿 | `controller/MeetingController.java` | `POST /api/meetings`（仅校验 title）；`PUT /api/meetings/{id}` |
| 查询详情 | `controller/MeetingController.java` | `GET /api/meetings/{id}`（含四级日程） |
| 提交审核 | `controller/MeetingController.java` | `POST /api/meetings/{id}/submit` |
| 活动模板 | `controller/MeetingTemplateController.java` | 新建；`GET /api/meeting-templates`；管理员 CRUD |

### 应用层 (csdn-meeting-application)

| 任务 | 文件路径 | 说明 |
|-----|---------|------|
| 创建/更新用例 | `application/service/MeetingApplicationService.java` | 编排：创建草稿、更新字段、提交审核 |
| 模板用例 | `application/service/MeetingTemplateUseCase.java` | 新建；获取模板列表、应用模板创建草稿 |
| DTO 扩展 | `application/dto/CreateMeetingCommand.java` | 补充所有新增字段 + 四级日程结构字段 |
| DTO 扩展 | `application/dto/MeetingDTO.java` | 补充所有响应字段 + 四级日程 |

### 交付物与验收

- 办会方可创建含完整字段 + 四级日程的会议草稿（保存草稿仅需标题）
- 提交审核时完整性校验正确（日程缺失返回字段级错误定位）
- 活动模板可查询、预览；运营员可维护模板
- `t_meeting`、`t_meeting_agenda_item`、`t_meeting_template` 表结构与 agent.prd DDL 一致

---

## 4. Sprint 2：AI 辅助、智能标签与完整状态机 (智能化阶段)

**目标**：集成 AI 解析与 NLP 标签推荐；完善全部状态流转（含撤回/拒绝/下架）；接入定时任务自动切换状态。

### 领域层 (csdn-meeting-domain)

| 任务 | 文件路径 | 说明 |
|-----|---------|------|
| 完善状态流转 | `domain/entity/Meeting.java` | `approve()`; `reject(reason)`; `resubmit()`; `takedown(reason)` PUBLISHED/IN_PROGRESS→OFFLINE; `delete()` 逻辑删除 |
| 领域事件 | `domain/event/MeetingEndedEvent.java` | 新建；会议进入 ENDED 触发，供简报引擎监听 |
| 领域事件 | `domain/event/MeetingStatusChangedEvent.java` | 新建；通用状态变更事件（审计日志用） |
| AI 解析端口 | `domain/service/AIParsePort.java`（接口） | 定义在 domain 层；实现在 infrastructure 层 |
| NLP 标签端口 | `domain/service/NLPTagPort.java`（接口） | 定义在 domain 层；实现在 infrastructure 层 |

### 应用层 (csdn-meeting-application)

| 任务 | 文件路径 | 说明 |
|-----|---------|------|
| AI 解析用例 | `application/service/AIParsingUseCase.java` | 编排：文件上传→病毒扫描→文本提取→LLM解析→敏感词过滤→返回 AIParseResultDTO（traceId/filledFields/sensitiveFields） |
| 标签推荐用例 | `application/service/TagSuggestionUseCase.java` | 新建；调用 NLP 端口返回 3-5 个推荐标签 |
| 状态流转用例 | `application/service/MeetingApplicationService.java` | 补充：withdraw/approve/reject/takedown/delete 方法 |
| DTO | `application/dto/AIParseResultDTO.java` | 新建；含 traceId/data(MeetingDTO)/filledFields[]/sensitiveFields[] |
| DTO | `application/dto/TagSuggestionDTO.java` | 新建；含 tags[] |

### 基础设施层 (csdn-meeting-infrastructure)

| 任务 | 文件路径 | 说明 |
|-----|---------|------|
| AI 客户端 | `infrastructure/client/AIServiceClient.java` | 新建；实现 AIParsePort；对接外部 LLM API；15 秒超时 → 抛出 AIParseException |
| 文件安全 | `infrastructure/client/VirusScanClient.java` | 新建；文件病毒扫描 |
| 敏感词过滤 | `infrastructure/client/SensitiveWordFilter.java` | 新建；对 AI 回填内容进行敏感词检测，返回含敏感词的字段列表 |
| NLP 客户端 | `infrastructure/client/NLPTagClient.java` | 新建；实现 NLPTagPort；调用 NLP API 返回推荐标签 |
| 定时任务 | `infrastructure/task/MeetingStatusScheduleTask.java` | 新建；`@Scheduled`：`PUBLISHED` → `IN_PROGRESS`（按 startTime）；`IN_PROGRESS` → `ENDED`（按 endTime，触发后发布 MeetingEndedEvent） |
| 审计日志 | `infrastructure/` AOP 或 EventListener | 监听 MeetingStatusChangedEvent，记录 Log4j2 审计日志 |

### 接口层 (csdn-meeting-interfaces)

| 任务 | 文件路径 | 说明 |
|-----|---------|------|
| AI 解析接口 | `controller/MeetingController.java` | `POST /api/meetings/actions/ai-parse`；MultipartFile 上传；解析失败 422 |
| 标签推荐接口 | `controller/MeetingController.java` | `POST /api/meetings/actions/suggest-tags`；返回推荐标签列表 |
| 撤回/审核/下架 | `controller/MeetingController.java` | `POST /api/meetings/{id}/withdraw`；`/approve`；`/reject`；`/takedown` |

### 交付物与验收

- 上传 PDF/Word/图片可解析并回填表单（15 秒超时降级）
- 填写标题+简介后可获取 NLP 推荐标签（3-5 个）
- 完整状态机运转正确：提交→撤回→审核通过/拒绝→自动开始/结束→下架→逻辑删除
- 审核、下架等关键操作审计日志正常记录

---

## 5. Sprint 3：我的会议、报名管理与收藏 (管理阶段)

**目标**：实现我的会议三页签、参会人审核双入口及多渠道通知。

### 领域层 (csdn-meeting-domain)

| 任务 | 文件路径 | 说明 |
|-----|---------|------|
| Registration 实体 | `domain/entity/Registration.java` | 状态：PENDING/APPROVED/REJECTED；含 name/phone/email/company/position/auditedAt/auditRemark |
| MeetingFavorite 实体 | `domain/entity/MeetingFavorite.java` | 新建；关联 userId + meetingId；UK(userId, meetingId) |
| 仓储接口 | `domain/repository/RegistrationRepository.java` | 按 meetingId + status 分页查询；findByUserIdAndMeetingId |
| 仓储接口 | `domain/repository/MeetingFavoriteRepository.java` | 新建；findByUserIdOrderByCreatedAtDesc；existsByUserIdAndMeetingId |
| 审核事件 | `domain/event/RegistrationAuditedEvent.java` | 新建；携带 meetingId/userId/auditResult(APPROVED/REJECTED)，供消息通知异步消费 |

### 应用层 (csdn-meeting-application)

| 任务 | 文件路径 | 说明 |
|-----|---------|------|
| 我报名的会议 | `application/service/MyMeetingsUseCase.java` | 新建；`getMyRegistered(userId, includeEnded, pageable)`：按会议日期倒序，默认 status IN [PUBLISHED,IN_PROGRESS] |
| 我收藏的会议 | `application/service/MyMeetingsUseCase.java` | `getMyFavorites(userId, pageable)`：查询收藏列表 |
| 我创建的会议 | `application/service/MyMeetingsUseCase.java` | `getMyCreated(userId, status, startDate, endDate, pageable)` |
| 参会人审核 | `application/service/RegistrationAuditUseCase.java` | 新建；`approve(regId, auditorId)` / `reject(regId, auditorId, remark)`；发布 RegistrationAuditedEvent |
| DTO | `application/dto/RegistrationDTO.java`, `RegistrationAuditCommand.java` | 新建 |

### 基础设施层 (csdn-meeting-infrastructure)

| 任务 | 文件路径 | 说明 |
|-----|---------|------|
| RegistrationPO | `infrastructure/po/RegistrationPO.java` | 新建；`t_registration` 全字段 |
| MeetingFavoritePO | `infrastructure/po/MeetingFavoritePO.java` | 新建；`t_meeting_favorite` |
| JPA 仓储 | `infrastructure/repository/RegistrationJpaRepository.java` | 新建；按 meetingId + status 分页 |
| JPA 仓储 | `infrastructure/repository/MeetingFavoriteJpaRepository.java` | 新建；按 userId 查询 |
| 仓储实现 | `infrastructure/repository/impl/RegistrationRepositoryImpl.java` | 新建 |
| 仓储实现 | `infrastructure/repository/impl/MeetingFavoriteRepositoryImpl.java` | 新建 |
| 通知客户端 | `infrastructure/client/NotificationClient.java` | 新建；监听 RegistrationAuditedEvent，对接 CSDN 消息中心，发送 Push/短信/邮件/私信（站内信） |

### 接口层 (csdn-meeting-interfaces)

| 任务 | 文件路径 | 说明 |
|-----|---------|------|
| 我报名的会议 | `controller/MeetingController.java` | `GET /api/meetings/my-registered?includeEnded=false&page=&size=` |
| 我收藏的会议 | `controller/MeetingController.java` | `GET /api/meetings/my-favorites?page=&size=` |
| 我创建的会议 | `controller/MeetingController.java` | `GET /api/meetings/my-created?status=&startDate=&endDate=&page=&size=` |
| 报名列表 | `controller/RegistrationController.java` | 新建；`GET /api/meetings/{id}/registrations?status=PENDING` |
| 审核通过/拒绝 | `controller/RegistrationController.java` | `POST /api/registrations/{regId}/approve`；`/reject` |

### 交付物与验收

- 我的会议三页签数据正确：我报名的（按日期倒序，默认不含已结束）、我收藏的（含收藏态同步）、我创建的（支持状态/时间筛选）
- 双入口（列表操作列 + 会议详情页）进入同一报名审核页
- 审核通过/拒绝后报名状态更新正确；多渠道通知（Push/短信/邮件/私信）发送成功

---

## 6. Sprint 4：商业化权益、推广配置与简报 (变现阶段)

**目标**：完成商业化闭环，含高阶数据权益购买、推广配置、实时估算与会议简报。

### 领域层 (csdn-meeting-domain)

| 任务 | 文件路径 | 说明 |
|-----|---------|------|
| MeetingRights 实体 | `domain/entity/MeetingRights.java` | 新建；rightsType=DATA_PREMIUM；status=ACTIVE/INACTIVE；orderNo |
| MeetingBill 实体 | `domain/entity/MeetingBill.java` | 新建；feeType=PROMOTION/DATA_RIGHTS；amount；payStatus；invoiceStatus |
| PromotionConfig 实体 | `domain/entity/PromotionConfig.java` | 新建；包含推广配置全部字段（§1.7 in agent.prd）；计费方式 CPM/CPC/CPA |
| 权益校验 | `domain/entity/Meeting.java` | `isAdvancedDataAvailable()` 方法：return isPremium |
| 权益端口 | `domain/service/PaymentPort.java`（接口） | 定义在 domain；实现在 infrastructure；负责唤起 CSDN 统一收银台 |
| 广告系统端口 | `domain/service/AdSystemPort.java`（接口） | 定义在 domain；调用广告系统估算接口 |
| 仓储接口 | `domain/repository/MeetingRightsRepository.java`, `MeetingBillRepository.java`, `PromotionConfigRepository.java` | 新建 |

### 应用层 (csdn-meeting-application)

| 任务 | 文件路径 | 说明 |
|-----|---------|------|
| 数据统计用例 | `application/service/MeetingStatisticsUseCase.java` | 新建；`getStatistics(meetingId)`：基础数据（PV/UV/报名/签到/签到率/近7日趋势）；高阶画像数据受 `isPremium` 控制（false 时 advanced=null，premiumRequired=true） |
| 权益购买用例 | `application/service/MeetingRightsPurchaseUseCase.java` | 新建；调用 PaymentPort 唤起收银台；支付成功回调：更新 MeetingRights（ACTIVE）+ Meeting.isPremium=true + 记录 MeetingBill |
| 推广配置用例 | `application/service/PromotionUseCase.java` | 新建；`estimate(config)`（实时估算）；`createOrder(config)`（生成订单，通知管理后台，85折倒计时） |
| 简报用例 | `application/service/MeetingBriefUseCase.java` | 新建；聚合数据、判断权益、调用 ReportEngine 生成 PDF/Word |
| DTO | `application/dto/MeetingStatisticsDTO.java`, `PromotionConfigCommand.java`, `PromotionEstimateDTO.java`, `MeetingRightsDTO.java` | 新建 |

### 基础设施层 (csdn-meeting-infrastructure)

| 任务 | 文件路径 | 说明 |
|-----|---------|------|
| PO | `infrastructure/po/MeetingRightsPO.java`, `MeetingBillPO.java`, `PromotionConfigPO.java` | 新建 |
| JPA 仓储 | `infrastructure/repository/MeetingRightsJpaRepository.java`, `MeetingBillJpaRepository.java`, `PromotionConfigJpaRepository.java` | 新建 |
| 仓储实现 | `infrastructure/repository/impl/` 对应实现类 | 新建 |
| 广告系统客户端 | `infrastructure/client/AdSystemClient.java` | 新建；实现 AdSystemPort；对接 CSDN 广告系统估算 API；同步推广点击/报名数据到 t_promotion_stats |
| 支付客户端 | `infrastructure/client/PaymentClient.java` | 新建；实现 PaymentPort；唤起 CSDN 统一收银台；提供支付回调处理 |
| CSDN 用户画像客户端 | `infrastructure/client/UserProfileClient.java` | 新建；当 isPremium=true 时，传入报名 userId 列表，获取聚合画像（公司/职级/行业/技术栈） |
| 权益价格配置 | `infrastructure/config/RightsPriceConfig.java` | 新建；提供 `/api/config/rights-price`（GET/POST）接口，运营端配置权益价格 |
| 简报引擎 | `infrastructure/report/ReportEngine.java` | 新建；监听 MeetingEndedEvent；聚合 t_meeting + t_meeting_agenda_item + t_registration + t_promotion_stats；若 isPremium 则调用 UserProfileClient；渲染 Markdown → PDF（iText）/ Word（Apache POI） |
| 管理后台通知 | `infrastructure/client/AdminNotificationClient.java` | 新建；推广订单生成时通知管理后台（HTTP 回调或消息队列） |

### 接口层 (csdn-meeting-interfaces)

| 任务 | 文件路径 | 说明 |
|-----|---------|------|
| 数据统计 | `controller/MeetingController.java` | `GET /api/meetings/{id}/statistics`；Bearer Token；advanced 按 isPremium 控制 |
| 权益查询 | `controller/MeetingController.java` | `GET /api/meetings/{id}/rights` |
| 权益购买 | `controller/MeetingController.java` | `POST /api/meetings/{id}/rights/purchase` |
| 推广估算 | `controller/PromotionController.java` | 新建；`POST /api/meetings/{id}/promotion/estimate` |
| 推广订单 | `controller/PromotionController.java` | `POST /api/meetings/{id}/promotion/order` |
| 推广查询 | `controller/PromotionController.java` | `GET /api/meetings/{id}/promotion` |
| 简报下载 | `controller/MeetingController.java` | `GET /api/meetings/{id}/brief?format=pdf|word` |

### 交付物与验收

- `Meeting.isPremium` 正确控制画像数据接口的返回（未购买时 advanced=null）
- 推广配置实时估算：调用广告系统返回预计覆盖/曝光/点击/总价
- 推广订单生成：记录 orderCreatedAt，支付时 85 折逻辑正确，管理后台收到通知
- 会议简报正确生成（PDF/Word），高阶章节按权益控制展示

---

## 7. 项目里程碑 (Milestones)

| 里程碑 | 时间点 | 验收标准 |
|-------|-------|---------|
| **M1** | Sprint 1 结束 | 核心数据表结构锁定（`t_meeting`/`t_meeting_agenda_item`/`t_meeting_template`）；支持手动创建含四级日程的会议草稿；活动模板 CRUD 可用 |
| **M2** | Sprint 2 结束 | AI 解析成功率 ≥80%；NLP 标签推荐可用；完整状态机（DRAFT→DELETED 全路径）运转正确；定时任务自动切换状态正常 |
| **M3** | Sprint 3 结束 | 我的会议三页签数据正确；参会人审核双入口可用；多渠道通知（Push/短信/邮件/私信）发送成功 |
| **M4** | Sprint 4 结束 | 全量功能测试完成；权益购买、推广配置、简报下载均可用；具备上线变现能力 |

---

## 8. AI 提示 (For Vibe Coding)

> 1. 执行每个 Sprint 前，先检查 `csdn-meeting-domain` 是否已包含对应业务规则，再开始 `application` 层编排。
> 2. 严禁在 `interfaces` 层编写业务逻辑；Controller 仅做参数校验 + 鉴权 + 调用 ApplicationService。
> 3. **状态机以 agent.prd.md §3.1 为准**；**DDL 以 agent.prd.md §5 为准**；**接口路径以 agent.prd.md §2 为准**。
> 4. 使用 MapStruct 完成 Entity ↔ PO、Entity ↔ DTO 转换，PO 与 Domain Entity 严格分离。
> 5. 外部系统（AI/NLP/广告/支付/通知）的调用接口定义在 domain 层（Port 接口），实现放在 infrastructure/client 下，通过 Spring 依赖注入。
> 6. 关键状态流转（审核/下架/权益购买/推广订单）必须写 Log4j2 审计日志。
