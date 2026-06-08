package com.anuj.tests;

import com.anuj.base.BaseTest;
import com.anuj.pages.LoginPage;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test
    public void testLogin() {
        driver.get("https://www.google.com"); // test with google for now
        System.out.println("Title: " + driver.getTitle());
    }
}