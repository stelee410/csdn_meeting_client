-- ============================================
-- queryMeetingList 测试数据（仅开发/测试环境执行）
-- 用途：使 POST /api/meetings/list 在各筛选、关键词、时间范围下均能查出数据
-- 说明：publish_time 故意设置不同时间（倒序），用于测试发布时间排序功能
-- 清理：可按 meeting_id 前缀 'MT0000000%' 删除后重新执行
-- 说明：若 t_meeting.tags 为 JSON 类型且插入报错，可改为 VARCHAR 或使用 JSON_QUOTE('1') 等形式
-- ============================================

-- ---------- 可选：清理旧测试数据（重复执行时先删再插） ----------
-- DELETE FROM t_meeting_tag WHERE meeting_id LIKE 'MT0000000%';
-- DELETE FROM t_meeting WHERE meeting_id LIKE 'MT0000000%';
-- 如需清理标签（会影响到 tags 关联）：先确认无其他会议引用后再删
-- DELETE FROM t_tag WHERE id IN (1, 2, 3) AND tag_name IN ('Java', '人工智能', '鸿蒙');

-- ---------- 1. 插入 t_tag（显式 id，便于 t_meeting.tags 引用） ----------
-- 若表已有相同 id 或 uk_tag_name 冲突，可改用 INSERT IGNORE 或调整 id
INSERT INTO t_tag (id, tag_name, tag_category, is_system, sort_order, is_deleted)
VALUES
    (1, 'Java', 'TECH', FALSE, 0, 0),
    (2, '人工智能', 'TECH', FALSE, 0, 0),
    (3, '鸿蒙', 'TECH', FALSE, 0, 0)
ON DUPLICATE KEY UPDATE tag_name = VALUES(tag_name), is_deleted = 0;

-- ---------- 2. 插入 t_meeting ----------
-- status: 2=已发布 3=进行中 4=已结束；format: 1=ONLINE 2=OFFLINE 3=HYBRID
-- meeting_type: 1=SUMMIT 2=SALON 3=WORKSHOP；scene: 1~5=DEVELOPER~UNIVERSITY
-- creator_id 占位 '10001'，请按实际环境替换

-- 2.1 无筛选 + 默认一条（本月、已发布）- 发布时间最早（14天前）
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT0000000001', '默认技术大会', '用于无筛选时列表展示', NULL, '10001',
    CONCAT(DATE_FORMAT(NOW(), '%Y-%m-01'), ' 09:00:00'),
    CONCAT(DATE_FORMAT(NOW(), '%Y-%m-01'), ' 18:00:00'),
    2, '1', '1', '1',
    '110100', '北京', '国际会议中心', 100, 50, 200,
    10001, 'CSDN', NULL, NULL, NULL,
    0, NOW(), NOW(), NOW() - INTERVAL 14 DAY
);

-- 2.2 会议形式：ONLINE(1) / OFFLINE(2) / HYBRID(3) - 发布时间 13天前/12天前/11天前
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES
('MT0000000002', '线上技术峰会', '纯线上会议', NULL, '10001',
    NOW() + INTERVAL 5 DAY, NOW() + INTERVAL 5 DAY + INTERVAL 7 HOUR,
    2, '1', '1', '1',
    NULL, NULL, '线上', 200, 80, 500,
    10001, 'CSDN', NULL, NULL, NULL,
    0, NOW(), NOW(), NOW() - INTERVAL 13 DAY),
('MT0000000003', '线下技术沙龙', '线下沙龙', NULL, '10001',
    NOW() + INTERVAL 7 DAY, NOW() + INTERVAL 7 DAY + INTERVAL 4 HOUR,
    2, '2', '2', '2',
    '310000', '上海', '浦东创新中心', 150, 30, 100,
    10001, '技术社区', NULL, NULL, NULL,
    0, NOW(), NOW(), NOW() - INTERVAL 12 DAY),
('MT0000000004', '混合模式研讨会', '线上+线下', NULL, '10001',
    NOW() + INTERVAL 10 DAY, NOW() + INTERVAL 10 DAY + INTERVAL 8 HOUR,
    2, '3', '3', '3',
    '440100', '广州', '国际会展中心', 80, 20, 150,
    10001, '开发者联盟', NULL, NULL, NULL,
    0, NOW(), NOW(), NOW() - INTERVAL 11 DAY);

-- 2.3 会议类型：SUMMIT(1) / SALON(2) / WORKSHOP(3) 各一条（上面已覆盖，补一条 SUMMIT 线下）- 发布时间 10天前
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT0000000005', '全球技术峰会', '技术峰会', NULL, '10001',
    NOW() + INTERVAL 14 DAY, NOW() + INTERVAL 15 DAY,
    2, '2', '1', '1',
    '110100', '北京', '国家会议中心', 500, 200, 1000,
    10001, 'CSDN', NULL, NULL, NULL,
    0, NOW(), NOW(), NOW() - INTERVAL 10 DAY
);

