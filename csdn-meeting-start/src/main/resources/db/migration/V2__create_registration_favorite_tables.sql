-- 报名记录表
CREATE TABLE `t_registration` (
    `id`            BIGINT PRIMARY KEY AUTO_INCREMENT,
    `meeting_id`    BIGINT NOT NULL,
    `user_id`       BIGINT NOT NULL,
    `name`          VARCHAR(100) COMMENT '报名人姓名',
    `phone`         VARCHAR(20) COMMENT '手机号（脱敏存储）',
    `email`         VARCHAR(200),
    `company`       VARCHAR(200) COMMENT '公司',
    `position`      VARCHAR(100) COMMENT '职位',
    `status`        VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED',
    `registered_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `audited_at`    TIMESTAMP NULL DEFAULT NULL COMMENT '审核时间',
    `audit_remark`  VARCHAR(500) COMMENT '拒绝备注',
    INDEX `idx_meeting_status` (`meeting_id`, `status`),
    UNIQUE KEY `uk_meeting_user` (`meeting_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 会议收藏表
CREATE TABLE `t_meeting_favorite` (
    `id`         BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id`    BIGINT NOT NULL,
    `meeting_id` BIGINT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_user_meeting` (`user_id`, `meeting_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
