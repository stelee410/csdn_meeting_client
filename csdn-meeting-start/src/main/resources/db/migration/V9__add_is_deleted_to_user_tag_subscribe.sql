-- ============================================
-- V9: 为 t_user_tag_subscribe 表增加软删除标志
-- 创建时间: 2026-03-08
-- 功能: 支持订阅记录的软删除（取消订阅）
-- ============================================

ALTER TABLE t_user_tag_subscribe
    ADD COLUMN is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标志：0-未删除（已订阅），1-已删除（取消订阅）';

-- 可选：为查询加索引
CREATE INDEX idx_user_tag_subscribe_is_deleted ON t_user_tag_subscribe (is_deleted);
