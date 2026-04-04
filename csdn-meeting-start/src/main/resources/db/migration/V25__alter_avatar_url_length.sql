-- ============================================
-- V25: 修改用户表头像URL字段长度
-- 创建时间: 2026-04-04
-- 功能:
--   1. 将 t_user 表的 avatar_url 从 VARCHAR(500) 扩展为大文本
--   2. 解决超长内容（如 Base64 大图、带长签名参数的 URL）导致的 Data truncation
-- 说明:
--   TEXT 最大约 64KB，不足以容纳数百 KB 的 Base64；使用 MEDIUMTEXT（最大约 16MB）
-- ============================================

ALTER TABLE `t_user` MODIFY COLUMN `avatar_url` MEDIUMTEXT COMMENT '头像URL';
