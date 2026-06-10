package com.anuj.testcases;

import com.anuj.base.BaseTest;
import com.anuj.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeMethod
    public void initPages() {
        loginPage = new LoginPage(driver);
        driver.get(LOGIN_URL); // BaseTest se aa raha hai — no hardcoding
    }

    // ── T072: Valid credentials ──────────────────────────────
    @Test
    public void testLoginWithValidCredentials() {
        loginPage.login(VALID_EMAIL, VALID_PASSWORD); // BaseTest se credentials
        try { Thread.sleep(3000); } catch (Exception ignored) {}

        String url = loginPage.getCurrentUrl();
        System.out.println("After login URL: " + url);

        Assert.assertFalse(
                url.equals(LOGIN_URL),
                "Should redirect after successful login. URL: " + url
        );
    }

    // ── T073: Invalid credentials ────────────────────────────
    @Test
    public void testLoginWithInvalidCredentials() {
        loginPage.login("wrong@gmail.com", "WrongPass@123");
        try { Thread.sleep(2000); } catch (Exception ignored) {}

        String url      = loginPage.getCurrentUrl();
        String errorMsg = loginPage.getErrorMessage();
        System.out.println("Invalid login - URL: " + url + " | Error: " + errorMsg);

        Assert.assertTrue(
                url.contains("/login") || !errorMsg.isEmpty(),
                "Should show error or stay on login page"
        );
    }

    // ── T073: Wrong password ─────────────────────────────────
    @Test
    public void testLoginWithWrongPassword() {
        loginPage.login(VALID_EMAIL, "WrongPass999");
        try { Thread.sleep(2000); } catch (Exception ignored) {}

        String url      = loginPage.getCurrentUrl();
        String errorMsg = loginPage.getErrorMessage();
        System.out.println(" Wrong password - URL: " + url + " | Error: " + errorMsg);

        Assert.assertTrue(
                url.contains("/login") || !errorMsg.isEmpty(),
                "Should show error or stay on login page"
        );
    }

    // ── T074: Empty fields ───────────────────────────────────
    @Test
    public void testLoginWithEmptyFields() {
        loginPage.login("", "");
        try { Thread.sleep(1000); } catch (Exception ignored) {}

        String url = loginPage.getCurrentUrl();
        System.out.println("Empty fields URL: " + url);
        Assert.assertTrue(url.contains("/login"), "Should stay on login page");
    }

    // ── T074: Invalid email format ───────────────────────────
    @Test
    public void testLoginWithInvalidEmailFormat() {
        loginPage.login("notanemail", "Test@1234");
        try { Thread.sleep(1000); } catch (Exception ignored) {}

        String url = loginPage.getCurrentUrl();
        System.out.println(" Invalid email URL: " + url);
        Assert.assertTrue(url.contains("/login"), "Should stay on login page");
    }

    // ── T075: Assert error message ───────────────────────────
    @Test
    public void testErrorMessageContent() {
        loginPage.login("invalid@test.com", "wrongpass");
        try { Thread.sleep(2000); } catch (Exception ignored) {}

        String errorMsg = loginPage.getErrorMessage();
        String url      = loginPage.getCurrentUrl();
        System.out.println(" Error: " + errorMsg + " | URL: " + url);

        Assert.assertTrue(
                url.contains("/login") || !errorMsg.isEmpty(),
                "Should show error or stay on login page"
        );
    }

    // ── T075: Page title assert ──────────────────────────────
    @Test
    public void testLoginPageTitle() {
        String title = driver.getTitle();
        System.out.println("Page title: " + title);
        Assert.assertFalse(title.isEmpty(), "Page title should not be empty");
    }

    // ── T079 Demo: loginAsDefaultUser() reuse ────────────────
    // Agar kisi aur test mein logged-in state chahiye toh:
    // loginAsDefaultUser(); // bas yeh ek line — BaseTest se aata hai
}
