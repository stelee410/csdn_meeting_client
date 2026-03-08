# CSDN会议客户端

## 项目介绍

CSDN会议客户端是一个基于DDD（领域驱动设计）架构的会议管理系统，提供完整的会议生命周期管理功能，包括会议创建、审核发布、报名管理、日程编排、权益购买、推广营销等功能。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| JDK | 1.8 | Java开发环境 |
| Spring Boot | 2.7.18 | 基础框架 |
| MyBatis-Plus | 3.5.5 | 数据持久化 |
| MySQL | 5.7+ | 数据库 |
| Flyway | 7.15.0 | 数据库迁移 |
| Log4j2 | 2.21.1 | 日志框架 |
| Lombok | 1.18.30 | 简化代码 |
| MapStruct | 1.5.5.Final | 对象映射 |
| Druid | 最新 | 数据库连接池 |
| SpringDoc | 1.7.0 | API文档（Swagger/OpenAPI） |
| Hutool | 5.8.23 | 工具类库 |

## DDD分层架构

```
csdn-meeting-client/
├── csdn-meeting-domain/              # 领域层 - 核心业务逻辑
├── csdn-meeting-application/         # 应用层 - 用例编排
├── csdn-meeting-infrastructure/      # 基础设施层 - 技术实现
├── csdn-meeting-interfaces/          # 接口层 - REST API
└── csdn-meeting-start/               # 启动层 - Spring Boot入口
```

### 各层职责说明

#### 1. 领域层 (domain)

领域层是系统的核心，包含业务逻辑和业务规则。

```
csdn-meeting-domain/
├── entity/           # 实体 - 具有唯一标识的业务对象
│   ├── Meeting.java              # 会议实体（聚合根）
│   ├── Participant.java            # 参与者实体
│   ├── Registration.java           # 报名实体
│   ├── MeetingFavorite.java       # 收藏实体
│   ├── MeetingRights.java         # 权益实体
│   ├── MeetingBill.java           # 账单实体
│   ├── PromotionConfig.java       # 推广配置实体
│   ├── MeetingTag.java            # 会议标签实体
│   ├── Tag.java                   # 标签实体
│   ├── UserTagSubscribe.java      # 用户标签订阅实体
│   └── MeetingTemplate.java       # 会议模板实体
├── valueobject/      # 值对象 - 无唯一标识的不可变对象
│   ├── MeetingFormat.java         # 会议形式（ONLINE/OFFLINE/HYBRID）
│   ├── MeetingType.java           # 会议类型（SUMMIT/SALON/WORKSHOP）
│   └── TimeRange.java             # 时间范围
├── event/            # 领域事件
│   ├── DomainEvent.java
│   ├── MeetingCreatedEvent.java
│   ├── MeetingStatusChangedEvent.java
│   └── MeetingEndedBriefEvent.java
├── repository/       # 仓储接口
│   ├── MeetingRepository.java
│   ├── RegistrationRepository.java
│   └── TagRepository.java
├── service/          # 领域服务
│   └── MeetingDomainService.java
└── port/           # 端口接口（外部服务抽象）
    ├── AIServicePort.java
    ├── NLPTagPort.java
    ├── PaymentPort.java
    └── AdSystemPort.java
```

#### 2. 应用层 (application)

应用层负责协调领域对象完成用例，不包含业务逻辑。

```
csdn-meeting-application/
├── dto/              # 数据传输对象
│   ├── CreateMeetingCommand.java
│   ├── UpdateMeetingCommand.java
│   ├── JoinMeetingCommand.java
│   ├── MeetingDTO.java
│   ├── MeetingListQueryDTO.java
│   └── MeetingCardItemDTO.java
├── service/          # 应用服务 - 用例编排
│   ├── MeetingApplicationService.java
│   ├── MeetingListUseCase.java
│   ├── MyMeetingsUseCase.java
│   ├── MeetingStatisticsUseCase.java
│   ├── MeetingRightsPurchaseUseCase.java
│   ├── TagSuggestionUseCase.java
│   ├── AIParsingUseCase.java
│   └── MeetingBriefUseCase.java
└── event/            # 事件监听
    └── MeetingEndedBriefListener.java
```

#### 3. 基础设施层 (infrastructure)

基础设施层提供技术实现，支撑上层运行。

