package in.college.coursefeedback.ui;

import in.college.coursefeedback.ui.pages.FeedbackFormPage;
import in.college.coursefeedback.ui.pages.FeedbackListPage;
import in.college.coursefeedback.ui.pages.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Journey 3: Admin Updates Feedback Status (Workflow)
 * 
 * Critical because: Represents the feedback lifecycle management.
 * Status workflow (SUBMITTED → REVIEWED → RESOLVED) is key to tracking feedback progress.
 */
@DisplayName("J3: Feedback Status Workflow Management")
public class FeedbackStatusWorkflowUITest extends BaseUITest {

    private String testStudent;
    private String testCourse;
    
    @BeforeEach
    public void setupTestData() {
        // Create test feedback as student
        String timestamp = String.valueOf(System.currentTimeMillis());
        testStudent = "StatusTest Student " + timestamp;
        testCourse = "StatusTest Course " + timestamp;
        
        LoginPage loginPage = new LoginPage(driver);
        FeedbackFormPage formPage = new FeedbackFormPage(driver);
        
        loginPage.navigateTo(baseUrl);
        loginPage.loginAsStudent();
        
        formPage.navigateTo(baseUrl);
        formPage.fillAndSubmit(testStudent, testCourse, "4", "Test feedback for status workflow");
        
        // Logout by navigating to login page
        driver.get(baseUrl + "/logout");
    }

    @Test
    @DisplayName("Admin should be able to change feedback status from SUBMITTED to REVIEWED")
    public void testUpdateStatusToReviewed() {
        // Arrange - Login as admin
        LoginPage loginPage = new LoginPage(driver);
        FeedbackListPage listPage = new FeedbackListPage(driver);
        
        loginPage.navigateTo(baseUrl);
        loginPage.loginAsAdmin();
        
        listPage.navigateTo(baseUrl);
        
        // Act - Update first feedback to REVIEWED
        listPage.updateFirstFeedbackStatus("REVIEWED");
        
        // Assert - Success message should appear
        assertTrue(listPage.hasSuccessMessage(), "Success message should be displayed");
        assertEquals("Feedback status updated successfully.", listPage.getSuccessMessage());
    }
    
    @Test
    @DisplayName("Admin should be able to change feedback status from REVIEWED to RESOLVED")
    public void testUpdateStatusToResolved() {
        // Arrange - Login as admin and set initial status to REVIEWED
        LoginPage loginPage = new LoginPage(driver);
        FeedbackListPage listPage = new FeedbackListPage(driver);
        
        loginPage.navigateTo(baseUrl);
        loginPage.loginAsAdmin();
        
        listPage.navigateTo(baseUrl);
        
        // Set to REVIEWED first
        listPage.updateFirstFeedbackStatus("REVIEWED");
        
        // Act - Update to RESOLVED
        listPage.navigateTo(baseUrl); // Refresh to get updated state
        listPage.updateFirstFeedbackStatus("RESOLVED");
        
        // Assert - Success message should appear
        assertTrue(listPage.hasSuccessMessage(), "Success message should be displayed");
    }
    
    @Test
    @DisplayName("Complete workflow: SUBMITTED → REVIEWED → RESOLVED")
    public void testCompleteStatusWorkflow() {
        // Arrange - Login as admin
        LoginPage loginPage = new LoginPage(driver);
        FeedbackListPage listPage = new FeedbackListPage(driver);
        
        loginPage.navigateTo(baseUrl);
        loginPage.loginAsAdmin();
        
        listPage.navigateTo(baseUrl);
        
        // Act & Assert - Step 1: SUBMITTED → REVIEWED
        listPage.updateFirstFeedbackStatus("REVIEWED");
        assertTrue(listPage.hasSuccessMessage(), "Status update to REVIEWED should succeed");
        
        // Act & Assert - Step 2: REVIEWED → RESOLVED
        listPage.navigateTo(baseUrl);
        listPage.updateFirstFeedbackStatus("RESOLVED");
        assertTrue(listPage.hasSuccessMessage(), "Status update to RESOLVED should succeed");
        
        // Assert - Feedback still exists in list
        assertTrue(listPage.getFeedbackCount() >= 1, "Feedback should still be in the list");
    }
}
