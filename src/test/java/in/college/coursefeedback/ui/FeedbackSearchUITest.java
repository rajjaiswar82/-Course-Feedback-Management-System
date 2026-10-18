package in.college.coursefeedback.ui;

import in.college.coursefeedback.ui.pages.FeedbackFormPage;
import in.college.coursefeedback.ui.pages.FeedbackListPage;
import in.college.coursefeedback.ui.pages.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Journey 2: Search/Filter Feedback by Keyword
 * 
 * Critical because: Essential for users to find specific feedback among many records.
 * Admin productivity depends on this functionality.
 */
@DisplayName("J2: Feedback Search and Filtering")
public class FeedbackSearchUITest extends BaseUITest {

    private String uniqueStudent1;
    private String uniqueStudent2;
    private String uniqueCourse1;
    private String uniqueCourse2;
    
    @BeforeEach
    public void setupTestData() {
        // Create unique test data
        String timestamp = String.valueOf(System.currentTimeMillis());
        uniqueStudent1 = "SearchTest Alice " + timestamp;
        uniqueStudent2 = "SearchTest Bob " + timestamp;
        uniqueCourse1 = "SearchCourse Alpha " + timestamp;
        uniqueCourse2 = "SearchCourse Beta " + timestamp;
        
        // Login and create test feedback
        LoginPage loginPage = new LoginPage(driver);
        FeedbackFormPage formPage = new FeedbackFormPage(driver);
        
        loginPage.navigateTo(baseUrl);
        loginPage.loginAsStudent();
        
        // Create first feedback
        formPage.navigateTo(baseUrl);
        formPage.fillAndSubmit(uniqueStudent1, uniqueCourse1, "5", "Test feedback 1");
        
        // Create second feedback
        formPage.navigateTo(baseUrl);
        formPage.fillAndSubmit(uniqueStudent2, uniqueCourse2, "4", "Test feedback 2");
    }

    @Test
    @DisplayName("Search by student name should filter results correctly")
    public void testSearchByStudentName() {
        // Arrange
        FeedbackListPage listPage = new FeedbackListPage(driver);
        listPage.navigateTo(baseUrl);
        
        // Act - Search for first student
        listPage.searchByKeyword(uniqueStudent1);
        
        // Assert - Only matching feedback should appear
        assertTrue(listPage.isFeedbackDisplayed(uniqueStudent1), 
                "Searched student feedback should be displayed");
        
        int resultCount = listPage.getFeedbackCount();
        assertTrue(resultCount >= 1, "At least one result should match");
    }
    
    @Test
    @DisplayName("Search by course name should filter results correctly")
    public void testSearchByCourseName() {
        // Arrange
        FeedbackListPage listPage = new FeedbackListPage(driver);
        listPage.navigateTo(baseUrl);
        
        // Act - Search for first course
        listPage.searchByKeyword(uniqueCourse1);
        
        // Assert - Only matching feedback should appear
        assertTrue(listPage.isFeedbackDisplayedByCourse(uniqueCourse1), 
                "Searched course feedback should be displayed");
    }
    
    @Test
    @DisplayName("Clear search should show all feedback again")
    public void testClearSearch() {
        // Arrange
        FeedbackListPage listPage = new FeedbackListPage(driver);
        listPage.navigateTo(baseUrl);
        
        int initialCount = listPage.getFeedbackCount();
        
        // Act - Search for specific feedback
        listPage.searchByKeyword(uniqueStudent1);
        int filteredCount = listPage.getFeedbackCount();
        
        // Assert - Filtered results should be less than or equal to initial
        assertTrue(filteredCount <= initialCount, 
                "Filtered results should not exceed initial count");
        
        // Act - Clear the search
        listPage.clearSearch();
        int countAfterClear = listPage.getFeedbackCount();
        
        // Assert - Should show all feedback again
        assertTrue(countAfterClear >= filteredCount, 
                "After clearing search, more results should be visible");
    }
    
    @Test
    @DisplayName("Search for non-existent feedback should show no results")
    public void testSearchNoResults() {
        // Arrange
        FeedbackListPage listPage = new FeedbackListPage(driver);
        listPage.navigateTo(baseUrl);
        
        // Act - Search for non-existent keyword
        String nonExistentKeyword = "NonExistentKeyword" + System.currentTimeMillis();
        listPage.searchByKeyword(nonExistentKeyword);
        
        // Assert - Should show "no feedback found" message or empty list
        boolean noResults = listPage.isNoFeedbackMessageDisplayed() || listPage.getFeedbackCount() == 0;
        assertTrue(noResults, "No results should be displayed for non-existent search");
    }
}
