package pageObjects;

import com.aventstack.extentreports.MediaEntityBuilder;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.berk.e2e.BaseTest.properties;


public class HomePage extends BasePage {

    public HomePage (WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    private String emailSubjectDate = null;
    private static String emailSubject = null;
    private String bodyOfEmail = "This is an auto-generated email for testing purposes";



    //*********Web Elements*********
    String composeButtonXpath = "//div[@role='button'][(.)='Compose']";
    String subjectFieldXpath = "//input[@name='subjectbox']";
    String bodyFieldXpath = "//div[@class='Am Al editable LW-avf']";
    String moreOptionsXpath = "//div[@data-tooltip='More options']";
    String labelElementXpath = "//*[text()='Label']";
    String socialLabelXpath = "//div[@role='menu']//div[@title='Social']";
    String sendButtonXpath = "//*[@role='button'][text()='Send']";
    String socialTabXpath = "//div[@aria-label='Social']";
    String subjectsXpath = "//span[@class='bog']";
    String socialLabelCheckXpath = "//div[@title='Search for all messages with label Social']";
    String starSignXpath = "//div[@role='checkbox']/span";
    String actualTextXpath = "//div[@title='Search for all messages with label Social']";

    //*********Page Methods*********
    public String uniqueDatePicker() throws Exception{

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");
        String uid = localDateAndTime.format(formatter);
        return uid;
    }


    public void composeMail() throws Exception {
        //click compose
        waitUntilClickableByLocator(By.xpath(composeButtonXpath));
        WebElement composeElement = driver.findElement(By.xpath(composeButtonXpath));
        composeElement.click();

        //fill sender field
        driver.findElement(By.name("to")).clear();
        driver.findElement(By.name("to")).sendKeys(properties.getProperty("username"));

        //fill subject field
        driver.findElement(By.xpath(subjectFieldXpath)).clear();
        emailSubjectDate = uniqueDatePicker();
        emailSubject = "This is a Subject and the date is: " + emailSubjectDate;
        driver.findElement(By.xpath(subjectFieldXpath)).sendKeys(emailSubject);

        //fill body
        driver.findElement(By.xpath(bodyFieldXpath)).sendKeys(bodyOfEmail);
        try{
            getChildTest().pass("Email composed truely!");
        } catch (Exception e) {
            getChildTest().fail("Email couldn't be composed");
        }
    }

    public void addSocialLabel(){
        //click More Options
        WebElement moreOptions = driver.findElement(By.xpath(moreOptionsXpath));
        moreOptions.click();

        //mouseHover Label
        WebElement labelElement = driver.findElement(By.xpath(labelElementXpath));
        Actions builder = new Actions(driver);
        builder.moveToElement(labelElement).build().perform();

        //click Social label
        driver.findElement(By.xpath(socialLabelXpath)).click();
        try{
            getChildTest().pass("Label applied with succcess!");
        } catch (Exception e) {
            getChildTest().fail("Label couldn't be applied!");
        }

    }

    public void sendEmail() throws InterruptedException {
        driver.findElement(By.xpath(sendButtonXpath)).click();
        try{
            getChildTest().pass("Email sent with success!");
        } catch (Exception e) {
            getChildTest().fail("Email sending error!");
        }
        Thread.sleep(1000);

    }

    public void verifyReceivedEmail() throws InterruptedException, IOException {
        //switch to Social tab
        waitUntilClickableByLocator(By.xpath(socialTabXpath));
        WebElement socialTabElement = driver.findElement(By.xpath(socialTabXpath));
        socialTabElement.click();
        try{
            getChildTest().pass("Email received with success!");
        } catch (Exception e) {
            getChildTest().fail("Email couldn't be received!");
        }
        //waitUntilClickableByLocator(By.xpath(subjectsXpath));



        //find and click the received email to open and verify the subject
        List<WebElement> email = driver.findElements(By.xpath(subjectsXpath));

        for (WebElement emailsubject : email) {
            if (emailsubject.getText().equals(emailSubject)) {
                emailsubject.click();
                getParentTest().pass("Email Subject match!");
                break;
            }
        }

        //wait for element to be visible
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(socialLabelCheckXpath)));

    }

    public void markEmailAsStarred(){
        WebElement starSign = driver.findElement(By.xpath(starSignXpath));
        starSign.click();
        try{
            getChildTest().pass("Email marked with star sign!");
        } catch (Exception e) {
            getChildTest().fail("Marking unsuccessfull!");
        }
    }

    public void verifySocialLabel() throws IOException {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(actualTextXpath)));
        String actualText = driver.findElement(By.xpath(actualTextXpath)).getText();
        String expectedText = "Social";
        if (actualText.contains(expectedText)) {
            System.out.println("Label is ok");
            getChildTest().pass("Social Label is OK!");

        }
        else{
            System.out.println("Label missing!");
            getChildTest().fail("Label missing! See the screenshot", MediaEntityBuilder.createScreenCaptureFromPath("screenshot.png").build());

        }

    }

    public void verifyBodyOfEmail() throws IOException{
        String actualBodyText = driver.findElement(By.xpath("//div[@class='no']//div[@role='gridcell']")).getText();
        if (actualBodyText.contains(bodyOfEmail)) {
            System.out.println("Body text OK!");
            getChildTest().pass("Body text OK!");
        }
        else{
            System.out.println("Body text does not match!");
            getChildTest().fail("Body text does not match! See the screenshot", MediaEntityBuilder.createScreenCaptureFromPath("screenshot.png").build());
        }

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
