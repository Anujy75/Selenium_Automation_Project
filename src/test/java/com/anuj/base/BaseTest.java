package com.anuj.base;

import com.anuj.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    // ── Shared test credentials ──────────────────────────────
    protected static final String VALID_EMAIL    = "anujydv@gmail.com";
    protected static final String VALID_PASSWORD = "Anuj@1234";
    protected static final String BASE_URL       = "http://localhost:3000";
    protected static final String LOGIN_URL      = BASE_URL + "/login/user";

    @BeforeMethod
    public void setUp() {
        System.out.println("🚀 Setting up WebDriver...");

        // Chrome options for better stability
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--remote-allow-origins=*");

        // Optional: Run in headless mode (uncomment if needed)
        // options.addArguments("--headless");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

        wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        System.out.println("✅ WebDriver setup complete");

        // ── Check if application is reachable ────────────────
        try {
            System.out.println("🌐 Checking application at: " + BASE_URL);
            driver.get(BASE_URL);
            System.out.println("✅ Application is reachable!");
        } catch (Exception e) {
            System.err.println("❌ ERROR: Application not reachable at " + BASE_URL);
            System.err.println("❌ Please make sure your application is running!");
            System.err.println("❌ Backend: http://localhost:8080");
            System.err.println("❌ Frontend: http://localhost:3000");
            System.err.println("❌ Error: " + e.getMessage());
            // Don't throw exception, let tests fail gracefully
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            System.out.println("🔄 Closing WebDriver...");
            driver.quit();
            System.out.println("✅ WebDriver closed");
        }
    }

    // ── T79: Reusable login utility ──────────────────────────
    /**
     * Navigates to login page and logs in with given credentials.
     * Reuse karo har test mein jahan logged-in state chahiye.
     *
     * Usage: loginAs(VALID_EMAIL, VALID_PASSWORD);
     */
    protected void loginAs(String email, String password) {
        driver.get(LOGIN_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(email, password);
        try { Thread.sleep(1000); } catch (Exception ignored) {}
    }

    /**
     * Default login — valid credentials se login karta hai.
     * Usage: loginAsDefaultUser();
     */
    protected void loginAsDefaultUser() {
        loginAs(VALID_EMAIL, VALID_PASSWORD);
    }

    // ── NEW: Helper method to wait ────────────────────────────
    /**
     * Static sleep method for quick waits
     * Usage: sleep(1000); // waits 1 second
     */
    protected void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ── NEW: Check if application is running ──────────────────
    /**
     * Returns true if application is reachable
     */
    protected boolean isApplicationRunning() {
        try {
            driver.get(BASE_URL);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ── NEW: Navigate to a specific page ──────────────────────
    /**
     * Navigate to any page within the application
     * Usage: navigateTo("/products");
     */
    protected void navigateTo(String path) {
        driver.get(BASE_URL + path);
        sleep(1000);
    }

    // ── NEW: Get current page URL ─────────────────────────────
    /**
     * Returns current page URL
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    // ── NEW: Refresh current page ─────────────────────────────
    /**
     * Refresh the current page
     */
    protected void refreshPage() {
        driver.navigate().refresh();
        sleep(1000);
    }
}