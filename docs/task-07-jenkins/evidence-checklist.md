# Task 7: Evidence Collection Checklist

This document lists all screenshots and evidence required for the Task 7 submission report.

## Required Screenshots

### 1. Jenkins Installation and Initial Setup

#### 1.1 Jenkins Unlock Screen
- **File Name:** `01-jenkins-unlock-screen.png`
- **Content:** Initial Jenkins unlock page showing the password prompt
- **URL:** http://localhost:8080

#### 1.2 Plugin Installation Progress
- **File Name:** `02-plugin-installation.png`
- **Content:** Screen showing suggested plugins being installed
- **Shows:** Progress bars and plugin list

#### 1.3 Admin User Creation
- **File Name:** `03-admin-user-creation.png`
- **Content:** Create First Admin User form (with password hidden)
- **Note:** DO NOT show actual password in screenshot

#### 1.4 Jenkins Dashboard (Initial)
- **File Name:** `04-jenkins-dashboard-initial.png`
- **Content:** Jenkins main dashboard after initial setup
- **URL:** http://localhost:8080

---

### 2. Plugin Management

#### 2.1 Installed Plugins List
- **File Name:** `05-installed-plugins.png`
- **Content:** Manage Jenkins → Plugins → Installed plugins
- **Shows:** Git, GitHub, Maven Integration, JUnit plugins
- **Path:** Manage Jenkins → Plugins → Installed tab

---

### 3. Global Tool Configuration

#### 3.1 JDK Configuration
- **File Name:** `06-jdk-configuration.png`
- **Content:** Manage Jenkins → Tools → JDK section
- **Shows:** JDK-21 name and JAVA_HOME path
- **Path:** Manage Jenkins → Tools

#### 3.2 Maven Configuration
- **File Name:** `07-maven-configuration.png`
- **Content:** Manage Jenkins → Tools → Maven section
- **Shows:** Maven-3.9.16 name and MAVEN_HOME path
- **Path:** Manage Jenkins → Tools

#### 3.3 Git Configuration
- **File Name:** `08-git-configuration.png`
- **Content:** Manage Jenkins → Tools → Git section
- **Shows:** Git executable path
- **Path:** Manage Jenkins → Tools

---

### 4. Credentials Configuration (If Applicable)

#### 4.1 Credentials List
- **File Name:** `09-credentials-list.png`
- **Content:** Manage Jenkins → Credentials → Global credentials
- **Shows:** GitHub credentials entry (without revealing token)
- **Path:** Manage Jenkins → Credentials
- **Note:** REDACT any sensitive tokens or passwords

---

### 5. Job Configuration - Source Code Management

#### 5.1 Job Creation
- **File Name:** `10-job-creation.png`
- **Content:** New Item screen with job name `course-feedback-ci`
- **Shows:** Job name and type selection (Maven/Freestyle)

#### 5.2 SCM Configuration
- **File Name:** `11-scm-configuration.png`
- **Content:** Source Code Management section in job config
- **Shows:** 
  - Git selected
  - Repository URL
  - Branch specifier: `*/Dev`
  - Credentials (if private repo)

---

### 6. Job Configuration - Build Triggers

#### 6.1 Build Triggers
- **File Name:** `12-build-triggers.png`
- **Content:** Build Triggers section in job config
- **Shows:**
  - "Poll SCM" checkbox enabled
  - Schedule: `H/5 * * * *`
  - Help text for cron syntax visible

---

### 7. Job Configuration - Build Steps

#### 7.1 Build Configuration
- **File Name:** `13-build-configuration.png`
- **Content:** Build section in job config
- **Shows:**
  - Maven version: `Maven-3.9.16`
  - Goals: `clean package`
  - POM file: `pom.xml`

---

### 8. Job Configuration - Post-Build Actions

#### 8.1 Archive Artifacts
- **File Name:** `14-archive-artifacts.png`
- **Content:** Post-build Actions section
- **Shows:**
  - "Archive the artifacts" configured
  - Files to archive: `target/*.jar`
  - Fingerprint artifacts enabled

#### 8.2 Complete Job Configuration Summary
- **File Name:** `15-job-config-summary.png`
- **Content:** Full job configuration page scrolled to top
- **Shows:** All sections collapsed/visible

---

### 9. Manual Build Execution

#### 9.1 Build Now Action
- **File Name:** `16-build-now-clicked.png`
- **Content:** Job page showing "Build Now" being clicked
- **Shows:** Job dashboard with Build Now button highlighted

