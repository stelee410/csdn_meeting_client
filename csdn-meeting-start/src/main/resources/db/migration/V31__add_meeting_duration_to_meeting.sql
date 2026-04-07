-- 会议时长字典编码（half_day/one_day/two_days/three_days/more），与发起会议表单一致，供详情展示直接读取
ALTER TABLE t_meeting
    ADD COLUMN meeting_duration VARCHAR(64) NULL COMMENT '会议时长字典 item_code' AFTER meeting_frequency;
