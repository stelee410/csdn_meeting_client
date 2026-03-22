-- V17: 更新举办频率字典，改为「定期举办」和「单次举办」两个选项
DELETE FROM t_dictionary WHERE dict_type = 'meeting_frequency';

INSERT INTO t_dictionary (dict_type, item_code, item_label, sort_order) VALUES
('meeting_frequency', 'RECURRING', '定期举办', 1),
('meeting_frequency', 'ONCE',      '单次举办', 2);
