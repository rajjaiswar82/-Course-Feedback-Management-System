# How to Run Selenium UI Tests
## Course Feedback Management System

This guide provides step-by-step instructions for running the Selenium UI test suite locally.

---

## Prerequisites

### Required Software:
1. **Java 21** (OpenJDK or Oracle JDK)
   ```powershell
   java -version
   # Should show: openjdk version "21"
   ```

2. **Maven 3.9+**
   ```powershell
   mvn -version
   # Should show: Apache Maven 3.9.x
   ```

3. **Chrome Browser** (or Firefox/Edge)
   - Chrome is the default browser
   - Latest stable version recommended
   - Selenium Manager handles driver automatically (no manual chromedriver download needed)

4. **Git** (for cloning repository)

---

## Step 1: Start the Application

The tests require the application to be running locally.

### Option A: Run with Maven
```powershell
# Navigate to project root
cd C:\path\to\course-feedback-git

# Start the application
mvn spring-boot:run
```

### Option B: Run JAR file
```powershell
# Build the application first
mvn clean package -DskipTests

# Run the JAR
java -jar target\course-feedback-management-system-0.0.1-SNAPSHOT.jar
```

### Verify Application is Running:
Open browser and navigate to: `http://localhost:8080`

You should see the login page. Test credentials:
- **Student:** username=`student`, password=`Student@123`
- **Admin:** username=`admin`, password=`Admin@123`

**IMPORTANT:** Keep the application running in this terminal while you run tests in another terminal.

---

## Step 2: Run Selenium Tests

Open a **new terminal/PowerShell window** (keep the app running in the first one).

### Basic Test Execution

Run all UI tests with default settings:
```powershell
cd C:\path\to\course-feedback-git

mvn clean test -Pselenium
```

