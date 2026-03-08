-- ============================================
-- V10: 添加发布时间字段到 t_meeting 表
-- 创建时间: 2026-03-08
-- 功能: 支持会议发布时间排序（与创建时间 created_at 不同）
-- ============================================

-- 1. 添加 publish_time 字段到 t_meeting 表
ALTER TABLE t_meeting
    ADD COLUMN publish_time DATETIME COMMENT '发布时间（会议审核通过变为已发布状态的时间）' AFTER status;

-- 2. 为现有数据初始化 publish_time（已发布/进行中/已结束的会议，设置为创建时间）
UPDATE t_meeting
SET publish_time = created_at
WHERE status IN (2, 3, 4) AND publish_time IS NULL;

-- 3. 创建索引（用于按发布时间排序查询）
CREATE INDEX idx_publish_time ON t_meeting(publish_time);

-- 4. 创建触发器：当会议状态从非发布变为已发布(2)时，自动设置 publish_time
DELIMITER $$
CREATE TRIGGER trg_meeting_set_publish_time
BEFORE UPDATE ON t_meeting
FOR EACH ROW
BEGIN
    -- 如果状态变为已发布(2)且 publish_time 为空，则设置为当前时间
    IF NEW.status = 2 AND OLD.status != 2 AND NEW.publish_time IS NULL THEN
        SET NEW.publish_time = NOW();
    END IF;
END$$
DELIMITER ;
