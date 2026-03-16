-- ============================================
-- 会议ID=35的完整测试数据（所有字段都有值）
-- 用途：确保GET /api/meetings/35返回完整数据，无null字段
-- ============================================

-- 清理旧数据
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM t_meeting_agenda_item WHERE meeting_id = 35;
DELETE FROM t_participant WHERE meeting_id = '35';
DELETE FROM t_meeting_tag WHERE meeting_id = 35;
DELETE FROM t_registration WHERE meeting_id = 35;
DELETE FROM t_meeting_favorite WHERE meeting_id = 35;
DELETE FROM t_meeting WHERE id = 35 OR meeting_id = '35';
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 第一部分：完整的会议主表数据（所有字段填充）
-- ============================================

INSERT INTO t_meeting (
    id, meeting_id, title, description, 
    creator_id, creator_name,  -- 填充creatorName
    organizer, organizer_id, organizer_name, organizer_avatar,  -- 填充organizer
    format, scene, 
    start_time, end_time, 
    venue, city_code, city_name,  -- 城市信息
    regions,  -- JSON格式，涉及区域
    cover_image, poster_url,  -- 封面和海报
    tags, target_audience,  -- JSON格式标签和目标人群
    hot_score, current_participants, max_participants,
    status, is_premium,
    reg_end_time, checkin_code, require_checkin,  -- V12新增字段
    takedown_reason, reject_reason,  -- 原因字段（可为null，但填充默认值）
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    35,
    '35',  -- meetingId使用"35"与id一致
    '开源技术分享会',
    '开源技术分享会，欢迎所有开发者参与，名额不限。本次会议将邀请业界知名开源贡献者分享技术心得，涵盖前端、后端、DevOps等多个领域。',
    '10001',  -- creator_id
    'CSDN官方账号',  -- creator_name
    '开源中国社区',  -- organizer（填充）
    10001,  -- organizer_id
    '开源中国',  -- organizer_name
    'https://img.csdn.com/avatar/osc.png',  -- organizer_avatar
    'ONLINE',  -- format
    'UNIVERSITY',  -- scene（高校场景）
    NOW() + INTERVAL 30 DAY,  -- start_time（30天后）
    NOW() + INTERVAL 30 DAY + INTERVAL 3 HOUR,  -- end_time（持续3小时）
    '线上直播',  -- venue
    '110000',  -- city_code（北京）
    '北京',  -- city_name
    '[{"code": "110000", "name": "北京市"}, {"code": "310000", "name": "上海市"}, {"code": "440100", "name": "广州市"}]',  -- regions（涉及多个区域）
    'https://img.csdn.com/meeting/cover/opensource.jpg',  -- cover_image
    'https://img.csdn.com/meeting/cover/opensource.jpg',  -- poster_url
    '[{"id": 19, "name": "开源"}, {"id": 15, "name": "前端开发"}, {"id": 11, "name": "Python"}]',  -- tags（JSON数组格式）
    '[{"type": "level", "value": "初级工程师"}, {"type": "level", "value": "中级工程师"}, {"type": "direction", "value": "全栈开发"}]',  -- target_audience
    500,  -- hot_score（热度500）
    50,  -- current_participants（当前50人报名）
    0,  -- max_participants（0表示不限名额）
    2,  -- status（2=已发布）
    FALSE,  -- is_premium
    NOW() + INTERVAL 25 DAY,  -- reg_end_time（报名截止：开始前5天）
    'checkin_token_35_xyz123',  -- checkin_code（签到码）
    TRUE,  -- require_checkin（启用签到）
    NULL,  -- takedown_reason（未下架）
    NULL,  -- reject_reason（未拒绝）
    0,  -- is_deleted
    NOW() - INTERVAL 7 DAY,  -- created_at（7天前创建）
    NOW(),  -- updated_at
    NOW() - INTERVAL 5 DAY  -- publish_time（5天前发布）
);

-- ============================================
-- 第二部分：四级会议日程数据（scheduleDays）
-- ============================================

