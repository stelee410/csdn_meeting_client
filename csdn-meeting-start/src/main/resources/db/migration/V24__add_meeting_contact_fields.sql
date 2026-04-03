-- 会议联系人信息持久化（与前端创建/编辑表单对齐）
ALTER TABLE t_meeting
    ADD COLUMN creator_name VARCHAR(100) NULL COMMENT '联系人/展示姓名' AFTER creator_id,
    ADD COLUMN contact_phone VARCHAR(50) NULL COMMENT '联系人电话' AFTER creator_name,
    ADD COLUMN contact_department VARCHAR(200) NULL COMMENT '联系人部门' AFTER contact_phone,
    ADD COLUMN contact_position VARCHAR(100) NULL COMMENT '联系人职位' AFTER contact_department;
