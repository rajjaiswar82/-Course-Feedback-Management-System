# Jenkins Running - Task 15

**Status**: ✅ Jenkins Successfully Started  
**Date**: October 21, 2026  
**Version**: Jenkins 2.580.1

---

## Jenkins Information

- **URL**: http://localhost:8080
- **Version**: 2.580.1
- **Home Directory**: `C:\Users\RAJ JAISWAR\.jenkins`
- **WAR Location**: `C:\Users\RAJ JAISWAR\Downloads\jenkins.war`
- **Process Status**: Running in background (Terminal ID: 1)

---

## How Jenkins Was Started

```powershell
java -jar "C:\Users\RAJ JAISWAR\Downloads\jenkins.war" --httpPort=8080
```

---

## Accessing Jenkins

1. **Open Browser**: http://localhost:8080
2. **Login**: Use your existing Jenkins credentials (Jenkins was already configured)
3. **Jobs**: Navigate to your Course Feedback build job

---

## Next Steps for Task 15 Demo

### 1. Verify Jenkins Job Configuration

```powershell
# Access Jenkins web interface
Start-Process "http://localhost:8080"
```

In Jenkins:
- Navigate to your existing job (likely named "course-feedback" or similar)
- Click "Configure" to review the pipeline
- Check Source Code Management points to your Git repository
- Verify Build Triggers are configured

### 2. Trigger a Build

**Option A: Manual Build**
- Click "Build Now" in Jenkins UI

**Option B: Git Push (Automatic)**
```powershell
# Make a small change and commit
git checkout feature/task-15-final-release
echo "# Jenkins Demo - Task 15" >> docs/task-15-final/JENKINS-BUILD-LOG.md
git add docs/task-15-final/JENKINS-BUILD-LOG.md
git commit -m "test: trigger Jenkins build for Task 15 demo"
git push origin feature/task-15-final-release
```

### 3. Monitor Build

1. In Jenkins, click on the build number (e.g., #42)
2. Click "Console Output" to view real-time logs
3. Watch for these stages:
   - ✓ Checkout (Git clone)
   - ✓ Build (Maven compile)
   - ✓ Test (JUnit tests)
   - ✓ Quality Gate (Selenium tests)
   - ✓ Docker Build
   - ✓ Docker Push
   - ✓ Deploy (optional)

### 4. Verify Build Success

After successful build:
```powershell
# Check new Docker image was created
docker images | Select-String "course-feedback"

# New image should appear with build number
# Example: rajjaiswar/course-feedback:42
```

---

## Complete End-to-End Demo Flow

### Step 1: Jenkins Running ✅
- Jenkins accessible at http://localhost:8080

### Step 2: Application Running ✅
- Application at http://localhost:8082 (Docker container)

### Step 3: Trigger Complete Pipeline

```powershell
# Navigate to project
cd C:\Users\RAJ JAISWAR\OneDrive\Documents\Devops\course-feedback-git

# Make a demo change
git checkout feature/task-15-final-release

# Edit a file (or create demo file)
"Demo change for Task 15" | Out-File -FilePath "DEMO-CHANGE.txt"

# Commit and push
git add DEMO-CHANGE.txt
git commit -m "feat: Task 15 final demo trigger"
git push origin feature/task-15-final-release

# Wait 5 minutes for Jenkins to detect (SCM polling)
# Or trigger manually in Jenkins UI
```

### Step 4: Watch Jenkins Pipeline Execute

1. **Go to Jenkins**: http://localhost:8080
2. **Open Your Job**: Click on the job name
3. **Watch Build**: New build should start automatically or trigger "Build Now"
4. **View Console**: Click build number → "Console Output"

Expected Console Output:
```
Started by SCM change
Checking out Git repository...
[Maven] Building course-feedback-git 1.0.0-SNAPSHOT
[INFO] Compiling source files...
[INFO] Running tests...
[INFO] All tests passed
[Selenium] Running quality gate tests...
[Docker] Building image rajjaiswar/course-feedback:BUILD_NUMBER
[Docker] Pushing to Docker Hub...
[SUCCESS] Build completed successfully
```

### Step 5: Deploy New Version

```powershell
# Use the new build number from Jenkins
$buildNumber = 42  # Replace with actual Jenkins build number

# Deploy using Puppet provisioning
.\scripts\provision.ps1 -Version "1.0.$buildNumber"

# Verify deployment
docker ps
docker logs feedback-app --tail 20
```

### Step 6: Verify Application

```powershell
# Test HTTP endpoint
Invoke-WebRequest -Uri http://localhost:8082 -UseBasicParsing

# Open in browser
Start-Process "http://localhost:8082"
```

---

## Capturing Evidence for Task 15

### Screenshots to Capture

1. **Jenkins Dashboard** - Showing build history
2. **Jenkins Job Configuration** - Pipeline stages
3. **Jenkins Console Output** - Successful build
4. **Docker Images** - Multiple versions available
5. **Docker Containers** - Running application
6. **Application UI** - Feedback form
7. **Application UI** - Feedback list
8. **Application UI** - Dashboard with analytics

### Screen Recording (Optional)

Record the complete flow:
1. Start Jenkins
2. Make code change
3. Git commit and push
4. Jenkins automatically builds
5. Docker image created
6. Deploy with Puppet
7. Application running and accessible

---

## Troubleshooting

### Jenkins Won't Start

```powershell
# Check if port 8080 is in use
netstat -ano | Select-String "8080"

# Kill process if needed
Stop-Process -Id <PID> -Force

# Restart Jenkins
java -jar "C:\Users\RAJ JAISWAR\Downloads\jenkins.war" --httpPort=8080
```

### Build Fails in Jenkins

**Common Issues**:

1. **Git not found**:
   - Configure Git path in Jenkins: Manage Jenkins → Tools → Git
   
2. **Maven not found**:
   - Configure Maven path: Manage Jenkins → Tools → Maven
   - Add Maven to system PATH

3. **Selenium tests fail**:
   - Ensure ChromeDriver is installed
   - Application must be running on port 8080 during tests
   - Use headless mode in CI/CD

4. **Docker commands fail**:
   - Ensure Docker Desktop is running
   - Jenkins user needs Docker permissions
   - Check Docker Hub credentials

### Can't Login to Jenkins

If you forgot Jenkins password:
```powershell
# Edit Jenkins config to disable security temporarily
Stop-Process -Name java -Force  # Stop Jenkins

# Edit config file
$configFile = "$env:USERPROFILE\.jenkins\config.xml"
# Set <useSecurity>false</useSecurity>

# Restart Jenkins
java -jar "C:\Users\RAJ JAISWAR\Downloads\jenkins.war" --httpPort=8080

# Reset password in UI, then re-enable security
```

---

## Stopping Jenkins

When demo is complete:

```powershell
# Find Jenkins process
Get-Process | Where-Object {$_.ProcessName -eq "java"}

# Stop gracefully
Stop-Process -Name java -Force

# Or in PowerShell terminal where Jenkins is running, press Ctrl+C
```

---

## Task 15 Checklist

- [x] Jenkins installed and configured
- [x] Jenkins running on port 8080
- [x] Application running on port 8082 (Docker)
- [ ] Jenkins job configured with Jenkinsfile
- [ ] Trigger manual build in Jenkins
- [ ] Verify build success (all stages green)
- [ ] Capture screenshots of build process
- [ ] Document build logs
- [ ] Deploy new version using Puppet
- [ ] Complete end-to-end demo recording
- [ ] Update final documentation

---

**Status**: Jenkins ready for Task 15 demonstration  
**Next Action**: Configure Jenkins job and trigger build
