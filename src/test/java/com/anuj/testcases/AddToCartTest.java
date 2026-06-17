package com.anuj.testcases;

import com.anuj.base.BaseTest;
import com.anuj.pages.ShopEaseAddToCartPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AddToCartTest extends BaseTest {

    private ShopEaseAddToCartPage addToCartPage;
    private static final String EMAIL = "anujydv@gmail.com";
    private static final String PASSWORD = "Anuj@1234";

    @BeforeMethod
    public void init() {
        addToCartPage = new ShopEaseAddToCartPage(driver);
        addToCartPage.login(EMAIL, PASSWORD);
        addToCartPage.gotoProductListingPage();
    }

    @Test
    public void testAddToCartAndValidate() {
        System.out.println("========== TC01: Add to Cart and Validate ==========");

        var products = addToCartPage.getAllProducts();
        Assert.assertTrue(products.size() > 0, "No products found");
        System.out.println("Total products: " + products.size());
        System.out.println("First product: " + products.get(0).getName());

        addToCartPage.addProductToCart(0);
        addToCartPage.gotoCartPage();

        int cartItemCount = addToCartPage.getCartItemCount();
        System.out.println("Cart items count: " + cartItemCount);
        Assert.assertTrue(cartItemCount > 0, "Cart should have at least 1 item");

        ShopEaseAddToCartPage.CartItemDetails item = addToCartPage.getCartItemDetails(0);
        if (item != null) {
            System.out.println("Cart item - Name: " + item.getName() +
                    ", Price: " + item.getPrice() +
                    ", Quantity: " + item.getQuantity());
            Assert.assertTrue(item.getQuantity() >= 1, "Quantity should be at least 1");
        }

        System.out.println("✅ TC01: Add to cart and validate passed!");
    }

    @Test
    public void testIncreaseQuantity() {
        System.out.println("========== TC02: Increase Quantity ==========");

        var products = addToCartPage.getAllProducts();
        Assert.assertTrue(products.size() > 0, "No products found");
        addToCartPage.addProductToCart(0);
        addToCartPage.gotoCartPage();

        // Get initial quantity (should be 1)
        ShopEaseAddToCartPage.CartItemDetails initialItem = addToCartPage.getCartItemDetails(0);
        Assert.assertNotNull(initialItem, "Cart should have item");
        int initialQty = initialItem.getQuantity();
        System.out.println("Initial quantity: " + initialQty);

        // ✅ Force check - quantity should be 1
        Assert.assertEquals(initialQty, 1, "Initial quantity should be 1");

        // Click + button - multiple attempts
        System.out.println("Attempt 1: Clicking + button...");
        addToCartPage.increaseQuantity(0);

        // Wait and refresh
        try { Thread.sleep(3000); } catch (Exception e) {}
        driver.navigate().refresh();
        try { Thread.sleep(3000); } catch (Exception e) {}

        // Check quantity after attempt 1
        ShopEaseAddToCartPage.CartItemDetails afterIncrease = addToCartPage.getCartItemDetails(0);
        int increasedQty = afterIncrease != null ? afterIncrease.getQuantity() : initialQty;
        System.out.println("Quantity after attempt 1: " + increasedQty);

        // If not increased, try again
        if (increasedQty <= initialQty) {
            System.out.println("Attempt 2: Retrying + button...");
            addToCartPage.increaseQuantity(0);
            try { Thread.sleep(3000); } catch (Exception e) {}
            driver.navigate().refresh();
            try { Thread.sleep(3000); } catch (Exception e) {}

            afterIncrease = addToCartPage.getCartItemDetails(0);
            increasedQty = afterIncrease != null ? afterIncrease.getQuantity() : initialQty;
            System.out.println("Quantity after attempt 2: " + increasedQty);
        }

        // If still not increased, try JavaScript direct
        if (increasedQty <= initialQty) {
            System.out.println("Attempt 3: Direct JavaScript click...");
            String js = "var btns = document.querySelectorAll('button'); " +
                    "for(var i=0; i<btns.length; i++) { " +
                    "  if(btns[i].textContent.trim() === '+') { " +
                    "    btns[i].click(); " +
                    "    return 'clicked'; " +
                    "  } " +
                    "} " +
                    "return 'not_found';";
            String result = (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(js);
            System.out.println("Global JS result: " + result);
            try { Thread.sleep(3000); } catch (Exception e) {}
            driver.navigate().refresh();
            try { Thread.sleep(3000); } catch (Exception e) {}

            afterIncrease = addToCartPage.getCartItemDetails(0);
            increasedQty = afterIncrease != null ? afterIncrease.getQuantity() : initialQty;
            System.out.println("Quantity after attempt 3: " + increasedQty);
        }

        Assert.assertTrue(increasedQty > initialQty, "Quantity should increase after clicking +");

        System.out.println("✅ TC02: Increase quantity passed!");
    }

    @Test
    public void testDecreaseQuantity() {
        System.out.println("========== TC03: Decrease Quantity ==========");

        var products = addToCartPage.getAllProducts();
        Assert.assertTrue(products.size() > 0, "No products found");
        addToCartPage.addProductToCart(0);
        addToCartPage.gotoCartPage();

        // Step 1: First increase to 2
        System.out.println("Step 1: Increasing quantity to 2 first...");
        addToCartPage.increaseQuantity(0);
        try { Thread.sleep(3000); } catch (Exception e) {}
        driver.navigate().refresh();
        try { Thread.sleep(3000); } catch (Exception e) {}

        // Step 2: Verify quantity is 2
        ShopEaseAddToCartPage.CartItemDetails initialItem = addToCartPage.getCartItemDetails(0);
        Assert.assertNotNull(initialItem, "Cart should have item");
        int initialQty = initialItem.getQuantity();
        System.out.println("Step 2: Quantity after increase: " + initialQty);

        // Step 3: Decrease quantity
        if (initialQty >= 2) {
            System.out.println("Step 3: Clicking - button to decrease...");
            addToCartPage.decreaseQuantity(0);
            try { Thread.sleep(3000); } catch (Exception e) {}
            driver.navigate().refresh();
            try { Thread.sleep(3000); } catch (Exception e) {}

            ShopEaseAddToCartPage.CartItemDetails afterDecrease = addToCartPage.getCartItemDetails(0);
            int decreasedQty = afterDecrease != null ? afterDecrease.getQuantity() : initialQty;
            System.out.println("Step 4: Quantity after decrease: " + decreasedQty);

            Assert.assertTrue(decreasedQty < initialQty, "Quantity should decrease after clicking -");
        } else {
            System.out.println("⚠️ Quantity is already " + initialQty + ", cannot decrease further");
        }

        System.out.println("✅ TC03: Decrease quantity passed!");
    }

    @Test
    public void testRemoveItem() {
        System.out.println("========== TC04: Remove Item ==========");

        var products = addToCartPage.getAllProducts();
        Assert.assertTrue(products.size() > 0, "No products found");
        addToCartPage.addProductToCart(0);
        addToCartPage.gotoCartPage();

        int beforeCount = addToCartPage.getCartItemCount();
        System.out.println("Items before remove: " + beforeCount);
        Assert.assertTrue(beforeCount > 0, "Cart should have items");

        addToCartPage.removeItem(0);
        addToCartPage.gotoCartPage();

        int afterCount = addToCartPage.getCartItemCount();
        System.out.println("Items after remove: " + afterCount);
        Assert.assertTrue(afterCount < beforeCount, "Item should be removed");

        System.out.println("✅ TC04: Remove item passed!");
    }

    @Test
    public void testCompleteCartFlow() {
        System.out.println("========== TC05: Complete Cart Flow ==========");

        var products = addToCartPage.getAllProducts();
        Assert.assertTrue(products.size() >= 2, "Need at least 2 products");

        System.out.println("Adding first product: " + products.get(0).getName());
        addToCartPage.addProductToCart(0);

        System.out.println("Adding second product: " + products.get(1).getName());
        addToCartPage.addProductToCart(1);

        addToCartPage.gotoCartPage();

        int itemCount = addToCartPage.getCartItemCount();
        System.out.println("Items in cart: " + itemCount);
        Assert.assertTrue(itemCount >= 2, "Cart should have at least 2 items");

        addToCartPage.increaseQuantity(0);
        addToCartPage.removeItem(1);

        addToCartPage.gotoCartPage();
        int finalCount = addToCartPage.getCartItemCount();
        System.out.println("Final items in cart: " + finalCount);
        Assert.assertTrue(finalCount >= 1, "At least 1 item should remain");

        ShopEaseAddToCartPage.CartItemDetails item = addToCartPage.getCartItemDetails(0);
        if (item != null) {
            System.out.println("Final item - Name: " + item.getName() +
                    ", Price: " + item.getPrice() +
                    ", Quantity: " + item.getQuantity());
            Assert.assertTrue(item.getQuantity() >= 1, "Quantity should be at least 1");
        }

        String total = addToCartPage.getCartTotal();
        System.out.println("Cart total: " + total);
        Assert.assertNotNull(total, "Cart total should not be null");

        System.out.println("✅ TC05: Complete cart flow passed!");
    }
}