# Task 9: Selenium Test Plan
## Course Feedback Management System - UI Automation Testing

**Version:** 1.0  
**Date:** October 2026  
**Project:** Puppet-Provisioned Course Feedback Management System  
**Test Framework:** Selenium WebDriver 4.27.0 with JUnit 5

---

## 1. Objective

Design and implement automated UI tests for the Course Feedback Management System to verify critical user journeys work correctly. These tests will ensure that key features function as expected from an end-user perspective before deployment.

**Goals:**
- Validate 5 critical user journeys through the web interface
- Catch UI-breaking bugs before they reach production
- Establish a regression test suite for future changes
- Document expected behavior for all tested scenarios

---

## 2. Scope

### In Scope:
- ✅ Student feedback submission workflow
- ✅ Search and filtering functionality
- ✅ Admin status workflow management (SUBMITTED → REVIEWED → RESOLVED)
- ✅ Form validation and error handling
- ✅ Dashboard analytics and metrics display

### Out of Scope:
- ❌ Backend API testing (separate unit/integration tests)
- ❌ Performance/load testing
- ❌ Cross-browser compatibility (tested on Chrome only in this phase)
- ❌ Mobile responsive design testing
- ❌ Security penetration testing
- ❌ Accessibility compliance (WCAG)

---

## 3. Test Environment

### Application Under Test:
- **Application:** Course Feedback Management System
- **Version:** 0.0.1-SNAPSHOT
- **Technology Stack:** Spring Boot 3.5.6, Thymeleaf, H2 Database
- **Default URL:** `http://localhost:8080`
- **Authentication:** Spring Security with in-memory users

### Test Environment:
- **OS:** Windows 11
- **Java:** OpenJDK 21
- **Maven:** 3.9.16
- **Browser:** Chrome (primary), Firefox/Edge (configurable)
- **Selenium Version:** 4.27.0
- **Driver Management:** Selenium Manager (automatic)

### Test Accounts:
| Username | Password | Role | Permissions |
|----------|----------|------|-------------|
| student | Student@123 | STUDENT | Submit, view feedback |
| admin | Admin@123 | ADMIN | All operations, status updates, dashboard access |

---

## 4. Critical User Journeys

### J1: Student Submits New Feedback (Create)

**Why Critical:** Core business function - primary user action that creates data in the system. If submission fails, no feedback data enters the system.

**Test Cases:**
- TC-J1-01: Submit valid feedback with all required fields
- TC-J1-02: Submit multiple feedback entries and verify all appear in list

**Risk if Broken:** Students cannot provide feedback; primary system function is non-operational.

---

### J2: Search/Filter Feedback by Keyword

**Why Critical:** Essential for users to find specific feedback among many records. Admin productivity depends on this functionality.

**Test Cases:**
- TC-J2-01: Search feedback by student name
- TC-J2-02: Search feedback by course name
- TC-J2-03: Clear search filter and verify all results return
- TC-J2-04: Search for non-existent keyword shows appropriate message

**Risk if Broken:** Users cannot locate specific feedback; admins waste time scrolling through unfiltered lists.

---

### J3: Admin Updates Feedback Status (Workflow)

**Why Critical:** Represents the feedback lifecycle management. Status workflow (SUBMITTED → REVIEWED → RESOLVED) is key to tracking feedback progress.

**Test Cases:**
- TC-J3-01: Update status from SUBMITTED to REVIEWED
- TC-J3-02: Update status from REVIEWED to RESOLVED
- TC-J3-03: Complete workflow through all three statuses

**Risk if Broken:** Feedback cannot be properly tracked through its lifecycle; admins lose visibility into progress.

---

### J4: Form Validation (Invalid Data Rejection)

**Why Critical:** Data integrity - prevents garbage data from entering the system. Without validation, database fills with incomplete/invalid records.

**Test Cases:**
- TC-J4-01: Submit form with all empty fields
- TC-J4-02: Submit form with empty student name
- TC-J4-03: Submit form with empty course name
- TC-J4-04: Submit form without selecting rating
- TC-J4-05: Submit form with empty comments
- TC-J4-06: Submit valid form successfully (positive test)

**Risk if Broken:** Database polluted with incomplete data; reports and analytics become unreliable.

---

### J5: Dashboard Analytics Display

**Why Critical:** Admin decision-making depends on accurate metrics and summary data. Incorrect metrics lead to poor management decisions.

**Test Cases:**
- TC-J5-01: Verify all dashboard metrics are visible
- TC-J5-02: Verify total feedback count is accurate
- TC-J5-03: Verify submitted and reviewed counts match actual data
- TC-J5-04: Verify average rating calculation is correct
- TC-J5-05: Verify metrics update after new submission

