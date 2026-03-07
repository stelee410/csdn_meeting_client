-- 会议主表
CREATE TABLE `t_meeting` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT,
    `title`           VARCHAR(50) NOT NULL COMMENT '会议名称，最大50字',
    `organizer`       VARCHAR(100) COMMENT '主办方/公司名',
    `creator_id`      VARCHAR(64) NOT NULL COMMENT '创建者ID（复用CSDN账号）',
    `format`          VARCHAR(20) COMMENT 'ONLINE/OFFLINE/HYBRID',
    `scene`           VARCHAR(50) COMMENT '会议场景',
    `start_time`      DATETIME COMMENT '会议开始时间（精确到分钟）',
    `end_time`        DATETIME COMMENT '会议结束时间（精确到分钟）',
    `venue`           VARCHAR(255) COMMENT '举办地址（线下时必填）',
    `regions`         JSON COMMENT '涉及区域（区域营销时必填）',
    `cover_image`     VARCHAR(500) COMMENT '会议海报URL，16:9',
    `description`     TEXT COMMENT '会议简介（富文本）',
    `tags`            JSON COMMENT '会议标签列表，最多5个',
    `target_audience` JSON COMMENT '适合人群标签（职级+技术方向）',
    `status`          TINYINT DEFAULT 0 COMMENT '0-草稿 1-待审 2-已发布 3-进行中 4-已结束 5-已拒绝 6-已下架 7-已删除',
    `is_premium`      BOOLEAN DEFAULT FALSE COMMENT '是否已购买高阶数据权益',
    `takedown_reason` VARCHAR(500) COMMENT '下架原因',
    `reject_reason`   VARCHAR(500) COMMENT '拒绝原因',
    `created_at`      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 会议日程四级结构（树状存储）
-- level: 1=ScheduleDay 2=Session 3=SubVenue 4=Topic
-- extra字段按level存储不同扩展信息：
--   level=1: { "schedule_date": "2026-03-01", "day_label": "Day1" }
--   level=2: { "start_time": "09:00", "end_time": "12:00", "session_name": "上午" }
--   level=3: { "sub_venue_name": "主会场" }
--   level=4: { "guests": [], "topic_intro": "", "involved_products": "" }
CREATE TABLE `t_meeting_agenda_item` (
    `id`         BIGINT PRIMARY KEY AUTO_INCREMENT,
    `meeting_id` BIGINT NOT NULL,
    `parent_id`  BIGINT COMMENT 'NULL表示ScheduleDay；其余指向父级节点',
    `level`      TINYINT NOT NULL COMMENT '1-日程日 2-环节 3-分会场 4-议题',
    `title`      VARCHAR(200) COMMENT '节点标题（议题时为topic_title，最大100字）',
    `sort_order` INT DEFAULT 0 COMMENT '同级排序',
    `extra`      JSON COMMENT '扩展字段，按level存储对应元数据',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_meeting_id` (`meeting_id`),
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 活动模板表（运营维护，办会方只读）
CREATE TABLE `t_meeting_template` (
    `id`                   BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name`                 VARCHAR(100) NOT NULL COMMENT '模板名称，如"技术沙龙"',
    `scene`                VARCHAR(50) COMMENT '预置会议场景',
    `description_template` TEXT COMMENT '简介骨架（Markdown格式）',
    `default_tags`         JSON COMMENT '预置标签列表',
    `target_audience`      JSON COMMENT '预置适合人群标签',
    `sort_order`           INT DEFAULT 0 COMMENT '展示排序',
    `is_active`            BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    `created_at`           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at`           TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
