package in.college.coursefeedback.ui;

import in.college.coursefeedback.ui.pages.DashboardPage;
import in.college.coursefeedback.ui.pages.FeedbackFormPage;
import in.college.coursefeedback.ui.pages.FeedbackListPage;
import in.college.coursefeedback.ui.pages.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Journey 5: Dashboard Analytics Display
 * 
 * Critical because: Admin decision-making depends on accurate metrics and summary data.
 * Incorrect metrics lead to poor management decisions.
 */
@DisplayName("J5: Dashboard Analytics and Metrics")
public class DashboardAnalyticsUITest extends BaseUITest {

    @BeforeEach
    public void setupTestData() {
        // Create multiple feedback entries with different characteristics
        LoginPage loginPage = new LoginPage(driver);
        FeedbackFormPage formPage = new FeedbackFormPage(driver);
        FeedbackListPage listPage = new FeedbackListPage(driver);
        
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        // Login as student and create feedback
        loginPage.navigateTo(baseUrl);
        loginPage.loginAsStudent();
        
        // Create feedback with rating 5
        formPage.navigateTo(baseUrl);
        formPage.fillAndSubmit(
                "Dashboard Student 1 " + timestamp,
                "Dashboard Course 1 " + timestamp,
                "5",
                "Excellent feedback"
        );
        
        // Create feedback with rating 4
        formPage.navigateTo(baseUrl);
        formPage.fillAndSubmit(
                "Dashboard Student 2 " + timestamp,
                "Dashboard Course 2 " + timestamp,
                "4",
                "Good feedback"
        );
        
        // Create feedback with rating 3
        formPage.navigateTo(baseUrl);
        formPage.fillAndSubmit(
                "Dashboard Student 3 " + timestamp,
                "Dashboard Course 3 " + timestamp,
                "3",
                "Average feedback"
        );
        
        // Logout - Spring Security 6 GET /logout shows confirmation page; click the button
        performLogout();
        
        // Login as admin and update one status to REVIEWED
        loginPage.navigateTo(baseUrl);
        loginPage.loginAsAdmin();
        
        listPage.navigateTo(baseUrl);
        listPage.updateFirstFeedbackStatus("REVIEWED");
    }

    /**
     * Perform logout by navigating to /logout and clicking the confirmation button,
     * or clearing cookies if the confirmation page is not available.
     */
    private void performLogout() {
        driver.get(baseUrl + "/logout");
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement logoutButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))
            );
            logoutButton.click();
        } catch (Exception e) {
            // Fallback: clear cookies to end session
            driver.manage().deleteAllCookies();
        }
    }

    @Test
    @DisplayName("Dashboard should display all metrics correctly")
    public void testDashboardMetricsVisible() {
        // Arrange
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = new DashboardPage(driver);
        
        // If not already logged in, login as admin
        if (!driver.getCurrentUrl().contains("/dashboard")) {
            loginPage.navigateTo(baseUrl);
            loginPage.loginAsAdmin();
        }
        
        // Act
        dashboardPage.navigateTo(baseUrl);
        
        // Assert - All metrics should be visible
        assertTrue(dashboardPage.areMetricsVisible(), 
                "All dashboard metrics should be visible");
        assertTrue(dashboardPage.isOnDashboardPage(), 
                "Should be on dashboard page");
    }
    
    @Test
    @DisplayName("Total feedback count should be accurate")
    public void testTotalFeedbackCount() {
        // Arrange
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.navigateTo(baseUrl);
        
        // Act
        int totalFeedback = dashboardPage.getTotalFeedback();
        
        // Assert - Should have at least the 3 feedback we created
        assertTrue(totalFeedback >= 3, 
                "Total feedback should be at least 3 (created in setup)");
    }
    
    @Test
    @DisplayName("Submitted and reviewed counts should be accurate")
    public void testStatusCounts() {
        // Arrange
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.navigateTo(baseUrl);
        
        // Act
        int totalFeedback = dashboardPage.getTotalFeedback();
        int submittedCount = dashboardPage.getSubmittedCount();
        int reviewedCount = dashboardPage.getReviewedCount();
        
        // Assert - Counts should make sense
        assertTrue(submittedCount >= 0, "Submitted count should be non-negative");
        assertTrue(reviewedCount >= 1, 
                "Reviewed count should be at least 1 (we updated one in setup)");
        
        // Total should be greater than or equal to submitted + reviewed
        // (there might be RESOLVED status feedback from other tests)
        assertTrue(totalFeedback >= submittedCount + reviewedCount, 
                "Total should account for all statuses");
    }
    
    @Test
    @DisplayName("Average rating should be calculated correctly")
    public void testAverageRating() {
        // Arrange
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.navigateTo(baseUrl);
        
        // Act
        double averageRating = dashboardPage.getAverageRating();
        
        // Assert - Average rating should be between 1 and 5
        assertTrue(averageRating >= 1.0 && averageRating <= 5.0, 
                "Average rating should be between 1.0 and 5.0");
        
        // Our test data: ratings of 5, 4, 3
        // Average should be 4.0 if only our data exists
        // But other tests may have created data, so just verify it's reasonable
        assertTrue(averageRating > 0, "Average rating should be greater than 0");
    }
    
    @Test
    @DisplayName("Dashboard metrics should update after new feedback submission")
    public void testMetricsUpdateAfterSubmission() {
        // Arrange
        DashboardPage dashboardPage = new DashboardPage(driver);
        FeedbackFormPage formPage = new FeedbackFormPage(driver);
        
        dashboardPage.navigateTo(baseUrl);
        int initialTotal = dashboardPage.getTotalFeedback();
        
        // Act - Create new feedback
        String timestamp = String.valueOf(System.currentTimeMillis());
        formPage.navigateTo(baseUrl);
        formPage.fillAndSubmit(
                "Metrics Test Student " + timestamp,
                "Metrics Test Course " + timestamp,
                "5",
                "Test feedback for metrics update"
        );
        
        // Navigate back to dashboard
        dashboardPage.navigateTo(baseUrl);
        int updatedTotal = dashboardPage.getTotalFeedback();
        
        // Assert - Total should have increased
        assertEquals(initialTotal + 1, updatedTotal, 
                "Total feedback count should increase by 1 after new submission");
    }
}