```
csdn-meeting-infrastructure/
├── po/               # 持久化对象 - 数据库映射
│   ├── MeetingPO.java
│   ├── MeetingAgendaItemPO.java
│   └── ParticipantPO.java
├── repository/       # 仓储实现
│   ├── impl/         # 领域层仓储接口实现
│   │   ├── MeetingRepositoryImpl.java
│   │   └── TagRepositoryImpl.java
│   └── mapper/       # MyBatis Mapper接口
│       ├── MeetingMapper.java
│       └── MeetingTagMapper.java
├── client/           # 外部服务客户端
│   ├── AIServiceClient.java
│   ├── NLPTagClient.java
│   ├── VirusScanClient.java
│   ├── PaymentClient.java
│   └── AdSystemClient.java
├── config/           # 配置类
└── report/           # 报告生成
    └── ReportEngine.java
```

#### 4. 接口层 (interfaces)

接口层处理外部请求，是系统的入口。

```
csdn-meeting-interfaces/
├── controller/       # REST控制器
│   ├── MeetingController.java       # 会议管理接口
│   ├── MeetingTemplateController.java # 模板管理接口
│   ├── RegistrationController.java    # 报名审核接口
│   ├── UserSubscribeController.java   # 标签订阅接口
│   ├── PromotionController.java       # 推广配置接口
│   └── ConfigController.java          # 配置接口
├── dto/              # 接口DTO
│   └── ApiResponse.java
└── exception/        # 异常处理
    └── GlobalExceptionHandler.java
```

#### 5. 启动层 (start)

启动层是应用的入口点。

```
csdn-meeting-start/
├── CsdnMeetingApplication.java  # Spring Boot启动类
└── resources/
    ├── application.yml            # 应用配置
    ├── log4j2.xml                 # 日志配置
    ├── i18n/                      # 国际化消息
    └── db/migration/              # Flyway数据库迁移脚本
        ├── V1__create_meeting_core_tables.sql
        ├── V2__create_registration_favorite_tables.sql
        ├── V3__create_rights_bill_promotion_tables.sql
        └── ...
```

### 分层依赖关系

```
┌─────────────────────────────────────────────────────────┐
│                    interfaces (接口层)                    │
├─────────────────────────────────────────────────────────┤
│                   application (应用层)                    │
├─────────────────────────────────────────────────────────┤
│                     domain (领域层)                       │
├─────────────────────────────────────────────────────────┤
│                infrastructure (基础设施层)                │
└─────────────────────────────────────────────────────────┘

依赖方向: interfaces → application → domain ← infrastructure
```

## 核心功能模块

### 1. 会议管理

- **会议创建**：支持草稿创建、模板创建、AI解析创建
- **四级日程**：支持Day → Session → SubVenue → Topic四级日程编排
- **状态流转**：草稿 → 待审核 → 已发布 → 进行中 → 已结束
- **会议列表**：支持多维度筛选（形式/类型/场景/时间）、关键词搜索、分页

### 2. 报名与收藏

- **会议报名**：用户报名参加会议
- **报名审核**：支持自动通过和人工审核模式
- **我的会议**：我创建的/我报名的/我收藏的会议列表
- **收藏管理**：会议收藏与取消收藏

### 3. 权益与商业化

- **权益购买**：高阶数据权益购买（含用户画像+简报高阶数据）
- **账单管理**：权益购买账单记录
- **数据统计**：会议数据统计（浏览/报名/签到/取消）
- **会议简报**：PDF/Word格式简报导出

### 4. 推广营销

- **推广配置**：用户意图、行为周期、目标地域、投放渠道配置
- **实时估算**：预计覆盖人数/曝光/点击估算
- **订单管理**：推广订单生成与支付

### 5. 标签系统

- **标签管理**：会议标签增删改查
- **智能推荐**：基于标题和描述的标签推荐
- **标签订阅**：用户订阅感兴趣标签，接收新会议推送

## 配置说明

### 数据库配置

配置文件：`csdn-meeting-start/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/csdn_meeting?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: your_password
    type: com.alibaba.druid.pool.DruidDataSource
    
    druid:
      initial-size: 5
      min-idle: 5
      max-active: 20
      max-wait: 60000

  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

**数据库初始化：**

```sql
CREATE DATABASE IF NOT EXISTS csdn_meeting 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

### 日志配置

配置文件：`csdn-meeting-start/src/main/resources/log4j2.xml`

**日志级别：**

| Logger | 级别 | 说明 |
|--------|------|------|
| com.csdn.meeting | DEBUG | 项目代码日志 |
| org.springframework | INFO | Spring框架日志 |

**日志文件：**

| 文件 | 说明 |
|------|------|
| logs/csdn-meeting.log | 全量日志 |
| logs/csdn-meeting-error.log | 错误日志 |

### 服务端口

```yaml
server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health
```

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+ 或 8.0+

### 构建项目

```bash
mvn clean install
```

### 运行项目

```bash
cd csdn-meeting-start
mvn spring-boot:run
```

