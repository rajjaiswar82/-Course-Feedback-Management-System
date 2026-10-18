package in.college.coursefeedback.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for the login page.
 */
public class LoginPage {
    
    private final WebDriver driver;
    private final WebDriverWait wait;
    
    // Locators
    private final By usernameInput = By.id("username");
    private final By passwordInput = By.id("password");
    private final By loginButton = By.cssSelector("button[type='submit']");
    
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    /**
     * Navigate to the login page.
     */
    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/login");
    }
    
    /**
     * Perform login with username and password.
     */
    public void login(String username, String password) {
        wait.until(ExpectedConditions.presenceOfElementLocated(usernameInput));
        
        WebElement usernameField = driver.findElement(usernameInput);
        WebElement passwordField = driver.findElement(passwordInput);
        WebElement submitButton = driver.findElement(loginButton);
        
        usernameField.clear();
        usernameField.sendKeys(username);
        
        passwordField.clear();
        passwordField.sendKeys(password);
        
        submitButton.click();
    }
    
    /**
     * Login as student with default credentials.
     */
    public void loginAsStudent() {
        login("student", "Student@123");
    }
    
    /**
     * Login as admin with default credentials.
     */
    public void loginAsAdmin() {
        login("admin", "Admin@123");
    }
    
    /**
     * Check if currently on login page.
     */
    public boolean isOnLoginPage() {
        return driver.getCurrentUrl().contains("/login");
    }
}
