# CSDN会议客户端

## 项目介绍

CSDN会议客户端是一个基于DDD（领域驱动设计）架构的会议管理系统，提供会议创建、参与者管理、会议状态控制等功能。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| JDK | 1.8 | Java开发环境 |
| Spring Boot | 2.7.18 | 基础框架 |
| Spring Data JPA | 2.7.18 | 数据持久化 |
| MySQL | 8.0+ | 数据库 |
| Log4j2 | 2.21.1 | 日志框架 |
| Lombok | 1.18.30 | 简化代码 |
| MapStruct | 1.5.5.Final | 对象映射 |

## DDD分层架构

```
csdn-meeting-client/
├── csdn-meeting-domain/              # 领域层
├── csdn-meeting-application/         # 应用层
├── csdn-meeting-infrastructure/      # 基础设施层
├── csdn-meeting-interfaces/          # 接口层
└── csdn-meeting-start/               # 启动层
```

### 各层职责说明

#### 1. 领域层

领域层是系统的核心，包含业务逻辑和业务规则。

```
csdn-meeting-domain/
├── entity/           # 实体 - 具有唯一标识的业务对象
│   ├── BaseEntity.java
│   ├── Meeting.java
│   └── Participant.java
├── valueobject/      # 值对象 - 无唯一标识的不可变对象
│   ├── MeetingId.java
│   └── TimeRange.java
├── event/            # 领域事件 - 表达领域内发生的事情
│   ├── DomainEvent.java
│   ├── MeetingCreatedEvent.java
│   └── ParticipantJoinedEvent.java
├── repository/       # 仓储接口 - 定义数据访问契约
│   ├── MeetingRepository.java
│   └── ParticipantRepository.java
└── service/          # 领域服务 - 跨实体的业务逻辑
    └── MeetingDomainService.java
```

**核心概念：**
- **实体**：具有生命周期和唯一标识，如`Meeting`、`Participant`
- **值对象**：不可变、无标识，如`MeetingId`、`TimeRange`
- **领域事件**：解耦领域对象，如`MeetingCreatedEvent`
- **仓储接口**：定义在领域层，实现在基础设施层

#### 2. 应用层

应用层负责协调领域对象完成用例，不包含业务逻辑。

```
csdn-meeting-application/
├── dto/              # 数据传输对象
│   ├── CreateMeetingCommand.java
│   ├── JoinMeetingCommand.java
│   ├── MeetingDTO.java
│   └── ParticipantDTO.java
└── service/          # 应用服务 - 用例编排
    └── MeetingApplicationService.java
```

**职责：**
- 接收外部请求，转换为领域对象操作
- 协调多个领域对象完成业务用例
- 管理事务边界
- 返回DTO给调用方

#### 3. 基础设施层

基础设施层提供技术实现，支撑上层运行。

```
csdn-meeting-infrastructure/
├── po/               # 持久化对象 - 数据库映射
│   ├── MeetingPO.java
│   └── ParticipantPO.java
├── repository/       # JPA仓储接口
│   ├── MeetingJpaRepository.java
│   └── ParticipantJpaRepository.java
├── repository/impl/  # 仓储实现 - 实现领域层接口
│   ├── MeetingRepositoryImpl.java
│   └── ParticipantRepositoryImpl.java
└── config/           # 配置类
    └── JpaConfig.java
```

**职责：**
- 实现领域层定义的仓储接口
- 数据库访问实现
- 外部服务集成
- 技术组件配置

#### 4. 接口层

接口层处理外部请求，是系统的入口。

```
csdn-meeting-interfaces/
├── controller/       # REST控制器
│   └── MeetingController.java
├── dto/              # 接口DTO
│   └── ApiResponse.java
└── exception/        # 异常处理
    └── GlobalExceptionHandler.java
```

**职责：**
- 接收HTTP请求
- 参数校验
- 调用应用服务
- 返回响应结果

#### 5. 启动层

启动层是应用的入口点。

```
csdn-meeting-start/
├── CsdnMeetingApplication.java  # Spring Boot启动类
└── resources/
    ├── application.yml          # 应用配置
    └── log4j2.xml               # 日志配置
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

## 配置说明

### 数据库配置

配置文件：`csdn-meeting-start/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/csdn_meeting?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: root

  jpa:
    hibernate:
      ddl-auto: update    # create-drop / create / update / validate / none
    show-sql: true        # 是否显示SQL
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQLDialect
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
| org.hibernate.SQL | DEBUG | SQL语句日志 |
| org.springframework | INFO | Spring框架日志 |

**日志文件：**

| 文件 | 说明 |
|------|------|
| logs/csdn-meeting.log | 全量日志 |
| logs/csdn-meeting-error.log | 错误日志 |

**滚动策略：**
- 按天滚动
- 单文件最大100MB
- 保留30天

### 服务端口

```yaml
server:
  port: 8080
```

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+

### 构建项目

```bash
mvn clean install
```

### 运行项目

```bash
cd csdn-meeting-start
mvn spring-boot:run
```

### API接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/meetings | 创建会议 |
| GET | /api/meetings/{meetingId} | 获取会议详情 |
| GET | /api/meetings | 获取所有会议 |
| GET | /api/meetings/creator/{creatorId} | 获取用户创建的会议 |
| POST | /api/meetings/{meetingId}/join | 加入会议 |
| POST | /api/meetings/{meetingId}/leave | 离开会议 |
| POST | /api/meetings/{meetingId}/start | 开始会议 |
| POST | /api/meetings/{meetingId}/end | 结束会议 |
| POST | /api/meetings/{meetingId}/cancel | 取消会议 |

## 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request
