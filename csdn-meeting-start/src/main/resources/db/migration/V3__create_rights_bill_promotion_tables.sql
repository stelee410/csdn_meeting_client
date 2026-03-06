-- 会议数据高阶权益记录表
CREATE TABLE IF NOT EXISTS `t_meeting_rights` (
    `id`          BIGINT PRIMARY KEY AUTO_INCREMENT,
    `meeting_id`  BIGINT NOT NULL,
    `rights_type` VARCHAR(50) COMMENT 'DATA_PREMIUM（含用户画像+简报高阶数据）',
    `status`      VARCHAR(20) COMMENT 'ACTIVE/INACTIVE',
    `active_time` DATETIME COMMENT '权益生效时间',
    `order_no`    VARCHAR(100) COMMENT '关联支付订单号',
    `created_at`  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_meeting_id` (`meeting_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 账单明细表（本期不提供前端，仅建表记录流水）
CREATE TABLE IF NOT EXISTS `t_meeting_bill` (
    `id`             BIGINT PRIMARY KEY AUTO_INCREMENT,
    `meeting_id`     BIGINT NOT NULL,
    `fee_type`       VARCHAR(50) COMMENT 'PROMOTION/DATA_RIGHTS',
    `amount`         DECIMAL(10,2) COMMENT '金额',
    `pay_status`     VARCHAR(20) COMMENT 'PAID/UNPAID',
    `invoice_status` VARCHAR(20) DEFAULT 'NONE' COMMENT 'NONE/APPLIED/ISSUED',
    `created_at`     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_meeting_id` (`meeting_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 推广配置表
CREATE TABLE IF NOT EXISTS `t_promotion_config` (
    `id`                    BIGINT PRIMARY KEY AUTO_INCREMENT,
    `meeting_id`            BIGINT NOT NULL,
    `user_intents`          JSON COMMENT '用户意图多选列表',
    `behavior_period`       VARCHAR(10) COMMENT '7d/15d/1m/2m/3m',
    `target_behaviors`      JSON COMMENT '["SEARCH","CREATE"]',
    `target_regions`        JSON COMMENT '目标地域城市ID列表（精确到市级）',
    `target_industries`     JSON COMMENT '目标行业枚举列表',
    `channels`              JSON COMMENT '["SMS","EMAIL","PRIVATE_MSG","PUSH"]',
    `pay_mode`              VARCHAR(20) COMMENT 'CPM/CPC/CPA',
    `estimated_reach`       BIGINT COMMENT '预计覆盖人数',
    `estimated_impressions` BIGINT COMMENT '预计曝光',
    `estimated_clicks`      BIGINT COMMENT '预计点击',
    `base_price`            DECIMAL(10,2) COMMENT '原价',
    `order_status`          VARCHAR(20) COMMENT 'PENDING/PAID',
    `order_created_at`      DATETIME COMMENT '订单生成时间（85折倒计时起点）',
    `created_at`            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_meeting_id` (`meeting_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 推广效果统计表（用于简报数据聚合）
CREATE TABLE IF NOT EXISTS `t_promotion_stats` (
    `id`            BIGINT PRIMARY KEY AUTO_INCREMENT,
    `meeting_id`    BIGINT NOT NULL,
    `stat_date`     DATE,
    `impressions`   INT DEFAULT 0,
    `clicks`        INT DEFAULT 0,
    `registrations` INT DEFAULT 0,
    `created_at`    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_meeting_date` (`meeting_id`, `stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
