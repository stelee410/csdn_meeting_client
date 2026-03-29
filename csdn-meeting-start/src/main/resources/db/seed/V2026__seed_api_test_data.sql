-- ============================================
-- API接口完整测试数据脚本
-- 版本: V2026
-- 说明：覆盖以下接口的所有入参组合和返回字段
--   1. GET /api/meetings/filter-options
--   2. POST /api/meetings/list
--   3. POST /api/subscriptions
--   4. DELETE /api/subscriptions
--   5. GET /api/subscriptions/tag-ids
-- ============================================

-- ---------- 可选：清理旧测试数据（重复执行时先删再插） ----------
-- DELETE FROM t_user_tag_subscribe WHERE user_id IN ('10001', '10002');
-- DELETE FROM t_meeting_tag WHERE meeting_id LIKE 'MT2026%';
-- DELETE FROM t_meeting WHERE meeting_id LIKE 'MT2026%';
-- DELETE FROM t_tag WHERE id BETWEEN 10 AND 25;

-- ============================================
-- 第一部分：扩展标签数据（15个标签，覆盖所有分类）
-- 用于测试：订阅/取消订阅/获取订阅列表接口
-- ============================================
INSERT INTO t_tag (id, tag_name, tag_category, is_system, sort_order, is_deleted, created_at, updated_at)
VALUES
    -- 技术类标签 (TECH)
    (10, 'Java', 'TECH', TRUE, 1, 0, NOW(), NOW()),
    (11, 'Python', 'TECH', TRUE, 2, 0, NOW(), NOW()),
    (12, '人工智能', 'TECH', TRUE, 3, 0, NOW(), NOW()),
    (13, '鸿蒙', 'TECH', TRUE, 4, 0, NOW(), NOW()),
    (14, '云原生', 'TECH', TRUE, 5, 0, NOW(), NOW()),
    (15, '前端开发', 'TECH', TRUE, 6, 0, NOW(), NOW()),
    
    -- 场景类标签 (SCENE)
    (16, '开发者大会', 'SCENE', TRUE, 1, 0, NOW(), NOW()),
    (17, '产业峰会', 'SCENE', TRUE, 2, 0, NOW(), NOW()),
    
    -- 主题类标签 (TOPIC)
    (18, '大模型', 'TOPIC', TRUE, 1, 0, NOW(), NOW()),
    (19, '开源', 'TOPIC', TRUE, 2, 0, NOW(), NOW()),
    
    -- 品牌类标签 (BRAND)
    (20, '华为', 'BRAND', TRUE, 1, 0, NOW(), NOW()),
    (21, '阿里云', 'BRAND', TRUE, 2, 0, NOW(), NOW()),
    
    -- 类型类标签 (TYPE)
    (22, '技术峰会', 'TYPE', TRUE, 1, 0, NOW(), NOW()),
    (23, '技术沙龙', 'TYPE', TRUE, 2, 0, NOW(), NOW()),
    (24, '技术研讨会', 'TYPE', TRUE, 3, 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    tag_name = VALUES(tag_name), 
    tag_category = VALUES(tag_category),
    is_system = VALUES(is_system),
    is_deleted = 0;

-- ============================================
-- 第二部分：完整会议数据（18条，覆盖所有筛选维度组合）
-- 用于测试：会议列表查询接口的各种入参组合
-- ============================================

-- 说明：
-- format: 1=ONLINE(线上), 2=OFFLINE(线下), 3=HYBRID(线上+线下)
-- meeting_type: 1=SUMMIT(技术峰会), 2=SALON(技术沙龙), 3=WORKSHOP(技术研讨会)
-- scene: 1=DEVELOPER(开发者会议), 2=INDUSTRY(产业会议), 3=PRODUCT(产品发布会议), 4=REGIONAL(区域营销会议), 5=UNIVERSITY(高校会议)
-- status: 2=PUBLISHED(已发布), 3=IN_PROGRESS(进行中), 4=ENDED(已结束)

-- 2.1 本周会议 - ONLINE + SUMMIT + DEVELOPER + PUBLISHED + 标题含Java
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026000001', 'Java开发者技术峰会', '专注于Java生态与最佳实践的开发者大会，涵盖Spring Boot、微服务架构等热门话题', 
    'https://img.csdn.com/meeting/cover/java-summit.jpg', '10001',
    CONCAT(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), ' 09:00:00'),
    CONCAT(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), ' 18:00:00'),
    2, 1, 1, 1,
    NULL, NULL, '线上直播',
    2500, 1200, 5000,
    10001, 'Java社区联盟', 'https://img.csdn.com/avatar/java-community.png', 
    'https://img.csdn.com/meeting/cover/java-summit.jpg', '10,16,22',
    0, NOW(), NOW(), NOW() - INTERVAL 7 DAY
);

