package Steps;

import Base.BaseTest;
import com.thoughtworks.gauge.Step;
import helper.ElementHelper;
import helper.StoreHelper;
import model.ElementInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;


public class StepImplementation extends BaseTest {

    protected static Logger logger = LogManager.getLogger(StepImplementation.class);

    public static String emailDegeri;
    public static int DEFAULT_MAX_ITERATION_COUNT = 150;
    public static int DEFAULT_MILLISECOND_WAIT_AMOUNT = 100;

    private List<WebElement> findElements(String key) {
        ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
        By infoParam = ElementHelper.getElementInfoToBy(elementInfo);
        return driver.findElements(infoParam);

    }

    @Step("Go to <url> address")
    public void goToUrl(String url) {

        StringBuilder sb = new StringBuilder(url);
        driver.get(String.valueOf(sb));
    }

    public void javaScriptClicker(WebDriver driver, WebElement element) {

        JavascriptExecutor jse = ((JavascriptExecutor) driver);
        jse.executeScript("var evt = document.createEvent('MouseEvents');"
                + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);"
                + "arguments[0].dispatchEvent(evt);", element);

    }

    private void clickElement(WebElement element) {
        javaScriptClicker(driver, element);
    }

    private WebElement findElement(String key) {
        try {
            ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
            By infoParam = ElementHelper.getElementInfoToBy(elementInfo);
            WebDriverWait webDriverWait = new WebDriverWait(driver, 3, 300);
            WebElement webElement = webDriverWait
                    .until(ExpectedConditions.presenceOfElementLocated(infoParam));
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'center'})",
                    webElement);
            return webElement;

        } catch (TimeoutException e) {
            Assert.fail("Verilen Sürede Aranan Eleman Oluşmamıştır...");
            return null;
        }
    }

    @Step({"select date of birth element"})
    public void selectDaysElement() {
        Select days = new Select(driver.findElement(By.id("days")));
        days.selectByValue("5");
        Select months = new Select(driver.findElement(By.id("months")));
        months.selectByValue("5");
        Select years = new Select(driver.findElement(By.id("years")));
        years.selectByValue("1990");
    }

    @Step({"select state element"})
    public void selectStateElement() {
        Select state = new Select(driver.findElement(By.id("id_state")));
        state.selectByValue("9");

    }

    @Step("Scroll Page Down")
    public void scrollPage() {

        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,300)", "");
    }

    @Step({"Wait <value> seconds",
            "<int> saniye bekle"})
    public void waitBySeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step("<key> li element olustuysa tikla")
    public void elementVarsaTikla(String key) {
        List<WebElement> elements = findElements(key);
        if (elements.size() > 0) {
            clickElement(elements.get(0));
        }
    }

    @Step("Focus back tab")
    public void focusBackTab() {
        ArrayList<String> tabList = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(tabList.get(0));

    }

    @Step({"Click to element <key>",
            "Elementine tıkla <key>"})
    public void clickElement(String key) {
        WebElement element = findElement(key);
        clickElement(element);

    }

    @Step({"Write value <key> to element <key>",
            "<key> textini elemente yaz <key>"})
    public void sendKeys(String email, String key) {
        if (!key.equals("")) {
            findElement(key).clear();
            findElement(key).sendKeys(email);
        }
    }


    @Step("<email> degerini getText ile Al")
    public void pasteEmail(String email) {

        emailDegeri = findElement(email).getText();

    }

    @Step("<key> li elemana alınan değeri yapıştır")
    public void paste(String key) {

        findElement(key).sendKeys(emailDegeri);
    }

    @Step("<key> Kullanıcı Adı veya Parola Yanlis")
    public void keyliElemanOlusmamisMi(String key) {

        List<WebElement> elements = findElements(key);

        Assert.assertTrue("Authentication failed.", elements.size() > 0);

        logger.info("Authentication Pass.");


    }


    @Step("Back Page")
    public void backPage() {

        driver.navigate().back();
    }

    @Step("Chrome New Tab <key>")
    public void chromeNewTab(String key) {

        driver.findElement(By.cssSelector(key)).sendKeys(Keys.ALT + "t");
        ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(0));
        driver.get("https://generator.email/");

    }

    @Step("Email Generator Copy Mail <key>")
    public void copyToMail(String key) {
        driver.findElement(By.cssSelector(key)).sendKeys(Keys.CONTROL + "c");

        logger.info(key);

    }


    @Step("Print element text by css <totalPrice>, totalShipping <totalShipping>, totalProduct <totalProduct>")
    public void printElementText(String totalPrice, String totalShipping, String totalProduct) {
        logger.info("elemanın Text degeri: " + findElement(totalPrice).getText());
        logger.info("elemanın Text degeri: " + findElement(totalShipping).getText());
        logger.info("elemanın Text degeri: " + findElement(totalProduct).getText());
        String total = totalShipping + totalProduct;
        total = totalProduct;
        Assert.assertTrue("ürün toplam fiyatı kargo fiyatı toplamı ile eşit değil", total == totalProduct);
    }

    @Step({"Check if element <key> exists",
            "Wait for element to load with key <key>",
            "Element var mı kontrol et <key>",
            "Elementinin yüklenmesini bekle <key>"})
    public WebElement getElementWithKeyIfExists(String key) {
        ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
        By by = ElementHelper.getElementInfoToBy(elementInfo);

        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            if (driver.findElements(by).size() > 0) {
                return driver.findElement(by);
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assert.fail("Element: '" + key + "' doesn't exist.");
        logger.info("Element Oluşmuştur..");
        return null;
    }

    @Step({"Wait <value> milliseconds",
            "<long> milisaniye bekle"})
    public void waitByMilliSeconds(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
