# Task 8: Jenkins Pipeline Setup Guide

## Project Configuration

### Build Information
- **Build Tool:** Maven 3.9.16
- **Java Version:** Java 21 (OpenJDK 21.0.12)
- **Packaging:** JAR (Spring Boot executable)
- **Artifact:** `course-feedback-management-system-0.0.1-SNAPSHOT.jar`
- **Operating System:** Windows 11
- **Shell Commands:** PowerShell/CMD (bat)

### Jenkins Tool Names (from Task 7)
- **JDK:** `JDK-21`
- **Maven:** `Maven-3.9.16`

---

## Deployment Strategy

### Spring Boot JAR Deployment

This application uses **Spring Boot with embedded server**, so it doesn't require external Tomcat. Instead, we deploy as a standalone JAR application.

**Deployment Method:**
1. Package as executable JAR
2. Copy to environment-specific deployment directory
3. Run with `java -jar` command
4. Use environment-specific configuration files

**Deployment Directories:**
- `C:\Jenkins\deployments\dev` - Development environment
- `C:\Jenkins\deployments\test` - Test environment
- `C:\Jenkins\deployments\prod` - Production environment

---

## Jenkinsfile Structure

### Pipeline Overview

The declarative Jenkinsfile implements a complete CI/CD pipeline with the following stages:

```
Checkout → Build → Package → Deploy
```

Future enhancement (Task 9-10): Insert **Test** stage between Build and Package

---

### Parameters

The pipeline is **parameterized** with the following inputs:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `DEPLOY_ENV` | Choice | `dev` | Target deployment environment (dev/test/prod) |
| `APP_PORT` | String | `8080` | Application server port |
| `SKIP_DEPLOY` | Boolean | `false` | Skip deployment stage if true |

**Important:** Parameters only appear after the first pipeline run. The first run loads the Jenkinsfile and registers the parameters.

---

### Environment Variables

```groovy
environment {
    APP_NAME = 'course-feedback-management-system'
    VERSION = '0.0.1-SNAPSHOT'
    ARTIFACT_NAME = "${APP_NAME}-${VERSION}.jar"
    DEPLOY_DIR = "C:\\\\Jenkins\\\\deployments\\\\${params.DEPLOY_ENV}"
    APP_URL = "http://localhost:${params.APP_PORT}"
}
```

---

### Stage Details

#### 1. Checkout Stage
```groovy
stage('Checkout') {
    steps {
        echo "Checking out source code from SCM..."
        checkout scm
        echo "Checkout completed for branch: ${env.GIT_BRANCH}"
    }
}
```

**Purpose:** Checkout source code from Git repository  
**Actions:**
- Clones the repository
- Checks out the specified branch
- Logs the branch name

---

#### 2. Build Stage
```groovy
stage('Build') {
    steps {
        echo "Compiling the application..."
        bat 'mvn clean compile'
        echo "Build compilation completed successfully"
    }
}
```

**Purpose:** Compile the Java source code  
**Actions:**
- Runs `mvn clean compile`
- Compiles all Java classes
- Validates code compiles without errors

**Maven Command:** `mvn clean compile`
- `clean` - Removes previous build artifacts
- `compile` - Compiles source code

---

#### 3. Package Stage
```groovy
stage('Package') {
    steps {
        echo "Packaging the application..."
        bat 'mvn package -DskipTests'
        echo "Application packaged successfully"
        
        archiveArtifacts artifacts: "target/${ARTIFACT_NAME}", fingerprint: true
        echo "Artifact archived: ${ARTIFACT_NAME}"
    }
}
```

**Purpose:** Package application into executable JAR  
**Actions:**
- Runs `mvn package -DskipTests`
- Creates executable JAR in `target/` directory
- Archives artifact in Jenkins with fingerprinting

**Maven Command:** `mvn package -DskipTests`
- `package` - Creates JAR file
- `-DskipTests` - Skips test execution (tests run in separate Test stage)

**Artifact Archiving:**
- File: `target/course-feedback-management-system-0.0.1-SNAPSHOT.jar`
- Fingerprinting: Enabled (tracks artifact versions)

---

#### 4. Deploy Stage
```groovy
stage('Deploy') {
    when {
        expression { return !params.SKIP_DEPLOY }
    }
    steps {
        script {
            // Deployment logic
        }
    }
}
```

**Purpose:** Deploy application to target environment  
**Conditional Execution:** Only runs when `SKIP_DEPLOY` is `false`

**Deployment Steps:**

##### Step 1: Display Configuration
```groovy
echo "========================================="
echo "DEPLOYMENT CONFIGURATION"
echo "Environment: ${params.DEPLOY_ENV}"
echo "Port: ${params.APP_PORT}"
echo "Deploy Directory: ${DEPLOY_DIR}"
echo "========================================="
```

##### Step 2: Create Deployment Directory
```bat
if not exist "${DEPLOY_DIR}" mkdir "${DEPLOY_DIR}"
```

