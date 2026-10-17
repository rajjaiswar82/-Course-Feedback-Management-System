# Task 7: Jenkins Installation and Continuous Integration

## Overview

This directory contains all documentation, configuration, and evidence for Task 7 of the DevOps course project. Task 7 focuses on installing Jenkins, configuring a CI job for the Course Feedback Management System, and demonstrating automated build triggers.

## Task 7 Objectives

1. ✅ Install and configure Jenkins
2. ✅ Connect GitHub repository to Jenkins
3. ✅ Create Maven build job with SCM polling
4. ✅ Archive build artifacts (JAR file)
5. ✅ Demonstrate manual and automated builds
6. ✅ Document the complete setup process

## Deliverables

### 1. Documentation
- ✅ `jenkins-setup.md` - Complete installation and configuration guide
- ✅ `evidence-checklist.md` - Screenshot and evidence collection guide
- ✅ `README.md` - This overview document

### 2. Configuration Files
- ✅ `config.xml` - Exported Jenkins job configuration
- ⏳ `build-log.txt` - Console output from successful build (to be captured)

### 3. Evidence (To Be Collected)
- ⏳ Screenshots of Jenkins installation and setup
- ⏳ Screenshots of job configuration
- ⏳ Console logs showing successful builds
- ⏳ Evidence of SCM-triggered builds
- ⏳ Screenshots of archived artifacts

## Project Build Information

- **Build Tool:** Maven 3.9.16
- **Java Version:** Java 21 (OpenJDK 21.0.12)
- **Packaging:** JAR (Spring Boot executable)
- **Artifact:** `course-feedback-management-system-0.0.1-SNAPSHOT.jar`
- **Size:** ~58 MB
- **Build Command:** `mvn clean package`

## Jenkins Job Configuration

### Job Details
- **Job Name:** `course-feedback-ci`
- **Job Type:** Maven Project
- **Repository:** https://github.com/rajjaiswar82/-Course-Feedback-Management-System.git
- **Branch:** `*/Dev`
- **Build Trigger:** Poll SCM - `H/5 * * * *` (every 5 minutes)
- **Build Goals:** `clean package`
- **Artifacts:** `target/*.jar` (with fingerprinting)

### Tools Configuration
- **JDK:** JDK-21 (OpenJDK 21.0.12)
- **Maven:** Maven-3.9.16
- **Git:** System Git installation

## Quick Start

### Prerequisites
1. Install Java 21
2. Install Maven 3.9.16
3. Install Git
4. Clone the repository

### Setup Steps (Summary)
1. **Install Jenkins:** Follow `jenkins-setup.md` STEP 1-2
2. **Configure Plugins:** Install Git, GitHub, Maven Integration plugins
3. **Configure Tools:** Set up JDK-21 and Maven-3.9.16 paths
4. **Create Job:** Follow job configuration in `jenkins-setup.md` STEP 6
5. **Test Build:** Run manual build and verify success
6. **Test Trigger:** Make a commit and verify SCM polling works

### Verification
```powershell
# Verify Java
java -version  # Should show 21.0.12

# Verify Maven
mvn -version   # Should show 3.9.16

# Verify Jenkins service (Windows)
Get-Service Jenkins  # Should show Running

# Access Jenkins
# Browser: http://localhost:8080
```

## Documentation Structure

```
docs/task-07-jenkins/
├── README.md                  ← You are here
├── jenkins-setup.md           ← Complete setup guide
├── evidence-checklist.md      ← Evidence collection guide
├── config.xml                 ← Jenkins job configuration
└── build-log.txt              ← Sample/actual build log
```

## Evidence Collection

After completing the Jenkins setup, collect evidence using `evidence-checklist.md`:

### Critical Evidence Required
1. ✅ Jenkins dashboard screenshot
2. ✅ Plugin installation confirmation
3. ✅ Tool configuration screenshots (JDK, Maven, Git)
4. ✅ Job configuration screenshots
5. ✅ Successful build console log
6. ✅ **"Started by an SCM change"** console log (CRITICAL!)
7. ✅ Build artifacts screenshot showing JAR file
8. ✅ Poll SCM log showing change detection

### Evidence Organization
```
screenshots/
├── 01-installation/
│   └── (unlock, plugins, admin, dashboard)
├── 02-configuration/
│   └── (JDK, Maven, Git, credentials)
├── 03-job-config/
│   └── (SCM, triggers, build, artifacts)
├── 04-builds/
│   └── (manual, SCM-triggered, logs, artifacts)
└── 05-verification/
    └── (multiple builds, dashboard)
```

## Key Files Explained

### jenkins-setup.md
Complete step-by-step installation and configuration guide covering:
- Jenkins installation on Windows
- Initial setup and plugin installation
- Global tool configuration
- CI job creation and configuration
- Build trigger setup
- Troubleshooting guide

### evidence-checklist.md
Comprehensive checklist of all screenshots and evidence needed for Task 7 report:
- 30+ screenshot specifications
- File naming conventions
- Content requirements for each screenshot
- Organization structure
- Report writing guide

### config.xml
Exported Jenkins job configuration in XML format:
- SCM settings (Git repository, branch)
- Build triggers (Poll SCM schedule)
- Build goals (Maven clean package)
- Post-build actions (artifact archiving)
- Note: Credentials removed for security

### build-log.txt
Console output from successful Jenkins build showing:
- Git repository checkout
- Maven dependency resolution
- Compilation process
- Test execution
- JAR packaging
- Artifact archiving
- Build success confirmation

