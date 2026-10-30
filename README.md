# 📚 Course Feedback Management System

A modern, full-stack web application for collecting and managing student feedback on college courses, built with Spring Boot and featuring a premium UI design.

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green)
![Docker](https://img.shields.io/badge/Docker-Ready-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## ✨ Features

### Core Functionality
- 📝 **Student Feedback Submission** - Easy-to-use form for course feedback
- 📊 **Admin Dashboard** - Analytics with total feedback, average ratings, and status metrics
- 🔍 **Search & Filter** - Find feedback by student name or course name
- ✏️ **Edit & Delete** - Admin can manage all feedback entries
- 🏷️ **Status Management** - Track feedback lifecycle (Submitted → Reviewed → Resolved)

### Technical Features
- 🎨 **Premium UI Design** - Modern, elegant interface with gradient colors
- 🔐 **Role-Based Access Control** - Student and Admin roles with different permissions
- 🐳 **Docker Ready** - Fully containerized with Docker Compose
- 🚀 **CI/CD Pipeline** - Jenkins automation with quality gates
- 🤖 **Puppet Provisioning** - Infrastructure as Code for deployment
- ✅ **Selenium Testing** - Automated UI testing
- 🗄️ **Multi-Database Support** - H2 (dev) and PostgreSQL (production)

---

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Maven 3.9+
- Docker Desktop (for containerized deployment)
- Git

### Run Locally (Development)

```bash
# Clone repository
git clone <repository-url>
cd course-feedback-git

# Run with Maven
mvn spring-boot:run

# Access application
open http://localhost:8081
```

### Run with Docker

```bash
# Using Docker Compose
docker-compose up -d

# Access application
open http://localhost:8082
```

---

## 🌐 Access URLs

| Service | URL | Description |
|---------|-----|-------------|
| Application | http://localhost:8081 | Main application (Maven) |
| Application | http://localhost:8082 | Docker deployment |
| Jenkins | http://localhost:8080 | CI/CD Pipeline |
| H2 Console | http://localhost:8081/h2-console | Database console (dev) |

---

## 🔐 Login Credentials

### Student Account
```
Username: student
Password: Student@123
```
**Permissions**: Submit feedback, view feedback list

### Admin Account
```
Username: admin
Password: Admin@123
```
**Permissions**: Full access - dashboard, edit, delete, status updates

---

## 📋 Application Structure

```
course-feedback-git/
├── src/
│   ├── main/
│   │   ├── java/in/college/coursefeedback/
│   │   │   ├── config/          # Security configuration
│   │   │   ├── model/           # Domain entities
│   │   │   ├── repository/      # Data access layer
│   │   │   ├── service/         # Business logic
│   │   │   └── web/             # Controllers
│   │   └── resources/
│   │       ├── templates/       # Thymeleaf HTML templates
│   │       ├── static/          # CSS, images
│   │       └── application.properties
│   └── test/                    # JUnit & Selenium tests
├── scripts/
│   └── provision.ps1            # Puppet provisioning script
├── manifests/
│   └── site.pp                  # Puppet manifest
├── docker-compose.yml           # Multi-container setup
├── Dockerfile                   # Application image
├── Jenkinsfile                  # CI/CD pipeline
└── pom.xml                      # Maven dependencies
```

---

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 3.5.6
- **Language**: Java 21
- **Security**: Spring Security with role-based access
- **Data Access**: Spring Data JPA + Hibernate
- **Database**: H2 (dev), PostgreSQL (prod)
- **Validation**: Jakarta Bean Validation

### Frontend
- **Template Engine**: Thymeleaf
- **Styling**: Custom CSS with modern design system
- **Design**: Responsive, mobile-friendly UI

### DevOps
- **Build Tool**: Maven 3.9.16
- **Version Control**: Git
- **CI/CD**: Jenkins
- **Containerization**: Docker + Docker Compose
- **Orchestration**: Puppet
- **Testing**: JUnit 5 + Selenium WebDriver

---

## 🐳 Docker Deployment

### Using Docker Compose

```bash
# Set version
$env:VERSION="1.0.31"

# Start services
docker-compose up -d

# Check status
docker ps

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

### Using Puppet Provisioning

```bash
# Deploy specific version
.\scripts\provision.ps1 -Version 1.0.31

# Force redeployment
.\scripts\provision.ps1 -Version 1.0.31 -Force
```

### Docker Images

Available on Docker Hub:
- `rajjaiswar/course-feedback:1.0.31` (latest)
- `rajjaiswar/course-feedback:1.0.30`
- `rajjaiswar/course-feedback:1.0.28`

---

## 🔄 CI/CD Pipeline

The Jenkins pipeline automates:

1. **Checkout** - Clone from Git repository
2. **Build** - Maven compile and package
3. **Test** - JUnit unit tests
4. **Quality Gate** - Selenium UI tests
5. **Docker Build** - Create container image
6. **Docker Push** - Publish to Docker Hub
7. **Deploy** - Puppet-based provisioning

### Run Jenkins Pipeline

```bash
# Start Jenkins
java -jar jenkins.war --httpPort=8080

# Access at http://localhost:8080
# Configure job with Jenkinsfile from repository
```

---

## 🧪 Testing

### Unit Tests

```bash
mvn test
```

### Selenium UI Tests

```bash
# Run specific Selenium tests
mvn test -Dtest=FeedbackFormValidationTest

# Run all tests
mvn verify
```

---

## 📊 Database Configuration

### H2 (Development)

```properties
spring.datasource.url=jdbc:h2:mem:coursefeedback
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
```

**Console**: http://localhost:8081/h2-console

### PostgreSQL (Production)

```properties
spring.datasource.url=jdbc:postgresql://postgres:5432/coursefeedback
spring.datasource.username=feedbackuser
spring.datasource.password=feedbackpass
spring.profiles.active=postgres
```

---

## 🎨 UI Design

### Design Features
- **Modern Color System**: Gradient-based brand colors (purple to pink)
- **Glassmorphism**: Frosted glass effects on navigation
- **3D Depth**: Multi-layer shadows for visual hierarchy
- **Subtle Animations**: Smooth transitions on hover
- **Responsive Design**: Mobile-first approach
- **Clean Typography**: Professional font system

### Color Palette
```css
Primary: #6366f1 (Indigo)
Secondary: #8b5cf6 (Purple)
Success: #10b981 (Green)
Warning: #f59e0b (Amber)
Danger: #ef4444 (Red)
```

---

## 📖 API Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/` | Redirect to home | - |
| GET | `/login` | Login page | Public |
| GET | `/home` | Role-based redirect | Auth |
| GET | `/feedback/new` | Feedback form | Student/Admin |
| POST | `/feedback` | Submit feedback | Student/Admin |
| GET | `/feedback` | List all feedback | Student/Admin |
| GET | `/feedback/edit/{id}` | Edit form | Admin |
| POST | `/feedback/update/{id}` | Update feedback | Admin |
| POST | `/feedback/delete/{id}` | Delete feedback | Admin |
| POST | `/feedback/status/{id}` | Update status | Admin |
| GET | `/dashboard` | Analytics dashboard | Admin |

---

## 🔧 Configuration

### Change Server Port

Edit `src/main/resources/application.properties`:

```properties
server.port=8081
```

### Enable PostgreSQL

Edit `docker-compose.yml` or set environment variable:

```bash
export SPRING_PROFILES_ACTIVE=postgres
```

### CSRF Configuration

CSRF is enabled by default. To disable for testing:

```java
http.csrf(csrf -> csrf.disable());
```

---

## 📝 Development Tasks

### Task Completion Status

- ✅ Task 1-4: Git Setup and Workflows
- ✅ Task 5: Branching Strategy
- ✅ Task 6-8: Jenkins CI/CD Pipeline
- ✅ Task 9: Selenium Automated Testing
- ✅ Task 10: Maven Build Integration
- ✅ Task 11: Docker Containerization
- ✅ Task 12: Docker Registry (Docker Hub)
- ✅ Task 13: Puppet Provisioning
- ✅ Task 14: Infrastructure Automation
- ✅ **Task 15: Final Release with Premium UI**

---

## 🚀 Deployment Guide

### Local Development

```bash
mvn spring-boot:run
```

### Docker Production

```bash
docker-compose up -d
```

### Kubernetes (Future)

```bash
kubectl apply -f k8s/deployment.yaml
```

---

## 🐛 Troubleshooting

### Port Already in Use

```bash
# Find process on port
netstat -ano | findstr :8081

# Kill process (Windows)
taskkill /PID <process-id> /F
```

### Docker Issues

```bash
# Restart Docker Desktop
# Clear containers
docker-compose down -v

# Rebuild images
docker-compose build --no-cache
```

### Maven Build Failures

```bash
# Clean build
mvn clean install -DskipTests

# Update dependencies
mvn dependency:resolve
```

---

## 📚 Documentation

Detailed documentation available in `/docs`:

- `docs/task-15-final/architecture.md` - System architecture
- `docs/task-15-final/workflow-guide.md` - Complete DevOps workflow
- `docs/task-15-final/APPLICATION-ACCESS.md` - Access guide
- `docs/task-15-final/UI-TRANSFORMATION.md` - UI design details
- `docs/task-15-final/JENKINS-RUNNING.md` - Jenkins setup

---

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'feat: add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

### Commit Convention

- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation changes
- `style:` Code style changes
- `refactor:` Code refactoring
- `test:` Test updates
- `chore:` Build/tooling changes

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👥 Team

**DevOps Course Project**  
College: [Your College Name]  
Course: DevOps Engineering  
Semester: [Your Semester]

---

## 🙏 Acknowledgments

- Spring Boot Team for the excellent framework
- Docker for containerization platform
- Jenkins for CI/CD automation
- Puppet for infrastructure management
- Thymeleaf for templating engine

---

## 📞 Support

For issues and questions:
- Create an issue in GitHub
- Contact: [Your Email]

---

## 🔮 Future Enhancements

- [ ] Dark mode support
- [ ] Email notifications
- [ ] Export to PDF/Excel
- [ ] Multi-language support
- [ ] Advanced analytics charts
- [ ] Real-time updates with WebSockets
- [ ] Mobile app (React Native)
- [ ] Kubernetes deployment
- [ ] Microservices architecture
- [ ] GraphQL API

---

## 📊 Project Statistics

- **Lines of Code**: ~5,000+
- **Test Coverage**: 80%+
- **Docker Images**: 3 versions
- **Documentation**: 12+ files
- **Commits**: 50+
- **Development Time**: 4 weeks

---

## ⭐ Star History

If you find this project useful, please consider giving it a star on GitHub!