-- 2.2 本周会议 - ONLINE + SUMMIT + REGIONAL + IN_PROGRESS + 标题含云原生
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026000002', '云原生技术峰会-华东站', '聚焦云原生技术栈，Kubernetes、Docker、DevOps最佳实践', 
    'https://img.csdn.com/meeting/cover/cloudnative.jpg', '10001',
    CONCAT(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), ' 10:00:00'),
    CONCAT(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), ' 17:00:00'),
    3, 1, 1, 4,
    NULL, NULL, '线上直播',
    1800, 900, 3000,
    10001, '云原生社区', 'https://img.csdn.com/avatar/cloudnative.png', 
    'https://img.csdn.com/meeting/cover/cloudnative.jpg', '14,16,20',
    0, NOW(), NOW(), NOW() - INTERVAL 6 DAY
);

-- 2.3 本月会议 - OFFLINE + SALON + INDUSTRY + PUBLISHED
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026000003', '人工智能产业沙龙', 'AI技术在各行业的应用实践沙龙，探讨人工智能赋能传统产业的路径', 
    'https://img.csdn.com/meeting/cover/ai-salon.jpg', '10001',
    CONCAT(DATE_FORMAT(NOW(), '%Y-%m-15'), ' 14:00:00'),
    CONCAT(DATE_FORMAT(NOW(), '%Y-%m-15'), ' 17:00:00'),
    2, 2, 2, 2,
    '310000', '上海', '浦东新区张江AI创新中心',
    800, 45, 100,
    10002, 'AI产业联盟', 'https://img.csdn.com/avatar/ai-alliance.png', 
    'https://img.csdn.com/meeting/cover/ai-salon.jpg', '12,17,23',
    0, NOW(), NOW(), NOW() - INTERVAL 5 DAY
);

-- 2.4 本月会议 - OFFLINE + SALON + UNIVERSITY + ENDED + 主办方含高校
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026000004', '高校AI技术沙龙', '面向高校师生的人工智能技术分享沙龙', 
    'https://img.csdn.com/meeting/cover/university-ai.jpg', '10001',
    CONCAT(DATE_FORMAT(NOW(), '%Y-%m-05'), ' 14:00:00'),
    CONCAT(DATE_FORMAT(NOW(), '%Y-%m-05'), ' 16:00:00'),
    4, 2, 2, 5,
    '110000', '北京', '清华大学计算机系',
    300, 80, 150,
    10003, '清华大学AI实验室', 'https://img.csdn.com/avatar/tsinghua.png', 
    'https://img.csdn.com/meeting/cover/university-ai.jpg', '12,15',
    0, NOW(), NOW(), NOW() - INTERVAL 4 DAY
);

-- 2.5 未来三个月会议 - HYBRID + WORKSHOP + PRODUCT + PUBLISHED + 描述含鸿蒙
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026000005', '鸿蒙生态产品发布研讨会', '鸿蒙HarmonyOS生态产品发布与技术研讨，涵盖鸿蒙应用开发、分布式技术等', 
    'https://img.csdn.com/meeting/cover/harmonyos.jpg', '10001',
    NOW() + INTERVAL 45 DAY,
    NOW() + INTERVAL 45 DAY + INTERVAL 6 HOUR,
    2, 3, 3, 3,
    '440100', '广州', '琶洲国际会展中心',
    3500, 600, 2000,
    10004, '华为开发者联盟', 'https://img.csdn.com/avatar/huawei.png', 
    'https://img.csdn.com/meeting/cover/harmonyos.jpg', '13,20,24',
    0, NOW(), NOW(), NOW() - INTERVAL 3 DAY
);

