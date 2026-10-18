package in.college.coursefeedback.ui;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;

/**
 * JUnit 5 TestWatcher extension that captures screenshots on test failure.
 * Automatically invoked when a test fails.
 */
public class ScreenshotOnFailureExtension implements TestWatcher {

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        // Get the test instance and check if it's a BaseUITest
        Optional<Object> testInstance = context.getTestInstance();
        
        if (testInstance.isPresent() && testInstance.get() instanceof BaseUITest) {
            BaseUITest baseTest = (BaseUITest) testInstance.get();
            
            // Generate test name for screenshot filename
            String testName = context.getTestClass()
                    .map(Class::getSimpleName)
                    .orElse("UnknownClass") + 
                    "_" + 
                    context.getTestMethod()
                    .map(method -> method.getName())
                    .orElse("unknownMethod");
            
            // Capture screenshot
            baseTest.captureScreenshot(testName);
        }
    }
}
