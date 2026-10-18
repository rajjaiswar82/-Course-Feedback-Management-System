# Task 9: Evidence Collection Checklist
## Selenium UI Test Suite - Documentation and Proof

This checklist outlines all evidence required to demonstrate successful completion of Task 9: Selenium Test Design and Local Execution.

---

## 📋 Checklist Overview

| Category | Item | Status | Location |
|----------|------|--------|----------|
| **Documentation** | Test plan | ✅ | `docs/task-09-selenium/test-plan.md` |
| **Documentation** | How-to-run guide | ✅ | `docs/task-09-selenium/how-to-run.md` |
| **Documentation** | Evidence checklist | ✅ | `docs/task-09-selenium/evidence-checklist.md` |
| **Code** | Base test class | ✅ | `src/test/java/.../ui/BaseUITest.java` |
| **Code** | Screenshot extension | ✅ | `src/test/java/.../ui/ScreenshotOnFailureExtension.java` |
| **Code** | Page Objects (4) | ✅ | `src/test/java/.../ui/pages/` |
| **Code** | Test classes (5) | ✅ | `src/test/java/.../ui/*UITest.java` |
| **Code** | Test data file | ✅ | `src/test/resources/testdata.csv` |
| **Config** | Maven profile | ✅ | `pom.xml` (selenium profile) |
| **Config** | Selenium dependency | ✅ | `pom.xml` |
| **Evidence** | Console output | ⏳ | Capture during execution |
| **Evidence** | HTML test report | ⏳ | Generate after execution |
| **Evidence** | Failure screenshot | ⏳ | Create intentional failure |
| **Evidence** | Browser test run | ⏳ | Screenshot during test |

---

## 1. Required Screenshots

### 📸 Screenshot 1: Test Execution - Console Output
**What to Capture:** Maven test execution console showing all tests passing

**How to Capture:**
```powershell
# Run tests
mvn clean test -Pselenium > test-output.txt

# Or copy console output manually
```

**What to Show:**
- Maven command with `-Pselenium` profile
- "Running" messages for each test class
- Test counts: Tests run, Failures, Errors, Skipped
- "BUILD SUCCESS" message
- Total test execution time

**Example Output to Capture:**
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running in.college.coursefeedback.ui.DashboardAnalyticsUITest
Screenshot saved: target\screenshots\...
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Running in.college.coursefeedback.ui.FeedbackSearchUITest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
...
[INFO] BUILD SUCCESS
```

**Save As:** `docs/task-09-selenium/evidence/01-console-output.txt` or screenshot

---

### 📸 Screenshot 2: HTML Test Report - Summary
**What to Capture:** Surefire HTML report showing test results summary

**How to Generate:**
```powershell
mvn clean test -Pselenium
mvn surefire-report:report-only
```

**Open:** `target/site/surefire-report.html` in browser

**What to Show:**
- Summary section with:
  - Tests run count (should be ~22)
  - Failures: 0
  - Errors: 0
  - Success Rate: 100%
  - Time elapsed
- List of test classes and their individual results
- All tests showing green checkmarks/success

**Save As:** `docs/task-09-selenium/evidence/02-test-report-summary.png`

**Bonus:** Also copy the actual HTML file to evidence folder

---

### 📸 Screenshot 3: HTML Test Report - Detailed View
**What to Capture:** Drill-down view of one test class showing individual test methods

**Where:** Click on a test class name in the HTML report (e.g., `FeedbackSubmissionUITest`)

**What to Show:**
- Test class name
- Each test method listed
- Time taken for each test
- Success status for all methods

**Save As:** `docs/task-09-selenium/evidence/03-test-report-details.png`

---

### 📸 Screenshot 4: Screenshot-on-Failure Mechanism
**What to Capture:** Proof that screenshot mechanism works on test failure

**How to Create:**
1. Temporarily modify a test to fail:
   ```java
   @Test
   public void testIntentionalFailure() {
       LoginPage loginPage = new LoginPage(driver);
       loginPage.navigateTo(baseUrl);
       loginPage.loginAsStudent();
       
       // Intentional failure
       assertEquals("Wrong Expected Value", "Actual Value", 
           "This test intentionally fails to demonstrate screenshot capture");
   }
   ```

2. Run the test:
   ```powershell
   mvn test -Pselenium -Dtest=FeedbackSubmissionUITest#testIntentionalFailure
   ```

3. Check `target/screenshots/` directory for PNG file

**What to Show:**
- Screenshot file in Windows Explorer showing:
  - Filename with test name and timestamp
  - File size (should be 50-500 KB)
  - Modified date/time matching test run
- OR open the PNG to show it captured the browser state

**Save As:** 
- Screenshot file: `docs/task-09-selenium/evidence/04-failure-screenshot-example.png`
- Explorer view: `docs/task-09-selenium/evidence/04-screenshot-mechanism-proof.png`

**IMPORTANT:** After capturing evidence, remove the intentional failure and commit that fix separately.

---

### 📸 Screenshot 5: Browser During Test Execution (Optional but Impressive)
**What to Capture:** Chrome/Firefox browser window during an actual test run

**How to Capture:**
1. Run tests in headed mode (not headless):
   ```powershell
   mvn test -Pselenium -Dheadless=false -Dtest=FeedbackSubmissionUITest
   ```

2. Be ready with screenshot tool (Windows: Win+Shift+S)

3. Capture browser window showing:
   - Application being tested (feedback form, list page, etc.)
   - Selenium filling in form fields or interacting with page
   - URL bar showing localhost:8080

**What to Show:**
- Chrome/Firefox window clearly visible
- Application UI (feedback form or list page)
- Optionally: Developer tools if interesting

**Save As:** `docs/task-09-selenium/evidence/05-browser-during-test.png`

---

### 📸 Screenshot 6: Test Execution - Multiple Runs Stability
**What to Capture:** Proof that tests pass consistently (3 runs minimum)

**How to Execute:**
```powershell
# Run 1
mvn clean test -Pselenium

