# Task 15: Manual Commands Required

This document lists all commands that you need to run manually for Task 15 completion.

---

## STEP 1: Tool Version Verification

Run these commands and update the audit.md with actual versions:

### In PowerShell
```powershell
# Navigate to project
cd 'C:\Users\RAJ JAISWAR\OneDrive\Documents\Devops\course-feedback-git'

# Check tool versions
Write-Host "=== Tool Versions ===" -ForegroundColor Cyan
Write-Host "Git:" -ForegroundColor Yellow
git --version

Write-Host "`nJava:" -ForegroundColor Yellow
java --version

Write-Host "`nMaven:" -ForegroundColor Yellow
mvn --version

Write-Host "`nDocker:" -ForegroundColor Yellow
docker --version
docker-compose --version

Write-Host "`nSelenium (from pom.xml):" -ForegroundColor Yellow
Select-String -Path pom.xml -Pattern "<selenium" -Context 0,1
```

### In WSL2 Ubuntu
```bash
# Check Puppet
puppet --version

# Check Chrome (for Selenium headless)
google-chrome --version || chromium-browser --version
```

### Jenkins Version
1. Open: http://localhost:8080/
2. Click: Manage Jenkins
3. Click: System Information
4. Record Jenkins version

---

## STEP 2: Service Health Check

### Check Running Services
```powershell
# Check if Jenkins is accessible
curl http://localhost:8080 -TimeoutSec 5

# Check Docker service
Get-Service | Where-Object {$_.Name -like "*docker*"}

# Check port availability
netstat -an | Select-String "8080|8082|8083"
```

If Jenkins is not running:
- Start Jenkins service or
- Run: `java -jar jenkins.war` (if standalone)

If Docker is not running:
- Start Docker Desktop from Windows

---

## STEP 3: End-to-End Demo Preparation

### Before Running Demo

1. **Ensure services are running**:
```powershell
# Jenkins should respond
curl http://localhost:8080

# Docker should be running
docker ps
```

2. **Clean up previous containers** (if needed):
```powershell
docker stop feedback-app
docker rm feedback-app
```

3. **Verify Docker images exist**:
```powershell
docker images rajjaiswar/course-feedback
```

---

## STEP 4: End-to-End Demo Run

### Part 1: Make a Visible Change

```powershell
# Create demo branch
git checkout -b feature/task-15-demo-change

# Make a small visible change (example: update footer in login.html)
# Edit: src/main/resources/templates/feedback-form.html
# Change line 50 (or similar) to add: "DevOps Demo - v1.0.0"

# Commit and push
git add src/main/resources/templates/
git commit -m "feat(demo): add version footer for Task 15 demo"
git push -u origin feature/task-15-demo-change
```

### Part 2: Create and Merge PR

1. **Go to GitHub**: https://github.com/rajjaiswar82/-Course-Feedback-Management-System
2. **Create Pull Request**:
   - From: `feature/task-15-demo-change`
   - To: `Dev`
   - Title: "Demo: Add version footer for Task 15"
   - Description: "Adds visible version footer for end-to-end demo"
3. **Review and Merge** the PR
4. **Record timestamp** of merge

### Part 3: Monitor Jenkins Build

1. **Open Jenkins**: http://localhost:8080/
2. **Navigate to Job**: Course Feedback Pipeline
3. **Watch the build** trigger (SCM polling or webhook)
4. **Capture screenshots** of:
   - Build triggered
   - Stage View showing all stages
   - Console output
   - Test results
   - Docker push confirmation

### Part 4: Verify Docker Registry

```powershell
# Check what tags were pushed
docker images rajjaiswar/course-feedback

# Or check Docker Hub manually
Start-Process "https://hub.docker.com/r/rajjaiswar/course-feedback/tags"
```

### Part 5: Deploy with Docker Compose (Task 14 scripts)

```powershell
# Update version in .env.provision (or create it)
@"
APP_VERSION=1.0.31
APP_ENV=prod
JAVA_OPTS=-Xms256m -Xmx512m
"@ | Out-File -FilePath .env.provision -Encoding UTF8

# Deploy with provision script
.\scripts\provision.ps1 -Version 1.0.31

# Run health check
.\scripts\health-check.ps1

# Open in browser
Start-Process "http://localhost:8082/"
```

### Part 6: Quality Gate Failure Demo

```powershell
# Create a branch with intentional bug
git checkout Dev
git pull
git checkout -b feature/task-15-broken-test

# Break a test (example: change expected text in a Selenium test)
# Edit: src/test/java/.../LoginPageTest.java
# Change assertion to expect wrong text

git add src/test/
git commit -m "test: intentionally break test for quality gate demo"
git push -u origin feature/task-15-broken-test

# Create PR, watch it fail in Jenkins
# Then close/revert the PR
```

### Part 7: Rollback Demo

```powershell
# Rollback to previous version
.\scripts\rollback.ps1

# Verify rollback
.\scripts\health-check.ps1

