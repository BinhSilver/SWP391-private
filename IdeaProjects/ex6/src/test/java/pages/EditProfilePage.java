package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class EditProfilePage extends BasePage {
    public EditProfilePage(WebDriver driver) {
        super(driver);
    }
    private By emailInput = By.name("email");
    private By fullNameInput = By.name("fullName");
    private By phoneNumberInput = By.name("phoneNumber");
    private By birthDateInput = By.name("birthDate");
    private By japaneseLevelSelect = By.name("japaneseLevel");
    private By countryInput = By.name("country");
    private By addressInput = By.id("address");
    private By avatarInput = By.name("avatar");
    private By saveButton = By.xpath("//button[@type='submit' and text()='Save Changes']");
    private By errorMessage = By.cssSelector("p[style*='color: red']");
    public void enterEmail(String email) {
        type(emailInput, email);
    }
    public void enterFullName(String fullName) {
        type(fullNameInput, fullName);
    }
    public void enterPhoneNumber(String phoneNumber) {
        type(phoneNumberInput, phoneNumber);
    }
    public void enterBirthDate(String birthDate) {
        type(birthDateInput, birthDate);
    }
    public void selectJapaneseLevel(String level) {
        Select select = new Select(waitForVisibility(japaneseLevelSelect));
        select.selectByValue(level);
    }
    public void enterCountry(String country) {
        type(countryInput, country);
    }
    public void enterAddress(String address) {
        type(addressInput, address);
    }
    public void uploadAvatar(String filePath) {
        type(avatarInput, filePath);
    }
    public void clickSaveButton() {
        click(saveButton);
    }
    public boolean isErrorMessageDisplayed() {
        return isElementVisible(errorMessage);
    }
    public String getErrorMessageText() {
        try {
            return waitForVisibility(errorMessage).getText();
        } catch (TimeoutException e) {
            return "";
        }
    }
    public void navigate() {
        navigateTo("http://localhost:8080/SWP_HUY/editprofile");
    }
    public void updateProfile(String email, String fullName, String phoneNumber, String birthDate,
                              String japaneseLevel, String country, String address, String avatarPath) {
        enterEmail(email);
        enterFullName(fullName);
        enterPhoneNumber(phoneNumber);
        enterBirthDate(birthDate);
        selectJapaneseLevel(japaneseLevel);
        enterCountry(country);
        enterAddress(address);
        uploadAvatar(avatarPath);
        clickSaveButton();
    }
}