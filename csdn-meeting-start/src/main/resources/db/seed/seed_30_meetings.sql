-- ============================================
-- 30 条多类型会议 seed 数据（开发/测试环境）
-- 分布：3 条待审核（status=1），27 条已发布（status=2）
-- 类型覆盖：峰会/沙龙/研讨会 × 线上/线下/混合 × 多种技术场景
-- 清理：DELETE FROM t_meeting WHERE meeting_id LIKE 'MT999%';
--        DELETE FROM t_meeting_tag WHERE meeting_id LIKE 'MT999%';
-- ============================================

-- ============================================
-- 1. 确保标签数据存在（20 个技术标签）
-- ============================================
INSERT INTO t_tag (id, tag_name, tag_category, is_system, sort_order, is_deleted)
VALUES
    (101, 'AI大模型',    'TECH', TRUE, 100, 0),
    (102, '云原生',      'TECH', TRUE,  99, 0),
    (103, '大数据',      'TECH', TRUE,  98, 0),
    (104, '前端开发',    'TECH', TRUE,  97, 0),
    (105, '后端架构',    'TECH', TRUE,  96, 0),
    (106, '移动开发',    'TECH', TRUE,  95, 0),
    (107, '网络安全',    'TECH', TRUE,  94, 0),
    (108, 'DevOps',      'TECH', TRUE,  93, 0),
    (109, '区块链',      'TECH', TRUE,  92, 0),
    (110, '物联网',      'TECH', TRUE,  91, 0),
    (111, 'Rust',        'TECH', TRUE,  90, 0),
    (112, 'Go语言',      'TECH', TRUE,  89, 0),
    (113, 'Python',      'TECH', TRUE,  88, 0),
    (114, '数据库',      'TECH', TRUE,  87, 0),
    (115, '微服务',      'TECH', TRUE,  86, 0),
    (116, '低代码',      'TECH', TRUE,  85, 0),
    (117, '开源生态',    'TECH', TRUE,  84, 0),
    (118, '量子计算',    'TECH', TRUE,  83, 0),
    (119, '边缘计算',    'TECH', TRUE,  82, 0),
    (120, 'AR/VR',       'TECH', TRUE,  81, 0)
ON DUPLICATE KEY UPDATE tag_name = VALUES(tag_name), is_deleted = 0;

-- ============================================
-- 2. 插入会议数据
-- status: 1=待审核  2=已发布
-- format: ONLINE/OFFLINE/HYBRID
-- meeting_type: SUMMIT=峰会 / SALON=沙龙 / WORKSHOP=研讨会
-- scene: DEVELOPER/ENTERPRISE/STARTUP/REGIONAL/UNIVERSITY
-- ============================================

-- ======= 待审核会议（3 条，status=1） =======

INSERT INTO t_meeting (
    meeting_id, title, description, creator_id,
    start_time, end_time, status, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, cover_image, tags,
    is_deleted, created_at, updated_at
) VALUES
-- 待审核 #1：AI 峰会（线下，北京）
(
    'MT9990000001',
    '2026 全球人工智能峰会',
    '## 会议介绍\n聚焦 AI 大模型、具身智能与 AI Agent 前沿技术，汇聚全球顶级研究机构与科技企业。\n\n## 主要议题\n- LLM 工程化落地实践\n- 多模态大模型最新进展\n- AI Agent 框架选型与对比',
    '10001',
    NOW() + INTERVAL 15 DAY, NOW() + INTERVAL 16 DAY,
    1, 'OFFLINE', 'SUMMIT', 'DEVELOPER',
    '110100', '北京', '国家会议中心 A 厅', 0, 0, 2000,
    10001, 'CSDN AI 研究院', NULL, '101,113',
    0, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY
),
-- 待审核 #2：前端沙龙（线上）
(
    'MT9990000002',
    '前端工程化与性能优化沙龙',
    '## 会议介绍\n深入探讨现代前端工程体系——从构建工具、模块联邦到运行时性能优化。\n\n## 主要议题\n- Vite/Rollup 最佳实践\n- React Server Components 实战\n- Web Vitals 指标优化案例',
    '10001',
    NOW() + INTERVAL 8 DAY, NOW() + INTERVAL 8 DAY + INTERVAL 4 HOUR,
    1, 'ONLINE', 'SALON', 'DEVELOPER',
    NULL, NULL, '线上直播', 0, 0, 500,
    10001, '前端技术社区', NULL, '104',
    0, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY
),
-- 待审核 #3：安全研讨会（混合，上海）
(
    'MT9990000003',
    '企业网络安全与零信任架构研讨会',
    '## 会议介绍\n面向企业安全负责人、架构师，探讨零信任安全模型在复杂业务场景中的落地路径。\n\n## 主要议题\n- 零信任架构设计原则\n- 供应链安全防护实践\n- 安全左移与 DevSecOps',
    '10001',
    NOW() + INTERVAL 20 DAY, NOW() + INTERVAL 20 DAY + INTERVAL 6 HOUR,
    1, 'HYBRID', 'WORKSHOP', 'ENTERPRISE',
    '310000', '上海', '张江高科技园区国际会议中心', 0, 0, 300,
    10001, '安全技术联盟', NULL, '107,108',
    0, NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY
);

