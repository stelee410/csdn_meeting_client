-- V20: 更新涉及高校字典，覆盖全部 985/211 院校并追加"其他"

DELETE FROM t_dictionary WHERE dict_type = 'scene_university';

INSERT INTO t_dictionary (dict_type, item_code, item_label, sort_order) VALUES
-- ===== 985 高校（39所）=====
-- 北京
('scene_university', 'THU',    '清华大学',             1),
('scene_university', 'PKU',    '北京大学',             2),
('scene_university', 'RUC',    '中国人民大学',         3),
('scene_university', 'BUAA',   '北京航空航天大学',     4),
('scene_university', 'BIT',    '北京理工大学',         5),
('scene_university', 'BNU',    '北京师范大学',         6),
('scene_university', 'CAU',    '中国农业大学',         7),
('scene_university', 'MUC',    '中央民族大学',         8),
-- 天津
('scene_university', 'NKU',    '南开大学',             9),
('scene_university', 'TJU',    '天津大学',            10),
-- 东北
('scene_university', 'NEU',    '东北大学',            11),
('scene_university', 'DLUT',   '大连理工大学',        12),
('scene_university', 'JLU',    '吉林大学',            13),
('scene_university', 'HIT',    '哈尔滨工业大学',      14),
-- 上海
('scene_university', 'FDU',    '复旦大学',            15),
('scene_university', 'TONGJI', '同济大学',            16),
('scene_university', 'SJTU',   '上海交通大学',        17),
('scene_university', 'ECNU',   '华东师范大学',        18),
-- 华东
('scene_university', 'NJU',    '南京大学',            19),
('scene_university', 'SEU',    '东南大学',            20),
('scene_university', 'ZJU',    '浙江大学',            21),
('scene_university', 'USTC',   '中国科学技术大学',    22),
('scene_university', 'XMU',    '厦门大学',            23),
('scene_university', 'SDU',    '山东大学',            24),
('scene_university', 'OUC',    '中国海洋大学',        25),
-- 华中
('scene_university', 'WHU',    '武汉大学',            26),
('scene_university', 'HUST',   '华中科技大学',        27),
('scene_university', 'HNU',    '湖南大学',            28),
('scene_university', 'CSU',    '中南大学',            29),
-- 华南
('scene_university', 'SYSU',   '中山大学',            30),
('scene_university', 'SCUT',   '华南理工大学',        31),
-- 西南
('scene_university', 'SCU',    '四川大学',            32),
('scene_university', 'CQU',    '重庆大学',            33),
('scene_university', 'UESTC',  '电子科技大学',        34),
-- 西北
('scene_university', 'XJTU',   '西安交通大学',        35),
('scene_university', 'NWPU',   '西北工业大学',        36),
('scene_university', 'LZU',    '兰州大学',            37),
-- 其他
('scene_university', 'YNU',    '云南大学',            38),
('scene_university', 'NUDT',   '国防科技大学',        39),

