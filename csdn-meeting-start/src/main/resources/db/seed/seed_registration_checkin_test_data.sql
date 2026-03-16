-- ============================================
-- 会议报名、签到、收藏功能测试数据
-- 版本: V2026
-- 用途：覆盖以下接口的所有查询条件
--   1. GET /api/meetings/{meetingId}/detail-page - 会议详情页
--   2. GET /api/meetings/{meetingId}/registration-status - 报名状态
--   3. GET /api/registrations/config/{meetingId} - 表单配置
--   4. GET /api/registrations/pre-fill - 预填数据
--   5. POST /api/registrations - 提交报名
--   6. GET /api/registrations/my/{meetingId} - 我的报名
--   7. POST /api/registrations/{regId}/cancel - 取消报名
--   8. POST /api/meetings/{meetingId}/checkin-code - 生成签到码
--   9. POST /api/checkin - 扫码签到
--  10. GET /api/checkin/status - 签到状态
--  11. POST /api/meetings/{meetingId}/favorite - 切换收藏
--  12. GET /api/meetings/{meetingId}/favorite - 收藏状态
-- ============================================

-- 可选：清理旧测试数据（重复执行时先删再插）
-- DELETE FROM t_checkin_record WHERE meeting_id LIKE 'MT2026REG%';
-- DELETE FROM t_registration WHERE meeting_id IN (SELECT id FROM t_meeting WHERE meeting_id LIKE 'MT2026REG%');
-- DELETE FROM t_meeting_favorite WHERE meeting_id IN (SELECT id FROM t_meeting WHERE meeting_id LIKE 'MT2026REG%');
-- DELETE FROM t_meeting WHERE meeting_id LIKE 'MT2026REG%';

-- ============================================
-- 第一部分：会议测试数据（覆盖所有业务场景）
-- ============================================

-- 说明：
-- status: 0=DRAFT草稿, 1=PENDING_REVIEW待审核, 2=PUBLISHED已发布, 3=IN_PROGRESS进行中, 4=ENDED已结束
-- format: 1=ONLINE线上, 2=OFFLINE线下, 3=HYBRID混合
-- meeting_type: 1=SUMMIT峰会, 2=SALON沙龙, 3=WORKSHOP研讨会
-- scene: 1=DEVELOPER开发者, 2=INDUSTRY产业, 3=PRODUCT产品发布, 4=REGIONAL区域营销, 5=UNIVERSITY高校

-- 1.1 可报名会议（已发布 + 有名额 + 报名未截止）- 基础测试用
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    reg_end_time, checkin_code, require_checkin,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026REG0001', '2024 AI技术创新峰会', '聚焦人工智能前沿技术，探讨大模型、机器学习、深度学习等领域的创新应用与实践案例。',
    'https://img.csdn.com/meeting/cover/ai-summit-2024.jpg', '10001',
    NOW() + INTERVAL 30 DAY, NOW() + INTERVAL 30 DAY + INTERVAL 2 DAY,
    2, 3, 1, 1,
    '110000', '北京', '北京国际会议中心',
    1200, 856, 1000,
    10001, '阿里云', 'https://img.csdn.com/avatar/aliyun.png',
    'https://img.csdn.com/meeting/cover/ai-summit-2024.jpg', '12,18,22',
    NOW() + INTERVAL 25 DAY, NULL, TRUE,
    0, NOW(), NOW(), NOW() - INTERVAL 7 DAY
);

-- 1.2 名额已满会议（已发布 + 无名额）
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    reg_end_time, checkin_code, require_checkin,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026REG0002', 'Java开发者技术沙龙', 'Java生态与最佳实践分享，涵盖Spring Boot、微服务架构等热门话题。',
    'https://img.csdn.com/meeting/cover/java-salon.jpg', '10001',
    NOW() + INTERVAL 15 DAY, NOW() + INTERVAL 15 DAY + INTERVAL 4 HOUR,
    2, 1, 2, 1,
    '310000', '上海', '浦东新区软件园',
    500, 100, 100,
    10002, 'Java社区联盟', 'https://img.csdn.com/avatar/java.png',
    'https://img.csdn.com/meeting/cover/java-salon.jpg', '10,23',
    NOW() + INTERVAL 10 DAY, NULL, FALSE,
    0, NOW(), NOW(), NOW() - INTERVAL 6 DAY
);

