package com.anuj.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class AdminLoginPage {

    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(name = "email")
    private WebElement emailInput;

    @FindBy(name = "password")
    private WebElement passwordInput;

    @FindBy(xpath = "//button[contains(text(), 'Login as Admin')]")
    private WebElement loginButton;

    // ✅ Multiple error message locators
    @FindBy(xpath = "//div[contains(@style, '#fee2e2')] | //div[@class='error-message'] | //div[contains(@class, 'error')]")
    private WebElement errorMessage;

    @FindBy(xpath = "//a[contains(text(), 'Back to Login Portal')]")
    private WebElement backToPortalLink;

    public AdminLoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void goToAdminLoginPage() {
        driver.get("http://localhost:3000/login/admin");
    }

    public void adminLogin(String email, String password) {
        emailInput.clear();
        passwordInput.clear();

        if (email != null && !email.isEmpty()) {
            emailInput.sendKeys(email);
        }
        if (password != null && !password.isEmpty()) {
            passwordInput.sendKeys(password);
        }

        loginButton.click();
        waitForPageLoad();
    }

    private void waitForPageLoad() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public String getErrorMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOf(errorMessage));
            return errorMessage.getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isErrorDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(errorMessage));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAdminDashboardDisplayed() {
        return driver.getCurrentUrl().contains("/admin/dashboard");
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public void clickBackToPortal() {
        wait.until(ExpectedConditions.elementToBeClickable(backToPortalLink)).click();
    }
}