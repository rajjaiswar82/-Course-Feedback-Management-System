# Task 11 - Evidence Checklist
## Screenshots and Documentation Required

---

## 📸 Screenshots to Capture

### 1. Docker Images
**Command:** `docker images course-feedback`

**What to show:**
- Image name: `course-feedback`
- Tags: `1.0.0`, `latest`
- Image ID
- Size (~522MB)

**Purpose:** Prove image was built successfully

---

### 2. Docker Build Output
**Command:** `docker build -t course-feedback:1.0.0 .`

**What to show:**
- Build stages (Stage 1: build, Stage 2: runtime)
- Successful completion message
- Final image SHA
- Build time

**Purpose:** Show multi-stage build process

---

### 3. Running Container
**Command:** `docker ps`

**What to show:**
- Container ID
- Container name: `feedback-app`
- Status: `Up X seconds (healthy)`
- Port mapping: `0.0.0.0:8082->8080/tcp`

**Purpose:** Prove container is running and healthy

---

### 4. Application in Browser
**URL:** `http://localhost:8082`

**What to show:**
- Application homepage loaded
- Course Feedback Management System interface
- URL bar showing `localhost:8082`

**Purpose:** Demonstrate application is accessible

---

### 5. Container Logs
**Command:** `docker logs feedback-app --tail 30`

**What to show:**
- Spring Boot startup logs
- "Started CourseFeedbackApplication" message
- Tomcat started on port 8080
- No error messages

**Purpose:** Show application started successfully

---

### 6. Container Inspection (Port Mapping)
**Command:** `docker port feedback-app`

**What to show:**
- Port mapping: `8080/tcp -> 0.0.0.0:8082`

**Purpose:** Demonstrate port mapping configuration

---

### 7. Container Stats
**Command:** `docker stats --no-stream feedback-app`

**What to show:**
- CPU usage
- Memory usage (~385MB)
- Network I/O
- Number of processes (~46)

**Purpose:** Show resource consumption

---

### 8. Two Containers Running
**Command:** `docker ps`

**What to show:**
- Container 1: `feedback-app` on port 8082 (APP_ENV=dev)
- Container 2: `feedback-app-test` on port 8083 (APP_ENV=test)
- Both healthy

**Purpose:** Demonstrate multiple containers with different configs

---

### 9. Stopped Container
**Command:** `docker ps -a` (after `docker stop feedback-app`)

**What to show:**
- Container ID
- Status: `Exited (143) X seconds ago`
- Name: `feedback-app`

**Purpose:** Show container lifecycle (stopped state)

---

### 10. Container Removed
**Command:** `docker ps -a` (after `docker rm -f feedback-app`)

**What to show:**
- Empty list or container gone
- No `feedback-app` in the list

**Purpose:** Show container cleanup

---

### 11. Docker Exec Inside Container
**Command:** `docker exec feedback-app ls -la /app`

**What to show:**
- Directory listing of `/app`
- `app.jar` file present
- Owner: `appuser:appgroup`

**Purpose:** Demonstrate container file system access

---

### 12. Environment Variables
**Command:** `docker exec feedback-app env | grep APP_ENV`

**What to show:**
- `APP_ENV=dev` (for dev container)
- `APP_ENV=test` (for test container)

**Purpose:** Show environment variable configuration

---

## 📄 Documentation Files Required

### 1. Dockerfile
- ✅ Multi-stage build
- ✅ Comments explaining each instruction
- ✅ Non-root user
- ✅ HEALTHCHECK included
- ✅ Environment variables

### 2. .dockerignore
- ✅ Excludes `target/`, `.git/`, docs
- ✅ Excludes IDE files

### 3. docker-lifecycle.md
- ✅ Dockerfile explanation line by line
- ✅ Lifecycle diagram
- ✅ Command reference table
- ✅ Port mapping explanation
- ✅ Troubleshooting guide

### 4. image-details.md
- ✅ Image name and tags
- ✅ Size information
- ✅ Base images used
- ✅ Layer count
- ✅ Exposed ports
- ✅ Environment variables

### 5. docker-command-log.txt
- ✅ Real terminal output
- ✅ All commands executed
- ✅ Timestamps
- ✅ Results shown

### 6. evidence-checklist.md (this file)
- ✅ List of all screenshots
- ✅ Commands used
- ✅ What each proves

---

## ✅ Verification Checklist

Before submitting, verify:

- [ ] All 12 screenshots captured
- [ ] Screenshots are clear and readable
- [ ] All documentation files created
- [ ] README updated with Docker instructions
- [ ] Commands logged in docker-command-log.txt
- [ ] Image size noted (~522MB)
- [ ] Both containers tested (dev and test)
- [ ] Application accessible on both ports
- [ ] Container lifecycle demonstrated (run → stop → start → remove)
- [ ] Dockerfile well-commented
- [ ] Git commits made
- [ ] Pull request created

---

## 📊 Expected Results Summary

| Item | Expected Value | Verified |
|------|----------------|----------|
| Image name | course-feedback | ☐ |
| Image tags | 1.0.0, latest | ☐ |
| Image size | ~520-530 MB | ☐ |
| Base image (build) | maven:3.9-eclipse-temurin-21 | ☐ |
| Base image (runtime) | eclipse-temurin:21-jre-alpine | ☐ |
| Container port | 8080 | ☐ |
| Host port (dev) | 8082 | ☐ |
| Host port (test) | 8083 | ☐ |
| Health status | healthy | ☐ |
| Memory usage | ~380-400 MB | ☐ |
| Process count | ~40-50 | ☐ |
| Application starts | Yes (< 10 seconds) | ☐ |
| Application accessible | Yes (HTTP 200) | ☐ |

---

## 🎯 Grading Criteria Coverage

| Criterion | Evidence | Status |
|-----------|----------|--------|
| Dockerfile created | Dockerfile in repo root | ✅ |
| Multi-stage build | Stages in Dockerfile | ✅ |
| Image built | docker images screenshot | ☐ |
| Container runs | docker ps screenshot | ☐ |
| Port mapping works | Browser screenshot | ☐ |
| Lifecycle demonstrated | Multiple screenshots | ☐ |
| Documentation complete | All .md files | ☐ |
| Commands logged | docker-command-log.txt | ☐ |

---

**All items should be checked (✅) before final submission!**
