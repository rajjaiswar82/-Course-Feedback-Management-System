package in.college.coursefeedback.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for the dashboard page.
 */
public class DashboardPage {
    
    private final WebDriver driver;
    private final WebDriverWait wait;
    
    // Locators
    private final By totalFeedbackCard = By.id("total-feedback");
    private final By submittedCountCard = By.id("submitted-count");
    private final By reviewedCountCard = By.id("reviewed-count");
    private final By averageRatingCard = By.id("average-rating");
    
    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    /**
     * Navigate to the dashboard page.
     */
    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/dashboard");
    }
    
    /**
     * Get the total feedback count from the dashboard.
     */
    public int getTotalFeedback() {
        WebElement card = wait.until(ExpectedConditions.presenceOfElementLocated(totalFeedbackCard));
        WebElement value = card.findElement(By.tagName("h2"));
        return Integer.parseInt(value.getText().trim());
    }
    
    /**
     * Get the submitted count from the dashboard.
     */
    public int getSubmittedCount() {
        WebElement card = wait.until(ExpectedConditions.presenceOfElementLocated(submittedCountCard));
        WebElement value = card.findElement(By.tagName("h2"));
        return Integer.parseInt(value.getText().trim());
    }
    
    /**
     * Get the reviewed count from the dashboard.
     */
    public int getReviewedCount() {
        WebElement card = wait.until(ExpectedConditions.presenceOfElementLocated(reviewedCountCard));
        WebElement value = card.findElement(By.tagName("h2"));
        return Integer.parseInt(value.getText().trim());
    }
    
    /**
     * Get the average rating from the dashboard.
     */
    public double getAverageRating() {
        WebElement card = wait.until(ExpectedConditions.presenceOfElementLocated(averageRatingCard));
        WebElement value = card.findElement(By.tagName("h2"));
        String ratingText = value.getText().trim().replace(",", ".");
        return Double.parseDouble(ratingText);
    }
    
    /**
     * Check if the dashboard metrics are visible.
     */
    public boolean areMetricsVisible() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(totalFeedbackCard));
            wait.until(ExpectedConditions.presenceOfElementLocated(submittedCountCard));
            wait.until(ExpectedConditions.presenceOfElementLocated(reviewedCountCard));
            wait.until(ExpectedConditions.presenceOfElementLocated(averageRatingCard));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if currently on dashboard page.
     */
    public boolean isOnDashboardPage() {
        return driver.getCurrentUrl().contains("/dashboard");
    }
}
