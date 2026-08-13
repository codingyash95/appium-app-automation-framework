package com.example.automation.base;

import com.example.automation.utils.ConfigReader;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class DriverManager {
    private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();

    public static AndroidDriver getDriver() {
        if (DRIVER.get() == null) {
            DRIVER.set(createDriver());
        }
        return DRIVER.get();
    }

    public static AndroidDriver peekDriver() {
        return DRIVER.get();
    }

    public static void quitDriver() {
        AndroidDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }

    private static AndroidDriver createDriver() {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setDeviceName(ConfigReader.get("device.name"));
        options.setApp(resolveApkPath());
        options.setNoReset(ConfigReader.getBoolean("no.reset"));
        options.setCapability("appium:adbExecTimeout", ConfigReader.getInt("adb.exec.timeout.ms"));

        try {
            URL serverUrl = URI.create(ConfigReader.get("appium.server.url")).toURL();
            return new AndroidDriver(serverUrl, options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid appium.server.url in config.properties", e);
        }
    }

    private static String resolveApkPath() {
        String apkPath = new File(System.getProperty("user.dir"), "apps/android/" + ConfigReader.get("apk.name")).getAbsolutePath();
        if (!new File(apkPath).exists()) {
            throw new RuntimeException("Test APK not found at " + apkPath
                    + ". Place the APK at apps/android/ before running tests.");
        }
        return apkPath;
    }
}