# Run 2
mvn clean test -Pselenium

# Run 3
mvn clean test -Pselenium
```

**What to Show:**
- Console output or text file showing 3 consecutive successful runs
- Each showing "BUILD SUCCESS"
- Similar execution times
- Same test counts

**Save As:** `docs/task-09-selenium/evidence/06-stability-three-runs.txt`

---

### 📸 Screenshot 7: Maven Profile Configuration
**What to Capture:** pom.xml showing selenium profile configuration

**How to Capture:**
Open `pom.xml` in editor and screenshot the `<profiles>` section

**What to Show:**
```xml
<profiles>
    <profile>
        <id>selenium</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-surefire-plugin</artifactId>
                    ...
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

**Save As:** `docs/task-09-selenium/evidence/07-maven-profile-config.png`

---

### 📸 Screenshot 8: Test Code Structure
**What to Capture:** Project structure in IDE or file explorer showing test organization

**What to Show:**
```
src/test/java/in/college/coursefeedback/ui/
├── BaseUITest.java
├── ScreenshotOnFailureExtension.java
├── FeedbackSubmissionUITest.java
├── FeedbackSearchUITest.java
├── FeedbackStatusWorkflowUITest.java
├── FeedbackValidationUITest.java
├── DashboardAnalyticsUITest.java
└── pages/
    ├── LoginPage.java
    ├── FeedbackFormPage.java
    ├── FeedbackListPage.java
    └── DashboardPage.java
```

**Save As:** `docs/task-09-selenium/evidence/08-test-code-structure.png`

---

### 📸 Screenshot 9: Headless Mode Execution
**What to Capture:** Tests running successfully in headless mode

**How to Execute:**
```powershell
mvn clean test -Pselenium -Dheadless=true
```

**What to Show:**
- Console output showing tests passing
- No browser window visible (mention this in caption)
- BUILD SUCCESS

**Save As:** `docs/task-09-selenium/evidence/09-headless-execution.png` or .txt

---

## 2. Required Text Files

### 📄 File 1: Successful Test Run Console Output
**Filename:** `docs/task-09-selenium/local-test-output.txt`

**Content:** Complete console output from a successful test run

**How to Create:**
```powershell
mvn clean test -Pselenium > docs/task-09-selenium/local-test-output.txt 2>&1
```

**Must Include:**
- Maven version
- Java version
- Test discovery
- All test class executions
- Test results summary
- BUILD SUCCESS
- Total time

---

### 📄 File 2: Test Run with Different Browsers (Optional)
**Filename:** `docs/task-09-selenium/evidence/browser-compatibility-tests.txt`

**Content:** Output from running tests with different browsers

**How to Create:**
```powershell
echo "=== Chrome ===" > browser-tests.txt
mvn clean test -Pselenium -Dbrowser=chrome >> browser-tests.txt 2>&1

echo "=== Firefox ===" >> browser-tests.txt
mvn clean test -Pselenium -Dbrowser=firefox >> browser-tests.txt 2>&1

echo "=== Edge ===" >> browser-tests.txt
mvn clean test -Pselenium -Dbrowser=edge >> browser-tests.txt 2>&1
```

---

## 3. Files to Include in Evidence Folder

### Copy These Files:
```powershell
# HTML test report
Copy-Item target\site\surefire-report.html docs\task-09-selenium\evidence\

# Sample XML test result
Copy-Item target\surefire-reports\TEST-*.xml docs\task-09-selenium\evidence\sample-test-result.xml

# Screenshot example (after creating intentional failure)
Copy-Item target\screenshots\*.png docs\task-09-selenium\evidence\04-failure-screenshot-example.png
```

---

## 4. Documentation Checklist

### Test Plan (`test-plan.md`) Must Include:
- ✅ Objective and scope
- ✅ Test environment details
- ✅ All 5 critical user journeys with justification
- ✅ Complete test case table (22 test cases)
- ✅ Entry/exit criteria
- ✅ Risk assessment
- ✅ Test data strategy

### How-to-Run Guide (`how-to-run.md`) Must Include:
- ✅ Prerequisites checklist
- ✅ Step-by-step application startup
- ✅ Exact Maven commands
- ✅ All system property options
- ✅ Troubleshooting section
- ✅ File location reference

