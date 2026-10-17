# Task 7: Manual Steps Required

## Overview

Task 7 documentation has been created and committed to the repository. However, Jenkins installation and configuration **cannot be automated** and must be performed manually. This document provides the exact steps you need to complete.

---

## What Has Been Done (Automated)

✅ Created comprehensive documentation:
- `jenkins-setup.md` - Complete installation guide
- `evidence-checklist.md` - Screenshot requirements
- `config.xml` - Sample Jenkins job configuration
- `build-log.txt` - Expected build log format
- `README.md` - Task overview and troubleshooting
- `QUICKSTART.md` - Quick reference guide

✅ Git workflow:
- Created branch: `feature/task-07-jenkins-ci`
- Committed all documentation
- Pushed to remote repository

✅ Project verification:
- Maven build tested locally: **SUCCESS** ✅
- Artifact verified: `course-feedback-management-system-0.0.1-SNAPSHOT.jar` (58 MB)

---

## What You Must Do Manually

### STEP 1: Install Jenkins (Required)

**Time:** 10-15 minutes

#### Option A: Windows Installer (Recommended)
```powershell
# Download Jenkins Windows installer
# Visit: https://www.jenkins.io/download/
# Download jenkins.msi and install

# Or download via PowerShell:
Invoke-WebRequest -Uri "https://get.jenkins.io/windows-stable/2.479.3/jenkins.msi" -OutFile "$env:USERPROFILE\Downloads\jenkins.msi"

# Run installer
Start-Process "$env:USERPROFILE\Downloads\jenkins.msi"
```

#### Option B: WAR File
```powershell
# Download Jenkins WAR
Invoke-WebRequest -Uri "https://get.jenkins.io/war-stable/2.479.3/jenkins.war" -OutFile "jenkins.war"

# Run Jenkins
java -jar jenkins.war --httpPort=8080
```

#### Start Jenkins Service
```powershell
Start-Service Jenkins
```

#### Access Jenkins
Open browser: **http://localhost:8080**

---

### STEP 2: Initial Setup (Required)

**Time:** 10 minutes

#### 2.1 Unlock Jenkins
```powershell
# Get initial password
Get-Content "C:\ProgramData\Jenkins\.jenkins\secrets\initialAdminPassword"
```
- Copy the password
- Paste into Jenkins unlock screen

#### 2.2 Install Plugins
- Select: **"Install suggested plugins"**
- Wait for installation to complete (~5-10 minutes)

#### 2.3 Create Admin User
Fill in form:
- Username: `admin` (or your choice)
- Password: [create secure password]
- Full Name: Your Name
- Email: your-email@example.com
- Click **Save and Continue**

#### 2.4 Instance Configuration
- Jenkins URL: `http://localhost:8080/`
- Click **Save and Finish**
- Click **Start using Jenkins**

---

### STEP 3: Configure Tools (Required)

**Time:** 5 minutes

#### Navigate to Tools
**Manage Jenkins → Tools**

#### 3.1 Configure JDK
1. Scroll to **JDK** section
2. Click **Add JDK**
3. Name: `JDK-21`
4. **Uncheck** "Install automatically"
5. JAVA_HOME: `C:\Users\RAJ JAISWAR\OneDrive\Desktop\DevTools\jdk-21.0.12+8`
6. (Verify path with `mvn -version` output)

#### 3.2 Configure Maven
1. Scroll to **Maven** section
2. Click **Add Maven**
3. Name: `Maven-3.9.16`
4. **Uncheck** "Install automatically"
5. MAVEN_HOME: `C:\Users\RAJ JAISWAR\OneDrive\Desktop\DevTools\apache-maven-3.9.16`
6. (Verify path with `mvn -version` output)

#### 3.3 Verify Git
1. Scroll to **Git** section
2. Should auto-detect: `C:\Program Files\Git\bin\git.exe`
3. If not, add manually

#### Save Configuration
Click **Save** at bottom of page

---

### STEP 4: Create CI Job (Required)

**Time:** 10 minutes

#### 4.1 Create New Job
1. From Jenkins dashboard, click **New Item**
2. Enter name: `course-feedback-ci`
3. Select: **Maven project**
4. Click **OK**

#### 4.2 Configure Source Code Management
**Section: Source Code Management**
1. Select **Git**
2. Repository URL: `https://github.com/rajjaiswar82/-Course-Feedback-Management-System.git`
3. Credentials: (leave as "none" if public repo)
4. Branches to build:
   - Branch Specifier: `*/Dev`

#### 4.3 Configure Build Triggers
**Section: Build Triggers**
1. ✅ Check **Poll SCM**
2. Schedule: `H/5 * * * *`
   (This polls GitHub every 5 minutes)

#### 4.4 Configure Build
**Section: Build**
- Goals and options: `clean package`
- (POM should auto-detect as `pom.xml`)

#### 4.5 Configure Post-build Actions
**Section: Post-build Actions**
1. Click **Add post-build action**
2. Select **Archive the artifacts**
3. Files to archive: `target/*.jar`
4. ✅ Check **Fingerprint artifacts**

