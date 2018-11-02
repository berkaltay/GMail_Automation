package pageObjects;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

import static com.berk.e2e.BaseTest.properties;

public class LoginPage extends BasePage {

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    //*********Web Elements*********
    String usernameXpath = "//input[@id='identifierId']";
    String passwordName = "password";
    String identifierNextId = "identifierNext";
    String passwordNextId = "passwordNext";



    //*********Page Methods*********

    public void loginToGmail () throws InterruptedException {

        WebElement userElement = driver.findElement(By.xpath(usernameXpath));
        userElement.sendKeys(properties.getProperty("username"));

        //click next
        driver.findElement(By.id(identifierNextId)).click();

        //fill password field
        waitUntilClickableByLocator(By.name(passwordName));
        WebElement passwordElement = driver.findElement(By.name(passwordName));
        passwordElement.clear();
        passwordElement.sendKeys(properties.getProperty("password"));

        //click next
        waitUntilClickableByLocator(By.id(passwordNextId));
        driver.findElement(By.id(passwordNextId)).click();
    }

    public WebElement waitUntilClickableByLocator(By locator) {
        WebElement element = null;

        try {
            FluentWait wait = (new FluentWait(this.driver)).withTimeout(30, TimeUnit.SECONDS).pollingEvery(10,TimeUnit.MILLISECONDS).ignoring(StaleElementReferenceException.class).ignoring(NoSuchElementException.class);
            element = (WebElement)wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (Exception var4) {

        }

        return element;
    }


}
