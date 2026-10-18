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
 * Page Object for the feedback submission form page.
 */
public class FeedbackFormPage {
    
    private final WebDriver driver;
    private final WebDriverWait wait;
    
    // Locators
    private final By studentNameInput = By.id("studentName");
    private final By courseNameInput = By.id("courseName");
    private final By ratingSelect = By.id("rating");
    private final By commentsTextarea = By.id("comments");
    private final By submitButton = By.cssSelector("button[type='submit']");
    private final By errorMessages = By.cssSelector("small.error");
    
    public FeedbackFormPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    /**
     * Navigate to the feedback form page.
     */
    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/feedback/new");
    }
    
    /**
     * Fill the feedback form with provided data.
     */
    public void fillForm(String studentName, String courseName, String rating, String comments) {
        wait.until(ExpectedConditions.presenceOfElementLocated(studentNameInput));
        
        if (studentName != null) {
            WebElement studentField = driver.findElement(studentNameInput);
            studentField.clear();
            studentField.sendKeys(studentName);
        }
        
        if (courseName != null) {
            WebElement courseField = driver.findElement(courseNameInput);
            courseField.clear();
            courseField.sendKeys(courseName);
        }
        
        if (rating != null) {
            Select ratingDropdown = new Select(driver.findElement(ratingSelect));
            ratingDropdown.selectByValue(rating);
        }
        
        if (comments != null) {
            WebElement commentsField = driver.findElement(commentsTextarea);
            commentsField.clear();
            commentsField.sendKeys(comments);
        }
    }
    
    /**
     * Submit the feedback form.
     */
    public void submitForm() {
        WebElement submit = wait.until(ExpectedConditions.elementToBeClickable(submitButton));
        submit.click();
    }
    
    /**
     * Fill and submit the form in one action.
     */
    public void fillAndSubmit(String studentName, String courseName, String rating, String comments) {
        fillForm(studentName, courseName, rating, comments);
        submitForm();
    }
    
    /**
     * Get all error messages displayed on the form.
     */
    public List<String> getErrorMessages() {
        wait.until(ExpectedConditions.presenceOfElementLocated(errorMessages));
        List<WebElement> errors = driver.findElements(errorMessages);
        return errors.stream()
                .map(WebElement::getText)
                .filter(text -> !text.trim().isEmpty())
                .toList();
    }
    
    /**
     * Check if error messages are displayed.
     */
    public boolean hasErrors() {
        List<WebElement> errors = driver.findElements(errorMessages);
        return !errors.isEmpty() && errors.stream().anyMatch(e -> !e.getText().trim().isEmpty());
    }
    
    /**
     * Check if currently on the feedback form page.
     */
    public boolean isOnFormPage() {
        return driver.getCurrentUrl().contains("/feedback/new");
    }
}
