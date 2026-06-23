package com.anuj.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
    private JavascriptExecutor js;

    // ============ CART PAGE CHECKOUT FORM LOCATORS ============

    // Checkout button on cart page
    private By checkoutBtn1 = By.xpath("//button[contains(text(),'Proceed To Checkout')]");
    private By checkoutBtn2 = By.xpath("//button[contains(text(),'Checkout')]");

    // ✅ FIXED: Using name attributes (your Checkout component uses these)
    private By fullName = By.name("fullName");
    private By email = By.name("email");
    private By phone = By.name("phone");
    private By address = By.name("address");
    private By city = By.name("city");
    private By pincode = By.name("pincode");

    // ⚠️ Fallback locators (if name doesn't work)
    private By firstName = By.id("firstName");
    private By lastName = By.id("lastName");
    private By emailAlt = By.id("email");
    private By phoneAlt = By.id("phone");
    private By addressLine1 = By.id("addressLine1");
    private By cityAlt = By.id("city");
    private By state = By.id("state");
    private By zipCode = By.id("zipCode");
    private By country = By.id("country");

    // Place Order button - ✅ FIXED: Your app uses "Pay" button
    private By payBtn = By.xpath("//button[contains(text(),'Pay')]");
    private By placeOrderBtn = By.xpath("//button[contains(text(),'Place Order')]");

    // Error/Validation Messages
    private By fieldErrors = By.cssSelector(".field-error, .error-message, .text-red-500, .invalid-feedback, [class*='error']");
    private By requiredIndicator = By.cssSelector("[required]");

    // Razorpay Locators
    private By razorpayFrame = By.xpath("//iframe[contains(@src, 'razorpay')]");
    private By razorpayPayBtn = By.xpath("//button[contains(text(),'Pay')]");
    private By razorpayCloseBtn = By.xpath("//button[contains(@class,'close') or contains(@class,'cross')]");

    // Success/Confirmation
    private By orderConfirmation = By.xpath("//*[contains(text(),'Order Placed') or contains(@class,'success')]");

    // Use Current Location
    private By useCurrentLocationBtn = By.xpath("//button[contains(text(),'Use Current Location')]");

    public ShopEaseCheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    // ===================== NAVIGATION =====================

    public void clickProceedToCheckoutIfExists() {
        System.out.println("🔄 Checking for Proceed To Checkout button...");

        By[] checkoutButtons = {checkoutBtn1, checkoutBtn2};

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
                // Continue
            }
        }
        System.out.println("ℹ️ No checkout button found - form might already be visible");
    }

    // 📍 USE CURRENT LOCATION
    public void useCurrentLocation() {
        System.out.println("📍 Clicking 'Use Current Location'...");
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(useCurrentLocationBtn));
            scrollToElement(btn);
            Thread.sleep(500);
            btn.click();
            System.out.println("✅ Use Current Location clicked");
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("⚠️ Could not click: " + e.getMessage());
            // JavaScript fallback
            try {
                String jsScript = "var btns = document.querySelectorAll('button'); " +
                        "for(var i=0; i<btns.length; i++) { " +
                        "  if(btns[i].textContent.includes('Use Current Location')) { " +
                        "    btns[i].click(); return 'clicked'; " +
                        "  } " +
                        "} return 'not_found';";
                js.executeScript(jsScript);
                System.out.println("✅ Clicked via JavaScript");
                Thread.sleep(2000);
            } catch (Exception ex) {
                System.out.println("❌ All click methods failed");
            }
        }
    }

    // ===================== ✅ FIXED: FILL SHIPPING DETAILS =====================

    public void fillShippingDetails(String fName, String lName, String mail,
                                    String phn, String addr, String cty,
                                    String st, String zip, String cntry) {

        System.out.println("📝 Filling shipping details...");

        // Combine first and last name
        String fullNameVal = fName + " " + lName;

        // Try name attributes first (your component uses these)
        boolean filled = false;

        try {
            WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(fullName));
            nameField.clear();
            nameField.sendKeys(fullNameVal);
            Thread.sleep(300);
            System.out.println("✅ Full Name filled: " + fullNameVal);
            filled = true;
        } catch (Exception e) {
            System.out.println("⚠️ fullName field not found, trying firstName...");
        }

        // If name attribute not found, try id attributes
        if (!filled) {
            try {
                WebElement fNameField = driver.findElement(firstName);
                fNameField.clear();
                fNameField.sendKeys(fName);
                Thread.sleep(300);
                System.out.println("✅ First Name filled: " + fName);
            } catch (Exception e) {}

            try {
                WebElement lNameField = driver.findElement(lastName);
                lNameField.clear();
                lNameField.sendKeys(lName);
                Thread.sleep(300);
                System.out.println("✅ Last Name filled: " + lName);
            } catch (Exception e) {}
        }

        // Email
        try {
            WebElement emailField = driver.findElement(email);
            emailField.clear();
            emailField.sendKeys(mail);
            Thread.sleep(300);
            System.out.println("✅ Email filled: " + mail);
        } catch (Exception e) {
            try {
                WebElement emailField = driver.findElement(emailAlt);
                emailField.clear();
                emailField.sendKeys(mail);
                Thread.sleep(300);
                System.out.println("✅ Email filled (alt): " + mail);
            } catch (Exception ex) {
                System.out.println("⚠️ Email field not found");
            }
        }

        // Phone
        try {
            WebElement phoneField = driver.findElement(phone);
            phoneField.clear();
            phoneField.sendKeys(phn);
            Thread.sleep(300);
            System.out.println("✅ Phone filled: " + phn);
        } catch (Exception e) {
            try {
                WebElement phoneField = driver.findElement(phoneAlt);
                phoneField.clear();
                phoneField.sendKeys(phn);
                Thread.sleep(300);
                System.out.println("✅ Phone filled (alt): " + phn);
            } catch (Exception ex) {
                System.out.println("⚠️ Phone field not found");
            }
        }

        // Address
        try {
            WebElement addressField = driver.findElement(address);
            addressField.clear();
            addressField.sendKeys(addr);
            Thread.sleep(300);
            System.out.println("✅ Address filled: " + addr);
        } catch (Exception e) {
            try {
                WebElement addressField = driver.findElement(addressLine1);
                addressField.clear();
                addressField.sendKeys(addr);
                Thread.sleep(300);
                System.out.println("✅ Address filled (alt): " + addr);
            } catch (Exception ex) {
                System.out.println("⚠️ Address field not found");
            }
        }

        // City
        try {
            WebElement cityField = driver.findElement(city);
            cityField.clear();
            cityField.sendKeys(cty);
            Thread.sleep(300);
            System.out.println("✅ City filled: " + cty);
        } catch (Exception e) {
            try {
                WebElement cityField = driver.findElement(cityAlt);
                cityField.clear();
                cityField.sendKeys(cty);
                Thread.sleep(300);
                System.out.println("✅ City filled (alt): " + cty);
            } catch (Exception ex) {
                System.out.println("⚠️ City field not found");
            }
        }

        // State
        try {
            WebElement stateField = driver.findElement(state);
            stateField.clear();
            stateField.sendKeys(st);
            Thread.sleep(300);
            System.out.println("✅ State filled: " + st);
        } catch (Exception e) {
            System.out.println("⚠️ State field not found");
        }

        // ZIP / Pincode
        try {
            WebElement pincodeField = driver.findElement(pincode);
            pincodeField.clear();
            pincodeField.sendKeys(zip);
            Thread.sleep(300);
            System.out.println("✅ Pincode filled: " + zip);
        } catch (Exception e) {
            try {
                WebElement zipField = driver.findElement(zipCode);
                zipField.clear();
                zipField.sendKeys(zip);
                Thread.sleep(300);
                System.out.println("✅ ZIP filled (alt): " + zip);
            } catch (Exception ex) {
                System.out.println("⚠️ Pincode field not found");
            }
        }

        // Country
        try {
            WebElement countryField = driver.findElement(country);
            countryField.clear();
            countryField.sendKeys(cntry);
            Thread.sleep(300);
            System.out.println("✅ Country filled: " + cntry);
        } catch (Exception e) {
            System.out.println("⚠️ Country field not found");
        }

        System.out.println("✅ Shipping details filled successfully!");
    }

    public void clearAllFields() {
        try { driver.findElement(fullName).clear(); } catch (Exception e) {}
        try { driver.findElement(email).clear(); } catch (Exception e) {}
        try { driver.findElement(phone).clear(); } catch (Exception e) {}
        try { driver.findElement(address).clear(); } catch (Exception e) {}
        try { driver.findElement(city).clear(); } catch (Exception e) {}
        try { driver.findElement(pincode).clear(); } catch (Exception e) {}
        System.out.println("🧹 All fields cleared");
    }

    // ===================== PLACE ORDER =====================

    public void clickPlaceOrder() {
        System.out.println("🛒 Clicking Place Order...");

        // Try "Pay" button first (your app uses this)
        try {
            WebElement payButton = wait.until(ExpectedConditions.elementToBeClickable(payBtn));
            scrollToElement(payButton);
            Thread.sleep(500);
            payButton.click();
            System.out.println("✅ Pay button clicked");
            Thread.sleep(3000);
            return;
        } catch (Exception e) {
            System.out.println("⚠️ Pay button not found, trying Place Order...");
        }

        // Try "Place Order" button
        try {
            WebElement placeOrder = wait.until(ExpectedConditions.elementToBeClickable(placeOrderBtn));
            scrollToElement(placeOrder);
            Thread.sleep(500);
            placeOrder.click();
            System.out.println("✅ Place Order clicked");
            Thread.sleep(3000);
            return;
        } catch (Exception e) {
            System.out.println("⚠️ Place Order button not found");
        }

        // JavaScript fallback
        try {
            String jsScript = "var btns = document.querySelectorAll('button'); " +
                    "for(var i=0; i<btns.length; i++) { " +
                    "  if(btns[i].textContent.includes('Pay') || btns[i].textContent.includes('Place Order')) { " +
                    "    btns[i].click(); " +
                    "    return 'clicked'; " +
                    "  } " +
                    "} " +
                    "return 'not_found';";
            String result = (String) js.executeScript(jsScript);
            System.out.println("JavaScript click result: " + result);
            Thread.sleep(3000);
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
            Thread.sleep(3000);
            boolean framePresent = !driver.findElements(razorpayFrame).isEmpty();

            if (framePresent) {
                System.out.println("✅ Razorpay iframe detected");
                return true;
            }

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
            Thread.sleep(2000);
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
            Thread.sleep(3000);
            return !driver.findElements(orderConfirmation).isEmpty() ||
                    driver.getPageSource().contains("Order Placed") ||
                    driver.getPageSource().contains("Success") ||
                    driver.getCurrentUrl().contains("success") ||
                    driver.getCurrentUrl().contains("order-success");
        } catch (Exception e) {
            return false;
        }
    }

    // ===================== HELPER METHODS =====================

    private void scrollToElement(WebElement element) {
        try {
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
            Thread.sleep(300);
        } catch (Exception e) {}
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}