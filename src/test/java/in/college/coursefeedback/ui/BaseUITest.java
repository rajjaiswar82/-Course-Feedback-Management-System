package in.college.coursefeedback.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Base class for Selenium UI tests.
 * Handles WebDriver setup, configuration, teardown, and screenshot capture.
 */
@ExtendWith(ScreenshotOnFailureExtension.class)
public abstract class BaseUITest {

    protected WebDriver driver;
    protected String baseUrl;
    protected static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    @BeforeEach
    public void setUp() {
        // Get configuration from system properties with defaults
        baseUrl = System.getProperty("base.url", "http://localhost:8080");
        String browser = System.getProperty("browser", "chrome").toLowerCase();
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        // Initialize WebDriver based on browser choice
        driver = createDriver(browser, headless);
        
        // Configure implicit wait and window size
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        driver.manage().window().maximize();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Create WebDriver instance based on browser type.
     * Uses Selenium Manager (Selenium 4+) for automatic driver management.
     */
    private WebDriver createDriver(String browser, boolean headless) {
        return switch (browser) {
            case "firefox" -> {
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("--headless");
                }
                yield new FirefoxDriver(options);
            }
            case "edge" -> {
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless");
                }
                yield new EdgeDriver(options);
            }
            default -> {
                // Chrome is default
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless");
                }
                options.addArguments("--disable-gpu");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                yield new ChromeDriver(options);
            }
        };
    }

    /**
     * Capture screenshot and save to target/screenshots directory.
     * Called automatically on test failure via ScreenshotOnFailureExtension.
     */
    public void captureScreenshot(String testName) {
        if (driver == null) {
            return;
        }

        try {
            // Create screenshots directory
            Path screenshotDir = Paths.get("target", "screenshots");
            Files.createDirectories(screenshotDir);

            // Generate filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = String.format("%s_%s.png", testName, timestamp);
            Path screenshotPath = screenshotDir.resolve(filename);

            // Capture and save screenshot
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), screenshotPath);

            System.out.println("Screenshot saved: " + screenshotPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
        }
    }

    /**
     * Navigate to a path relative to the base URL.
     */
    protected void navigateTo(String path) {
        String url = baseUrl + (path.startsWith("/") ? path : "/" + path);
        driver.get(url);
    }

    /**
     * Get the WebDriver instance for use in tests.
     */
    public WebDriver getDriver() {
        return driver;
    }
}