-- 1.3 报名已截止会议（已发布 + 报名截止）
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    reg_end_time, checkin_code, require_checkin,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026REG0003', '云原生技术研讨会', '云原生技术栈深度研讨，Kubernetes、Docker、DevOps最佳实践。',
    'https://img.csdn.com/meeting/cover/cloudnative.jpg', '10001',
    NOW() + INTERVAL 5 DAY, NOW() + INTERVAL 5 DAY + INTERVAL 6 HOUR,
    2, 3, 3, 2,
    '440100', '广州', '琶洲国际会展中心',
    300, 45, 150,
    10003, '云原生社区', 'https://img.csdn.com/avatar/cloudnative.png',
    'https://img.csdn.com/meeting/cover/cloudnative.jpg', '14,17,24',
    NOW() - INTERVAL 1 DAY, 'a1b2c3d4e5f6', TRUE,
    0, NOW(), NOW(), NOW() - INTERVAL 5 DAY
);

-- 1.4 进行中会议（已发布 + 会议已开始）
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    reg_end_time, checkin_code, require_checkin,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026REG0004', '鸿蒙开发者大会', '鸿蒙HarmonyOS生态开发者大会，涵盖应用开发、分布式技术、设备互联等。',
    'https://img.csdn.com/meeting/cover/harmonyos.jpg', '10001',
    NOW() - INTERVAL 2 HOUR, NOW() + INTERVAL 6 HOUR,
    3, 2, 1, 3,
    '440300', '深圳', '深圳会展中心',
    2000, 800, 1500,
    10004, '华为开发者联盟', 'https://img.csdn.com/avatar/huawei.png',
    'https://img.csdn.com/meeting/cover/harmonyos.jpg', '13,20,22',
    NOW() - INTERVAL 3 DAY, 'b2c3d4e5f6g7', TRUE,
    0, NOW(), NOW(), NOW() - INTERVAL 10 DAY
);

-- 1.5 已结束会议
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    reg_end_time, checkin_code, require_checkin,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026REG0005', 'Python技术峰会-回顾', 'Python全栈开发技术峰会回顾，Web开发、数据分析、机器学习等领域分享。',
    'https://img.csdn.com/meeting/cover/python-summit.jpg', '10001',
    NOW() - INTERVAL 7 DAY, NOW() - INTERVAL 6 DAY,
    4, 1, 1, 1,
    NULL, NULL, '线上直播',
    800, 300, 500,
    10005, 'Python中国社区', 'https://img.csdn.com/avatar/python.png',
    'https://img.csdn.com/meeting/cover/python-summit.jpg', '11,16',
    NOW() - INTERVAL 10 DAY, 'c3d4e5f6g7h8', TRUE,
    0, NOW(), NOW(), NOW() - INTERVAL 14 DAY
);

-- 1.6 草稿状态会议（不可报名）
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    reg_end_time, checkin_code, require_checkin,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026REG0006', '前端技术大会-筹备中', '前端技术生态大会筹备中，React、Vue、小程序等技术深度解析。',
    'https://img.csdn.com/meeting/cover/frontend.jpg', '10001',
    NOW() + INTERVAL 45 DAY, NOW() + INTERVAL 45 DAY + INTERVAL 8 HOUR,
    0, 3, 1, 1,
    '500000', '重庆', '重庆国际博览中心',
    0, 0, 1000,
    10006, '前端技术社区', 'https://img.csdn.com/avatar/frontend.png',
    'https://img.csdn.com/meeting/cover/frontend.jpg', '15',
    NULL, NULL, FALSE,
    0, NOW(), NOW(), NOW()
);

