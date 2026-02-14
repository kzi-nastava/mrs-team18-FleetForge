package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class PassengerHomePage {

    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(id = "sidebarBtn")
    private WebElement sidebar;

    @FindBy(xpath = "//a[span[text()='Favourite Rides']]")
    private WebElement favoriteRoutesLink;

    @FindBy(xpath = "//a[span[text()='Ride History']]")
    private WebElement rideHistoryLink;

    @FindBy(xpath = "//app-navbar//div[text()='Hello, passenger']")
    private WebElement passengerTitle;

    @FindBy(id = "passengers")
    private WebElement passengersInput;

    @FindBy(id = "now")
    private WebElement nowCheckBox;

    @FindBy(id = "type")
    private WebElement vehicleType;

    @FindBy(xpath = "//form//button[@type='submit']")
    private WebElement submitBtn;

    @FindBy(id = "babySeat")
    private WebElement babySeatCB;

    @FindBy(id = "datetime")
    private WebElement datetime;

    @FindBy(id="pickup")
    private WebElement pickup;

    @FindBy(id = "dropoff")
    private WebElement dropoff;

    @FindBy(xpath = "//form//input[contains(@placeholder,'Enter waypoint')]")
    private List<WebElement> waypointFields;

    public PassengerHomePage(WebDriver driver){
        this.driver= driver;
        wait= new WebDriverWait(driver, 10);
        PageFactory.initElements(driver,this);
    }

    public boolean isLoaded(){
        try {
            wait.until(ExpectedConditions.visibilityOf(passengerTitle));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void goToPassengerHistory(){
        sidebar.click();
        wait.until(ExpectedConditions.elementToBeClickable(rideHistoryLink)).click();
    }

    public void goToFavoritesPage(){
        sidebar.click();
        wait.until(ExpectedConditions.elementToBeClickable(favoriteRoutesLink)).click();
    }
    public boolean areAllCoordinatesFilled(){
        try{
            wait.until(ExpectedConditions.elementToBeClickable(submitBtn));
            wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(submitBtn, "Calculating route...")));

            if(Objects.equals(pickup.getAttribute("value"), FavoriteRoutesPage.startAddressOrdered)
                    && Objects.equals(dropoff.getAttribute("value"),FavoriteRoutesPage.endAddressOrdered)){
                if(FavoriteRoutesPage.waypointsOrdered.isEmpty())
                    return true;
                for(WebElement waypointField : waypointFields){
                    String value = waypointField.getAttribute("value");
                    if(!FavoriteRoutesPage.waypointsOrdered.contains(value)){
                        return false;
                    }
                }
            }
            return true;
        }catch (TimeoutException t){
            return false;
        }
    }

/// /////////////////////////////////////////////////////////////////////////////

//    public void fillOrderFormAndOrder(){
//        passengersInput.sendKeys(Keys.CONTROL+"a");
//        passengersInput.sendKeys(Keys.DELETE);
//        passengersInput.sendKeys("2");
//        nowCheckBox.click();
//        Select vehicleTypeSelect=new Select(vehicleType);
//        vehicleTypeSelect.selectByValue("STANDARD");
//        wait.until(ExpectedConditions.elementToBeClickable(submitBtn));
//
//        wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(submitBtn, "Calculating route...")));
//
//        submitBtn.click();
//    }
//
//    public boolean isOrdered(){
//        try {
//            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
//            if (alert.getText().contains("Ride created successfully")) {
//                alert.accept();
//                return true;
//            } else {
//                return false;
//            }
//        }catch (TimeoutException e){
//            return false;
//        }
//    }
//
//    public void fillOrderFormAndOrderNoFreeDriver(){
//        passengersInput.sendKeys(Keys.CONTROL+"a");
//        passengersInput.sendKeys(Keys.DELETE);
//        passengersInput.sendKeys("2");
//        nowCheckBox.click();
//        Select vehicleTypeSelect=new Select(vehicleType);
//        vehicleTypeSelect.selectByValue("STANDARD");
//        babySeatCB.click();
//
//        wait.until(ExpectedConditions.elementToBeClickable(submitBtn));
//
//        wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(submitBtn, "Calculating route...")));
//
//        submitBtn.click();
//    }
//
//    public boolean isOrderedNoDriver(){
//        try {
//            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
//            if (alert.getText().contains("Failed to create ride. There is no free drivers.")) {
//                alert.accept();
//                return true;
//            } else {
//                return false;
//            }
//        }catch (TimeoutException e){
//            return false;
//        }
//    }
//
//    public void fillOrderFormAndOrderScheduled() {
//        passengersInput.sendKeys(Keys.CONTROL + "a");
//        passengersInput.sendKeys(Keys.DELETE);
//        passengersInput.sendKeys("2");
//        LocalDateTime scheduleTime = LocalDateTime.now().plusHours(2);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
//
//        ((JavascriptExecutor) driver).executeScript("arguments[0].value='" + scheduleTime.format(formatter) + "';", datetime);
//        ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('input', {bubbles:true}));", datetime);
//        ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('change', {bubbles:true}));", datetime);
//
//        Select vehicleTypeSelect = new Select(vehicleType);
//        vehicleTypeSelect.selectByValue("STANDARD");
//        wait.until(ExpectedConditions.elementToBeClickable(submitBtn));
//
//        wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(submitBtn, "Calculating route...")));
//
//        submitBtn.click();
//    }
//
//    public void fillOrderFormAndOrderScheduled_OverLapTime() {
//        passengersInput.sendKeys(Keys.CONTROL + "a");
//        passengersInput.sendKeys(Keys.DELETE);
//        passengersInput.sendKeys("2");
//        LocalDateTime scheduledTime=LocalDateTime.now().plusDays(1).withHour(17).withMinute(30);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
//
//        ((JavascriptExecutor) driver).executeScript("arguments[0].value='" + scheduledTime.format(formatter) + "';", datetime);
//        ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('input', {bubbles:true}));", datetime);
//        ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('change', {bubbles:true}));", datetime);
//
//        Select vehicleTypeSelect = new Select(vehicleType);
//        vehicleTypeSelect.selectByValue("STANDARD");
//        wait.until(ExpectedConditions.elementToBeClickable(submitBtn));
//
//        wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(submitBtn, "Calculating route...")));
//
//        submitBtn.click();
//    }
//
//    public void fillOrderFormAndOrderScheduled_InvalidDateTime() {
//        passengersInput.sendKeys(Keys.CONTROL + "a");
//        passengersInput.sendKeys(Keys.DELETE);
//        passengersInput.sendKeys("2");
//        LocalDateTime scheduleTime = LocalDateTime.now().minusHours(2);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
//
//        ((JavascriptExecutor) driver).executeScript("arguments[0].value='" + scheduleTime.format(formatter) + "';", datetime);
//        ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('input', {bubbles:true}));", datetime);
//        ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('change', {bubbles:true}));", datetime);
//
//        Select vehicleTypeSelect = new Select(vehicleType);
//        vehicleTypeSelect.selectByValue("STANDARD");
//            wait.until(ExpectedConditions.elementToBeClickable(submitBtn));
//            wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(submitBtn, "Calculating route...")));
//            submitBtn.click();
//
//    }
//
//    public boolean invalidForm(){
//        try {
//            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
//            if (alert.getText().contains("Form is not valid!")) {
//                alert.accept();
//                return true;
//            } else {
//                return false;
//            }
//        }catch (TimeoutException e){
//            return false;
//        }
//    }
//
//    public void fillOrderFormAndOrderScheduled_InvalidPassengerNumber() {
//        passengersInput.sendKeys(Keys.CONTROL + "a");
//        passengersInput.sendKeys(Keys.DELETE);
//        passengersInput.sendKeys("0");
//        LocalDateTime scheduleTime = LocalDateTime.now().plusDays(5);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
//
//        ((JavascriptExecutor) driver).executeScript("arguments[0].value='" + scheduleTime.format(formatter) + "';", datetime);
//        ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('input', {bubbles:true}));", datetime);
//        ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('change', {bubbles:true}));", datetime);
//
//        Select vehicleTypeSelect = new Select(vehicleType);
//        vehicleTypeSelect.selectByValue("STANDARD");
//        wait.until(ExpectedConditions.elementToBeClickable(submitBtn));
//        wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(submitBtn, "Calculating route...")));
//        submitBtn.click();
//
//    }
//
//    public void fillOrderFormAndOrderScheduled_InvalidVehicleType() {
//        passengersInput.sendKeys(Keys.CONTROL + "a");
//        passengersInput.sendKeys(Keys.DELETE);
//        passengersInput.sendKeys("0");
//        LocalDateTime scheduleTime = LocalDateTime.now().plusDays(5);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
//
//        ((JavascriptExecutor) driver).executeScript("arguments[0].value='" + scheduleTime.format(formatter) + "';", datetime);
//        ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('input', {bubbles:true}));", datetime);
//        ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('change', {bubbles:true}));", datetime);
//
//        wait.until(ExpectedConditions.elementToBeClickable(submitBtn));
//        wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(submitBtn, "Calculating route...")));
//        submitBtn.click();
//
//    }

}
