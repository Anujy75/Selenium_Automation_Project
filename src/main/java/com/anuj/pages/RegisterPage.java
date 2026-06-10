package com.anuj.pages;

import com.anuj.utils.WaitUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class RegisterPage {

    private WebDriver driver;
    private WaitUtils waitUtils;

    // Locators based on your actual Register component
    @FindBy(name = "name")
    private WebElement nameInput;

    @FindBy(name = "email")
    private WebElement emailInput;

    @FindBy(name = "password")
    private WebElement passwordInput;

    @FindBy(name = "phone")
    private WebElement phoneInput;

    @FindBy(name = "address")
    private WebElement addressInput;

    @FindBy(xpath = "//button[contains(text(), 'Create Account') or contains(text(), 'Creating Account')]")
    private WebElement registerButton;

    // Message div (success or error)
    @FindBy(xpath = "//div[contains(@style, 'background') and contains(@style, '#e6f9f0')]")
    private WebElement successMessage;

    @FindBy(xpath = "//div[contains(@style, 'background') and contains(@style, '#fdecea')]")
    private WebElement errorMessage;

    // Any toast/alert message
    @FindBy(xpath = "//div[@role='alert']")
    private WebElement alertMessage;

    // Sign In link
    @FindBy(xpath = "//a[contains(text(), 'Sign In')]")
    private WebElement signInLink;

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
        PageFactory.initElements(driver, this);
    }

    // T070: Automate user registration flow
    public void register(String name, String email, String password, String phone, String address) {
        waitUtils.waitForClickable(nameInput).sendKeys(name);
        waitUtils.waitForClickable(emailInput).sendKeys(email);
        waitUtils.waitForClickable(passwordInput).sendKeys(password);

        if (phone != null && !phone.isEmpty()) {
            waitUtils.waitForClickable(phoneInput).sendKeys(phone);
        }

        if (address != null && !address.isEmpty()) {
            waitUtils.waitForClickable(addressInput).sendKeys(address);
        }

        waitUtils.waitForClickable(registerButton).click();
    }

    //  Get success message (green background)
    public String getSuccessMessage() {
        try {
            waitUtils.waitForVisibility(successMessage);
            return successMessage.getText();
        } catch (Exception e) {
            return "";
        }
    }

    //  get error message (red background)
    public String getErrorMessage() {
        try {
            waitUtils.waitForVisibility(errorMessage);
            return errorMessage.getText();
        } catch (Exception e) {
            return "";
        }
    }

    // Get any alert message
    public String getAlertMessage() {
        try {
            waitUtils.waitForVisibility(alertMessage);
            return alertMessage.getText();
        } catch (Exception e) {
            return "";
        }
    }

    // T071: Validate input fields
    public boolean isNameFieldEmpty() {
        return nameInput.getAttribute("value").isEmpty();
    }

    public boolean isEmailFieldEmpty() {
        return emailInput.getAttribute("value").isEmpty();
    }

    public boolean isPasswordFieldEmpty() {
        return passwordInput.getAttribute("value").isEmpty();
    }

    //  Check if registration was successful (redirect to login page)
    public boolean isRedirectedToLoginPage() {
        waitUtils.hardWait(2500); // Wait for redirect (2 seconds timeout in your code)
        return driver.getCurrentUrl().contains("/login");
    }

    // Click Sign In link
    public void clickSignInLink() {
        waitUtils.waitForClickable(signInLink).click();
    }

    public void clearForm() {
        nameInput.clear();
        emailInput.clear();
        passwordInput.clear();
        phoneInput.clear();
        addressInput.clear();
    }
}
