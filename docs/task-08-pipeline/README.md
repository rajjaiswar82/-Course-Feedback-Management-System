# Task 8: Jenkins Pipeline as Code and Deployment

## Overview

This directory contains all documentation and configuration for Task 8 - implementing a declarative Jenkins pipeline with parameterized deployment to multiple environments.

---

## Task 8 Objectives

1. ✅ Create declarative Jenkinsfile with multiple stages
2. ✅ Implement parameterized deployment
3. ✅ Deploy to environment-specific locations
4. ✅ Demonstrate configuration changes based on parameters
5. ✅ Archive build artifacts with fingerprinting
6. ✅ Document complete pipeline setup

---

## Deliverables

### 1. Pipeline Implementation
- ✅ **Jenkinsfile** - Declarative pipeline in repository root
- ✅ **Pipeline Job** - `course-feedback-pipeline` in Jenkins
- ✅ **SCM Integration** - Pipeline script from Git

### 2. Pipeline Stages
1. **Checkout** - Clone repository from GitHub
2. **Build** - Compile Java application with Maven
3. **Package** - Create executable JAR and archive artifact
4. **Deploy** - Deploy to environment-specific directory

### 3. Parameters
- **DEPLOY_ENV** - Choice (dev/test/prod)
- **APP_PORT** - String (default: 8080)
- **SKIP_DEPLOY** - Boolean (default: false)

### 4. Documentation
- ✅ `pipeline-setup.md` - Complete setup and configuration guide
- ✅ `evidence-checklist.md` - Screenshot requirements
- ✅ `README.md` - This overview document
- ⏳ `pipeline-run-log.txt` - Successful console output (to be captured)

---

## Quick Start

### Prerequisites
- Jenkins installed and running (from Task 7)
- JDK-21 and Maven-3.9.16 configured in Jenkins
- Git repository accessible from Jenkins
- Windows environment with PowerShell/CMD

### Setup Steps

1. **Create deployment directories:**
   ```powershell
   New-Item -ItemType Directory -Path "C:\Jenkins\deployments\dev" -Force
   New-Item -ItemType Directory -Path "C:\Jenkins\deployments\test" -Force
   New-Item -ItemType Directory -Path "C:\Jenkins\deployments\prod" -Force
   ```

2. **Create pipeline job in Jenkins:**
   - Dashboard → New Item
   - Name: `course-feedback-pipeline`
   - Type: Pipeline
   - Definition: Pipeline script from SCM
   - Repository: `https://github.com/rajjaiswar82/-Course-Feedback-Management-System.git`
   - Branch: `*/feature/task-08-jenkins-pipeline-deploy`
   - Script Path: `Jenkinsfile`

3. **Run initial build:**
   - Build Now (registers parameters)

4. **Run with parameters:**
   - Build with Parameters
   - Select environment
   - Click Build

### Test Deployments

```powershell
# Deploy to DEV (port 8080)
DEPLOY_ENV: dev
APP_PORT: 8080
SKIP_DEPLOY: false

# Deploy to TEST (port 8081)
DEPLOY_ENV: test
APP_PORT: 8081
SKIP_DEPLOY: false

# Skip deployment
SKIP_DEPLOY: true
```

---

## Project Configuration

### Build Information
- **Build Tool:** Maven 3.9.16
- **Java Version:** Java 21
- **Packaging:** JAR (Spring Boot)
- **Artifact:** `course-feedback-management-system-0.0.1-SNAPSHOT.jar`

### Jenkins Tools
- **JDK:** `JDK-21`
- **Maven:** `Maven-3.9.16`

### Deployment Strategy
- **Method:** Spring Boot JAR deployment
- **Server:** Embedded (Tomcat/Jetty)
- **Directories:**
  - DEV: `C:\Jenkins\deployments\dev`
  - TEST: `C:\Jenkins\deployments\test`
  - PROD: `C:\Jenkins\deployments\prod`

---

## Pipeline Structure

```
┌─────────────┐
│  Checkout   │  Clone repository from Git
└──────┬──────┘
       │
┌──────▼──────┐
│    Build    │  Compile with Maven (mvn clean compile)
└──────┬──────┘
       │
┌──────▼──────┐
│   Package   │  Create JAR (mvn package) + Archive artifacts
└──────┬──────┘
       │
┌──────▼──────┐
│   Deploy    │  Deploy to environment directory (conditional)
└─────────────┘
       │
┌──────▼──────┐
│   Post      │  Success/Failure notifications
└─────────────┘
```

---

## Parameter Configuration

### DEPLOY_ENV Parameter

Affects multiple aspects of deployment:

| Environment | Directory | Config File | Port (default) | Profile |
|-------------|-----------|-------------|----------------|---------|
| **dev** | `C:\Jenkins\deployments\dev` | `application-dev.properties` | 8080 | dev |
| **test** | `C:\Jenkins\deployments\test` | `application-test.properties` | 8081 | test |
| **prod** | `C:\Jenkins\deployments\prod` | `application-prod.properties` | 8082 | prod |

### Configuration Files

Each environment gets a unique configuration file:

