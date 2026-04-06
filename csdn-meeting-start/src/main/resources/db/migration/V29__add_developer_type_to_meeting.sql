-- 给 t_meeting 表添加 developer_type 列，存储逗号分隔的开发者类型 code（如 frontend,backend）
-- 使用存储过程兼容列已存在的情况（MySQL 5.7 不支持 ADD COLUMN IF NOT EXISTS）
DROP PROCEDURE IF EXISTS add_developer_type_column;

CREATE PROCEDURE add_developer_type_column()
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 't_meeting'
          AND COLUMN_NAME = 'developer_type'
    ) THEN
        ALTER TABLE t_meeting ADD COLUMN developer_type VARCHAR(500) NULL COMMENT '开发者类型，逗号分隔 code';
    END IF;
END;

CALL add_developer_type_column();

DROP PROCEDURE IF EXISTS add_developer_type_column;
