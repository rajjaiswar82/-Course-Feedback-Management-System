package in.college.coursefeedback.ui;

import in.college.coursefeedback.ui.pages.FeedbackFormPage;
import in.college.coursefeedback.ui.pages.FeedbackListPage;
import in.college.coursefeedback.ui.pages.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Journey 4: Form Validation (Invalid Data Rejection)
 * 
 * Critical because: Data integrity - prevents garbage data from entering the system.
 * Without validation, database fills with incomplete/invalid records.
 */
@DisplayName("J4: Form Validation and Invalid Data Rejection")
public class FeedbackValidationUITest extends BaseUITest {

    @BeforeEach
    public void setupLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateTo(baseUrl);
        loginPage.loginAsStudent();
    }

    @Test
    @DisplayName("Submitting form with all empty fields should show validation errors")
    public void testEmptyFormValidation() {
        // Arrange
        FeedbackFormPage formPage = new FeedbackFormPage(driver);
        FeedbackListPage listPage = new FeedbackListPage(driver);
        
        formPage.navigateTo(baseUrl);
        int initialFeedbackCount = 0;
        
        // Get initial count (may navigate to list)
        listPage.navigateTo(baseUrl);
        initialFeedbackCount = listPage.getFeedbackCount();
        
        // Navigate back to form
        formPage.navigateTo(baseUrl);
        
        // Act - Submit empty form (don't fill any fields)
        formPage.submitForm();
        
        // Assert - Should still be on form page (HTML5 validation prevents submission)
        assertTrue(formPage.isOnFormPage(), 
                "Should remain on form page when validation fails");
        
        // Assert - If we navigate to list, count should not have increased
        listPage.navigateTo(baseUrl);
        int finalFeedbackCount = listPage.getFeedbackCount();
        assertEquals(initialFeedbackCount, finalFeedbackCount, 
                "No feedback should be created with empty form");
    }
    
    @Test
    @DisplayName("Submitting form with empty student name should fail validation")
    public void testEmptyStudentNameValidation() {
        // Arrange
        FeedbackFormPage formPage = new FeedbackFormPage(driver);
        formPage.navigateTo(baseUrl);
        
        // Act - Fill form but leave student name empty
        formPage.fillForm("", "Test Course", "4", "Some comments");
        formPage.submitForm();
        
        // Assert - Should remain on form page
        assertTrue(formPage.isOnFormPage(), 
                "Should remain on form page with empty student name");
    }
    
    @Test
    @DisplayName("Submitting form with empty course name should fail validation")
    public void testEmptyCourseNameValidation() {
        // Arrange
        FeedbackFormPage formPage = new FeedbackFormPage(driver);
        formPage.navigateTo(baseUrl);
        
        // Act - Fill form but leave course name empty
        formPage.fillForm("Test Student", "", "3", "Some comments");
        formPage.submitForm();
        
        // Assert - Should remain on form page
        assertTrue(formPage.isOnFormPage(), 
                "Should remain on form page with empty course name");
    }
    
    @Test
    @DisplayName("Submitting form without selecting rating should fail validation")
    public void testEmptyRatingValidation() {
        // Arrange
        FeedbackFormPage formPage = new FeedbackFormPage(driver);
        formPage.navigateTo(baseUrl);
        
        // Act - Fill form but don't select rating
        formPage.fillForm("Test Student", "Test Course", null, "Some comments");
        formPage.submitForm();
        
        // Assert - Should remain on form page
        assertTrue(formPage.isOnFormPage(), 
                "Should remain on form page without rating");
    }
    
    @Test
    @DisplayName("Submitting form with empty comments should fail validation")
    public void testEmptyCommentsValidation() {
        // Arrange
        FeedbackFormPage formPage = new FeedbackFormPage(driver);
        formPage.navigateTo(baseUrl);
        
        // Act - Fill form but leave comments empty
        formPage.fillForm("Test Student", "Test Course", "5", "");
        formPage.submitForm();
        
        // Assert - Should remain on form page
        assertTrue(formPage.isOnFormPage(), 
                "Should remain on form page with empty comments");
    }
    
    @Test
    @DisplayName("Valid form should submit successfully and not show errors")
    public void testValidFormSubmission() {
        // Arrange
        FeedbackFormPage formPage = new FeedbackFormPage(driver);
        FeedbackListPage listPage = new FeedbackListPage(driver);
        
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        formPage.navigateTo(baseUrl);
        
        // Act - Fill with valid data
        formPage.fillAndSubmit(
                "Valid Student " + timestamp,
                "Valid Course " + timestamp,
                "4",
                "Valid comments for testing"
        );
        
        // Assert - Should redirect to list page with success message
        assertTrue(listPage.isOnFeedbackListPage(), 
                "Should redirect to feedback list on successful submission");
        assertTrue(listPage.hasSuccessMessage(), 
                "Success message should be displayed");
        assertFalse(formPage.isOnFormPage(), 
                "Should not remain on form page after successful submission");
    }
}
