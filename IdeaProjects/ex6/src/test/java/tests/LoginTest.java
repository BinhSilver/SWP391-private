package tests;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import pages.LoginPage;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTest extends BaseTest {
    private LoginPage loginPage;

    @Test
    public void testLoginFunctionality() {
        loginPage = new LoginPage(driver);
        loginPage.navigate();
        System.out.println("Current URL: " + driver.getCurrentUrl());

        try {
            // Step 1: Nhập email và mật khẩu
            loginPage.login("teacher@gmail.com", "123");

            // Step 2: Đợi xử lý đăng nhập
            Thread.sleep(2000);

            // Step 3: Xác minh kết quả
            String currentUrl = driver.getCurrentUrl();
            if (loginPage.isErrorMessageDisplayed()) {
                String errorMessage = loginPage.getErrorMessageText();
                System.out.println("Login failed with error: " + errorMessage);
                fail("Login failed with error message: " + errorMessage);
            } else {
                assertTrue(currentUrl.contains("login"),
                        "Login failed: Still on login page or incorrect redirection");
                System.out.println("Login successful, redirected to: " + currentUrl);
            }
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            System.out.println("Screenshot saved at: " + screenshot.getAbsolutePath());
            fail("Test failed due to exception: " + e.getMessage());
        }
    }
}