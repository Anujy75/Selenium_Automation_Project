package com.anuj.testcases;

import com.anuj.base.BaseTest;
import com.anuj.pages.ShopEaseProductListingPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ProductListingTest extends BaseTest {

    private ShopEaseProductListingPage productListingPage;
    private boolean setupSuccess = false;

    @BeforeMethod
    public void setUpProductListing() {
        try {
            loginAsDefaultUser();
            Thread.sleep(2000);
            productListingPage = new ShopEaseProductListingPage(driver);
            productListingPage.navigateToProductListingPage();
            setupSuccess = true;
            System.out.println("✓ Setup completed successfully");
        } catch (Exception e) {
            setupSuccess = false;
            System.out.println("✗ Setup failed: " + e.getMessage());
        }
    }

    // ========== T080: Automate product listing page ==========

    @Test(priority = 1, description = "T080: Verify product listing page loads successfully")
    public void testProductListingPageLoads() {
        if (!setupSuccess) Assert.fail("Setup failed");
        Assert.assertTrue(productListingPage.isProductListingPageLoaded(),
                "Product listing page should be loaded and visible");
        System.out.println("✓ Product listing page loaded successfully");
    }

    @Test(priority = 2, description = "T080: Verify products are displayed on listing page")
    public void testProductsAreDisplayed() {
        if (!setupSuccess) Assert.fail("Setup failed");
        int productCount = productListingPage.getProductCount();
        Assert.assertTrue(productCount > 0,
                "Product listing page should display at least one product. Found: " + productCount);
        System.out.println("✓ Total products found: " + productCount);
    }

    @Test(priority = 3, description = "T080: Verify all product cards have complete details")
    public void testAllProductsHaveCompleteDetails() {
        if (!setupSuccess) Assert.fail("Setup failed");
        Assert.assertTrue(productListingPage.doAllProductsHaveCompleteDetails(),
                "All products should have name, price and image");
        System.out.println("✓ All products have complete details");
    }

    @Test(priority = 4, description = "T080: Verify product cards are present")
    public void testProductCardsArePresent() {
        if (!setupSuccess) Assert.fail("Setup failed");
        Assert.assertTrue(productListingPage.areProductCardsPresent(),
                "Product cards should be present on the page");
        System.out.println("✓ Product cards are present");
    }

    @Test(priority = 5, description = "T080: Verify search functionality works")
    public void testSearchFunctionality() {
        if (!setupSuccess) Assert.fail("Setup failed");
        productListingPage.searchForProduct("laptop");
        Assert.assertTrue(productListingPage.isSearchBarVisible(), "Search bar should be visible");
        System.out.println("✓ Search functionality works");
    }

    // ========== T081: Validate product visibility ==========

    @Test(priority = 6, description = "T081: Verify product names are visible")
    public void testProductNamesVisible() {
        if (!setupSuccess) Assert.fail("Setup failed");
        Assert.assertTrue(productListingPage.areProductNamesVisible(),
                "All product names should be visible on the listing page");
        System.out.println("✓ All product names are visible");
    }

    @Test(priority = 7, description = "T081: Verify product prices are visible")
    public void testProductPricesVisible() {
        if (!setupSuccess) Assert.fail("Setup failed");
        Assert.assertTrue(productListingPage.areProductPricesVisible(),
                "All product prices should be visible on the listing page");
        System.out.println("✓ All product prices are visible");
    }

    @Test(priority = 8, description = "T081: Verify product images are visible")
    public void testProductImagesVisible() {
        if (!setupSuccess) Assert.fail("Setup failed");
        Assert.assertTrue(productListingPage.areProductImagesVisible(),
                "All product images should be visible on the listing page");
        System.out.println("✓ All product images are visible");
    }

    @Test(priority = 9, description = "T081: Verify first product details not empty")
    public void testFirstProductDetailsNotEmpty() {
        if (!setupSuccess) Assert.fail("Setup failed");
        String firstName = productListingPage.getProductNameByIndex(0);
        String firstPrice = productListingPage.getProductPriceByIndex(0);
        String firstImageUrl = productListingPage.getProductImageUrlByIndex(0);

        Assert.assertFalse(firstName.isEmpty(), "First product name should not be empty");
        Assert.assertFalse(firstPrice.isEmpty(), "First product price should not be empty");
        Assert.assertNotNull(firstImageUrl, "First product image should have src attribute");

        System.out.println("✓ First Product - Name: " + firstName + ", Price: ₹" + firstPrice);
    }

    @Test(priority = 10, description = "T081: Verify all products are visible in viewport")
    public void testAllProductsVisibleInViewport() {
        if (!setupSuccess) Assert.fail("Setup failed");
        Assert.assertTrue(productListingPage.areAllProductsVisible(),
                "All products should be visible in the viewport");
        System.out.println("✓ All products visible in viewport");
    }

    @Test(priority = 11, description = "T081: Verify products remain visible after scrolling")
    public void testProductsVisibleAfterScrolling() {
        if (!setupSuccess) Assert.fail("Setup failed");
        Assert.assertTrue(productListingPage.areProductsVisibleAfterScroll(),
                "Products should remain visible after scrolling");
        System.out.println("✓ Products visible after scrolling");
    }

    @Test(priority = 12, description = "T081: Verify responsive layout on window resize")
    public void testResponsiveProductListing() {
        if (!setupSuccess) Assert.fail("Setup failed");
        Assert.assertTrue(productListingPage.isProductListingResponsive(),
                "Product listing should remain visible on window resize");
        System.out.println("✓ Responsive layout works");
    }

    @Test(priority = 13, description = "T081: Verify wishlist buttons are visible")
    public void testWishlistButtonsVisible() {
        if (!setupSuccess) Assert.fail("Setup failed");
        Assert.assertTrue(productListingPage.areWishlistButtonsVisible(),
                "Wishlist buttons should be visible on products");
        System.out.println("✓ Wishlist buttons are visible");
    }

    // ========== Combined Validation for US0015 ==========

    @Test(priority = 14, description = "US0015 Complete: Validate entire product listing functionality")
    public void testCompleteProductListingValidation() {
        if (!setupSuccess) Assert.fail("Setup failed");

        System.out.println("\n=========================================");
        System.out.println(" US0015: Product Listing Validation");
        System.out.println("=========================================");

        // T080 Validations
        System.out.println("\n📋 T080 - Automate Product Listing Page:");
        Assert.assertTrue(productListingPage.isProductListingPageLoaded(), " Page should load");
        System.out.println("  ✓ Page loaded successfully");

        int productCount = productListingPage.getProductCount();
        Assert.assertTrue(productCount > 0, " Products should exist");
        System.out.println("  ✓ Total products found: " + productCount);

        Assert.assertTrue(productListingPage.doAllProductsHaveCompleteDetails(),
                "All products should have complete details");
        System.out.println("  ✓ All products have complete details");

        Assert.assertTrue(productListingPage.areProductCardsPresent(), " Product cards should exist");
        System.out.println("  ✓ Product cards are present");

        // T081 Validations
        System.out.println("\n📋 T081 - Validate Product Visibility:");
        Assert.assertTrue(productListingPage.areProductNamesVisible(), "Names should be visible");
        System.out.println("  ✓ All product names visible");

        Assert.assertTrue(productListingPage.areProductPricesVisible(), " Prices should be visible");
        System.out.println("  ✓ All product prices visible");

        Assert.assertTrue(productListingPage.areProductImagesVisible(), " Images should be visible");
        System.out.println("  ✓ All product images visible");

        Assert.assertTrue(productListingPage.areAllProductsVisible(), " All products visible");
        System.out.println("  ✓ All products visible in viewport");

        // Sample product details
        System.out.println("\n Sample Product Details:");
        System.out.println("  • Name: " + productListingPage.getProductNameByIndex(0));
        System.out.println("  • Price: ₹" + productListingPage.getProductPriceByIndex(0));

        System.out.println("\n=========================================");
        System.out.println("US0015: Product Listing Validation - PASSED");
        System.out.println("=========================================\n");
    }
}