# Check version in browser
Start-Process "http://localhost:8082/"
```

---

## STEP 5: Capture Evidence

### Screenshots Needed
1. Jenkins dashboard showing successful build
2. Stage view with all stages green
3. Selenium test results
4. Docker Hub showing new image tag
5. Application in browser showing new footer
6. Quality gate failure (red stage)
7. Health check output (pass)
8. Rollback confirmation
9. Application after rollback

### Logs to Save
```powershell
# Save Jenkins console output
# (Copy from Jenkins UI to docs/task-15-final/jenkins-console.txt)

# Save deployment logs
Copy-Item deployment-history.json docs/task-15-final/
Copy-Item rollback-history.json docs/task-15-final/

# Save provision logs if they exist
Get-ChildItem docs/task-14-provisioning/*.log | Copy-Item -Destination docs/task-15-final/
```

---

## STEP 6: Create Release

### Tag and Release

```powershell
# Ensure you're on main/Dev with latest changes
git checkout main
git pull origin main

# Create annotated tag
git tag -a v1.0.0 -m "Final release: Course Feedback Management System with complete DevOps pipeline"

# Push tag
git push origin v1.0.0

# Verify tag
git tag -l
git show v1.0.0
```

### Create GitHub Release

1. Go to: https://github.com/rajjaiswar82/-Course-Feedback-Management-System/releases/new
2. Select tag: `v1.0.0`
3. Release title: `v1.0.0 - Course Feedback Management System`
4. Description: (see template below)
5. Attach artifacts:
   - `target/course-feedback-management-system-0.0.1-SNAPSHOT.jar`
   - Test reports (if available)

**Release Notes Template**:
```markdown
# Course Feedback Management System v1.0.0

## Overview
Complete DevOps workflow implementation with CI/CD pipeline, automated testing, Docker containerization, and Puppet provisioning.

## Features
- Full-stack Spring Boot application for course feedback management
- User authentication and authorization
- Feedback submission and review workflows
- Admin dashboard
- PostgreSQL and H2 database support

## DevOps Pipeline
- ✅ Git workflow with branching strategy
- ✅ Jenkins CI/CD pipeline
- ✅ Selenium quality gate
- ✅ Docker containerization
- ✅ Automated deployment
- ✅ Puppet configuration management
- ✅ Health checks and rollback capability

## Docker Image
- Repository: `rajjaiswar/course-feedback`
- Tag: `1.0.31`
- Multi-stage build (Maven + JRE)

## Deployment
See [Setup Guide](docs/task-15-final/setup-guide.md) for installation instructions.

## Known Limitations
- Single-node deployment
- Masterless Puppet
- Local Jenkins instance
- See [Limitations](docs/task-15-final/limitations.md) for full list

## Future Enhancements
See [Future Enhancements](docs/task-15-final/future-enhancements.md)

## Contributors
- Raj Jaiswar (DevOps Engineer)
```

---

## STEP 7: Final Verification

### Repository Structure
```powershell
# Print repository tree (install tree command or use dir)
tree /F docs > docs/task-15-final/repo-structure.txt

# Show git log
git log --oneline --graph --decorate -n 30 > docs/task-15-final/git-history.txt

# List branches
git branch -a > docs/task-15-final/branches.txt

# List tags
git tag -l > docs/task-15-final/tags.txt
```

---

## Troubleshooting

### If Jenkins is not running
```powershell
# Check if process exists
Get-Process java | Where-Object {$_.Path -like "*jenkins*"}

# Start Jenkins (adjust path)
Start-Process java -ArgumentList "-jar","C:\path\to\jenkins.war"
```

### If Docker push fails
```powershell
# Login to Docker Hub
docker login

# Verify credentials in Jenkins
# Go to: Manage Jenkins > Credentials
```

### If port 8082 is in use
```powershell
# Find what's using it
netstat -ano | Select-String "8082"

# Kill the process (replace PID)
Stop-Process -Id <PID> -Force

# Or use different port in docker-compose.yml
```

### If Puppet fails in WSL2
```bash
# Check Puppet installation
puppet --version

# Check if systemd is working
systemctl status

# If systemd issues, use Docker-based approach (Task 14)
```

---

## Quick Reference: Complete Demo Sequence

```powershell
# 1. Start services
Start-Process "http://localhost:8080"  # Jenkins
docker ps  # Docker

# 2. Make change
git checkout -b feature/task-15-demo-change
# Edit file, commit, push

# 3. Create PR and merge on GitHub

# 4. Watch Jenkins build

# 5. Deploy
.\scripts\provision.ps1 -Version 1.0.31

# 6. Health check
.\scripts\health-check.ps1

# 7. Open app
Start-Process "http://localhost:8082/"

# 8. Demo rollback
.\scripts\rollback.ps1

# Total time: ~5-10 minutes
```

---

## Video Recording

**Recommended Tool**: OBS Studio (free)
- Download: https://obsproject.com/
- Settings: 1920x1080, 30fps, MP4 format
- Duration: 5-8 minutes

**What to Record**:
1. Jenkins dashboard (0:30)
2. Making code change and PR (1:00)
3. Jenkins build running - timelapse (2:00)
4. Stage view all green (0:30)
5. Docker Hub showing new image (0:30)
6. Health check and app running (1:00)
7. Quality gate failure demo (1:00)
8. Rollback demonstration (1:00)

**Alternative**: Windows Game Bar (Win + G)

---

This completes the manual commands reference for Task 15.