-- ======= 已发布会议（27 条，status=2） =======

INSERT INTO t_meeting (
    meeting_id, title, description, creator_id,
    start_time, end_time, status, publish_time, format, meeting_type, scene,
    city_code, city_name, venue, hot_score, current_participants, max_participants,
    organizer_id, organizer_name, cover_image, tags,
    is_deleted, created_at, updated_at
) VALUES
-- 已发布 #1：云原生峰会（线下，北京）
(
    'MT9990000004',
    'KubeCon China 2026 云原生峰会',
    '## 会议介绍\nKubernetes、Service Mesh、eBPF 等云原生核心技术年度盛会。\n\n## 主要议题\n- Kubernetes 1.32 新特性\n- Cilium/eBPF 网络实践\n- 多云与混合云治理',
    '10001',
    NOW() + INTERVAL 30 DAY, NOW() + INTERVAL 31 DAY,
    2, NOW() - INTERVAL 5 DAY, 'OFFLINE', 'SUMMIT', 'DEVELOPER',
    '110100', '北京', '国家会议中心', 850, 680, 3000,
    10001, 'CNCF & CSDN', NULL, '102,115',
    0, NOW() - INTERVAL 20 DAY, NOW() - INTERVAL 5 DAY
),
-- 已发布 #2：大数据峰会（线下，上海）
(
    'MT9990000005',
    'DataSummit 2026 大数据与数据智能峰会',
    '## 会议介绍\n聚焦数据湖、数据仓库、实时计算与数据治理的行业年度大会。\n\n## 主要议题\n- Apache Flink/Spark 最新进展\n- Lakehouse 架构演进\n- 数据资产管理实践',
    '10001',
    NOW() + INTERVAL 25 DAY, NOW() + INTERVAL 26 DAY,
    2, NOW() - INTERVAL 8 DAY, 'OFFLINE', 'SUMMIT', 'ENTERPRISE',
    '310000', '上海', '世博展览馆', 720, 540, 2500,
    10001, '数据技术社区', NULL, '103,114',
    0, NOW() - INTERVAL 25 DAY, NOW() - INTERVAL 8 DAY
),
-- 已发布 #3：Go 语言沙龙（线上）
(
    'MT9990000006',
    'Gopher China 线上沙龙：Go 1.23 特性解析',
    '## 会议介绍\nGo 语言社区月度线上沙龙，深入解析 Go 1.23 新特性及性能优化技巧。',
    '10001',
    NOW() + INTERVAL 5 DAY, NOW() + INTERVAL 5 DAY + INTERVAL 3 HOUR,
    2, NOW() - INTERVAL 3 DAY, 'ONLINE', 'SALON', 'DEVELOPER',
    NULL, NULL, 'Bilibili 直播间', 420, 380, 1000,
    10001, 'Go 中国社区', NULL, '112',
    0, NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 3 DAY
),
-- 已发布 #4：DevOps 研讨会（线下，深圳）
(
    'MT9990000007',
    'DevOps Days Shenzhen 2026',
    '## 会议介绍\n聚焦持续交付、平台工程与 SRE 实践，来自一线互联网企业的真实案例分享。\n\n## 主要议题\n- 平台工程（Platform Engineering）\n- GitOps 与 ArgoCD 实战\n- 故障管理与可观测性',
    '10001',
    NOW() + INTERVAL 12 DAY, NOW() + INTERVAL 12 DAY + INTERVAL 8 HOUR,
    2, NOW() - INTERVAL 6 DAY, 'OFFLINE', 'WORKSHOP', 'DEVELOPER',
    '440300', '深圳', '深圳湾科技园', 310, 240, 400,
    10001, 'DevOps 中国社区', NULL, '108,102',
    0, NOW() - INTERVAL 15 DAY, NOW() - INTERVAL 6 DAY
),
-- 已发布 #5：Rust 研讨会（混合，成都）
(
    'MT9990000008',
    'RustCon Asia 2026：系统编程新范式',
    '## 会议介绍\n探讨 Rust 在操作系统、嵌入式、WebAssembly 及 AI 推理引擎中的最新实践。',
    '10001',
    NOW() + INTERVAL 18 DAY, NOW() + INTERVAL 19 DAY,
    2, NOW() - INTERVAL 7 DAY, 'HYBRID', 'SUMMIT', 'DEVELOPER',
    '510100', '成都', '天府国际会议中心', 290, 210, 800,
    10001, 'Rust 基金会', NULL, '111',
    0, NOW() - INTERVAL 18 DAY, NOW() - INTERVAL 7 DAY
),
-- 已发布 #6：物联网峰会（线下，杭州）
(
    'MT9990000009',
    '2026 智能物联网产业峰会',
    '## 会议介绍\n聚焦工业物联网、车联网与智慧城市，探讨 IoT 平台架构与边缘智能的融合落地。',
    '10001',
    NOW() + INTERVAL 22 DAY, NOW() + INTERVAL 22 DAY + INTERVAL 7 HOUR,
    2, NOW() - INTERVAL 9 DAY, 'OFFLINE', 'SUMMIT', 'ENTERPRISE',
    '330100', '杭州', '云栖小镇国际会展中心', 380, 290, 1000,
    10001, '阿里云 IoT', NULL, '110,119',
    0, NOW() - INTERVAL 22 DAY, NOW() - INTERVAL 9 DAY
),
-- 已发布 #7：Python 数据科学沙龙（线上）
(
    'MT9990000010',
    'PyData 中国：AI 时代的数据科学工作流',
    '## 会议介绍\n从数据清洗、特征工程到模型部署，探讨 AI 时代 Python 数据科学全链路最佳实践。',
    '10001',
    NOW() + INTERVAL 6 DAY, NOW() + INTERVAL 6 DAY + INTERVAL 4 HOUR,
    2, NOW() - INTERVAL 4 DAY, 'ONLINE', 'SALON', 'DEVELOPER',
    NULL, NULL, '线上直播', 510, 440, 2000,
    10001, 'PyData China', NULL, '113,101',
    0, NOW() - INTERVAL 12 DAY, NOW() - INTERVAL 4 DAY
),
-- 已发布 #8：区块链峰会（线下，北京）
(
    'MT9990000011',
    'Web3 & 区块链技术峰会 2026',
    '## 会议介绍\n聚焦区块链基础设施、DeFi 协议安全与企业级链上数据合规应用。',
    '10001',
    NOW() + INTERVAL 35 DAY, NOW() + INTERVAL 36 DAY,
    2, NOW() - INTERVAL 11 DAY, 'OFFLINE', 'SUMMIT', 'ENTERPRISE',
    '110100', '北京', '财富中心会议室', 180, 120, 500,
    10001, '链节点', NULL, '109,107',
    0, NOW() - INTERVAL 28 DAY, NOW() - INTERVAL 11 DAY
),
-- 已发布 #9：移动开发沙龙（混合，广州）
(
    'MT9990000012',
    'Flutter & 鸿蒙 双生态移动开发沙龙',
    '## 会议介绍\n深入对比 Flutter 与 HarmonyOS NEXT 的开发体验、渲染架构与生态现状。',
    '10001',
    NOW() + INTERVAL 9 DAY, NOW() + INTERVAL 9 DAY + INTERVAL 5 HOUR,
    2, NOW() - INTERVAL 5 DAY, 'HYBRID', 'SALON', 'DEVELOPER',
    '440100', '广州', '广州国际创新城', 340, 280, 600,
    10001, '华为开发者联盟', NULL, '106',
    0, NOW() - INTERVAL 14 DAY, NOW() - INTERVAL 5 DAY
),
-- 已发布 #10：微服务研讨会（线上）
(
    'MT9990000013',
    '微服务治理与 Service Mesh 落地研讨',
    '## 会议介绍\n聚焦大规模微服务架构下的服务治理难题，分享 Istio、Envoy 与 Higress 的落地经验。',
    '10001',
    NOW() + INTERVAL 4 DAY, NOW() + INTERVAL 4 DAY + INTERVAL 3 HOUR,
    2, NOW() - INTERVAL 2 DAY, 'ONLINE', 'WORKSHOP', 'DEVELOPER',
    NULL, NULL, '腾讯会议', 260, 220, 500,
    10001, '蚂蚁集团 MSE', NULL, '115,102',
    0, NOW() - INTERVAL 8 DAY, NOW() - INTERVAL 2 DAY
),
-- 已发布 #11：AR/VR 沙龙（线下，上海）
(
    'MT9990000014',
    'XR 空间计算开发者沙龙',
    '## 会议介绍\n面向 Apple Vision Pro、Meta Quest 开发者，探讨空间计算 UX 设计与 visionOS 开发实践。',
    '10001',
    NOW() + INTERVAL 11 DAY, NOW() + INTERVAL 11 DAY + INTERVAL 5 HOUR,
    2, NOW() - INTERVAL 6 DAY, 'OFFLINE', 'SALON', 'DEVELOPER',
    '310000', '上海', '新国际博览中心', 210, 150, 300,
    10001, '苹果开发者社区', NULL, '120',
    0, NOW() - INTERVAL 16 DAY, NOW() - INTERVAL 6 DAY
),
-- 已发布 #12：低代码峰会（线上）
(
    'MT9990000015',
    '低代码/无代码平台产业峰会',
    '## 会议介绍\n聚焦低代码平台在金融、制造与政务场景的规模化落地，探讨标准化与安全合规挑战。',
    '10001',
    NOW() + INTERVAL 28 DAY, NOW() + INTERVAL 28 DAY + INTERVAL 6 HOUR,
    2, NOW() - INTERVAL 10 DAY, 'ONLINE', 'SUMMIT', 'ENTERPRISE',
    NULL, NULL, '线上直播', 190, 160, 800,
    10001, '低代码联盟', NULL, '116',
    0, NOW() - INTERVAL 20 DAY, NOW() - INTERVAL 10 DAY
),
-- 已发布 #13：开源生态研讨会（混合，武汉）
(
    'MT9990000016',
    'OpenAtom 开源生态研讨会 2026',
    '## 会议介绍\n聚焦基础软件开源进展，探讨开源治理模型、社区运营与商业化路径。',
    '10001',
    NOW() + INTERVAL 16 DAY, NOW() + INTERVAL 16 DAY + INTERVAL 8 HOUR,
    2, NOW() - INTERVAL 7 DAY, 'HYBRID', 'WORKSHOP', 'DEVELOPER',
    '420100', '武汉', '武汉光谷科技会展中心', 170, 130, 500,
    10001, 'OpenAtom 基金会', NULL, '117',
    0, NOW() - INTERVAL 18 DAY, NOW() - INTERVAL 7 DAY
),
-- 已发布 #14：数据库技术峰会（线下，北京）
(
    'MT9990000017',
    'OceanBase & TiDB 分布式数据库技术峰会',
    '## 会议介绍\n聚焦 HTAP 数据库架构、金融级高可用与分布式事务，来自顶级数据库内核团队的深度分享。',
    '10001',
    NOW() + INTERVAL 40 DAY, NOW() + INTERVAL 40 DAY + INTERVAL 8 HOUR,
    2, NOW() - INTERVAL 12 DAY, 'OFFLINE', 'SUMMIT', 'ENTERPRISE',
    '110100', '北京', '中关村软件园', 440, 360, 1500,
    10001, 'PingCAP & OceanBase', NULL, '114,115',
    0, NOW() - INTERVAL 30 DAY, NOW() - INTERVAL 12 DAY
),
-- 已发布 #15：量子计算沙龙（线上）
(
    'MT9990000018',
    '量子计算前沿技术线上沙龙',
    '## 会议介绍\n面向量子算法工程师与物理学研究者，介绍量子纠错、量子机器学习最新研究成果。',
    '10001',
    NOW() + INTERVAL 7 DAY, NOW() + INTERVAL 7 DAY + INTERVAL 3 HOUR,
    2, NOW() - INTERVAL 3 DAY, 'ONLINE', 'SALON', 'DEVELOPER',
    NULL, NULL, '线上直播', 95, 80, 300,
    10001, '中科院量子实验室', NULL, '118',
    0, NOW() - INTERVAL 9 DAY, NOW() - INTERVAL 3 DAY
),
-- 已发布 #16：边缘计算峰会（混合，西安）
(
    'MT9990000019',
    '边缘计算与云边协同峰会',
    '## 会议介绍\n探讨边缘节点部署、云边协同框架与 5G 边缘计算在工业和交通领域的应用实践。',
    '10001',
    NOW() + INTERVAL 14 DAY, NOW() + INTERVAL 14 DAY + INTERVAL 7 HOUR,
    2, NOW() - INTERVAL 8 DAY, 'HYBRID', 'SUMMIT', 'ENTERPRISE',
    '610100', '西安', '西安国际会议中心', 240, 180, 600,
    10001, '中国边缘计算产业联盟', NULL, '119,110',
    0, NOW() - INTERVAL 20 DAY, NOW() - INTERVAL 8 DAY
),
-- 已发布 #17：后端架构沙龙（线下，北京）
(
    'MT9990000020',
    '高并发系统架构设计沙龙',
    '## 会议介绍\n来自字节、美团、滴滴的架构师分享亿级流量系统的设计经验与踩坑实录。',
    '10001',
    NOW() + INTERVAL 3 DAY, NOW() + INTERVAL 3 DAY + INTERVAL 4 HOUR,
    2, NOW() - INTERVAL 1 DAY, 'OFFLINE', 'SALON', 'DEVELOPER',
    '110100', '北京', '北京朝阳 Park 1 号', 680, 580, 800,
    10001, '架构师技术社区', NULL, '105,114',
    0, NOW() - INTERVAL 7 DAY, NOW() - INTERVAL 1 DAY
),
-- 已发布 #18：前端工程峰会（线上）
(
    'MT9990000021',
    'FEDAY 2026 前端技术大会',
    '## 会议介绍\n国内最具影响力的前端年度大会，聚焦框架生态、性能工程与 WebAssembly 前沿。',
    '10001',
    NOW() + INTERVAL 45 DAY, NOW() + INTERVAL 46 DAY,
    2, NOW() - INTERVAL 15 DAY, 'ONLINE', 'SUMMIT', 'DEVELOPER',
    NULL, NULL, '线上直播', 1200, 980, 5000,
    10001, '掘金技术社区', NULL, '104,111',
    0, NOW() - INTERVAL 40 DAY, NOW() - INTERVAL 15 DAY
),
-- 已发布 #19：安全峰会（线下，深圳）
(
    'MT9990000022',
    'GeekCon 极棍安全峰会 2026',
    '## 会议介绍\n涵盖漏洞挖掘、红蓝对抗、AI 安全与供应链安全的综合性信息安全大会。',
    '10001',
    NOW() + INTERVAL 50 DAY, NOW() + INTERVAL 51 DAY,
    2, NOW() - INTERVAL 18 DAY, 'OFFLINE', 'SUMMIT', 'DEVELOPER',
    '440300', '深圳', '深圳国际会展中心（宝安）', 560, 430, 2000,
    10001, '极棍联盟', NULL, '107',
    0, NOW() - INTERVAL 35 DAY, NOW() - INTERVAL 18 DAY
),
-- 已发布 #20：DevOps 沙龙（混合，杭州）
(
    'MT9990000023',
    '效能工程沙龙：研发提效的 10 个实践',
    '## 会议介绍\n聚焦代码审查、测试自动化、CI/CD 流水线优化与 AI 辅助开发的落地故事。',
    '10001',
    NOW() + INTERVAL 13 DAY, NOW() + INTERVAL 13 DAY + INTERVAL 5 HOUR,
    2, NOW() - INTERVAL 4 DAY, 'HYBRID', 'SALON', 'ENTERPRISE',
    '330100', '杭州', '阿里巴巴西溪园区', 320, 260, 500,
    10001, 'CSDN 研发效能团队', NULL, '108,104',
    0, NOW() - INTERVAL 12 DAY, NOW() - INTERVAL 4 DAY
),
-- 已发布 #21：高校 AI 论坛（线下，南京）
(
    'MT9990000024',
    '长三角高校 AI 创新论坛',
    '## 会议介绍\n面向高校师生，分享 AI 领域最新研究成果与产学研合作机会，鼓励创业探索。',
    '10001',
    NOW() + INTERVAL 17 DAY, NOW() + INTERVAL 17 DAY + INTERVAL 8 HOUR,
    2, NOW() - INTERVAL 6 DAY, 'OFFLINE', 'SUMMIT', 'UNIVERSITY',
    '320100', '南京', '南京大学鼓楼校区', 280, 230, 600,
    10001, '南京大学 AI 实验室', NULL, '101,113',
    0, NOW() - INTERVAL 16 DAY, NOW() - INTERVAL 6 DAY
),
-- 已发布 #22：开源数据库沙龙（线上）
(
    'MT9990000025',
    'PostgreSQL 中国用户组年度沙龙',
    '## 会议介绍\nPostgreSQL 内核优化、扩展生态与向量数据库集成的深度分享，适合 DBA 与后端工程师。',
    '10001',
    NOW() + INTERVAL 10 DAY, NOW() + INTERVAL 10 DAY + INTERVAL 4 HOUR,
    2, NOW() - INTERVAL 3 DAY, 'ONLINE', 'SALON', 'DEVELOPER',
    NULL, NULL, '线上会议室', 230, 190, 600,
    10001, 'PG 中国用户组', NULL, '114,117',
    0, NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 3 DAY
),
-- 已发布 #23：区域新能源 + IoT 峰会（线下，重庆）
(
    'MT9990000026',
    '西南数字新能源与智能制造峰会',
    '## 会议介绍\n聚焦新能源汽车、光伏储能与工业 IoT 的数字化转型，探讨数据采集与边缘 AI 应用。',
    '10001',
    NOW() + INTERVAL 33 DAY, NOW() + INTERVAL 33 DAY + INTERVAL 7 HOUR,
    2, NOW() - INTERVAL 13 DAY, 'OFFLINE', 'SUMMIT', 'REGIONAL',
    '500000', '重庆', '重庆国际博览中心', 200, 160, 800,
    10001, '重庆制造业数字化联盟', NULL, '110,119',
    0, NOW() - INTERVAL 25 DAY, NOW() - INTERVAL 13 DAY
),
-- 已发布 #24：云原生沙龙（线上）
(
    'MT9990000027',
    'Serverless & FaaS 架构落地沙龙',
    '## 会议介绍\n来自腾讯云、阿里云、火山引擎的技术专家分享 Serverless 在电商秒杀与 IoT 场景的实战。',
    '10001',
    NOW() + INTERVAL 5 DAY, NOW() + INTERVAL 5 DAY + INTERVAL 3 HOUR,
    2, NOW() - INTERVAL 2 DAY, 'ONLINE', 'SALON', 'DEVELOPER',
    NULL, NULL, '线上直播', 360, 310, 1000,
    10001, '云厂商技术联盟', NULL, '102,108',
    0, NOW() - INTERVAL 8 DAY, NOW() - INTERVAL 2 DAY
),
-- 已发布 #25：数字营销 + AI 研讨（混合，广州）
(
    'MT9990000028',
    'AI 营销技术研讨会：增长黑客 2026',
    '## 会议介绍\n聚焦生成式 AI 在广告创意、用户推荐与内容运营中的规模化落地方案。',
    '10001',
    NOW() + INTERVAL 19 DAY, NOW() + INTERVAL 19 DAY + INTERVAL 6 HOUR,
    2, NOW() - INTERVAL 9 DAY, 'HYBRID', 'WORKSHOP', 'ENTERPRISE',
    '440100', '广州', '广州琶洲国际会展中心', 270, 210, 600,
    10001, '数字营销技术圈', NULL, '101,116',
    0, NOW() - INTERVAL 20 DAY, NOW() - INTERVAL 9 DAY
),
-- 已发布 #26：机器人与 AI 峰会（线下，深圳）
(
    'MT9990000029',
    '具身智能与工业机器人技术峰会',
    '## 会议介绍\n面向机器人工程师与 AI 研究者，探讨强化学习、具身感知与机器人操作系统的最新进展。',
    '10001',
    NOW() + INTERVAL 38 DAY, NOW() + INTERVAL 39 DAY,
    2, NOW() - INTERVAL 16 DAY, 'OFFLINE', 'SUMMIT', 'DEVELOPER',
    '440300', '深圳', '深圳湾体育中心', 490, 380, 1500,
    10001, '深圳机器人协会', NULL, '101,110',
    0, NOW() - INTERVAL 32 DAY, NOW() - INTERVAL 16 DAY
),
-- 已发布 #27：初创企业创投峰会（线下，上海）
(
    'MT9990000030',
    'CSDN 极客创业者峰会 2026',
    '## 会议介绍\n面向技术创业者，提供 BP 路演、投资人对接与 CTO 技术选型咨询，聚焦 AI/IoT 赛道。',
    '10001',
    NOW() + INTERVAL 42 DAY, NOW() + INTERVAL 43 DAY,
    2, NOW() - INTERVAL 14 DAY, 'OFFLINE', 'SUMMIT', 'STARTUP',
    '310000', '上海', '张江人工智能岛', 410, 340, 1000,
    10001, 'CSDN & 真格基金', NULL, '101,102,108',
    0, NOW() - INTERVAL 28 DAY, NOW() - INTERVAL 14 DAY
);

