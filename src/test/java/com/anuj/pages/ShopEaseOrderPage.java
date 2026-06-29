package com.anuj.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;

public class ShopEaseOrderPage {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    // ============ ORDERS PAGE LOCATORS ============
    private By orderHashSpan    = By.xpath("//span[text()='#']");
    private By statusBadgeTexts = By.xpath(
            "//span[text()='Confirmed' or text()='Pending' or text()='Shipped' or " +
                    "text()='Delivered' or text()='Cancelled' or text()='Processing']"
    );
    private By grandTotalLabel  = By.xpath("//span[contains(text(),'Grand Total')]");
    private By viewDetailsBtns  = By.xpath("//button[normalize-space()='View Full Details']");

    // ============ ORDER DETAIL PAGE LOCATORS ============
    // "Order confirmed!" heading — page load check
    private By detailHeading    = By.xpath("//h1[contains(text(),'Order confirmed')]");

    // Stat grid: 4 cells — date, status, payment, total
    // Structure: statCell > statIcon(span) + statLabel(span) + statVal(span)
    // statLabel texts: "Order date", "Status", "Payment", "Total"
    private By allStatLabels    = By.xpath("//span[contains(@style,'statLabel') or " +
            "text()='Order date' or text()='Status' or " +
            "text()='Payment' or text()='Total']");

    // Items: div containing "Qty:" text
    private By itemRows         = By.xpath("//div[.//div[contains(text(),'Qty:')]]");

    // Grand total box: "Grand total" span → sibling
    private By grandTotalBox    = By.xpath(
            "//*[contains(text(),'Grand total') or contains(text(),'Grand Total')]" +
                    "/following-sibling::*[1]"
    );

    // Invoice button
    private By invoiceBtn       = By.xpath("//button[contains(normalize-space(),'Invoice')]");

    // My Orders / Continue Shopping on detail page
    private By myOrdersBtn      = By.xpath("//button[contains(normalize-space(),'My Orders')]");
    private By continueShoppingBtn = By.xpath("//button[contains(normalize-space(),'Continue Shopping')]");

