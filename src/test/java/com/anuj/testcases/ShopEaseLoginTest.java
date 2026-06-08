package com.anuj.tests;

import com.anuj.base.BaseTest;
import com.anuj.pages.ShopEaseLoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ShopEaseLoginTest extends BaseTest {

    @Test
    public void testValidLogin() throws InterruptedException {
        driver.get("http://localhost:3000/login/user");

        ShopEaseLoginPage loginPage = new ShopEaseLoginPage(driver);
        loginPage.login("anujydv@gmail.com", "Anuj@1234");

        Thread.sleep(5000);

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/home"),
                "Valid login ke baad /home nahi aaya!"
        );
    }

    @Test
    public void testInvalidLogin() throws InterruptedException {
        driver.get("http://localhost:3000/login/user");

        ShopEaseLoginPage loginPage = new ShopEaseLoginPage(driver);
        loginPage.login("anujydv@gmail.com", "Anuj@1234");

        Thread.sleep(3000);

        Assert.assertTrue(
                loginPage.isErrorDisplayed(),
                "Error message nahi aaya!"
        );
    }
}