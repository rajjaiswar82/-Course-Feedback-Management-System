package in.college.coursefeedback.ui;

import in.college.coursefeedback.ui.pages.FeedbackFormPage;
import in.college.coursefeedback.ui.pages.FeedbackListPage;
import in.college.coursefeedback.ui.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Journey 1: Student Submits New Feedback (Create)
 * 
 * Critical because: Core business function - primary user action that creates data in the system.
 * If submission fails, no feedback data enters the system.
 */
@DisplayName("J1: Student Feedback Submission")
public class FeedbackSubmissionUITest extends BaseUITest {

    @Test
    @DisplayName("Student should successfully submit valid feedback and see it in the list")
    public void testSubmitValidFeedback() {
        // Arrange
        LoginPage loginPage = new LoginPage(driver);
        FeedbackFormPage formPage = new FeedbackFormPage(driver);
        FeedbackListPage listPage = new FeedbackListPage(driver);
        
        String timestamp = String.valueOf(System.currentTimeMillis());
        String studentName = "AutoTest Student " + timestamp;
        String courseName = "Automated Testing Course " + timestamp;
        String rating = "5";
        String comments = "This is an automated test feedback submission at " + timestamp;
        
        // Act - Step 1: Login as student
        loginPage.navigateTo(baseUrl);
        loginPage.loginAsStudent();
        
        // Act - Step 2: Navigate to feedback form
        formPage.navigateTo(baseUrl);
        assertTrue(formPage.isOnFormPage(), "Should be on feedback form page");
        
        // Act - Step 3: Fill and submit feedback
        formPage.fillAndSubmit(studentName, courseName, rating, comments);
        
        // Assert - Step 4: Verify success message appears
        assertTrue(listPage.hasSuccessMessage(), "Success message should be displayed");
        assertEquals("Feedback submitted successfully.", listPage.getSuccessMessage());
        
        // Assert - Step 5: Verify redirected to feedback list
        assertTrue(listPage.isOnFeedbackListPage(), "Should be redirected to feedback list");
        
        // Assert - Step 6: Verify feedback appears in the list
        assertTrue(listPage.isFeedbackDisplayed(studentName), 
                "Newly submitted feedback should appear in the list");
        
        // Assert - Step 7: Verify feedback count increased
        int feedbackCount = listPage.getFeedbackCount();
        assertTrue(feedbackCount >= 1, "At least one feedback should be visible");
    }
    
    @Test
    @DisplayName("Multiple feedback submissions should all appear in the list")
    public void testMultipleFeedbackSubmissions() {
        // Arrange
        LoginPage loginPage = new LoginPage(driver);
        FeedbackFormPage formPage = new FeedbackFormPage(driver);
        FeedbackListPage listPage = new FeedbackListPage(driver);
        
        loginPage.navigateTo(baseUrl);
        loginPage.loginAsStudent();
        
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        // Act - Submit first feedback
        formPage.navigateTo(baseUrl);
        formPage.fillAndSubmit(
                "Student One " + timestamp,
                "Course A " + timestamp,
                "5",
                "First feedback"
        );
        int countAfterFirst = listPage.getFeedbackCount();
        
        // Act - Submit second feedback
        formPage.navigateTo(baseUrl);
        formPage.fillAndSubmit(
                "Student Two " + timestamp,
                "Course B " + timestamp,
                "4",
                "Second feedback"
        );
        int countAfterSecond = listPage.getFeedbackCount();
        
        // Assert - Count should increase
        assertTrue(countAfterSecond > countAfterFirst, 
                "Feedback count should increase after second submission");
        
        // Assert - Both feedbacks should be visible
        assertTrue(listPage.isFeedbackDisplayed("Student One " + timestamp));
        assertTrue(listPage.isFeedbackDisplayed("Student Two " + timestamp));
    }
}