-- 2.1 第一级：日程日（Day1）
SET @day1_id = 0;
INSERT INTO t_meeting_agenda_item (meeting_id, parent_id, level, title, sort_order, extra, created_at)
VALUES (35, NULL, 1, 'Day 1', 1, '{"schedule_date": "2026-04-15", "day_label": "Day1"}', NOW());
SET @day1_id = LAST_INSERT_ID();

-- 2.2 第二级：上午环节（Session）
SET @session1_id = 0;
INSERT INTO t_meeting_agenda_item (meeting_id, parent_id, level, title, sort_order, extra, created_at)
VALUES (35, @day1_id, 2, '上午场', 1, '{"start_time": "09:00", "end_time": "12:00", "session_name": "上午"}', NOW());
SET @session1_id = LAST_INSERT_ID();

-- 2.3 第三级：主会场（SubVenue）
SET @venue1_id = 0;
INSERT INTO t_meeting_agenda_item (meeting_id, parent_id, level, title, sort_order, extra, created_at)
VALUES (35, @session1_id, 3, '主会场', 1, '{"sub_venue_name": "主会场"}', NOW());
SET @venue1_id = LAST_INSERT_ID();

-- 2.4 第四级：议题1（Topic）
INSERT INTO t_meeting_agenda_item (meeting_id, parent_id, level, title, sort_order, extra, created_at)
VALUES (35, @venue1_id, 4, '开源项目的商业化路径', 1, '{"guests": [{"name": "张三", "title": "开源社区负责人"}], "topic_intro": "探讨开源项目如何实现商业化可持续发展", "involved_products": "开源工具链"}', NOW());

-- 2.5 第四级：议题2（Topic）
INSERT INTO t_meeting_agenda_item (meeting_id, parent_id, level, title, sort_order, extra, created_at)
VALUES (35, @venue1_id, 4, '前端开源组件库实践', 2, '{"guests": [{"name": "李四", "title": "前端架构师"}], "topic_intro": "分享企业内部开源组件库的建设经验", "involved_products": "UI组件库"}', NOW());

-- 2.6 第二级：下午环节（Session）
SET @session2_id = 0;
INSERT INTO t_meeting_agenda_item (meeting_id, parent_id, level, title, sort_order, extra, created_at)
VALUES (35, @day1_id, 2, '下午场', 2, '{"start_time": "14:00", "end_time": "17:00", "session_name": "下午"}', NOW());
SET @session2_id = LAST_INSERT_ID();

-- 2.7 第三级：分会场1（SubVenue）
SET @venue2_id = 0;
INSERT INTO t_meeting_agenda_item (meeting_id, parent_id, level, title, sort_order, extra, created_at)
VALUES (35, @session2_id, 3, '分会场A', 1, '{"sub_venue_name": "分会场A"}', NOW());
SET @venue2_id = LAST_INSERT_ID();

-- 2.8 第四级：议题3（Topic）
INSERT INTO t_meeting_agenda_item (meeting_id, parent_id, level, title, sort_order, extra, created_at)
VALUES (35, @venue2_id, 4, 'Python开源生态发展', 1, '{"guests": [{"name": "王五", "title": "Python核心开发者"}], "topic_intro": "Python开源库的发展趋势与最佳实践", "involved_products": "Python生态工具"}', NOW());

-- ============================================
-- 第三部分：会议参与者数据（participants）
-- ============================================

INSERT INTO t_participant (user_id, user_name, meeting_id, role, status, created_at, updated_at) VALUES
(35001, '参会者001', '35', 'ATTENDEE', 'JOINED', NOW() - INTERVAL 1 DAY, NOW()),
(35002, '参会者002', '35', 'ATTENDEE', 'JOINED', NOW() - INTERVAL 2 DAY, NOW()),
(35003, '参会者003', '35', 'ATTENDEE', 'INVITED', NOW() - INTERVAL 3 DAY, NOW()),
(35004, '参会者004', '35', 'CO_HOST', 'JOINED', NOW() - INTERVAL 1 DAY, NOW()),
(35005, '参会者005', '35', 'HOST', 'JOINED', NOW(), NOW());

-- ============================================
-- 第四部分：报名记录数据（registration）
-- ============================================

