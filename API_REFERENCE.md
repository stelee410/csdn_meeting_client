# CSDN Meeting API Reference

本文档为 CSDN 会议平台后端服务的完整 API 参考，适用于 Vibe Coding（AI 辅助编码）场景。文档遵循 OpenAI API 参考格式。

---

## 概览

**Base URL**：`http://{SERVER_HOST}:{NGINX_PORT}/api`

**协议**：HTTP/HTTPS，所有请求/响应体均使用 `application/json`（文件上传接口除外）。

**认证方式**（当前版本）：
- 大部分接口通过请求参数 `userId` 或请求头 `X-User-Id` 传递用户标识
- 统计类接口需在请求头携带 `Authorization: Bearer <token>`
- 管理员接口暂为占位实现，后续对接 CSDN 统一鉴权

**通用响应结构**：

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

---

## 枚举值速查

### 会议形式 `format`
| 值 | 说明 |
|----|------|
| `1` | 线上（ONLINE） |
| `2` | 线下（OFFLINE） |
| `3` | 线上+线下（HYBRID） |

### 会议类型 `type`
| 值 | 说明 |
|----|------|
| `1` | 技术峰会（SUMMIT） |
| `2` | 技术沙龙（SALON） |
| `3` | 技术研讨会（WORKSHOP） |

### 会议场景 `scene`
| 值 | 说明 |
|----|------|
| `1` | 开发者会议 |
| `2` | 产业会议 |
| `3` | 产品发布会议 |
| `4` | 区域营销会议 |
| `5` | 高校会议 |

### 会议状态 `status`
| 值 | 说明 |
|----|------|
| `DRAFT` | 草稿 |
| `PENDING_REVIEW` | 审核中 |
| `REJECTED` | 审核拒绝 |
| `PUBLISHED` | 已发布 |
| `IN_PROGRESS` | 进行中 |
| `OFFLINE` | 已下架 |
| `CANCELLED` | 已取消 |
| `ENDED` | 已结束 |
| `DELETED` | 已删除 |

### 时间范围 `timeRange`（列表筛选）
| 值 | 说明 |
|----|------|
| `0` | 全部 |
| `1` | 本周 |
| `2` | 本月 |
| `3` | 未来三个月 |

---

## 会议管理 `/api/meetings`

---

### 创建会议草稿

`POST /api/meetings`

创建一个处于草稿（DRAFT）状态的会议，创建后可继续编辑，提交审核后才会对外发布。

**请求体**

```json
{
  "title": "string",            // 必填，会议标题
  "description": "string",     // 必填，会议描述（富文本 HTML）
  "creatorId": "string",       // 必填，创建者用户 ID
  "creatorName": "string",     // 必填，创建者昵称
  "startTime": "2025-06-01T09:00:00", // 必填，开始时间（ISO 8601）
  "endTime": "2025-06-01T18:00:00",   // 必填，结束时间（ISO 8601）
  "maxParticipants": 500,      // 可选，最大参与人数，默认不限制
  "organizer": "string",       // 可选，主办方名称
  "format": 1,                 // 可选，会议形式（1线上/2线下/3混合）
  "scene": 1,                  // 可选，会议场景（1-5）
  "venue": "string",           // 可选，举办地点
  "regions": "[\"北京\",\"上海\"]", // 可选，面向区域（JSON 字符串）
  "coverImage": "string",      // 可选，封面图片 URL
  "tags": "1,2,3",             // 可选，标签 ID，逗号分隔
  "targetAudience": "{\"roles\":[\"developer\"]}", // 可选，目标受众（JSON 字符串）
  "isPremium": false,          // 可选，是否高阶权益会议
  "scheduleDays": [            // 可选，会议日程（四级结构）
    {
      "scheduleDate": "2025-06-01",
      "dayLabel": "第一天",
      "sessions": [
        {
          "sessionName": "主论坛",
          "startTime": "09:00",
          "endTime": "12:00",
          "subVenues": [
            {
              "subVenueName": "A 会场",
              "topics": [
                {
                  "title": "演讲主题",
                  "topicIntro": "主题简介",
                  "involvedProducts": "相关产品",
                  "guests": "嘉宾信息"
                }
              ]
            }
          ]
        }
      ]
    }
  ]
}
```

