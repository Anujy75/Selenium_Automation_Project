package com.anuj.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LogoutPage {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    // ✅ Locators
    private By emailField    = By.name("email");
    private By passwordField = By.name("password");
    private By loginButton   = By.cssSelector("button[type='submit']");

    // ✅ Fixed — button with logout style (background: #fff7f7)
    private By signOutButton  = By.cssSelector("button[style*='fff7f7']");
    private By signOutByText  = By.xpath("//*[contains(text(),'Sign out')]");
    private By signOutByBtn   = By.xpath("//button[.//*[contains(text(),'Sign out')]]");

    public LogoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.js     = (JavascriptExecutor) driver;
    }

    // ✅ Login
    public void login(String email, String password) {
        driver.get("http://localhost:3000/login/user");
        WebElement emailEl = wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
        emailEl.clear();
        emailEl.sendKeys(email);
        WebElement passEl = driver.findElement(passwordField);
        passEl.clear();
        passEl.sendKeys(password);
        driver.findElement(loginButton).click();
        try { Thread.sleep(2500); } catch (Exception e) {}
    }

    // ✅ Profile page
    public void goToProfile() {
        driver.get("http://localhost:3000/profile");
        try { Thread.sleep(2500); } catch (Exception e) {} // Framer Motion wait
    }

    // ✅ Click Sign out — 4 fallback locators
    public void clickSignOut() {
        WebElement btn = null;

        // Try 1: button style
        try {
            btn = wait.until(ExpectedConditions.elementToBeClickable(signOutButton));
            System.out.println("✅ Found via: button[style*='fff7f7']");
        } catch (Exception e) { System.out.println("⚠️ Try 1 failed..."); }

        // Try 2: button containing span with text
        if (btn == null) {
            try {
                btn = wait.until(ExpectedConditions.elementToBeClickable(signOutByBtn));
                System.out.println("✅ Found via: button > Sign out text");
            } catch (Exception e) { System.out.println("⚠️ Try 2 failed..."); }
        }

        // Try 3: any element with text
        if (btn == null) {
            try {
                btn = wait.until(ExpectedConditions.elementToBeClickable(signOutByText));
                System.out.println("✅ Found via: xpath text");
            } catch (Exception e) { System.out.println("⚠️ Try 3 failed..."); }
        }

        // Try 4: JavaScript
        if (btn == null) {
            try {
                btn = (WebElement) js.executeScript(
                        "return Array.from(document.querySelectorAll('button'))" +
                                ".find(b => b.textContent.trim().includes('Sign out'));"
                );
                System.out.println("✅ Found via: JavaScript");
            } catch (Exception e) { System.out.println("❌ All locators failed"); }
        }

        if (btn != null) {
            js.executeScript("arguments[0].scrollIntoView(true);", btn);
            try { Thread.sleep(500); } catch (Exception e) {}
            js.executeScript("arguments[0].click();", btn);
            System.out.println("✅ Sign out clicked!");
        } else {
            throw new RuntimeException("Sign out button not found!");
        }
    }

    public String getCurrentUrl() {
        try { Thread.sleep(1500); } catch (Exception e) {}
        return driver.getCurrentUrl();
    }

    public Object getTokenFromStorage(String key) {
        return js.executeScript("return localStorage.getItem('" + key + "');");
    }

    public boolean areAllTokensCleared() {
        Object token      = getTokenFromStorage("token");
        Object custToken  = getTokenFromStorage("customerToken");
        Object adminToken = getTokenFromStorage("adminToken");
        Object role       = getTokenFromStorage("role");
        System.out.println("DEBUG token: "        + token);
        System.out.println("DEBUG customerToken: " + custToken);
        System.out.println("DEBUG adminToken: "   + adminToken);
        System.out.println("DEBUG role: "         + role);
        return token == null && custToken == null && adminToken == null && role == null;
    }

    public void tryAccessProtectedPage() {
        driver.get("http://localhost:3000/profile");
        try { Thread.sleep(1500); } catch (Exception e) {}
    }
}