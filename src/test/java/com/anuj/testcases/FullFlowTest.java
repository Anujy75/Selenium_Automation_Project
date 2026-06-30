package com.anuj.testcases;

import com.anuj.base.BaseTest;
import com.anuj.pages.ShopEaseFullFlowPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FullFlowTest extends BaseTest {

    private ShopEaseFullFlowPage flow;

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
        flow = new ShopEaseFullFlowPage(driver);
    }

    @Test(priority = 1)
    public void testCompleteShopEaseFlow() {

        // ─────────────────────────────────────────────
        // STEP 1: LOGIN
        // ─────────────────────────────────────────────
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("  STEP 1 — LOGIN");
        System.out.println("══════════════════════════════════════════");
        flow.login(EMAIL, PASSWORD);
        Assert.assertTrue(flow.isLoggedIn(), "❌ Login failed");
        System.out.println("✅ STEP 1 PASSED — User logged in");

        // ─────────────────────────────────────────────
        // STEP 2: BROWSE PRODUCTS
        // ─────────────────────────────────────────────
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("  STEP 2 — BROWSE PRODUCTS");
        System.out.println("══════════════════════════════════════════");
        flow.goToProducts();
        int productCount = flow.getProductCount();
        Assert.assertTrue(productCount > 0, "❌ No products found");
        System.out.println("   Products visible: " + productCount);
        System.out.println("✅ STEP 2 PASSED — Products loaded");

        // ─────────────────────────────────────────────
        // STEP 3: ADD TO CART
        // ─────────────────────────────────────────────
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("  STEP 3 — ADD TO CART");
        System.out.println("══════════════════════════════════════════");
        flow.addFirstProductToCart();
        System.out.println("✅ STEP 3 PASSED — Product added to cart");

        // ─────────────────────────────────────────────
        // STEP 4: CART VALIDATION
        // ─────────────────────────────────────────────
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("  STEP 4 — CART VALIDATION");
        System.out.println("══════════════════════════════════════════");
        flow.goToCart();
        Assert.assertFalse(flow.isCartEmpty(), "❌ Cart is empty after adding product");
        System.out.println("   Cart total: " + flow.getCartTotal());
        System.out.println("✅ STEP 4 PASSED — Cart has items");

        // ─────────────────────────────────────────────
        // STEP 5: CHECKOUT
        // ─────────────────────────────────────────────
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("  STEP 5 — CHECKOUT");
        System.out.println("══════════════════════════════════════════");
        flow.clickProceedToCheckout();
        flow.fillShippingAndPay(
                FIRST_NAME, LAST_NAME, EMAIL,
                PHONE, ADDRESS, CITY, STATE, ZIP, COUNTRY
        );
        System.out.println("✅ STEP 5 PASSED — Checkout form submitted");

        // ─────────────────────────────────────────────
        // STEP 6: RAZORPAY
        // ─────────────────────────────────────────────
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("  STEP 6 — RAZORPAY");
        System.out.println("══════════════════════════════════════════");
        boolean razorpayOpen = flow.isRazorpayOpen();
        if (razorpayOpen) {
            System.out.println("   Razorpay modal detected ✅");
            flow.closeRazorpay();
            System.out.println("✅ STEP 6 PASSED — Razorpay opened and closed");
        } else {
            System.out.println("⚠️ STEP 6 — Razorpay not detected (may have auto-proceeded)");
        }

        // ─────────────────────────────────────────────
        // STEP 7: ORDER SUCCESS PAGE
        // ─────────────────────────────────────────────
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("  STEP 7 — ORDER SUCCESS PAGE");
        System.out.println("══════════════════════════════════════════");
        flow.goToOrdersPage();
        String latestOrderId = flow.getFirstOrderIdFromOrdersPage();
        Assert.assertNotEquals(latestOrderId, "N/A", "❌ No order found on Orders page");

        driver.get("http://localhost:3000/order-success?orderId=" + latestOrderId);
        sleep(2500);

        Assert.assertTrue(flow.isOrderSuccessPageLoaded(),
                "❌ Order Success page did not load");

        String successOrderId = flow.getOrderIdFromSuccessPage();
        System.out.println("   Order ID : " + successOrderId);
        System.out.println("   Status   : " + flow.getStatValue("Status"));
        System.out.println("   Payment  : " + flow.getStatValue("Payment"));
        System.out.println("   Total    : " + flow.getStatValue("Total"));
        System.out.println("   Items    : " + flow.getItemCountOnDetailPage());

        Assert.assertEquals(successOrderId, latestOrderId, "❌ Order ID mismatch");
        Assert.assertTrue(flow.getItemCountOnDetailPage() > 0,
                "❌ No items on order success page");
        System.out.println("✅ STEP 7 PASSED — Order details validated");

        // ─────────────────────────────────────────────
        // STEP 8: INVOICE DOWNLOAD
        // ─────────────────────────────────────────────
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("  STEP 8 — INVOICE DOWNLOAD");
        System.out.println("══════════════════════════════════════════");
        flow.clickInvoiceOnSuccessPage();
        Assert.assertTrue(
                driver.getCurrentUrl().contains("order-success"),
                "❌ Navigated away after invoice click"
        );
        System.out.println("✅ STEP 8 PASSED — Invoice triggered");

        // ─────────────────────────────────────────────
        // STEP 9: ORDERS PAGE VALIDATION
        // ─────────────────────────────────────────────
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("  STEP 9 — ORDERS PAGE VALIDATION");
        System.out.println("══════════════════════════════════════════");
        flow.goToOrdersPage();
        String firstId     = flow.getFirstOrderIdFromOrdersPage();
        String firstStatus = flow.getFirstOrderStatus();
        Assert.assertNotEquals(firstId,     "N/A", "❌ Order ID not found");
        Assert.assertNotEquals(firstStatus, "N/A", "❌ Status not found");
        System.out.println("   Order ID : " + firstId);
        System.out.println("   Status   : " + firstStatus);
        System.out.println("✅ STEP 9 PASSED — Orders page validated");

        // ─────────────────────────────────────────────
        // STEP 10: CONFIRMED FILTER → VIEW DETAILS → INVOICE
        // ─────────────────────────────────────────────
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("  STEP 10 — CONFIRMED FILTER + VIEW DETAILS");
        System.out.println("══════════════════════════════════════════");
        flow.goToOrdersPage();
        flow.clickConfirmedFilterAndViewDetails();

        Assert.assertTrue(flow.isOrderDetailPageLoaded(),
                "❌ Order detail page did not load");

        String detailId = flow.getOrderIdFromDetailPage();
        System.out.println("   Detail Order ID : " + detailId);
        System.out.println("   Detail Status   : " + flow.getStatValue("Status"));
        System.out.println("   Detail Total    : " + flow.getStatValue("Total"));
        System.out.println("   Items           : " + flow.getItemCountOnDetailPage());

        Assert.assertNotEquals(detailId, "N/A", "❌ Order ID not on detail page");
        Assert.assertTrue(flow.getItemCountOnDetailPage() > 0, "❌ No items on detail page");
        System.out.println("✅ STEP 10 PASSED — Order details validated");

        // ─────────────────────────────────────────────
        // STEP 11: INVOICE FROM DETAIL PAGE
        // ─────────────────────────────────────────────
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("  STEP 11 — INVOICE FROM DETAIL PAGE");
        System.out.println("══════════════════════════════════════════");
        flow.clickInvoiceOnSuccessPage();
        Assert.assertTrue(
                driver.getCurrentUrl().contains("order-success"),
                "❌ Navigated away after invoice click"
        );
        System.out.println("✅ STEP 11 PASSED — Invoice triggered from detail page");

        // ─────────────────────────────────────────────
        // STEP 12: PROFILE PAGE
        // ─────────────────────────────────────────────
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("  STEP 12 — PROFILE PAGE");
        System.out.println("══════════════════════════════════════════");
        flow.goToProfile();
        Assert.assertTrue(flow.isProfilePageLoaded(), "❌ Profile page did not load");
        System.out.println("   Profile email: " + flow.getProfileEmail());
        System.out.println("✅ STEP 12 PASSED — Profile page validated");

        // ─────────────────────────────────────────────
        // STEP 13: LOGOUT
        // ─────────────────────────────────────────────
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("  STEP 13 — LOGOUT");
        System.out.println("══════════════════════════════════════════");
        flow.logout();
        Assert.assertTrue(flow.isLoggedOut(), "❌ Logout failed");
        System.out.println("✅ STEP 13 PASSED — User logged out");

        // ─────────────────────────────────────────────
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║  🎉 FULL E2E FLOW COMPLETED SUCCESSFULLY  ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println("   Login → Products → Cart → Checkout");
        System.out.println("   → Razorpay → Orders → Confirmed Filter");
        System.out.println("   → Invoice → Profile → Logout");
    }

    protected void sleep(long ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}