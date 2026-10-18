# Task 8: Evidence Collection Checklist

This document lists all screenshots and evidence required for Task 8 submission.

---

## Required Screenshots

### 1. Jenkins Pipeline Job Configuration

#### 1.1 Pipeline Job Creation
- **File Name:** `01-pipeline-job-creation.png`
- **Content:** New Item screen with `course-feedback-pipeline` name and Pipeline type selected
- **Location:** Jenkins Dashboard → New Item

#### 1.2 Pipeline Configuration - SCM
- **File Name:** `02-pipeline-scm-config.png`
- **Content:** Pipeline configuration showing:
  - Definition: Pipeline script from SCM
  - SCM: Git
  - Repository URL
  - Branch: `*/feature/task-08-jenkins-pipeline-deploy` (or `*/Dev` after merge)
  - Script Path: `Jenkinsfile`
- **Location:** Pipeline job → Configure → Pipeline section

#### 1.3 Build Triggers
- **File Name:** `03-build-triggers.png`
- **Content:** Poll SCM enabled with schedule `H/5 * * * *`
- **Location:** Pipeline job → Configure → Build Triggers section

---

### 2. First Pipeline Run (Parameter Registration)

#### 2.1 First Build (No Parameters Yet)
- **File Name:** `04-first-build-no-params.png`
- **Content:** First build using "Build Now" option
- **Shows:** Build history with #1
- **Location:** Pipeline job page

#### 2.2 Stage View (Initial)
- **File Name:** `05-stage-view-initial.png`
- **Content:** Pipeline stage view showing all stages
- **Shows:** Checkout, Build, Package, Deploy stages
- **Location:** Pipeline job page after first build

---

### 3. Build with Parameters Page

#### 3.1 Parameters Page
- **File Name:** `06-build-with-parameters.png`
- **Content:** "Build with Parameters" page showing all parameters:
  - DEPLOY_ENV (choice: dev/test/prod)
  - APP_PORT (string: default 8080)
  - SKIP_DEPLOY (boolean: default false)
- **Location:** Pipeline job → Build with Parameters

---

### 4. DEV Environment Deployment

#### 4.1 Parameters for DEV
- **File Name:** `07-dev-parameters.png`
- **Content:** Build parameters set to:
  - DEPLOY_ENV: `dev`
  - APP_PORT: `8080`
  - SKIP_DEPLOY: `false`
- **Location:** Build with Parameters page

#### 4.2 DEV Stage View
- **File Name:** `08-dev-stage-view.png`
- **Content:** Successful pipeline execution with all stages green
- **Shows:** Checkout → Build → Package → Deploy all successful
- **Location:** Pipeline job page after DEV build

#### 4.3 DEV Console Output (Top)
- **File Name:** `09-dev-console-top.png`
- **Content:** Beginning of console output showing:
  - "Started by user..."
  - Parameters values
  - Git checkout
- **Location:** DEV build → Console Output

#### 4.4 DEV Console Output (Deployment)
- **File Name:** `10-dev-console-deployment.png`
- **Content:** Deployment section showing:
  - "DEPLOYMENT CONFIGURATION"
  - Environment: dev
  - Port: 8080
  - Deploy Directory: C:\Jenkins\deployments\dev
  - "DEPLOYMENT SUCCESSFUL!"
  - Application URL
- **Location:** DEV build → Console Output (scroll to Deploy stage)

#### 4.5 DEV Deployment Directory
- **File Name:** `11-dev-deployment-files.png`
- **Content:** File Explorer showing deployment directory contents:
  - `C:\Jenkins\deployments\dev\`
  - Files: JAR, application-dev.properties, app-dev.log
- **Command to capture:**
  ```powershell
  Get-ChildItem "C:\Jenkins\deployments\dev" | Format-Table Name, Length, LastWriteTime
  ```

#### 4.6 DEV Configuration File
- **File Name:** `12-dev-config-file.png`
- **Content:** Contents of `application-dev.properties`:
  ```
  server.port=8080
  spring.profiles.active=dev
  app.environment=dev
  info.app.environment=dev
  ```
- **Command:**
  ```powershell
  Get-Content "C:\Jenkins\deployments\dev\application-dev.properties"
  ```

#### 4.7 DEV Application URL
- **File Name:** `13-dev-application-browser.png`
- **Content:** Browser showing application running at `http://localhost:8080`
- **Shows:** Login page or application homepage
- **URL visible in address bar**

