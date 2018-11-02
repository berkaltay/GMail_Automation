package com.berk.e2e;

import org.junit.Test;
import pageObjects.HomePage;
import pageObjects.LoginPage;

public class GMailTest extends BaseTest {
    public GMailTest() {
        super(driver, wait);
    }


    @Test
    public void testSendEmail() throws Exception {

        //*************PAGE INSTANTIATIONS*************
        LoginPage loginPage = new LoginPage(driver,wait);
        HomePage homePage = new HomePage(driver,wait);

        //*************PAGE METHODS********************
        loginPage.loginToGmail();

        homePage.composeMail();
        homePage.addSocialLabel();
        homePage.sendEmail();
        homePage.verifyReceivedEmail();
        homePage.markEmailAsStarred();
        homePage.verifySocialLabel();
        homePage.verifyBodyOfEmail();


    }

}