# CSDN Meeting Client

## Project Introduction

CSDN Meeting Client is a conference management system based on DDD (Domain-Driven Design) architecture, providing complete conference lifecycle management features, including meeting creation, review and publication, registration management, agenda scheduling, rights purchase, promotion and marketing, etc.

## Tech Stack

| Technology | Version | Description |
|------------|---------|-------------|
| JDK | 1.8 | Java Development Environment |
| Spring Boot | 2.7.18 | Framework |
| MyBatis-Plus | 3.5.5 | Data Persistence |
| MySQL | 5.7+ | Database |
| Flyway | 7.15.0 | Database Migration |
| Log4j2 | 2.21.1 | Logging Framework |
| Lombok | 1.18.30 | Code Simplification |
| MapStruct | 1.5.5.Final | Object Mapping |
| Druid | Latest | Database Connection Pool |
| SpringDoc | 1.7.0 | API Documentation (Swagger/OpenAPI) |
| Hutool | 5.8.23 | Utility Library |

## DDD Layered Architecture

```
csdn-meeting-client/
├── csdn-meeting-domain/              # Domain Layer - Core Business Logic
├── csdn-meeting-application/         # Application Layer - Use Case Orchestration
├── csdn-meeting-infrastructure/    # Infrastructure Layer - Technical Implementation
├── csdn-meeting-interfaces/          # Interface Layer - REST API
└── csdn-meeting-start/               # Bootstrap Layer - Spring Boot Entry
```

### Layer Responsibilities

#### 1. Domain Layer

The domain layer is the core of the system, containing business logic and rules.

**Key Concepts:**
- **Entities**: Objects with unique identity and lifecycle, such as `Meeting`, `Participant`
- **Value Objects**: Immutable objects without identity, such as `MeetingFormat`, `TimeRange`
- **Domain Events**: Decouple domain objects, such as `MeetingCreatedEvent`
- **Repository Interfaces**: Define data access contracts
- **Domain Services**: Cross-entity business logic

#### 2. Application Layer

The application layer coordinates domain objects to complete use cases, without containing business logic.

**Responsibilities:**
- Receive external requests and convert to domain object operations
- Coordinate multiple domain objects to complete business use cases
- Manage transaction boundaries
- Return DTOs to callers

#### 3. Infrastructure Layer

The infrastructure layer provides technical implementation to support upper layers.

**Responsibilities:**
- Implement repository interfaces defined in the domain layer
- Database access implementation
- External service integration
- Technical component configuration

#### 4. Interface Layer

The interface layer handles external requests and is the system entry point.

**Responsibilities:**
- Receive HTTP requests
- Parameter validation
- Call application services
- Return response results

#### 5. Bootstrap Layer

The bootstrap layer is the application entry point.

### Layer Dependencies

```
┌─────────────────────────────────────────────────────────┐
│                    interfaces (Interface Layer)          │
├─────────────────────────────────────────────────────────┤
│                   application (Application Layer)        │
├─────────────────────────────────────────────────────────┤
│                     domain (Domain Layer)                │
├─────────────────────────────────────────────────────────┤
│                infrastructure (Infrastructure Layer)     │
└─────────────────────────────────────────────────────────┘

Dependency Direction: interfaces → application → domain ← infrastructure
```

## Core Functional Modules

### 1. Meeting Management

- **Meeting Creation**: Support draft creation, template creation, AI parsing creation
- **Four-level Agenda**: Support Day → Session → SubVenue → Topic four-level agenda arrangement
- **Status Flow**: Draft → Pending Review → Published → In Progress → Ended
- **Meeting List**: Support multi-dimensional filtering (format/type/scene/time), keyword search, pagination

### 2. Registration and Favorites

- **Meeting Registration**: Users register for meetings
- **Registration Review**: Support automatic approval and manual review modes
- **My Meetings**: My created/my registered/my favorite meetings list
- **Favorites Management**: Meeting favorite and unfavorite

### 3. Rights and Commercialization

- **Rights Purchase**: Premium data rights purchase (including user profiles + advanced brief data)
- **Bill Management**: Rights purchase bill records
- **Data Statistics**: Meeting data statistics (views/registrations/check-ins/cancellations)
- **Meeting Brief**: PDF/Word format brief export

### 4. Promotion and Marketing

- **Promotion Configuration**: User intent, behavior period, target region, delivery channel configuration
- **Real-time Estimation**: Estimated reach/exposure/click estimation
- **Order Management**: Promotion order generation and payment

### 5. Tag System

- **Tag Management**: Meeting tag CRUD operations
- **Smart Recommendation**: Tag recommendation based on title and description
- **Tag Subscription**: Users subscribe to interested tags and receive new meeting notifications

## Configuration

### Database Configuration

