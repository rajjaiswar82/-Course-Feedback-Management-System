# Task 7: Jenkins Installation and Configuration Guide

## Project Build Information

**Build Tool:** Maven 3.9.16  
**Java Version:** Java 21 (OpenJDK 21.0.12)  
**Packaging Type:** JAR (Spring Boot executable)  
**Artifact Location:** `target/course-feedback-management-system-0.0.1-SNAPSHOT.jar`  
**Build Command:** `mvn clean package`

---

## STEP 1: Install Jenkins on Windows

### Prerequisites Check

Verify that Java 21 is installed:
```powershell
java -version
# Expected: openjdk version "21.0.12" or higher
```

Verify that Maven is installed:
```powershell
mvn -version
# Expected: Apache Maven 3.9.16 or higher
```

### Installation Options

#### Option 1: Windows Installer (Recommended for Windows)

1. **Download Jenkins:**
   - Visit: https://www.jenkins.io/download/
   - Download the Windows installer (.msi file) from the "Generic Java package (.war)" or "Windows" section
   - For Windows, download the Jenkins LTS installer

2. **Run the Installer:**
   ```powershell
   # Navigate to downloads and run the installer
   # Or download directly:
   Invoke-WebRequest -Uri "https://get.jenkins.io/windows-stable/2.479.3/jenkins.msi" -OutFile "jenkins.msi"
   Start-Process msiexec.exe -Wait -ArgumentList '/I jenkins.msi /quiet'
   ```

3. **Jenkins will be installed as a Windows Service:**
   - Default installation path: `C:\Program Files\Jenkins`
   - Default port: 8080
   - Service name: Jenkins
   - JENKINS_HOME: `C:\ProgramData\Jenkins\.jenkins`

4. **Start Jenkins Service:**
   ```powershell
   Start-Service Jenkins
   # Or use Services.msc GUI
   ```

5. **Access Jenkins:**
   - Open browser: http://localhost:8080

#### Option 2: Run Jenkins WAR File

If you prefer to run Jenkins without installing as a service:

```powershell
# Download Jenkins WAR
Invoke-WebRequest -Uri "https://get.jenkins.io/war-stable/2.479.3/jenkins.war" -OutFile "jenkins.war"

# Run Jenkins (keeps terminal open)
java -jar jenkins.war --httpPort=8080
```

#### Option 3: Docker (Alternative)

If Docker is available:

```powershell
docker pull jenkins/jenkins:lts
docker run -d -p 8080:8080 -p 50000:50000 -v jenkins_home:/var/jenkins_home --name jenkins jenkins/jenkins:lts
```

---

## STEP 2: Initial Jenkins Setup

### Unlock Jenkins

1. **Retrieve the initial admin password:**

   For Windows Installer:
   ```powershell
   Get-Content "C:\ProgramData\Jenkins\.jenkins\secrets\initialAdminPassword"
   ```

   For WAR file or Docker:
   ```powershell
   # Check console output or:
   Get-Content "$env:USERPROFILE\.jenkins\secrets\initialAdminPassword"
   ```

2. **Paste the password** in the Jenkins unlock screen at http://localhost:8080

### Install Plugins

1. **Select:** "Install suggested plugins" (recommended)
   - This will install essential plugins including Git, Pipeline, etc.

2. **Wait for plugin installation** to complete (may take 5-10 minutes)

### Create Admin User

1. Fill in the admin user creation form:
   - Username: `admin` (or your preference)
   - Password: `[secure password]`
   - Full name: Your name
   - Email: your-email@example.com

2. **Instance Configuration:**
   - Jenkins URL: http://localhost:8080/ (or your server's URL)
   - Click "Save and Finish"

3. **Jenkins is ready!** Click "Start using Jenkins"

---

## STEP 3: Install Required Plugins

Navigate to **Manage Jenkins → Plugins → Available plugins** and install:

### Required Plugins for Task 7

✅ **Git Plugin** (usually pre-installed)  
✅ **GitHub Plugin** (for GitHub integration)  
✅ **Pipeline Plugin** (usually pre-installed)  
✅ **Maven Integration Plugin**  
✅ **JUnit Plugin** (for test results)  

### How to Install:

1. Go to **Manage Jenkins → Plugins**
2. Click **Available plugins** tab
3. Search for each plugin
4. Check the box next to each plugin
5. Click **Install** (select "Restart Jenkins when installation is complete")