-- 1.7 不限名额会议（max_participants = 0）
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    reg_end_time, checkin_code, require_checkin,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026REG0007', '开源技术分享会', '开源技术分享会，欢迎所有开发者参与，名额不限。',
    'https://img.csdn.com/meeting/cover/opensource.jpg', '10001',
    NOW() + INTERVAL 20 DAY, NOW() + INTERVAL 20 DAY + INTERVAL 3 HOUR,
    2, 1, 2, 5,
    NULL, NULL, '线上直播',
    100, 50, 0,
    10007, '开源中国', 'https://img.csdn.com/avatar/osc.png',
    'https://img.csdn.com/meeting/cover/opensource.jpg', '19',
    NOW() + INTERVAL 15 DAY, NULL, FALSE,
    0, NOW(), NOW(), NOW() - INTERVAL 2 DAY
);

-- 1.8 已下架会议
INSERT INTO t_meeting (
    meeting_id, title, description, poster_url, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, organizer_avatar, cover_image, tags,
    reg_end_time, checkin_code, require_checkin,
    is_deleted, created_at, updated_at, publish_time
) VALUES (
    'MT2026REG0008', '已下架会议示例', '该会议已被主办方下架。',
    'https://img.csdn.com/meeting/cover/offline.jpg', '10001',
    NOW() + INTERVAL 10 DAY, NOW() + INTERVAL 10 DAY + INTERVAL 4 HOUR,
    6, 2, 2, 2,
    '330100', '杭州', '杭州国际会议中心',
    0, 0, 200,
    10008, '测试主办方', NULL,
    'https://img.csdn.com/meeting/cover/offline.jpg', NULL,
    NULL, NULL, FALSE,
    0, NOW(), NOW(), NOW() - INTERVAL 3 DAY
);

-- ============================================
-- 第二部分：报名记录测试数据（覆盖所有状态）
-- ============================================

-- 获取会议ID
SET @meeting1_id = (SELECT id FROM t_meeting WHERE meeting_id = 'MT2026REG0001' LIMIT 1);
SET @meeting4_id = (SELECT id FROM t_meeting WHERE meeting_id = 'MT2026REG0004' LIMIT 1);
SET @meeting5_id = (SELECT id FROM t_meeting WHERE meeting_id = 'MT2026REG0005' LIMIT 1);

-- 2.1 待审核报名（用户20001报名MT2026REG0001）
INSERT INTO t_registration (
    meeting_id, user_id, name, phone, email, company, position,
    status, registered_at, audited_at, audit_remark, checkin_time, cancel_time, is_deleted
) VALUES (
    @meeting1_id, 20001, '张三', '138****1234', 'zhangsan@csdn.net', '阿里巴巴', 'Java开发工程师',
    'PENDING', NOW() - INTERVAL 2 DAY, NULL, NULL, NULL, NULL, 0
);

-- 2.2 已通过报名（用户20002报名MT2026REG0001，未签到）
INSERT INTO t_registration (
    meeting_id, user_id, name, phone, email, company, position,
    status, registered_at, audited_at, audit_remark, checkin_time, cancel_time, is_deleted
) VALUES (
    @meeting1_id, 20002, '李四', '139****5678', 'lisi@csdn.net', '腾讯', '前端工程师',
    'APPROVED', NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 3 DAY, NULL, NULL, NULL, 0
);

-- 2.3 已通过报名且已签到（用户20003报名MT2026REG0004）
INSERT INTO t_registration (
    meeting_id, user_id, name, phone, email, company, position,
    status, registered_at, audited_at, audit_remark, checkin_time, cancel_time, is_deleted
) VALUES (
    @meeting4_id, 20003, '王五', '137****9012', 'wangwu@csdn.net', '华为', '后端开发',
    'CHECKED_IN', NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 4 DAY, NULL, NOW() - INTERVAL 1 HOUR, NULL, 0
);