Creates environment-specific directory if it doesn't exist.

##### Step 3: Stop Existing Application
```bat
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :${params.APP_PORT} ^| findstr LISTENING') do (
    echo Killing process %%a
    taskkill /F /PID %%a 2>nul
)
```

Finds and kills any process listening on the target port.

##### Step 4: Copy Artifact
```bat
copy /Y "target\\${ARTIFACT_NAME}" "${DEPLOY_DIR}\\"
```

Copies the JAR file to the deployment directory.

##### Step 5: Create Environment Configuration
```bat
echo server.port=${params.APP_PORT} > "${DEPLOY_DIR}\\application-${params.DEPLOY_ENV}.properties"
echo spring.profiles.active=${params.DEPLOY_ENV} >> "${DEPLOY_DIR}\\application-${params.DEPLOY_ENV}.properties"
echo app.environment=${params.DEPLOY_ENV} >> "${DEPLOY_DIR}\\application-${params.DEPLOY_ENV}.properties"
```

Creates environment-specific configuration file:
- `application-dev.properties` for dev environment
- `application-test.properties` for test environment
- `application-prod.properties` for prod environment

**Configuration Properties:**
- `server.port` - Application HTTP port
- `spring.profiles.active` - Active Spring profile
- `app.environment` - Custom environment identifier

##### Step 6: Start Application
```bat
start /B java -jar -Dspring.profiles.active=${params.DEPLOY_ENV} -Dserver.port=${params.APP_PORT} "${ARTIFACT_NAME}"
```

Starts the Spring Boot application in background with:
- Active profile matching environment
- Port from parameters
- Logs redirected to `app-${DEPLOY_ENV}.log`

##### Step 7: Wait for Startup
```bat
timeout /t 10 /nobreak
```

Waits 10 seconds for application to initialize.

##### Step 8: Display Success Message
```groovy
echo "========================================="
echo "DEPLOYMENT SUCCESSFUL!"
echo "Application URL: ${APP_URL}"
echo "Environment: ${params.DEPLOY_ENV}"
echo "Deployment Directory: ${DEPLOY_DIR}"
echo "Log File: ${DEPLOY_DIR}\\app-${params.DEPLOY_ENV}.log"
echo "========================================="
```

---

### Post Actions

```groovy
post {
    success {
        echo "✅ Pipeline completed successfully!"
        echo "Environment: ${params.DEPLOY_ENV}"
        echo "Application URL: ${APP_URL}"
    }
    failure {
        echo "❌ Pipeline failed! Check the logs for details."
    }
    always {
        echo "Pipeline execution finished."
        // Optional: cleanWs()
    }
}
```

**Purpose:** Execute actions after pipeline completes

**Blocks:**
- `success` - Runs only if pipeline succeeds
- `failure` - Runs only if pipeline fails
- `always` - Runs regardless of outcome

---

## Parameter Configuration Evidence

### How Parameters Change Configuration

The `DEPLOY_ENV` parameter directly affects:

1. **Deployment Directory:**
   - `dev` → `C:\Jenkins\deployments\dev`
   - `test` → `C:\Jenkins\deployments\test`
   - `prod` → `C:\Jenkins\deployments\prod`

2. **Configuration File:**
   - `dev` → `application-dev.properties`
   - `test` → `application-test.properties`
   - `prod` → `application-prod.properties`

3. **Spring Profile:**
   - `dev` → `spring.profiles.active=dev`
   - `test` → `spring.profiles.active=test`
   - `prod` → `spring.profiles.active=prod`

4. **Log File:**
   - `dev` → `app-dev.log`
   - `test` → `app-test.log`
   - `prod` → `app-prod.log`

**Verification:**
Check the configuration file in the deployment directory to confirm environment-specific settings.

---

## Jenkins Pipeline Job Configuration

### Creating the Pipeline Job

1. **Go to Jenkins Dashboard**
2. Click **New Item**
3. **Name:** `course-feedback-pipeline`
4. **Type:** Select **Pipeline**
5. Click **OK**

### Pipeline Configuration

#### General Section
- **Description:** `CI/CD pipeline for Course Feedback Management System with parameterized deployment`

#### Build Triggers
- ☑ **Poll SCM**
- **Schedule:** `H/5 * * * *`
  (Polls GitHub every 5 minutes for changes)

#### Pipeline Section
- **Definition:** `Pipeline script from SCM`
- **SCM:** `Git`
- **Repository URL:** `https://github.com/rajjaiswar82/-Course-Feedback-Management-System.git`
- **Credentials:** `- none -` (if public repo)
- **Branch Specifier:** 
  - Initial: `*/feature/task-08-jenkins-pipeline-deploy`
  - After merge: `*/Dev`
- **Script Path:** `Jenkinsfile`

### Save Configuration

Click **Save** at the bottom of the page.

