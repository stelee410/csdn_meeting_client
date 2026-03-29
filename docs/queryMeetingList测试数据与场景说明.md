# 会议详情查询问题排查指南

## 问题描述
查询 `id=35` 的会议详情返回为空。

## 原因分析

系统有两个不同的会议详情接口：

### 1. 原有接口（按数据库ID查询）
```
GET /api/meetings/{id}
```
- 参数类型: `Long id` (数据库自增ID)
- 示例: `GET /api/meetings/35`
- 问题: 数据库中可能没有id=35的记录

### 2. 新接口（按业务ID查询详情页）
```
GET /api/meetings/{meetingId}/detail-page?userId={userId}
```
- 参数类型: `String meetingId` (业务ID，如 MT2026000001)
- 示例: `GET /api/meetings/MT2026REG0001/detail-page?userId=20001`
- 注意: 这是V1.2新增的详情页接口

## 解决方案

### 方案1：插入id=35的测试数据

执行SQL脚本：
```bash
# 方式1：使用Flyway（推荐）
# 文件已创建: db/seed/V2026__seed_meeting_id35_test_data.sql
# 启动应用时会自动执行

# 方式2：手动执行
mysql -u root -p csdn_meeting < csdn-meeting-start/src/main/resources/db/seed/V2026__seed_meeting_id35_test_data.sql
```

### 方案2：使用现有的测试数据查询

已有的测试数据会议ID：
- **会议列表查询测试数据** (seed_meeting_list_test_data.sql):
  - MT0000000001 ~ MT0000000014

- **API接口测试数据** (V2026__seed_api_test_data.sql):
  - MT2026000001 ~ MT2026000018

- **报名签到测试数据** (V2026__seed_registration_checkin_test_data.sql):
  - MT2026REG0001 ~ MT2026REG0008

**推荐查询示例**:
```bash
# 查询会议列表（返回所有会议）
POST /api/meetings/list
{
  "page": 0,
  "size": 20
}

# 查询某个具体会议详情（原有接口）
GET /api/meetings/1

# 查询会议详情页（新接口 - 需要业务ID）
GET /api/meetings/MT2026REG0001/detail-page

# 查询会议详情页（带用户信息）
GET /api/meetings/MT2026REG0001/detail-page?userId=20001
```

## 测试数据说明

### 报名签到专用测试数据 (MT2026REG0001~0008)

| 会议ID | 状态 | 名额 | 报名截止 | 签到 | 用途 |
|--------|------|------|----------|------|------|
| MT2026REG0001 | 已发布 | 856/1000 | 未截止 | ✅ | 基础测试 |
| MT2026REG0002 | 已发布 | 100/100(满) | 未截止 | ❌ | 名额已满 |
| MT2026REG0003 | 已发布 | 45/150 | 已截止 | ✅ | 报名截止 |
| MT2026REG0004 | 进行中 | 800/1500 | 已截止 | ✅ | 签到测试 |
| MT2026REG0005 | 已结束 | 300/500 | 已截止 | ✅ | 已结束 |
| MT2026REG0006 | 草稿 | 0/1000 | - | ❌ | 不可报名 |
| MT2026REG0007 | 已发布 | 50/不限 | 未截止 | ❌ | 不限名额 |
| MT2026REG0008 | 已下架 | 0/200 | - | ❌ | 已下架 |

### 报名记录测试数据

用户20001~20006在会议MT2026REG0001、0004、0005上有各种状态的报名记录。

## API调用示例

### 1. 查询会议详情（原有接口）
```bash
curl -X GET "http://localhost:8080/api/meetings/1"
```

### 2. 查询会议详情页（新接口）
```bash
# 未登录用户
curl -X GET "http://localhost:8080/api/meetings/MT2026REG0001/detail-page"

# 已登录用户（待审核状态）
curl -X GET "http://localhost:8080/api/meetings/MT2026REG0001/detail-page?userId=20001"

# 已登录用户（已通过未签到）
curl -X GET "http://localhost:8080/api/meetings/MT2026REG0001/detail-page?userId=20002"

# 已登录用户（已签到）
curl -X GET "http://localhost:8080/api/meetings/MT2026REG0004/detail-page?userId=20003"
```

### 3. 查询报名状态
```bash
curl -X GET "http://localhost:8080/api/meetings/MT2026REG0001/registration-status"
```

### 4. 提交报名
```bash
curl -X POST "http://localhost:8080/api/registrations" \
  -H "Content-Type: application/json" \
  -d '{
    "meetingId": "MT2026REG0001",
    "userId": 30001,
    "formData": {
      "name": "张三",
      "phone": "13812345678",
      "email": "zhangsan@example.com",
      "company": "测试公司",
      "position": "工程师"
    }
  }'
```

## 常见问题

### Q1: 为什么GET /api/meetings/35返回404？
A: 数据库中没有id=35的记录。请先执行测试数据脚本，或者使用id=1查询。

### Q2: 为什么GET /api/meetings/MT2026REG0001/detail-page报错？
A: 确保使用了正确的业务ID格式（以MT开头的字符串），而不是数字ID。

### Q3: 如何查看有哪些会议可以测试？
A: 调用会议列表接口：
```bash
POST /api/meetings/list
{
  "page": 0,
  "size": 50
}
```

### Q4: 用户ID怎么获取？
A: 当前版本从RequestParam传入。测试数据中的用户ID：20001~20006，30001~30003。

## 相关SQL验证

```sql
-- 查看所有会议
SELECT id, meeting_id, title, status, current_participants, max_participants 
FROM t_meeting 
WHERE is_deleted = 0;

-- 查看会议id=35是否存在
SELECT * FROM t_meeting WHERE id = 35;

-- 查看业务ID为MT2026REG0001的会议
SELECT * FROM t_meeting WHERE meeting_id = 'MT2026REG0001';

-- 查看报名记录
SELECT * FROM t_registration WHERE meeting_id = 1;

-- 查看收藏记录
SELECT * FROM t_meeting_favorite WHERE meeting_id = 1;
```
