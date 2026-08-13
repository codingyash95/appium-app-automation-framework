package com.example.automation.base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {
    private static final String REPORT_PATH = "target/extent-reports/report.html";
    private static ExtentReports extent;

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            extent = createInstance();
        }
        return extent;
    }

    private static ExtentReports createInstance() {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(REPORT_PATH);
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle("App Automation Test Results");
        sparkReporter.config().setReportName("App Automation Report");

        ExtentReports extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);
        // TODO: replace with your app's package name once known
        extentReports.setSystemInfo("Application", "com.example.app");
        extentReports.setSystemInfo("Platform", "Android");
        extentReports.setSystemInfo("Environment", "UAT");
        extentReports.setSystemInfo("Automation Tool", "Appium + TestNG");

        return extentReports;
    }
}
