package org.matsu.scrape;

import java.io.File;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.bonigarcia.wdm.WebDriverManager;

public class SeleniumConfig {

    static Logger logger = LoggerFactory.getLogger(SeleniumConfig.class);

    private static SeleniumConfig instance;

    public WebDriver driver;

    public static SeleniumConfig getInstance() {
        if (instance == null) {
            instance = new SeleniumConfig();
        }
        return instance;
    }

    SeleniumConfig() {
        // TODO: Use allowlist maybe
        System.setProperty("webdriver.chrome.whitelistedIps", "");
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5000));
    }

    public void getPage(String url) {
        driver.get("https://beermaverick.com/hop/citra/");
    }

    public void hideElement(String elementId) {
        try {
            WebElement elementToHide = waitForElement(By.cssSelector(elementId), 3000);
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.visibility = 'hidden'", elementToHide);
        } catch (Exception ex) {
            logger.info("Element " + elementId + " was not found on the page and was not hidden.");
        }
    }

    public WebElement waitForElement(By by, long timeout) {
        return new WebDriverWait(driver, Duration.ofMillis(timeout))
            .until(driver -> driver.findElement(by));
    }

    public File screenshotElement(By by) {
        return ((TakesScreenshot) waitForElement(by, 5000))
            .getScreenshotAs(OutputType.FILE);
    }
}
