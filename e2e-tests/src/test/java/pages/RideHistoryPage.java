package pages;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.Time;
import java.util.List;

public class RideHistoryPage {
    private WebDriver driver;
    private WebDriverWait wait;


    @FindBy(xpath = "//table//button[@title='Favorite']")
    private List<WebElement> favoritesBtns;

    @FindBy(id = "favoriteRouteName")
    private WebElement favoriteRouteName;

    @FindBy(id = "saveFavoriteRouteBtn")
    private WebElement saveFavoriteRouteBtn;

    @FindBy(id = "sidebarBtn")
    private WebElement sidebar;

    @FindBy(xpath = "//a[span[text()='Favourite Rides']]")
    private WebElement favoriteRoutesLink;

    @FindBy(tagName = "h2")
    private WebElement title;


    public RideHistoryPage(WebDriver driver){
        this.driver=driver;
        wait= new WebDriverWait(driver, 3);
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
    public boolean addRouteToFavorites(){
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(favoritesBtns));

            WebElement firstFavBtn = favoritesBtns.get(0);
            if(firstFavBtn.getAttribute("class").contains("favorited")){
                wait.until(ExpectedConditions.elementToBeClickable(firstFavBtn)).click(); // da se izbaci iz omiljenih prvo
                wait.until(ExpectedConditions.elementToBeClickable(firstFavBtn)).click();
            }else{
                wait.until(ExpectedConditions.elementToBeClickable(firstFavBtn)).click();
            }
            wait.until(ExpectedConditions.visibilityOf(favoriteRouteName));
            favoriteRouteName.sendKeys("Home->Gym");

            wait.until(ExpectedConditions.elementToBeClickable(saveFavoriteRouteBtn)).click();
            wait.until(ExpectedConditions.attributeContains(firstFavBtn, "class", "favorited"));
        }
         catch (TimeoutException e){
                return false;
            }
        return true;
    }

    public void goToFavoriteRoutes(){
        sidebar.click();
        wait.until(ExpectedConditions.elementToBeClickable(favoriteRoutesLink)).click();
    }

    public boolean removeFromFavorites(){
        try {
            wait.until(ExpectedConditions.visibilityOfAllElements(favoritesBtns));

            WebElement firstFavBtn = favoritesBtns.get(0);
            wait.until(ExpectedConditions.elementToBeClickable(firstFavBtn)).click();
            wait.until(ExpectedConditions.not(ExpectedConditions.attributeContains(firstFavBtn,"class","favorited")));
        }
        catch (TimeoutException e){
            return false;
        }
        return true;
    }
}
