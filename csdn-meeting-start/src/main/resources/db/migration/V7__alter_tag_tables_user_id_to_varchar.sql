-- ============================================
-- V7: 标签相关表 user_id 改为 VARCHAR 类型
-- 创建时间: 2026-03-08
-- ============================================

-- 用户标签订阅表：user_id 改为 VARCHAR(64)
ALTER TABLE t_user_tag_subscribe
    MODIFY COLUMN user_id VARCHAR(64) NOT NULL COMMENT '用户ID';
