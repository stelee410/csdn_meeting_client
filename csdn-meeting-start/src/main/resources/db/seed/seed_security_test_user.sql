-- 安全测试用户（密码登录，与 PasswordService/jBCrypt 一致）
-- 明文密码: TestSecure2026a（≥8 位且含字母与数字，符合注册强度规则）
-- 仅在不存在该手机号时插入，可重复执行
--
-- 登录: 手机号 13900009999 + 上述密码（生产环境用毕请删除或改密）

INSERT INTO t_user (
    user_id,
    mobile,
    password,
    user_type,
    nickname,
    status,
    is_delete,
    agreement_accepted,
    privacy_accepted,
    email_verified,
    created_at
)
SELECT
    'U1234567890123456',
    '13900009999',
    '$2a$10$ssUBjqriec6sjUt/duAalu2uPCHLiVjphS3k6sCKZDLRjRocyW/E2',
    0,
    '安全测试用户',
    0,
    0,
    1,
    1,
    0,
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM t_user WHERE mobile = '13900009999');