**Risk if Broken:** Administrators make decisions based on incorrect data; resource allocation and planning suffer.

---

## 5. Test Case Details

| Test ID | Journey | Test Case | Steps | Test Data | Expected Result | Status |
|---------|---------|-----------|-------|-----------|-----------------|--------|
| TC-J1-01 | J1 | Submit valid feedback | 1. Login as student<br>2. Navigate to form<br>3. Fill all fields<br>4. Submit | Student: "AutoTest Student {timestamp}"<br>Course: "Test Course"<br>Rating: 5<br>Comments: "Test comments" | Success message displayed<br>Feedback appears in list | ✅ Pass |
| TC-J1-02 | J1 | Multiple submissions | 1. Submit first feedback<br>2. Submit second feedback<br>3. Verify both in list | Two distinct feedback entries | Both feedbacks visible<br>Count increases | ✅ Pass |
| TC-J2-01 | J2 | Search by student name | 1. Create test feedback<br>2. Search by student name<br>3. Verify filtered results | Search keyword: student name | Only matching feedback shown | ✅ Pass |
| TC-J2-02 | J2 | Search by course name | 1. Create test feedback<br>2. Search by course name<br>3. Verify filtered results | Search keyword: course name | Only matching feedback shown | ✅ Pass |
| TC-J2-03 | J2 | Clear search filter | 1. Apply search filter<br>2. Click Clear<br>3. Verify all results return | Any valid keyword | Full unfiltered list displayed | ✅ Pass |
| TC-J2-04 | J2 | Non-existent search | 1. Search for non-existent keyword | Keyword: unique non-matching string | "No feedback found" or empty list | ✅ Pass |
| TC-J3-01 | J3 | Status SUBMITTED → REVIEWED | 1. Login as admin<br>2. Select feedback<br>3. Change status to REVIEWED | Existing SUBMITTED feedback | Success message<br>Status updated | ✅ Pass |
| TC-J3-02 | J3 | Status REVIEWED → RESOLVED | 1. Login as admin<br>2. Select REVIEWED feedback<br>3. Change to RESOLVED | Existing REVIEWED feedback | Success message<br>Status updated | ✅ Pass |
| TC-J3-03 | J3 | Complete workflow | 1. Start with SUBMITTED<br>2. Move to REVIEWED<br>3. Move to RESOLVED | New feedback | All transitions successful | ✅ Pass |
| TC-J4-01 | J4 | Empty form validation | 1. Navigate to form<br>2. Click Submit without filling | All fields empty | Form not submitted<br>Stays on form page | ✅ Pass |
| TC-J4-02 | J4 | Empty student name | 1. Fill all except student name<br>2. Submit | Student name: empty | Validation error<br>Form not submitted | ✅ Pass |
| TC-J4-03 | J4 | Empty course name | 1. Fill all except course name<br>2. Submit | Course name: empty | Validation error<br>Form not submitted | ✅ Pass |
| TC-J4-04 | J4 | No rating selected | 1. Fill all except rating<br>2. Submit | Rating: not selected | Validation error<br>Form not submitted | ✅ Pass |
| TC-J4-05 | J4 | Empty comments | 1. Fill all except comments<br>2. Submit | Comments: empty | Validation error<br>Form not submitted | ✅ Pass |
| TC-J4-06 | J4 | Valid form submission | 1. Fill all fields correctly<br>2. Submit | All fields valid | Success redirect<br>Feedback created | ✅ Pass |
| TC-J5-01 | J5 | Dashboard metrics visible | 1. Login as admin<br>2. Navigate to dashboard<br>3. Verify all cards visible | N/A | All 4 metric cards displayed | ✅ Pass |
| TC-J5-02 | J5 | Total feedback count | 1. Navigate to dashboard<br>2. Check total count | Test data: 3+ feedback | Count ≥ 3 | ✅ Pass |
| TC-J5-03 | J5 | Status counts accurate | 1. Navigate to dashboard<br>2. Verify submitted & reviewed counts | Test data with known statuses | Counts match actual data | ✅ Pass |
| TC-J5-04 | J5 | Average rating correct | 1. Navigate to dashboard<br>2. Verify average rating | Test data: ratings 3,4,5 | Average between 1.0 and 5.0 | ✅ Pass |
| TC-J5-05 | J5 | Metrics update after submission | 1. Note initial total<br>2. Submit new feedback<br>3. Check dashboard | New feedback submission | Total increases by 1 | ✅ Pass |

---

## 6. Entry Criteria

