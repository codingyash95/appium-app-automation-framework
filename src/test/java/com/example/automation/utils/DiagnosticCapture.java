package com.example.automation.utils;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DiagnosticCapture {
    private static final Path DIR = Paths.get("target", "diagnostics");

    public static void dump(AndroidDriver driver, String label) {
        try {
            Files.createDirectories(DIR);
            String timestamp = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS").format(LocalDateTime.now());
            Path base = DIR.resolve(label + "-" + timestamp);

            try {
                Files.write(Paths.get(base + ".png"), driver.getScreenshotAs(OutputType.BYTES));
            } catch (WebDriverException e) {
                System.err.println("[DiagnosticCapture] screenshot failed for '" + label + "': " + e.getMessage());
            }

            try {
                Files.writeString(Paths.get(base + ".xml"), driver.getPageSource());
            } catch (WebDriverException e) {
                System.err.println("[DiagnosticCapture] page source failed for '" + label + "': " + e.getMessage());
            }

            System.out.println("[DiagnosticCapture] dumped diagnostics under " + base);
        } catch (IOException e) {
            System.err.println("[DiagnosticCapture] failed to capture diagnostics for '" + label + "': " + e.getMessage());
        }
    }
}