#### Save Job
Click **Save** at bottom

---

### STEP 5: Test Manual Build (Required)

**Time:** 5 minutes

#### 5.1 Trigger Build
1. From job page `course-feedback-ci`, click **Build Now**
2. Watch Build History (left sidebar)
3. Click on build number (e.g., `#1`)

#### 5.2 Verify Success
1. Click **Console Output**
2. Scroll to bottom
3. Should show: **BUILD SUCCESS**
4. Should show: Archiving artifacts

#### 5.3 Verify Artifact
1. Back to build page
2. Section: **Build Artifacts**
3. Should show: `course-feedback-management-system-0.0.1-SNAPSHOT.jar`

---

### STEP 6: Test SCM Trigger (Required)

**Time:** 10 minutes

#### 6.1 Make a Commit
```powershell
# Navigate to repository
cd "C:\Users\RAJ JAISWAR\OneDrive\Documents\Devops\course-feedback-git"

# Ensure on correct branch
git checkout feature/task-07-jenkins-ci

# Make trivial change
echo "# Jenkins CI test" >> README.md

# Commit and push
git add README.md
git commit -m "trigger: test Jenkins CI polling"
git push origin feature/task-07-jenkins-ci
```

#### 6.2 Wait for Polling
- Wait up to **5 minutes**
- Jenkins polls every 5 minutes with `H/5 * * * *`

#### 6.3 Verify Automatic Build
1. Check Jenkins dashboard
2. New build should appear (e.g., `#2`)
3. Click on build number
4. Click **Console Output**
5. **CRITICAL:** First line should show: **"Started by an SCM change"**
   (This is the key evidence for automatic triggering!)

#### 6.4 Check Poll SCM Log
1. From job page, left sidebar
2. Click **Git Polling Log**
3. Should show polling schedule and detected changes

---

### STEP 7: Collect Evidence (Required for Report)

**Time:** 20-30 minutes

#### 7.1 Take Screenshots
Follow the detailed list in `evidence-checklist.md`. Minimum required:

1. **Jenkins Dashboard** - Main page showing job
2. **Job Configuration** - SCM, triggers, build, post-build sections
3. **Manual Build Console** - Full output with "BUILD SUCCESS"
4. **SCM-Triggered Build Console** - Showing "Started by an SCM change"
5. **Build Artifacts** - Page showing JAR file
6. **Tool Configuration** - JDK and Maven settings
7. **Plugin List** - Installed plugins
8. **Build History** - Multiple successful builds

#### 7.2 Export Configuration
```powershell
# Copy actual Jenkins job configuration
Copy-Item "C:\ProgramData\Jenkins\.jenkins\jobs\course-feedback-ci\config.xml" `
  -Destination "C:\Users\RAJ JAISWAR\OneDrive\Documents\Devops\course-feedback-git\docs\task-07-jenkins\config-actual.xml"

# Or manually navigate to:
# C:\ProgramData\Jenkins\.jenkins\jobs\course-feedback-ci\config.xml
```

#### 7.3 Capture Build Log
1. Go to successful build (e.g., build #1)
2. Click **Console Output**
3. Select all text (Ctrl+A)
4. Copy (Ctrl+C)
5. Open: `docs/task-07-jenkins/build-log.txt`
6. Replace template with actual output
7. Save file

#### 7.4 Commit Evidence
```powershell
cd "C:\Users\RAJ JAISWAR\OneDrive\Documents\Devops\course-feedback-git"

# Add updated files
git add docs/task-07-jenkins/build-log.txt
git add docs/task-07-jenkins/config-actual.xml

# Commit
git commit -m "docs: add actual Jenkins build log and configuration"

# Push
git push origin feature/task-07-jenkins-ci
```

---

### STEP 8: Create Pull Request (Required)

**Time:** 5 minutes

#### Using GitHub CLI (if installed)
```powershell
gh pr create `
  --base Dev `
  --head feature/task-07-jenkins-ci `
  --title "Task 7: Jenkins installation and CI job" `
  --body "## Task 7 Deliverables

- ✅ Jenkins installed and configured
- ✅ Required plugins installed (Git, GitHub, Maven, JUnit)
- ✅ Global tools configured (JDK-21, Maven-3.9.16)
- ✅ CI job created: course-feedback-ci
- ✅ SCM configured with Dev branch
- ✅ Poll SCM trigger: H/5 * * * *
- ✅ Maven build: clean package
- ✅ Artifact archiving configured
- ✅ Manual build successful
- ✅ Automatic build triggered by SCM change
- ✅ Build artifacts archived and verified
- ✅ Documentation complete
- ✅ Evidence collected

## Documentation
- jenkins-setup.md: Complete setup guide
- evidence-checklist.md: Screenshot requirements
- build-log.txt: Successful build output
- config.xml: Jenkins job configuration
- README.md: Overview and troubleshooting
- QUICKSTART.md: Quick reference