---

## STEP 4: Configure Global Tools

Navigate to **Manage Jenkins → Tools**

### Configure JDK

1. Click **Add JDK**
2. **Name:** `JDK-21`
3. **Uncheck** "Install automatically"
4. **JAVA_HOME:** `C:\Users\RAJ JAISWAR\OneDrive\Desktop\DevTools\jdk-21.0.12+8`
   (Verify path with: `where java` then navigate up to JDK root)

### Configure Maven

1. Click **Add Maven**
2. **Name:** `Maven-3.9.16`
3. **Uncheck** "Install automatically"
4. **MAVEN_HOME:** `C:\Users\RAJ JAISWAR\OneDrive\Desktop\DevTools\apache-maven-3.9.16`
   (Verify path with: `mvn -version` output for "Maven home")

### Configure Git

1. **Git** section should auto-detect system Git
2. **Name:** `Default`
3. **Path to Git executable:** Usually auto-detected (e.g., `C:\Program Files\Git\bin\git.exe`)

Click **Save**

---

## STEP 5: Configure GitHub Credentials (If Private Repo)

If your repository is private, you need to add GitHub credentials:

### Create GitHub Personal Access Token

1. Go to GitHub: https://github.com/settings/tokens
2. Click **Generate new token (classic)**
3. Select scopes:
   - ✅ `repo` (full control of private repositories)
