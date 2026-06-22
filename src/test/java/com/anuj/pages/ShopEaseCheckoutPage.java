package com.anuj.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class ShopEaseCheckoutPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // ============ CART PAGE CHECKOUT FORM LOCATORS ============

    // Checkout button on cart page - MULTIPLE LOCATORS
    private By checkoutBtn1 = By.xpath("//button[contains(text(),'Proceed To Checkout')]");
    private By checkoutBtn2 = By.xpath("//button[contains(text(),'Checkout')]");
    private By checkoutBtn3 = By.xpath("//a[contains(text(),'Checkout')]");

    // Checkout form fields (on cart page)
    private By firstName = By.id("firstName");
    private By lastName = By.id("lastName");
    private By email = By.id("email");
    private By phone = By.id("phone");
    private By addressLine1 = By.id("addressLine1");
    private By addressLine2 = By.id("addressLine2");
    private By city = By.id("city");
    private By state = By.id("state");
    private By zipCode = By.id("zipCode");
    private By country = By.id("country");

    // Place Order button (triggers Razorpay)
    private By placeOrderBtn = By.xpath("//button[contains(text(),'Place Order')]");
    private By placeOrderBtn2 = By.xpath("//button[contains(text(),'Place Order') or contains(@class,'place-order')]");

    // Error/Validation Messages
    private By fieldErrors = By.cssSelector(".field-error, .error-message, .text-red-500, .invalid-feedback, [class*='error']");
    private By requiredIndicator = By.cssSelector("[required]");

    // Razorpay Locators
    private By razorpayFrame = By.xpath("//iframe[contains(@src, 'razorpay')]");
    private By razorpayPayBtn = By.xpath("//button[contains(text(),'Pay')]");
    private By razorpayCloseBtn = By.xpath("//button[contains(@class,'close') or contains(@class,'cross')]");

    // Success/Confirmation
    private By orderConfirmation = By.xpath("//*[contains(text(),'Order Placed') or contains(@class,'success')]");

    public ShopEaseCheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        PageFactory.initElements(driver, this);
    }

    // ===================== NAVIGATION =====================

    public void clickProceedToCheckoutIfExists() {
        System.out.println("🔄 Checking for Proceed To Checkout button...");

        // Try multiple locators
        By[] checkoutButtons = {checkoutBtn1, checkoutBtn2, checkoutBtn3};

        for (By locator : checkoutButtons) {
            try {
                List<WebElement> buttons = driver.findElements(locator);
                if (!buttons.isEmpty() && buttons.get(0).isDisplayed()) {
                    WebElement btn = buttons.get(0);
                    scrollToElement(btn);
                    Thread.sleep(500);
                    btn.click();
                    System.out.println("✅ Clicked: " + locator);
                    Thread.sleep(2000);
                    return;
                }
            } catch (Exception e) {
                // Continue to next locator
            }
        }

        System.out.println("ℹ️ No checkout button found - form might already be visible");
    }

    public boolean isCheckoutFormDisplayed() {
        try {
            // Check if any form field is displayed
            boolean formDisplayed = !driver.findElements(firstName).isEmpty() ||
                    !driver.findElements(email).isEmpty() ||
                    !driver.findElements(phone).isEmpty();
            System.out.println("Checkout form displayed: " + formDisplayed);
            return formDisplayed;
        } catch (Exception e) {
            return false;
        }
    }

    // ===================== FORM ACTIONS =====================

    public void fillShippingDetails(String fName, String lName, String mail,
                                    String phn, String addr, String cty,
                                    String st, String zip, String cntry) {
        System.out.println("📝 Filling shipping details...");

        try {
            WebElement field = driver.findElement(firstName);
            field.clear();
            field.sendKeys(fName);
        } catch (Exception e) { System.out.println("⚠️ First name field not found"); }

        try { driver.findElement(lastName).sendKeys(lName); } catch (Exception e) {}
        try { driver.findElement(email).sendKeys(mail); } catch (Exception e) {}
        try { driver.findElement(phone).sendKeys(phn); } catch (Exception e) {}
        try { driver.findElement(addressLine1).sendKeys(addr); } catch (Exception e) {}
        try { driver.findElement(city).sendKeys(cty); } catch (Exception e) {}
        try { driver.findElement(state).sendKeys(st); } catch (Exception e) {}
        try { driver.findElement(zipCode).sendKeys(zip); } catch (Exception e) {}
        try { driver.findElement(country).sendKeys(cntry); } catch (Exception e) {}

        System.out.println("✅ Shipping details filled");
        try { Thread.sleep(500); } catch (Exception e) {}
    }

    public void clearAllFields() {
        try { driver.findElement(firstName).clear(); } catch (Exception e) {}
        try { driver.findElement(lastName).clear(); } catch (Exception e) {}
        try { driver.findElement(email).clear(); } catch (Exception e) {}
        try { driver.findElement(phone).clear(); } catch (Exception e) {}
        try { driver.findElement(addressLine1).clear(); } catch (Exception e) {}
        try { driver.findElement(city).clear(); } catch (Exception e) {}
        try { driver.findElement(state).clear(); } catch (Exception e) {}
        try { driver.findElement(zipCode).clear(); } catch (Exception e) {}
        try { driver.findElement(country).clear(); } catch (Exception e) {}
        System.out.println("🧹 All fields cleared");
    }

    // ===================== PLACE ORDER =====================

    public void clickPlaceOrder() {
        System.out.println("🛒 Clicking Place Order...");

        By[] placeOrderLocators = {placeOrderBtn, placeOrderBtn2};

        for (By locator : placeOrderLocators) {
            try {
                WebElement placeOrder = wait.until(ExpectedConditions.elementToBeClickable(locator));
                scrollToElement(placeOrder);
                Thread.sleep(500);
                placeOrder.click();
                System.out.println("✅ Place Order clicked");
                Thread.sleep(1000);
                return;
            } catch (Exception e) {
                System.out.println("⚠️ Could not click: " + locator);
            }
        }

        // Try JavaScript click as fallback
        try {
            String js = "var btns = document.querySelectorAll('button'); " +
                    "for(var i=0; i<btns.length; i++) { " +
                    "  if(btns[i].textContent.includes('Place Order')) { " +
                    "    btns[i].click(); " +
                    "    return 'clicked'; " +
                    "  } " +
                    "} " +
                    "return 'not_found';";
            String result = (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(js);
            System.out.println("JavaScript click result: " + result);
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("❌ All click methods failed");
        }
    }

    // ===================== VALIDATIONS =====================

    public boolean hasErrorMessages() {
        try {
            boolean hasErrors = !driver.findElements(fieldErrors).isEmpty();
            System.out.println("Has error messages: " + hasErrors);
            return hasErrors;
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> getErrorMessages() {
        try {
            List<WebElement> errors = driver.findElements(fieldErrors);
            return errors.stream()
                    .map(WebElement::getText)
                    .filter(text -> !text.isEmpty())
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }

    public boolean areFieldsRequired() {
        try {
            List<WebElement> required = driver.findElements(requiredIndicator);
            System.out.println("Required fields count: " + required.size());
            return required.size() >= 3;
        } catch (Exception e) {
            return false;
        }
    }

    // ===================== RAZORPAY VALIDATION =====================

    public boolean isRazorpayDisplayed() {
        try {
            Thread.sleep(1000);
            boolean framePresent = !driver.findElements(razorpayFrame).isEmpty();

            if (framePresent) {
                System.out.println("✅ Razorpay iframe detected");
                return true;
            }

            // Check for Razorpay elements
            List<WebElement> razorpayElements = driver.findElements(
                    By.xpath("//*[contains(text(),'Razorpay') or contains(@class,'razorpay')]")
            );
            if (!razorpayElements.isEmpty()) {
                System.out.println("✅ Razorpay element detected");
                return true;
            }

            return false;
        } catch (Exception e) {
            System.out.println("❌ Razorpay not detected: " + e.getMessage());
            return false;
        }
    }

    public boolean isRazorpayPayButtonDisplayed() {
        try {
            Thread.sleep(1000);
            return !driver.findElements(razorpayPayBtn).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public void closeRazorpayModal() {
        try {
            WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(razorpayCloseBtn));
            closeBtn.click();
            System.out.println("✅ Razorpay modal closed");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("⚠️ Could not close Razorpay modal");
        }
    }

    public boolean isOrderPlaced() {
        try {
            Thread.sleep(1000);
            return !driver.findElements(orderConfirmation).isEmpty() ||
                    driver.getPageSource().contains("Order Placed") ||
                    driver.getPageSource().contains("Success") ||
                    driver.getCurrentUrl().contains("success");
        } catch (Exception e) {
            return false;
        }
    }

    // ===================== HELPER METHODS =====================

    private void scrollToElement(WebElement element) {
        try {
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
            Thread.sleep(1000);
        } catch (Exception e) {}
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}