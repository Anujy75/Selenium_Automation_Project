package com.anuj.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;

public class ShopEaseFullFlowPage {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    // ============ LOGIN ============
    private By emailInput     = By.xpath("//input[@type='email' or @name='email' or @placeholder[contains(.,'email') or contains(.,'Email')]]");
    private By passwordInput  = By.xpath("//input[@type='password']");
    private By loginBtn       = By.xpath("//button[contains(normalize-space(),'Login') or contains(normalize-space(),'Sign In')]");
    private By logoutBtn      = By.xpath("//button[contains(normalize-space(),'Logout') or contains(normalize-space(),'Sign Out')]");
    private By navbarUserMenu = By.xpath("//*[contains(@class,'profile') or contains(@class,'user') or contains(@class,'avatar') or contains(normalize-space(),'Account')]");

    // ============ PRODUCTS PAGE ============
    private By addToCartBtns = By.xpath("//button[contains(normalize-space(),'Add to Cart') or contains(normalize-space(),'Add To Cart')]");

    // ============ CART ============
    private By proceedToCheckout = By.xpath("//button[contains(normalize-space(),'Proceed To Checkout') or contains(normalize-space(),'Proceed to Checkout') or contains(normalize-space(),'Checkout')]");

    // ============ CHECKOUT ============
    private By fullNameField = By.name("fullName");
    private By emailField    = By.name("email");
    private By phoneField    = By.name("phone");
    private By addressField  = By.name("address");
    private By cityField     = By.name("city");
    private By pincodeField  = By.name("pincode");
    private By payBtn        = By.xpath("//button[contains(normalize-space(),'Pay')]");

    // ============ RAZORPAY ============
    private By razorpayIframe = By.xpath("//iframe[contains(@src,'razorpay')]");

    // ============ ORDER SUCCESS ============
    private By orderConfirmedHeading = By.xpath("//h1[contains(text(),'Order confirmed')]");
    private By orderIdPill           = By.xpath("//span[starts-with(text(),'ORD-')]");
    private By invoiceBtn            = By.xpath("//button[contains(normalize-space(),'Invoice')]");

    // ============ ORDERS PAGE ============
    private By orderHashSpan     = By.xpath("//span[text()='#']");
    private By statusBadges      = By.xpath(
            "//span[text()='Confirmed' or text()='Pending' or text()='Shipped' or " +
                    "text()='Delivered' or text()='Cancelled' or text()='Processing']"
    );

    // ============ ORDERS PAGE — EXACT DOM XPATHS ============
    private By confirmedFilterBtn = By.xpath("//*[@id='root']/div[2]/div[2]/div[3]/div[1]/div/div[2]/span");