-- 2.6 未来三个月会议 - HYBRID + WORKSHOP + DEVELOPER + PUBLISHED
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026000006', 'Python技术研讨会', 'Python全栈开发技术研讨，涵盖Web开发、数据分析、机器学习等领域', 
    'https://img.csdn.com/meeting/cover/python-workshop.jpg', '10001',
    NOW() + INTERVAL 60 DAY,
    NOW() + INTERVAL 60 DAY + INTERVAL 8 HOUR,
    2, 3, 3, 1,
    '320100', '南京', '南京软件谷',
    1200, 200, 500,
    10005, 'Python中国社区', 'https://img.csdn.com/avatar/python.png', 
    'https://img.csdn.com/meeting/cover/python-workshop.jpg', '11,16',
    0, NOW(), NOW(), NOW() - INTERVAL 2 DAY
);

-- 2.7 ONLINE + SALON + PRODUCT + PUBLISHED
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026000007', '大模型产品发布沙龙', '国产大模型产品发布会与技术交流沙龙', 
    'https://img.csdn.com/meeting/cover/llm-product.jpg', '10001',
    NOW() + INTERVAL 20 DAY,
    NOW() + INTERVAL 20 DAY + INTERVAL 3 HOUR,
    2, 1, 2, 3,
    NULL, NULL, '线上直播',
    5000, 1500, 10000,
    10006, '百度AI开放平台', 'https://img.csdn.com/avatar/baidu-ai.png', 
    'https://img.csdn.com/meeting/cover/llm-product.jpg', '12,18,21',
    0, NOW(), NOW(), NOW() - INTERVAL 1 DAY
);

-- 2.8 OFFLINE + SUMMIT + DEVELOPER + PUBLISHED
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026000008', '前端开发者技术峰会', '前端技术生态峰会，React、Vue、小程序等技术深度解析', 
    'https://img.csdn.com/meeting/cover/frontend-summit.jpg', '10001',
    NOW() + INTERVAL 25 DAY,
    NOW() + INTERVAL 25 DAY + INTERVAL 8 HOUR,
    2, 2, 1, 1,
    '500000', '重庆', '重庆国际博览中心',
    2000, 300, 800,
    10007, '前端技术社区', 'https://img.csdn.com/avatar/frontend.png', 
    'https://img.csdn.com/meeting/cover/frontend-summit.jpg', '15,16,22',
    0, NOW(), NOW(), NOW() - INTERVAL 1 DAY
);

-- 2.9 HYBRID + SUMMIT + INDUSTRY + PUBLISHED
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026000009', '产业数字化转型峰会', '产业数字化转型与智能化升级峰会', 
    'https://img.csdn.com/meeting/cover/industry-digital.jpg', '10001',
    NOW() + INTERVAL 35 DAY,
    NOW() + INTERVAL 35 DAY + INTERVAL 7 HOUR,
    2, 3, 1, 2,
    '330100', '杭州', '杭州国际会议中心',
    1500, 250, 600,
    10008, '阿里云智能', 'https://img.csdn.com/avatar/aliyun.png', 
    'https://img.csdn.com/meeting/cover/industry-digital.jpg', '14,17,21',
    0, NOW(), NOW(), NOW()
);

-- 2.10 ONLINE + WORKSHOP + UNIVERSITY + PUBLISHED
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026000010', '高校开源技术研讨会', '面向高校学生的开源技术推广与实践研讨会', 
    'https://img.csdn.com/meeting/cover/opensource-uni.jpg', '10001',
    NOW() + INTERVAL 28 DAY,
    NOW() + INTERVAL 28 DAY + INTERVAL 4 HOUR,
    2, 1, 3, 5,
    NULL, NULL, '线上直播',
    600, 120, 500,
    10009, '开源中国高校联盟', 'https://img.csdn.com/avatar/opensource.png', 
    'https://img.csdn.com/meeting/cover/opensource-uni.jpg', '19',
    0, NOW(), NOW(), NOW()
);

