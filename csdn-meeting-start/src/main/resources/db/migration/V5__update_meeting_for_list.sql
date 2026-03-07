-- ============================================
-- V5: 会议列表功能数据库变更
-- 创建时间: 2026-03-07
-- 功能: 支持双视图列表、多维度筛选、标签订阅
-- ============================================

-- 1. 补充t_meeting字段（用于列表展示和筛选）
-- 注意：venue字段已在V1中创建，此处不再重复添加
ALTER TABLE t_meeting
    ADD COLUMN meeting_id VARCHAR(50) UNIQUE COMMENT '业务ID（如 MT0000000001）' AFTER id,
    ADD COLUMN poster_url VARCHAR(500) COMMENT '海报URL（阅读视图16:9大图）' AFTER cover_image,
    ADD COLUMN hot_score INT DEFAULT 0 COMMENT '热度分（报名人数）' AFTER status,
    ADD COLUMN current_participants INT DEFAULT 0 COMMENT '当前报名人数' AFTER hot_score,
    ADD COLUMN max_participants INT COMMENT '最大参与人数' AFTER current_participants,
    ADD COLUMN city_code VARCHAR(20) COMMENT '城市编码' AFTER regions,
    ADD COLUMN city_name VARCHAR(50) COMMENT '城市名称（列表视图展示）' AFTER city_code,
    ADD COLUMN meeting_type VARCHAR(20) COMMENT '会议类型：SUMMIT/SALON/WORKSHOP' AFTER format,
    ADD COLUMN organizer_id BIGINT COMMENT '主办方用户ID' AFTER organizer,
    ADD COLUMN organizer_name VARCHAR(100) COMMENT '主办方名称' AFTER organizer_id,
    ADD COLUMN organizer_avatar VARCHAR(500) COMMENT '主办方头像URL' AFTER organizer_name;

-- 为现有数据生成meeting_id（如果有数据）
-- UPDATE t_meeting SET meeting_id = CONCAT('MT', LPAD(id, 10, '0')) WHERE meeting_id IS NULL;

-- 2. 创建标签主表
CREATE TABLE IF NOT EXISTS t_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tag_name VARCHAR(50) NOT NULL COMMENT '标签名称（如 Java、AI、鸿蒙）',
    tag_category VARCHAR(20) COMMENT '标签分类：TECH/INDUSTRY/TOPIC/FORM',
    is_system BOOLEAN DEFAULT FALSE COMMENT '是否系统预设标签',
    sort_order INT DEFAULT 0 COMMENT '排序权重',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_tag_name (tag_name),
    INDEX idx_category (tag_category),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签主表';

-- 3. 创建会议标签关联表
CREATE TABLE IF NOT EXISTS t_meeting_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    meeting_id VARCHAR(50) NOT NULL COMMENT '会议业务ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    is_deleted TINYINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_meeting_tag (meeting_id, tag_id),
    INDEX idx_meeting_id (meeting_id),
    INDEX idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会议标签关联表';

-- 4. 创建用户标签订阅表
CREATE TABLE IF NOT EXISTS t_user_tag_subscribe (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否有效订阅',
    notify_site BOOLEAN DEFAULT TRUE COMMENT '是否接收站内信',
    notify_push BOOLEAN DEFAULT TRUE COMMENT '是否接收Push',
    last_notify_at TIMESTAMP COMMENT '上次通知时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_tag (user_id, tag_id),
    INDEX idx_user_id (user_id),
    INDEX idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户标签订阅表';
