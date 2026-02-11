package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class NavbarPage {

    private WebDriverWait wait;

    @FindBy(className = "login-btn")
    private WebElement loginButton;

    @FindBy(className = "profile")
    private WebElement profileSection;

    @FindBy(xpath = "//button[text()='Logout']")
    private WebElement logoutButton;

    public NavbarPage(WebDriver driver) {
        this.wait = new WebDriverWait(driver, 5);
        PageFactory.initElements(driver, this);
    }

    public void waitUntilLoggedIn() {
        wait.until(ExpectedConditions.visibilityOf(profileSection));
    }

    public void waitUntilLoggedOut() {
        wait.until(ExpectedConditions.visibilityOf(loginButton));
    }

    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(profileSection)).click();
        wait.until(ExpectedConditions.elementToBeClickable(logoutButton)).click();
        waitUntilLoggedOut();
    }
}
