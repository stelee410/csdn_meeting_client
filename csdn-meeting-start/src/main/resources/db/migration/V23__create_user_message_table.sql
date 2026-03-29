-- ============================================
-- V23: 创建用户消息表
-- 创建时间: 2026-03-29
-- 功能: 支持系统内部消息推送（站内信）
-- ============================================

-- 创建用户消息表（站内信）
CREATE TABLE IF NOT EXISTS `t_user_message` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '自增主键',
    `message_id`      VARCHAR(64) NOT NULL COMMENT '消息业务ID',
    `user_id`         VARCHAR(64) NOT NULL COMMENT '接收用户ID',
    `message_type`    TINYINT NOT NULL DEFAULT 1 COMMENT '消息类型: 1-会议发布 2-报名通过 3-报名拒绝',
    `title`           VARCHAR(100) NOT NULL COMMENT '消息标题',
    `content`         TEXT COMMENT '消息内容（HTML或纯文本）',
    `biz_id`          VARCHAR(64) COMMENT '关联业务ID（如会议ID）',
    `biz_type`        VARCHAR(20) COMMENT '业务类型: MEETING/REGISTRATION',
    `extra_data`      JSON COMMENT '扩展数据（如会议标题、标签名等）',
    `is_read`         BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    `read_time`       TIMESTAMP COMMENT '阅读时间',
    `is_deleted`      BOOLEAN DEFAULT FALSE COMMENT '是否删除（用户删除）',
    `created_at`      TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    -- 唯一索引
    UNIQUE KEY `uk_message_id` (`message_id`),

    -- 查询索引
    INDEX `idx_user_id_created` (`user_id`, `created_at`),
    INDEX `idx_user_id_read` (`user_id`, `is_read`),
    INDEX `idx_biz_id` (`biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户消息表（站内信）';
