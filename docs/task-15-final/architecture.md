# Course Feedback Management System - Architecture Documentation

**Project**: Course Feedback Management System  
**Version**: 1.0.31  
**Date**: January 2025  
**Task**: Task 15 - Final Release

---

## Table of Contents
1. [System Overview](#system-overview)
2. [Architecture Layers](#architecture-layers)
3. [Technology Stack](#technology-stack)
4. [Component Details](#component-details)
5. [Data Flow](#data-flow)
6. [Deployment Architecture](#deployment-architecture)
7. [Security Architecture](#security-architecture)
8. [DevOps Pipeline Architecture](#devops-pipeline-architecture)

---

## 1. System Overview

The Course Feedback Management System is a web-based application designed to collect, manage, and analyze student feedback for college courses. The system follows a modern DevOps architecture with automated CI/CD pipelines, containerization, and infrastructure as code provisioning.

### Key Characteristics
- **Architecture Pattern**: Model-View-Controller (MVC)
- **Application Type**: Spring Boot Web Application
- **Database**: PostgreSQL (production), H2 (development)
- **Deployment**: Docker containers with Docker Compose orchestration
- **Provisioning**: Puppet automation for infrastructure management
- **CI/CD**: Jenkins pipeline with automated testing and quality gates

### System Boundaries
- **Frontend**: Server-side rendered HTML with Thymeleaf templates
- **Backend**: Spring Boot REST-like controllers with service layer
- **Data Layer**: JPA/Hibernate with PostgreSQL
- **Infrastructure**: Docker containers, Puppet-managed provisioning
- **CI/CD**: Jenkins with Maven, Selenium, and Docker integration

---

## 2. Architecture Layers

### 2.1 Presentation Layer
**Technology**: Thymeleaf, HTML5, CSS3  
**Location**: `src/main/resources/templates/`, `src/main/resources/static/`

**Components**:
- `feedback-form.html` - Student feedback submission interface
- `feedback-list.html` - Administrative feedback listing
- `feedback-edit.html` - Feedback editing interface
- `dashboard.html` - Analytics and reporting dashboard
- `styles.css` - Application styling

**Responsibilities**:
- Render user interfaces
- Form validation (client-side)
- Display feedback data with status indicators
- Responsive design for multiple devices

### 2.2 Controller Layer
**Technology**: Spring MVC  
**Location**: `src/main/java/in/college/coursefeedback/web/`

**Components**:
- `FeedbackController.java` - Main controller handling all HTTP requests
  - `GET /` - Display feedback submission form
  - `POST /feedback` - Submit new feedback
  - `GET /feedback/list` - List all feedback entries
  - `GET /feedback/edit/{id}` - Load feedback for editing
  - `POST /feedback/update/{id}` - Update existing feedback
  - `POST /feedback/delete/{id}` - Delete feedback entry
  - `GET /dashboard` - Display analytics dashboard

- `FeedbackForm.java` - Form binding object with validation
  - Student name validation (not blank, 2-100 chars)
  - Course name validation (not blank, 2-100 chars)
  - Rating validation (1-5 range)
  - Comments validation (10-1000 chars)

**Responsibilities**:
- Handle HTTP requests and responses
- Form validation (server-side)
- Model binding and data transformation
- Exception handling and error responses

### 2.3 Service Layer
**Technology**: Spring Service Components  
**Location**: `src/main/java/in/college/coursefeedback/service/`

**Components**:
- `FeedbackService.java` - Business logic service
  - `submitFeedback()` - Process and store new feedback
  - `getAllFeedback()` - Retrieve all feedback entries
  - `getFeedbackById()` - Retrieve specific feedback by ID
  - `updateFeedback()` - Update existing feedback entry
  - `deleteFeedback()` - Remove feedback entry
  - `getFeedbackStatistics()` - Calculate analytics metrics

**Responsibilities**:
- Business logic implementation
- Transaction management
- Data transformation between DTOs and entities
- Business rule enforcement

### 2.4 Data Access Layer
**Technology**: Spring Data JPA  
**Location**: `src/main/java/in/college/coursefeedback/repository/`

**Components**:
- `FeedbackRepository.java` - JPA repository interface
  - Extends `JpaRepository<Feedback, Long>`
  - Custom query methods for filtering and searching
  - Automatic CRUD operations

**Responsibilities**:
- Database interaction abstraction
- Query generation and optimization
- Transaction support
- Connection pool management

### 2.5 Domain Model Layer
**Technology**: JPA Entities  
**Location**: `src/main/java/in/college/coursefeedback/model/`

**Components**:
- `Feedback.java` - Core domain entity
  - `id` (Long) - Primary key
  - `studentName` (String) - Student identifier
  - `courseName` (String) - Course identifier
  - `rating` (Integer) - 1-5 rating scale
  - `comments` (String) - Detailed feedback text
  - `status` (FeedbackStatus) - Processing status
  - `submittedDate` (LocalDateTime) - Timestamp
  - `lastModifiedDate` (LocalDateTime) - Update timestamp

- `FeedbackStatus.java` - Status enumeration
  - `PENDING` - Newly submitted
  - `REVIEWED` - Reviewed by instructor
  - `ARCHIVED` - Archived feedback

**Responsibilities**:
- Domain model definition
- Data validation rules
- Entity relationships
- Persistence mapping

---

## 3. Technology Stack

### 3.1 Core Application Stack

| Layer | Technology | Version | Purpose |
|-------|-----------|---------|---------|
| Language | Java | 21.0.12 | Core programming language |
| Framework | Spring Boot | 3.x | Application framework |
| Web | Spring MVC | 6.x | Web framework |
| Templates | Thymeleaf | 3.x | Server-side templating |
| Data Access | Spring Data JPA | 3.x | ORM framework |
| ORM | Hibernate | 6.x | Object-relational mapping |
| Database (Prod) | PostgreSQL | 15+ | Production database |
| Database (Dev) | H2 | 2.x | In-memory development database |
| Security | Spring Security | 6.x | Authentication and authorization |
| Build | Maven | 3.9.16 | Build automation |

### 3.2 DevOps Stack

| Category | Technology | Version | Purpose |
|----------|-----------|---------|---------|
| Version Control | Git | 2.47.1 | Source code management |
| CI/CD | Jenkins | Latest | Continuous integration/deployment |
| Testing | Selenium WebDriver | Latest | Automated UI testing |
| Quality Gate | JUnit | 5.x | Unit testing framework |
| Containerization | Docker | 29.9.1 | Application containerization |
| Orchestration | Docker Compose | 2.x | Multi-container management |
| Provisioning | Puppet | 8.10.0 | Infrastructure automation |
| Registry | Docker Hub | N/A | Container image registry |

### 3.3 Development Tools

| Tool | Version | Purpose |
|------|---------|---------|
| Java JDK | 21.0.12 | Java development |
| Maven | 3.9.16 | Dependency management and build |
| Git | 2.47.1 | Version control |
| Docker Desktop | 29.9.1 | Local container management |
| Puppet | 8.10.0 | Configuration management |

---

## 4. Component Details

### 4.1 Spring Boot Application

**Main Class**: `CourseFeedbackApplication.java`

```java
@SpringBootApplication
public class CourseFeedbackApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourseFeedbackApplication.class, args);
    }
}
```

**Configuration**: `application.properties`
- Server port: 8080 (internal), 8082 (external Docker mapping)
- Database connection pooling
- JPA hibernate settings
- Logging configuration
- Profile-specific configurations

**Profiles**:
- `default` - H2 in-memory database for development
- `postgres` - PostgreSQL for production deployment

### 4.2 Security Configuration

**Class**: `SecurityConfig.java`

**Security Rules**:
- All endpoints publicly accessible (development mode)
- CSRF protection enabled for form submissions
- HTTP Basic authentication available
- Session management configured

**Production Recommendations**:
- Enable authentication for admin operations
- Role-based access control (RBAC)
- OAuth2/LDAP integration for college SSO
- API rate limiting

### 4.3 Database Schema

**Table**: `feedback`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto-incrementing ID |
| student_name | VARCHAR(100) | NOT NULL | Student identifier |
| course_name | VARCHAR(100) | NOT NULL | Course identifier |
| rating | INTEGER | CHECK (1-5) | Feedback rating |
| comments | TEXT | NOT NULL | Detailed comments |
| status | VARCHAR(20) | NOT NULL | Processing status |
| submitted_date | TIMESTAMP | NOT NULL | Submission timestamp |
| last_modified_date | TIMESTAMP | NOT NULL | Last update timestamp |

**Indexes**:
- Primary key index on `id`
- Index on `course_name` for filtering
- Index on `submitted_date` for sorting

---

## 5. Data Flow

### 5.1 Feedback Submission Flow

```
User Browser
    ↓ HTTP GET /
FeedbackController
    ↓ return view
feedback-form.html
    ↓ HTTP POST /feedback (FeedbackForm)
FeedbackController.submitFeedback()
    ↓ validate form
FeedbackService.submitFeedback()
    ↓ create entity
FeedbackRepository.save()
    ↓ persist
PostgreSQL Database
    ↓ redirect
GET /feedback/list
```

### 5.2 Feedback Listing Flow

```
Admin Browser
    ↓ HTTP GET /feedback/list
FeedbackController.listFeedback()
    ↓ retrieve all
FeedbackService.getAllFeedback()
    ↓ query
FeedbackRepository.findAll()
    ↓ fetch
PostgreSQL Database
    ↓ return list
FeedbackController
    ↓ add to model
feedback-list.html (rendered with data)
```

### 5.3 Dashboard Analytics Flow

```
Admin Browser
    ↓ HTTP GET /dashboard
FeedbackController.dashboard()
    ↓ calculate stats
FeedbackService.getFeedbackStatistics()
    ↓ aggregate queries
FeedbackRepository (custom queries)
    ↓ aggregate
PostgreSQL Database
    ↓ return metrics
Dashboard Model (total, average rating, by course, by status)
    ↓ render
dashboard.html (charts and metrics)
```

---

## 6. Deployment Architecture

### 6.1 Container Architecture

**Docker Image**: `rajjaiswar/course-feedback:1.0.31`

**Dockerfile Structure**:
```dockerfile
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/ || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Container Specifications**:
- Base image: Eclipse Temurin 21 JRE (Ubuntu Jammy)
- Working directory: `/app`
- Exposed port: 8080
- Health check: HTTP GET on `/`
- Entry point: Java JAR execution

### 6.2 Docker Compose Architecture

**File**: `docker-compose.yml`

**Services**:

1. **PostgreSQL Database**:
   - Image: `postgres:15-alpine`
   - Port: 5432 (internal)
   - Environment: POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD
   - Volume: `postgres_data` for data persistence
   - Health check: `pg_isready` command

2. **Application Container**:
   - Image: `rajjaiswar/course-feedback:${VERSION}`
   - Port: 8082:8080 (host:container)
   - Depends on: PostgreSQL service
   - Profile: `postgres` (activated via environment)
   - Health check: HTTP GET on `/`
   - Restart policy: `unless-stopped`

**Network**:
- Bridge network (default)
- Service discovery via service names
- Internal DNS resolution

**Volumes**:
- `postgres_data` - Named volume for database persistence
- Survives container restarts
- Mounted at `/var/lib/postgresql/data`

### 6.3 Deployment Environments

| Environment | Database | Port | Profile | Purpose |
|-------------|----------|------|---------|---------|
| Development | H2 (in-memory) | 8080 | default | Local development |
| Docker Local | PostgreSQL | 8082 | postgres | Local containerized testing |
| CI/CD | H2 (in-memory) | 8080 | default | Jenkins pipeline testing |
| Production | PostgreSQL | 8082 | postgres | Production deployment |

---

## 7. Security Architecture

### 7.1 Application Security

**Authentication**: Currently disabled (development mode)

**Authorization**: Public access to all endpoints

**CSRF Protection**: Enabled for POST requests

**Input Validation**:
- Client-side: HTML5 validation attributes
- Server-side: Spring Validation annotations
- XSS prevention: Thymeleaf auto-escaping

**Database Security**:
- Parameterized queries (JPA prevents SQL injection)
- Connection pooling with credentials management
- Database user with limited privileges

### 7.2 Network Security

**Container Network**:
- Isolated bridge network
- No direct database exposure to host
- Application as gateway to database

**Port Exposure**:
- Application: 8082 (public)
- Database: 5432 (internal only)

**HTTPS**: Not configured (future enhancement)

### 7.3 Security Recommendations

1. **Authentication**: Implement Spring Security with user roles
2. **HTTPS**: Add SSL/TLS certificates for encrypted communication
3. **Secrets Management**: Use Docker secrets or external vault
4. **API Rate Limiting**: Prevent abuse and DoS attacks
5. **Audit Logging**: Track all administrative actions
6. **Database Encryption**: Enable at-rest encryption
7. **Regular Updates**: Security patches for dependencies

---

## 8. DevOps Pipeline Architecture

### 8.1 Git Workflow

**Branching Strategy**:
```
master (stable releases)
    ← Dev (integration branch)
        ← feature/task-* (feature branches)
```

**Workflow**:
1. Create feature branch from Dev
2. Develop and test locally
3. Commit with conventional commit messages
4. Merge to Dev via pull request
5. Tag releases from master branch

### 8.2 Jenkins CI/CD Pipeline

**Pipeline Stages**:

1. **Checkout**: Git clone from repository
2. **Build**: Maven clean package
3. **Test**: JUnit unit tests execution
4. **Quality Gate**: Selenium automated testing
   - UI functionality verification
   - Form submission testing
   - Data validation testing
5. **Docker Build**: Create container image
6. **Docker Push**: Publish to Docker Hub registry
7. **Deploy**: Trigger deployment (manual or automated)

**Pipeline Configuration**: `Jenkinsfile`

**Triggers**:
- Poll SCM: Check for changes every 5 minutes
- Webhook: GitHub push events (optional)
- Manual: Triggered by administrator

### 8.3 Automated Testing Architecture

**Unit Tests**: JUnit 5
- Service layer testing
- Repository testing
- Form validation testing
- Location: `src/test/java/`

**Integration Tests**: Selenium WebDriver
- UI workflow testing
- End-to-end scenarios
- Browser automation (Chrome headless)
- Quality gate in Jenkins pipeline

**Test Execution**:
- Maven: `mvn test` (unit tests)
- Selenium: Executed in Jenkins pipeline
- Coverage: Measured with JaCoCo (future enhancement)

### 8.4 Infrastructure as Code

**Puppet Provisioning**: `scripts/provision.ps1`

**Puppet Manifest**: `manifests/site.pp`

**Provisioning Steps**:
1. Validate Docker installation
2. Pull latest application image
3. Stop existing containers
4. Deploy via Docker Compose
5. Verify health checks
6. Log deployment history

**Idempotency**: Puppet ensures desired state regardless of current state

**Rollback**: Previous images retained for rollback capability

---

## Architecture Diagrams (Conceptual)

### High-Level System Architecture
```
[User Browser] 
    ↕ HTTP/HTTPS
[Load Balancer / Reverse Proxy]
    ↕
[Application Container] (Spring Boot)
    ↕
[PostgreSQL Container]
```

### CI/CD Pipeline Flow
```
[Developer] → [Git Commit] → [Jenkins Pipeline]
    ↓
[Build & Test] → [Quality Gate] → [Docker Build]
    ↓
[Docker Registry] → [Puppet Provision] → [Production]
```

### Data Flow Architecture
```
[Presentation Layer] (Thymeleaf Templates)
    ↕
[Controller Layer] (Spring MVC)
    ↕
[Service Layer] (Business Logic)
    ↕
[Repository Layer] (Spring Data JPA)
    ↕
[Database Layer] (PostgreSQL)
```

---

## Conclusion

This architecture provides a modern, scalable, and maintainable foundation for the Course Feedback Management System. The system demonstrates industry best practices including:

- **Layered architecture** for separation of concerns
- **Spring Boot** for rapid development and production-readiness
- **Containerization** for consistent deployment
- **Infrastructure as Code** for automated provisioning
- **CI/CD pipeline** for automated testing and deployment
- **Quality gates** for ensuring code quality

The architecture is designed to support future enhancements including microservices decomposition, cloud deployment, advanced analytics, and enterprise integration.

---

**Document Version**: 1.0  
**Last Updated**: January 2025  
**Maintained By**: DevOps Team  
**Review Cycle**: Quarterly
