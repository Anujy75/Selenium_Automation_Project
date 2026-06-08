package com.anuj.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RegisterPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // ✅ Constructor
    public RegisterPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ✅ Locators — name attribute se (tumhare form me yahi hai)
    private By nameField     = By.name("name");
    private By emailField    = By.name("email");
    private By passwordField = By.name("password");
    private By phoneField    = By.name("phone");
    private By addressField  = By.name("address");
    private By submitButton  = By.cssSelector("button[type='submit']");

    // Success/Error message locator
    private By successMessage = By.cssSelector("div[style*='#e6f9f0'], div[style*='2e7d32']");
    private By errorMessage   = By.cssSelector("div[style*='#fdecea'], div[style*='c62828']");

    // ✅ Fill and submit form
    public void register(String name, String email, String password,
                         String phone, String address) {

        // Clear and fill name
        WebElement name_field = driver.findElement(nameField);
        name_field.clear();
        name_field.sendKeys(name);

        // Clear and fill email
        WebElement email_field = driver.findElement(emailField);
        email_field.clear();
        email_field.sendKeys(email);

        // Clear and fill password
        WebElement pass_field = driver.findElement(passwordField);
        pass_field.clear();
        pass_field.sendKeys(password);

        // Clear and fill phone
        WebElement phone_field = driver.findElement(phoneField);
        phone_field.clear();
        phone_field.sendKeys(phone);

        // Clear and fill address
        WebElement addr_field = driver.findElement(addressField);
        addr_field.clear();
        addr_field.sendKeys(address);

        // Click submit
        driver.findElement(submitButton).click();
    }

    // ✅ Get success message
    public String getSuccessMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage));
            return driver.findElement(successMessage).getText();
        } catch (Exception e) {
            return "";
        }
    }

    // ✅ Get error message
    public String getErrorMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
            return driver.findElement(errorMessage).getText();
        } catch (Exception e) {
            return "";
        }
    }

    // ✅ Click Sign In link
    public void clickSignInLink() {
        driver.findElement(By.linkText("Sign In")).click();
    }
}