Before test execution can begin:
- ✅ Application must be running and accessible at configured URL
- ✅ H2 database initialized and accessible
- ✅ Test accounts (student/admin) configured and working
- ✅ Maven and Java 21 installed
- ✅ Chrome browser installed (or configured browser)
- ✅ All Selenium dependencies resolved

---

## 7. Exit Criteria

Test execution is complete when:
- ✅ All 5 critical journeys have been tested
- ✅ All 22 test cases have been executed
- ✅ Test pass rate ≥ 95% (max 1 failing test allowed)
- ✅ Screenshot mechanism verified (intentional failure captured)
- ✅ HTML test report generated
- ✅ Tests run successfully 3 times in a row (stability check)

---

## 8. Test Data Strategy

### Dynamic Test Data:
All test data includes timestamps to ensure uniqueness and avoid collisions between test runs:

```
studentName = "AutoTest Student " + System.currentTimeMillis()
courseName = "Test Course " + System.currentTimeMillis()
```

### Test Data File:
Located at: `src/test/resources/testdata.csv`

Contains pre-defined test cases with:
- Valid submission data (ratings 1-5)
- Invalid data for validation testing
- Expected outcomes

### Data Cleanup:
H2 in-memory database resets on application restart - no manual cleanup needed.

---

## 9. Risks and Mitigations

| Risk | Impact | Likelihood | Mitigation |
|------|--------|------------|------------|
| Application not running | High | Medium | Clear instructions in how-to-run.md |
| Timing issues (flaky tests) | Medium | Medium | Use explicit waits (WebDriverWait), no Thread.sleep |
| Port conflicts | High | Low | Document port requirements, check with netstat |
| Browser driver issues | Medium | Low | Use Selenium Manager for automatic driver management |
| Test data collisions | Low | Medium | Use timestamp-based unique identifiers |
| H2 database locked | Medium | Low | Ensure single application instance |

---

## 10. Test Execution Strategy

### Phases:
1. **Local Development:** Run tests during development to catch issues early
2. **Pre-commit:** Run test suite before committing changes
3. **CI Pipeline (Future - Task 10):** Integrate with Jenkins

### Execution Frequency:
- **On-demand:** Developer manually triggers test run
- **Pre-merge:** Before merging feature branches
- **Scheduled:** Daily regression run (future)

### Reporting:
- Console output for quick feedback
- HTML report (`target/site/surefire-report.html`) for detailed analysis
- Screenshots (`target/screenshots/`) captured on failures

---

## 11. Tools and Technologies

| Tool | Version | Purpose |
|------|---------|---------|
| Selenium WebDriver | 4.27.0 | Browser automation |
| Selenium Manager | Built-in | Automatic driver management |
| JUnit 5 | 5.x (via Spring Boot) | Test framework |
| Maven Surefire Plugin | 3.2.5 | Test execution |
| Chrome/Firefox/Edge | Latest | Test browsers |
| Maven Surefire Report Plugin | 3.2.5 | HTML test reports |

---

## 12. Test Maintenance

### Page Object Pattern:
Tests use Page Object Model for maintainability:
- `LoginPage` - Login operations
- `FeedbackFormPage` - Form submission
- `FeedbackListPage` - List viewing and search
- `DashboardPage` - Dashboard metrics

### When UI Changes:
1. Update only the relevant Page Object class
2. Test classes remain unchanged
3. Re-run affected tests

### Adding New Tests:
1. Identify user journey
2. Create Page Objects if needed
3. Write test class extending `BaseUITest`
4. Add `*UITest.java` suffix for Maven profile
5. Update this test plan

---

## 13. Success Criteria

Task 9 is successful when:
- ✅ All 5 critical journeys covered
- ✅ Minimum 15 test cases implemented (target: 22)
- ✅ Tests execute reliably (3 consecutive successful runs)
- ✅ Screenshot mechanism working
- ✅ HTML report generated
- ✅ Documentation complete
- ✅ Code reviewed and merged

---

## 14. Appendix

### Glossary:
- **Journey:** End-to-end user workflow through the application
- **Page Object:** Design pattern that creates object repository for web elements
- **Explicit Wait:** WebDriverWait for specific conditions (recommended)
- **Implicit Wait:** Global timeout for element location (used sparingly)
- **Headless Mode:** Browser runs without GUI (for CI environments)

### References:
- Selenium Documentation: https://www.selenium.dev/documentation/
- JUnit 5 User Guide: https://junit.org/junit5/docs/current/user-guide/
- Maven Surefire Plugin: https://maven.apache.org/surefire/maven-surefire-plugin/

---

**Document Status:** ✅ Complete  
**Last Updated:** October 2026  
**Approved By:** DevOps Team
