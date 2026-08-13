# Appium App Automation Framework

A reusable starting point for Android UI automation, built on Appium + UiAutomator2 + TestNG +
Maven. The base infrastructure (driver management, reporting, config/test-data loading) is
app-agnostic — plug in your own app and build page objects/tests on top.

## What's already wired up (reused, no app-specific code)

- `base/DriverManager` — creates/tears down the AndroidDriver session (UiAutomator2), one per thread
- `base/BaseTest` — starts the driver per test class, dumps diagnostics on session start, exposes `logStep()`
- `base/TestListener` + `base/ExtentManager` — ExtentReports HTML report, auto-categorized by test class name, with failure screenshots attached automatically
- `pageobjects/BasePage` — shared `wait`/`waitFor(seconds)`/`byBounds(bounds)` helpers for every page object
- `utils/ConfigReader`, `utils/TestDataReader` — property file readers (env-var overridable via `-Dkey=value`)
- `utils/DiagnosticCapture` — screenshot + page-source dump on demand, written to `target/diagnostics/`

## What you still need to build

- `pageobjects/*` — one class per screen, built from your own Appium Inspector element dumps
- `tests/positive/*`, `tests/negative/*` — TestNG test classes, picked up automatically by `testng.xml`'s package scan
- Fill in `src/test/resources/config.properties` and `testdata.properties` (see below)

## Setup

1. Copy `src/test/resources/config.properties.example` → `config.properties`, and fill in your app's real Android package name (`app.package`) and APK file name (`apk.name`).
2. Copy `src/test/resources/testdata.properties.example` → `testdata.properties`, fill in your test account's credentials. **Never commit either of these files — both are gitignored.**
3. Place your APK at `apps/android/<apk.name from config.properties>` (this path is gitignored — add the APK yourself).
4. Start Appium server (`appium`) and an emulator/device, then run:
   ```bash
   mvn test
   ```

## A few things to watch for when writing page objects

These aren't universal truths — just common pitfalls worth checking early rather than assuming:
- `WebElement.sendKeys()` can be unreliable on some emulator sessions for text fields; `adb shell input text` is a reliable fallback if you see fields silently not receiving keystrokes.
- Some apps have close/back buttons that don't register a WebDriver `click()` reliably (often WebView-hosted screens) while `driver.navigate().back()` works — only apply this workaround if you actually hit the symptom, not preemptively.
- `no.reset=true` plus session-persistence assumptions across test classes only makes sense if your test suite is designed to run sequentially against one continuous app session. Decide this deliberately based on your app's login/session model.
