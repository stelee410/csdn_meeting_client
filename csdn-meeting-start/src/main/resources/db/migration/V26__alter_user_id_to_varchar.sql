-- ============================================
-- V26: 修改用户ID相关表 user_id 字段为 VARCHAR 类型
-- 创建时间: 2026-04-04
-- 原因: JWT Token中用户ID改为字符串格式（如U927CFE0E0D2F4A65），需要同步修改数据库字段类型
-- ============================================

-- 报名记录表：user_id 改为 VARCHAR(64)
ALTER TABLE t_registration
    MODIFY COLUMN user_id VARCHAR(64) NOT NULL COMMENT '用户ID（CSDN统一账号，字符串格式）';

-- 会议收藏表：user_id 改为 VARCHAR(64)
ALTER TABLE t_meeting_favorite
    MODIFY COLUMN user_id VARCHAR(64) NOT NULL COMMENT '用户ID（CSDN统一账号，字符串格式）';

-- 会议参与者表：user_id 改为 VARCHAR(64)
ALTER TABLE t_participant
    MODIFY COLUMN user_id VARCHAR(64) NOT NULL COMMENT '用户ID（CSDN统一账号，字符串格式）';
