# 系统消息分类功能实现总结

## 概述

本次变更在现有消息模块基础上，通过在 `biz_type` 字段增加 "SYSTEM" 类型，支持系统消息（如平台公告、服务协议更新等）与会议消息的分类区分，满足 UI 设计中"全部消息/会议提醒/系统通知"三个标签页的筛选需求。

## 变更文件清单

### 1. Domain 层

#### UserMessage.java
**路径**: `csdn-meeting-domain/src/main/java/com/csdn/meeting/domain/entity/UserMessage.java`

**变更内容**:
- 扩展 `MessageType` 枚举，新增系统消息类型：
  - `SYSTEM_NOTICE(4, "系统公告")`
  - `SYSTEM_UPDATE(5, "服务更新")`

**影响**: 支持新的消息类型编码 4 和 5，向后兼容现有类型 1-3。

#### MessagePushPort.java
**路径**: `csdn-meeting-domain/src/main/java/com/csdn/meeting/domain/port/MessagePushPort.java`

**变更内容**:
- 扩展 `MessageType` 枚举，新增：
  - `SYSTEM_NOTICE`
  - `SYSTEM_UPDATE`

**影响**: 端口层定义与实体层保持一致。

#### UserMessageRepository.java
**路径**: `csdn-meeting-domain/src/main/java/com/csdn/meeting/domain/repository/UserMessageRepository.java`

**变更内容**:
- 新增 `findByUserIdAndBizType(String userId, String bizType, int page, int size)` 方法
- 新增 `findUnreadByUserIdAndBizType(String userId, String bizType, int page, int size)` 方法

**影响**: 支持按业务类型（MEETING/SYSTEM）筛选查询。

### 2. Infrastructure 层

#### InternalMessagePushClient.java
**路径**: `csdn-meeting-infrastructure/src/main/java/com/csdn/meeting/infrastructure/client/InternalMessagePushClient.java`

**变更内容**:
1. `convertMessageType` 方法：新增系统消息类型的转换逻辑
2. `detectBizType` 方法：系统消息类型返回 "SYSTEM"
3. `tryWebSocketPush` 方法：WebSocket 推送消息格式标准化，新增 `messageId` 字段

**WebSocket 推送格式标准化**:
```json
{
  "type": "NEW_MESSAGE",
  "messageId": "MSG2026032912345678",
  "messageType": "MEETING_PUBLISH",
  "bizType": "MEETING",
  "title": "AI技术峰会",
  "bizId": "MT202603290001",
  "unreadCount": 5,
  "timestamp": 1711699200000
}
```

**字段说明**:
- `type`: 固定为 `NEW_MESSAGE`
- `messageId`: 消息唯一标识（新增，用于前端去重和跳转）
- `messageType`: 消息类型（MEETING_PUBLISH, REGISTRATION_APPROVED, REGISTRATION_REJECTED, SYSTEM_NOTICE, SYSTEM_UPDATE）
- `bizType`: 业务分类（MEETING/REGISTRATION/SYSTEM）- 用于前端分类展示
- `title`: 消息标题
- `bizId`: 关联业务ID（如会议ID）
- `unreadCount`: 用户总未读数
- `timestamp`: 推送时间戳

**影响**: 前端可基于 `bizType` 字段实时分类显示新消息，无需重新拉取列表。

#### MessageWebSocketHandler.java（格式统一改造）
**路径**: `csdn-meeting-interfaces/src/main/java/com/csdn/meeting/interfaces/websocket/MessageWebSocketHandler.java`

**变更内容**:
- `sendNewMessageNotification` 方法参数扩展，新增 `messageType`, `bizType`, `bizId` 参数
- 推送消息格式与 `InternalMessagePushClient` 统一

**影响**: 保证所有 WebSocket 推送入口的消息格式一致，前端无需处理不同格式。

#### UserMessageRepositoryImpl.java
**路径**: `csdn-meeting-infrastructure/src/main/java/com/csdn/meeting/infrastructure/repository/impl/UserMessageRepositoryImpl.java`

**变更内容**:
- 实现 `findByUserIdAndBizType` 方法
- 实现 `findUnreadByUserIdAndBizType` 方法
- 业务类型筛选逻辑：
  - `bizType="MEETING"` 查询 `MEETING` 和 `REGISTRATION`
  - `bizType="SYSTEM"` 查询 `SYSTEM`

**影响**: 实现按业务类型聚合查询（会议提醒包含会议发布和报名通知）。

### 3. Interfaces 层

#### MessageController.java
**路径**: `csdn-meeting-interfaces/src/main/java/com/csdn/meeting/interfaces/controller/MessageController.java`

**变更内容**:
- `GET /api/messages` 接口新增 `bizType` 查询参数
- 支持三种查询模式：
  - 不传 `bizType` → 查询全部消息
  - `bizType=MEETING` → 查询会议相关（`MEETING` + `REGISTRATION`）
  - `bizType=SYSTEM` → 查询系统消息

