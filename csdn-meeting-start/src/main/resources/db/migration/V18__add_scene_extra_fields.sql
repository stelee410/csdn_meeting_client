-- V18: 会议场景扩展字段 + 字典数据
-- 根据会议场景动态展示: 所属产业/涉及产品/涉及区域/涉及高校

ALTER TABLE t_meeting
    ADD COLUMN scene_industry         VARCHAR(100)  NULL COMMENT '所属产业（场景=产业会议）',
    ADD COLUMN scene_product          VARCHAR(500)  NULL COMMENT '涉及产品（场景=产品发布会议）',
    ADD COLUMN scene_marketing_regions VARCHAR(500) NULL COMMENT '涉及区域，逗号分隔（场景=区域营销会议）',
    ADD COLUMN scene_universities     VARCHAR(500)  NULL COMMENT '涉及高校，逗号分隔（场景=高校会议）';

-- ===================== 所属产业 =====================
INSERT INTO t_dictionary (dict_type, item_code, item_label, sort_order) VALUES
('scene_industry', 'AI',       'AI人工智能', 1),
('scene_industry', 'CLOUD',    '云计算',     2),
('scene_industry', 'OPENSOURE','开源',       3),
('scene_industry', 'OVERSEAS', '出海',       4),
('scene_industry', 'HARMONYOS','鸿蒙',       5),
('scene_industry', 'GAME',     '游戏',       6),
('scene_industry', 'FINANCE',  '金融',       7);

-- ===================== 区域营销-涉及区域 =====================
INSERT INTO t_dictionary (dict_type, item_code, item_label, sort_order) VALUES
('scene_marketing_region', 'BEIJING',   '北京',  1),
('scene_marketing_region', 'SHANGHAI',  '上海',  2),
('scene_marketing_region', 'SHENZHEN',  '深圳',  3),
('scene_marketing_region', 'GUANGZHOU', '广州',  4),
('scene_marketing_region', 'WUHAN',     '武汉',  5),
('scene_marketing_region', 'HANGZHOU',  '杭州',  6),
('scene_marketing_region', 'CHENGDU',   '成都',  7),
('scene_marketing_region', 'XIAN',      '西安',  8),
('scene_marketing_region', 'NANJING',   '南京',  9),
('scene_marketing_region', 'CHONGQING', '重庆', 10),
('scene_marketing_region', 'TIANJIN',   '天津', 11),
('scene_marketing_region', 'QINGDAO',   '青岛', 12),
('scene_marketing_region', 'SUZHOU',    '苏州', 13),
('scene_marketing_region', 'CHANGSHA',  '长沙', 14),
('scene_marketing_region', 'ZHENGZHOU', '郑州', 15);

-- ===================== 涉及高校 =====================
INSERT INTO t_dictionary (dict_type, item_code, item_label, sort_order) VALUES
('scene_university', 'THU',    '清华大学',       1),
('scene_university', 'PKU',    '北京大学',       2),
('scene_university', 'ZJU',    '浙江大学',       3),
('scene_university', 'SJTU',   '上海交通大学',   4),
('scene_university', 'FDU',    '复旦大学',       5),
('scene_university', 'NJU',    '南京大学',       6),
('scene_university', 'WHU',    '武汉大学',       7),
('scene_university', 'USTC',   '中国科学技术大学', 8),
('scene_university', 'HIT',    '哈尔滨工业大学', 9),
('scene_university', 'TONGJI', '同济大学',      10),
('scene_university', 'HUST',   '华中科技大学',  11),
('scene_university', 'XJTU',   '西安交通大学',  12),
('scene_university', 'RUC',    '中国人民大学',  13),
('scene_university', 'BUAA',   '北京航空航天大学', 14),
('scene_university', 'BIT',    '北京理工大学',  15),
('scene_university', 'SEU',    '东南大学',      16),
('scene_university', 'NKU',    '南开大学',      17),
('scene_university', 'UESTC',  '电子科技大学',  18),
('scene_university', 'TJU',    '天津大学',      19),
('scene_university', 'SYSU',   '中山大学',      20),
('scene_university', 'SCU',    '四川大学',      21),
('scene_university', 'XMU',    '厦门大学',      22),
('scene_university', 'SDU',    '山东大学',      23),
('scene_university', 'JLU',    '吉林大学',      24),
('scene_university', 'DLUT',   '大连理工大学',  25),
('scene_university', 'CSU',    '中南大学',      26),
('scene_university', 'BJTU',   '北京交通大学',  27),
('scene_university', 'BUPT',   '北京邮电大学',  28),
('scene_university', 'ECNU',   '华东师范大学',  29),
('scene_university', 'NWPU',   '西北工业大学',  30);