---

## Running the Pipeline

### First Run (Parameter Registration)

**Important:** The first run only loads parameters and may fail. This is expected behavior.

1. Go to pipeline job page
2. Click **Build Now**
3. Wait for completion
4. Parameters are now registered

### Subsequent Runs (With Parameters)

1. Click **Build with Parameters**
2. Configure parameters:
   - **DEPLOY_ENV:** Select `dev`, `test`, or `prod`
   - **APP_PORT:** Keep default `8080` or change
   - **SKIP_DEPLOY:** Keep unchecked
3. Click **Build**

---

## Testing Different Configurations

### Test 1: Deploy to DEV Environment
```
DEPLOY_ENV: dev
APP_PORT: 8080
SKIP_DEPLOY: false
```

**Expected Results:**
- Deploys to `C:\Jenkins\deployments\dev`
- Creates `application-dev.properties`
- Application accessible at `http://localhost:8080`

### Test 2: Deploy to TEST Environment
```
DEPLOY_ENV: test
APP_PORT: 8081
SKIP_DEPLOY: false
```

**Expected Results:**
- Deploys to `C:\Jenkins\deployments\test`
- Creates `application-test.properties`
- Application accessible at `http://localhost:8081`

### Test 3: Deploy to PROD Environment
```
DEPLOY_ENV: prod
APP_PORT: 8082
SKIP_DEPLOY: false
```

**Expected Results:**
- Deploys to `C:\Jenkins\deployments\prod`
- Creates `application-prod.properties`
- Application accessible at `http://localhost:8082`

### Test 4: Skip Deployment
```
DEPLOY_ENV: dev
APP_PORT: 8080
SKIP_DEPLOY: true
```

**Expected Results:**
- Pipeline runs Checkout, Build, Package
- Deploy stage is **skipped**
- No deployment occurs

---

## Verifying Deployment

### Check Deployment Directory

```powershell
# List files in deployment directory
Get-ChildItem "C:\Jenkins\deployments\dev"

# Expected files:
# - course-feedback-management-system-0.0.1-SNAPSHOT.jar
# - application-dev.properties
# - app-dev.log
```

### Check Configuration File

```powershell
Get-Content "C:\Jenkins\deployments\dev\application-dev.properties"
```

**Expected content:**
```
server.port=8080
spring.profiles.active=dev
app.environment=dev
info.app.environment=dev
```

### Check Application Logs

```powershell
Get-Content "C:\Jenkins\deployments\dev\app-dev.log" -Tail 50
```

### Access Application

Open browser:
```
http://localhost:8080
```

Expected: Login page or application homepage

---

## Troubleshooting

### Pipeline Fails at Build Stage

**Symptom:** Maven build fails

**Solutions:**
- Verify JDK-21 and Maven-3.9.16 are configured in Jenkins Tools
- Check console output for compilation errors
- Ensure `pom.xml` is valid

### Pipeline Fails at Deploy Stage

**Symptom:** Deployment fails or application doesn't start

**Solutions:**
- Check if port is already in use
- Verify deployment directory exists and is writable
- Check application logs in deployment directory
- Ensure Java 21 is available on Jenkins server

### Application Not Accessible

**Symptom:** Cannot access application URL

**Solutions:**
- Check if application is running: `netstat -ano | findstr :<PORT>`
- Check application logs for startup errors
- Verify firewall allows access to the port
- Try accessing with `http://127.0.0.1:<PORT>` instead

### Parameters Not Showing

**Symptom:** "Build with Parameters" option not available

**Solutions:**
- Run the pipeline once with "Build Now"
- Parameters register after first successful Jenkinsfile parse
- Refresh the Jenkins page

---

## Security Notes

⚠️ **DO NOT COMMIT:**
- Real database credentials
- Production passwords
- API keys or tokens
- Environment-specific secrets

✅ **Safe to commit:**
- Jenkinsfile (without embedded credentials)
- Documentation
- Build logs (sanitized)
- Configuration templates

---

## Future Enhancements (Task 9-10)

The Jenkinsfile includes a placeholder for the Test stage:

```groovy
// TODO: Insert Test stage here for Task 9-10 (Selenium automated tests)
// stage('Test') {
//     steps {
//         echo "Running automated tests..."
//         bat 'mvn test'
//     }
// }
```

This will be implemented in:
- **Task 9:** Selenium automated testing
- **Task 10:** Integration of tests into pipeline

---

## Summary

This pipeline provides:
- ✅ Parameterized deployment to multiple environments
- ✅ Environment-specific configuration
- ✅ Automated artifact archiving
- ✅ Conditional deployment (skip option)
- ✅ Clear deployment verification
- ✅ Post-build notifications
- ✅ Ready for test integration (Task 9-10)

---

**Document Version:** 1.0  
**Last Updated:** October 18, 2026  
**Task:** Task 8 - Pipeline as Code and Server Deployment
