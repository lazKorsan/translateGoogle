package stepdefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import drivers.DriverManager;
import java.time.Duration;

/**
 * Artık sadece WebDriver'ın kurulup kapatılmasından sorumlu.
 * Step bazlı loglama (isim, süre, yavaş step uyarısı, test başlangıç/bitiş
 * özetleri) artık LoggingPlugin.java içinde, Cucumber'ın resmi event
 * sistemi (TestStepStarted/TestStepFinished) üzerinden yapılıyor.
 *
 * Bkz: stepdefinitions.LoggingPlugin
 */
public class Hooks {
	private static final Logger logger = LogManager.getLogger(Hooks.class);
	private static WebDriver driver;

	@Before
	public void setUp(Scenario scenario) {
		driver = DriverManager.getDriver();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));

		logger.debug("Driver başlatıldı - {}", driver.getClass().getSimpleName());
	}

	@After
	public void tearDown(Scenario scenario) {
		try {
			DriverManager.quitDriver();
			logger.debug("Driver kapatıldı.");
		} catch (Exception e) {
			logger.warn("Driver kapatılırken hata oluştu: {}", e.getMessage());
		}
	}

	public static WebDriver getDriver() {
		return driver;
	}
}