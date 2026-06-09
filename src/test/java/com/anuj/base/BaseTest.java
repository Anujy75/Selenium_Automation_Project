package com.anuj.base;

import com.anuj.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public class BaseTest {

    protected WebDriver driver;

    // ── Shared test credentials ──────────────────────────────
    protected static final String VALID_EMAIL    = "anujydv@gmail.com";
    protected static final String VALID_PASSWORD = "Anuj@1234";
    protected static final String BASE_URL       = "http://localhost:3000";
    protected static final String LOGIN_URL      = BASE_URL + "/login/user";

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get(BASE_URL);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
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
        try { Thread.sleep(2000); } catch (Exception ignored) {}
    }

    /**
     * Default login — valid credentials se login karta hai.
     * Usage: loginAsDefaultUser();
     */
    protected void loginAsDefaultUser() {
        loginAs(VALID_EMAIL, VALID_PASSWORD);
    }
}