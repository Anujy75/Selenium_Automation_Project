package com.anuj.testcases;

import com.anuj.base.BaseTest;
import com.anuj.pages.LogoutPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LogoutTest extends BaseTest {

    private LogoutPage logoutPage;

    //  Valid credentials
    private static final String VALID_EMAIL    = "anujydv@gmail.com";
    private static final String VALID_PASSWORD = "Anuj@1234";

    @BeforeMethod
    public void initPages() {
        logoutPage = new LogoutPage(driver);
    }

    // ─────────────────────────────────────────────────────────────
    // T076 — Automate logout functionality
    // ─────────────────────────────────────────────────────────────

    //  T076a: Login → Profile → Sign out → redirect to /portal
    @Test
    public void testLogoutRedirectsToPortal() {
        logoutPage.login(VALID_EMAIL, VALID_PASSWORD);
        logoutPage.goToProfile();
        logoutPage.clickSignOut();

        String url = logoutPage.getCurrentUrl();
        System.out.println(" After logout URL: " + url);

        Assert.assertTrue(
                url.contains("/portal"),
                "Should redirect to /portal after logout. Actual: " + url
        );
    }

    // Sign out button visible on profile page
    @Test
    public void testSignOutButtonVisible() {
        logoutPage.login(VALID_EMAIL, VALID_PASSWORD);
        logoutPage.goToProfile();

        // Page source mein "Sign out" text hona chahiye
        String pageSource = driver.getPageSource();
        Assert.assertTrue(
                pageSource.contains("Sign out"),
                "Sign out button should be visible on profile page"
        );
        System.out.println(" Sign out button found on profile page");
    }

    // ─────────────────────────────────────────────────────────────
    // T077 — Validate session handling
    // ─────────────────────────────────────────────────────────────

    //  T077a: After logout — all localStorage tokens cleared
    @Test
    public void testTokensClearedAfterLogout() {
        logoutPage.login(VALID_EMAIL, VALID_PASSWORD);
        logoutPage.goToProfile();
        logoutPage.clickSignOut();

        boolean cleared = logoutPage.areAllTokensCleared();
        System.out.println(" All tokens cleared: " + cleared);

        Assert.assertTrue(cleared,
                "All localStorage tokens should be cleared after logout");
    }

    //  T077b: After logout — token specifically null
    @Test
    public void testTokenNullAfterLogout() {
        logoutPage.login(VALID_EMAIL, VALID_PASSWORD);
        logoutPage.goToProfile();
        logoutPage.clickSignOut();

        Object token = logoutPage.getTokenFromStorage("token");
        System.out.println(" token after logout: " + token);

        Assert.assertNull(token, "token should be null after logout");
    }

    //  T077c: After logout — protected page access redirects away
    @Test
    public void testProtectedPageAfterLogout() {
        logoutPage.login(VALID_EMAIL, VALID_PASSWORD);
        logoutPage.goToProfile();
        logoutPage.clickSignOut();

        // Profile page access try karo bina login ke
        logoutPage.tryAccessProtectedPage();

        String url = logoutPage.getCurrentUrl();
        System.out.println(" Protected page access URL after logout: " + url);

        // Should NOT stay on /profile — redirect to login or portal
        Assert.assertFalse(
                url.equals("http://localhost:3000/profile"),
                "Should not access profile page after logout. URL: " + url
        );
    }

    //  T077d: Session does not persist after logout (fresh login required)
    @Test
    public void testSessionDoesNotPersistAfterLogout() {
        // Login → logout
        logoutPage.login(VALID_EMAIL, VALID_PASSWORD);
        logoutPage.goToProfile();
        logoutPage.clickSignOut();

        // Token cleared verify karo
        Object token = logoutPage.getTokenFromStorage("token");

        // Ab direct profile visit karo
        logoutPage.tryAccessProtectedPage();
        String url = logoutPage.getCurrentUrl();

        System.out.println("✅ Session check — token: " + token + " | URL: " + url);

        Assert.assertTrue(
                token == null || url.contains("/login") || url.contains("/portal"),
                "Session should not persist after logout"
        );
    }
}
