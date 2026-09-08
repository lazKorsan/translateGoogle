package pages;

import config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class TranslateGooglePage extends BasePage {

    private static final Logger logger = LogManager.getLogger(TranslateGooglePage.class);

    public TranslateGooglePage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//*[@id=\"i16\"]")
    public WebElement englishButton;

    @FindBy(xpath = "//*[@id=\"yDmH0d\"]/c-wiz[2]/div/div[2]/c-wiz/div[2]/c-wiz/div[1]/div[2]/div[2]/div/c-wiz/span/span/div/textarea")
    public WebElement englishTextBox;

    @FindBy(xpath = "//*[@id=\"yDmH0d\"]/c-wiz[2]/div/div[2]/c-wiz/div[2]/c-wiz/div[1]/div[2]/div[2]/c-wiz/div/div[6]/div/div[1]/span[1]/span/span")
    public WebElement translateField;
















}
