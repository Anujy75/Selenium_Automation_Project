package com.anuj.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TimeoutException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Day 40 - US016: Validate Product Details
 * T084: Test search functionality
 * T085: Validate filtering
 * T086: Handle dynamic elements
 * T087: Implement reusable components
 */
public class ShopEaseProductDetailsValidationPage {

    private WebDriver driver;
    private WebDriverWait wait;
    private WebDriverWait shortWait;

    // ========== LOCATORS ==========
    private By productCardBy = By.cssSelector(".card-hover");
    private By productNameBy = By.cssSelector("h3[style*='font-size: 16px'], h3[class*='name']");
    private By productPriceBy = By.cssSelector("span[style*='font-size: 20px'], .price-row span:first-child");
    private By productImageBy = By.cssSelector(".image-zoom img, .img-box img");
    private By searchInputBy = By.cssSelector("input[placeholder*='Search']");
    private By searchButtonBy = By.cssSelector("button[type='submit'], svg[class*='search']");
    private By clearSearchButtonBy = By.cssSelector("button[aria-label='clear'], .clear-btn");
    private By categoryChipBy = By.cssSelector(".cat-chip");
    private By filterMinPriceBy = By.cssSelector("input[placeholder*='Min'], [name='minPrice']");
    private By filterMaxPriceBy = By.cssSelector("input[placeholder*='Max'], [name='maxPrice']");
    private By applyFilterButtonBy = By.xpath("//button[contains(text(), 'Apply') or contains(text(), 'Apply Filter')]");
    private By clearFiltersButtonBy = By.xpath("//button[contains(text(), 'Clear') or contains(text(), 'Clear All')]");
    private By loadingSpinnerBy = By.cssSelector(".loader, .spinner, [class*='loading']");
    private By skeletonLoaderBy = By.cssSelector("[class*='skeleton']");
    private By noResultsMessageBy = By.xpath("//div[contains(text(), 'No products') or contains(text(), 'No results')]");
    private By resultCountBy = By.cssSelector("[class*='resultCount'], div[style*='font-size: 13px']");

    public ShopEaseProductDetailsValidationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        PageFactory.initElements(driver, this);
    }

    public void navigateToProductsPage() {
        driver.get("http://localhost:3000/products");
        waitForPageToLoad();
    }

    private void waitForPageToLoad() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(productCardBy));
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Warning: Page load timeout");
        }
    }

    // ========== T084: TEST SEARCH FUNCTIONALITY ==========

    public boolean isSearchBarVisible() {
        try {
            return driver.findElement(searchInputBy).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void searchProduct(String productName) {
        try {
            WebElement searchBox = driver.findElement(searchInputBy);
            searchBox.clear();
            searchBox.sendKeys(productName);
            Thread.sleep(300);

            try {
                WebElement searchBtn = driver.findElement(searchButtonBy);
                if (searchBtn.isDisplayed()) {
                    searchBtn.click();
                } else {
                    searchBox.sendKeys(Keys.ENTER);
                }
            } catch (Exception e) {
                searchBox.sendKeys(Keys.ENTER);
            }

            waitForDynamicContentToLoad();
        } catch (Exception e) {
            System.out.println("Search failed: " + e.getMessage());
        }
    }

    public List<String> getSearchResultNames() {
        List<String> names = new ArrayList<>();
        try {
            for (WebElement el : driver.findElements(productNameBy)) {
                names.add(el.getText().trim().toLowerCase());
            }
        } catch (Exception e) {}
        return names;
    }

    public boolean allResultsContainKeyword(String keyword) {
        List<String> results = getSearchResultNames();
        if (results.isEmpty()) return false;
        for (String name : results) {
            if (!name.contains(keyword.toLowerCase())) {
                System.out.println("  ✗ Product doesn't match: " + name);
                return false;
            }
        }
        return true;
    }

    public int getSearchResultCount() {
        try {
            String text = driver.findElement(resultCountBy).getText();
            String num = text.replaceAll("[^0-9]", "");
            return num.isEmpty() ? driver.findElements(productCardBy).size() : Integer.parseInt(num);
        } catch (Exception e) {
            return driver.findElements(productCardBy).size();
        }
    }

    public boolean isNoResultsMessageDisplayed() {
        try {
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(noResultsMessageBy)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void clearSearch() {
        try {
            driver.findElement(searchInputBy).clear();
            Thread.sleep(500);
        } catch (Exception e) {}
    }

    // ========== T085: VALIDATE FILTERING ==========

    public void applyCategoryFilter(String category) {
        try {
            for (WebElement cat : driver.findElements(categoryChipBy)) {
                if (cat.getText().equalsIgnoreCase(category)) {
                    cat.click();
                    waitForDynamicContentToLoad();
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Category filter failed: " + e.getMessage());
        }
    }

    public void applyPriceFilter(int minPrice, int maxPrice) {
        try {
            driver.findElement(filterMinPriceBy).clear();
            driver.findElement(filterMinPriceBy).sendKeys(String.valueOf(minPrice));
            driver.findElement(filterMaxPriceBy).clear();
            driver.findElement(filterMaxPriceBy).sendKeys(String.valueOf(maxPrice));
            Thread.sleep(300);

            try {
                driver.findElement(applyFilterButtonBy).click();
            } catch (Exception e) {}

            waitForDynamicContentToLoad();
        } catch (Exception e) {
            System.out.println("Price filter not available");
        }
    }

    public List<Integer> getAllPrices() {
        List<Integer> prices = new ArrayList<>();
        try {
            for (WebElement priceEl : driver.findElements(productPriceBy)) {
                String priceText = priceEl.getText().replace("₹", "").replace(",", "").trim();
                try {
                    prices.add(Integer.parseInt(priceText));
                } catch (NumberFormatException e) {}
            }
        } catch (Exception e) {}
        return prices;
    }

    public boolean allPricesInRange(int minPrice, int maxPrice) {
        List<Integer> prices = getAllPrices();
        if (prices.isEmpty()) return false;
        for (int price : prices) {
            if (price < minPrice || price > maxPrice) {
                System.out.println("  ✗ Price out of range: " + price);
                return false;
            }
        }
        return true;
    }

    public int getProductCount() {
        return driver.findElements(productCardBy).size();
    }

    public void clearAllFilters() {
        try {
            driver.findElement(clearFiltersButtonBy).click();
            waitForDynamicContentToLoad();
        } catch (Exception e) {
            System.out.println("Clear filters not available");
        }
    }

    // ========== T086: HANDLE DYNAMIC ELEMENTS ==========

    public void waitForDynamicContentToLoad() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingSpinnerBy));
        } catch (Exception e) {}

        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(skeletonLoaderBy));
        } catch (Exception e) {}

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(productCardBy));
            Thread.sleep(500);
        } catch (Exception e) {}
    }

    public boolean isLoaderDisplayed() {
        try {
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(loadingSpinnerBy)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void handleLazyImages() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight / 2);");
        try { Thread.sleep(500); } catch (Exception e) {}
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        try { Thread.sleep(500); } catch (Exception e) {}
        js.executeScript("window.scrollTo(0, 0);");
        try { Thread.sleep(500); } catch (Exception e) {}
    }

    public boolean allImagesLoaded() {
        List<WebElement> images = driver.findElements(productImageBy);
        if (images.isEmpty()) return true;

        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (WebElement img : images) {
            boolean loaded = (boolean) js.executeScript(
                    "return arguments[0].complete && arguments[0].naturalWidth > 0", img);
            if (!loaded) return false;
        }
        return true;
    }

    public void retryAction(Runnable action, int maxRetries) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                action.run();
                return;
            } catch (Exception e) {
                System.out.println("Retry " + (i + 1) + "/" + maxRetries);
                try { Thread.sleep(1000); } catch (Exception ex) {}
            }
        }
    }

    // ========== T087: REUSABLE COMPONENTS ==========

    public SearchResult searchAndValidate(String keyword) {
        SearchResult result = new SearchResult();
        result.keyword = keyword;

        searchProduct(keyword);
        result.resultCount = getSearchResultCount();
        result.productNames = getSearchResultNames();
        result.allMatch = allResultsContainKeyword(keyword);
        result.noResults = isNoResultsMessageDisplayed();

        return result;
    }

    public FilterResult filterAndValidate(int minPrice, int maxPrice) {
        FilterResult result = new FilterResult();
        result.minPrice = minPrice;
        result.maxPrice = maxPrice;

        applyPriceFilter(minPrice, maxPrice);
        result.productCount = getProductCount();
        result.prices = getAllPrices();
        result.allInRange = allPricesInRange(minPrice, maxPrice);

        return result;
    }

    public PageStatus getPageStatus() {
        PageStatus status = new PageStatus();
        status.productCount = getProductCount();
        status.hasSearch = isSearchBarVisible();
        status.hasProducts = status.productCount > 0;
        status.imagesLoaded = allImagesLoaded();
        return status;
    }

    // ========== HELPER METHODS ==========

    public String getFirstProductName() {
        try {
            return driver.findElements(productNameBy).get(0).getText();
        } catch (Exception e) {
            return "";
        }
    }

    // ========== INNER CLASSES FOR REUSABLE COMPONENTS ==========

    public static class SearchResult {
        public String keyword;
        public int resultCount;
        public List<String> productNames;
        public boolean allMatch;
        public boolean noResults;

        public boolean isValid() {
            return noResults ? true : (resultCount > 0 && allMatch);
        }

        public String toString() {
            return "SearchResult{keyword='" + keyword + "', count=" + resultCount +
                    ", allMatch=" + allMatch + ", noResults=" + noResults + "}";
        }
    }

    public static class FilterResult {
        public int minPrice;
        public int maxPrice;
        public int productCount;
        public List<Integer> prices;
        public boolean allInRange;

        public String toString() {
            return "FilterResult{range=" + minPrice + "-" + maxPrice +
                    ", count=" + productCount + ", allInRange=" + allInRange + "}";
        }
    }

    public static class PageStatus {
        public int productCount;
        public boolean hasSearch;
        public boolean hasProducts;
        public boolean imagesLoaded;

        public String toString() {
            return "PageStatus{products=" + productCount + ", search=" + hasSearch +
                    ", images=" + imagesLoaded + "}";
        }
    }
}