-- ============================================
-- 3. 插入会议-标签关联数据
-- ============================================
INSERT INTO t_meeting_tag (meeting_id, tag_id, is_deleted)
VALUES
    -- 待审核
    ('MT9990000001', 101, 0), ('MT9990000001', 113, 0),
    ('MT9990000002', 104, 0),
    ('MT9990000003', 107, 0), ('MT9990000003', 108, 0),
    -- 已发布
    ('MT9990000004', 102, 0), ('MT9990000004', 115, 0),
    ('MT9990000005', 103, 0), ('MT9990000005', 114, 0),
    ('MT9990000006', 112, 0),
    ('MT9990000007', 108, 0), ('MT9990000007', 102, 0),
    ('MT9990000008', 111, 0),
    ('MT9990000009', 110, 0), ('MT9990000009', 119, 0),
    ('MT9990000010', 113, 0), ('MT9990000010', 101, 0),
    ('MT9990000011', 109, 0), ('MT9990000011', 107, 0),
    ('MT9990000012', 106, 0),
    ('MT9990000013', 115, 0), ('MT9990000013', 102, 0),
    ('MT9990000014', 120, 0),
    ('MT9990000015', 116, 0),
    ('MT9990000016', 117, 0),
    ('MT9990000017', 114, 0), ('MT9990000017', 115, 0),
    ('MT9990000018', 118, 0),
    ('MT9990000019', 119, 0), ('MT9990000019', 110, 0),
    ('MT9990000020', 105, 0), ('MT9990000020', 114, 0),
    ('MT9990000021', 104, 0), ('MT9990000021', 111, 0),
    ('MT9990000022', 107, 0),
    ('MT9990000023', 108, 0), ('MT9990000023', 104, 0),
    ('MT9990000024', 101, 0), ('MT9990000024', 113, 0),
    ('MT9990000025', 114, 0), ('MT9990000025', 117, 0),
    ('MT9990000026', 110, 0), ('MT9990000026', 119, 0),
    ('MT9990000027', 102, 0), ('MT9990000027', 108, 0),
    ('MT9990000028', 101, 0), ('MT9990000028', 116, 0),
    ('MT9990000029', 101, 0), ('MT9990000029', 110, 0),
    ('MT9990000030', 101, 0), ('MT9990000030', 102, 0), ('MT9990000030', 108, 0)
ON DUPLICATE KEY UPDATE is_deleted = 0;
