package com.anuj.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage {

    WebDriver driver;
    String url = "https://www.google.com"; // replace with your project URL later

    // Locators — by ID
    By searchBox = By.id("APjFqb");                        // Google search box id
    By searchBtn = By.cssSelector("input[value='Google Search']"); // CSS selector

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    // Actions
    public void open() {
        driver.get(url);
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public void searchFor(String keyword) {
        driver.findElement(searchBox).sendKeys(keyword);
        driver.findElement(searchBox).submit();
    }
}