#### 4.8 DEV Application Login/Home
- **File Name:** `14-dev-application-logged-in.png`
- **Content:** Application interface after login (if applicable)
- **Shows:** Dashboard or main application page

---

### 5. TEST Environment Deployment

#### 5.1 Parameters for TEST
- **File Name:** `15-test-parameters.png`
- **Content:** Build parameters set to:
  - DEPLOY_ENV: `test`
  - APP_PORT: `8081`
  - SKIP_DEPLOY: `false`
- **Location:** Build with Parameters page

#### 5.2 TEST Stage View
- **File Name:** `16-test-stage-view.png`
- **Content:** Successful pipeline execution for TEST environment
- **Shows:** All stages green with TEST parameters
- **Location:** Pipeline job page after TEST build

#### 5.3 TEST Console Output (Deployment)
- **File Name:** `17-test-console-deployment.png`
- **Content:** Deployment section showing:
  - Environment: test
  - Port: 8081
  - Deploy Directory: C:\Jenkins\deployments\test
  - "DEPLOYMENT SUCCESSFUL!"
- **Location:** TEST build → Console Output

#### 5.4 TEST Deployment Directory
- **File Name:** `18-test-deployment-files.png`
- **Content:** File Explorer showing `C:\Jenkins\deployments\test\` contents
- **Shows:** JAR, application-test.properties, app-test.log

#### 5.5 TEST Configuration File
- **File Name:** `19-test-config-file.png`
- **Content:** Contents of `application-test.properties` showing:
  - `server.port=8081`
  - `spring.profiles.active=test`
  - `app.environment=test`

#### 5.6 TEST Application URL
- **File Name:** `20-test-application-browser.png`
- **Content:** Browser showing application at `http://localhost:8081`
- **Shows:** Application running on different port

---

### 6. PROD Environment Deployment (Optional but Recommended)

#### 6.1 Parameters for PROD
- **File Name:** `21-prod-parameters.png`
- **Content:** Build parameters set to:
  - DEPLOY_ENV: `prod`
  - APP_PORT: `8082`
  - SKIP_DEPLOY: `false`

#### 6.2 PROD Stage View
- **File Name:** `22-prod-stage-view.png`
- **Content:** Successful pipeline execution for PROD environment

#### 6.3 PROD Console Output (Deployment)
- **File Name:** `23-prod-console-deployment.png`
- **Content:** Deployment showing Environment: prod, Port: 8082

#### 6.4 PROD Application URL
- **File Name:** `24-prod-application-browser.png`
- **Content:** Browser showing application at `http://localhost:8082`

---

### 7. SKIP_DEPLOY Test

#### 7.1 Parameters with SKIP_DEPLOY=true
- **File Name:** `25-skip-deploy-parameters.png`
- **Content:** Build parameters set to:
  - DEPLOY_ENV: `dev`
  - APP_PORT: `8080`
  - SKIP_DEPLOY: `true` ← **This is key**

#### 7.2 Stage View (Deploy Skipped)
- **File Name:** `26-skip-deploy-stage-view.png`
- **Content:** Stage view showing:
  - Checkout ✅ (green)
  - Build ✅ (green)
  - Package ✅ (green)
  - Deploy ⏭️ (skipped/gray)
- **Location:** Pipeline job page

#### 7.3 Console Output (Deploy Skipped Message)
- **File Name:** `27-skip-deploy-console.png`
- **Content:** Console output showing:
  - "Stage 'Deploy' skipped due to when conditional"
  - Or similar skip message
