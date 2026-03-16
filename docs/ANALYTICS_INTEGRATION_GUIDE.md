# CSDN会议系统 - 埋点服务集成指南

## 概述

本文档介绍如何在 CSDN 会议系统中集成埋点服务，用于记录用户行为和业务操作数据，支持数据分析和业务优化。

## 架构设计

### 核心组件

1. **AnalyticsService** - 埋点服务接口，提供各类埋点方法
2. **AnalyticsEventRepository** - 事件仓储接口，负责数据持久化
3. **AnalyticsEvent** - 事件实体，记录事件核心信息
4. **AnalyticsTaskExecutor** - 异步执行器，确保埋点不影响主业务性能

### 数据流

```
Controller/Service -> AnalyticsService -> Repository -> MySQL (analytics_event 表)
                      |
                      v
              异步执行器（不影响主流程）
```

## 快速开始

### 1. 注入埋点服务

```java
@Service
public class MeetingService {
    
    private final AnalyticsService analyticsService;
    
    public MeetingService(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }
}
```

### 2. 记录埋点事件

```java
// 会议创建埋点
analyticsService.trackMeetingCreate(userId, meetingId, meetingType);

// 报名参会埋点
analyticsService.trackMeetingRegister(userId, meetingId);

// 审核通过埋点
analyticsService.trackMeetingAuditApprove(operatorId, operatorName, meetingId, meetingTitle, organizerId);
```

## 埋点类型清单

### Client 端埋点（参会者端）

| 埋点方法 | 触发场景 | 参数说明 |
|---------|---------|---------|
| `trackMeetingListFilter` | 会议列表筛选 | userId, format, type, scene, timeRange, keyword, resultCount |
| `trackMeetingClick` | 会议点击/浏览 | userId, meetingId, source |
| `trackViewSwitch` | 视图切换 | userId, targetView |
| `trackTagSubscribe` | 标签订阅 | userId, tagId, tagName, source |
| `trackTagUnsubscribe` | 标签取消订阅 | userId, tagId, tagName, source |
| `trackMeetingCreate` | 创建会议 | userId, meetingId, meetingType |
| `trackMeetingSubmit` | 提交审核 | userId, meetingId |
| `trackMeetingPublish` | 会议发布 | userId, meetingId, organizerId |
| `trackMeetingRegister` | 报名参会 | userId, meetingId |
| `trackMeetingCheckin` | 签到 | userId, meetingId, result |
| `trackMeetingFavorite` | 收藏会议 | userId, meetingId, isAdd |

### Mobile 端埋点

| 埋点方法 | 触发场景 | 参数说明 |
|---------|---------|---------|
| `trackMobileHomeExposure` | 移动端首页曝光 | userId, source |
| `trackMobileCreateEntryClick` | 发起会议入口点击 | userId, source |
| `trackMobileMyEventsClick` | 我的会议入口点击 | userId |
| `trackMobileFavoritesTabClick` | 收藏页签点击 | userId |
| `trackMobileCheckinScan` | 签到扫码 | userId, meetingId, result |
| `trackMobileChannelAdd` | 频道添加 | userId, channelId |

### Operation 端埋点（运营端）

| 埋点方法 | 触发场景 | 参数说明 |
|---------|---------|---------|
| `trackMeetingAuditApprove` | 审核通过 | operatorId, operatorName, meetingId, meetingTitle, organizerId |
| `trackMeetingAuditReject` | 审核驳回 | operatorId, operatorName, meetingId, meetingTitle, organizerId, violationTags, comment |
| `trackMeetingTakedown` | 强制下架 | operatorId, operatorName, meetingId, meetingTitle, organizerId, violationTags, comment, originalStatus |
| `trackTemplateCreate` | 模板创建 | operatorId, templateId, templateName |
| `trackTemplateUpdate` | 模板编辑 | operatorId, templateId |
| `trackTemplateDelete` | 模板删除 | operatorId, templateId |
| `trackTemplateList` | 模板上架 | operatorId, templateId, templateName, oldStatus, newStatus |
| `trackTemplateUnlist` | 模板下架 | operatorId, templateId, templateName, oldStatus, newStatus |
| `trackDashboardView` | 查看数据看板 | operatorId, page, module |
| `trackDashboardStatsExpose` | 统计数据曝光 | operatorId, component, metrics |
| `trackDashboardPromotedMeetingsView` | 查看推广会议列表 | operatorId, pageNum, count |

## Controller 集成示例

### 会议创建接口集成

```java
@RestController
@RequestMapping("/api/meetings")
public class MeetingController {
    
    private final MeetingApplicationService meetingService;
    private final AnalyticsService analyticsService;
    
    @PostMapping
    public ResponseEntity<MeetingDTO> createMeeting(
            @RequestBody CreateMeetingCommand command,
            @RequestParam String userId) {
        
        // 1. 执行业务操作
        MeetingDTO meeting = meetingService.createDraft(command);
        
        // 2. 记录埋点（异步执行，不影响响应）
        analyticsService.trackMeetingCreate(
            userId, 
            meeting.getMeetingId(), 
            meeting.getMeetingType()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(meeting);
    }
}
```

### 审核接口集成

