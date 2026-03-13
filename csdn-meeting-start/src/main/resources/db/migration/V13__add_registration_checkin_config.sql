-- ============================================
-- V12: 会议报名签到功能数据库变更
-- 创建时间: 2026-03-12
-- 功能: 支持报名表单配置、签到功能、报名截止时间
-- ============================================

-- 1. 扩展报名记录表 - 增加签到相关字段
ALTER TABLE t_registration
    ADD COLUMN checkin_time DATETIME NULL COMMENT '签到时间' AFTER audit_remark,
    ADD COLUMN cancel_time DATETIME NULL COMMENT '取消报名时间' AFTER checkin_time,
    MODIFY COLUMN status VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED/CANCELLED/CHECKED_IN';

-- 2. 扩展会议表 - 增加报名和签到控制字段
ALTER TABLE t_meeting
    ADD COLUMN reg_end_time DATETIME NULL COMMENT '报名截止时间' AFTER end_time,
    ADD COLUMN checkin_code VARCHAR(64) NULL COMMENT '签到码（用于生成二维码）' AFTER reg_end_time,
    ADD COLUMN require_checkin BOOLEAN DEFAULT FALSE COMMENT '是否启用签到' AFTER checkin_code;

-- 3. 创建报名表单配置表（运营人员配置）
CREATE TABLE IF NOT EXISTS t_registration_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    meeting_id VARCHAR(50) NULL COMMENT '会议ID（NULL表示默认配置）',
    config_type VARCHAR(20) DEFAULT 'DEFAULT' COMMENT '配置类型：DEFAULT默认/CUSTOM自定义',
    field_name VARCHAR(50) NOT NULL COMMENT '字段标识：name/phone/email/company/position/purpose',
    field_label VARCHAR(100) NOT NULL COMMENT '字段显示名称',
    field_type VARCHAR(20) DEFAULT 'TEXT' COMMENT '字段类型：TEXT/PHONE/EMAIL/SELECT/TEXTAREA',
    required BOOLEAN DEFAULT FALSE COMMENT '是否必填',
    editable BOOLEAN DEFAULT TRUE COMMENT '是否允许用户编辑',
    source VARCHAR(20) DEFAULT 'none' COMMENT '数据来源：user_profile用户画像/none无预填',
    placeholder VARCHAR(200) COMMENT '输入提示',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    options JSON NULL COMMENT '选项列表（SELECT类型使用）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_meeting_id (meeting_id),
    INDEX idx_field_name (field_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报名表单配置表';

-- 4. 插入默认表单配置（全局默认）
INSERT INTO t_registration_config (meeting_id, config_type, field_name, field_label, field_type, required, editable, source, placeholder, sort_order, enabled) VALUES
(NULL, 'DEFAULT', 'name', '姓名', 'TEXT', TRUE, TRUE, 'user_profile', '请输入您的真实姓名', 1, TRUE),
(NULL, 'DEFAULT', 'phone', '手机号', 'PHONE', TRUE, TRUE, 'user_profile', '用于接收会议通知短信', 2, TRUE),
(NULL, 'DEFAULT', 'email', '邮箱', 'EMAIL', FALSE, TRUE, 'user_profile', '用于接收会议详情邮件', 3, TRUE),
(NULL, 'DEFAULT', 'company', '公司', 'TEXT', FALSE, TRUE, 'none', '请输入您所在的公司', 4, TRUE),
(NULL, 'DEFAULT', 'position', '职位', 'TEXT', FALSE, TRUE, 'none', '例如：前端工程师、产品经理等', 5, TRUE),
(NULL, 'DEFAULT', 'industry', '所属行业', 'SELECT', FALSE, TRUE, 'none', '请选择行业', 6, TRUE),
(NULL, 'DEFAULT', 'purpose', '参会目的', 'TEXTAREA', FALSE, TRUE, 'none', '简单描述您参加本次会议的目的或期望收获（选填）', 7, TRUE);

-- 5. 创建行业选项表（用于SELECT类型字段）
CREATE TABLE IF NOT EXISTS t_industry_option (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    option_value VARCHAR(50) NOT NULL COMMENT '选项值',
    option_label VARCHAR(100) NOT NULL COMMENT '选项显示名称',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_option_value (option_value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行业选项表';

-- 插入行业选项数据
INSERT INTO t_industry_option (option_value, option_label, sort_order) VALUES
('internet', '互联网/IT', 1),
('finance', '金融/银行/保险', 2),
('manufacturing', '制造业', 3),
('education', '教育/培训', 4),
('healthcare', '医疗/健康', 5),
('retail', '零售/电商', 6),
('real_estate', '房地产/建筑', 7),
('media', '传媒/广告', 8),
('energy', '能源/环保', 9),
('transport', '交通/物流', 10),
('government', '政府/公共事业', 11),
('other', '其他', 12);

-- 6. 创建签到记录表（用于高并发签到场景，可独立部署）
CREATE TABLE IF NOT EXISTS t_checkin_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    meeting_id VARCHAR(50) NOT NULL COMMENT '会议ID',
    registration_id BIGINT NOT NULL COMMENT '报名记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    checkin_time DATETIME NOT NULL COMMENT '签到时间',
    checkin_method VARCHAR(20) DEFAULT 'QR_CODE' COMMENT '签到方式：QR_CODE扫码/MANUAL手动',
    device_info VARCHAR(200) COMMENT '设备信息',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_registration_checkin (registration_id),
    INDEX idx_meeting_id (meeting_id),
    INDEX idx_user_id (user_id),
    INDEX idx_checkin_time (checkin_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到记录表';

-- 7. 为报名记录表添加索引优化
CREATE INDEX idx_user_status ON t_registration(user_id, status);
CREATE INDEX idx_registered_at ON t_registration(registered_at);
