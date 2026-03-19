-- 删除旧埋点相关表（如果存在）
DROP TABLE IF EXISTS `analytics_event_property`;
DROP TABLE IF EXISTS `analytics_session`;
DROP TABLE IF EXISTS `analytics_meeting_event`;
DROP TABLE IF EXISTS `analytics_user_profile`;
DROP TABLE IF EXISTS `analytics_tag_subscription`;
DROP TABLE IF EXISTS `analytics_meeting_list_filter`;
DROP TABLE IF EXISTS `analytics_mobile_event`;
DROP TABLE IF EXISTS `analytics_event`;

-- 创建新的简化埋点事件表
-- 统一存储所有埋点数据，使用JSON格式存储动态属性
CREATE TABLE `analytics_track_event` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT,
    `event_id`        VARCHAR(64) NOT NULL COMMENT '事件唯一标识(UUID)',
    `module`          VARCHAR(50) NOT NULL COMMENT '模块(如: meeting_list, audit, dashboard)',
    `action`          VARCHAR(50) NOT NULL COMMENT '动作(如: click_view_switch, click_filter)',
    `event_type`      VARCHAR(30) NOT NULL COMMENT '事件类型(client/operation/mobile)',
    `user_id`         VARCHAR(64) COMMENT '用户ID',
    `anonymous_id`    VARCHAR(64) COMMENT '匿名用户标识',
    `session_id`      VARCHAR(64) COMMENT '会话ID',
    `device_id`       VARCHAR(64) COMMENT '设备标识',
    `platform`        VARCHAR(20) COMMENT '平台(web/ios/android/miniapp)',
    `app_version`     VARCHAR(20) COMMENT '应用版本',
    `ip_address`      VARCHAR(50) COMMENT 'IP地址',
    `user_agent`      VARCHAR(500) COMMENT '用户代理',
    `occurred_at`     DATETIME NOT NULL COMMENT '事件发生时间(前端上报)',
    `received_at`     TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '后端接收时间',
    `properties`      JSON COMMENT '事件属性(JSON格式)',
    INDEX `idx_event_id` (`event_id`),
    INDEX `idx_module_action` (`module`, `action`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_occurred_at` (`occurred_at`),
    INDEX `idx_event_type` (`event_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='埋点事件表';
