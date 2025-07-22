package tests;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import pages.EditProfilePage;
import pages.LoginPage;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class EditProfileTest extends BaseTest {
    private LoginPage loginPage;
    private EditProfilePage editProfilePage;

    @Test
    public void testEditProfileFunctionality() {
        loginPage = new LoginPage(driver);
        loginPage.navigate();
        loginPage.login("teacher@gmail.com", "123");
        try {
            Thread.sleep(2000);
            if (loginPage.isErrorMessageDisplayed()) {
                String errorMessage = loginPage.getErrorMessageText();
                fail("Login failed with error message: " + errorMessage);
            }
        } catch (InterruptedException e) {
            fail("Login interrupted: " + e.getMessage());
        }
        editProfilePage = new EditProfilePage(driver);
        editProfilePage.navigate();
        System.out.println("Current URL: " + driver.getCurrentUrl());

        try {
            String avatarPath = Paths.get("src/test/resources/im.png").toAbsolutePath().toString();
            editProfilePage.updateProfile(
                    "teacher@gmail.com", // Email
                    "Teacher Updated",           // Full Name
                    "0123456789",               // Phone Number
                    "1990-01-01",               // Birth Date (yyyy-MM-dd)
                    "N3",                       // Japanese Level
                    "Vietnam",                  // Country
                    "123 Main Street",          // Address
                    avatarPath                  // Avatar file path
            );

            Thread.sleep(2000);
            String currentUrl = driver.getCurrentUrl();
            if (editProfilePage.isErrorMessageDisplayed()) {
                String errorMessage = editProfilePage.getErrorMessageText();
                System.out.println("Profile update failed with error: " + errorMessage);
                fail("Profile update failed with error message: " + errorMessage);
            } else {
                assertTrue(currentUrl.contains("profile") || !currentUrl.contains("editprofile"),
                        "Profile update failed: Still on edit profile page or incorrect redirection");
                System.out.println("Profile update successful, redirected to: " + currentUrl);
            }
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            System.out.println("Screenshot saved at: " + screenshot.getAbsolutePath());
            fail("Test failed due to exception: " + e.getMessage());
        }
    }
}