**`application-dev.properties`:**
```properties
server.port=8080
spring.profiles.active=dev
app.environment=dev
info.app.environment=dev
```

**`application-test.properties`:**
```properties
server.port=8081
spring.profiles.active=test
app.environment=test
info.app.environment=test
```

---

## Verification

### Check Deployment

```powershell
# List deployment files
Get-ChildItem "C:\Jenkins\deployments\dev"

# Expected files:
# - course-feedback-management-system-0.0.1-SNAPSHOT.jar
# - application-dev.properties
# - app-dev.log

# View configuration
Get-Content "C:\Jenkins\deployments\dev\application-dev.properties"

# View logs
Get-Content "C:\Jenkins\deployments\dev\app-dev.log" -Tail 50

# Check if running
netstat -ano | findstr :8080 | findstr LISTENING

# Test application
Start-Process "http://localhost:8080"
```

---

## Evidence Collection

See `evidence-checklist.md` for complete screenshot list.

### Critical Screenshots
1. Pipeline job configuration (SCM settings)
2. Build with Parameters page showing all parameters
3. Stage view for DEV deployment (all green)
4. Console output showing deployment configuration
5. DEV application running in browser
6. TEST application running on different port
7. Configuration files showing environment differences
8. Deploy stage skipped when SKIP_DEPLOY=true

### Console Logs
- Save full console output from at least one successful deployment
- Include in `pipeline-run-log.txt`

---

## Troubleshooting

### Pipeline Fails
- Check Jenkins console output for errors
- Verify JDK and Maven tools configured
- Ensure repository accessible

### Deployment Fails
- Check if port is already in use
- Verify deployment directory exists and is writable
- Check application logs in deployment directory

### Application Not Accessible
- Verify application started (check logs)
- Check firewall settings
- Try `http://127.0.0.1:<port>` instead of localhost

---

## File Structure

```
docs/task-08-pipeline/
├── README.md                  ← You are here
├── pipeline-setup.md          ← Complete setup guide
├── evidence-checklist.md      ← Screenshot requirements
└── pipeline-run-log.txt       ← Console output (to be added)

Jenkinsfile                     ← Pipeline script (repository root)
```

---

## Git Workflow

### Branch Strategy
```bash
git checkout Dev
git pull origin Dev
git checkout -b feature/task-08-jenkins-pipeline-deploy

# Make changes, commit
git add Jenkinsfile docs/task-08-pipeline/
git commit -m "ci: add declarative Jenkinsfile with parameterized deployment"

# Push
git push -u origin feature/task-08-jenkins-pipeline-deploy

# Create PR: feature/task-08-jenkins-pipeline-deploy → Dev
```

### After Merge
```bash
# Update Jenkins pipeline job
# Change branch specifier to: */Dev

# Clean up
git checkout Dev
git pull origin Dev
git branch -d feature/task-08-jenkins-pipeline-deploy
```

---

## Success Criteria

Task 8 is complete when:

1. ✅ Jenkinsfile exists in repository root
2. ✅ Pipeline job `course-feedback-pipeline` configured
3. ✅ Parameters available: DEPLOY_ENV, APP_PORT, SKIP_DEPLOY
4. ✅ Pipeline runs successfully through all stages
5. ✅ DEV deployment successful (http://localhost:8080)
6. ✅ TEST deployment successful (http://localhost:8081)
7. ✅ Configuration files show environment-specific settings
8. ✅ SKIP_DEPLOY parameter skips Deploy stage
9. ✅ Artifacts archived with fingerprinting
10. ✅ Documentation complete
11. ✅ Evidence collected (screenshots and logs)
12. ✅ Pull request merged to Dev

---

## Next Steps (Task 9-10)

The Jenkinsfile includes a placeholder for automated testing:

```groovy
// TODO: Insert Test stage here for Task 9-10
```

Future tasks will add:
- Selenium automated tests
- Test stage in pipeline
- Test result reporting
- Quality gates

---

## Resources

### Documentation Files
- `pipeline-setup.md` - Detailed setup and configuration
- `evidence-checklist.md` - Complete screenshot list
- `README.md` - This overview

### Jenkins Documentation
- Declarative Pipeline: https://www.jenkins.io/doc/book/pipeline/syntax/
- Pipeline Parameters: https://www.jenkins.io/doc/book/pipeline/syntax/#parameters
- SCM: https://www.jenkins.io/doc/book/pipeline/syntax/#scm

### Spring Boot Documentation
- Application Properties: https://docs.spring.io/spring-boot/reference/features/external-config.html
- Profiles: https://docs.spring.io/spring-boot/reference/features/profiles.html

---

## Notes

- This is an academic project demonstrating CI/CD concepts
- Deployment strategy uses Spring Boot embedded server (no external Tomcat)
- Multiple environments can run simultaneously on different ports
- Configuration is parameterized for flexibility
- Security: Never commit real credentials or secrets

---

**Status:** Implementation Complete - Testing Required  
**Last Updated:** October 18, 2026  
**Branch:** feature/task-08-jenkins-pipeline-deploy  
**Task:** Task 8 - Pipeline as Code and Server Deployment
