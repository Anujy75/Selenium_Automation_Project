package com.anuj.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ShopEaseLoginPage {

    WebDriver driver;

    // Locators
    By emailField = By.name("email");
    By passwordField = By.name("password");
    By loginButton = By.cssSelector("button[type='submit']");
    By errorMessage = By.xpath("//*[contains(@style,'fee2e2') or contains(@style,'dc2626')]");

    public ShopEaseLoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void enterEmail(String email) {
        driver.findElement(emailField).sendKeys(email);
    }

    public void enterPassword(String password) {
        driver.findElement(passwordField).sendKeys(password);
    }

    public void clickLogin() {
        driver.findElement(loginButton).click();
    }

    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLogin();
    }

    public boolean isErrorDisplayed() {
        return driver.findElement(errorMessage).isDisplayed();
    }
}