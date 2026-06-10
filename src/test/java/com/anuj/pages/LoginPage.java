package com.anuj.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private WebDriver driver;
    private WebDriverWait wait;

    //  Locators — tumhare Login.js ke name attributes
    private By emailField    = By.name("email");
    private By passwordField = By.name("password");
    private By loginButton   = By.cssSelector("button[type='submit']");
    private By errorMessage  = By.cssSelector("div[style*='fee2e2']");

    //  Constructor
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    //  Enter email
    public void enterEmail(String email) {
        WebElement field = wait.until(
                ExpectedConditions.visibilityOfElementLocated(emailField));
        field.clear();
        field.sendKeys(email);
    }

    //  Enter password
    public void enterPassword(String password) {
        WebElement field = driver.findElement(passwordField);
        field.clear();
        field.sendKeys(password);
    }

    // Click login button
    public void clickLogin() {
        driver.findElement(loginButton).click();
    }

    //  Full login action
    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLogin();
    }

    //  Get error message
    public String getErrorMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
            return driver.findElement(errorMessage).getText();
        } catch (Exception e) {
            System.out.println("DEBUG Error msg not found: " + e.getMessage());
            return "";
        }
    }

    //  Check if redirected after login
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