-- 2.4 已拒绝报名（用户20004报名MT2026REG0001）
INSERT INTO t_registration (
    meeting_id, user_id, name, phone, email, company, position,
    status, registered_at, audited_at, audit_remark, checkin_time, cancel_time, is_deleted
) VALUES (
    @meeting1_id, 20004, '赵六', '136****3456', 'zhaoliu@csdn.net', '百度', '算法工程师',
    'REJECTED', NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 2 DAY, '报名人数已满，请关注下期活动', NULL, NULL, 0
);

-- 2.5 已取消报名（用户20005曾报名MT2026REG0001后取消）
INSERT INTO t_registration (
    meeting_id, user_id, name, phone, email, company, position,
    status, registered_at, audited_at, audit_remark, checkin_time, cancel_time, is_deleted
) VALUES (
    @meeting1_id, 20005, '钱七', '135****7890', 'qianqi@csdn.net', '字节跳动', '产品经理',
    'CANCELLED', NOW() - INTERVAL 4 DAY, NULL, NULL, NULL, NOW() - INTERVAL 1 DAY, 0
);

-- 2.6 已结束会议的签到记录（用户20006报名MT2026REG0005）
INSERT INTO t_registration (
    meeting_id, user_id, name, phone, email, company, position,
    status, registered_at, audited_at, audit_remark, checkin_time, cancel_time, is_deleted
) VALUES (
    @meeting5_id, 20006, '孙八', '134****2345', 'sunba@csdn.net', '美团', '数据分析师',
    'CHECKED_IN', NOW() - INTERVAL 14 DAY, NOW() - INTERVAL 12 DAY, NULL, NOW() - INTERVAL 7 DAY, NULL, 0
);

-- ============================================
-- 第三部分：签到记录测试数据
-- ============================================

-- 3.1 用户20003在MT2026REG0004的签到记录
INSERT INTO t_checkin_record (
    meeting_id, registration_id, user_id, checkin_time, checkin_method, device_info, ip_address, created_at
) VALUES (
    'MT2026REG0004',
    (SELECT id FROM t_registration WHERE user_id = 20003 AND meeting_id = @meeting4_id LIMIT 1),
    20003,
    NOW() - INTERVAL 1 HOUR,
    'QR_CODE',
    'iPhone 15 Pro / iOS 17.0 / CSDN App 6.5.0',
    '192.168.1.100',
    NOW() - INTERVAL 1 HOUR
);

-- 3.2 用户20006在MT2026REG0005的签到记录（已结束会议）
INSERT INTO t_checkin_record (
    meeting_id, registration_id, user_id, checkin_time, checkin_method, device_info, ip_address, created_at
) VALUES (
    'MT2026REG0005',
    (SELECT id FROM t_registration WHERE user_id = 20006 AND meeting_id = @meeting5_id LIMIT 1),
    20006,
    NOW() - INTERVAL 7 DAY,
    'QR_CODE',
    'Xiaomi 14 / Android 14 / CSDN App 6.4.0',
    '192.168.1.101',
    NOW() - INTERVAL 7 DAY
);

-- ============================================
-- 第四部分：收藏测试数据
-- ============================================

SET @meeting2_id = (SELECT id FROM t_meeting WHERE meeting_id = 'MT2026REG0002' LIMIT 1);
SET @meeting3_id = (SELECT id FROM t_meeting WHERE meeting_id = 'MT2026REG0003' LIMIT 1);
SET @meeting7_id = (SELECT id FROM t_meeting WHERE meeting_id = 'MT2026REG0007' LIMIT 1);

-- 4.1 用户20001收藏MT2026REG0001
INSERT INTO t_meeting_favorite (user_id, meeting_id, created_at) VALUES (20001, @meeting1_id, NOW() - INTERVAL 5 DAY);

