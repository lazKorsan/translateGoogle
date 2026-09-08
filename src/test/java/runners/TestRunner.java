package runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = "src/test/java/features",
		glue = "stepdefinitions",
		plugin = {
				"pretty",
				"stepdefinitions.LoggingPlugin",
				"html:target/cucumber-reports/cucumber.html",
				"json:target/cucumber-reports/cucumber.json",
				"io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
		},
		monochrome = false,
		tags="@translate",
		publish=true,
        dryRun = false
)
public class TestRunner {
	private static final Logger logger = LogManager.getLogger(TestRunner.class);

	@BeforeClass
	public static void setup() {

		logger.info("Cucumber Test Runner başlatılıyor...");

		// mvn allure:report
		// allure serve target/allure-results
	}
}