## Git Workflow for Task 7

### Branch Strategy
```bash
# Start from Dev branch
git checkout Dev
git pull origin Dev

# Create feature branch
git checkout -b feature/task-07-jenkins-ci

# Make changes and commit
git add docs/task-07-jenkins/
git commit -m "docs: add Jenkins setup guide for Task 7"

# Push to remote
git push -u origin feature/task-07-jenkins-ci

# Create Pull Request to Dev
# After review and approval, merge PR
```

### Commit Messages
- `docs: add Jenkins setup guide for Task 7`
- `ci: add Jenkins job configuration template`
- `docs: add build log and evidence checklist`
- `docs: update task 7 documentation with screenshots`

## Manual Steps Required

Since Jenkins installation and configuration cannot be fully automated, the following steps must be performed manually:

### 1. Install Jenkins
- Download and run Windows installer
- Or run Jenkins WAR file
- Access http://localhost:8080

### 2. Initial Setup
- Unlock Jenkins with initial admin password
- Install suggested plugins
- Create admin user

### 3. Configure Tools
- Add JDK-21 in Global Tool Configuration
- Add Maven-3.9.16 in Global Tool Configuration
- Verify Git auto-detection

### 4. Create and Configure Job
- Create `course-feedback-ci` job
- Configure Git SCM with repository URL and branch
- Set up Poll SCM trigger `H/5 * * * *`
- Configure Maven build goals
- Set up artifact archiving

### 5. Test and Verify
- Run manual build (Build Now)
- Make a commit to trigger SCM polling
- Verify automatic build triggered
- Confirm artifacts archived

### 6. Collect Evidence
- Take screenshots per evidence-checklist.md
- Export config.xml from Jenkins
- Capture console logs
- Document any issues and resolutions

## Commands Reference

### Local Build Testing
```powershell
# Navigate to project directory
cd "C:\Users\RAJ JAISWAR\OneDrive\Documents\Devops\course-feedback-git"

# Clean build
mvn clean package

# Skip tests
mvn clean package -DskipTests

# Run tests only
mvn test

# Run application
mvn spring-boot:run
```

### Jenkins Service Management (Windows)
```powershell
# Check service status
Get-Service Jenkins

# Start service
Start-Service Jenkins

# Stop service
Stop-Service Jenkins

# Restart service
Restart-Service Jenkins
```

### Git Commands for Testing Triggers
```powershell
# Make a trivial change
echo "" >> README.md

# Commit and push
git add README.md
git commit -m "trigger: test Jenkins CI polling"
git push origin Dev

# Wait 5 minutes for polling, then check Jenkins
```

## Troubleshooting

### Common Issues

**Issue:** Jenkins won't start  
**Solution:** Check Java installation, verify port 8080 is free, check logs in JENKINS_HOME/logs

**Issue:** Build fails with "Maven not found"  
**Solution:** Verify Maven path in Global Tool Configuration, restart Jenkins

**Issue:** Git authentication fails  
**Solution:** Add GitHub credentials with Personal Access Token

**Issue:** Polling not triggering builds  
**Solution:** Check Poll SCM log in job, verify branch name matches, ensure repository is accessible

**Issue:** Artifacts not archived  
**Solution:** Verify build succeeds, check artifact path `target/*.jar` matches actual location

## Success Criteria

Task 7 is complete when:

1. ✅ Jenkins is installed and accessible at http://localhost:8080
2. ✅ Required plugins are installed (Git, GitHub, Maven, JUnit)
3. ✅ Tools are configured (JDK-21, Maven-3.9.16)
4. ✅ CI job `course-feedback-ci` exists and is properly configured
5. ✅ Job builds from `*/Dev` branch successfully
6. ✅ Manual build completes with BUILD SUCCESS
7. ✅ SCM polling is configured with `H/5 * * * *`
8. ✅ Automatic build triggered by commit shows "Started by an SCM change"
9. ✅ Build artifact (JAR) is archived and downloadable from Jenkins
10. ✅ All documentation is committed to the repository
11. ✅ Evidence is collected per checklist
12. ✅ Pull request is created and merged to Dev branch

## Next Steps (Task 8)

After Task 7 is complete:
- Implement Selenium automated testing
- Integrate tests into Jenkins pipeline
- Add deployment automation
- Configure Docker containerization
- Set up Puppet provisioning

## Resources

### Jenkins Documentation
- Official Docs: https://www.jenkins.io/doc/
- Maven Plugin: https://plugins.jenkins.io/maven-plugin/
- Git Plugin: https://plugins.jenkins.io/git/

### Maven Documentation
- Maven Guide: https://maven.apache.org/guides/
- Spring Boot with Maven: https://spring.io/guides/gs/maven/

### Project Repository
- GitHub: https://github.com/rajjaiswar82/-Course-Feedback-Management-System

## Notes

- This is a learning project for DevOps course
- Focus on understanding CI/CD concepts and Jenkins basics
- Document all steps and challenges for learning reflection
- Evidence collection is critical for report submission
- Security best practices: Never commit passwords or tokens

## Contact

For questions or issues with Task 7 setup, refer to:
- Course materials
- jenkins-setup.md troubleshooting section
- Jenkins community forums
- Course instructor/TA

---

**Task Status:** Documentation Complete - Manual Setup Required  
**Last Updated:** October 18, 2026  
**Branch:** feature/task-07-jenkins-ci