    public ShopEaseOrderPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.js     = (JavascriptExecutor) driver;
    }

    // ===================== NAVIGATION =====================

    public void navigateToOrders() {
        System.out.println("🔄 Navigating to Orders page...");
        driver.get("http://localhost:3000/orders");
        waitForOrdersToLoad();
        System.out.println("✅ Orders page loaded");
    }

    public void navigateToOrderDetail(String orderId) {
        System.out.println("🔄 Navigating to Order detail: " + orderId);
        driver.get("http://localhost:3000/order-success?orderId=" + orderId);
        sleep(2500);
    }

    // ===================== WAIT FOR ORDERS =====================

    private void waitForOrdersToLoad() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> {
                List<WebElement> hashes = d.findElements(orderHashSpan);
                List<WebElement> empty  = d.findElements(
                        By.xpath("//h3[contains(text(),'No orders found')]")
                );
                return !hashes.isEmpty() || !empty.isEmpty();
            });
        } catch (Exception e) {
            System.out.println("⚠️ Timeout waiting for orders");
        }
        sleep(500);
    }

    // ===================== GET ORDER DETAILS FROM CARD =====================

    public String getFirstOrderId() {
        return getOrderIdByIndex(0);
    }

    public String getOrderIdByIndex(int index) {
        try {
            List<WebElement> hashes = wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(orderHashSpan)
            );
            if (index >= hashes.size()) return null;
            WebElement idSpan = hashes.get(index).findElement(
                    By.xpath("./following-sibling::span[1]")
            );
            String id = idSpan.getText().trim();
            System.out.println("✅ Order ID at index " + index + ": " + id);
            return id.isEmpty() ? null : id;
        } catch (Exception e) {
            System.out.println("⚠️ getOrderIdByIndex(" + index + ") failed: " + e.getMessage());
            return null;
        }
    }

    public String getFirstOrderStatus() {
        try {
            List<WebElement> badges = driver.findElements(statusBadgeTexts);
            if (badges.isEmpty()) return null;
            String status = badges.get(0).getText().trim();
            System.out.println("✅ First Order Status: " + status);
            return status;
        } catch (Exception e) {
            System.out.println("⚠️ getFirstOrderStatus: " + e.getMessage());
            return null;
        }
    }

    public String getFirstOrderTotal() {
        try {
            List<WebElement> labels = driver.findElements(grandTotalLabel);
            if (labels.isEmpty()) return null;
            // parent div ke andar ₹ wali span
            WebElement parent = labels.get(0).findElement(By.xpath("./.."));
            List<WebElement> spans = parent.findElements(By.xpath(".//span"));
            for (WebElement span : spans) {
                String txt = span.getText().trim();
                if (txt.startsWith("₹")) {
                    System.out.println("✅ First Order Total: " + txt);
                    return txt;
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ getFirstOrderTotal: " + e.getMessage());
        }
        return null;
    }

    // ===================== CONFIRMED ORDER INDEX =====================

    public int getConfirmedOrderIndex() {
        try {
            List<WebElement> badges = driver.findElements(statusBadgeTexts);
            for (int i = 0; i < badges.size(); i++) {
                if (badges.get(i).getText().trim().equalsIgnoreCase("Confirmed")) {
                    System.out.println("✅ Confirmed order found at index: " + i);
                    return i;
                }
            }
            System.out.println("⚠️ No Confirmed order — using index 0");
            return badges.isEmpty() ? -1 : 0;
        } catch (Exception e) {
            return -1;
        }
    }

    // ===================== EXPAND ORDER =====================

    public void expandOrderByIndex(int index) {
        System.out.println("🔽 Expanding order at index: " + index);
        try {
            List<WebElement> hashSpans = driver.findElements(orderHashSpan);
            if (index >= hashSpans.size()) {
                System.out.println("⚠️ Index out of range");
                return;
            }
            // '#' span → 4 levels upar = card header div
            WebElement cardHeader = hashSpans.get(index).findElement(
                    By.xpath("./ancestor::div[4]")
            );
            scrollToElement(cardHeader);
            sleep(400);
            cardHeader.click();
            System.out.println("✅ Card clicked at index " + index);
            sleep(1200);
        } catch (Exception e) {
            System.out.println("⚠️ expandOrderByIndex JS fallback: " + e.getMessage());
            try {
                js.executeScript(
                        "var hashes=[...document.querySelectorAll('span')]" +
                                ".filter(s=>s.textContent==='#');" +
                                "if(hashes[" + index + "]){" +
                                "  var el=hashes[" + index + "];" +
                                "  for(var i=0;i<4;i++) el=el.parentElement;" +
                                "  el.click();" +
                                "}"
                );
                sleep(1200);
            } catch (Exception ex) {
                System.out.println("❌ JS fallback failed: " + ex.getMessage());
            }
        }
    }

    public void expandFirstOrder() {
        expandOrderByIndex(0);
    }

    // ===================== CLICK VIEW DETAILS =====================
    /*
     * KEY FIX: Expand ke baad sirf US card ka button visible hota hai.
     * Index pe depend mat karo — hamesha FIRST visible button click karo.
     * Agar index 1 wala expand kiya toh bhi DOM mein first clickable
     * "View Full Details" wahi card ka hoga.
     */
    public void clickViewDetailsByIndex(int cardIndex) {
        System.out.println("👁️ Clicking View Details (expanded card: " + cardIndex + ")");
        try {
            sleep(600);
            // Wait for at least one View Details button to appear
            List<WebElement> btns = wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(viewDetailsBtns)
            );
            System.out.println("   Found " + btns.size() + " View Details button(s)");

            // Hamesha first visible button click — expanded card ka hi hoga
            WebElement btn = btns.get(0);
            scrollToElement(btn);
            sleep(300);
            btn.click();
            System.out.println("✅ View Details clicked");
            sleep(2500);
        } catch (Exception e) {
            System.out.println("⚠️ clickViewDetails failed: " + e.getMessage());
            // JS fallback
            try {
                js.executeScript(
                        "var btns=document.querySelectorAll('button');" +
                                "for(var b of btns){" +
                                "  if(b.textContent.includes('View Full Details')){b.click();break;}" +
                                "}"
                );
                sleep(2500);
                System.out.println("✅ View Details clicked via JS");
            } catch (Exception ex) {
                System.out.println("❌ JS fallback failed");
            }
        }
    }

    public void clickViewDetails() {
        clickViewDetailsByIndex(0);
    }

    // ===================== ORDER DETAIL PAGE =====================

    public boolean isOrderDetailPageLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(detailHeading));
            System.out.println("✅ Order Detail page loaded successfully");
            return true;
        } catch (Exception e) {
            // Fallback: URL check
            String url = driver.getCurrentUrl();
            if (url.contains("order-success")) {
                System.out.println("✅ Detail page detected via URL");
                return true;
            }
            System.out.println("❌ Detail page NOT loaded: " + e.getMessage());
            return false;
        }
    }

    /*
     * KEY FIX: Order ID seedha URL se nikalo — 100% reliable.
     * orderPill locator React inline styles ki wajah se fail ho raha tha.
     */
    public String getOrderIdFromDetailPage() {
        try {
            String url = driver.getCurrentUrl();
            if (url.contains("orderId=")) {
                String id = url.split("orderId=")[1].split("&")[0].trim();
                System.out.println("✅ Order ID from URL: " + id);
                return id;
            }
        } catch (Exception e) {
            System.out.println("⚠️ getOrderIdFromDetailPage URL parse failed");
        }

        // Fallback: pill div mein monospace span dhundho
        try {
            // orderPill structure: div > span("Order") + span(orderId)
            List<WebElement> allSpans = driver.findElements(
                    By.xpath("//span[starts-with(text(),'ORD-')]")
            );
            if (!allSpans.isEmpty()) {
                String id = allSpans.get(0).getText().trim();
                System.out.println("✅ Order ID from span: " + id);
                return id;
            }
        } catch (Exception e) {
            System.out.println("⚠️ Order ID span fallback failed");
        }
        return "N/A";
    }

    /*
     * KEY FIX: statLabel text OrderSuccess.jsx mein hai:
     * "Order date", "Status", "Payment", "Total"
     * Inke SIBLING span mein value hai.
     * Structure: statCell > span(icon) + span(label) + span(value)
     */
    public String getOrderStatusFromDetailPage() {
        return getStatValueByLabel("Status");
    }

    public String getPaymentStatusFromDetailPage() {
        return getStatValueByLabel("Payment");
    }

    public String getGrandTotalFromDetailPage() {
        // "Total" stat cell se
        String fromStat = getStatValueByLabel("Total");
        if (!fromStat.equals("N/A")) return fromStat;

        // totalBox se fallback
        try {
            List<WebElement> candidates = driver.findElements(
                    By.xpath("//*[contains(text(),'Grand total')]")
            );
            for (WebElement c : candidates) {
                WebElement parent = c.findElement(By.xpath("./.."));
                List<WebElement> children = parent.findElements(By.xpath(".//*"));
                for (WebElement child : children) {
                    String txt = child.getText().trim();
                    if (txt.startsWith("₹")) {
                        System.out.println("✅ Grand Total (totalBox): " + txt);
                        return txt;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ getGrandTotalFromDetailPage fallback: " + e.getMessage());
        }
        return "N/A";
    }

    /**
     * statCell structure (OrderSuccess.jsx):
     *   <div style={S.statCell}>
     *     <span style={S.statIcon}>📅</span>
     *     <span style={S.statLabel}>Order date</span>   ← label
     *     <span style={S.statVal}>15 Jun 2025</span>    ← value
     *   </div>
     */
    private String getStatValueByLabel(String labelText) {
        try {
            // Label span dhundho, phir uska next sibling
            WebElement labelSpan = driver.findElement(
                    By.xpath("//span[normalize-space(text())='" + labelText + "']")
            );
            WebElement valueSpan = labelSpan.findElement(
                    By.xpath("./following-sibling::span[1]")
            );
            String val = valueSpan.getText().trim();
            System.out.println("✅ Stat [" + labelText + "]: " + val);
            return val.isEmpty() ? "N/A" : val;
        } catch (Exception e) {
            System.out.println("⚠️ getStatValueByLabel('" + labelText + "'): " + e.getMessage());
            return "N/A";
        }
    }

    public int getItemCountOnDetailPage() {
        try {
            // "Qty:" text wale divs = item rows
            List<WebElement> items = driver.findElements(itemRows);
            // Fallback: itemName divs count karo
            if (items.isEmpty()) {
                items = driver.findElements(
                        By.xpath("//div[contains(text(),'Qty:')]")
                );
            }
            System.out.println("📦 Item count: " + items.size());
            return items.size();
        } catch (Exception e) {
            return 0;
        }
    }

    // ===================== INVOICE =====================

    public void clickInvoiceButton() {
        System.out.println("📄 Clicking Invoice button...");
        try {
            WebElement btn = wait.until(
                    ExpectedConditions.presenceOfElementLocated(invoiceBtn)
            );
            scrollToElement(btn);
            sleep(500);
            // JS click — elementToBeClickable timeout issue fix
            js.executeScript("arguments[0].click();", btn);
            System.out.println("✅ Invoice button clicked (window.print() triggered)");
            sleep(1500);
        } catch (Exception e) {
            System.out.println("⚠️ Invoice click failed: " + e.getMessage());
        }
    }

    public void dismissPrintDialog() {
        try {
            java.awt.Robot robot = new java.awt.Robot();
            robot.keyPress(java.awt.event.KeyEvent.VK_ESCAPE);
            robot.keyRelease(java.awt.event.KeyEvent.VK_ESCAPE);
            sleep(500);
            System.out.println("✅ Print dialog dismissed");
        } catch (Exception e) {
            System.out.println("⚠️ dismissPrintDialog: " + e.getMessage());
        }
    }

    // ===================== NAVIGATION BUTTONS =====================

    public void clickMyOrdersButton() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(myOrdersBtn)).click();
            sleep(2000);
            System.out.println("✅ My Orders clicked");
        } catch (Exception e) {
            System.out.println("⚠️ My Orders btn: " + e.getMessage());
        }
    }

    public void clickContinueShopping() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(continueShoppingBtn)).click();
            sleep(2000);
            System.out.println("✅ Continue Shopping clicked");
        } catch (Exception e) {
            System.out.println("⚠️ Continue Shopping: " + e.getMessage());
        }
    }

    // ===================== HELPERS =====================

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