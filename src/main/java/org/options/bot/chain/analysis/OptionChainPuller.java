package org.options.bot.chain.analysis;

import org.options.bot.utils.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OptionChainPuller {
    private static final String DRIVER_PROP = "webdriver.chrome.driver";
    private static final String DRIVER_PATH = "/home/administrator/Desktop/Karthik/Java/Workspace/NSEDaily/src/main/resources";
    private static OptionChainPuller optionChainPuller;

    private WebDriver driver;

    private OptionChainPuller() {

    }

    public static OptionChainPuller getInstance() {
        if (optionChainPuller == null)
            optionChainPuller = new OptionChainPuller();
        return optionChainPuller;
    }

    public void initWebDriver() {
        if(driver == null) {
            OS os = OS.getOS();
            String driverPath = DRIVER_PATH ;
            if (OS.WINDOWS.equals(os))
                driverPath = driverPath + "/chromedriver.exe";
            else if (OS.LINUX.equals(os))
                driverPath = driverPath + "/chromedriver";
            System.setProperty(DRIVER_PROP, driverPath);
            driver = new ChromeDriver();
            driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        }
    }

    public void navigateWebPage() {
        final String URL = "https://www1.nseindia.com/";
        driver.navigate().to(URL);
        driver.manage().window().maximize();
        Actions act = new Actions(driver);
        WebDriverWait wt = new WebDriverWait(driver, 20);
        WebElement live = driver.findElement(By.linkText("Live Market"));

        act.moveToElement(live).perform();

        WebElement equity = driver.findElement(By.xpath("//li[@id=\"main_livewth_oc\"]/a"));
        act.moveToElement(equity);
        act.click().build().perform();
    }

    public void refresh() {
        driver.navigate().refresh();
    }

    //"28NOV2019"
    public void selectExpiry(String expiry) {
        WebElement expirtySelect = driver.findElement(By.xpath("//select[@id=\"date\"]"));
        Select sc = new Select(expirtySelect);
        sc.selectByValue(expiry);
    }

    public String calculateExpiry() {
        String exxpiry = null;
        return exxpiry;
    }

    public float getNiftyPrice() throws NumberFormatException {
        WebElement niftyPrice=driver.findElement(By.xpath("//nobr[contains(text(),1)]"));
        String livePriceStr = niftyPrice.getText();
        livePriceStr = sanitizeNiftyPrice(livePriceStr);
        float livePrice = Float.parseFloat(livePriceStr);
        return livePrice;
    }

    public String sanitizeNiftyPrice(String livePrice) {
        if (livePrice.contains(",")) {
            return livePrice.replace(",", "");
        }
        return livePrice;
    }

    public List<String> getStrikePriceList(float livePrice) {
        List<String> strikePrices = new ArrayList<>();
        int adjustedPrice = (int) (Math.ceil(livePrice/100.0))*100;
        System.out.println("adjustedPrice : " + adjustedPrice);
        for (int i = -500; i < 500; i=i+100)
            strikePrices.add("" + (adjustedPrice+i));
        System.out.println("strikePrices : " + strikePrices);
        return strikePrices;
    }

    public void initOptionsPuller() {
        initWebDriver();
        navigateWebPage();
        selectExpiry("14NOV2019");  // TODO: 07/11/19 set expiry properly
    }

    public List<String> getStrikePriceList() {
        float niftyPrice = getNiftyPrice();
        System.out.println("niftyPrice : " + niftyPrice);
        List<String> strikePriceList = getStrikePriceList(niftyPrice);
        return strikePriceList;
    }

    public List<Object> getOptionData(String strikePrice) {
        List<Object> data = new ArrayList<>();
        WebElement optionTable = driver.findElement(By.xpath("//table[@id=\"octable\"]"));
        WebElement strikePriceRow = optionTable.findElement(By.xpath("//b[contains(text(),'"+strikePrice+"')]"));

        WebElement callOpenInterest = strikePriceRow.findElement(By.xpath("../parent::td/preceding-sibling::td/following-sibling::td"));
        WebElement callChangeInInterest = strikePriceRow.findElement(By.xpath("../parent::td/preceding-sibling::td/following-sibling::td[2]"));
        WebElement callVolume = strikePriceRow.findElement(By.xpath("../parent::td/preceding-sibling::td/following-sibling::td[3]"));
        WebElement callIV = strikePriceRow.findElement(By.xpath("../parent::td/preceding-sibling::td/following-sibling::td[4]"));
        WebElement callLTP=strikePriceRow.findElement(By.xpath("../parent::td/preceding-sibling::td/following-sibling::td[5]"));
        WebElement putLTP=strikePriceRow.findElement(By.xpath("../parent::td/preceding-sibling::td/following-sibling::td[17]"));
        WebElement putIV = strikePriceRow.findElement(By.xpath("../parent::td/preceding-sibling::td/following-sibling::td[18]"));
        WebElement putVolume = strikePriceRow.findElement(By.xpath("../parent::td/preceding-sibling::td/following-sibling::td[19]"));
        WebElement putChangeInInterest=strikePriceRow.findElement(By.xpath("../parent::td/preceding-sibling::td/following-sibling::td[20]"));
        WebElement putOpenInterest =strikePriceRow.findElement(By.xpath("../parent::td/preceding-sibling::td/following-sibling::td[21]"));
        WebElement niftyPrice=driver.findElement(By.xpath("//nobr[contains(text(),1)]"));

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = now.format(formatter);

        data.add(time);
        data.add(callOpenInterest.getText());
        data.add(callChangeInInterest.getText());
        data.add(callVolume.getText());
        data.add(callIV.getText());
        data.add(callLTP.getText());
        data.add(putLTP.getText());
        data.add(putIV.getText());
        data.add(putVolume.getText());
        data.add(putChangeInInterest.getText());
        data.add(putOpenInterest.getText());
        data.add(niftyPrice.getText());

        return data;
    }

    public static void main(String[] args) {

    }
}
