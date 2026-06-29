package com.anuj.testcases;

import com.anuj.base.BaseTest;
import com.anuj.pages.ShopEaseAddToCartPage;
import com.anuj.pages.ShopEaseOrderPage;
import com.anuj.pages.ShopEasePaymentPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class OrderValidationTest extends BaseTest {

    private ShopEaseAddToCartPage addToCartPage;
    private ShopEasePaymentPage   paymentPage;
    private ShopEaseOrderPage     orderPage;

    private static final String EMAIL    = "anujydv@gmail.com";
    private static final String PASSWORD = "Anuj@1234";

    private static final String FIRST_NAME = "Anuj";
    private static final String LAST_NAME  = "Yadav";
    private static final String PHONE      = "9876543210";
    private static final String ADDRESS    = "123 Test Street";
    private static final String CITY       = "Bangalore";
    private static final String STATE      = "Karnataka";
    private static final String ZIP        = "560001";
    private static final String COUNTRY    = "India";

    @BeforeMethod
    public void setup() {
        addToCartPage = new ShopEaseAddToCartPage(driver);
        paymentPage   = new ShopEasePaymentPage(driver);
        orderPage     = new ShopEaseOrderPage(driver);
        addToCartPage.login(EMAIL, PASSWORD);
    }

    // =====================================================================
    // TC01 — Order Summary Validation
    // =====================================================================
    @Test(priority = 1)
    public void testOrderSummaryValidation() {
        System.out.println("\n========== 🚀 TC01: Order Summary Validation ==========");

        orderPage.navigateToOrders();

        String orderId     = orderPage.getFirstOrderId();
        String orderStatus = orderPage.getFirstOrderStatus();
        String orderTotal  = orderPage.getFirstOrderTotal();

        System.out.println("📝 Order Summary:");
        System.out.println("   ID     : " + orderId);
        System.out.println("   Status : " + orderStatus);
        System.out.println("   Total  : " + orderTotal);

        Assert.assertNotNull(orderId,     "❌ Order ID not found on Orders page");
        Assert.assertNotNull(orderStatus, "❌ Order Status not found on Orders page");
        Assert.assertNotNull(orderTotal,  "❌ Order Total not found on Orders page");

        boolean validStatus =
                orderStatus.equalsIgnoreCase("Confirmed")  ||
                        orderStatus.equalsIgnoreCase("Pending")    ||
                        orderStatus.equalsIgnoreCase("Shipped")    ||
                        orderStatus.equalsIgnoreCase("Delivered")  ||
                        orderStatus.equalsIgnoreCase("Cancelled")  ||
                        orderStatus.equalsIgnoreCase("Processing");

        Assert.assertTrue(validStatus,
                "❌ Invalid order status: " + orderStatus);

        Assert.assertTrue(orderTotal.contains("₹"),
                "❌ Total should contain ₹, got: " + orderTotal);

        System.out.println("✅ TC01 PASSED 🎉");
    }

    // =====================================================================
    // TC02 — View Order Details
    // =====================================================================
    @Test(priority = 2)
    public void testViewOrderDetails() {
        System.out.println("\n========== 🚀 TC02: View Order Details ==========");

        orderPage.navigateToOrders();

        int targetIndex = orderPage.getConfirmedOrderIndex();
        if (targetIndex < 0) targetIndex = 0;

        String orderIdFromCard = orderPage.getOrderIdByIndex(targetIndex);
        Assert.assertNotNull(orderIdFromCard, "❌ Could not read Order ID from card");
        System.out.println("📝 Target Order ID: " + orderIdFromCard);

        orderPage.expandOrderByIndex(targetIndex);
        orderPage.clickViewDetailsByIndex(targetIndex);

        Assert.assertTrue(orderPage.isOrderDetailPageLoaded(),
                "❌ Order Detail page did not load");

        String detailOrderId = orderPage.getOrderIdFromDetailPage();
        String detailStatus  = orderPage.getOrderStatusFromDetailPage();
        String detailTotal   = orderPage.getGrandTotalFromDetailPage();
        int    itemCount     = orderPage.getItemCountOnDetailPage();

        System.out.println("📝 Detail Page:");
        System.out.println("   Order ID : " + detailOrderId);
        System.out.println("   Status   : " + detailStatus);
        System.out.println("   Total    : " + detailTotal);
        System.out.println("   Items    : " + itemCount);

        Assert.assertEquals(detailOrderId, orderIdFromCard,
                "❌ Order ID mismatch between card and detail page");
        Assert.assertFalse(detailStatus.equals("N/A"),
                "❌ Status not found on detail page");
        Assert.assertTrue(detailTotal.contains("₹"),
                "❌ Grand Total missing ₹");
        Assert.assertTrue(itemCount > 0,
                "❌ No items found on detail page");

        System.out.println("✅ TC02 PASSED 🎉");
    }

    // =====================================================================
    // TC03 — Invoice Download
    // =====================================================================
    @Test(priority = 3)
    public void testInvoiceDownload() {
        System.out.println("\n========== 🚀 TC03: Invoice Download ==========");

        orderPage.navigateToOrders();

        String orderId = orderPage.getFirstOrderId();
        Assert.assertNotNull(orderId, "❌ No orders found");
        System.out.println("📝 Invoice test for Order: " + orderId);

        // Direct detail page navigate — faster & reliable
        orderPage.navigateToOrderDetail(orderId);

        Assert.assertTrue(orderPage.isOrderDetailPageLoaded(),
                "❌ Order Detail page did not load");

        orderPage.clickInvoiceButton();
        orderPage.dismissPrintDialog();

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("order-success"),
                "❌ Navigated away from detail page after invoice click");

        System.out.println("✅ TC03 PASSED 🎉");
    }

    // =====================================================================
    // TC04 — Full Order Flow
    // =====================================================================
    @Test(priority = 4)
    public void testFullOrderFlow() {
        System.out.println("\n========== 🚀 TC04: Full Order Flow ==========");

        System.out.println("📌 Step 1: Navigate to Orders");
        orderPage.navigateToOrders();

        String orderId = orderPage.getFirstOrderId();
        Assert.assertNotNull(orderId, "❌ No orders found");

        String orderStatus = orderPage.getFirstOrderStatus();
        Assert.assertNotNull(orderStatus, "❌ Order Status not found");

        System.out.println("   Order ID : " + orderId);
        System.out.println("   Status   : " + orderStatus);

        System.out.println("📌 Step 2: Expand Confirmed order");
        int targetIndex = orderPage.getConfirmedOrderIndex();
        if (targetIndex < 0) targetIndex = 0;

        String targetOrderId = orderPage.getOrderIdByIndex(targetIndex);
        orderPage.expandOrderByIndex(targetIndex);

        System.out.println("📌 Step 3: Click View Details");
        orderPage.clickViewDetailsByIndex(targetIndex);

        Assert.assertTrue(orderPage.isOrderDetailPageLoaded(),
                "❌ Order Detail page did not load");

        System.out.println("📌 Step 4: Validate Detail Page");
        String detailOrderId = orderPage.getOrderIdFromDetailPage();
        int    itemCount     = orderPage.getItemCountOnDetailPage();

        System.out.println("   Detail Order ID : " + detailOrderId);
        System.out.println("   Items           : " + itemCount);

        Assert.assertEquals(detailOrderId, targetOrderId,
                "❌ Order ID mismatch");
        Assert.assertTrue(itemCount > 0,
                "❌ No items on detail page");

        System.out.println("📌 Step 5: Invoice Download");
        orderPage.clickInvoiceButton();
        orderPage.dismissPrintDialog();

        System.out.println("✅ TC04 PASSED 🎉");
    }
}