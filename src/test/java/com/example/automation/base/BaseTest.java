package com.example.automation.base;

import com.aventstack.extentreports.ExtentTest;
import com.example.automation.utils.DiagnosticCapture;
import io.appium.java_client.android.AndroidDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

@Listeners({TestListener.class})
public abstract class BaseTest {
    protected AndroidDriver driver;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        driver = DriverManager.getDriver();
        DiagnosticCapture.dump(driver, "session-start-" + getClass().getSimpleName());
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        DriverManager.quitDriver();
    }

    protected void logStep(String message) {
        ExtentTest test = TestListener.getTest();
        if (test != null) {
            test.info(message);
        }
    }
}