INSERT INTO t_registration (
    meeting_id, user_id, name, phone, email, company, position,
    status, registered_at, audited_at, audit_remark, checkin_time, cancel_time, is_deleted
) VALUES 
-- 待审核
(35, 36001, '报名用户001', '138****0001', 'user001@csdn.net', '阿里巴巴', '工程师', 
 'PENDING', NOW() - INTERVAL 2 DAY, NULL, NULL, NULL, NULL, 0),
-- 已通过
(35, 36002, '报名用户002', '139****0002', 'user002@csdn.net', '腾讯', '产品经理',
 'APPROVED', NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 1 DAY, NULL, NULL, NULL, 0),
-- 已签到
(35, 36003, '报名用户003', '137****0003', 'user003@csdn.net', '百度', '技术总监',
 'CHECKED_IN', NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 4 DAY, NULL, NOW() - INTERVAL 1 HOUR, NULL, 0),
-- 已拒绝
(35, 36004, '报名用户004', '136****0004', 'user004@csdn.net', '字节跳动', '架构师',
 'REJECTED', NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 3 DAY, '名额已满', NULL, NULL, 0),
-- 已取消
(35, 36005, '报名用户005', '135****0005', 'user005@csdn.net', '美团', '运营',
 'CANCELLED', NOW() - INTERVAL 6 DAY, NULL, NULL, NULL, NOW() - INTERVAL 1 DAY, 0);

-- ============================================
-- 第五部分：收藏数据
-- ============================================

INSERT INTO t_meeting_favorite (user_id, meeting_id, created_at) VALUES
(37001, 35, NOW() - INTERVAL 5 DAY),
(37002, 35, NOW() - INTERVAL 3 DAY),
(37003, 35, NOW() - INTERVAL 1 DAY);

-- ============================================
-- 第六部分：签到记录
-- ============================================

INSERT INTO t_checkin_record (meeting_id, registration_id, user_id, checkin_time, checkin_method, device_info, ip_address, created_at)
SELECT 
    '35',
    id,
    user_id,
    NOW() - INTERVAL 1 HOUR,
    'QR_CODE',
    'iPhone 15 / iOS 17.0 / CSDN App 6.5.0',
    '192.168.1.100',
    NOW() - INTERVAL 1 HOUR
FROM t_registration 
WHERE meeting_id = 35 AND status = 'CHECKED_IN';

-- ============================================
-- 第七部分：会议标签关联
-- ============================================

INSERT INTO t_meeting_tag (meeting_id, tag_id, is_deleted, created_at) VALUES
(35, 19, 0, NOW()),  -- 开源
(35, 15, 0, NOW()),  -- 前端开发
(35, 11, 0, NOW());  -- Python

-- ============================================
-- 第八部分：更新会议统计信息
-- ============================================

-- 更新报名人数（根据报名记录统计）
UPDATE t_meeting 
SET current_participants = (SELECT COUNT(*) FROM t_registration WHERE meeting_id = 35 AND status IN ('PENDING', 'APPROVED', 'CHECKED_IN'))
WHERE id = 35;

-- ============================================
-- 验证查询（取消注释可查看数据）
-- ============================================

-- 验证会议主表数据完整性
-- SELECT 
--     id, meeting_id, title, creator_id, creator_name, organizer,
--     organizer_name, format, scene, start_time, end_time,
--     venue, city_name, regions, cover_image, poster_url,
--     tags, target_audience, hot_score, current_participants, max_participants,
--     status, reg_end_time, require_checkin
-- FROM t_meeting 
-- WHERE id = 35;

-- 验证四级日程
-- SELECT * FROM t_meeting_agenda_item WHERE meeting_id = 35 ORDER BY level, sort_order;

-- 验证参与者
-- SELECT * FROM t_participant WHERE meeting_id = '35';

-- 验证报名记录
-- SELECT id, meeting_id, user_id, name, status, registered_at, checkin_time FROM t_registration WHERE meeting_id = 35;

-- 验证收藏
-- SELECT * FROM t_meeting_favorite WHERE meeting_id = 35;

-- 验证签到记录
-- SELECT * FROM t_checkin_record WHERE meeting_id = '35';

-- ============================================
-- 预期返回的完整JSON结构
-- ============================================

