package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class AdminHistoryPage {
    private WebDriverWait wait;

    private static String PAGE_URL = "http://localhost:4200/admin/admin-history";

    // FILTERS
    @FindBy(how = How.ID, using = "username")
    private WebElement usernameInput;

    @FindBy(how = How.ID, using = "startDate")
    private WebElement startDateInput;

    @FindBy(how = How.ID, using = "endDate")
    private WebElement endDateInput;

    @FindBy(how = How.CSS, using = ".filter-section button")
    private WebElement searchButton;

    // Username suggestions list
    @FindBy(how = How.CSS, using = ".suggestions li")
    private List<WebElement> usernameSuggestions;

    // TABLE
    @FindBy(how = How.CSS, using = ".rides-table tbody tr")
    private List<WebElement> rideRows;

    // PAGINATION
    @FindBy(how = How.XPATH, using = "//div[contains(@class,'pagination')]/button[text()='Previous']")
    private WebElement prevPageButton;

    @FindBy(how = How.XPATH, using = "//div[contains(@class,'pagination')]/button[text()='Next']")
    private WebElement nextPageButton;

    @FindBy(how = How.CSS, using = ".pagination span")
    private WebElement pageInfo;

    public AdminHistoryPage(WebDriver driver) {
        this.wait = new WebDriverWait(driver, 5);
        driver.get(PAGE_URL);
        PageFactory.initElements(driver, this);

        // Wait for username input to appear as page is ready
        wait.until(ExpectedConditions.visibilityOf(usernameInput));
    }

    // FILTER ACTIONS
    public void enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOf(usernameInput)).clear();
        usernameInput.sendKeys(username);
    }

    public void selectUsernameSuggestion(String suggestion) {
        wait.until(ExpectedConditions.visibilityOfAllElements(usernameSuggestions));
        for (WebElement el : usernameSuggestions) {
            if (el.getText().equals(suggestion)) {
                el.click();
                break;
            }
        }
    }

    public void enterStartDate(String date) {
        startDateInput.clear();
        startDateInput.sendKeys(date);
    }

    public void enterEndDate(String date) {
        endDateInput.clear();
        endDateInput.sendKeys(date);
    }

    public void clickSearch() {
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
    }

    // TABLE INTERACTIONS
    public List<WebElement> getRideRows() {
        return wait.until(ExpectedConditions.visibilityOfAllElements(rideRows));
    }

    public WebElement getRideRowByIndex(int index) {
        return getRideRows().get(index);
    }

    public void toggleRideDetails(int index) {
        WebElement row = getRideRowByIndex(index);
        WebElement detailsButton = row.findElement(By.cssSelector(".details-btn"));
        wait.until(ExpectedConditions.elementToBeClickable(detailsButton)).click();
    }

    public boolean isRideDetailsVisible(int index) {
        WebElement row = getRideRowByIndex(index);
        WebElement detailsRow = row.findElement(By.xpath("following-sibling::tr[contains(@class,'details-row')]"));
        return detailsRow.isDisplayed();
    }

    // PAGINATION ACTIONS
    public void clickNextPage() {
        wait.until(ExpectedConditions.elementToBeClickable(nextPageButton)).click();
    }

    public void clickPrevPage() {
        wait.until(ExpectedConditions.elementToBeClickable(prevPageButton)).click();
    }

    public String getPageInfo() {
        return pageInfo.getText();
    }
}