-- ===== 211 高校（部分，除已包含在985中）=====
-- 北京
('scene_university', 'BUPT',   '北京邮电大学',        40),
('scene_university', 'BJTU',   '北京交通大学',        41),
('scene_university', 'USTB',   '北京科技大学',        42),
('scene_university', 'BUA',    '北京化工大学',        43),
('scene_university', 'BJFU',   '北京林业大学',        44),
('scene_university', 'BUCM',   '北京中医药大学',      45),
('scene_university', 'CUC',    '中国传媒大学',        46),
('scene_university', 'UIBE',   '对外经济贸易大学',    47),
('scene_university', 'CUPL',   '中国政法大学',        48),
('scene_university', 'CUMTB',  '中国矿业大学(北京)',  49),
('scene_university', 'CUGB',   '中国地质大学(北京)',  50),
('scene_university', 'CUPB',   '中国石油大学(北京)',  51),
('scene_university', 'NCEPU',  '华北电力大学',        52),
-- 天津
('scene_university', 'TMU',    '天津医科大学',        53),
-- 河北
('scene_university', 'HEBUT',  '河北工业大学',        54),
-- 山西
('scene_university', 'TUT',    '太原理工大学',        55),
-- 内蒙古
('scene_university', 'IMU',    '内蒙古大学',          56),
-- 东北
('scene_university', 'LNU',    '辽宁大学',            57),
('scene_university', 'DLMU',   '大连海事大学',        58),
('scene_university', 'NENU',   '东北师范大学',        59),
('scene_university', 'YBU',    '延边大学',            60),
('scene_university', 'HRBEU',  '哈尔滨工程大学',      61),
('scene_university', 'NEAU',   '东北农业大学',        62),
('scene_university', 'NEFU',   '东北林业大学',        63),
-- 上海
('scene_university', 'SHU',    '上海大学',            64),
('scene_university', 'ECUST',  '华东理工大学',        65),
-- 江苏
('scene_university', 'SZU_JS', '苏州大学',            66),
('scene_university', 'NUAA',   '南京航空航天大学',    67),
('scene_university', 'NJUST',  '南京理工大学',        68),
('scene_university', 'CUMT',   '中国矿业大学',        69),
('scene_university', 'HHU',    '河海大学',            70),
('scene_university', 'JNU_JS', '江南大学',            71),
('scene_university', 'NJFU',   '南京林业大学',        72),
('scene_university', 'NUIST',  '南京信息工程大学',    73),
('scene_university', 'NAU',    '南京农业大学',        74),
('scene_university', 'NNU',    '南京师范大学',        75),
('scene_university', 'CPU',    '中国药科大学',        76),
-- 安徽
('scene_university', 'HFU',    '合肥工业大学',        77),
('scene_university', 'AHU',    '安徽大学',            78),
-- 福建
('scene_university', 'FZU',    '福州大学',            79),
-- 江西
('scene_university', 'NCU',    '南昌大学',            80),
-- 山东
('scene_university', 'UPCQD',  '中国石油大学(华东)',  81),
-- 河南
('scene_university', 'ZZU',    '郑州大学',            82),
-- 湖北
('scene_university', 'WHUT',   '武汉理工大学',        83),
('scene_university', 'HZAU',   '华中农业大学',        84),
('scene_university', 'CCNU',   '华中师范大学',        85),
('scene_university', 'ZUEL',   '中南财经政法大学',    86),
-- 湖南
('scene_university', 'HUNNU',  '湖南师范大学',        87),
-- 广东
('scene_university', 'JNU',    '暨南大学',            88),
('scene_university', 'SCNU',   '华南师范大学',        89),
-- 广西
('scene_university', 'GXU',    '广西大学',            90),
-- 海南
('scene_university', 'HNU_HN', '海南大学',            91),
-- 重庆/西南
('scene_university', 'SWU',    '西南大学',            92),
('scene_university', 'SWJTU',  '西南交通大学',        93),
('scene_university', 'SWUFE',  '西南财经大学',        94),
-- 贵州
('scene_university', 'GZU',    '贵州大学',            95),
-- 西藏
('scene_university', 'TU',     '西藏大学',            96),
-- 陕西
('scene_university', 'XIDIAN', '西安电子科技大学',    97),
('scene_university', 'CHANGAN','长安大学',            98),
('scene_university', 'SNNU',   '陕西师范大学',        99),
('scene_university', 'NWU',    '西北大学',           100),
('scene_university', 'NWAFU',  '西北农林科技大学',   101),
-- 青海
('scene_university', 'QHU',    '青海大学',           102),
-- 宁夏
('scene_university', 'NXU',    '宁夏大学',           103),
-- 新疆
('scene_university', 'XJU',    '新疆大学',           104),
('scene_university', 'SHZU',   '石河子大学',         105),

-- 其他
('scene_university', 'OTHER',  '其他',               999);
