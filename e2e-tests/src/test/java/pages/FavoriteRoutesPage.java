package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class FavoriteRoutesPage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(xpath = "//table//button[normalize-space(text())='Ride again']")
    private List<WebElement> rideAgainBtns;
    @FindBy(tagName = "h2")
    private WebElement title;

    @FindBy(xpath = "//table//button[normalize-space(text())='Remove']")
    private WebElement removeBtns;

    @FindBy(xpath = "//table[//div[text()='Home->Gym']]//div[contains(@class,'first-address')]")
    private WebElement firstAddress;
    @FindBy(xpath = "//table[//div[text()='Home->Gym']]//div[contains(@class,'last-address')]")
    private WebElement lastAddress;
    @FindBy(xpath = "//table[//div[text()='Home->Gym']]//div[contains(@class,'waypoint-chip')]")
    private List<WebElement> waypoints=new ArrayList<>();

    public static String startAddressOrdered;
    public static String endAddressOrdered;
    public static List<String> waypointsOrdered=new ArrayList<>();
    public FavoriteRoutesPage(WebDriver driver){
        this.driver=driver;
        PageFactory.initElements(driver,this);
        wait= new WebDriverWait(driver,3);
    }

    public boolean favoriteRoutesPageLoaded()
    {
        try{
            wait.until(ExpectedConditions.textToBePresentInElement(title,"Favorite routes"));
            return true;
        }catch (TimeoutException e){
            return false;
        }
    }
    public boolean favoriteRouteAdded(){
        try {
            wait.until(visibilityOfElementLocated(By.xpath("//table//div[text()='Home->Gym']")));
            return true;
        }catch (TimeoutException e){
            return false;
        }
    }

    public void orderRouteAgain(){

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table//tr[.//div[text()='Home->Gym']]//button[normalize-space(text())='Ride again']")));
        WebElement rideAgain=driver.findElement(By.xpath("//table//tr[.//div[text()='Home->Gym']]//button[normalize-space(text())='Ride again']"));
        startAddressOrdered=firstAddress.getText();
        endAddressOrdered=lastAddress.getText();
        waypointsOrdered.clear();
        for(WebElement element:waypoints){
            String text = element.getText().trim().replaceAll("[^\\p{L}\\p{N}\\s,.]", "").trim();
            waypointsOrdered.add(text);
        }
        rideAgain.click();
    }
    public boolean removeFavoriteRoute(){
        try {
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//table//tr[.//div[text()='Home->Gym']]//button[normalize-space(text())='Remove']"))).click();
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.xpath("//table//div[text()='Home->Gym']")));
            return true;
        }
        catch (TimeoutException t){
            return false;
        }
    }

}