This will:
- Run all `*UITest.java` classes
- Use Chrome browser (default)
- Run in headed mode (you'll see the browser)
- Use base URL: `http://localhost:8080` (default)

---

## Step 3: Configuration Options

### System Properties

You can customize test execution using system properties:

#### Change Base URL:
```powershell
mvn clean test -Pselenium -Dbase.url=http://localhost:8081
```

#### Change Browser:
```powershell
# Chrome (default)
mvn clean test -Pselenium -Dbrowser=chrome

# Firefox
mvn clean test -Pselenium -Dbrowser=firefox

# Microsoft Edge
mvn clean test -Pselenium -Dbrowser=edge
```

#### Run in Headless Mode:
```powershell
mvn clean test -Pselenium -Dheadless=true
```

Headless mode runs the browser without GUI - faster and suitable for CI environments.

#### Combine Multiple Properties:
```powershell
mvn clean test -Pselenium -Dbase.url=http://localhost:8080 -Dbrowser=chrome -Dheadless=false
```

---

## Step 4: Run Specific Test Classes

### Run Single Journey:
```powershell
# Journey 1: Feedback Submission
mvn test -Pselenium -Dtest=FeedbackSubmissionUITest

# Journey 2: Search Functionality
mvn test -Pselenium -Dtest=FeedbackSearchUITest

# Journey 3: Status Workflow
mvn test -Pselenium -Dtest=FeedbackStatusWorkflowUITest

# Journey 4: Form Validation
mvn test -Pselenium -Dtest=FeedbackValidationUITest

# Journey 5: Dashboard Analytics
mvn test -Pselenium -Dtest=DashboardAnalyticsUITest
```

### Run Specific Test Method:
```powershell
mvn test -Pselenium -Dtest=FeedbackSubmissionUITest#testSubmitValidFeedback
```

---

## Step 5: Generate HTML Test Reports

### Generate Surefire HTML Report:
```powershell
# Run tests and generate report
mvn clean test -Pselenium
mvn surefire-report:report-only

# Or use site goal (generates in target/site/)
mvn clean test -Pselenium site
```

### View the Report:
Open in browser:
```
file:///C:/path/to/course-feedback-git/target/site/surefire-report.html
```

Or use PowerShell:
```powershell
Invoke-Item target\site\surefire-report.html
```

---

## Step 6: Review Test Results

### Console Output:
Watch the console for real-time test execution:
```
[INFO] Running in.college.coursefeedback.ui.FeedbackSubmissionUITest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
```

### XML Reports:
Located at: `target/surefire-reports/TEST-*.xml`

### Screenshots on Failure:
If a test fails, a screenshot is automatically captured to:
```
target/screenshots/<TestClassName>_<testMethodName>_<timestamp>.png
```

Example:
```
target/screenshots/FeedbackSubmissionUITest_testSubmitValidFeedback_20261018_183045.png
```

---

## Common Commands Cheat Sheet

```powershell
# Run all UI tests (default Chrome, headed)
mvn clean test -Pselenium

# Run in headless mode
mvn clean test -Pselenium -Dheadless=true

# Run with Firefox
mvn clean test -Pselenium -Dbrowser=firefox

# Run specific test class
mvn test -Pselenium -Dtest=FeedbackSubmissionUITest

# Run and generate HTML report
mvn clean test -Pselenium site

# Run unit tests only (exclude Selenium)
mvn clean test
```

---

## Troubleshooting

### Problem: "Address already in use" or Application Won't Start
**Cause:** Port 8080 is already in use.

**Solution:**
```powershell
# Find what's using port 8080
netstat -ano | findstr "8080" | findstr LISTENING

# Kill the process (replace PID with actual process ID)
Stop-Process -Id <PID> -Force

# Or run app on different port
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081

# Then run tests with that URL
mvn clean test -Pselenium -Dbase.url=http://localhost:8081
```

### Problem: "Could not start a new session" or Browser Driver Error
**Cause:** Browser or driver issues.

**Solution:**
1. Update your browser to the latest version
2. Selenium Manager should auto-download the correct driver
3. Try a different browser:
   ```powershell
   mvn clean test -Pselenium -Dbrowser=firefox
   ```

### Problem: Tests are Flaky or Timing Out
**Cause:** Application slow to start or respond.

**Solution:**
1. Ensure only one application instance is running
2. Clear H2 database by restarting the app
3. Increase timeout in `BaseUITest.java` if needed (default: 10 seconds)

### Problem: "No tests were executed"
**Cause:** Not using the `selenium` profile.

**Solution:**
Always include `-Pselenium`:
```powershell
mvn clean test -Pselenium
```

Without the profile, UI tests are excluded to keep regular builds fast.

### Problem: Screenshots Not Being Created
**Cause:** Test passed (screenshots only on failure) or permissions issue.

**Solution:**
1. Intentionally fail a test to verify screenshot mechanism
2. Check `target/screenshots/` directory exists and is writable
3. Review console output for screenshot save messages

---

## Test Data Notes

### Unique Test Data:
Tests use timestamps to create unique data:
```java
String studentName = "AutoTest Student " + System.currentTimeMillis();
```

This prevents conflicts between multiple test runs.

### Database Reset:
H2 in-memory database resets when application restarts. To start fresh:
1. Stop the application (Ctrl+C)
2. Restart it
3. Run tests again

---

## File Locations

### Test Source Code:
```
src/test/java/in/college/coursefeedback/ui/
├── BaseUITest.java                         # Base class with WebDriver setup
├── ScreenshotOnFailureExtension.java       # JUnit 5 extension for screenshots
├── FeedbackSubmissionUITest.java           # Journey 1 tests
├── FeedbackSearchUITest.java               # Journey 2 tests
├── FeedbackStatusWorkflowUITest.java       # Journey 3 tests
├── FeedbackValidationUITest.java           # Journey 4 tests
├── DashboardAnalyticsUITest.java           # Journey 5 tests
└── pages/
    ├── LoginPage.java                      # Page Object: Login
    ├── FeedbackFormPage.java               # Page Object: Feedback form
    ├── FeedbackListPage.java               # Page Object: Feedback list
    └── DashboardPage.java                  # Page Object: Dashboard
```

### Test Data:
```
src/test/resources/testdata.csv             # Test data file
```

### Test Output:
```
target/surefire-reports/                    # XML test results
target/screenshots/                         # Failure screenshots
target/site/surefire-report.html            # HTML test report
```

---

## CI/CD Integration (Future - Task 10)

For Jenkins or other CI servers, use headless mode:
```bash
mvn clean test -Pselenium \
  -Dbase.url=http://localhost:8080 \
  -Dbrowser=chrome \
  -Dheadless=true
```

---

## Performance Notes

### Test Execution Time:
- Complete suite: ~2-5 minutes (depends on application startup time)
- Single test class: ~30-60 seconds
- Headless mode: 20-30% faster than headed

### Parallel Execution:
Not configured by default. Tests manipulate shared application state (H2 database), so sequential execution is recommended.

---

## Quick Start (TL;DR)

```powershell
# Terminal 1: Start the app
mvn spring-boot:run

# Terminal 2: Run tests (after app is ready)
mvn clean test -Pselenium

# View HTML report
mvn surefire-report:report-only
Invoke-Item target\site\surefire-report.html
```

---

## Next Steps

After successful test execution:
1. Review the HTML test report for detailed results
2. Check screenshots folder if any tests failed
3. Update test plan documentation with any findings
4. Ready for Task 10: Jenkins CI/CD integration

---

**Need Help?**
- Check test-plan.md for test case details
- Review evidence-checklist.md for what to capture
- Consult Java/Selenium documentation for framework issues

---

**Last Updated:** October 2026  
**Maintained By:** DevOps Team
