package in.college.coursefeedback.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object for the feedback list page.
 */
public class FeedbackListPage {
    
    private final WebDriver driver;
    private final WebDriverWait wait;
    
    // Locators
    private final By keywordInput = By.id("keyword");
    private final By searchButton = By.cssSelector("button[type='submit']");
    private final By clearButton = By.cssSelector("a.button.link-button");
    private final By successMessage = By.id("success-message");
    private final By feedbackCards = By.cssSelector("#feedback-cards-container .feedback-card");
    private final By noFeedbackMessage = By.cssSelector(".empty.panel");
    
    public FeedbackListPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    /**
     * Navigate to the feedback list page.
     */
    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/feedback");
    }
    
    /**
     * Search for feedback by keyword.
     */
    public void searchByKeyword(String keyword) {
        wait.until(ExpectedConditions.presenceOfElementLocated(keywordInput));
        
        WebElement keywordField = driver.findElement(keywordInput);
        keywordField.clear();
        keywordField.sendKeys(keyword);
        
        driver.findElement(searchButton).click();
        
        // Wait for page reload
        wait.until(ExpectedConditions.or(
            ExpectedConditions.presenceOfElementLocated(feedbackCards),
            ExpectedConditions.presenceOfElementLocated(noFeedbackMessage)
        ));
    }
    
    /**
     * Clear the search filter.
     */
    public void clearSearch() {
        WebElement clear = driver.findElement(clearButton);
        clear.click();
    }
    
    /**
     * Get the success message text if displayed.
     */
    public String getSuccessMessage() {
        try {
            WebElement message = wait.until(ExpectedConditions.presenceOfElementLocated(successMessage));
            return message.getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Check if success message is displayed.
     */
    public boolean hasSuccessMessage() {
        return !getSuccessMessage().isEmpty();
    }
    
    /**
     * Get the count of feedback cards displayed.
     */
    public int getFeedbackCount() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(feedbackCards));
            return driver.findElements(feedbackCards).size();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Check if a specific feedback is displayed by student name.
     */
    public boolean isFeedbackDisplayed(String studentName) {
        List<WebElement> cards = driver.findElements(feedbackCards);
        return cards.stream()
                .anyMatch(card -> card.getText().contains(studentName));
    }
    
    /**
     * Check if a specific feedback is displayed by course name.
     */
    public boolean isFeedbackDisplayedByCourse(String courseName) {
        List<WebElement> cards = driver.findElements(feedbackCards);
        return cards.stream()
                .anyMatch(card -> card.getText().contains(courseName));
    }
    
    /**
     * Get all feedback cards.
     */
    public List<WebElement> getAllFeedbackCards() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(feedbackCards));
            return driver.findElements(feedbackCards);
        } catch (Exception e) {
            return List.of();
        }
    }
    
    /**
     * Get status of a specific feedback by data-feedback-id.
     */
    public String getFeedbackStatus(String feedbackId) {
        By feedbackCard = By.cssSelector("[data-feedback-id='" + feedbackId + "']");
        By statusBadge = By.cssSelector("[data-status]");
        
        WebElement card = wait.until(ExpectedConditions.presenceOfElementLocated(feedbackCard));
        WebElement status = card.findElement(statusBadge);
        
        return status.getAttribute("data-status");
    }
    
    /**
     * Update the status of a feedback by its ID.
     */
    public void updateFeedbackStatus(String feedbackId, String newStatus) {
        By feedbackCard = By.cssSelector("[data-feedback-id='" + feedbackId + "']");
        
        WebElement card = wait.until(ExpectedConditions.presenceOfElementLocated(feedbackCard));
        
        // Find the status select dropdown within this card
        WebElement statusSelect = card.findElement(By.cssSelector("select[name='status']"));
        Select dropdown = new Select(statusSelect);
        dropdown.selectByValue(newStatus);
        
        // Click the save status button
        WebElement saveButton = card.findElement(By.cssSelector("button[type='submit']"));
        saveButton.click();
        
        // Wait for page reload and success message
        wait.until(ExpectedConditions.presenceOfElementLocated(successMessage));
    }
    
    /**
     * Update status of the first feedback found.
     */
    public void updateFirstFeedbackStatus(String newStatus) {
        List<WebElement> cards = getAllFeedbackCards();
        if (cards.isEmpty()) {
            throw new IllegalStateException("No feedback cards found");
        }
        
        WebElement firstCard = cards.get(0);
        String feedbackId = firstCard.getAttribute("data-feedback-id");
        updateFeedbackStatus(feedbackId, newStatus);
    }
    
    /**
     * Check if "no feedback found" message is displayed.
     */
    public boolean isNoFeedbackMessageDisplayed() {
        try {
            driver.findElement(noFeedbackMessage);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if currently on feedback list page.
     */
    public boolean isOnFeedbackListPage() {
        return driver.getCurrentUrl().contains("/feedback") 
                && !driver.getCurrentUrl().contains("/new")
                && !driver.getCurrentUrl().contains("/edit");
    }
}
