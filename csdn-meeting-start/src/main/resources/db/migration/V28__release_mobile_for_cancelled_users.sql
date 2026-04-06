-- ============================================================================
-- 功能：处理已注销用户的手机号，加前缀释放手机号供重新注册
-- 说明：
-- 1. 首先扩展 mobile 字段长度以容纳前缀+时间戳
-- 2. 给所有status=2（已注销）且手机号未加前缀的用户添加CANCELLED_前缀
-- 3. 前缀格式：C_原手机号_时间戳（毫秒），缩短前缀避免超过字段长度
-- ============================================================================

-- 步骤1：扩展 mobile 字段长度以容纳前缀+时间戳（11位手机+2位前缀+1位分隔符+13位时间戳=27位）
ALTER TABLE t_user MODIFY COLUMN mobile VARCHAR(50) NOT NULL COMMENT '手机号，唯一索引。注销用户会加前缀：C_手机号_时间戳';

-- 步骤2：先查询有多少已注销用户需要处理（用于确认影响范围）
-- SELECT COUNT(*) FROM t_user WHERE status = 2 AND mobile NOT LIKE 'C_%';

-- 步骤3：执行更新，给已注销用户的手机号加前缀（使用缩短的前缀C_）
UPDATE t_user
SET mobile = CONCAT('C_', mobile, '_', UNIX_TIMESTAMP(COALESCE(update_time, create_time)) * 1000)
WHERE status = 2
  AND mobile NOT LIKE 'C_%';

-- 步骤4：验证更新结果（执行后手动检查）
-- SELECT user_id, mobile, status, created_at FROM t_user WHERE status = 2 LIMIT 5;