package com.berk.e2e;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import reportFactory.ReportUtility;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class BaseTest extends ReportUtility {
    public static WebDriver driver;
    public static WebDriverWait wait;
    public static Properties properties = new Properties();
    public String testNameFromXml = null;

    //*********Page Variables*********
    static String baseURL = "https://mail.google.com/";
    static String path = System.getProperty("user.dir") + "//test.properties";

    public BaseTest(WebDriver driver, WebDriverWait wait) {

    }

    @Before
    public void setUp() throws Exception {

        System.setProperty("webdriver.chrome.driver",System.getProperty("user.dir") + "//src//setup//chromedriver.exe");
        driver = new ChromeDriver();
        createReportFile();
        testNameFromXml = this.getClass().getName();
        ReportUtility.createTest(this.getClass().getName());
        ReportUtility.createChildTest(testNameFromXml);
        wait = new WebDriverWait(driver,15);
        FileInputStream fs = new FileInputStream(path);
        properties.load(fs);
        driver.manage().timeouts().implicitlyWait(30,TimeUnit.SECONDS);
        driver.manage().window().maximize();
        driver.get(baseURL);
        }


    @After
    public void tearDown() throws Exception {
        saveReport();
        driver.quit();
    }
}


