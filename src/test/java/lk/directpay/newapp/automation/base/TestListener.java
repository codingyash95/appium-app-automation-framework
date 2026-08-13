package lk.directpay.newapp.automation.base;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Base64;

public class TestListener implements ITestListener {

    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        String description = result.getMethod().getDescription();
        String title = (description != null && !description.isEmpty())
                ? description
                : result.getMethod().getMethodName();
        ExtentTest test = ExtentManager.getInstance().createTest(title);
        test.assignCategory(deriveModuleCategory(result.getTestClass().getRealClass()));
        extentTest.set(test);
    }

    // Groups the report's "Categories" view by test module (e.g. "Dashboard", "Login", "Logout")
    // instead of leaving every test in one flat list, by stripping the class name's common test
    // suffixes and splitting the remaining camelCase words — works for any current or future test
    // class without needing a hardcoded per-class mapping.
    private String deriveModuleCategory(Class<?> testClass) {
        String name = testClass.getSimpleName();
        String[] suffixesToStrip = {"VerificationTest", "NegativeTest", "SmokeTest", "DataDrivenTest", "E2ETest", "Test"};
        for (String suffix : suffixesToStrip) {
            if (name.endsWith(suffix)) {
                name = name.substring(0, name.length() - suffix.length());
                break;
            }
        }
        return name.replaceAll("(?<!^)(?=[A-Z])", " ");
    }

    public static ExtentTest getTest() {
        return extentTest.get();
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            test.pass("Test passed");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = extentTest.get();
        if (test == null) {
            return;
        }

        Object driver = DriverManager.peekDriver();
        if (driver != null) {
            try {
                byte[] screenshotBytes = captureScreenshot(driver);
                String base64Screenshot = Base64.getEncoder().encodeToString(screenshotBytes);
                test.fail(result.getThrowable(),
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build());
            } catch (WebDriverException | ClassCastException e) {
                test.fail(result.getThrowable());
                test.warning("Could not capture failure screenshot: " + e.getMessage());
            }
        } else {
            test.fail(result.getThrowable());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = extentTest.get();
        if (test == null) {
            return;
        }

        if (result.getThrowable() != null) {
            test.skip(result.getThrowable());
        } else {
            test.skip("Test skipped");
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.getInstance().flush();
    }

    public byte[] captureScreenshot(Object driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