-- 4.2 用户20001收藏MT2026REG0002
INSERT INTO t_meeting_favorite (user_id, meeting_id, created_at) VALUES (20001, @meeting2_id, NOW() - INTERVAL 3 DAY);

-- 4.3 用户20002收藏MT2026REG0001
INSERT INTO t_meeting_favorite (user_id, meeting_id, created_at) VALUES (20002, @meeting1_id, NOW() - INTERVAL 4 DAY);

-- 4.4 用户20002收藏MT2026REG0003
INSERT INTO t_meeting_favorite (user_id, meeting_id, created_at) VALUES (20002, @meeting3_id, NOW() - INTERVAL 2 DAY);

-- 4.5 用户20003收藏MT2026REG0007
INSERT INTO t_meeting_favorite (user_id, meeting_id, created_at) VALUES (20003, @meeting7_id, NOW() - INTERVAL 1 DAY);

-- ============================================
-- 第五部分：报名表单配置测试数据
-- ============================================

-- 5.1 MT2026REG0001的自定义表单配置（增加"参会目的"字段为必填）
INSERT INTO t_registration_config (
    meeting_id, config_type, field_name, field_label, field_type, required, editable, source, placeholder, sort_order, enabled
) VALUES 
('MT2026REG0001', 'CUSTOM', 'name', '姓名', 'TEXT', TRUE, TRUE, 'user_profile', '请输入您的真实姓名', 1, TRUE),
('MT2026REG0001', 'CUSTOM', 'phone', '手机号', 'PHONE', TRUE, TRUE, 'user_profile', '用于接收会议通知短信', 2, TRUE),
('MT2026REG0001', 'CUSTOM', 'email', '邮箱', 'EMAIL', FALSE, TRUE, 'user_profile', '用于接收会议详情邮件', 3, TRUE),
('MT2026REG0001', 'CUSTOM', 'company', '公司', 'TEXT', TRUE, TRUE, 'none', '请输入您所在的公司', 4, TRUE),
('MT2026REG0001', 'CUSTOM', 'position', '职位', 'TEXT', TRUE, TRUE, 'none', '例如：前端工程师、产品经理等', 5, TRUE),
('MT2026REG0001', 'CUSTOM', 'purpose', '参会目的', 'TEXTAREA', TRUE, TRUE, 'none', '请详细描述您参加本次会议的目的', 6, TRUE);

-- 5.2 MT2026REG0002的自定义表单配置（简化版，仅必填姓名和手机号）
INSERT INTO t_registration_config (
    meeting_id, config_type, field_name, field_label, field_type, required, editable, source, placeholder, sort_order, enabled
) VALUES 
('MT2026REG0002', 'CUSTOM', 'name', '姓名', 'TEXT', TRUE, TRUE, 'user_profile', '请输入您的真实姓名', 1, TRUE),
('MT2026REG0002', 'CUSTOM', 'phone', '手机号', 'PHONE', TRUE, TRUE, 'user_profile', '用于接收会议通知短信', 2, TRUE);

-- ============================================
-- 数据验证注释
-- ============================================

-- 【会议详情页接口测试数据】
-- GET /api/meetings/{meetingId}/detail-page?userId={userId}
--
-- 场景1: 未登录用户查看可报名会议
--   meetingId: MT2026REG0001, userId: null
--   预期: buttonState.type = REGISTER, 可报名
--
-- 场景2: 已登录用户查看可报名会议（未报名）
--   meetingId: MT2026REG0001, userId: 20010（新用户，无报名记录）
--   预期: buttonState.type = REGISTER, isFavorite = false
--
-- 场景3: 已报名用户查看会议（待审核）
--   meetingId: MT2026REG0001, userId: 20001
--   预期: myRegistration.status = PENDING, buttonState.type = ALREADY_REGISTERED
--
-- 场景4: 已通过报名用户查看会议（未签到）
--   meetingId: MT2026REG0001, userId: 20002
--   预期: myRegistration.status = APPROVED, buttonState.text = "已报名"
--
-- 场景5: 已签到用户查看会议
--   meetingId: MT2026REG0004, userId: 20003
--   预期: myRegistration.status = CHECKED_IN, buttonState.text = "已签到"
--
-- 场景6: 名额已满会议
--   meetingId: MT2026REG0002, userId: 20010
--   预期: buttonState.type = FULL, buttonState.enabled = false
--
-- 场景7: 已收藏用户查看会议
--   meetingId: MT2026REG0001, userId: 20001
--   预期: isFavorite = true