4. Generate token and **copy it immediately** (won't be shown again)

### Add Credentials to Jenkins

1. Navigate to **Manage Jenkins → Credentials**
2. Click **(global)** domain
3. Click **Add Credentials**
4. Configure:
   - **Kind:** Username with password
   - **Username:** Your GitHub username
   - **Password:** Paste the Personal Access Token
   - **ID:** `github-credentials`
   - **Description:** GitHub Personal Access Token
5. Click **Create**

---

## STEP 6: Create the CI Job

### Create New Job

1. From Jenkins dashboard, click **New Item**
2. **Job Name:** `course-feedback-ci`
3. **Type:** Select **Maven project** (if available) or **Freestyle project**
4. Click **OK**

### Configure Source Code Management

**Section: Source Code Management**

1. Select **Git**
2. **Repository URL:** 
   ```
   https://github.com/rajjaiswar82/-Course-Feedback-Management-System.git
   ```
3. **Credentials:** 
   - If public: none
   - If private: Select `github-credentials`
4. **Branches to build:**
   - **Branch Specifier:** `*/Dev`
   (This ensures Jenkins builds from the Dev branch where the MVP code exists)

### Configure Build Triggers

**Section: Build Triggers**

✅ **Check:** Poll SCM

**Schedule:** 
```
H/5 * * * *
```

**Explanation:**
- Polls GitHub every 5 minutes for changes
- `H/5` uses Jenkins hash to distribute load
- Format: `MINUTE HOUR DOM MONTH DOW`

**Alternative (for later):** GitHub webhook for instant triggering

### Configure Build Environment (Optional)

**Section: Build Environment**

- ✅ **Delete workspace before build starts** (optional, for clean builds)

### Configure Build

**Section: Build (for Maven Project type)**

**Goals and options:**
```
clean package
```

**OR for Freestyle Project:**

1. **Add build step:** Invoke top-level Maven targets
2. **Maven Version:** `Maven-3.9.16`
3. **Goals:** `clean package`
4. **POM:** `pom.xml`

### Configure Post-Build Actions

**Section: Post-build Actions**

1. **Add post-build action:** Archive the artifacts
   - **Files to archive:** `target/*.jar`
   - ✅ **Check:** Use default excludes
   - ✅ **Check:** Fingerprint artifacts (optional but recommended)

2. **Add post-build action:** Publish JUnit test result report (optional)
   - **Test report XMLs:** `target/surefire-reports/*.xml`
   - ✅ **Check:** Retain long standard output/error

3. **Add post-build action:** Build other projects (optional for pipeline)

Click **Save**

---

## STEP 7: Run the Job

### Manual Build (First Test)

1. From the `course-feedback-ci` job page
2. Click **Build Now**
3. Watch the **Build Queue** and then **Build History**
4. Click on the build number (e.g., `#1`)
5. Click **Console Output** to view the build log
6. Verify:
   - ✅ Build successful
   - ✅ Maven compiles code
   - ✅ JAR artifact created
   - ✅ Artifact appears under "Build Artifacts"

### Trigger via SCM Change

1. Make a small change to the repository on the `Dev` branch:
   ```powershell
   # Example: Update README
   echo "" >> README.md
   git add README.md
   git commit -m "trigger: test Jenkins CI polling"
   git push origin Dev
   ```

2. Wait 5 minutes (or less) for Jenkins to poll and detect the change

3. Verify in Jenkins:
   - New build triggered automatically
   - Console output shows: **"Started by an SCM change"**

---

## STEP 8: Verify Deliverables

### Checklist

✅ **Jenkins Installed:** Service running on http://localhost:8080  
✅ **Plugins Installed:** Git, GitHub, Maven Integration, JUnit  
✅ **Tools Configured:** JDK-21, Maven-3.9.16, Git  
✅ **Job Created:** `course-feedback-ci`  
✅ **SCM Configured:** Git repository, `*/Dev` branch  
✅ **Build Trigger:** Poll SCM `H/5 * * * *`  
✅ **Build Steps:** `mvn clean package`  
✅ **Artifacts Archived:** `target/*.jar` with fingerprinting  
✅ **Manual Build:** Successful with console log  
✅ **Automatic Build:** Triggered by SCM change  
✅ **Build Artifacts:** JAR visible on build page  

---

## Polling Schedule Explanation

**Cron Syntax:** `H/5 * * * *`

- `H/5` = Every 5 minutes (H uses hash for load distribution)
- `*` = Every hour
- `*` = Every day of month
- `*` = Every month
- `*` = Every day of week

**Alternative Schedules:**

- Every 10 minutes: `H/10 * * * *`
- Every 15 minutes: `H/15 * * * *`
- Every hour: `H * * * *`
- Every 4 hours: `H H/4 * * *`

---

## GitHub Webhook Alternative (Future Enhancement)

For instant build triggering instead of polling:

1. **Jenkins:** 
   - In job configuration, enable **"GitHub hook trigger for GITScm polling"**

2. **GitHub Repository:**
   - Settings → Webhooks → Add webhook
   - Payload URL: `http://your-jenkins-server:8080/github-webhook/`
   - Content type: `application/json`
   - Events: Just the push event
   - Active: ✅

**Note:** Requires Jenkins to be publicly accessible or use tools like ngrok for local testing.

---

## Troubleshooting

### Jenkins Service Won't Start
```powershell
# Check service status
Get-Service Jenkins

# Restart service
Restart-Service Jenkins

# Check Jenkins logs
Get-Content "C:\ProgramData\Jenkins\.jenkins\logs\jenkins.log" -Tail 50
```

### Build Fails - Maven Not Found
- Verify Maven path in Global Tool Configuration
- Ensure MAVEN_HOME points to Maven installation directory
- Restart Jenkins after configuration changes

### Build Fails - Java Version Mismatch
- Ensure JDK-21 is configured in Global Tools
- Verify JAVA_HOME points to JDK 21 installation
- Check pom.xml specifies Java 21 in properties

### SCM Polling Not Triggering
- Verify repository URL is correct
- Check credentials if private repository
- Ensure branch specifier matches pushed branch
- Review polling log: Job → Poll SCM Log (in left menu)

### Artifact Not Archived
- Verify path `target/*.jar` matches actual artifact location
- Check console output for "Archiving artifacts" message
- Ensure build completed successfully before archiving

---

## Security Notes

⚠️ **DO NOT COMMIT:**
- Initial admin password
- GitHub personal access tokens
- Jenkins credentials XML files
- JENKINS_HOME directory contents

✅ **Safe to commit:**
- This setup documentation
- Exported job configuration (config.xml with secrets redacted)
- Build logs (without sensitive information)
- Evidence screenshots

---

## Next Steps (Task 8)

After Task 7 is complete and merged:
- Set up automated testing with Selenium
- Configure Jenkins pipeline for multi-stage CI/CD
- Add code quality tools (SonarQube, Checkstyle)
- Implement deployment stages
- Container integration with Docker

---

**Document Version:** 1.0  
**Last Updated:** October 18, 2026  
**Author:** DevOps Course - Task 7