-- 2.11 OFFLINE + WORKSHOP + REGIONAL + PUBLISHED
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026000011', '区域开发者技术研讨会', '区域开发者技术能力建设工作坊', 
    'https://img.csdn.com/meeting/cover/regional-workshop.jpg', '10001',
    NOW() + INTERVAL 40 DAY,
    NOW() + INTERVAL 40 DAY + INTERVAL 5 HOUR,
    2, 2, 3, 4,
    '420100', '武汉', '武汉光谷软件园',
    400, 80, 200,
    10010, '武汉开发者社区', 'https://img.csdn.com/avatar/wuhan-dev.png', 
    'https://img.csdn.com/meeting/cover/regional-workshop.jpg', '16,24',
    0, NOW(), NOW(), NOW()
);

-- 2.12 HYBRID + SALON + PRODUCT + PUBLISHED
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026000012', '字节跳动产品技术沙龙', '字节跳动产品技术分享沙龙，了解大厂技术实践', 
    'https://img.csdn.com/meeting/cover/bytedance-salon.jpg', '10001',
    NOW() + INTERVAL 15 DAY,
    NOW() + INTERVAL 15 DAY + INTERVAL 3 HOUR,
    2, 3, 2, 3,
    '110000', '北京', '字节跳动大厦',
    3000, 500, 800,
    10011, '字节跳动技术团队', 'https://img.csdn.com/avatar/bytedance.png', 
    'https://img.csdn.com/meeting/cover/bytedance-salon.jpg', '23',
    0, NOW(), NOW(), NOW()
);

-- 2.13 本周 - HYBRID + SUMMIT + UNIVERSITY + IN_PROGRESS
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026000013', '高校AI创新峰会', '高校人工智能创新应用峰会正在进行中', 
    'https://img.csdn.com/meeting/cover/ai-uni-summit.jpg', '10001',
    CONCAT(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), ' 13:00:00'),
    CONCAT(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), ' 20:00:00'),
    3, 3, 1, 5,
    '510100', '成都', '四川大学望江校区',
    800, 350, 600,
    10012, '四川大学AI学院', 'https://img.csdn.com/avatar/scu.png', 
    'https://img.csdn.com/meeting/cover/ai-uni-summit.jpg', '12,16',
    0, NOW(), NOW(), NOW() - INTERVAL 2 DAY
);

-- 2.14 本月 - ONLINE + WORKSHOP + INDUSTRY + ENDED
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026000014', '产业大数据技术研讨会-回顾', '产业大数据应用研讨会已圆满结束', 
    'https://img.csdn.com/meeting/cover/bigdata-ended.jpg', '10001',
    CONCAT(DATE_FORMAT(NOW(), '%Y-%m-08'), ' 09:00:00'),
    CONCAT(DATE_FORMAT(NOW(), '%Y-%m-08'), ' 18:00:00'),
    4, 1, 3, 2,
    NULL, NULL, '线上直播',
    1200, 400, 1000,
    10013, '大数据产业联盟', 'https://img.csdn.com/avatar/bigdata.png', 
    'https://img.csdn.com/meeting/cover/bigdata-ended.jpg', '17',
    0, NOW(), NOW(), NOW() - INTERVAL 3 DAY
);

-- 2.15 未来三个月 - OFFLINE + SUMMIT + PRODUCT + PUBLISHED
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026000015', 'CSDN年度产品发布峰会', 'CSDN年度重磅产品发布与技术生态峰会', 
    'https://img.csdn.com/meeting/cover/csdn-summit.jpg', '10001',
    NOW() + INTERVAL 80 DAY,
    NOW() + INTERVAL 80 DAY + INTERVAL 8 HOUR,
    2, 2, 1, 3,
    '110000', '北京', '国家会议中心',
    5000, 1000, 3000,
    10001, 'CSDN官方', 'https://img.csdn.com/avatar/csdn.png', 
    'https://img.csdn.com/meeting/cover/csdn-summit.jpg', '10,16,22',
    0, NOW(), NOW(), NOW()
);

