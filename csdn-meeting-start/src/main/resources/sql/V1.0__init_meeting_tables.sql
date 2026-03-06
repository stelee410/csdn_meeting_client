-- CSDN会议列表与检索功能数据库初始化脚本
-- 版本: V1.0
-- 创建时间: 2026-03-06

-- ==============================================
-- 1. 创建标签表 t_tag
-- ==============================================
create table if not exists t_tag (
    id bigint primary key auto_increment comment '主键ID',
    tag_name varchar(50) not null comment '标签名称',
    tag_category varchar(20) comment '标签分类(tech/scene/topic/brand)',
    create_time datetime not null default current_timestamp comment '创建时间',
    create_by bigint comment '创建人ID',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    update_by bigint comment '更新人ID',
    is_deleted tinyint not null default 0 comment '软删除标志(0:未删除 1:已删除)',
    unique key uk_tag_name (tag_name),
    index idx_is_deleted (is_deleted)
) comment='标签表';

-- ==============================================
-- 2. 创建会议标签关联表 t_meeting_tag
-- ==============================================
create table if not exists t_meeting_tag (
    id bigint primary key auto_increment comment '主键ID',
    meeting_id varchar(20) not null comment '会议ID',
    tag_id bigint not null comment '标签ID',
    create_time datetime not null default current_timestamp comment '创建时间',
    create_by bigint comment '创建人ID',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    update_by bigint comment '更新人ID',
    is_deleted tinyint not null default 0 comment '软删除标志(0:未删除 1:已删除)',
    unique key uk_meeting_tag (meeting_id, tag_id),
    index idx_meeting_id (meeting_id),
    index idx_tag_id (tag_id),
    index idx_is_deleted (is_deleted)
) comment='会议标签关联表';

-- ==============================================
-- 3. 创建用户标签订阅表 t_user_tag_subscribe
-- ==============================================
create table if not exists t_user_tag_subscribe (
    id bigint primary key auto_increment comment '主键ID',
    user_id bigint not null comment '用户ID',
    tag_id bigint not null comment '标签ID',
    create_time datetime not null default current_timestamp comment '创建时间',
    create_by bigint comment '创建人ID',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    update_by bigint comment '更新人ID',
    is_deleted tinyint not null default 0 comment '软删除标志(0:未删除 1:已删除)',
    unique key uk_user_tag (user_id, tag_id),
    index idx_user_id (user_id),
    index idx_tag_id (tag_id),
    index idx_is_deleted (is_deleted)
) comment='用户标签订阅表';

-- ==============================================
-- 4. 扩展会议表 t_meeting
-- ==============================================
-- 添加会议列表检索功能所需字段
alter table t_meeting add column if not exists poster_url varchar(500) comment '海报地址' after description;
alter table t_meeting add column if not exists format tinyint not null default 1 comment '会议形式(1:线上 2:线下 3:混合)' after status;
alter table t_meeting add column if not exists meeting_type tinyint comment '会议类型(1:技术峰会 2:技术沙龙 3:技术研讨会)' after format;
alter table t_meeting add column if not exists scene tinyint comment '会议场景(1:开发者会议 2:产业会议 3:产品发布 4:区域营销 5:高校会议)' after meeting_type;
alter table t_meeting add column if not exists city_code varchar(20) comment '城市编码' after scene;
alter table t_meeting add column if not exists city_name varchar(50) comment '城市名称' after city_code;
alter table t_meeting add column if not exists venue varchar(200) comment '场馆地址' after city_name;
alter table t_meeting add column if not exists hot_score int default 0 comment '热度分数' after venue;
alter table t_meeting add column if not exists current_participants int default 0 comment '当前报名人数' after hot_score;
alter table t_meeting add column if not exists organizer_id bigint comment '主办方ID' after current_participants;
alter table t_meeting add column if not exists organizer_name varchar(100) comment '主办方名称' after organizer_id;
alter table t_meeting add column if not exists organizer_avatar varchar(500) comment '主办方头像URL' after organizer_name;

-- 统一基础字段命名（如果不存在则添加）
-- 注意：如果原表已有 created_at/updated_at 字段，需要先修改字段名
-- alter table t_meeting change created_at create_time datetime not null default current_timestamp comment '创建时间';
-- alter table t_meeting change updated_at update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间';
alter table t_meeting add column if not exists create_by bigint comment '创建人ID' after update_time;
alter table t_meeting add column if not exists update_by bigint comment '更新人ID' after create_by;
alter table t_meeting add column if not exists is_deleted tinyint not null default 0 comment '软删除标志(0:未删除 1:已删除)' after update_by;

-- 添加索引
alter table t_meeting add index idx_format (format);
alter table t_meeting add index idx_meeting_type (meeting_type);
alter table t_meeting add index idx_scene (scene);
alter table t_meeting add index idx_city_code (city_code);
alter table t_meeting add index idx_start_time (start_time);
alter table t_meeting add index idx_status (status);
alter table t_meeting add index idx_is_deleted (is_deleted);
alter table t_meeting add index idx_hot_score (hot_score);

-- ==============================================
-- 5. 初始化标签数据
-- ==============================================
-- 技术类标签
insert into t_tag (tag_name, tag_category) values 
('Java', 'tech'),
('Python', 'tech'),
('AI', 'tech'),
('鸿蒙', 'tech'),
('云原生', 'tech'),
('Kubernetes', 'tech'),
('Docker', 'tech'),
('前端', 'tech'),
('后端', 'tech'),
('大数据', 'tech'),
('数据库', 'tech'),
('操作系统', 'tech');

-- 品牌/主办方标签
insert into t_tag (tag_name, tag_category) values 
('华为', 'brand'),
('阿里云', 'brand'),
('腾讯云', 'brand'),
('百度AI', 'brand'),
('字节跳动', 'brand'),
('CSDN', 'brand');

-- 会议类型标签（冗余存储便于筛选）
insert into t_tag (tag_name, tag_category) values 
('技术峰会', 'type'),
('技术沙龙', 'type'),
('技术研讨会', 'type'),
('开发者会议', 'scene'),
('产业会议', 'scene'),
('产品发布会议', 'scene'),
('区域营销会议', 'scene'),
('高校会议', 'scene');

-- 技术领域细分标签
insert into t_tag (tag_name, tag_category) values 
('人工智能', 'topic'),
('大模型', 'topic'),
('机器学习', 'topic'),
('深度学习', 'topic'),
('物联网', 'topic'),
('边缘计算', 'topic'),
('开源', 'topic'),
('游戏开发', 'topic'),
('出海', 'topic'),
('教育', 'topic');