或使用Docker启动MySQL：

```bash
docker compose -f docker-compose-dev.yml up -d
```

### 访问API文档

启动后访问：http://localhost:8080/swagger-ui.html

## API接口概览

### 会议管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/meetings | 创建会议草稿 |
| PUT | /api/meetings/{id} | 更新会议 |
| GET | /api/meetings/{id} | 获取会议详情 |
| POST | /api/meetings/list | 会议列表查询（支持筛选、搜索、分页） |
| GET | /api/meetings/filter-options | 获取筛选选项枚举值 |
| POST | /api/meetings/{id}/submit | 提交审核 |
| POST | /api/meetings/{id}/withdraw | 撤回审核 |
| POST | /api/meetings/{id}/approve | 审核通过（管理员） |
| POST | /api/meetings/{id}/reject | 审核拒绝（管理员） |
| POST | /api/meetings/{id}/takedown | 下架会议 |
| DELETE | /api/meetings/{id} | 逻辑删除会议 |

### 会议操作接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/meetings/{id}/join | 报名参加会议 |
| POST | /api/meetings/{id}/leave | 取消报名 |
| POST | /api/meetings/{id}/start | 开始会议 |
| POST | /api/meetings/{id}/end | 结束会议 |
| POST | /api/meetings/{id}/cancel | 取消会议 |

### AI与标签接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/meetings/actions/ai-parse | AI解析上传文件 |
| POST | /api/meetings/actions/suggest-tags | 标签推荐 |

### 我的会议接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/meetings/my-registered | 我报名的会议 |
| GET | /api/meetings/my-favorites | 我收藏的会议 |
| GET | /api/meetings/my-created | 我创建的会议 |
| GET | /api/meetings/{id}/registrations | 会议报名列表 |

### 权益与统计接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/meetings/{id}/rights | 查询权益状态 |
| POST | /api/meetings/{id}/rights/purchase | 购买高阶权益 |
| GET | /api/meetings/{id}/statistics | 会议数据统计 |
| GET | /api/meetings/{id}/brief | 导出会议简报 |

### 推广接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/meetings/{id}/promotion/estimate | 推广实时估算 |
| POST | /api/meetings/{id}/promotion/order | 生成推广订单 |
| GET | /api/meetings/{id}/promotion | 查询推广配置 |

### 标签订阅接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/subscriptions | 订阅标签 |
| DELETE | /api/subscriptions | 取消订阅 |
| GET | /api/subscriptions | 获取用户订阅列表 |
| GET | /api/subscriptions/check | 检查订阅状态 |
| GET | /api/subscriptions/tag-ids | 获取订阅的标签ID列表 |

### 报名审核接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/registrations/{regId}/approve | 审核通过 |
| POST | /api/registrations/{regId}/reject | 审核拒绝 |

### 模板管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/meeting-templates | 获取模板列表 |
| GET | /api/meeting-templates/{id} | 获取模板详情 |
| POST | /api/meeting-templates | 创建模板（管理员） |
| PUT | /api/meeting-templates/{id} | 更新模板（管理员） |
| DELETE | /api/meeting-templates/{id} | 删除模板（管理员） |

## 外部依赖（待对接）

| 组件 | 说明 |
|------|------|
| AIServiceClient | LLM解析，需对接真实API |
| NLPTagClient | 标签推荐，需对接NLP API |
| VirusScanClient | 文件病毒扫描 |
| SensitiveWordFilter | 敏感词检测 |
| AdSystemClient | 广告系统估算 |
| PaymentClient | 收银台与支付回调 |
| UserProfileClient | 用户画像聚合 |
| ReportEngine | Markdown → PDF/Word |
| AdminNotificationClient | 推广订单通知管理后台 |
| NotificationClient | 报名审核通知（Push/短信/邮件/私信） |

## 待完善事项

1. **对接CSDN统一认证服务**：从JWT Token或Session获取当前登录用户ID
2. **实现管理员权限**：`approve`/`reject`接口的`ensureAdmin()`为占位实现
3. **对接外部服务**：将Stub替换为真实LLM、NLP、广告、支付、消息中心等API
4. **启动应用并集成验证**：执行`mvn -pl csdn-meeting-start spring-boot:run`

## 项目文档

- `docs/memory.md` - 开发状态与后续建议
- `docs/code-review/` - Code Review文档
  - `meeting-p0-code-review.md`
  - `meeting-p1-code-review.md`
  - `meeting-p2-code-review.md`
  - `meeting-p3-code-review.md`
  - `meeting-p4-code-review.md`

## 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request

## 许可证

本项目为CSDN内部项目，版权归CSDN所有。
