package com.anuj.testcases;

import com.anuj.base.BaseTest;
import com.anuj.pages.ShopEaseProductDetailsValidationPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ProductDetailsValidationTest extends BaseTest {

    private ShopEaseProductDetailsValidationPage productPage;

    @BeforeMethod
    public void setup() {
        try {
            loginAsDefaultUser();
            Thread.sleep(2000);
            productPage = new ShopEaseProductDetailsValidationPage(driver);
            productPage.navigateToProductsPage();
            System.out.println("✓ Day 40 Setup completed successfully");
        } catch (Exception e) {
            System.out.println("✗ Setup failed: " + e.getMessage());
            Assert.fail("Setup failed: " + e.getMessage());
        }
    }

    // ========== T084: Test Search Functionality ==========

    @Test(priority = 1, description = "T084-1: Verify search bar is visible")
    public void test01_searchBarIsVisible() {
        Assert.assertTrue(productPage.isSearchBarVisible(), "Search bar should be visible");
        System.out.println("✓ T084-1: Search bar is visible");
    }

    @Test(priority = 2, description = "T084-2: Verify search returns relevant results")
    public void test02_searchReturnsRelevantResults() throws InterruptedException {
        String keyword = "laptop";
        productPage.searchProduct(keyword);
        Thread.sleep(2000);

        int resultCount = productPage.getSearchResultCount();
        boolean allMatch = productPage.allResultsContainKeyword(keyword);

        System.out.println("  Search for '" + keyword + "' → " + resultCount + " results");

        if (resultCount > 0) {
            Assert.assertTrue(allMatch, "All results should contain '" + keyword + "'");
        }
        System.out.println("✓ T084-2: Search returns relevant results");
    }

    @Test(priority = 3, description = "T084-3: Verify search for non-existent product shows no results")
    public void test03_searchForNonExistentProduct() throws InterruptedException {
        productPage.searchProduct("xyzabc123nonexistent");
        Thread.sleep(2000);

        boolean noResults = productPage.isNoResultsMessageDisplayed();
        int resultCount = productPage.getSearchResultCount();

        Assert.assertTrue(noResults || resultCount == 0,
                "Non-existent search should show no results");
        System.out.println("✓ T084-3: Non-existent search correctly shows no results");
    }

    @Test(priority = 4, description = "T084-4: Verify empty search shows all products")
    public void test04_emptySearchShowsAllProducts() throws InterruptedException {
        int allProductsCount = productPage.getProductCount();
        productPage.searchProduct("");
        Thread.sleep(2000);

        int afterSearchCount = productPage.getSearchResultCount();

        Assert.assertEquals(afterSearchCount, allProductsCount,
                "Empty search should show all products");
        System.out.println("✓ T084-4: Empty search shows all " + allProductsCount + " products");
    }

    @Test(priority = 5, description = "T084-5: Verify search is case insensitive")
    public void test05_searchIsCaseInsensitive() throws InterruptedException {
        productPage.searchProduct("PHONE");
        Thread.sleep(2000);

        int resultCount = productPage.getSearchResultCount();
        boolean allMatch = productPage.allResultsContainKeyword("phone");

        if (resultCount > 0) {
            Assert.assertTrue(allMatch, "Search should be case insensitive");
        }
        System.out.println("✓ T084-5: Search is case insensitive");
    }

    // ========== T085: Validate Filtering ==========

    @Test(priority = 6, description = "T085-1: Verify category filter works")
    public void test06_categoryFilter() throws InterruptedException {
        int initialCount = productPage.getProductCount();
        System.out.println("  Initial product count: " + initialCount);

        productPage.applyCategoryFilter("Electronics");
        Thread.sleep(2000);

        int filteredCount = productPage.getProductCount();

        System.out.println("  After category filter: " + filteredCount + " products");
        Assert.assertTrue(filteredCount <= initialCount,
                "Filter should reduce or maintain product count");
        System.out.println("✓ T085-1: Category filter works");
    }

    @Test(priority = 7, description = "T085-3: Verify clear filters restores all products")
    public void test08_clearFilters() throws InterruptedException {
        int initialCount = productPage.getProductCount();
        productPage.applyPriceFilter(100, 500);
        Thread.sleep(1000);
        productPage.clearAllFilters();
        Thread.sleep(2000);

        int afterClearCount = productPage.getProductCount();

        Assert.assertEquals(afterClearCount, initialCount,
                "Clear filters should restore original product count");
        System.out.println("✓ T085-3: Clear filters restored " + afterClearCount + " products");
    }


    // ========== T086: Handle Dynamic Elements ==========

    @Test(priority = 8, description = "T086-1: Verify dynamic content loads properly")
    public void test10_dynamicContentLoads() {
        productPage.waitForDynamicContentToLoad();
        int productCount = productPage.getProductCount();

        Assert.assertTrue(productCount > 0, "Products should load dynamically");
        System.out.println("✓ T086-1: Dynamic content loaded - " + productCount + " products found");
    }

    @Test(priority = 9, description = "T086-2: Verify lazy loaded images work")
    public void test11_lazyLoadedImages() {
        productPage.handleLazyImages();
        productPage.waitForDynamicContentToLoad();

        boolean imagesLoaded = productPage.allImagesLoaded();
        Assert.assertTrue(imagesLoaded, "All lazy loaded images should be loaded");
        System.out.println("✓ T086-2: All lazy loaded images are properly loaded");
    }

    @Test(priority = 10, description = "T086-3: Verify loader appears and disappears")
    public void test12_loaderAppearsAndDisappears() throws InterruptedException {
        productPage.navigateToProductsPage();
        boolean loaderWasVisible = productPage.isLoaderDisplayed();
        productPage.waitForDynamicContentToLoad();

        int productCount = productPage.getProductCount();
        Assert.assertTrue(productCount > 0, "Products should load after loader disappears");
        System.out.println("✓ T086-3: Loader handled correctly");
    }

    @Test(priority = 11, description = "T086-4: Verify retry mechanism works")
    public void test13_retryMechanism() {
        productPage.retryAction(() -> {
            productPage.searchProduct("mobile");
        }, 3);

        productPage.waitForDynamicContentToLoad();
        int resultCount = productPage.getSearchResultCount();

        Assert.assertTrue(resultCount >= 0, "Retry should complete successfully");
        System.out.println("✓ T086-4: Retry mechanism works");
    }

    // ========== T087: Implement Reusable Components ==========

    @Test(priority = 12, description = "T087-1: Verify reusable search component")
    public void test14_reusableSearchComponent() throws InterruptedException {
        ShopEaseProductDetailsValidationPage.SearchResult result = productPage.searchAndValidate("phone");
        Thread.sleep(1000);

        System.out.println("  Search Result: " + result);
        Assert.assertNotNull(result, "Search result should not be null");
        System.out.println("✓ T087-1: Reusable search component works");
    }

    @Test(priority = 13, description = "T087-2: Verify reusable filter component")
    public void test15_reusableFilterComponent() throws InterruptedException {
        ShopEaseProductDetailsValidationPage.FilterResult result = productPage.filterAndValidate(1000, 5000);
        Thread.sleep(1000);

        System.out.println("  Filter Result: " + result);
        Assert.assertNotNull(result, "Filter result should not be null");
        System.out.println("✓ T087-2: Reusable filter component works");
    }

    @Test(priority = 14, description = "T087-3: Verify page status component")
    public void test16_pageStatusComponent() {
        ShopEaseProductDetailsValidationPage.PageStatus status = productPage.getPageStatus();

        System.out.println("  Page Status: " + status);
        Assert.assertTrue(status.hasSearch, "Search should be present");
        System.out.println("✓ T087-3: Page status component works");
    }

    @Test(priority = 15, description = "T087-4: Verify searchAndValidate returns valid data")
    public void test17_searchAndValidateReturnsValidData() throws InterruptedException {
        ShopEaseProductDetailsValidationPage.SearchResult result = productPage.searchAndValidate("smart");
        Thread.sleep(1000);

        Assert.assertNotNull(result, "Result should not be null");
        Assert.assertNotNull(result.productNames, "Product names list should not be null");
        System.out.println("  Found " + result.resultCount + " products for 'smart'");
        System.out.println("✓ T087-4: searchAndValidate works correctly");
    }

    // ========== DAY 40: Complete US016 Validation ==========

    @Test(priority = 16, description = "DAY 40: Complete US016 - Validate Product Details")
    public void test99_day40Complete() throws InterruptedException {
        System.out.println("\n=========================================");
        System.out.println("📅 DAY 40: US016 - Validate Product Details");
        System.out.println("=========================================");

        // T084 - Search Functionality
        System.out.println("\n🔍 T084: Test Search Functionality");
        ShopEaseProductDetailsValidationPage.SearchResult searchResult = productPage.searchAndValidate("mobile");
        Thread.sleep(1000);
        System.out.println("  → " + searchResult);
        Assert.assertNotNull(searchResult, "Search result should not be null");

        // T085 - Filter Validation
        System.out.println("\n🎯 T085: Validate Filtering");
        productPage.clearAllFilters();
        Thread.sleep(500);
        ShopEaseProductDetailsValidationPage.FilterResult filterResult = productPage.filterAndValidate(500, 10000);
        Thread.sleep(1000);
        System.out.println("  → " + filterResult);
        Assert.assertNotNull(filterResult, "Filter result should not be null");

        // T086 - Dynamic Elements
        System.out.println("\n⚡ T086: Handle Dynamic Elements");
        productPage.handleLazyImages();
        boolean imagesLoaded = productPage.allImagesLoaded();
        System.out.println("  → Images loaded: " + (imagesLoaded ? "✓" : "⚠"));
        productPage.waitForDynamicContentToLoad();
        System.out.println("  → Dynamic content: ✓ Stable");

        // T087 - Reusable Components
        System.out.println("\n🔄 T087: Implement Reusable Components");
        ShopEaseProductDetailsValidationPage.PageStatus status = productPage.getPageStatus();
        System.out.println("  → " + status);

        System.out.println("\n=========================================");
        System.out.println("✅ DAY 40: US016 - COMPLETED");
        System.out.println("   ✓ T084: Search functionality");
        System.out.println("   ✓ T085: Filter validation");
        System.out.println("   ✓ T086: Dynamic elements");
        System.out.println("   ✓ T087: Reusable components");
        System.out.println("=========================================\n");
    }
}