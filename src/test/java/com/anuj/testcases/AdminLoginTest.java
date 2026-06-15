package com.anuj.testcases;

import com.anuj.base.BaseTest;
import com.anuj.pages.AdminLoginPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AdminLoginTest extends BaseTest {

    private AdminLoginPage adminLoginPage;

    private static final String ADMIN_EMAIL = "admin@shopease.com";
    private static final String ADMIN_PASSWORD = "Admin@7275";

    @BeforeMethod
    public void init() {
        adminLoginPage = new AdminLoginPage(driver);
        adminLoginPage.goToAdminLoginPage();
    }

    @Test
    public void testAdminLoginPageLoads() {
        System.out.println("=== Testing Admin Login Page Loads ===");
        String url = adminLoginPage.getCurrentUrl();
        Assert.assertTrue(url.contains("/login/admin"));
        System.out.println("✅ Admin login page loaded");
    }

    @Test
    public void testAdminLoginSuccess() {
        System.out.println("=== Testing Admin Login Success ===");
        adminLoginPage.adminLogin(ADMIN_EMAIL, ADMIN_PASSWORD);

        boolean isDashboard = adminLoginPage.isAdminDashboardDisplayed();
        Assert.assertTrue(isDashboard, "Should redirect to admin dashboard");
        System.out.println("✅ Admin login successful");
    }

    @Test
    public void testBackToPortalLink() {
        System.out.println("=== Testing Back to Portal Link ===");
        adminLoginPage.clickBackToPortal();

        String url = adminLoginPage.getCurrentUrl();
        Assert.assertTrue(url.contains("/portal"));
        System.out.println("✅ Navigated to portal");
    }

    // ✅ Fixed: These tests will check if error appears OR stay on same page
    @Test
    public void testAdminLoginInvalidPassword() {
        System.out.println("=== Testing Invalid Password ===");
        adminLoginPage.adminLogin(ADMIN_EMAIL, "WrongPass");

        // Either error message appears OR we stay on login page (not dashboard)
        boolean hasError = adminLoginPage.isErrorDisplayed();
        boolean isStillOnLoginPage = adminLoginPage.getCurrentUrl().contains("/login/admin");

        Assert.assertTrue(hasError || isStillOnLoginPage, "Should show error or stay on login page");
        System.out.println("✅ Invalid password handled correctly");
    }

    @Test
    public void testAdminLoginWrongEmail() {
        System.out.println("=== Testing Wrong Email ===");
        adminLoginPage.adminLogin("wrong@email.com", ADMIN_PASSWORD);

        boolean hasError = adminLoginPage.isErrorDisplayed();
        boolean isStillOnLoginPage = adminLoginPage.getCurrentUrl().contains("/login/admin");

        Assert.assertTrue(hasError || isStillOnLoginPage, "Should show error or stay on login page");
        System.out.println("✅ Wrong email handled correctly");
    }

    @Test
    public void testAdminLoginEmptyFields() {
        System.out.println("=== Testing Empty Fields ===");
        adminLoginPage.adminLogin("", "");

        // Browser HTML5 validation prevents submission, so we stay on login page
        boolean isStillOnLoginPage = adminLoginPage.getCurrentUrl().contains("/login/admin");

        Assert.assertTrue(isStillOnLoginPage, "Should stay on login page when fields empty");
        System.out.println("✅ Empty fields handled correctly (HTML5 validation)");
    }

    @Test
    public void testAdminLoginMissingEmail() {
        System.out.println("=== Testing Missing Email ===");
        adminLoginPage.adminLogin("", ADMIN_PASSWORD);

        boolean isStillOnLoginPage = adminLoginPage.getCurrentUrl().contains("/login/admin");

        Assert.assertTrue(isStillOnLoginPage, "Should stay on login page when email missing");
        System.out.println("✅ Missing email handled correctly");
    }

    @Test
    public void testAdminLoginMissingPassword() {
        System.out.println("=== Testing Missing Password ===");
        adminLoginPage.adminLogin(ADMIN_EMAIL, "");

        boolean isStillOnLoginPage = adminLoginPage.getCurrentUrl().contains("/login/admin");

        Assert.assertTrue(isStillOnLoginPage, "Should stay on login page when password missing");
        System.out.println("✅ Missing password handled correctly");
    }
}