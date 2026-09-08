package stepdefinitions;

import config.ConfigReader;
import config.ConfigWriter;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import pages.BasePage;
import pages.TranslateGooglePage;
import utils.ReusableMethods;

public class TranslateGoogleSteps {

    WebDriver driver = Hooks.getDriver();
    TranslateGooglePage translateGooglePage = new TranslateGooglePage(driver);
    private static final Logger logger = LogManager.getLogger(TranslateGoogleSteps.class);


    @Given("translate {string}")
    public void translate(String key) throws InterruptedException {

        driver.get("https://translate.google.com/?sl=en&tl=tr&op=translate");



        Thread.sleep(1000);

        translateGooglePage.englishTextBox.sendKeys(key);
        Thread.sleep(1000);

        translateGooglePage.translateField.getText();
        Thread.sleep(1000);

        String value = translateGooglePage.translateField.getText();
        ConfigWriter.setProperty(key, value);

        logger.info(BasePage.redBold(key)+" : "+value);

        logger.info("Translate Success");





    }






















}