/*
GET /api/meetings/35 预期返回：

{
    "id": 35,
    "meetingId": "35",
    "title": "开源技术分享会",
    "description": "开源技术分享会，欢迎所有开发者参与，名额不限。本次会议将邀请业界知名开源贡献者分享技术心得...",
    "creatorId": "10001",
    "creatorName": "CSDN官方账号",
    "startTime": "2026-04-15T09:00:00",
    "endTime": "2026-04-15T12:00:00",
    "status": "PUBLISHED",
    "maxParticipants": 0,
    "participants": [
        {"id": 1, "userId": 35001, "userName": "参会者001", "role": "ATTENDEE", "status": "JOINED"},
        {"id": 2, "userId": 35002, "userName": "参会者002", "role": "ATTENDEE", "status": "JOINED"},
        {"id": 3, "userId": 35003, "userName": "参会者003", "role": "ATTENDEE", "status": "INVITED"},
        {"id": 4, "userId": 35004, "userName": "参会者004", "role": "CO_HOST", "status": "JOINED"},
        {"id": 5, "userId": 35005, "userName": "参会者005", "role": "HOST", "status": "JOINED"}
    ],
    "organizer": "开源中国社区",
    "format": "ONLINE",
    "scene": "UNIVERSITY",
    "venue": "线上直播",
    "regions": [{"code": "110000", "name": "北京市"}, {"code": "310000", "name": "上海市"}, {"code": "440100", "name": "广州市"}],
    "coverImage": "https://img.csdn.com/meeting/cover/opensource.jpg",
    "posterUrl": "https://img.csdn.com/meeting/cover/opensource.jpg",
    "tags": [{"id": 19, "name": "开源"}, {"id": 15, "name": "前端开发"}, {"id": 11, "name": "Python"}],
    "targetAudience": [{"type": "level", "value": "初级工程师"}, {"type": "level", "value": "中级工程师"}, {"type": "direction", "value": "全栈开发"}],
    "isPremium": false,
    "takedownReason": null,
    "rejectReason": null,
    "scheduleDays": [
        {
            "id": 1,
            "title": "Day 1",
            "level": 1,
            "extra": {"schedule_date": "2026-04-15", "day_label": "Day1"},
            "children": [
                {
                    "id": 2,
                    "title": "上午场",
                    "level": 2,
                    "extra": {"start_time": "09:00", "end_time": "12:00"},
                    "children": [
                        {
                            "id": 3,
                            "title": "主会场",
                            "level": 3,
                            "children": [
                                {
                                    "id": 4,
                                    "title": "开源项目的商业化路径",
                                    "level": 4,
                                    "extra": {"guests": [...], "topic_intro": "..."}
                                },
                                {
                                    "id": 5,
                                    "title": "前端开源组件库实践",
                                    "level": 4,
                                    "extra": {"guests": [...], "topic_intro": "..."}
                                }
                            ]
                        }
                    ]
                },
                {
                    "id": 6,
                    "title": "下午场",
                    "level": 2,
                    "extra": {"start_time": "14:00", "end_time": "17:00"},
                    "children": [
                        {
                            "id": 7,
                            "title": "分会场A",
                            "level": 3,
                            "children": [
                                {
                                    "id": 8,
                                    "title": "Python开源生态发展",
                                    "level": 4,
                                    "extra": {"guests": [...], "topic_intro": "..."}
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ]
}

GET /api/meetings/35/detail-page?userId=36001 预期返回：
- 包含上述会议基础信息
- myRegistration.status = "PENDING"（待审核）
- isFavorite = true/false（根据用户是否收藏）
- buttonState.type = "ALREADY_REGISTERED" 或 "REGISTER"

GET /api/meetings/35/detail-page?userId=36002 预期返回：
- myRegistration.status = "APPROVED"（已通过）
- buttonState.text = "已报名"

GET /api/meetings/35/detail-page?userId=36003 预期返回：
- myRegistration.status = "CHECKED_IN"（已签到）
- buttonState.text = "已签到"

GET /api/meetings/35/registration-status 预期返回：
- registrationOpen = true
- remainingSpots = -1（不限名额，max_participants=0）
- requireCheckin = true
*/

-- ============================================