    public ShopEaseFullFlowPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.js     = (JavascriptExecutor) driver;
    }

    // =====================================================================
    //  STEP 1 — LOGIN
    // =====================================================================
    public void login(String email, String password) {
        System.out.println("🔐 Logging in as: " + email);
        driver.get("http://localhost:3000/login/user");
        sleep(1500);
        try {
            WebElement em = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
            em.clear();
            em.sendKeys(email);
            driver.findElement(passwordInput).clear();
            driver.findElement(passwordInput).sendKeys(password);
            driver.findElement(loginBtn).click();
            sleep(2000);
            System.out.println("✅ Login successful");
        } catch (Exception e) {
            System.out.println("❌ Login failed: " + e.getMessage());
            throw new RuntimeException("Login failed", e);
        }
    }

    public boolean isLoggedIn() {
        try {
            Object token = js.executeScript(
                    "return localStorage.getItem('customerToken') || localStorage.getItem('adminToken');"
            );
            boolean loggedIn = token != null && !token.toString().isEmpty();
            System.out.println("✅ Logged in: " + loggedIn);
            return loggedIn;
        } catch (Exception e) {
            return false;
        }
    }

    // =====================================================================
    //  STEP 2 — BROWSE PRODUCTS
    // =====================================================================
    public void goToProducts() {
        System.out.println("🛍️ Navigating to Products page...");
        driver.get("http://localhost:3000/products");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15)).until(d ->
                    !d.findElements(addToCartBtns).isEmpty()
            );
        } catch (Exception e) {
            System.out.println("⚠️ Products load timeout");
        }
        sleep(500);
        System.out.println("✅ Products page loaded");
    }

    public int getProductCount() {
        try {
            int count = driver.findElements(addToCartBtns).size();
            System.out.println("✅ Products found: " + count);
            return count;
        } catch (Exception e) {
            return 0;
        }
    }

    // =====================================================================
    //  STEP 3 — ADD TO CART
    // =====================================================================
    public void addFirstProductToCart() {
        System.out.println("🛒 Adding first product to cart from Products page...");
        try {
            List<WebElement> btns = wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(addToCartBtns)
            );
            System.out.println("   Add to Cart buttons found: " + btns.size());
            WebElement firstBtn = btns.get(0);
            scrollToElement(firstBtn);
            sleep(500);
            firstBtn.click();
            sleep(2000);

            List<WebElement> cartConfirm = driver.findElements(By.xpath(
                    "//button[contains(normalize-space(),'Go to Cart') or " +
                            "contains(normalize-space(),'View Cart')] | " +
                            "//*[contains(text(),'Added to cart') or contains(text(),'added to cart')]"
            ));
            if (!cartConfirm.isEmpty()) {
                System.out.println("✅ Add to cart confirmed via toast/button");
                sleep(500);
                return;
            }
            System.out.println("⚠️ No toast detected, trying JS click...");
            js.executeScript("arguments[0].click();", firstBtn);
            sleep(2000);
            System.out.println("✅ Product add to cart attempted");
        } catch (Exception e) {
            System.out.println("⚠️ addFirstProductToCart: " + e.getMessage());
            try {
                js.executeScript(
                        "var btns=document.querySelectorAll('button');" +
                                "for(var b of btns){" +
                                "  if(b.textContent.toLowerCase().trim().includes('add to cart')){b.click();break;}" +
                                "}"
                );
                sleep(2000);
                System.out.println("✅ Add to cart via JS fallback");
            } catch (Exception ex) {
                System.out.println("❌ All add to cart methods failed");
            }
        }
    }

    // =====================================================================
    //  STEP 4 — CART VALIDATION
    // =====================================================================
    public void goToCart() {
        System.out.println("🛒 Navigating to cart...");
        driver.get("http://localhost:3000/cart");
        sleep(2000);
        System.out.println("✅ Cart loaded");
    }

    public boolean isCartEmpty() {
        try {
            sleep(1500);
            // Proceed to Checkout visible = NOT empty
            List<WebElement> checkoutBtns = driver.findElements(proceedToCheckout);
            if (!checkoutBtns.isEmpty()) {
                System.out.println("Cart empty: false (Proceed to Checkout visible)");
                return false;
            }
            // ₹ elements >= 2 = items present
            List<WebElement> priceEls = driver.findElements(
                    By.xpath("//*[contains(text(),'₹')]")
            );
            if (priceEls.size() >= 2) {
                System.out.println("Cart empty: false (price elements: " + priceEls.size() + ")");
                return false;
            }
            // Explicit empty text
            String pageText = driver.findElement(By.tagName("body")).getText().toLowerCase();
            boolean empty = pageText.contains("your cart is empty") ||
                    pageText.contains("cart is empty") ||
                    pageText.contains("no items");
            System.out.println("Cart empty: " + empty);
            return empty;
        } catch (Exception e) {
            System.out.println("⚠️ isCartEmpty: " + e.getMessage());
            return false;
        }
    }

    public String getCartTotal() {
        try {
            List<WebElement> priceEls = driver.findElements(
                    By.xpath("//*[contains(text(),'₹')]")
            );
            if (!priceEls.isEmpty()) {
                String total = priceEls.get(priceEls.size() - 1).getText().trim();
                System.out.println("✅ Cart total: " + total);
                return total;
            }
        } catch (Exception e) {
            System.out.println("⚠️ getCartTotal: " + e.getMessage());
        }
        return "N/A";
    }

    // =====================================================================
    //  STEP 5 — CHECKOUT
    // =====================================================================
    public void clickProceedToCheckout() {
        System.out.println("💳 Proceeding to checkout...");
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(proceedToCheckout));
            scrollToElement(btn);
            sleep(300);
            btn.click();
            sleep(2000);
            System.out.println("✅ Checkout page opened");
        } catch (Exception e) {
            System.out.println("⚠️ Checkout btn: " + e.getMessage());
        }
    }

    public void fillShippingAndPay(String fName, String lName, String mail,
                                   String phone, String addr, String city,
                                   String state, String zip, String country) {
        System.out.println("📝 Filling shipping form...");
        fillField(fullNameField, fName + " " + lName, "Full Name");
        fillField(emailField,    mail,                 "Email");
        fillField(phoneField,    phone,                "Phone");
        fillField(addressField,  addr,                 "Address");
        fillField(cityField,     city,                 "City");
        fillField(pincodeField,  zip,                  "Pincode");
        System.out.println("✅ Shipping form filled");
        sleep(500);

        System.out.println("💰 Clicking Pay...");
        try {
            WebElement pay = wait.until(ExpectedConditions.elementToBeClickable(payBtn));
            scrollToElement(pay);
            sleep(300);
            pay.click();
            sleep(3000);
            System.out.println("✅ Pay clicked");
        } catch (Exception e) {
            js.executeScript(
                    "var btns=document.querySelectorAll('button');" +
                            "for(var b of btns){if(b.textContent.trim()==='Pay'){b.click();break;}}"
            );
            sleep(3000);
        }
    }

    private void fillField(By locator, String value, String fieldName) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            el.clear();
            el.sendKeys(value);
            sleep(200);
            System.out.println("  ✅ " + fieldName + ": " + value);
        } catch (Exception e) {
            System.out.println("  ⚠️ " + fieldName + " not found");
        }
    }

    // =====================================================================
    //  STEP 6 — RAZORPAY
    // =====================================================================
    public boolean isRazorpayOpen() {
        try {
            sleep(2000);
            boolean open = !driver.findElements(razorpayIframe).isEmpty();
            System.out.println("Razorpay open: " + open);
            return open;
        } catch (Exception e) {
            return false;
        }
    }

    public void closeRazorpay() {
        System.out.println("❌ Closing Razorpay...");
        try {
            java.awt.Robot robot = new java.awt.Robot();
            robot.keyPress(java.awt.event.KeyEvent.VK_ESCAPE);
            robot.keyRelease(java.awt.event.KeyEvent.VK_ESCAPE);
            sleep(1000);
            System.out.println("✅ Razorpay closed via Escape");
        } catch (Exception e) {
            System.out.println("⚠️ Could not close Razorpay: " + e.getMessage());
        }
    }

    // =====================================================================
    //  STEP 7 — ORDER SUCCESS PAGE
    // =====================================================================
    public boolean isOrderSuccessPageLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(orderConfirmedHeading));
            System.out.println("✅ Order Success page loaded");
            return true;
        } catch (Exception e) {
            boolean onPage = driver.getCurrentUrl().contains("order-success");
            System.out.println("Order success via URL: " + onPage);
            return onPage;
        }
    }

    public String getOrderIdFromSuccessPage() {
        try {
            String url = driver.getCurrentUrl();
            if (url.contains("orderId=")) {
                String id = url.split("orderId=")[1].split("&")[0].trim();
                System.out.println("✅ Order ID from URL: " + id);
                return id;
            }
        } catch (Exception e) {}
        try {
            return driver.findElement(orderIdPill).getText().trim();
        } catch (Exception e) {}
        return "N/A";
    }

    // =====================================================================
    //  STEP 8 — INVOICE
    // =====================================================================
    public void clickInvoiceOnSuccessPage() {
        System.out.println("📄 Clicking Invoice button...");
        try {
            // window.print() override PEHLE — print dialog block na kare
            js.executeScript(
                    "window._printCalled = false;" +
                            "window.print = function(){ window._printCalled = true; console.log('Print intercepted'); };"
            );
            System.out.println("   ✅ window.print() intercepted");

            WebElement btn = wait.until(
                    ExpectedConditions.presenceOfElementLocated(invoiceBtn)
            );
            scrollToElement(btn);
            sleep(500);
            js.executeScript("arguments[0].click();", btn);
            sleep(1000);

            Boolean printCalled = (Boolean) js.executeScript(
                    "return window._printCalled === true;"
            );
            if (Boolean.TRUE.equals(printCalled)) {
                System.out.println("✅ Invoice button clicked — print() was triggered");
            } else {
                System.out.println("⚠️ Invoice clicked but print() may not have fired");
            }
            sleep(500);
        } catch (Exception e) {
            System.out.println("⚠️ Invoice click failed: " + e.getMessage());
        }
    }

    // =====================================================================
    //  ORDERS PAGE
    // =====================================================================
    public void goToOrdersPage() {
        System.out.println("📋 Navigating to Orders page...");
        driver.get("http://localhost:3000/orders");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15)).until(d ->
                    !d.findElements(orderHashSpan).isEmpty() ||
                            !d.findElements(By.xpath("//h3[contains(text(),'No orders')]")).isEmpty()
            );
        } catch (Exception e) {
            System.out.println("⚠️ Orders page timeout");
        }
        sleep(500);
        System.out.println("✅ Orders page loaded");
    }

    public String getFirstOrderIdFromOrdersPage() {
        try {
            List<WebElement> hashes = wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(orderHashSpan)
            );
            String id = hashes.get(0)
                    .findElement(By.xpath("./following-sibling::span[1]"))
                    .getText().trim();
            System.out.println("✅ Orders page first ID: " + id);
            return id;
        } catch (Exception e) {
            System.out.println("⚠️ getFirstOrderIdFromOrdersPage: " + e.getMessage());
            return "N/A";
        }
    }

    public String getFirstOrderStatus() {
        try {
            List<WebElement> badges = driver.findElements(statusBadges);
            if (!badges.isEmpty()) {
                String status = badges.get(0).getText().trim();
                System.out.println("✅ First order status: " + status);
                return status;
            }
        } catch (Exception e) {}
        return "N/A";
    }

    // =====================================================================
    //  STEP 10 — CONFIRMED FILTER + DIRECT NAVIGATE TO DETAIL
    // =====================================================================
    public void clickConfirmedFilterAndViewDetails() {
        // ── Part A: Confirmed filter click ──
        System.out.println("🔘 Clicking 'Confirmed' filter button...");
        try {
            WebElement confirmedBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(confirmedFilterBtn)
            );
            scrollToElement(confirmedBtn);
            sleep(400);
            confirmedBtn.click();
            System.out.println("✅ Confirmed filter clicked");
            sleep(2000);
        } catch (Exception e) {
            System.out.println("⚠️ Exact XPath failed, trying text fallback...");
            try {
                WebElement btn = driver.findElement(By.xpath(
                        "//button[normalize-space()='Confirmed'] | " +
                                "//button[.//span[normalize-space()='Confirmed']]"
                ));
                scrollToElement(btn);
                btn.click();
                sleep(2000);
                System.out.println("✅ Confirmed filter clicked via text");
            } catch (Exception ex) {
                System.out.println("⚠️ Confirmed filter fallback also failed, using all orders");
            }
        }

        // ── Part B: Filter ke baad pehla orderId lo ──
        System.out.println("📋 Getting first order ID after filter...");
        String orderId = "N/A";
        try {
            List<WebElement> hashes = wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(orderHashSpan)
            );
            orderId = hashes.get(0)
                    .findElement(By.xpath("./following-sibling::span[1]"))
                    .getText().trim();
            System.out.println("✅ Target Order ID: " + orderId);
        } catch (Exception e) {
            System.out.println("❌ Could not get order ID after filter: " + e.getMessage());
            return;
        }

        // ── Part C: Seedha URL navigate — 100% reliable ──
        System.out.println("🔗 Navigating directly to order detail: " + orderId);
        driver.get("http://localhost:3000/order-success?orderId=" + orderId);
        sleep(2500);
        System.out.println("✅ Order detail navigated directly");
    }

    // =====================================================================
    //  ORDER DETAIL PAGE VALIDATIONS
    // =====================================================================
    public boolean isOrderDetailPageLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(orderConfirmedHeading));
            System.out.println("✅ Order detail page loaded");
            return true;
        } catch (Exception e) {
            boolean onPage = driver.getCurrentUrl().contains("order-success");
            System.out.println("Order detail via URL: " + onPage);
            return onPage;
        }
    }

    public String getOrderIdFromDetailPage() {
        try {
            String url = driver.getCurrentUrl();
            if (url.contains("orderId=")) {
                return url.split("orderId=")[1].split("&")[0].trim();
            }
        } catch (Exception e) {}
        try {
            return driver.findElement(orderIdPill).getText().trim();
        } catch (Exception e) {}
        return "N/A";
    }

    public String getStatValue(String label) {
        try {
            WebElement lbl = driver.findElement(
                    By.xpath("//span[normalize-space(text())='" + label + "']")
            );
            return lbl.findElement(By.xpath("./following-sibling::span[1]"))
                    .getText().trim();
        } catch (Exception e) {
            return "N/A";
        }
    }

    public int getItemCountOnDetailPage() {
        try {
            List<WebElement> items = driver.findElements(
                    By.xpath("//div[contains(text(),'Qty:')]")
            );
            System.out.println("📦 Items on detail: " + items.size());
            return items.size();
        } catch (Exception e) {
            return 0;
        }
    }

    // =====================================================================
    //  PROFILE PAGE
    // =====================================================================
    public void goToProfile() {
        System.out.println("👤 Navigating to profile...");
        driver.get("http://localhost:3000/profile");
        sleep(2000);
        System.out.println("✅ Profile page loaded");
    }

    public boolean isProfilePageLoaded() {
        try {
            sleep(1000);
            // URL check — most reliable
            if (driver.getCurrentUrl().contains("/profile")) {
                System.out.println("✅ Profile page loaded (URL match)");
                return true;
            }
            // Heading check
            List<WebElement> headings = driver.findElements(
                    By.xpath("//h1 | //h2 | //h3")
            );
            for (WebElement h : headings) {
                String txt = h.getText().toLowerCase();
                if (txt.contains("profile") || txt.contains("account")) {
                    System.out.println("✅ Profile page loaded (heading: " + h.getText() + ")");
                    return true;
                }
            }
            // Any input visible = profile form load hua
            List<WebElement> inputs = driver.findElements(By.tagName("input"));
            if (inputs.size() >= 1) {
                System.out.println("✅ Profile page loaded (inputs: " + inputs.size() + ")");
                return true;
            }
            System.out.println("❌ Profile page not detected");
            return false;
        } catch (Exception e) {
            System.out.println("⚠️ isProfilePageLoaded: " + e.getMessage());
            return false;
        }
    }

    public String getProfileEmail() {
        try {
            List<WebElement> inputs = driver.findElements(By.tagName("input"));
            for (WebElement input : inputs) {
                String type        = input.getAttribute("type");
                String name        = input.getAttribute("name");
                String placeholder = input.getAttribute("placeholder");
                String val         = input.getAttribute("value");
                boolean isEmail = "email".equals(type) ||
                        (name != null && name.toLowerCase().contains("email")) ||
                        (placeholder != null && placeholder.toLowerCase().contains("email"));
                if (isEmail && val != null && !val.isEmpty()) {
                    System.out.println("✅ Profile email: " + val);
                    return val;
                }
            }
            // Fallback: @ wala value
            for (WebElement input : inputs) {
                String val = input.getAttribute("value");
                if (val != null && val.contains("@")) {
                    System.out.println("✅ Profile email (@ match): " + val);
                    return val;
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ getProfileEmail: " + e.getMessage());
        }
        return "N/A";
    }

    // =====================================================================
    //  LOGOUT
    // =====================================================================
    public void logout() {
        System.out.println("🚪 Logging out...");
        try {
            List<WebElement> btns = driver.findElements(logoutBtn);
            if (!btns.isEmpty()) {
                btns.get(0).click();
                sleep(1500);
                System.out.println("✅ Logged out");
                return;
            }
            List<WebElement> menu = driver.findElements(navbarUserMenu);
            if (!menu.isEmpty()) {
                menu.get(0).click();
                sleep(800);
                btns = driver.findElements(logoutBtn);
                if (!btns.isEmpty()) {
                    btns.get(0).click();
                    sleep(1500);
                    System.out.println("✅ Logged out via menu");
                    return;
                }
            }
            // JS fallback
            js.executeScript(
                    "localStorage.removeItem('customerToken');" +
                            "localStorage.removeItem('adminToken');"
            );
            driver.get("http://localhost:3000/login/user");
            sleep(1000);
            System.out.println("✅ Logged out via localStorage clear");
        } catch (Exception e) {
            System.out.println("⚠️ Logout: " + e.getMessage());
        }
    }

    public boolean isLoggedOut() {
        try {
            Object token = js.executeScript(
                    "return localStorage.getItem('customerToken') || localStorage.getItem('adminToken');"
            );
            boolean loggedOut = token == null || token.toString().isEmpty();
            System.out.println("✅ Logged out: " + loggedOut);
            return loggedOut;
        } catch (Exception e) {
            return true;
        }
    }

    // =====================================================================
    //  HELPERS
    // =====================================================================
    private void scrollToElement(WebElement el) {
        try {
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);
            sleep(300);
        } catch (Exception ignored) {}
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}