Configuration file: `csdn-meeting-start/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/csdn_meeting?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: your_password

  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

**Database Initialization:**

```sql
CREATE DATABASE IF NOT EXISTS csdn_meeting 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

### Server Port

```yaml
server:
  port: 8080
```

## Quick Start

### Requirements

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+ or 8.0+

### Build Project

```bash
mvn clean install
```

### Run Project

```bash
cd csdn-meeting-start
mvn spring-boot:run
```

Or start MySQL with Docker:

```bash
docker compose -f docker-compose-dev.yml up -d
```

### Access API Documentation

After startup, visit: http://localhost:8080/swagger-ui.html

## API Overview

### Meeting Management APIs

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/meetings | Create meeting draft |
| PUT | /api/meetings/{id} | Update meeting |
| GET | /api/meetings/{id} | Get meeting details |
| POST | /api/meetings/list | Meeting list query (with filter, search, pagination) |
| GET | /api/meetings/filter-options | Get filter options enum values |
| POST | /api/meetings/{id}/submit | Submit for review |
| POST | /api/meetings/{id}/withdraw | Withdraw review |
| POST | /api/meetings/{id}/approve | Approve (Admin) |
| POST | /api/meetings/{id}/reject | Reject (Admin) |
| POST | /api/meetings/{id}/takedown | Take down meeting |
| DELETE | /api/meetings/{id} | Logic delete meeting |

### Meeting Operation APIs

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/meetings/{id}/join | Register for meeting |
| POST | /api/meetings/{id}/leave | Cancel registration |
| POST | /api/meetings/{id}/start | Start meeting |
| POST | /api/meetings/{id}/end | End meeting |
| POST | /api/meetings/{id}/cancel | Cancel meeting |

### AI and Tag APIs

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/meetings/actions/ai-parse | AI parse uploaded file |
| POST | /api/meetings/actions/suggest-tags | Tag recommendation |

### My Meetings APIs

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/meetings/my-registered | My registered meetings |
| GET | /api/meetings/my-favorites | My favorite meetings |
| GET | /api/meetings/my-created | My created meetings |
| GET | /api/meetings/{id}/registrations | Meeting registrations list |

### Rights and Statistics APIs

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/meetings/{id}/rights | Query rights status |
| POST | /api/meetings/{id}/rights/purchase | Purchase premium rights |
| GET | /api/meetings/{id}/statistics | Meeting data statistics |
| GET | /api/meetings/{id}/brief | Export meeting brief |

### Promotion APIs

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/meetings/{id}/promotion/estimate | Promotion real-time estimation |
| POST | /api/meetings/{id}/promotion/order | Create promotion order |
| GET | /api/meetings/{id}/promotion | Query promotion configuration |

### Tag Subscription APIs

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/subscriptions | Subscribe to tag |
| DELETE | /api/subscriptions | Unsubscribe tag |
| GET | /api/subscriptions | Get user subscription list |
| GET | /api/subscriptions/check | Check subscription status |
| GET | /api/subscriptions/tag-ids | Get subscribed tag IDs |

### Registration Review APIs

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/registrations/{regId}/approve | Approve registration |
| POST | /api/registrations/{regId}/reject | Reject registration |

### Template Management APIs

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/meeting-templates | Get template list |
| GET | /api/meeting-templates/{id} | Get template details |
| POST | /api/meeting-templates | Create template (Admin) |
| PUT | /api/meeting-templates/{id} | Update template (Admin) |
| DELETE | /api/meeting-templates/{id} | Delete template (Admin) |

## External Dependencies (To Be Integrated)

| Component | Description |
|-----------|-------------|
| AIServiceClient | LLM parsing, needs real API integration |
| NLPTagClient | Tag recommendation, needs NLP API integration |
| VirusScanClient | File virus scan |
| SensitiveWordFilter | Sensitive word detection |
| AdSystemClient | Ad system estimation |
| PaymentClient | Cashier and payment callback |
| UserProfileClient | User profile aggregation |
| ReportEngine | Markdown → PDF/Word |
| AdminNotificationClient | Promotion order notification to admin |
| NotificationClient | Registration review notification (Push/SMS/Email/Private Message) |

## TODO Items

1. **Integrate CSDN Unified Authentication Service**: Get current login user ID from JWT Token or Session
2. **Implement Admin Permission**: `ensureAdmin()` in `approve`/`reject` interfaces is placeholder implementation
3. **Integrate External Services**: Replace Stubs with real LLM, NLP, ad, payment, message center, etc. APIs
4. **Startup and Integration Validation**: Execute `mvn -pl csdn-meeting-start spring-boot:run`

## Contributing

1. Fork this repository
2. Create a new Feat_xxx branch
3. Commit your code
4. Create a Pull Request

## License

This project is an internal CSDN project, copyright belongs to CSDN.
