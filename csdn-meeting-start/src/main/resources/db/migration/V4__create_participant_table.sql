-- 会议参与者表（实时会议参会人）
CREATE TABLE IF NOT EXISTS `t_participant` (
    `id`         BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id`    BIGINT NOT NULL,
    `user_name`  VARCHAR(100),
    `meeting_id` VARCHAR(50) NOT NULL COMMENT '会议ID（可为实时会议roomId）',
    `role`       VARCHAR(20) COMMENT 'HOST/CO_HOST/ATTENDEE',
    `status`     VARCHAR(20) COMMENT 'INVITED/JOINED/LEFT/REJECTED',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_meeting_id` (`meeting_id`),
    INDEX `idx_user_meeting` (`user_id`, `meeting_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