- **Location:** Build → Console Output

---

### 8. Build History & Artifacts

#### 8.1 Build History (Multiple Runs)
- **File Name:** `28-build-history.png`
- **Content:** Build History showing multiple successful builds:
  - Build #1 (first run)
  - Build #2 (dev)
  - Build #3 (test)
  - Build #4 (prod)
  - Build #5 (skip deploy)
- **Shows:** All builds with status and parameters
- **Location:** Pipeline job page → Build History

#### 8.2 Archived Artifacts
- **File Name:** `29-archived-artifacts.png`
- **Content:** Build page showing "Build Artifacts" section
- **Shows:** `course-feedback-management-system-0.0.1-SNAPSHOT.jar`
- **Shows:** Fingerprint link
- **Location:** Any successful build page

#### 8.3 Artifact Fingerprint
- **File Name:** `30-artifact-fingerprint.png`
- **Content:** Fingerprint page showing artifact tracking
- **Location:** Build → Artifact → Fingerprint link

---

### 9. Pipeline Script (Jenkinsfile)

#### 9.1 Jenkinsfile in Repository
- **File Name:** `31-jenkinsfile-in-repo.png`
- **Content:** GitHub showing Jenkinsfile in repository root
- **Location:** GitHub repository root

#### 9.2 Jenkinsfile Content (Stages)
- **File Name:** `32-jenkinsfile-stages.png`
- **Content:** Jenkinsfile content showing stage definitions
- **Can be screenshot of file in editor or GitHub view**

---

### 10. Comparison Screenshots

#### 10.1 Side-by-Side: DEV vs TEST Config
- **File Name:** `33-dev-vs-test-config.png`
- **Content:** Side-by-side comparison of:
  - `application-dev.properties` (port 8080)
  - `application-test.properties` (port 8081)
- **Shows:** Different port and environment settings

#### 10.2 Side-by-Side: Multiple Environments Running
- **File Name:** `34-multiple-envs-running.png`
- **Content:** Task Manager or `netstat` output showing:
  - Multiple Java processes
  - Each listening on different port (8080, 8081, 8082)
- **Command:**
  ```powershell
  netstat -ano | findstr "8080 8081 8082" | findstr LISTENING
  ```

---

## Evidence Files Checklist

In addition to screenshots, collect these files:

### Build Logs
- ✅ `pipeline-run-dev.txt` - Full console output from DEV deployment
- ✅ `pipeline-run-test.txt` - Full console output from TEST deployment
- ✅ `pipeline-run-skip.txt` - Full console output from SKIP_DEPLOY run

### Configuration Files
- ✅ `application-dev.properties` - DEV environment configuration
- ✅ `application-test.properties` - TEST environment configuration
- ✅ `application-prod.properties` - PROD environment configuration (if created)

### Pipeline Files
- ✅ `Jenkinsfile` - The pipeline script (already in repository)
- ✅ `pipeline-config-export.xml` - Exported Jenkins job configuration (optional)

---

## Screenshot Capture Tips

### Windows Screenshot Tools
- **Snipping Tool:** `Win + Shift + S`
- **Full Screen:** `Win + PrtScn`
- **Active Window:** `Alt + PrtScn`