#### 9.2 Build Queue
- **File Name:** `17-build-queue.png`
- **Content:** Build queue showing job waiting/executing
- **Location:** Left sidebar during build

#### 9.3 Build History (First Build)
- **File Name:** `18-build-history-first.png`
- **Content:** Build History showing build #1
- **Shows:** 
  - Build number
  - Status (success/failure icon)
  - Build timestamp

#### 9.4 Console Output (Success)
- **File Name:** `19-console-output-success.png`
- **Content:** Full console output of successful build
- **Shows:**
  - Maven build commands
  - "BUILD SUCCESS" message
  - Artifact creation message
  - Duration and timestamp

#### 9.5 Console Output (Top Section)
- **File Name:** `20-console-output-top.png`
- **Content:** Beginning of console output
- **Shows:**
  - "Started by user" message
  - Repository checkout
  - Maven initialization

---

### 10. Build Artifacts

#### 10.1 Build Artifacts List
- **File Name:** `21-build-artifacts.png`
- **Content:** Build page showing "Build Artifacts" section
- **Shows:**
  - JAR file name: `course-feedback-management-system-0.0.1-SNAPSHOT.jar`
  - File size
  - Download link visible
- **Location:** Build page, below console output

#### 10.2 Artifact Fingerprint
- **File Name:** `22-artifact-fingerprint.png`
- **Content:** Fingerprint record for the artifact
- **Shows:** MD5 checksum and usage tracking
- **Location:** Click on "fingerprint" link from artifacts section

---

### 11. SCM Polling Log

#### 11.1 Poll SCM Log (Initial)
- **File Name:** `23-poll-scm-log-initial.png`
- **Content:** Git Polling Log showing no changes initially
- **Location:** Job page → "Git Polling Log" (left sidebar)
- **Shows:** Polling schedule and repository check

---

### 12. SCM-Triggered Build (Evidence of Automation)

#### 12.1 Commit for Trigger
- **File Name:** `24-git-commit-for-trigger.png`
- **Content:** Git terminal showing commit and push
- **Shows:**
  - `git commit` command
  - Commit message: "trigger: test Jenkins CI polling"
  - `git push origin Dev`
  - Push success message

#### 12.2 Build Triggered by SCM
- **File Name:** `25-build-triggered-scm.png`
- **Content:** Build #2 (or higher) in build history
- **Shows:** Newer build number after the commit

#### 12.3 Console Output (SCM Trigger Proof)
- **File Name:** `26-console-scm-trigger-proof.png`
- **Content:** Console output of the SCM-triggered build
- **Shows:** 
  - **"Started by an SCM change"** (CRITICAL - this is the key evidence)
  - Git commit hash that triggered the build
  - Repository polling log

#### 12.4 Poll SCM Log (After Change)
- **File Name:** `27-poll-scm-log-changes.png`
- **Content:** Git Polling Log showing changes detected
- **Shows:**
  - Polling timestamp
  - "Changes found" message
  - Commit details

---

### 13. Multiple Successful Builds

#### 13.1 Build History (Multiple Builds)
- **File Name:** `28-build-history-multiple.png`
- **Content:** Build History showing at least 2 successful builds
- **Shows:**
  - Build #1 (manual trigger)
  - Build #2 (SCM trigger)
  - All showing success status (blue/green icons)

#### 13.2 Job Dashboard Overview
- **File Name:** `29-job-dashboard-overview.png`
- **Content:** Complete job page with all information
- **Shows:**
  - Build history graph
  - Last successful build
  - Last failed build (if any)
  - Build stability/weather indicator

---

### 14. System Information (Optional but Recommended)

#### 14.1 Jenkins System Information
- **File Name:** `30-jenkins-system-info.png`
- **Content:** Manage Jenkins → System Information
- **Shows:**
  - Jenkins version
  - Java version
  - OS details
  - JENKINS_HOME location

#### 14.2 Jenkins Version
- **File Name:** `31-jenkins-version.png`
- **Content:** Bottom right of Jenkins page
- **Shows:** Jenkins version number

---

## Evidence Files Checklist

In addition to screenshots, collect these files:

### Build Logs
- ✅ `build-log-manual.txt` - Full console output from manual build (#1)
- ✅ `build-log-scm-trigger.txt` - Full console output from SCM-triggered build (#2)

### Configuration Files
- ✅ `config.xml` - Exported Jenkins job configuration
- ✅ `polling-log.txt` - Git polling log showing schedule and detection

### Verification Commands
- ✅ `command-output.txt` - Output of verification commands:
  ```powershell
  java -version
  mvn -version
  git --version
  Get-Service Jenkins
  ```

---

## Screenshot Capture Tips

### Windows Screenshot Tools
- **Snipping Tool:** Press `Win + Shift + S`
- **Full Screen:** Press `PrtScn` or `Win + PrtScn`
- **Active Window:** Press `Alt + PrtScn`
- **Game Bar:** Press `Win + G` (for recording)

### Best Practices
1. **Capture full browser window** including URL bar
2. **Use high resolution** for readability
3. **Avoid capturing sensitive data** (passwords, tokens)
4. **Add timestamps** where visible (Jenkins shows timestamps)
5. **Clear naming** using the suggested file names
6. **Organize in folders** by section
7. **Redact secrets** if accidentally captured (use editing tool)

---

## Critical Evidence Requirements

### Minimum Evidence to Prove Task 7 Complete

1. ✅ **Jenkins Running:** Dashboard screenshot
2. ✅ **Plugins Installed:** Plugin list screenshot
3. ✅ **Tools Configured:** JDK, Maven, Git configuration screenshots
4. ✅ **Job Exists:** Job configuration screenshots
5. ✅ **SCM Configured:** Repository and branch configuration
6. ✅ **Polling Enabled:** Build trigger showing `H/5 * * * *`
7. ✅ **Manual Build Success:** Console log with "BUILD SUCCESS"
8. ✅ **SCM Build Success:** Console log with "Started by an SCM change"
9. ✅ **Artifacts Archived:** Build artifacts section showing JAR file
10. ✅ **Config Exported:** `config.xml` file in repository

---

## Submission Package Structure

```
docs/task-07-jenkins/
├── jenkins-setup.md              (This setup guide)
├── evidence-checklist.md         (This checklist)
├── build-log.txt                 (Console output)
├── config.xml                    (Job configuration)
├── screenshots/
│   ├── 01-installation/
│   │   ├── 01-jenkins-unlock-screen.png
│   │   ├── 02-plugin-installation.png
│   │   ├── 03-admin-user-creation.png
│   │   └── 04-jenkins-dashboard-initial.png
│   ├── 02-configuration/
│   │   ├── 05-installed-plugins.png
│   │   ├── 06-jdk-configuration.png
│   │   ├── 07-maven-configuration.png
│   │   └── 08-git-configuration.png
│   ├── 03-job-config/
│   │   ├── 10-job-creation.png
│   │   ├── 11-scm-configuration.png
│   │   ├── 12-build-triggers.png
│   │   ├── 13-build-configuration.png
│   │   └── 14-archive-artifacts.png
│   ├── 04-builds/
│   │   ├── 16-build-now-clicked.png
│   │   ├── 18-build-history-first.png
│   │   ├── 19-console-output-success.png
│   │   ├── 21-build-artifacts.png
│   │   ├── 25-build-triggered-scm.png
│   │   └── 26-console-scm-trigger-proof.png
│   └── 05-verification/
│       ├── 28-build-history-multiple.png
│       └── 29-job-dashboard-overview.png
└── verification/
    ├── build-log-manual.txt
    ├── build-log-scm-trigger.txt
    └── command-output.txt
```

---

## Report Writing Guide

When writing the Task 7 report, include:

### 1. Introduction
- Task 7 objectives
- Deliverables overview

### 2. Installation Process
- OS and environment details
- Jenkins installation method chosen
- Initial setup steps
- Challenges encountered and solutions

### 3. Configuration
- Plugin installation rationale
- Tool configuration (JDK, Maven, Git)
- Credentials setup (if applicable)

### 4. Job Creation
- Job type selection reasoning
- SCM configuration details
- Build trigger configuration
- Build steps explanation
- Post-build actions setup

### 5. Testing and Verification
- Manual build execution and results
- SCM polling configuration testing
- Automated build trigger demonstration
- Artifact archiving verification

### 6. Evidence
- Reference screenshots by number
- Include console log excerpts
- Show artifact creation proof

### 7. Challenges and Solutions
- Document any issues faced
- Explain how they were resolved
- Lessons learned

### 8. Conclusion
- Summary of achievements
- Confirmation of deliverables
- Future CI/CD enhancements (Task 8 preview)

---

**Document Version:** 1.0  
**Last Updated:** October 18, 2026  
**Purpose:** Evidence collection guide for Task 7 submission
