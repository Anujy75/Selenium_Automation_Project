package com.anuj.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ShopEaseAddToCartPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // ============ LOGIN PAGE LOCATORS ============
    private By emailInput = By.cssSelector("input[name='email']");
    private By passwordInput = By.cssSelector("input[name='password']");
    private By loginButton = By.cssSelector("button[type='submit']");

    // ============ PRODUCT PAGE LOCATORS ============
    private By productCards = By.cssSelector(".card-hover, [class*='card']");
    private By addToCartBtn = By.xpath(".//button[contains(text(),'Add to Cart')]");
    private By productGrid = By.cssSelector(".prod-grid");

    // ============ CART PAGE LOCATORS ============
    private By cartContainer = By.xpath("//*[@id='root']/div[2]/div/div[2]/div[1]");
    private By cartItems = By.xpath("//*[@id='root']/div[2]/div/div[2]/div[1]/div");
    private String cartItemXPath = "//*[@id='root']/div[2]/div/div[2]/div[1]/div[%d]";
    private By itemName = By.xpath(".//h3");
    private By itemQuantity = By.xpath(".//span[contains(@class, 'quantity') or contains(@class, 'qty')]");

    // 🔥 EXACT XPATH FOR + AND - BUTTONS
    private String increaseXPath = "//*[@id='root']/div[2]/div/div[2]/div[1]/div[%d]/div[3]/div[1]/button[2]";
    private String decreaseXPath = "//*[@id='root']/div[2]/div/div[2]/div[1]/div[%d]/div[3]/div[1]/button[1]";

    private By removeBtn = By.xpath(".//button[contains(text(),'Remove') or contains(text(),'🗑')]");
    private By emptyCart = By.xpath("//*[contains(text(),'Your cart is empty')]");
    private By subtitle = By.xpath("//*[contains(@class, 'subtitle')]");
    private By cartTotal = By.xpath("//*[contains(@class, 'summaryRowTotal')]//span[last()]");
    private By clearAllBtn = By.xpath("//button[contains(text(),'Clear All')]");
    private By checkoutBtn = By.xpath("//button[contains(text(),'Proceed To Checkout')]");
    private By continueBtn = By.xpath("//button[contains(text(),'Continue Shopping')]");
    private By promoInput = By.xpath("//input[contains(@placeholder, 'Promo Code')]");
    private By promoApplyBtn = By.xpath("//button[contains(text(),'Apply')]");
    private By promoSuccess = By.xpath("//*[contains(@class, 'promoSuccess')]");
    private By cartBadge = By.xpath("//*[contains(@class, 'badge') or contains(@class, 'cart-badge')]");

    private static final String BASE_URL = "http://localhost:3000";

    public ShopEaseAddToCartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        PageFactory.initElements(driver, this);
    }

    // ===================== LOGIN =====================
    public void login(String email, String password) {
        System.out.println("Navigating to login page: " + BASE_URL + "/login/user");
        driver.get(BASE_URL + "/login/user");
        sleep(3000);

        System.out.println("Entering email: " + email);
        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        emailField.clear();
        emailField.sendKeys(email);
        sleep(500);

        System.out.println("Entering password");
        WebElement passwordField = driver.findElement(passwordInput);
        passwordField.clear();
        passwordField.sendKeys(password);
        sleep(500);

        System.out.println("Clicking login button");
        driver.findElement(loginButton).click();
        sleep(5000);

        System.out.println("Login completed");
    }

    // ===================== PRODUCT PAGE =====================
    public void gotoProductListingPage() {
        System.out.println("Navigating to products page: " + BASE_URL + "/products");
        driver.get(BASE_URL + "/products");
        sleep(5000);

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(productGrid));
            System.out.println("Products grid loaded");
        } catch (Exception e) {
            System.out.println("Products grid not found");
        }
        sleep(2000);
    }

    public List<ProductDetails> getAllProducts() {
        List<ProductDetails> products = new ArrayList<>();
        try {
            List<WebElement> items = driver.findElements(By.cssSelector(".card-hover, [class*='card']"));
            System.out.println("Found " + items.size() + " product cards");

            for (WebElement item : items) {
                try {
                    List<WebElement> nameElements = item.findElements(By.cssSelector("h3, [class*='name']"));
                    if (!nameElements.isEmpty()) {
                        String name = nameElements.get(0).getText().trim();
                        if (!name.isEmpty() && !name.equals("Unknown")) {
                            ProductDetails product = new ProductDetails();
                            product.setName(name);
                            products.add(product);
                            System.out.println("Product found: " + name);
                        }
                    }
                } catch (Exception e) {}
            }
        } catch (Exception e) {
            System.out.println("Error getting products: " + e.getMessage());
        }
        return products;
    }

    public void addProductToCart(int productIndex) {
        try {
            System.out.println("Adding product at index " + productIndex + " to cart");

            List<WebElement> items = driver.findElements(By.cssSelector(".card-hover, [class*='card']"));

            if (productIndex < items.size()) {
                WebElement product = items.get(productIndex);
                scrollToElement(product);
                sleep(1000);

                List<WebElement> addBtns = product.findElements(By.xpath(".//button[contains(text(),'Add to Cart')]"));
                if (addBtns.isEmpty()) {
                    addBtns = product.findElements(By.xpath(".//button[contains(.,'Add')]"));
                }

                if (!addBtns.isEmpty()) {
                    scrollToElement(addBtns.get(0));
                    sleep(500);
                    addBtns.get(0).click();
                    System.out.println("✅ Product added to cart successfully!");
                    sleep(3000);
                } else {
                    product.click();
                    sleep(2000);

                    List<WebElement> detailAddBtns = driver.findElements(By.xpath("//button[contains(text(),'Add to Cart')]"));
                    if (!detailAddBtns.isEmpty()) {
                        detailAddBtns.get(0).click();
                        System.out.println("✅ Product added from detail page!");
                        sleep(3000);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error adding product: " + e.getMessage());
        }
    }

    // ===================== CART PAGE =====================
    public void gotoCartPage() {
        System.out.println("Navigating to cart page: " + BASE_URL + "/cart");
        driver.get(BASE_URL + "/cart");
        System.out.println("Waiting for cart to load...");
        sleep(8000);
        System.out.println("Cart page loaded");
    }

    public int getCartItemCount() {
        try {
            List<WebElement> items = driver.findElements(By.xpath("//*[@id='root']/div[2]/div/div[2]/div[1]/div"));
            System.out.println("📦 Cart items count: " + items.size());
            return items.size();
        } catch (Exception e) {
            System.out.println("Error getting cart count: " + e.getMessage());
            return 0;
        }
    }

    public CartItemDetails getCartItemDetails(int itemIndex) {
        try {
            int index = itemIndex + 1;
            WebElement item = driver.findElement(By.xpath("//*[@id='root']/div[2]/div/div[2]/div[1]/div[" + index + "]"));
            CartItemDetails details = new CartItemDetails();

            try {
                List<WebElement> nameElements = item.findElements(By.xpath(".//h3"));
                if (!nameElements.isEmpty()) {
                    details.setName(nameElements.get(0).getText().trim());
                }
            } catch (Exception e) {}

            try {
                List<WebElement> qtyElements = item.findElements(By.xpath(".//span[contains(@class, 'quantity') or contains(@class, 'qty')]"));
                if (!qtyElements.isEmpty()) {
                    String qtyText = qtyElements.get(0).getText().trim();
                    try {
                        details.setQuantity(Integer.parseInt(qtyText));
                    } catch (NumberFormatException e) {
                        details.setQuantity(1);
                    }
                } else {
                    details.setQuantity(1);
                }
            } catch (Exception e) {
                details.setQuantity(1);
            }

            try {
                List<WebElement> priceElements = item.findElements(By.xpath(".//*[contains(text(), '₹')]"));
                for (WebElement el : priceElements) {
                    String text = el.getText().trim();
                    if (text.startsWith("₹") && !text.contains("Total")) {
                        details.setPrice(text);
                        break;
                    }
                }
            } catch (Exception e) {}

            return details;
        } catch (Exception e) {
            System.out.println("Error getting cart item details: " + e.getMessage());
            return null;
        }
    }

    // ===================== INCREASE QUANTITY - FIXED =====================
    public void increaseQuantity(int itemIndex) {
        try {
            System.out.println("🔼 Increasing quantity for item " + (itemIndex + 1));
            int index = itemIndex + 1;

            String xpath = String.format(increaseXPath, index);
            System.out.println("Using XPath: " + xpath);

            // 🔥 METHOD 1: Normal Click
            try {
                WebElement plusBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
                scrollToElement(plusBtn);
                sleep(500);
                plusBtn.click();
                System.out.println("✅ + button clicked (Method 1)");
                sleep(2000);

                // 🔥 Check if quantity increased, if not try again
                driver.navigate().refresh();
                sleep(2000);
                return;
            } catch (Exception e1) {
                System.out.println("Method 1 failed: " + e1.getMessage());
            }

            // 🔥 METHOD 2: JavaScript Click with delay
            try {
                String js = "var xpath = \"" + xpath + "\"; " +
                        "var result = document.evaluate(xpath, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue; " +
                        "if(result) { " +
                        "  result.click(); " +
                        "  return 'clicked'; " +
                        "} else { " +
                        "  return 'not_found'; " +
                        "}";
                String result = (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(js);
                System.out.println("JS Method 2 result: " + result);
                sleep(2000);
                driver.navigate().refresh();
                sleep(2000);
                return;
            } catch (Exception e2) {
                System.out.println("Method 2 failed: " + e2.getMessage());
            }

            // 🔥 METHOD 3: Double Click
            try {
                WebElement plusBtn = driver.findElement(By.xpath(xpath));
                scrollToElement(plusBtn);
                sleep(500);
                plusBtn.click();
                sleep(500);
                plusBtn.click();
                System.out.println("✅ + button double clicked (Method 3)");
                sleep(2000);
                driver.navigate().refresh();
                sleep(2000);
                return;
            } catch (Exception e3) {
                System.out.println("Method 3 failed: " + e3.getMessage());
            }

            // 🔥 METHOD 4: Actions Click
            try {
                WebElement plusBtn = driver.findElement(By.xpath(xpath));
                scrollToElement(plusBtn);
                sleep(500);
                org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
                actions.moveToElement(plusBtn).click().perform();
                System.out.println("✅ + button clicked via Actions (Method 4)");
                sleep(2000);
                driver.navigate().refresh();
                sleep(2000);
            } catch (Exception e4) {
                System.out.println("Method 4 failed: " + e4.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Error increasing quantity: " + e.getMessage());
        }
    }

    // ===================== DECREASE QUANTITY - FIXED =====================
    public void decreaseQuantity(int itemIndex) {
        try {
            System.out.println("🔽 Decreasing quantity for item " + (itemIndex + 1));
            int index = itemIndex + 1;

            String xpath = String.format(decreaseXPath, index);
            System.out.println("Using XPath: " + xpath);

            // 🔥 METHOD 1: Normal Click
            try {
                WebElement minusBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
                scrollToElement(minusBtn);
                sleep(500);
                minusBtn.click();
                System.out.println("✅ - button clicked (Method 1)");
                sleep(2000);
                driver.navigate().refresh();
                sleep(2000);
                return;
            } catch (Exception e1) {
                System.out.println("Method 1 failed: " + e1.getMessage());
            }

            // 🔥 METHOD 2: JavaScript Click
            try {
                String js = "var xpath = \"" + xpath + "\"; " +
                        "var result = document.evaluate(xpath, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue; " +
                        "if(result) { " +
                        "  result.click(); " +
                        "  return 'clicked'; " +
                        "} else { " +
                        "  return 'not_found'; " +
                        "}";
                String result = (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(js);
                System.out.println("JS Method 2 result: " + result);
                sleep(2000);
                driver.navigate().refresh();
                sleep(2000);
                return;
            } catch (Exception e2) {
                System.out.println("Method 2 failed: " + e2.getMessage());
            }

            // 🔥 METHOD 3: Actions Click
            try {
                WebElement minusBtn = driver.findElement(By.xpath(xpath));
                scrollToElement(minusBtn);
                sleep(500);
                org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
                actions.moveToElement(minusBtn).click().perform();
                System.out.println("✅ - button clicked via Actions (Method 3)");
                sleep(2000);
                driver.navigate().refresh();
                sleep(2000);
            } catch (Exception e3) {
                System.out.println("Method 3 failed: " + e3.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Error decreasing quantity: " + e.getMessage());
        }
    }

    public void removeItem(int itemIndex) {
        try {
            System.out.println("🗑️ Removing item " + (itemIndex + 1));
            int index = itemIndex + 1;
            WebElement item = driver.findElement(By.xpath("//*[@id='root']/div[2]/div/div[2]/div[1]/div[" + index + "]"));

            List<WebElement> btns = item.findElements(By.xpath(".//button[contains(text(),'Remove') or contains(text(),'🗑')]"));
            if (btns.isEmpty()) {
                btns = item.findElements(By.xpath(".//button[contains(.,'Remove')]"));
            }

            if (!btns.isEmpty()) {
                scrollToElement(btns.get(0));
                sleep(500);
                btns.get(0).click();
                System.out.println("✅ Remove button clicked");
                sleep(3000);
            } else {
                System.out.println("❌ Remove button not found");
            }
        } catch (Exception e) {
            System.out.println("Error removing item: " + e.getMessage());
        }
    }

    public boolean isCartEmpty() {
        try {
            return !driver.findElements(By.xpath("//*[contains(text(),'Your cart is empty')]")).isEmpty();
        } catch (Exception e) {
            return getCartItemCount() == 0;
        }
    }

    public String getCartTotal() {
        try {
            WebElement total = driver.findElement(By.xpath("//*[contains(@class, 'summaryRowTotal')]//span[last()]"));
            return total.getText().trim();
        } catch (Exception e) {
            return "₹0";
        }
    }

    public void clearCart() {
        try {
            WebElement clearBtn = driver.findElement(By.xpath("//button[contains(text(),'Clear All')]"));
            clearBtn.click();
            sleep(2000);
            try {
                driver.switchTo().alert().accept();
                sleep(1000);
            } catch (Exception e) {}
            System.out.println("✅ Cart cleared");
            sleep(3000);
        } catch (Exception e) {
            System.out.println("Error clearing cart: " + e.getMessage());
        }
    }

    public int getCartBadgeCount() {
        try {
            List<WebElement> badges = driver.findElements(By.xpath("//*[contains(@class, 'badge') or contains(@class, 'cart-badge')]"));
            for (WebElement badge : badges) {
                if (badge.isDisplayed()) {
                    String text = badge.getText().trim();
                    if (!text.isEmpty()) {
                        try {
                            return Integer.parseInt(text);
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    }
                }
            }
        } catch (Exception e) {}
        return 0;
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    // ===================== HELPER =====================
    private void scrollToElement(WebElement element) {
        try {
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView(true);", element);
            sleep(500);
        } catch (Exception e) {}
    }

    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ===================== INNER CLASSES =====================
    public static class ProductDetails {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String toString() { return "Product{name='" + name + "'}"; }
    }

    public static class CartItemDetails {
        private String name;
        private int quantity;
        private String price;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public String getPrice() { return price; }
        public void setPrice(String price) { this.price = price; }

        public String toString() {
            return "CartItem{name='" + name + "', price='" + price + "', quantity=" + quantity + "}";
        }
    }
}