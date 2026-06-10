package com.anuj.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RegisterPage {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.js = (JavascriptExecutor) driver;
    }

    //  Locators
    private By nameField     = By.name("name");
    private By emailField    = By.name("email");
    private By passwordField = By.name("password");
    private By phoneField    = By.name("phone");
    private By addressField  = By.name("address");
    private By submitButton  = By.cssSelector("button[type='submit']");
    private By messageDiv    = By.cssSelector("div[style*='margin-top: 16px']");

    //  Fill form using JavaScript — bypasses HTML5 required validation
    public void register(String name, String email, String password,
                         String phone, String address) {

        fillField(nameField, name);
        fillField(emailField, email);
        fillField(passwordField, password);
        fillField(phoneField, phone);
        fillField(addressField, address);

        // Click via JavaScript — bypasses browser validation
        WebElement btn = driver.findElement(submitButton);
        js.executeScript("arguments[0].click();", btn);
    }

    // Helper — fill using JS to bypass HTML5 validation
    private void fillField(By locator, String value) {
        WebElement el = driver.findElement(locator);
        js.executeScript("arguments[0].removeAttribute('required')", el);
        js.executeScript("arguments[0].value = arguments[1]", el, value);
        // Also trigger React onChange event
        js.executeScript(
                "var nativeInputValueSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                        "nativeInputValueSetter.call(arguments[0], arguments[1]);" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));",
                el, value
        );
    }

    //  Get success message
    public String getSuccessMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(messageDiv));
            String text = driver.findElement(messageDiv).getText();
            System.out.println("DEBUG Success msg: " + text);
            return text;
        } catch (Exception e) {
            System.out.println("DEBUG Success msg not found: " + e.getMessage());
            return "";
        }
    }

    // ✅ Get error message
    public String getErrorMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(messageDiv));
            String text = driver.findElement(messageDiv).getText();
            System.out.println("DEBUG Error msg: " + text);
            return text;
        } catch (Exception e) {
            System.out.println("DEBUG Error msg not found: " + e.getMessage());
            return "";
        }
    }

    // ✅ Click Sign In link
    public void clickSignInLink() {
        driver.findElement(By.linkText("Sign In")).click();
    }
}
