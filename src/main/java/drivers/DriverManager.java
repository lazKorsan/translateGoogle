package drivers;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import config.ConfigReader;
import java.time.Duration;

public class DriverManager {
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static WebDriver getDriver() {
        if (driver.get() == null) {
            String browser = ConfigReader.getProperty("browser", "chrome").toLowerCase();
            switch (browser) {
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--remote-allow-origins=*");
                    chromeOptions.addArguments("--disable-notifications");
                    chromeOptions.addArguments("--disable-popup-blocking");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-extensions");
                    chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
                    chromeOptions.addArguments("--start-maximized");
                    chromeOptions.addArguments("--force-device-scale-factor=1.0");
                    chromeOptions.addArguments("--disable-search-engine-choice-screen");

                    ChromeDriver chromeDriver = new ChromeDriver(chromeOptions);


                    chromeDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
                    chromeDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(50));

                    driver.set(chromeDriver);
                    break;

                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    firefoxOptions.addPreference("layout.css.devPixelsPerPx", "1.0");
                    FirefoxDriver firefoxDriver = new FirefoxDriver(firefoxOptions);
                    firefoxDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
                    firefoxDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
                    driver.set(firefoxDriver);
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported browser: " + browser);
            }
        }
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            try {
                driver.get().quit();
            } catch (Exception e) {
                System.err.println("Driver kapatilirken hata (ignored): " + e.getMessage());
            } finally {
                driver.remove();
            }
        }
    }
}