### Evidence Checklist (`evidence-checklist.md`) Must Include:
- ✅ Complete screenshot list with instructions
- ✅ What each screenshot should show
- ✅ Commands to generate evidence
- ✅ File naming conventions

---

## 5. Git Commit Evidence

### Required Commits:
1. ✅ "test: add Selenium dependencies and selenium Maven profile"
2. ✅ "ui: add stable IDs to HTML templates for Selenium locators"
3. ✅ "test: add base test, page objects, screenshot-on-failure watcher and test data"
4. ⏳ "test: add all 5 user journey test classes (22 test cases total)"
5. ⏳ "docs: add test plan, how-to-run guide, and evidence checklist for Task 9"
6. ⏳ "test: demonstrate screenshot mechanism (intentional failure)"
7. ⏳ "test: remove intentional failure after screenshot verification"

### Check Commit History:
```powershell
git log --oneline feature/task-09-selenium-tests
```

---

## 6. Pull Request Evidence

### PR Title:
"Task 9: Selenium test suite and local execution"

### PR Description Must Include:
- Summary of 5 journeys covered
- Test case count (22 total)
- Commands to run tests
- Link to how-to-run.md
- Screenshots or link to evidence folder

---

## 7. Final Checklist Before Submission

### Code Quality:
- [ ] All tests have meaningful `@DisplayName` annotations
- [ ] Test methods follow naming convention: `test<Scenario>`
- [ ] Page Objects use explicit waits (no Thread.sleep)
- [ ] Test data uses timestamps for uniqueness
- [ ] Comments explain WHY not WHAT

### Test Execution:
- [ ] All 22 tests pass consistently
- [ ] Tests run 3 times successfully (stability)
- [ ] Headless mode execution successful
- [ ] Screenshot mechanism verified with intentional failure
- [ ] HTML report generated successfully

### Documentation:
- [ ] test-plan.md complete with all sections
- [ ] how-to-run.md has clear step-by-step instructions
- [ ] evidence-checklist.md (this file) completed
- [ ] local-test-output.txt captured
- [ ] Evidence folder has all required screenshots

### Repository:
- [ ] .gitignore excludes target/screenshots/ and target/surefire-reports/
- [ ] Feature branch created from Dev
- [ ] All changes committed with descriptive messages
- [ ] Changes pushed to remote
- [ ] Pull request created with proper description

---

## 8. Evidence Folder Final Structure

```
docs/task-09-selenium/
├── test-plan.md                               ✅ Test plan document
├── how-to-run.md                              ✅ Execution instructions
├── evidence-checklist.md                      ✅ This file
├── local-test-output.txt                      ⏳ Console output
└── evidence/
    ├── 01-console-output.png/.txt             ⏳ Test execution console
    ├── 02-test-report-summary.png             ⏳ HTML report summary
    ├── 03-test-report-details.png             ⏳ HTML report drill-down
    ├── 04-failure-screenshot-example.png      ⏳ Screenshot from intentional failure
    ├── 04-screenshot-mechanism-proof.png      ⏳ Explorer showing screenshot file
    ├── 05-browser-during-test.png             ⏳ Browser window during test
    ├── 06-stability-three-runs.txt            ⏳ Multiple successful runs
    ├── 07-maven-profile-config.png            ⏳ pom.xml profile section
    ├── 08-test-code-structure.png             ⏳ Project structure
    ├── 09-headless-execution.png/.txt         ⏳ Headless mode output
    ├── surefire-report.html                   ⏳ Actual HTML report file
    └── sample-test-result.xml                 ⏳ Sample XML test result
```

---

## 9. Verification Commands

Run these to verify everything is ready:

```powershell
# 1. Check test files exist
Get-ChildItem -Recurse -Filter "*UITest.java" | Select-Object Name

# 2. Check page objects exist
Get-ChildItem src\test\java\in\college\coursefeedback\ui\pages\

# 3. Verify Maven profile
Select-String -Path pom.xml -Pattern "selenium" -Context 2

# 4. Count test methods
Select-String -Path "src\test\java\in\college\coursefeedback\ui\*UITest.java" -Pattern "@Test" | Measure-Object

# 5. Verify test data file
Get-Content src\test\resources\testdata.csv

# 6. Check documentation
Get-ChildItem docs\task-09-selenium\

# 7. Run tests
mvn clean test -Pselenium
```

---

## 10. Success Criteria Met

Task 9 evidence is complete when:
- ✅ All 5 journey test classes implemented
- ✅ Minimum 22 test cases passing
- ✅ Screenshot mechanism proven with intentional failure example
- ✅ HTML report generated and saved
- ✅ Console output captured showing BUILD SUCCESS
- ✅ Tests stable (3 consecutive successful runs)
- ✅ All documentation files complete
- ✅ Evidence folder has required screenshots
- ✅ Pull request created with description

---

**Status:** ⏳ In Progress  
**Last Updated:** October 2026  
**Next Step:** Execute tests and capture evidence as outlined above