## Testing
Manual build and SCM-triggered build both successful.
Artifacts: course-feedback-management-system-0.0.1-SNAPSHOT.jar (58 MB)

## Evidence
All screenshots and logs collected per evidence-checklist.md

Ready for review and merge to Dev."
```

#### Using GitHub Web Interface
1. Open: https://github.com/rajjaiswar82/-Course-Feedback-Management-System
2. GitHub should show banner: "Compare & pull request"
3. Click **Compare & pull request**
4. Base: `Dev`
5. Compare: `feature/task-07-jenkins-ci`
6. Title: `Task 7: Jenkins installation and CI job`
7. Description: (paste the body content from above)
8. Click **Create pull request**

---

### STEP 9: Merge and Cleanup (After Review)

#### Merge Pull Request
1. Review PR in GitHub
2. Ensure all checks pass
3. Click **Merge pull request**
4. Click **Confirm merge**
5. Click **Delete branch** (on GitHub)

#### Local Cleanup
```powershell
cd "C:\Users\RAJ JAISWAR\OneDrive\Documents\Devops\course-feedback-git"

# Switch to Dev
git checkout Dev

# Pull merged changes
git pull origin Dev

# Delete local feature branch
git branch -d feature/task-07-jenkins-ci

# Verify
git branch
```

---

## Verification Checklist

Before creating the PR, verify all deliverables:

### Jenkins Installation
- [ ] Jenkins accessible at http://localhost:8080
- [ ] Admin user created
- [ ] Suggested plugins installed

### Plugin Installation
- [ ] Git plugin installed
- [ ] GitHub plugin installed
- [ ] Maven Integration plugin installed
- [ ] JUnit plugin installed

### Tool Configuration
- [ ] JDK-21 configured with correct path
- [ ] Maven-3.9.16 configured with correct path
- [ ] Git auto-detected or configured

### Job Configuration
- [ ] Job name: `course-feedback-ci`
- [ ] SCM: Git with correct repository URL
- [ ] Branch: `*/Dev`
- [ ] Build trigger: Poll SCM `H/5 * * * *`
- [ ] Build: `mvn clean package`
- [ ] Post-build: Archive artifacts `target/*.jar`
- [ ] Fingerprinting enabled

### Build Verification
- [ ] Manual build (#1) successful
- [ ] Console shows "BUILD SUCCESS"
- [ ] Artifact archived: `course-feedback-management-system-0.0.1-SNAPSHOT.jar`

### Trigger Verification
- [ ] Commit made to Dev branch
- [ ] Automatic build triggered (e.g., #2)
- [ ] Console shows "Started by an SCM change"
- [ ] Poll SCM log shows changes detected

### Documentation
- [ ] jenkins-setup.md complete
- [ ] evidence-checklist.md complete
- [ ] config.xml included
- [ ] build-log.txt updated with actual output
- [ ] README.md complete
- [ ] QUICKSTART.md complete

### Evidence Collection
- [ ] All required screenshots captured
- [ ] Screenshots organized by category
- [ ] Build logs exported
- [ ] Job configuration exported
- [ ] No passwords or tokens in committed files

---

## Expected Time Investment

| Step | Activity | Time |
|------|----------|------|
| 1 | Jenkins Installation | 10-15 min |
| 2 | Initial Setup | 10 min |
| 3 | Tool Configuration | 5 min |
| 4 | Job Creation | 10 min |
| 5 | Manual Build Test | 5 min |
| 6 | SCM Trigger Test | 10 min |
| 7 | Evidence Collection | 20-30 min |
| 8 | Create PR | 5 min |
| **Total** | | **75-95 min** |

---

## Support Resources

### Documentation Files
- `jenkins-setup.md` - Detailed step-by-step guide
- `QUICKSTART.md` - Quick reference
- `evidence-checklist.md` - Screenshot requirements
- `README.md` - Overview and troubleshooting

### Troubleshooting
Refer to `jenkins-setup.md` **Troubleshooting** section for:
- Service startup issues
- Build failures
- Tool configuration problems
- SCM polling issues

### External Resources
- Jenkins Official Docs: https://www.jenkins.io/doc/
- Maven Plugin: https://plugins.jenkins.io/maven-plugin/
- Git Plugin: https://plugins.jenkins.io/git/

---

## Security Reminders

⚠️ **DO NOT COMMIT:**
- Initial admin password
- User passwords
- GitHub tokens/credentials
- Jenkins secrets directory
- `secrets/` folder contents

✅ **SAFE TO COMMIT:**
- Documentation files
- Sanitized configuration (config.xml)
- Build logs (no sensitive info)
- Screenshots (with secrets redacted)

---

## Contact

For issues or questions:
- Review documentation in `docs/task-07-jenkins/`
- Check troubleshooting section
- Consult course materials
- Ask instructor/TA

---

**Status:** Documentation Complete - Manual Setup Required  
**Branch:** feature/task-07-jenkins-ci  
**Last Updated:** October 18, 2026