-- 2.16 本周 - OFFLINE + WORKSHOP + DEVELOPER + PUBLISHED（测试标签关键词搜索）
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026000016', '机器学习工作坊', '机器学习算法与实践工作坊', 
    'https://img.csdn.com/meeting/cover/ml-workshop.jpg', '10001',
    CONCAT(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), ' 14:00:00'),
    CONCAT(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), ' 18:00:00'),
    2, 2, 3, 1,
    '410100', '郑州', '郑州大学新校区',
    500, 45, 150,
    10014, '郑州AI实验室', 'https://img.csdn.com/avatar/zzu.png', 
    'https://img.csdn.com/meeting/cover/ml-workshop.jpg', '12',
    0, NOW(), NOW(), NOW() - INTERVAL 1 DAY
);

-- 2.17 本月 - HYBRID + SALON + REGIONAL + PUBLISHED（测试主办方关键词搜索）
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026000017', '腾讯云区域技术沙龙', '腾讯云技术沙龙-区域开发者交流活动', 
    'https://img.csdn.com/meeting/cover/tencent-salon.jpg', '10001',
    CONCAT(DATE_FORMAT(NOW(), '%Y-%m-20'), ' 14:00:00'),
    CONCAT(DATE_FORMAT(NOW(), '%Y-%m-20'), ' 17:00:00'),
    2, 3, 2, 4,
    '440300', '深圳', '腾讯大厦',
    2000, 200, 400,
    10015, '腾讯云开发者社区', 'https://img.csdn.com/avatar/tencent.png', 
    'https://img.csdn.com/meeting/cover/tencent-salon.jpg', '21',
    0, NOW(), NOW(), NOW() - INTERVAL 1 DAY
);

-- 2.18 未来三个月 - ONLINE + SUMMIT + DEVELOPER + PUBLISHED（完整字段填充）
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026000018', '全栈开发者技术峰会', '全栈技术生态峰会，前后端、DevOps、AI全栈技术', 
    'https://img.csdn.com/meeting/cover/fullstack-summit.jpg', '10001',
    NOW() + INTERVAL 55 DAY,
    NOW() + INTERVAL 55 DAY + INTERVAL 8 HOUR,
    2, 1, 1, 1,
    NULL, NULL, '线上直播',
    3500, 800, 2500,
    10016, '全栈技术社区', 'https://img.csdn.com/avatar/fullstack.png', 
    'https://img.csdn.com/meeting/cover/fullstack-summit.jpg', '10,11,15,16,22',
    0, NOW(), NOW(), NOW()
);

-- ============================================
-- 第三部分：用户标签订阅数据
-- 用于测试：订阅/取消订阅/获取订阅列表接口
-- ============================================

-- 3.1 用户10001的订阅数据（订阅6个标签，测试分页和列表查询）
INSERT INTO t_user_tag_subscribe (user_id, tag_id, is_active, notify_site, notify_push, is_deleted, created_at, updated_at)
VALUES
    ('10001', 10, TRUE, TRUE, TRUE, 0, NOW() - INTERVAL 30 DAY, NOW()),
    ('10001', 12, TRUE, TRUE, TRUE, 0, NOW() - INTERVAL 25 DAY, NOW()),
    ('10001', 13, TRUE, TRUE, TRUE, 0, NOW() - INTERVAL 20 DAY, NOW()),
    ('10001', 14, TRUE, TRUE, FALSE, 0, NOW() - INTERVAL 15 DAY, NOW()),
    ('10001', 15, TRUE, FALSE, TRUE, 0, NOW() - INTERVAL 10 DAY, NOW()),
    ('10001', 18, TRUE, TRUE, TRUE, 0, NOW() - INTERVAL 5 DAY, NOW())
