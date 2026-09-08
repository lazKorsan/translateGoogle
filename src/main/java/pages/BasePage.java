package pages;

import config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BasePage {

    // BasePage.java içine eklenecek pratik renklendiriciler:
    public static String green(String text) {
        return ANSI_GREEN + text + ANSI_RESET;
    }

    public static String greenBold(String text) {
        return ANSI_GREEN_BOLD + text + ANSI_RESET;
    }

    public static String yellowBold(String text) {
        return ANSI_YELLOW_BOLD + text + ANSI_RESET;
    }

    public static String blue(String text) {
        return ANSI_BLUE + text + ANSI_RESET;
    }

    public static String cyan(String text) {
        return ANSI_CYAN + text + ANSI_RESET;
    }

    public static String redBold(String text) {
        return ANSI_RED_BOLD + text + ANSI_RESET;
    }

    public static String purple(String text) {
        return ANSI_PURPLE + text + ANSI_RESET;
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_RED_BOLD = "\u001B[31;1m";
    public static final String ANSI_GREEN_BOLD = "\u001B[32;1m";
    public static final String ANSI_YELLOW_BOLD = "\u001B[33;1m";

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected static final Logger logger = LogManager.getLogger(BasePage.class);

    // Constructor
    public BasePage(WebDriver driver) {
        this.driver = driver;

        // Config'den timeout alma güvenli hale getirildi (Varsayılan 10 saniye)
        String timeoutStr = ConfigReader.getProperty("timeout", "keyOrText");
        int timeout = (timeoutStr != null && !timeoutStr.isEmpty()) ? Integer.parseInt(timeoutStr) : 10;

        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));

        // PageFactory ile elementleri alt sınıflar dahil tek noktadan başlat
        PageFactory.initElements(driver, this);
    }

    // Modern & Güvenli Temel Etkileşim Metotları
    public void click(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    public void type(WebElement element, String text) {
        WebElement visibleElement = wait.until(ExpectedConditions.visibilityOf(element));
        visibleElement.clear();
        visibleElement.sendKeys(text);
    }

    public boolean isElementReady(WebElement element) {
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            return element.isDisplayed() && element.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    // Reusable UI Visual Feedback (Vurgulama & Kırmızı Nokta) Metotları

    /**
     * Elementi mavi çerçeve ile vurgular ve merkezine Playwright tarzı kırmızı nokta ekler
     *
     * @param sizePx Dairenin çapı (px)
     */
    public void highlightWithRedDot(WebElement element, int sizePx) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            int radius = sizePx / 2;

            // 1. Mavi Çerçeve Vurgusu
            js.executeScript(
                    "arguments[0].style.border='3px solid #0066FF'; " +
                            "arguments[0].style.boxShadow='0 0 20px rgba(0, 102, 255, 0.8)'; " +
                            "arguments[0].style.transition='all 0.3s ease'; " +
                            "arguments[0].style.zIndex='9999'; " +
                            "arguments[0].style.backgroundColor='rgba(0, 102, 255, 0.1)';",
                    element
            );

            // 2. Playwright Kırmızı Noktası
            js.executeScript(
                    "var rect = arguments[0].getBoundingClientRect(); " +
                            "var centerX = rect.left + (rect.width / 2); " +
                            "var centerY = rect.top + (rect.height / 2); " +
                            "var daire = document.createElement('div'); " +
                            "daire.style.position = 'fixed'; " +
                            "daire.style.width = '" + sizePx + "px'; " +
                            "daire.style.height = '" + sizePx + "px'; " +
                            "daire.style.left = (centerX - " + radius + ") + 'px'; " +
                            "daire.style.top = (centerY - " + radius + ") + 'px'; " +
                            "daire.style.borderRadius = '50%'; " +
                            "daire.style.background = '#FF0055'; " +
                            "daire.style.border = '2px solid #FFFFFF'; " +
                            "daire.style.boxShadow = '0 0 0 4px rgba(255, 0, 85, 0.4), 0 0 15px rgba(255, 0, 85, 0.8)'; " +
                            "daire.style.zIndex = '10000'; " +
                            "daire.style.pointerEvents = 'none'; " +
                            "daire.style.animation = 'pulse 1.2s ease-in-out infinite'; " +
                            "daire.id = 'redCircle'; " +
                            "document.body.appendChild(daire);",
                    element
            );

            // 3. Pulse Animasyon Stili
            js.executeScript(
                    "if (!document.getElementById('redCirclePulseStyle')) { " +
                            "  var style = document.createElement('style'); " +
                            "  style.id = 'redCirclePulseStyle'; " +
                            "  style.innerHTML = `" +
                            "    @keyframes pulse { " +
                            "      0% { transform: scale(1); opacity: 1; } " +
                            "      50% { transform: scale(1.25); opacity: 0.7; } " +
                            "      100% { transform: scale(1); opacity: 1; } " +
                            "    } " +
                            "  `; " +
                            "  document.head.appendChild(style); " +
                            "}",
                    element
            );

        } catch (Exception e) {
            logger.warn("⚠️ Element vurgulanırken hata oluştu: " + e.getMessage());
        }
    }

    /**
     * Mavi çerçeveyi ve kırmızı noktayı sayfadan temizler
     */
    public void clearHighlight(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;

            js.executeScript(
                    "arguments[0].style.border=''; " +
                            "arguments[0].style.boxShadow=''; " +
                            "arguments[0].style.transition=''; " +
                            "arguments[0].style.zIndex=''; " +
                            "arguments[0].style.backgroundColor=''; " +
                            "var circle = document.getElementById('redCircle'); " +
                            "if(circle) { circle.remove(); }",
                    element
            );
        } catch (Exception e) {
            logger.warn("⚠️ Vurgulama temizlenirken hata oluştu: " + e.getMessage());
        }
    }

    /**
     * Çağrıldığı adımın adını ve gelen parametreleri otomatik tespit edip renklendirerek loglar.
     */
    public static void logStepDetails(Object... params) {
        // Çağıran metodun adını otomatik tespit eder
        StackTraceElement callingMethod = Thread.currentThread().getStackTrace()[2];
        String rawMethodName = callingMethod.getMethodName();

        // 1. camelCase isimleri kelimelere ayırır (örn: verifyKullanicisi -> verify Kullanicisi)
        // 2. Alt tireleri (_) boşluğa çevirir
        // 3. Tüm harfleri küçük harf yapar
        String formattedMethodName = rawMethodName
                .replaceAll("([a-z])([A-Z])", "$1 $2")
                .replace("_", " ")
                .toLowerCase();

        StringBuilder logMsg = new StringBuilder();
        logMsg.append(ANSI_YELLOW_BOLD).append("🚀 Step: ").append(ANSI_CYAN).append(formattedMethodName).append(ANSI_RESET);

        if (params.length > 0) {
            logMsg.append(ANSI_PURPLE).append(" | Parametreler: ").append(ANSI_RESET);
            for (Object param : params) {
                logMsg.append(ANSI_GREEN_BOLD).append("[").append(param.toString()).append("] ").append(ANSI_RESET);
            }
        }

        logger.info(logMsg.toString());
    }
}