**API 使用示例**:
| 前端标签页 | API 调用 |
|----------|---------|
| 全部消息 | `GET /api/messages?page=1&size=20` |
| 会议提醒 | `GET /api/messages?bizType=MEETING&page=1&size=20` |
| 系统通知 | `GET /api/messages?bizType=SYSTEM&page=1&size=20` |

#### MessageVO.java
**路径**: `csdn-meeting-interfaces/src/main/java/com/csdn/meeting/interfaces/vo/MessageVO.java`

**变更内容**:
- 更新 `messageType` 字段 Swagger 注解：增加 4-系统公告、5-服务更新
- 更新 `bizType` 字段 Swagger 注解：说明支持 MEETING/REGISTRATION/SYSTEM

**影响**: Swagger 文档自动更新，前端开发人员可查看最新接口定义。

### 4. Database 层

#### V24__add_system_message_type.sql
**路径**: `csdn-meeting-start/src/main/resources/db/migration/V24__add_system_message_type.sql`

**变更内容**:
- 更新 `t_user_message` 表注释，说明新增消息类型
- 创建 `idx_biz_type_user` 索引，优化按业务类型查询性能

**注意**: 由于 SQL 脚本首次执行时使用了不兼容的 `IF NOT EXISTS` 语法，导致 Flyway 迁移失败。已修复为 MySQL 标准语法。

## 架构流程

### 消息推送流程
```
业务服务 → MessagePushPort.sendSiteMessage() 
    → InternalMessagePushClient
        → 保存到数据库
        → WebSocket 推送（包含 bizType 字段）
```

### 消息查询流程
```
前端消息中心 → GET /api/messages?bizType=
    → MessageController
        → bizType=MEETING: 查询 MEETING + REGISTRATION
        → bizType=SYSTEM: 查询 SYSTEM
        → 不传: 查询全部
    → UserMessageRepository
        → MyBatis-Plus 分页查询
```

## 关键设计决策

### 1. biz_type 枚举值设计
- `MEETING` - 会议发布通知
- `REGISTRATION` - 报名审核通知
- `SYSTEM` - 系统通知（平台公告、服务更新等）

### 2. 前端分类映射
- "全部消息" → 不传 `bizType` 参数
- "会议提醒" → `bizType=MEETING`（后端实际查询 `MEETING` + `REGISTRATION`）
- "系统通知" → `bizType=SYSTEM`

### 3. 向后兼容性
- 现有消息类型编码 1-3 保持不变
- 现有 `biz_type` 值 `MEETING`/`REGISTRATION` 保持不变
- WebSocket 新增 `bizType` 字段不会破坏现有前端逻辑

## 数据库迁移修复

### 问题
首次执行 V24 迁移脚本时使用了 `CREATE INDEX IF NOT EXISTS` 语法，这是 SQLite 语法，MySQL 不支持，导致迁移失败。

### 修复方案
将 SQL 脚本改为 MySQL 标准语法：
```sql
ALTER TABLE `t_user_message` COMMENT = '...';
CREATE INDEX `idx_biz_type_user` ON `t_user_message` (`user_id`, `biz_type`, `created_at`);
```

### 后续操作
由于 Flyway 已记录 V24 执行失败，需要手动修复后才能重新执行：

```sql
-- 1. 手动执行 V24 的 SQL 语句
ALTER TABLE `t_user_message` COMMENT = '用户消息表（站内信）- 支持系统内部消息推送，消息类型：1-会议发布 2-报名通过 3-报名拒绝 4-系统公告 5-服务更新';
CREATE INDEX `idx_biz_type_user` ON `t_user_message` (`user_id`, `biz_type`, `created_at`);

-- 2. 更新 Flyway Schema History 表
UPDATE flyway_schema_history 
SET success = 1, checksum = null 
WHERE version = '24' AND success = 0;
```

## 编译验证

所有代码变更已通过 Maven 编译验证：
```
mvn clean compile -DskipTests
[INFO] BUILD SUCCESS
```

## 后续建议

1. **系统消息发送服务**: 需要实现具体的系统消息发送逻辑，调用 `MessagePushPort.sendSiteMessage()` 并传入 `SYSTEM_NOTICE` 或 `SYSTEM_UPDATE` 类型。

2. **前端适配**: 前端需要根据 WebSocket 推送消息中的 `bizType` 字段，实时将新消息显示在正确的分类标签页中。

3. **消息清理策略**: 系统消息是否需要不同的清理策略（如系统公告长期保留），可在 `MessageCleanupJob` 中扩展。

4. **系统消息管理后台**: 建议开发管理后台功能，支持运营人员发布系统公告。

## 变更统计

- 修改 Java 文件：7 个（含 WebSocket 格式统一改造）
- 新建 SQL 文件：1 个
- 新增接口方法：4 个
- 新增枚举值：4 个（实体层 2 个 + 端口层 2 个）
- WebSocket 字段标准化：`messageId`, `messageType`, `bizType`, `title`, `bizId`, `unreadCount`, `timestamp`
- 向后兼容：是
