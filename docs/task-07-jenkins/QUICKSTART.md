# Task 7: Quick Start Guide

**Time Required:** 30-45 minutes  
**Prerequisites:** Java 21, Maven 3.9.16, Git installed

## Installation (5 minutes)

### Download Jenkins
```powershell
# Download Windows installer
Invoke-WebRequest -Uri "https://get.jenkins.io/windows-stable/2.479.3/jenkins.msi" -OutFile "$env:USERPROFILE\Downloads\jenkins.msi"

# Or visit: https://www.jenkins.io/download/
```

### Install
```powershell
# Run installer
Start-Process "$env:USERPROFILE\Downloads\jenkins.msi"

# Or double-click the downloaded file
# Accept defaults, click through installer
```

### Start Jenkins
```powershell
# Start the service
Start-Service Jenkins

# Or use Services.msc GUI
```

### Access
Open browser: **http://localhost:8080**

---

## Initial Setup (10 minutes)

### 1. Unlock Jenkins
```powershell
# Get initial password
Get-Content "C:\ProgramData\Jenkins\.jenkins\secrets\initialAdminPassword"

# Copy password and paste into unlock screen
```

### 2. Install Plugins
- Select: **Install suggested plugins**
- Wait 5-10 minutes

### 3. Create Admin User
- Username: `admin`
- Password: [choose secure password]
- Full Name: Your Name
- Email: your-email@example.com
- Click **Save and Continue**

### 4. Instance Configuration
- Jenkins URL: `http://localhost:8080/`
- Click **Save and Finish**
- Click **Start using Jenkins**

---

## Configure Tools (5 minutes)

### Navigate
**Manage Jenkins → Tools**

### Add JDK
1. Click **Add JDK**
2. Name: `JDK-21`
3. Uncheck "Install automatically"
4. JAVA_HOME: `C:\Users\RAJ JAISWAR\OneDrive\Desktop\DevTools\jdk-21.0.12+8`

### Add Maven
1. Click **Add Maven**
2. Name: `Maven-3.9.16`
3. Uncheck "Install automatically"
4. MAVEN_HOME: `C:\Users\RAJ JAISWAR\OneDrive\Desktop\DevTools\apache-maven-3.9.16`

### Save
Click **Save** at bottom

---

## Create CI Job (10 minutes)

### 1. Create Job
- Dashboard → **New Item**
- Name: `course-feedback-ci`
- Type: **Maven project**
- Click **OK**

### 2. Source Code Management
- Select **Git**
- Repository URL: `https://github.com/rajjaiswar82/-Course-Feedback-Management-System.git`
- Branch Specifier: `*/Dev`

### 3. Build Triggers
- ✅ Check **Poll SCM**
- Schedule: `H/5 * * * *`

### 4. Build
- Goals and options: `clean package`

### 5. Post-build Actions
- Add: **Archive the artifacts**
- Files to archive: `target/*.jar`
- ✅ Check **Fingerprint artifacts**

### 6. Save
Click **Save**

---

## Test (10 minutes)

### Manual Build
1. Job page → **Build Now**
2. Wait for build to complete
3. Click build number (e.g., #1)
4. Click **Console Output**
5. Verify: **BUILD SUCCESS**

### Test Trigger
```powershell
# Navigate to repo
cd "C:\Users\RAJ JAISWAR\OneDrive\Documents\Devops\course-feedback-git"

# Make small change
echo "# Test Jenkins trigger" >> README.md

# Commit and push
git add README.md
git commit -m "trigger: test Jenkins CI polling"
git push origin Dev
```

### Verify Automatic Build
1. Wait up to 5 minutes
2. Check Jenkins for new build
3. Click build number
4. Console Output should show: **"Started by an SCM change"**

---

## Verify Deliverables

✅ Jenkins running at http://localhost:8080  
✅ Job `course-feedback-ci` exists  
✅ Manual build successful  
✅ Automatic build triggered by commit  
✅ Artifact visible: `course-feedback-management-system-0.0.1-SNAPSHOT.jar`  

---

## Collect Evidence

### Screenshots Needed
1. Jenkins dashboard
2. Job configuration pages
3. Console output (manual build)
4. Console output (SCM-triggered build) - shows "Started by an SCM change"
5. Build artifacts page

### Export Configuration
```powershell
# Copy job config
Copy-Item "C:\ProgramData\Jenkins\.jenkins\jobs\course-feedback-ci\config.xml" -Destination "C:\Users\RAJ JAISWAR\OneDrive\Documents\Devops\course-feedback-git\docs\task-07-jenkins\config-actual.xml"
```

**See `evidence-checklist.md` for complete list**

---

## Troubleshooting

### Jenkins Won't Start
```powershell
# Check service
Get-Service Jenkins

# Restart
Restart-Service Jenkins

# Check logs
Get-Content "C:\ProgramData\Jenkins\.jenkins\logs\jenkins.log" -Tail 20
```

### Build Fails
- Verify Java 21: `java -version`
- Verify Maven: `mvn -version`
- Check tool paths in Jenkins
- Restart Jenkins after tool config changes

### No Automatic Build
- Check **Poll SCM Log** (left menu in job)
- Verify branch name: `*/Dev`
- Ensure repository accessible
- Wait full 5 minutes after commit

---

## Next Steps

1. ✅ Complete Task 7 setup
2. 📸 Collect all screenshots
3. 📄 Update `build-log.txt` with actual output
4. 🔀 Push changes and create PR
5. ✅ Merge to Dev after review

---

## Commands Summary

```powershell
# Verify prerequisites
java -version      # Should show 21.0.12
mvn -version       # Should show 3.9.16
git --version      # Any recent version

# Jenkins service
Start-Service Jenkins
Get-Service Jenkins
Restart-Service Jenkins

# Test local build
cd "C:\Users\RAJ JAISWAR\OneDrive\Documents\Devops\course-feedback-git"
mvn clean package

# Access Jenkins
Start-Process "http://localhost:8080"

# Get initial password
Get-Content "C:\ProgramData\Jenkins\.jenkins\secrets\initialAdminPassword"
```

---

**Total Time:** ~40 minutes  
**Difficulty:** Beginner  
**Support:** See `jenkins-setup.md` for detailed instructions
