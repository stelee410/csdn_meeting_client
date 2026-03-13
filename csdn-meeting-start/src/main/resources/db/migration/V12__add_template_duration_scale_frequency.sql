-- issue001-2: 活动模板增加会议时长、会议规模、举办频率
ALTER TABLE t_meeting_template
    ADD COLUMN meeting_duration VARCHAR(50) COMMENT '会议时长：half_day/one_day/two_days/three_days/more' AFTER target_audience,
    ADD COLUMN meeting_scale VARCHAR(50) COMMENT '会议规模：small/medium/large/xlarge' AFTER meeting_duration,
    ADD COLUMN frequency VARCHAR(50) COMMENT '举办频率：once/series/annual/irregular' AFTER meeting_scale;
