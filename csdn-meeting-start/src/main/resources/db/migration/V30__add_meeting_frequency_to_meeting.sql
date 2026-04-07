-- PC 创建会议：举办频率需落库以便编辑页回填（字典编码如 RECURRING / ONCE，与 t_dictionary meeting_frequency 一致）
ALTER TABLE t_meeting
    ADD COLUMN meeting_frequency VARCHAR(64) NULL COMMENT '举办频率字典 item_code' AFTER max_participants;