### Best Practices
1. **Capture full browser/window** including address bar for URLs
2. **Ensure text is readable** - use high resolution
3. **Highlight key information** if needed (but don't obscure content)
4. **Include timestamps** from Jenkins (visible in builds)
5. **Show URL bar** when capturing application screenshots
6. **Use consistent naming** as specified above
7. **Organize in folders** by category

---

## Screenshot Organization

```
screenshots/
├── 01-configuration/
│   ├── 01-pipeline-job-creation.png
│   ├── 02-pipeline-scm-config.png
│   └── 03-build-triggers.png
├── 02-parameters/
│   ├── 04-first-build-no-params.png
│   ├── 05-stage-view-initial.png
│   └── 06-build-with-parameters.png
├── 03-dev-deployment/
│   ├── 07-dev-parameters.png
│   ├── 08-dev-stage-view.png
│   ├── 09-dev-console-top.png
│   ├── 10-dev-console-deployment.png
│   ├── 11-dev-deployment-files.png
│   ├── 12-dev-config-file.png
│   ├── 13-dev-application-browser.png
│   └── 14-dev-application-logged-in.png
├── 04-test-deployment/
│   ├── 15-test-parameters.png
│   ├── 16-test-stage-view.png
│   ├── 17-test-console-deployment.png
│   ├── 18-test-deployment-files.png
│   ├── 19-test-config-file.png
│   └── 20-test-application-browser.png
├── 05-prod-deployment/ (optional)
│   ├── 21-prod-parameters.png
│   ├── 22-prod-stage-view.png
│   ├── 23-prod-console-deployment.png
│   └── 24-prod-application-browser.png
├── 06-skip-deploy/
│   ├── 25-skip-deploy-parameters.png
│   ├── 26-skip-deploy-stage-view.png
│   └── 27-skip-deploy-console.png
└── 07-verification/
    ├── 28-build-history.png
    ├── 29-archived-artifacts.png
    ├── 30-artifact-fingerprint.png
    ├── 31-jenkinsfile-in-repo.png
    ├── 32-jenkinsfile-stages.png
    ├── 33-dev-vs-test-config.png
    └── 34-multiple-envs-running.png
```

---

## Critical Evidence Requirements

### Minimum Evidence to Prove Task 8 Complete

1. ✅ **Jenkinsfile exists** in repository root
2. ✅ **Pipeline job configured** with SCM
3. ✅ **Parameters visible** in "Build with Parameters"
4. ✅ **DEV deployment successful** with console log
5. ✅ **TEST deployment successful** showing different configuration
6. ✅ **Application accessible** at deployment URL
7. ✅ **Configuration files** show environment-specific settings
8. ✅ **SKIP_DEPLOY works** - Deploy stage skipped
9. ✅ **Artifacts archived** - JAR file visible in builds
10. ✅ **Documentation** complete in repository

---

## Verification Commands

Use these commands to verify deployments:

### Check Running Processes
```powershell
# List Java processes
Get-Process java

# Check ports in use
netstat -ano | findstr "8080 8081 8082" | findstr LISTENING
```

### Check Deployment Files
```powershell
# List DEV deployment
Get-ChildItem "C:\Jenkins\deployments\dev"

# Check DEV config
Get-Content "C:\Jenkins\deployments\dev\application-dev.properties"

# Check DEV logs
Get-Content "C:\Jenkins\deployments\dev\app-dev.log" -Tail 50
```

### Test Application
```powershell
# Test DEV endpoint
Invoke-WebRequest -Uri "http://localhost:8080" -UseBasicParsing | Select-Object StatusCode

# Test TEST endpoint
Invoke-WebRequest -Uri "http://localhost:8081" -UseBasicParsing | Select-Object StatusCode
```

---

## Report Writing Guide

When writing the Task 8 report, include:

### 1. Introduction
- Task 8 objectives
- Pipeline as code benefits
- Parameterized deployment strategy

### 2. Jenkinsfile Design
- Declarative pipeline structure
- Stage explanations
- Parameter definitions
- Environment configuration

### 3. Deployment Strategy
- Spring Boot JAR deployment
- Environment-specific directories
- Configuration management
- Port management

### 4. Testing & Verification
- DEV environment deployment
- TEST environment deployment
- Parameter variation testing
- Skip deployment testing

### 5. Evidence
- Reference screenshots by number
- Include console log excerpts
- Show configuration differences

### 6. Challenges & Solutions
- Document any issues encountered
- Explain resolutions
- Lessons learned

### 7. Conclusion
- Summary of achievements
- Confirmation of deliverables
- Future enhancements (Task 9-10)

---

**Document Version:** 1.0  
**Last Updated:** October 18, 2026  
**Purpose:** Evidence collection guide for Task 8 submission