-- 【报名状态查询接口测试数据】
-- GET /api/meetings/{meetingId}/registration-status
--
-- 场景1: 正常报名
--   meetingId: MT2026REG0001
--   预期: registrationOpen = true, remainingSpots = 144, full = false
--
-- 场景2: 名额已满
--   meetingId: MT2026REG0002
--   预期: full = true, registrationOpen = false
--
-- 场景3: 报名已截止
--   meetingId: MT2026REG0003
--   预期: registrationOpen = false
--
-- 场景4: 不限名额
--   meetingId: MT2026REG0007
--   预期: remainingSpots = -1, full = false

-- 【报名相关接口测试数据】
--
-- 场景1: 获取表单配置（自定义配置）
--   GET /api/registrations/config/MT2026REG0001
--   预期: 返回6个字段，purpose为必填
--
-- 场景2: 获取表单配置（使用默认配置）
--   GET /api/registrations/config/MT2026REG0007
--   预期: 返回默认配置的字段列表
--
-- 场景3: 预填表单数据
--   GET /api/registrations/pre-fill?meetingId=MT2026REG0001&userId=20001
--   预期: 返回用户画像预填数据（当前为模拟数据）
--
-- 场景4: 提交报名
--   POST /api/registrations
--   预期: 创建报名记录，状态=PENDING，会议报名人数+1
--
-- 场景5: 查询我的报名
--   GET /api/registrations/my/MT2026REG0001?userId=20001
--   预期: 返回用户20001在MT2026REG0001的报名记录
--
-- 场景6: 取消报名
--   POST /api/registrations/{regId}/cancel?userId=20005
--   预期: 报名状态=CANCELLED，会议报名人数-1

-- 【签到相关接口测试数据】
--
-- 场景1: 生成签到码
--   POST /api/meetings/MT2026REG0001/checkin-code
--   预期: 返回checkinToken和qrContent
--
-- 场景2: 扫码签到成功
--   POST /api/checkin
--   body: {meetingId: MT2026REG0004, userId: 20002, checkinToken: xxx}
--   预期: 签到成功，状态变更为CHECKED_IN
--
-- 场景3: 重复签到
--   POST /api/checkin (使用已签到的用户20003)
--   预期: 返回duplicate提示
--
-- 场景4: 未报名扫码
--   POST /api/checkin (使用未报名的用户)
--   预期: 返回not_registered提示
--
-- 场景5: 查询签到状态
--   GET /api/checkin/status?meetingId=MT2026REG0004&userId=20003
--   预期: checkedIn = true, checkinTime有值

-- 【收藏相关接口测试数据】
--
-- 场景1: 切换收藏（收藏）
--   POST /api/meetings/MT2026REG0007/favorite?userId=20001
--   预期: favorited = true, message = "收藏成功"
--
-- 场景2: 切换收藏（取消收藏）
--   POST /api/meetings/MT2026REG0001/favorite?userId=20001
--   预期: favorited = false, message = "已取消收藏"
--
-- 场景3: 查询收藏状态（已收藏）
--   GET /api/meetings/MT2026REG0001/favorite?userId=20001
--   预期: true
--
-- 场景4: 查询收藏状态（未收藏）
--   GET /api/meetings/MT2026REG0007/favorite?userId=20002
--   预期: false

-- ============================================
