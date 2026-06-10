package com.anuj.testcases;

import com.anuj.base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;

public class NavigationTest extends BaseTest {

    @Test
    public void testNavigationToProductsPage() {
        WebElement productsLink = driver.findElement(By.linkText("Products"));
        productsLink.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/products"));

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/products"), "Should navigate to products page");
        System.out.println("Navigated to: " + currentUrl);
    }

    @Test
    public void testNavigationToCartPage() {
        WebElement cartLink = driver.findElement(By.linkText("Cart"));
        cartLink.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/cart"));

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/cart"), "Should navigate to cart page");
        System.out.println(" Navigated to: " + currentUrl);
    }

    @Test
    public void testNavigationToHomePage() {
        WebElement homeLink = driver.findElement(By.linkText("Home"));
        homeLink.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/"));

        String currentUrl = driver.getCurrentUrl();
        System.out.println("Navigated to Home: " + currentUrl);
    }
}