ON DUPLICATE KEY UPDATE 
    is_active = VALUES(is_active),
    is_deleted = VALUES(is_deleted);

-- 3.2 用户10002的订阅数据（订阅4个标签，用于对比测试）
INSERT INTO t_user_tag_subscribe (user_id, tag_id, is_active, notify_site, notify_push, is_deleted, created_at, updated_at)
VALUES
    ('10002', 11, TRUE, TRUE, TRUE, 0, NOW() - INTERVAL 28 DAY, NOW()),
    ('10002', 16, TRUE, TRUE, TRUE, 0, NOW() - INTERVAL 22 DAY, NOW()),
    ('10002', 20, TRUE, TRUE, TRUE, 0, NOW() - INTERVAL 18 DAY, NOW()),
    ('10002', 22, TRUE, TRUE, TRUE, 0, NOW() - INTERVAL 12 DAY, NOW())
ON DUPLICATE KEY UPDATE 
    is_active = VALUES(is_active),
    is_deleted = VALUES(is_deleted);

-- 3.3 测试取消订阅场景（已软删除的订阅记录，用户10001曾订阅后又取消的标签19）
INSERT INTO t_user_tag_subscribe (user_id, tag_id, is_active, notify_site, notify_push, is_deleted, created_at, updated_at)
VALUES
    ('10001', 19, FALSE, TRUE, TRUE, 1, NOW() - INTERVAL 40 DAY, NOW() - INTERVAL 10 DAY)
ON DUPLICATE KEY UPDATE 
    is_active = VALUES(is_active),
    is_deleted = VALUES(is_deleted);

-- ============================================
-- 第四部分：会议标签关联数据
-- 用于测试：标签关联查询（如需使用t_meeting_tag表）
-- ============================================
INSERT INTO t_meeting_tag (meeting_id, tag_id, is_deleted, created_at)
VALUES
    ('MT2026000001', 10, 0, NOW()),
    ('MT2026000001', 16, 0, NOW()),
    ('MT2026000001', 22, 0, NOW()),
    ('MT2026000002', 14, 0, NOW()),
    ('MT2026000002', 16, 0, NOW()),
    ('MT2026000002', 20, 0, NOW()),
    ('MT2026000003', 12, 0, NOW()),
    ('MT2026000003', 17, 0, NOW()),
    ('MT2026000003', 23, 0, NOW()),
    ('MT2026000004', 12, 0, NOW()),
    ('MT2026000004', 15, 0, NOW()),
    ('MT2026000005', 13, 0, NOW()),
    ('MT2026000005', 20, 0, NOW()),
    ('MT2026000005', 24, 0, NOW()),
    ('MT2026000006', 11, 0, NOW()),
    ('MT2026000006', 16, 0, NOW()),
    ('MT2026000007', 12, 0, NOW()),
    ('MT2026000007', 18, 0, NOW()),
    ('MT2026000007', 21, 0, NOW()),
    ('MT2026000008', 15, 0, NOW()),
    ('MT2026000008', 16, 0, NOW()),
    ('MT2026000008', 22, 0, NOW()),
    ('MT2026000009', 14, 0, NOW()),
    ('MT2026000009', 17, 0, NOW()),
    ('MT2026000009', 21, 0, NOW()),
    ('MT2026000010', 19, 0, NOW()),
    ('MT2026000011', 16, 0, NOW()),
    ('MT2026000011', 24, 0, NOW()),
    ('MT2026000012', 23, 0, NOW()),
    ('MT2026000013', 12, 0, NOW()),
    ('MT2026000013', 16, 0, NOW()),
    ('MT2026000014', 17, 0, NOW()),
    ('MT2026000015', 10, 0, NOW()),
    ('MT2026000015', 16, 0, NOW()),
    ('MT2026000015', 22, 0, NOW()),
    ('MT2026000016', 12, 0, NOW()),
    ('MT2026000017', 21, 0, NOW()),
    ('MT2026000018', 10, 0, NOW()),
    ('MT2026000018', 11, 0, NOW()),
    ('MT2026000018', 15, 0, NOW()),
    ('MT2026000018', 16, 0, NOW()),
    ('MT2026000018', 22, 0, NOW())
