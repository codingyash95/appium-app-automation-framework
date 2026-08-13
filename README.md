# Appium App Automation Framework

Android UI automation for `lk.directpay.newapp`, built on Appium + UiAutomator2 + TestNG + Maven,
scaffolded from the reusable base of the Sampath Vishwa automation suite.

## What's already wired up (reused, no app-specific code)

- `base/DriverManager` — creates/tears down the AndroidDriver session (UiAutomator2), one per thread
- `base/BaseTest` — starts the driver per test class, dumps diagnostics on session start, exposes `logStep()`
- `base/TestListener` + `base/ExtentManager` — ExtentReports HTML report, auto-categorized by test class name, with failure screenshots attached automatically
- `pageobjects/BasePage` — shared `wait`/`waitFor(seconds)`/`byBounds(bounds)` helpers for every page object
- `utils/ConfigReader`, `utils/TestDataReader` — property file readers (env-var overridable via `-Dkey=value`)
- `utils/DiagnosticCapture` — screenshot + page-source dump on demand, written to `target/diagnostics/`

## What you still need to build

- `pageobjects/*` — one class per screen, built from your own Appium Inspector element dumps (same workflow used for Sampath Vishwa: paste the inspector JSON, get the locator + method back)
- `tests/positive/*`, `tests/negative/*` — TestNG test classes, picked up automatically by `testng.xml`'s package scan
- Fill in `src/test/resources/config.properties` and `testdata.properties` (see below)

## Setup

1. Copy `src/test/resources/config.properties.example` → `config.properties`, fill in real values (already pre-filled with `app.package=lk.directpay.newapp` and a placeholder `apk.name` — update `apk.name` once you know the real file name).
2. Copy `src/test/resources/testdata.properties.example` → `testdata.properties`, fill in your test account's credentials. **Never commit either of these files — both are gitignored.**
3. Place your APK at `apps/android/<apk.name from config.properties>` (this path is gitignored — add the APK yourself, per your existing plan).
4. Start Appium server (`appium`) and an emulator/device, then run:
   ```bash
   mvn test
   ```

## Notes carried over from the Sampath Vishwa suite

A few things that turned out to be app-specific there, not universal — re-verify rather than assume for this app:
- `WebElement.sendKeys()` was unreliable on some emulator sessions for that app; `adb shell input text` was used as a workaround. Test this app's own text fields before assuming the same fix is needed.
- Some close/back buttons silently failed to register a WebDriver `click()` on that app (a WebView-hosted-screen quirk) while `driver.navigate().back()` worked reliably. Don't apply this preemptively — only if you hit the same symptom.
- `no.reset=true` plus session-persistence assumptions across test classes was a deliberate choice for a stateful login flow. Decide fresh whether that fits this app's test data / account model.