**响应体**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "meetingId": "MTG20250601001",
    "title": "CSDN 技术峰会 2025",
    "status": "DRAFT",
    "creatorId": "user123",
    "creatorName": "张三",
    "startTime": "2025-06-01T09:00:00",
    "endTime": "2025-06-01T18:00:00",
    "format": 1,
    "scene": 1,
    "coverImage": "https://...",
    "tags": "1,2,3",
    "isPremium": false,
    "scheduleDays": [ ... ]
  }
}
```

---

### 更新会议

`PUT /api/meetings/{id}`

更新指定 ID 的会议信息，仅在草稿或已驳回状态下可更新。

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 会议 ID |

**请求体**

与"创建会议"相同，但不需要 `creatorId` 和 `creatorName`。

```json
{
  "title": "string",
  "description": "string",
  "startTime": "2025-06-01T09:00:00",
  "endTime": "2025-06-01T18:00:00",
  "maxParticipants": 500,
  "organizer": "string",
  "format": 1,
  "scene": 1,
  "venue": "string",
  "regions": "[\"北京\"]",
  "coverImage": "string",
  "tags": "1,2,3",
  "targetAudience": "{}",
  "isPremium": false,
  "scheduleDays": [ ... ]
}
```

**响应体**：同"创建会议"响应体。

---

### 获取会议详情

`GET /api/meetings/{id}`

返回会议的完整详情信息。

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 会议 ID |

**响应体**

```json
{
  "code": 200,
  "data": {
    "meetingId": "MTG20250601001",
    "title": "CSDN 技术峰会 2025",
    "description": "<p>会议详情...</p>",
    "posterUrl": "https://...",
    "organizerId": "org001",
    "organizerName": "CSDN",
    "organizerAvatar": "https://...",
    "format": 1,
    "formatName": "线上",
    "meetingType": 1,
    "meetingTypeName": "技术峰会",
    "scene": 1,
    "sceneName": "开发者会议",
    "cityCode": "110000",
    "cityName": "北京",
    "venue": "国家会议中心",
    "startTime": "2025-06-01T09:00:00",
    "endTime": "2025-06-01T18:00:00",
    "status": "PUBLISHED",
    "statusName": "已发布",
    "hotScore": 9850,
    "hotScoreDisplay": "9850热度",
    "currentParticipants": 328,
    "maxParticipants": 500,
    "participantsDisplay": "328/500人",
    "tags": [
      {
        "tagId": 1,
        "tagName": "云原生",
        "categoryId": 10,
        "categoryName": "技术方向",
        "subscribed": true
      }
    ],
    "createTime": "2025-05-01T10:00:00",
    "updateTime": "2025-05-20T16:30:00"
  }
}
```

---

### 会议列表查询

`POST /api/meetings/list`

支持多维度筛选、关键词搜索、分页的会议列表接口（推荐使用此接口）。

**请求体**

```json
{
  "keyword": "string",    // 可选，关键词搜索（标题/描述）
  "format": 0,            // 可选，会议形式（0全部/1线上/2线下/3混合）
  "type": 0,              // 可选，会议类型（0全部/1峰会/2沙龙/3研讨会）
  "scene": 0,             // 可选，会议场景（0全部/1-5具体场景）
  "timeRange": 0,         // 可选，时间范围（0全部/1本周/2本月/3未来三个月）
  "page": 0,              // 可选，页码，从 0 开始，默认 0
  "size": 20,             // 可选，每页数量，默认 20，最大 50
  "userId": "string"      // 可选，当前用户 ID（用于展示报名状态等个性化信息）
}
```

**响应体**

```json
{
  "code": 200,
  "data": {
    "total": 128,
    "page": 0,
    "size": 20,
    "totalPages": 7,
    "empty": false,
    "emptyTip": null,
    "suggestions": [],
    "items": [
      {
        "id": 1,
        "meetingId": "MTG20250601001",
        "title": "CSDN 技术峰会 2025",
        "description": "会议摘要...",
        "coverImage": "https://...",
        "organizerId": "org001",
        "organizerName": "CSDN",
        "organizerAvatar": "https://...",
        "statusId": 2,
        "status": "PUBLISHED",
        "statusDisplay": "已发布",
        "startTime": "2025-06-01T09:00:00",
        "endTime": "2025-06-01T18:00:00",
        "timeDisplay": "6月1日 09:00",
        "format": 1,
        "formatId": 1,
        "formatDisplay": "线上",
        "meetingType": 1,
        "meetingTypeId": 1,
        "meetingTypeDisplay": "技术峰会",
        "scene": 1,
        "sceneId": 1,
        "sceneDisplay": "开发者会议",
        "tags": [
          { "tagId": 1, "tagName": "云原生" }
        ],
        "hotScore": 9850,
        "hotScoreDisplay": "9850热度",
        "currentParticipants": 328,
        "maxParticipants": 500,
        "participantsDisplay": "328/500人",
        "cityName": "北京",
        "venue": "国家会议中心",
        "publishTime": "2025-05-20T16:30:00"
      }
    ]
  }
}
```

---

### 获取筛选选项

`GET /api/meetings/filter-options`

获取列表页所有筛选维度的枚举选项（format/type/scene/timeRange）。

**响应体**

```json
{
  "code": 200,
  "data": {
    "formats": [
      { "value": 0, "label": "全部" },
      { "value": 1, "label": "线上" },
      { "value": 2, "label": "线下" },
      { "value": 3, "label": "线上+线下" }
    ],
    "types": [
      { "value": 0, "label": "全部" },
      { "value": 1, "label": "技术峰会" },
      { "value": 2, "label": "技术沙龙" },
      { "value": 3, "label": "技术研讨会" }
    ],
    "scenes": [
      { "value": 0, "label": "全部" },
      { "value": 1, "label": "开发者会议" },
      { "value": 2, "label": "产业会议" },
      { "value": 3, "label": "产品发布会议" },
      { "value": 4, "label": "区域营销会议" },
      { "value": 5, "label": "高校会议" }
    ],
    "timeRanges": [
      { "value": 0, "label": "全部" },
      { "value": 1, "label": "本周" },
      { "value": 2, "label": "本月" },
      { "value": 3, "label": "未来三个月" }
    ]
  }
}
```

---

### 提交审核

`POST /api/meetings/{id}/submit`

将草稿（DRAFT）或驳回（REJECTED）状态的会议提交审核，状态变为 `PENDING_REVIEW`。

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 会议 ID |

**响应体**

```json
{
  "code": 200,
  "message": "提交成功，等待审核"
}
```

---

### 撤回审核

`POST /api/meetings/{id}/withdraw`

将处于审核中（PENDING_REVIEW）的会议撤回，状态恢复为草稿（DRAFT）。

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 会议 ID |

**响应体**

```json
{ "code": 200, "message": "已撤回" }
```

---

### 审核通过（管理员）

`POST /api/meetings/{id}/approve`

管理员审核通过，会议状态变为 `PUBLISHED`。

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 会议 ID |

**响应体**

```json
{ "code": 200, "message": "审核通过" }
```

---

### 审核拒绝（管理员）

`POST /api/meetings/{id}/reject`

管理员拒绝审核，会议状态变为 `REJECTED`，并附带拒绝原因。

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 会议 ID |

**请求体**

```json
{
  "reason": "string"  // 必填，拒绝原因
}
```

**响应体**

```json
{ "code": 200, "message": "已拒绝" }
```

---

### 下架会议

`POST /api/meetings/{id}/takedown`

将已发布的会议下架，状态变为 `OFFLINE`，附带下架原因。

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 会议 ID |

**请求体**

```json
{
  "reason": "string"  // 必填，下架原因
}
```

---

### 开始会议

`POST /api/meetings/{id}/start`

将已发布（PUBLISHED）的会议标记为进行中（IN_PROGRESS）。

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 会议 ID |

---

### 结束会议

`POST /api/meetings/{id}/end`

将进行中（IN_PROGRESS）的会议标记为已结束（ENDED）。

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 会议 ID |

---

### 取消会议

`POST /api/meetings/{id}/cancel`

取消会议，状态变为 `CANCELLED`。

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 会议 ID |

---

### 删除会议

`DELETE /api/meetings/{id}`

逻辑删除会议（软删除），状态变为 `DELETED`，不可恢复。

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 会议 ID |

**响应体**

```json
{ "code": 200, "message": "删除成功" }
```

---

### 报名参加会议

`POST /api/meetings/{id}/join`

用户报名参加指定会议。

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 会议 ID |

**请求体**

```json
{
  "meetingId": 1,           // 必填，会议 ID（与路径参数一致）
  "userId": "string",       // 必填，用户 ID
  "userName": "string"      // 必填，用户昵称
}
```

**响应体**

```json
{ "code": 200, "message": "报名成功" }
```

---

### 取消报名

`POST /api/meetings/{id}/leave`

用户取消参加指定会议。

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 会议 ID |

**查询参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `userId` | `String` | 必填，用户 ID |

**响应体**

```json
{ "code": 200, "message": "已取消报名" }
```

---

### 获取会议报名列表

`GET /api/meetings/{id}/registrations`

获取指定会议的报名列表，支持按状态筛选和分页。

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 会议 ID |

**查询参数**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `status` | `String` | — | 可选，报名状态（PENDING/APPROVED/REJECTED） |
| `page` | `Integer` | `0` | 页码，从 0 开始 |
| `size` | `Integer` | `20` | 每页数量 |

**响应体**

```json
{
  "code": 200,
  "data": {
    "total": 50,
    "page": 0,
    "size": 20,
    "items": [
      {
        "registrationId": "REG001",
        "meetingId": "MTG20250601001",
        "userId": "user123",
        "userName": "张三",
        "status": "APPROVED",
        "registrationTime": "2025-05-10T14:00:00",
        "auditRemark": null
      }
    ]
  }
}
```

---

### 获取会议数据统计

`GET /api/meetings/{id}/statistics`

获取会议的数据统计（浏览量、报名量、参与者等），需要 Bearer Token 认证。

**请求头**

| 字段 | 说明 |
|------|------|
| `Authorization` | `Bearer <token>` |

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 会议 ID |

**响应体**

```json
{
  "code": 200,
  "data": {
    "meetingId": "MTG20250601001",
    "viewCount": 12840,
    "registrationCount": 328,
    "participantCount": 298,
    "favoriteCount": 156,
    "shareCount": 89,
    "hotScore": 9850
  }
}
```

---

### 获取权益状态

`GET /api/meetings/{id}/rights`

查询指定会议的高阶权益使用状态。

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 会议 ID |

**响应体**

```json
{
  "code": 200,
  "data": {
    "meetingId": "MTG20250601001",
    "isPremium": true,
    "rightsEnabled": true,
    "rightsExpireTime": "2025-07-01T00:00:00",
    "rightsItems": ["AI推广", "数据分析", "专属客服"]
  }
}
```

---

### 购买高阶权益

`POST /api/meetings/{id}/rights/purchase`

为指定会议购买高阶权益，返回支付收银台 URL。

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 会议 ID |

**查询参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `userId` | `String` | 必填，购买用户 ID |

**响应体**

```json
{
  "code": 200,
  "data": {
    "orderId": "ORD20250601001",
    "payUrl": "https://pay.csdn.net/checkout?orderId=ORD20250601001",
    "amount": 999.00,
    "currency": "CNY"
  }
}
```

---

### 导出会议简报

`GET /api/meetings/{id}/brief`

导出会议简报，支持 PDF 和 Word 格式，返回文件流。

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 会议 ID |

**查询参数**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `format` | `String` | `pdf` | 导出格式（`pdf` / `word`） |

**响应**：二进制文件流，Content-Type 为 `application/pdf` 或 `application/vnd.openxmlformats-officedocument.wordprocessingml.document`。

---

### AI 智能解析文档

`POST /api/meetings/actions/ai-parse`

上传会议相关文档（PDF/Word/图片），AI 自动提取会议信息并返回结构化数据，可用于快速填充创建表单。

**请求格式**：`multipart/form-data`

| 字段 | 类型 | 说明 |
|------|------|------|
| `file` | `File` | 必填，待解析文件（PDF/DOCX/JPG/PNG） |

**响应体**

```json
{
  "code": 200,
  "data": {
    "title": "AI 提取的会议标题",
    "description": "AI 提取的会议描述",
    "startTime": "2025-06-01T09:00:00",
    "endTime": "2025-06-01T18:00:00",
    "venue": "国家会议中心",
    "organizer": "CSDN",
    "tags": ["云原生", "AI"],
    "confidence": 0.92
  }
}
```

---

### 智能标签推荐

`POST /api/meetings/actions/suggest-tags`

根据会议标题和描述，AI 推荐相关标签。

**请求体**

```json
{
  "title": "string",        // 必填，会议标题
  "description": "string"  // 可选，会议描述
}
```

**响应体**

```json
{
  "code": 200,
  "data": {
    "suggestedTags": [
      { "tagId": 1, "tagName": "云原生", "confidence": 0.95 },
      { "tagId": 2, "tagName": "Kubernetes", "confidence": 0.88 },
      { "tagId": 3, "tagName": "微服务", "confidence": 0.82 }
    ]
  }
}
```

---

### 我报名的会议

`GET /api/meetings/my-registered`

获取当前用户报名的会议列表，支持分页。

**查询参数**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `userId` | `String` | — | 必填，用户 ID |
| `includeEnded` | `Boolean` | `false` | 是否包含已结束的会议 |
| `page` | `Integer` | `0` | 页码，从 0 开始 |
| `size` | `Integer` | `20` | 每页数量 |

**响应体**：同"会议列表查询"响应体格式。

---

### 我收藏的会议

`GET /api/meetings/my-favorites`

获取当前用户收藏的会议列表，支持分页。

**查询参数**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `userId` | `String` | — | 必填，用户 ID |
| `page` | `Integer` | `0` | 页码，从 0 开始 |
| `size` | `Integer` | `20` | 每页数量 |

**响应体**：同"会议列表查询"响应体格式。

---

### 我创建的会议

`GET /api/meetings/my-created`

获取当前用户创建的会议列表，支持状态筛选、时间范围筛选和分页。

**查询参数**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `userId` | `String` | — | 必填，用户 ID |
| `status` | `String` | — | 可选，会议状态（DRAFT/PUBLISHED 等） |
| `startDate` | `String` | — | 可选，开始日期过滤（yyyy-MM-dd） |
| `endDate` | `String` | — | 可选，结束日期过滤（yyyy-MM-dd） |
| `page` | `Integer` | `0` | 页码，从 0 开始 |
| `size` | `Integer` | `20` | 每页数量 |

**响应体**：同"会议列表查询"响应体格式。

---

### 按创建者查询会议

`GET /api/meetings/creator/{creatorId}`

查询指定创建者的所有会议。

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `creatorId` | `String` | 创建者用户 ID |

**响应体**：同"会议列表查询"响应体格式。

---

## 报名审核 `/api/registrations`

---

### 审核通过报名

`POST /api/registrations/{regId}/approve`

管理员审核通过用户的报名申请。

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `regId` | `Long` | 报名记录 ID |

**响应体**

```json
{ "code": 200, "message": "审核通过" }
```

---

### 审核拒绝报名

`POST /api/registrations/{regId}/reject`

管理员拒绝用户的报名申请，附带拒绝理由。

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `regId` | `Long` | 报名记录 ID |

**请求体**

```json
{
  "auditRemark": "string"  // 必填，拒绝原因
}
```

**响应体**

```json
{ "code": 200, "message": "已拒绝" }
```

---

## 标签订阅 `/api/subscriptions`

用户可订阅感兴趣的标签，订阅后有新会议发布时将收到通知。

**认证**：请求头需携带 `X-User-Id: <userId>`

---

### 订阅标签

`POST /api/subscriptions`

**请求头**

| 字段 | 说明 |
|------|------|
| `X-User-Id` | 当前用户 ID |

**请求体**

```json
{
  "tagId": 1  // 必填，标签 ID（Long）
}
```

**响应体**

```json
{
  "code": 200,
  "data": {
    "subscribed": true,
    "tagId": 1,
    "tagName": "云原生"
  }
}
```

---

### 取消订阅标签

`DELETE /api/subscriptions`

**请求头**

| 字段 | 说明 |
|------|------|
| `X-User-Id` | 当前用户 ID |

**请求体**

```json
{
  "tagId": 1  // 必填，标签 ID（Long）
}
```

**响应体**

```json
{ "code": 200, "message": "已取消订阅" }
```

---

### 获取订阅列表

`GET /api/subscriptions`

获取当前用户已订阅的所有标签，支持分页。

**请求头**

| 字段 | 说明 |
|------|------|
| `X-User-Id` | 当前用户 ID |

**查询参数**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `page` | `Integer` | `0` | 页码，从 0 开始 |
| `size` | `Integer` | `20` | 每页数量 |

**响应体**

```json
{
  "code": 200,
  "data": {
    "total": 5,
    "page": 0,
    "size": 20,
    "items": [
      {
        "subscriptionId": 100,
        "tagId": 1,
        "tagName": "云原生",
        "categoryId": 10,
        "categoryName": "技术方向",
        "subscribeTime": "2025-05-01T10:00:00"
      }
    ]
  }
}
```

---

### 检查订阅状态

`GET /api/subscriptions/check`

检查当前用户是否已订阅指定标签。

**请求头**

| 字段 | 说明 |
|------|------|
| `X-User-Id` | 当前用户 ID |

**查询参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `tagId` | `Long` | 必填，标签 ID |

**响应体**

```json
{
  "code": 200,
  "data": {
    "tagId": 1,
    "subscribed": true
  }
}
```

---

### 获取所有已订阅标签 ID

`GET /api/subscriptions/tag-ids`

获取当前用户所有已订阅的标签 ID 列表（不分页）。

**请求头**

| 字段 | 说明 |
|------|------|
| `X-User-Id` | 当前用户 ID |

**响应体**

```json
{
  "code": 200,
  "data": [1, 2, 5, 8, 13]
}
```

---

## 推广营销 `/api/meetings/{id}/promotion`

---

### 推广受众估算

`POST /api/meetings/{id}/promotion/estimate`

根据推广条件实时估算目标受众规模和推广费用，不产生实际订单。

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 会议 ID |

**请求体**

```json
{
  "userIntents": ["AI开发", "云计算"],   // 用户意图标签列表
  "behaviorPeriod": "1m",               // 行为周期（7d/15d/1m/2m/3m）
  "targetBehaviors": ["浏览技术文章", "参与开源项目"],  // 目标行为列表
  "targetRegions": [110000, 310000],    // 目标地区代码列表（行政区划代码）
  "targetIndustries": ["互联网", "金融科技"],  // 目标行业列表
  "channels": ["EMAIL", "PUSH"],        // 推广渠道（SMS/EMAIL/PRIVATE_MSG/PUSH）
  "payMode": "CPM"                      // 计费方式（CPM/CPC/CPA）
}
```

**响应体**

```json
{
  "code": 200,
  "data": {
    "estimatedReach": 58000,
    "estimatedCost": 2900.00,
    "cpm": 50.00,
    "currency": "CNY",
    "breakdown": {
      "EMAIL": { "reach": 30000, "cost": 1500.00 },
      "PUSH":  { "reach": 28000, "cost": 1400.00 }
    }
  }
}
```

---

### 创建推广订单

`POST /api/meetings/{id}/promotion/order`

基于推广条件生成推广订单，进入待支付状态。

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 会议 ID |

**请求体**：同"推广受众估算"请求体。

**响应体**

```json
{
  "code": 200,
  "data": {
    "orderId": "PROMO20250601001",
    "payUrl": "https://pay.csdn.net/checkout?orderId=PROMO20250601001",
    "amount": 2900.00,
    "currency": "CNY",
    "expireTime": "2025-06-01T12:00:00"
  }
}
```

---

### 查询推广配置

`GET /api/meetings/{id}/promotion`

查询指定会议的当前推广配置和投放状态。

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 会议 ID |

**响应体**

```json
{
  "code": 200,
  "data": {
    "meetingId": "MTG20250601001",
    "promotionStatus": "ACTIVE",
    "orderId": "PROMO20250601001",
    "channels": ["EMAIL", "PUSH"],
    "payMode": "CPM",
    "totalBudget": 2900.00,
    "consumedBudget": 1200.00,
    "startTime": "2025-05-20T00:00:00",
    "endTime": "2025-06-01T09:00:00"
  }
}
```

---

## 会议模板 `/api/meeting-templates`

---

### 获取所有模板

`GET /api/meeting-templates`

获取所有已启用的会议模板列表。

**响应体**

```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "templateName": "技术峰会标准模板",
      "description": "适用于大型技术峰会的标准模板",
      "format": 1,
      "type": 1,
      "scene": 1,
      "scheduleDays": [ ... ],
      "enabled": true,
      "createTime": "2025-01-01T00:00:00"
    }
  ]
}
```

---

### 获取模板详情

`GET /api/meeting-templates/{id}`

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 模板 ID |

**响应体**：同上方列表中的单个 item 结构。

---

### 创建模板（管理员）

`POST /api/meeting-templates`

**请求体**

```json
{
  "name": "string",              // 必填，模板名称
  "scene": "string",             // 可选，会议场景
  "descriptionTemplate": "string",
  "defaultTags": "string",
  "targetAudience": "string",
  "meetingDuration": "half_day", // 可选，会议时长，见 /api/dictionaries
  "meetingScale": "medium",      // 可选，会议规模，见 /api/dictionaries
  "frequency": "annual",         // 可选，举办频率，见 /api/dictionaries
  "sortOrder": 0,
  "isActive": false
}
```

---

### 更新模板（管理员）

`PUT /api/meeting-templates/{id}`

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 模板 ID |

**请求体**：同"创建模板"请求体。

---

### 删除模板（管理员）

`DELETE /api/meeting-templates/{id}`

**路径参数**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 模板 ID |

**响应体**

```json
{ "code": 200, "message": "删除成功" }
```

---

## 图片上传 `/api/images`

---

## 字典接口 `/api/dictionaries`

issue001-2/6：创建会议/模板用下拉选项

---

### 获取创建会议/模板用字典

`GET /api/dictionaries`

返回会议时长、会议规模、举办频率、地域（省/市）、目标人群、开发者类型等下拉选项。

**响应体**

```json
{
  "code": 200,
  "data": {
    "meetingDurations": [
      { "value": "half_day", "label": "半天" },
      { "value": "one_day", "label": "1天" },
      { "value": "two_days", "label": "2天" }
    ],
    "meetingScales": [
      { "value": "small", "label": "50人以下" },
      { "value": "medium", "label": "50-200人" }
    ],
    "frequencies": [
      { "value": "once", "label": "一次性" },
      { "value": "annual", "label": "每年一届" }
    ],
    "regions": [
      {
        "provinceCode": "110000",
        "provinceName": "北京市",
        "cities": [{ "cityCode": "110100", "cityName": "北京市" }]
      }
    ],
    "targetAudiences": [
      { "value": "developer", "label": "开发者" },
      { "value": "architect", "label": "架构师" }
    ],
    "developerTypes": [
      { "value": "frontend", "label": "前端开发" },
      { "value": "backend", "label": "后端开发" }
    ]
  }
}
```

---

## 标签接口 `/api/tags`

---

### 热门标签

`GET /api/tags/hot`

按使用该标签的已发布会议数量降序，返回热门标签列表。issue001-9

**请求参数**

| 参数 | 类型 | 必填 | 默认 | 说明 |
|------|------|------|------|------|
| `limit` | `int` | 否 | 20 | 返回数量，1-50 |

**响应体**

```json
{
  "code": 200,
  "data": [
    { "id": 1, "name": "云原生", "category": "tech" },
    { "id": 2, "name": "Java", "category": "tech" }
  ]
}
```

---

### 上传图片

`POST /api/images/upload`

上传会议相关图片（封面图、宣传图等），返回可访问的图片 URL。

**请求格式**：`multipart/form-data`

| 字段 | 类型 | 说明 |
|------|------|------|
| `file` | `File` | 必填，图片文件（JPG/PNG/GIF/WEBP，≤ 10MB） |

**响应体**

```json
{
  "code": 200,
  "data": {
    "url": "https://your.server.ip/uploads/images/2025/06/abc123.jpg",
    "fileName": "abc123.jpg",
    "size": 204800
  }
}
```

---

## 系统配置 `/api/config`

---

### 获取权益价格（管理员）

`GET /api/config/rights-price`

**响应体**

```json
{
  "code": 200,
  "data": {
    "price": 999.00,
    "currency": "CNY",
    "updateTime": "2025-01-01T00:00:00"
  }
}
```

---

### 设置权益价格（管理员）

`POST /api/config/rights-price`

**请求体**

```json
{
  "price": 999.00  // 必填，新的高阶权益价格（BigDecimal）
}
```

**响应体**

```json
{ "code": 200, "message": "价格设置成功" }
```

---

## 会议状态流转图

```
DRAFT（草稿）
  │
  ├─[提交审核]──► PENDING_REVIEW（审核中）
  │                    │
  │               ┌────┴────┐
  │           [拒绝]      [通过]
  │               │          │
  │           REJECTED    PUBLISHED（已发布）
  │               │          │
  │         [重新提交]   ┌───┴────────┐
  │               │    [开始]      [下架]
  │               │      │          │
  │               │  IN_PROGRESS  OFFLINE
  │               │      │
  │               │    [结束]
  │               │      │
  │               │    ENDED
  │
  └─[取消]──► CANCELLED
  └─[删除]──► DELETED
```

---

## 错误码说明

| HTTP 状态码 | code | 说明 |
|-------------|------|------|
| 200 | 200 | 成功 |
| 400 | 400 | 请求参数错误 |
| 401 | 401 | 未授权（Token 无效或缺失） |
| 403 | 403 | 权限不足（非管理员操作） |
| 404 | 404 | 资源不存在 |
| 409 | 409 | 状态冲突（如重复报名、非法状态流转） |
| 500 | 500 | 服务器内部错误 |

**错误响应体示例**：

```json
{
  "code": 409,
  "message": "当前会议状态不允许此操作",
  "data": null
}
```

---

## Swagger UI

在线接口文档（开发环境）：

```
http://localhost:8080/swagger-ui.html
```