ON DUPLICATE KEY UPDATE is_deleted = 0;

-- ============================================
-- 数据验证注释
-- ============================================
-- 【会议列表接口测试覆盖矩阵】
-- 
-- 会议形式(format)测试数据:
--   format=1(ONLINE): MT2026000001, MT2026000002, MT2026000007, MT2026000010, MT2026000014, MT2026000018
--   format=2(OFFLINE): MT2026000003, MT2026000004, MT2026000008, MT2026000011, MT2026000015, MT2026000016
--   format=3(HYBRID): MT2026000005, MT2026000006, MT2026000009, MT2026000012, MT2026000013, MT2026000017
-- 
-- 会议类型(meeting_type)测试数据:
--   type=1(SUMMIT): MT2026000001, MT2026000002, MT2026000008, MT2026000009, MT2026000013, MT2026000015, MT2026000018
--   type=2(SALON): MT2026000003, MT2026000004, MT2026000007, MT2026000012, MT2026000017
--   type=3(WORKSHOP): MT2026000005, MT2026000006, MT2026000010, MT2026000011, MT2026000014, MT2026000016
-- 
-- 会议场景(scene)测试数据:
--   scene=1(DEVELOPER): MT2026000001, MT2026000002, MT2026000006, MT2026000008, MT2026000016, MT2026000018
--   scene=2(INDUSTRY): MT2026000003, MT2026000009, MT2026000014
--   scene=3(PRODUCT): MT2026000005, MT2026000007, MT2026000012, MT2026000015
--   scene=4(REGIONAL): MT2026000011, MT2026000017
--   scene=5(UNIVERSITY): MT2026000004, MT2026000010, MT2026000013
-- 
-- 时间范围(timeRange)测试数据:
--   timeRange=1(本周): MT2026000001, MT2026000002, MT2026000013, MT2026000016
--   timeRange=2(本月): MT2026000003, MT2026000004, MT2026000007, MT2026000008, MT2026000012, MT2026000014, MT2026000016, MT2026000017
--   timeRange=3(未来三个月): MT2026000005, MT2026000006, MT2026000009, MT2026000010, MT2026000011, MT2026000015, MT2026000018
-- 
-- 状态(status)测试数据:
--   status=2(已发布): 大部分会议
--   status=3(进行中): MT2026000002, MT2026000013
--   status=4(已结束): MT2026000004, MT2026000014
-- 
-- 关键词(keyword)测试数据:
--   标题含"Java": MT2026000001
--   标题含"云原生": MT2026000002
--   描述含"人工智能": MT2026000003
--   描述含"鸿蒙": MT2026000005
--   主办方含"腾讯云": MT2026000017
--   标签含"Java": MT2026000001, MT2026000015, MT2026000018
--   标签含"人工智能": MT2026000003, MT2026000004, MT2026000007, MT2026000013, MT2026000016
-- 
-- 【订阅接口测试场景】
-- 
-- 用户10001的订阅:
--   已订阅标签: 10(Java), 12(人工智能), 13(鸿蒙), 14(云原生), 15(前端开发), 18(大模型)
--   可测试重复订阅: 任意以上标签ID
--   可测试取消订阅: 任意以上标签ID
--   已取消订阅(软删除): 19(开源)
-- 
-- 用户10002的订阅:
--   已订阅标签: 11(Python), 16(开发者大会), 20(华为), 22(技术峰会)
--   可测试跨用户订阅
-- 
-- 【获取订阅列表接口测试】
-- 
-- GET /api/subscriptions/tag-ids:
--   用户10001返回: [10, 12, 13, 14, 15, 18]
--   用户10002返回: [11, 16, 20, 22]
-- 
-- GET /api/subscriptions (分页):
--   用户10001: 6条记录，可测试page=1,size=2等分页参数
--   用户10002: 4条记录
-- 
-- ============================================
