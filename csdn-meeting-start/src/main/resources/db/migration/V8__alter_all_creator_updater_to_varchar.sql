-- ============================================
-- V8: 所有表创建人/更新人字段改为 VARCHAR 类型
-- 创建时间: 2026-03-08
-- ============================================

-- 会议表：creator_id 改为 VARCHAR(64)（Flyway V1 创建的 t_meeting）
ALTER TABLE t_meeting
    MODIFY COLUMN creator_id VARCHAR(64) NOT NULL COMMENT '创建者ID（复用CSDN账号）';
