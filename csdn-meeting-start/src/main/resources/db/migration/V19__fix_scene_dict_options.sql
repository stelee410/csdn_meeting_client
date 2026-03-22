-- V19: 修正场景字典数据
-- 1. 修正 scene_industry 中 OPENSOURE 拼写错误
-- 2. 涉及区域只保留文档指定的 9 个城市

UPDATE t_dictionary SET item_code = 'OPENSOURCE' WHERE dict_type = 'scene_industry' AND item_code = 'OPENSOURE';

DELETE FROM t_dictionary
WHERE dict_type = 'scene_marketing_region'
  AND item_code IN ('CHONGQING', 'TIANJIN', 'QINGDAO', 'SUZHOU', 'CHANGSHA', 'ZHENGZHOU');
