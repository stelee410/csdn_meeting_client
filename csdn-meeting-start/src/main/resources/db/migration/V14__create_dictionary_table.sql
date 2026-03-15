-- V14: 创建字典表并写入种子数据
-- dict_type: organizer / meeting_duration / meeting_scale / meeting_frequency / target_audience / developer_type

CREATE TABLE IF NOT EXISTS t_dictionary (
    id          BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    dict_type   VARCHAR(64)     NOT NULL                COMMENT '字典分类编码',
    item_code   VARCHAR(64)     NOT NULL                COMMENT '选项值（code）',
    item_label  VARCHAR(128)    NOT NULL                COMMENT '显示名称',
    sort_order  INT             NOT NULL DEFAULT 0      COMMENT '排序权重（升序）',
    is_active   TINYINT(1)      NOT NULL DEFAULT 1      COMMENT '是否启用：1-启用 0-禁用',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_dict_type_code (dict_type, item_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据字典表';

-- ===================== 主办方 / 公司 =====================
INSERT INTO t_dictionary (dict_type, item_code, item_label, sort_order) VALUES
('organizer', 'csdn',            'CSDN',              1),
('organizer', 'alibaba',         '阿里巴巴',           2),
('organizer', 'tencent',         '腾讯',               3),
('organizer', 'baidu',           '百度',               4),
('organizer', 'bytedance',       '字节跳动',           5),
('organizer', 'huawei',          '华为',               6),
('organizer', 'meituan',         '美团',               7),
('organizer', 'jd',              '京东',               8),
('organizer', 'xiaomi',          '小米',               9),
('organizer', 'netease',         '网易',              10),
('organizer', 'microsoft_cn',    '微软（中国）',       11),
('organizer', 'amazon_cn',       '亚马逊云科技',       12),
('organizer', 'ibm_cn',          'IBM（中国）',        13),
('organizer', 'sap_cn',          'SAP（中国）',        14),
('organizer', 'oracle_cn',       '甲骨文（中国）',     15),
('organizer', 'linux_foundation', 'Linux Foundation',  16),
('organizer', 'apache',          'Apache 软件基金会',  17),
('organizer', 'infoq',           'InfoQ',             18),
('organizer', 'segmentfault',    'SegmentFault 思否',  19),
('organizer', 'oschina',         'OSChina 开源中国',   20);

-- ===================== 会议时长 =====================
INSERT INTO t_dictionary (dict_type, item_code, item_label, sort_order) VALUES
('meeting_duration', 'half_day',   '半天',     1),
('meeting_duration', 'one_day',    '1天',      2),
('meeting_duration', 'two_days',   '2天',      3),
('meeting_duration', 'three_days', '3天',      4),
('meeting_duration', 'more',       '3天以上',  5);

-- ===================== 会议规模 =====================
INSERT INTO t_dictionary (dict_type, item_code, item_label, sort_order) VALUES
('meeting_scale', 'small',  '50人以下',   1),
('meeting_scale', 'medium', '50-200人',   2),
('meeting_scale', 'large',  '200-500人',  3),
('meeting_scale', 'xlarge', '500人以上',  4);

-- ===================== 举办频率 =====================
INSERT INTO t_dictionary (dict_type, item_code, item_label, sort_order) VALUES
('meeting_frequency', 'once',       '一次性',   1),
('meeting_frequency', 'series',     '系列活动', 2),
('meeting_frequency', 'annual',     '每年一届', 3),
('meeting_frequency', 'irregular',  '不定期',   4);

-- ===================== 目标人群 =====================
INSERT INTO t_dictionary (dict_type, item_code, item_label, sort_order) VALUES
('target_audience', 'developer',       '开发者',     1),
('target_audience', 'architect',       '架构师',     2),
('target_audience', 'product_manager', '产品经理',   3),
('target_audience', 'cto',             '技术管理者', 4),
('target_audience', 'student',         '学生',       5),
('target_audience', 'general',         '泛技术人群', 6);

-- ===================== 开发者类型 =====================
INSERT INTO t_dictionary (dict_type, item_code, item_label, sort_order) VALUES
('developer_type', 'frontend',  '前端开发',      1),
('developer_type', 'backend',   '后端开发',      2),
('developer_type', 'fullstack', '全栈开发',      3),
('developer_type', 'mobile',    '移动端开发',    4),
('developer_type', 'devops',    '运维/DevOps',   5),
('developer_type', 'data',      '大数据/AI',     6),
('developer_type', 'embedded',  '嵌入式/物联网', 7),
('developer_type', 'game',      '游戏开发',      8),
('developer_type', 'other',     '其他',          9);