-- 2.4 会议场景：REGIONAL(4) / UNIVERSITY(5)（1,2,3 已在上面）- 发布时间 9天前/8天前
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES
('MT0000000006', '区域技术大会', '区域营销会议', NULL, '10001',
    NOW() + INTERVAL 20 DAY, NOW() + INTERVAL 20 DAY + INTERVAL 6 HOUR,
    2, '2', '1', '4',
    '330100', '杭州', '西湖国际', 60, 15, 80,
    10001, '区域运营', NULL, NULL, NULL,
    0, NOW(), NOW(), NOW() - INTERVAL 9 DAY),
('MT0000000007', '高校技术论坛', '高校会议', NULL, '10001',
    NOW() + INTERVAL 25 DAY, NOW() + INTERVAL 25 DAY + INTERVAL 8 HOUR,
    2, '1', '2', '5',
    '320100', '南京', '南京大学', 40, 10, 200,
    10001, '高校合作', NULL, NULL, NULL,
    0, NOW(), NOW(), NOW() - INTERVAL 8 DAY);

-- 2.5 状态：进行中(3)、已结束(4) - 发布时间 7天前/6天前（已结束状态会沉底）
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES
('MT0000000008', '进行中会议', '状态为进行中', NULL, '10001',
    NOW() - INTERVAL 1 HOUR, NOW() + INTERVAL 3 HOUR,
    3, '1', '1', '1',
    NULL, NULL, '线上', 300, 120, 500,
    10001, 'CSDN', NULL, NULL, NULL,
    0, NOW(), NOW(), NOW() - INTERVAL 7 DAY),
('MT0000000009', '已结束会议', '状态为已结束', NULL, '10001',
    NOW() - INTERVAL 7 DAY, NOW() - INTERVAL 6 DAY,
    4, '2', '2', '2',
    '440100', '广州', '已结束场馆', 50, 30, 100,
    10001, '技术社区', NULL, NULL, NULL,
    0, NOW(), NOW(), NOW() - INTERVAL 6 DAY);

-- 2.6 关键词：标题含「Java」+ tags 关联标签 id=1（Java）- 发布时间 5天前
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT0000000010', 'Java 技术大会', 'Java 生态与最佳实践', NULL, '10001',
    NOW() + INTERVAL 30 DAY, NOW() + INTERVAL 30 DAY + INTERVAL 9 HOUR,
    2, '1', '1', '1',
    '110100', '北京', '线上+线下', 180, 90, 300,
    10001, 'Java 社区', NULL, NULL, '1',
    0, NOW(), NOW(), NOW() - INTERVAL 5 DAY
);

-- 2.7 关键词：描述含「人工智能」、主办方含「AI 实验室」+ tags 关联标签 id=2,3 - 发布时间 4天前
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT0000000011', 'AI 前沿论坛', '会议介绍中包含人工智能相关内容', NULL, '10001',
    NOW() + INTERVAL 35 DAY, NOW() + INTERVAL 35 DAY + INTERVAL 7 HOUR,
    2, '1', '2', '3',
    NULL, NULL, '线上', 220, 110, 400,
    10001, 'AI 实验室', NULL, NULL, '2,3',
    0, NOW(), NOW(), NOW() - INTERVAL 4 DAY
);

-- 2.8 时间范围：THIS_WEEK（本周一 10:00）- 发布时间 3天前
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT0000000012', '本周会议', '用于 timeRange=THIS_WEEK', NULL, '10001',
    CONCAT(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), ' 10:00:00'),
    CONCAT(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), ' 18:00:00'),
    2, '1', '1', '1',
    NULL, NULL, '线上', 10, 5, 50,
    10001, '测试主办方', NULL, NULL, NULL,
    0, NOW(), NOW(), NOW() - INTERVAL 3 DAY
);

-- 2.9 时间范围：THIS_MONTH（本月 1 号 10:00）- 发布时间 2天前
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT0000000013', '本月会议', '用于 timeRange=THIS_MONTH', NULL, '10001',
    CONCAT(DATE_FORMAT(NOW(), '%Y-%m-01'), ' 10:00:00'),
    CONCAT(DATE_FORMAT(NOW(), '%Y-%m-01'), ' 18:00:00'),
    2, '2', '2', '2',
    '110100', '北京', '本月场馆', 20, 10, 80,
    10001, '本月主办方', NULL, NULL, NULL,
    0, NOW(), NOW(), NOW() - INTERVAL 2 DAY
);

-- 2.10 时间范围：NEXT_3_MONTHS（30 天后）- 发布时间 1天前（最新）
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT0000000014', '未来三个月会议', '用于 timeRange=NEXT_3_MONTHS', NULL, '10001',
    NOW() + INTERVAL 30 DAY,
    NOW() + INTERVAL 30 DAY + INTERVAL 8 HOUR,
    2, '1', '3', '1',
    '310000', '上海', '未来场馆', 30, 15, 120,
    10001, '未来主办方', NULL, NULL, NULL,
    0, NOW(), NOW(), NOW() - INTERVAL 1 DAY
);

-- ---------- 3. 可选：会议-标签关联表 t_meeting_tag（列表查询不依赖，其他业务可需要） ----------
INSERT INTO t_meeting_tag (meeting_id, tag_id, is_deleted)
VALUES
    ('MT0000000010', 1, 0),
    ('MT0000000011', 2, 0),
    ('MT0000000011', 3, 0)
ON DUPLICATE KEY UPDATE is_deleted = 0;
