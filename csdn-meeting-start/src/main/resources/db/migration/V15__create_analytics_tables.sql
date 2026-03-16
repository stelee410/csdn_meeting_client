-- 埋点事件主表
-- 存储所有埋点事件的核心信息
CREATE TABLE `analytics_event` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT,
    `event_id`        VARCHAR(64) NOT NULL COMMENT '事件唯一标识(UUID)',
    `event_type`      VARCHAR(50) NOT NULL COMMENT '事件类型(如:meeting_create, tag_subscribe)',
    `event_category`  VARCHAR(30) NOT NULL COMMENT '事件类别(client/operation/mobile)',
    `user_id`         VARCHAR(64) COMMENT '用户ID',
    `user_type`       TINYINT DEFAULT 1 COMMENT '用户类型(1=普通用户, 2=运营人员)',
    `anonymous_id`    VARCHAR(64) COMMENT '匿名用户标识',
    `session_id`      VARCHAR(64) COMMENT '会话ID',
    `device_id`       VARCHAR(64) COMMENT '设备标识',
    `platform`        VARCHAR(20) COMMENT '平台(web/ios/android/miniapp)',
    `app_version`     VARCHAR(20) COMMENT '应用版本',
    `ip_address`      VARCHAR(50) COMMENT 'IP地址',
    `user_agent`      VARCHAR(500) COMMENT '用户代理字符串',
    `occurred_at`     DATETIME NOT NULL COMMENT '事件发生时间',
    `created_at`      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_event_id` (`event_id`),
    INDEX `idx_event_type_occurred` (`event_type`, `occurred_at`),
    INDEX `idx_user_id_occurred` (`user_id`, `occurred_at`),
    INDEX `idx_session_id` (`session_id`),
    INDEX `idx_occurred_at` (`occurred_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='埋点事件主表';

-- 事件属性表
-- 存储事件的动态属性，支持灵活的键值对扩展
CREATE TABLE `analytics_event_property` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT,
    `event_id`        VARCHAR(64) NOT NULL COMMENT '关联事件ID',
    `property_key`    VARCHAR(100) NOT NULL COMMENT '属性键',
    `property_value`  TEXT COMMENT '属性值(字符串/JSON)',
    `value_type`      TINYINT DEFAULT 1 COMMENT '值类型(1=字符串,2=数字,3=布尔,4=JSON)',
    `created_at`      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_event_id` (`event_id`),
    INDEX `idx_event_id_key` (`event_id`, `property_key`),
    FOREIGN KEY (`event_id`) REFERENCES `analytics_event`(`event_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='事件属性表';

-- 会话跟踪表
-- 用户会话信息
CREATE TABLE `analytics_session` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT,
    `session_id`      VARCHAR(64) NOT NULL COMMENT '会话ID',
    `user_id`         VARCHAR(64) COMMENT '用户ID',
    `platform`        VARCHAR(20) COMMENT '平台',
    `device_info`     VARCHAR(200) COMMENT '设备信息',
    `ip_address`      VARCHAR(50) COMMENT 'IP地址',
    `geo_location`    VARCHAR(100) COMMENT '地理位置',
    `started_at`      DATETIME NOT NULL COMMENT '会话开始时间',
    `ended_at`        DATETIME COMMENT '会话结束时间',
    `page_views`      INT DEFAULT 0 COMMENT '页面浏览次数',
    `events_count`    INT DEFAULT 0 COMMENT '事件触发次数',
    `created_at`      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_session_id` (`session_id`),
    INDEX `idx_user_id_started` (`user_id`, `started_at`),
    INDEX `idx_started_at` (`started_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会话跟踪表';

-- 会议业务事件扩展表
-- 针对会议业务的专用扩展表
CREATE TABLE `analytics_meeting_event` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT,
    `event_id`        VARCHAR(64) NOT NULL COMMENT '关联事件ID',
    `meeting_id`      VARCHAR(64) COMMENT '会议ID',
    `meeting_title`   VARCHAR(200) COMMENT '会议标题',
    `organizer_id`    VARCHAR(64) COMMENT '主办方ID',
    `action_type`     VARCHAR(30) COMMENT '操作类型',
    `source`          VARCHAR(50) COMMENT '来源渠道',
    `referrer`        VARCHAR(200) COMMENT '引荐页面',
    `created_at`      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_event_id` (`event_id`),
    INDEX `idx_meeting_id` (`meeting_id`),
    INDEX `idx_meeting_id_action` (`meeting_id`, `action_type`),
    INDEX `idx_organizer_id` (`organizer_id`),
    FOREIGN KEY (`event_id`) REFERENCES `analytics_event`(`event_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会议业务事件扩展表';

