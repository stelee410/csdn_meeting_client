-- ============================================
-- V22: 创建用户体系核心表
-- 创建时间: 2026-03-29
-- 功能:
--   1. 创建 t_user 用户主表（client+operation共享，user_type区分类型）
--   2. 创建 t_verification_code 验证码记录表
-- ============================================

-- 用户主表（client端和operation端共享）
CREATE TABLE `t_user` (
    `id`                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '自增主键',
    `user_id`             VARCHAR(64) NOT NULL COMMENT '业务用户ID，对外暴露',
    `mobile`              VARCHAR(20) NOT NULL COMMENT '手机号，唯一索引',
    `password`            VARCHAR(100) COMMENT '加密密码（BCrypt）',
    `user_type`           TINYINT DEFAULT 0 COMMENT '用户类型：0-普通用户(USER), 1-管理员(ADMIN), 2-运营人员(OPERATOR)',
    `csdn_bind_id`        VARCHAR(64) COMMENT 'CSDN关联标识',
    `nickname`            VARCHAR(50) COMMENT '昵称',
    `avatar_url`          VARCHAR(500) COMMENT '头像URL',
    `email`               VARCHAR(100) COMMENT '邮箱',
    `email_verified`      BOOLEAN DEFAULT FALSE COMMENT '邮箱是否已验证',
    `real_name`           VARCHAR(50) COMMENT '真实姓名',
    `company`             VARCHAR(100) COMMENT '公司',
    `job_title`           VARCHAR(50) COMMENT '职位',
    `industry`            VARCHAR(50) COMMENT '行业（与会议侧产业枚举一致：AI人工智能、云计算、开源、出海、鸿蒙、游戏、金融）',
    `status`              TINYINT DEFAULT 0 COMMENT '账号状态：0-正常(NORMAL), 1-冻结(FROZEN)',
    `agreement_accepted`  BOOLEAN DEFAULT FALSE COMMENT '是否同意用户协议',
    `privacy_accepted`    BOOLEAN DEFAULT FALSE COMMENT '是否同意隐私政策',
    `created_at`          TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `last_login_at`       TIMESTAMP NULL COMMENT '最后登录时间',

    -- 审计字段（编码规范要求）
    `is_delete`           TINYINT DEFAULT 0 COMMENT '软删除标志：0-未删除, 1-已删除',
    `create_by`           VARCHAR(64) COMMENT '创建人',
    `create_time`         TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`           VARCHAR(64) COMMENT '修改人',
    `update_time`         TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',

    -- 唯一索引
    UNIQUE KEY `uk_mobile` (`mobile`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    UNIQUE KEY `uk_csdn_bind_id` (`csdn_bind_id`),

    -- 查询索引
    INDEX `idx_user_type` (`user_type`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_is_delete` (`is_delete`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户主表（client+operation共享）';

-- 验证码记录表（仅client端使用）
CREATE TABLE `t_verification_code` (
    `id`            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '自增主键',
    `target`        VARCHAR(100) NOT NULL COMMENT '目标（手机号/邮箱）',
    `code`          VARCHAR(10) NOT NULL COMMENT '验证码',
    `type`          TINYINT NOT NULL COMMENT '验证码类型：0-短信(SMS), 1-邮箱(EMAIL)',
    `scene`         TINYINT NOT NULL COMMENT '业务场景：0-注册(REGISTER), 1-登录(LOGIN), 2-重置密码(RESET)',
    `expire_time`   TIMESTAMP NOT NULL COMMENT '过期时间',
    `used`          BOOLEAN DEFAULT FALSE COMMENT '是否已使用',

    -- 审计字段（编码规范要求）
    `is_delete`     TINYINT DEFAULT 0 COMMENT '软删除标志：0-未删除, 1-已删除',
    `create_by`     VARCHAR(64) COMMENT '创建人',
    `create_time`   TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`     VARCHAR(64) COMMENT '修改人',
    `update_time`   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',

    -- 复合索引：快速查询指定目标的最新有效验证码
    INDEX `idx_target_type_scene` (`target`, `type`, `scene`),
    INDEX `idx_expire_time` (`expire_time`),
    INDEX `idx_create_time` (`create_time`),
    INDEX `idx_is_delete` (`is_delete`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验证码记录表';
