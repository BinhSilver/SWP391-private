package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
public class LoginPage extends BasePage {
    public LoginPage(WebDriver driver) {
        super(driver);
    }
    private By emailInput = By.id("email");
    private By passwordInput = By.id("password");
    private By loginButton = By.xpath("//button[@type='submit' and text()='Đăng nhập']");
    private By errorMessage = By.cssSelector("p.error-message");
    public void enterEmail(String email) {
        type(emailInput, email);
    }
    public void enterPassword(String password) {
        type(passwordInput, password);
    }
    public void clickLoginButton() {
        click(loginButton);
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
        navigateTo("http://localhost:8080/SWP_HUY/login");
    }
    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLoginButton();
    }
}