-- 用户画像表
-- 用户属性汇总
CREATE TABLE `analytics_user_profile` (
    `id`                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id`             VARCHAR(64) NOT NULL COMMENT '用户ID',
    `first_visit_at`      DATETIME COMMENT '首次访问时间',
    `last_visit_at`       DATETIME COMMENT '最后访问时间',
    `total_sessions`      INT DEFAULT 0 COMMENT '总会话数',
    `total_events`        INT DEFAULT 0 COMMENT '总事件数',
    `preferred_tags`      VARCHAR(200) COMMENT '偏好标签',
    `device_platforms`    VARCHAR(100) COMMENT '使用过的平台',
    `created_at`          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at`          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_user_id` (`user_id`),
    INDEX `idx_last_visit` (`last_visit_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户画像表';

-- 标签订阅埋点表（业务专用）
-- 记录用户的标签订阅/取消订阅行为
CREATE TABLE `analytics_tag_subscription` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT,
    `event_id`        VARCHAR(64) NOT NULL COMMENT '关联事件ID',
    `user_id`         VARCHAR(64) NOT NULL COMMENT '用户ID',
    `tag_id`          BIGINT NOT NULL COMMENT '标签ID',
    `tag_name`        VARCHAR(50) COMMENT '标签名称',
    `action`          VARCHAR(20) NOT NULL COMMENT '操作类型(subscribe/unsubscribe)',
    `source`          VARCHAR(50) COMMENT '操作来源',
    `occurred_at`     DATETIME NOT NULL COMMENT '发生时间',
    `created_at`      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_tag_id` (`tag_id`),
    INDEX `idx_user_tag` (`user_id`, `tag_id`),
    INDEX `idx_occurred_at` (`occurred_at`),
    FOREIGN KEY (`event_id`) REFERENCES `analytics_event`(`event_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签订阅埋点表';

-- 会议列表筛选埋点表（业务专用）
-- 记录会议列表的筛选操作
CREATE TABLE `analytics_meeting_list_filter` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT,
    `event_id`        VARCHAR(64) NOT NULL COMMENT '关联事件ID',
    `user_id`         VARCHAR(64) COMMENT '用户ID',
    `format`          VARCHAR(20) COMMENT '会议形式(ONLINE/OFFLINE/HYBRID)',
    `meeting_type`    VARCHAR(20) COMMENT '会议类型',
    `scene`           VARCHAR(50) COMMENT '会议场景',
    `time_range`      VARCHAR(20) COMMENT '时间范围',
    `keyword`         VARCHAR(100) COMMENT '搜索关键词',
    `result_count`      INT COMMENT '返回结果数',
    `occurred_at`     DATETIME NOT NULL COMMENT '发生时间',
    `created_at`      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_occurred_at` (`occurred_at`),
    FOREIGN KEY (`event_id`) REFERENCES `analytics_event`(`event_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会议列表筛选埋点表';

-- 移动端埋点事件表（业务专用）
-- 记录移动端特有的事件
CREATE TABLE `analytics_mobile_event` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT,
    `event_id`        VARCHAR(64) NOT NULL COMMENT '关联事件ID',
    `user_id`         VARCHAR(64) COMMENT '用户ID',
    `event_name`      VARCHAR(50) NOT NULL COMMENT '事件名称',
    `source`          VARCHAR(50) COMMENT '来源渠道',
    `meeting_id`      VARCHAR(64) COMMENT '会议ID',
    `result`          VARCHAR(20) COMMENT '操作结果(success/fail)',
    `extra_data`      JSON COMMENT '额外数据',
    `occurred_at`     DATETIME NOT NULL COMMENT '发生时间',
    `created_at`      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_event_name` (`event_name`),
    INDEX `idx_meeting_id` (`meeting_id`),
    INDEX `idx_occurred_at` (`occurred_at`),
    FOREIGN KEY (`event_id`) REFERENCES `analytics_event`(`event_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='移动端埋点事件表';
