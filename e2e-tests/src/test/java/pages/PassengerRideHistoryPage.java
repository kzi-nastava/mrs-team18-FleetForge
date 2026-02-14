package pages;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import tests.TestBase;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PassengerRideHistoryPage extends TestBase {

    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(tagName = "h2")
    private WebElement title;

    @FindBy(xpath= "//button[@class='action-btn rate'][1]")
    private WebElement rateButton;

    @FindBy(xpath="//button[@aria-label='Rate driver 4 star']")
    private WebElement driverRatingButton;

    @FindBy(xpath="//button[@aria-label='Rate vehicle 3 star']")
    private WebElement vehicleRatingButton;

    @FindBy(xpath="//textarea[contains(@class, 'comment-input')]")
    private WebElement commentSection;

    @FindBy(xpath="//h3[text()='Rate your ride']")
    private WebElement ratingModal;

    @FindBy(xpath="//button[normalize-space(text())='Submit']")
    private WebElement submitBtn;

    @FindBy(xpath="//tbody/tr[1]//div[@class='stars']")
    private WebElement starsField;

    @FindBy(xpath="//tbody/tr[2]//div[@class='stars']")
    private WebElement previousStarsField;

    @FindBy(xpath="//tbody/tr[3]//div[@class='rating-expired']")
    private WebElement ratingExpiredField;




    public PassengerRideHistoryPage(WebDriver driver){
        this.driver=driver;
        wait= new WebDriverWait(driver, 10);
        PageFactory.initElements(driver,this);
    }

    public boolean rideHistoryLoaded()
    {
        try{
            wait.until(ExpectedConditions.textToBePresentInElement(title,"Ride history"));
            return true;
        }catch (TimeoutException e){
            return false;
        }
    }

    public void clickRateButton() {
        wait.until(ExpectedConditions.elementToBeClickable(rateButton));
        rateButton.click();
    }

    public void rateDriver() {
        wait.until(ExpectedConditions.elementToBeClickable(driverRatingButton));
        driverRatingButton.click();
    }

    public void rateVehicle() {
        wait.until(ExpectedConditions.elementToBeClickable(vehicleRatingButton));
        vehicleRatingButton.click();
    }

    public void enterComment(String comment) {
        wait.until(ExpectedConditions.elementToBeClickable(commentSection));
        commentSection.sendKeys(comment);
    }

    public void submitRating() {
        wait.until(ExpectedConditions.elementToBeClickable(submitBtn));
        submitBtn.click();
    }

    public boolean isRatingModalOpen() {
        try {
            wait.until(ExpectedConditions.visibilityOf(ratingModal));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isStarFieldDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(starsField));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isSubmitEnabled() {
        return submitBtn.isEnabled();
    }

    public boolean isPreviousRideRated() {
        try {
            wait.until(ExpectedConditions.visibilityOf(previousStarsField));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isRatingExpired() {
        try {
            wait.until(ExpectedConditions.visibilityOf(ratingExpiredField));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
