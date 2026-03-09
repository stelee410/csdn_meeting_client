-- ============================================
-- V11: 将 tags 相关列从 JSON 改为 VARCHAR
-- 创建时间: 2026-03-09
-- 原因: tags/default_tags 实际存储逗号分隔的 ID 字符串（如 "1,2,3"），
--       Java 侧始终按 String 处理，JSON 类型无任何额外收益，改为 VARCHAR 更直观。
-- ============================================

-- 1. t_meeting.tags: JSON → VARCHAR(255)
ALTER TABLE t_meeting
    MODIFY COLUMN `tags` VARCHAR(255) COMMENT '会议标签ID列表，逗号分隔，如 1,2,3，最多5个';

-- 2. t_meeting_template.default_tags: JSON → VARCHAR(255)
ALTER TABLE t_meeting_template
    MODIFY COLUMN `default_tags` VARCHAR(255) COMMENT '预置标签ID列表，逗号分隔，如 1,2,3';
