-- ============================================
-- V6: 为所有表添加逻辑删除字段
-- 创建时间: 2026-03-07
-- 功能: 统一实现软删除功能
-- ============================================

-- 为t_meeting表添加逻辑删除字段
ALTER TABLE t_meeting ADD COLUMN is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除';

-- 为t_meeting_agenda_item表添加逻辑删除字段
ALTER TABLE t_meeting_agenda_item ADD COLUMN is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除';

-- 为t_meeting_template表添加逻辑删除字段
ALTER TABLE t_meeting_template ADD COLUMN is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除';

-- 为t_registration表添加逻辑删除字段
ALTER TABLE t_registration ADD COLUMN is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除';

-- 为t_meeting_favorite表添加逻辑删除字段
ALTER TABLE t_meeting_favorite ADD COLUMN is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除';

-- 为t_meeting_rights表添加逻辑删除字段
ALTER TABLE t_meeting_rights ADD COLUMN is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除';

-- 为t_meeting_bill表添加逻辑删除字段
ALTER TABLE t_meeting_bill ADD COLUMN is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除';

-- 为t_promotion_config表添加逻辑删除字段
ALTER TABLE t_promotion_config ADD COLUMN is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除';

-- 为t_promotion_stats表添加逻辑删除字段
ALTER TABLE t_promotion_stats ADD COLUMN is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除';

-- 为t_participant表添加逻辑删除字段
ALTER TABLE t_participant ADD COLUMN is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除';
