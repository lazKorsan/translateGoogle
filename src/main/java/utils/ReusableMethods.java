package utils;

import drivers.DriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class ReusableMethods {


    /**
     * Test sırasında belirli bir süre beklemek için kullanılır.
     * NOT: Bu, 'explicit wait' yerine geçmez. Sadece geçici veya demo amaçlı kullanılmalıdır.
     *
     * @param saniye Beklenecek süre (saniye cinsinden).
     */
    public static void bekle(int saniye) {
        try {
            Thread.sleep(saniye * 1000L);
        } catch (InterruptedException e) {
            // InterruptedException durumunda, thread'in kesintiye uğradığını belirtmek iyi bir pratiktir.
            Thread.currentThread().interrupt();
            System.err.println("Thread bekleme sırasında kesintiye uğradı: " + e.getMessage());
        }
    }


    public static String getScreenshot(String name) throws IOException {
        // naming the screenshot with the current date to avoid duplication
        String date = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        // TakesScreenshot is an interface of selenium that takes the screenshot
        TakesScreenshot ts = (TakesScreenshot) DriverManager.getDriver();
        File source = ts.getScreenshotAs(OutputType.FILE);
        // full path to the screenshot location
        String target = System.getProperty("user.dir") + "/target/Screenshots/" + name + date + ".png";
        File finalDestination = new File(target);
        // save the screenshot to the path given
        FileUtils.copyFile(source, finalDestination);
        return target;
    }


    //========Switching Window=====//
    public static void switchToWindow(String targetTitle) {
        String origin = DriverManager.getDriver().getWindowHandle();
        for (String handle : DriverManager.getDriver().getWindowHandles()) {
            DriverManager.getDriver().switchTo().window(handle);
            if (DriverManager.getDriver().getTitle().equals(targetTitle)) {
                return;
            }
        }
        DriverManager.getDriver().switchTo().window(origin);
    }


    /**
     * Belirtilen bir elementin tıklanabilir olmasını bekler.
     *
     * @param driver  WebDriver nesnesi
     * @param element Beklenecek WebElement.
     * @param timeout Saniye cinsinden maksimum bekleme süresi.
     * @return Tıklanabilir hale gelen WebElement.
     */
    public static WebElement waitForClickablility(WebDriver driver, WebElement element, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Belirtilen bir elementin görünür olmasını bekler.
     *
     * @param driver  WebDriver nesnesi
     * @param element Beklenecek WebElement.
     * @param timeout Saniye cinsinden maksimum bekleme süresi.
     * @return Görünür hale gelen WebElement.
     */
    public static WebElement waitForVisibility(WebDriver driver, WebElement element, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    public static void scrollToElement(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    // 2. Scroll + Beklemeli Versiyon
    public static void scrollToElementWithWait(WebDriver driver, WebElement element, int timeout) {
        scrollToElement(driver, element);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));

        wait.until(ExpectedConditions.visibilityOf(element));
    }

    // 3. Sayfa Sonuna Scroll
    public static void scrollToBottom(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    /**
     * Fareyi belirtilen elementin üzerine getirir (hover).
     *
     * @param driver  WebDriver nesnesi
     * @param element Üzerine gelinecek WebElement.
     */
    public static void hover(WebDriver driver, WebElement element) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element).perform();
    }

    /**
     * Bir dropdown elementinden görünür metne göre seçim yapar.
     *
     * @param element Dropdown WebElement'i (<select>).
     * @param text    Seçilecek olan seçeneğin görünür metni.
     */
    public static void selectByVisibleText(WebElement element, String text) {
        Select select = new Select(element);
        select.selectByVisibleText(text);
    }

    /**
     * Bir dropdown elementinden 'value' attribute'una göre seçim yapar.
     *
     * @param element Dropdown WebElement'i (<select>).
     * @param value   Seçilecek olan seçeneğin 'value' attribute değeri.
     */
    public static void selectByValue(WebElement element, String value) {
        Select select = new Select(element);
        select.selectByValue(value);
    }

    /**
     * Bir dropdown elementinden index'e göre seçim yapar (0'dan başlar).
     *
     * @param element Dropdown WebElement'i (<select>).
     * @param index   Seçilecek olan seçeneğin index'i.
     */
    public static void selectByIndex(WebElement element, int index) {
        Select select = new Select(element);
        select.selectByIndex(index);
    }

    /**
     * Sayfada bir JavaScript alert'i çıkana kadar bekler ve Alert nesnesini döndürür.
     *
     * @param driver  WebDriver nesnesi.
     * @param timeout Saniye cinsinden maksimum bekleme süresi.
     * @return Beklenen Alert nesnesi.
     */
    public static Alert waitForAlert(WebDriver driver, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.alertIsPresent());
    }

    /**
     * Belirtilen elementin içinde, beklenen metin görünür olana kadar bekler.
     *
     * @param driver  WebDriver nesnesi.
     * @param element Beklenecek WebElement.
     * @param text    Elementin içinde olması beklenen metin.
     * @param timeout Saniye cinsinden maksimum bekleme süresi.
     * @return Metin bulunursa true, bulunamazsa false döner.
     */
    public static boolean waitForTextToBePresent(WebDriver driver, WebElement element, String text, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    /**
     * Belirtilen elemente çift tıklar.
     *
     * @param driver  WebDriver nesnesi.
     * @param element Çift tıklanacak WebElement.
     */
    public static void doubleClick(WebDriver driver, WebElement element) {
        Actions actions = new Actions(driver);
        actions.doubleClick(element).perform();
    }

    public static boolean scrollToElementAndCheckVisibility(WebDriver driver, WebElement element) {
        try {
            // Elementi sayfada görünür hâle getir
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            Thread.sleep(1000); // küçük bekleme, sayfanın kayması için (opsiyonel)

            // Elementin görünürlüğünü kontrol et
            return element.isDisplayed();
        } catch (Exception e) {
            return false; // Element bulunamaz veya görünür değilse false döner
        }
    }


    /**
     * Belirtilen WebElement'i JavaScript kullanarak geçici olarak sarı arka plan ve kırmızı kenarlık ile vurgular.
     * Bu metot, WebDriver nesnesini DriverManager'dan otomatik olarak alır.
     *
     * @param element Vurgulanacak WebElement.
     * @return Vurgulanan WebElement'in kendisi.
     */
    public static WebElement highLightToElement(WebElement element) {

        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
        js.executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 2px solid red;');", element);
        return element;

    }
}