```java
@PostMapping("/{id}/audit")
public ResponseEntity<Void> auditMeeting(
        @PathVariable String meetingId,
        @RequestBody AuditRequest request,
        @RequestParam String operatorId,
        @RequestParam String operatorName) {
    
    // 1. 获取会议详情（用于埋点）
    MeetingDetailDTO meeting = meetingService.getMeetingDetail(meetingId);
    
    // 2. 执行审核操作
    meetingService.auditMeeting(meetingId, request, operatorId, operatorName);
    
    // 3. 根据审核结果记录埋点
    if ("APPROVE".equals(request.getAction())) {
        analyticsService.trackMeetingAuditApprove(
            operatorId,
            operatorName,
            meetingId,
            meeting.getTitle(),
            meeting.getOrganizerId()
        );
    } else if ("REJECT".equals(request.getAction())) {
        analyticsService.trackMeetingAuditReject(
            operatorId,
            operatorName,
            meetingId,
            meeting.getTitle(),
            meeting.getOrganizerId(),
            request.getViolationTags(),
            request.getComment()
        );
    }
    
    return ResponseEntity.ok().build();
}
```

## 数据库表结构

### 核心表

#### analytics_event（事件主表）

| 字段 | 类型 | 说明 |
|-----|------|------|
| id | BIGINT | 自增主键 |
| event_id | VARCHAR(64) | 事件UUID |
| event_type | VARCHAR(50) | 事件类型 |
| event_category | VARCHAR(30) | 事件类别 |
| user_id | VARCHAR(64) | 用户ID |
| user_type | TINYINT | 用户类型 |
| platform | VARCHAR(20) | 平台 |
| occurred_at | DATETIME | 发生时间 |

#### analytics_meeting_event（会议事件扩展表）

| 字段 | 类型 | 说明 |
|-----|------|------|
| id | BIGINT | 自增主键 |
| event_id | VARCHAR(64) | 关联事件ID |
| meeting_id | VARCHAR(64) | 会议ID |
| action_type | VARCHAR(30) | 操作类型 |

### 完整表结构

详见数据库迁移脚本：
- Client 端：`V15__create_analytics_tables.sql`
- Operation 端：`V5__create_operation_analytics_tables.sql`

## 性能优化

### 异步处理

所有埋点操作都采用异步方式执行：

```java
@Async("analyticsTaskExecutor")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void trackEvent(AnalyticsEvent event) {
    eventRepository.save(event);
}
```

配置参数（`AnalyticsAsyncConfig.java`）：
- 核心线程数：2
- 最大线程数：5
- 队列容量：100

### 批量插入

对于高频事件，可以使用批量插入：

```java
List<AnalyticsEvent> events = new ArrayList<>();
// 收集事件...
eventRepository.saveBatch(events);
```

## 查询分析

### 常用查询示例

```sql
-- 查询某用户最近30天的操作
SELECT * FROM analytics_event 
WHERE user_id = '123' 
AND occurred_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
ORDER BY occurred_at DESC;

-- 统计各类型事件数量
SELECT event_type, COUNT(*) as count 
FROM analytics_event 
WHERE occurred_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
GROUP BY event_type;

-- 查询会议的完整操作历史
SELECT e.*, m.action_type 
FROM analytics_event e
JOIN analytics_meeting_event m ON e.event_id = m.event_id
WHERE m.meeting_id = 'M123456'
ORDER BY e.occurred_at DESC;
```

## 最佳实践

### 1. 埋点位置选择

- 在核心业务操作成功后记录埋点
- 避免在事务内同步调用埋点（已配置为异步）
- 对于关键业务操作（支付、审核），确保埋点数据完整性

### 2. 数据一致性

- 埋点失败不应影响主业务流程
- 使用 try-catch 包裹埋点代码
- 记录埋点失败日志便于排查

### 3. 隐私合规

- 不记录敏感个人信息（手机号、身份证号等）
- IP地址等数据仅用于分析，不作其他用途
- 遵守 CSDN 数据安全规范

### 4. 扩展开发

如需添加新埋点类型：

1. 在 `AnalyticsService` 接口添加新方法
2. 在 `AnalyticsServiceImpl` 实现该方法
3. 在 `AnalyticsEvent.EventTypes` 添加常量
4. 在 Controller/Service 中调用新方法

## 注意事项

1. **CSDN 对接需求**：
   - 需要从 CSDN 统一认证获取真实用户ID（目前使用占位值）
   - 需要从 CSDN 数据分析平台获取实际的上报接口（目前仅记录到本地数据库）

2. **权限控制**：
   - 运营端埋点需要确保操作人身份真实可靠
   - 建议从 JWT Token 或 Session 获取用户身份

3. **数据清理**：
   - 建议定期归档历史埋点数据
   - 可配置数据保留策略（如保留1年）

## 后续规划

1. 对接 CSDN 数据分析平台，实现数据上报
2. 开发数据分析 Dashboard，可视化展示埋点数据
3. 添加实时统计功能，支持业务监控
4. 完善用户画像，支持个性化推荐

## 参考文档

- [埋点数据库设计文档](../.cursor/plans/埋点数据库设计_9b37a78c.plan.md)
- [MyBatis-Plus 文档](https://baomidou.com/)
- [Spring Boot 异步任务文档](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.task-execution-and-scheduling)
