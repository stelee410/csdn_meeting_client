-- CSDN会议列表与检索功能数据库初始化脚本
-- 版本: V1.0
-- 创建时间: 2026-03-06
-- 兼容: MySQL 5.7，仅包含建表与初始化数据

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
    key idx_is_deleted (is_deleted)
) engine=InnoDB default charset=utf8mb4 comment '标签表';

-- ==============================================
-- 2. 创建会议表 t_meeting
-- ==============================================
create table if not exists t_meeting (
    id bigint primary key auto_increment comment '主键ID',
    meeting_id varchar(20) not null comment '会议业务ID',
    title varchar(200) not null comment '会议标题',
    description text comment '会议描述',
    poster_url varchar(500) comment '海报地址',
    start_time datetime comment '开始时间',
    end_time datetime comment '结束时间',
    status tinyint not null default 0 comment '状态(0:草稿 1:报名中 2:已结束 3:已取消)',
    `format` tinyint not null default 1 comment '会议形式(1:线上 2:线下 3:混合)',
    meeting_type tinyint comment '会议类型(1:技术峰会 2:技术沙龙 3:技术研讨会)',
    scene tinyint comment '会议场景(1:开发者会议 2:产业会议 3:产品发布 4:区域营销 5:高校会议)',
    city_code varchar(20) comment '城市编码',
    city_name varchar(50) comment '城市名称',
    venue varchar(200) comment '场馆地址',
    hot_score int default 0 comment '热度分数',
    current_participants int default 0 comment '当前报名人数',
    max_participants int default 0 comment '最大报名人数',
    organizer_id bigint comment '主办方ID',
    organizer_name varchar(100) comment '主办方名称',
    organizer_avatar varchar(500) comment '主办方头像URL',
    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    create_by bigint comment '创建人ID',
    update_by bigint comment '更新人ID',
    is_deleted tinyint not null default 0 comment '软删除标志(0:未删除 1:已删除)',
    unique key uk_meeting_id (meeting_id),
    key idx_format (`format`),
    key idx_meeting_type (meeting_type),
    key idx_scene (scene),
    key idx_city_code (city_code),
    key idx_start_time (start_time),
    key idx_status (`status`),
    key idx_is_deleted (is_deleted),
    key idx_hot_score (hot_score)
) engine=InnoDB default charset=utf8mb4 comment '会议表';

-- ==============================================
-- 3. 创建会议标签关联表 t_meeting_tag
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
    key idx_meeting_id (meeting_id),
    key idx_tag_id (tag_id),
    key idx_is_deleted (is_deleted)
) engine=InnoDB default charset=utf8mb4 comment '会议标签关联表';

-- ==============================================
-- 4. 创建用户标签订阅表 t_user_tag_subscribe
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
    key idx_user_id (user_id),
    key idx_tag_id (tag_id),
    key idx_is_deleted (is_deleted)
) engine=InnoDB default charset=utf8mb4 comment '用户标签订阅表';

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

-- 会议类型标签
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
