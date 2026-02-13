package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class AdminHistoryPage {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String PAGE_URL = "http://localhost:4200/admin/admin-history";

    // MESSAGES
    @FindBy(how = How.CSS, using = ".no-rides-message")
    private WebElement noRidesMessage;

    // FILTERS
    @FindBy(how = How.ID, using = "username")
    private WebElement usernameInput;

    @FindBy(how = How.ID, using = "startDate")
    private WebElement startDateInput;

    @FindBy(how = How.ID, using = "endDate")
    private WebElement endDateInput;

    @FindBy(how = How.CSS, using = ".filter-section button")
    private WebElement searchButton;

    // Username suggestions locator
    private final By suggestionsLocator = By.cssSelector(".suggestions li");

    // TABLE
    // Sortable headers
    @FindBy(how = How.XPATH, using = "//th[contains(.,'Ride ID')]")
    private WebElement rideIdHeader;

    @FindBy(how = How.XPATH, using = "//th[contains(.,'Start Time')]")
    private WebElement startTimeHeader;

    @FindBy(how = How.XPATH, using = "//th[contains(.,'End Time')]")
    private WebElement endTimeHeader;

    @FindBy(how = How.XPATH, using = "//th[contains(.,'Start Address')]")
    private WebElement startAddressHeader;

    @FindBy(how = How.XPATH, using = "//th[contains(.,'End Address')]")
    private WebElement endAddressHeader;

    @FindBy(how = How.XPATH, using = "//th[contains(.,'Status')]")
    private WebElement statusHeader;

    // table body
    @FindBy(how = How.CSS, using = ".rides-table tbody tr")
    private List<WebElement> rideRows;

    @FindBy(how = How.CSS, using = ".table-container")
    private List<WebElement> tableContainer;

    // PAGINATION
    @FindBy(how = How.XPATH, using = "//div[contains(@class,'pagination')]/button[text()='Previous']")
    private WebElement prevPageButton;

    @FindBy(how = How.XPATH, using = "//div[contains(@class,'pagination')]/button[text()='Next']")
    private WebElement nextPageButton;

    @FindBy(how = How.CSS, using = ".pagination span")
    private WebElement pageInfo;

    // POPUP LOCATORS
    @FindBy(how = How.CSS, using = ".popup-overlay")
    private WebElement popupOverlay;

    @FindBy(how = How.CSS, using = ".popup-message")
    private WebElement popupMessage;

    @FindBy(how = How.CSS, using = ".popup-button")
    private WebElement popupCloseButton;

    public AdminHistoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 3);

        driver.get(PAGE_URL);
        PageFactory.initElements(driver, this);

        wait.until(ExpectedConditions.visibilityOf(usernameInput));
    }

    // FILTER ACTIONS
    public String getUsernameInputValue() {
        return usernameInput.getAttribute("value");
    }

    public void enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOf(usernameInput)).clear();
        usernameInput.sendKeys(username);
    }

    public void clearUsername() {
        wait.until(ExpectedConditions.visibilityOf(usernameInput)).clear();
    }

    public void waitForSuggestionsToLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(suggestionsLocator));
    }

    public void waitForSuggestionsCount(int expectedCount) {
        wait.until(driver ->
                driver.findElements(suggestionsLocator).size() == expectedCount
        );
    }

    public void waitForSuggestionsToDisappear() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(suggestionsLocator));
    }

    public List<WebElement> getUsernameSuggestions() {
        return driver.findElements(suggestionsLocator);
    }

    public void selectUsernameSuggestion(String suggestion) {
        waitForSuggestionsToLoad();
        for (WebElement el : getUsernameSuggestions()) {
            if (el.getText().equals(suggestion)) {
                el.click();
                break;
            }
        }
    }

    public void enterStartDate(String date) {
        wait.until(ExpectedConditions.visibilityOf(startDateInput)).clear();
        startDateInput.sendKeys(date);
    }

    public void enterEndDate(String date) {
        wait.until(ExpectedConditions.visibilityOf(endDateInput)).clear();
        endDateInput.sendKeys(date);
    }

    public void clickSearch() {
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
    }

    // TABLE
    public void sortBy(String column) {
        WebElement header;

        switch (column) {
            case "id": header = rideIdHeader; break;
            case "startTime": header = startTimeHeader; break;
            case "endTime": header = endTimeHeader; break;
            case "startAddress": header = startAddressHeader; break;
            case "endAddress": header = endAddressHeader; break;
            case "status": header = statusHeader; break;
            default: throw new IllegalArgumentException("Invalid column: " + column);
        }

        wait.until(ExpectedConditions.elementToBeClickable(header)).click();
        waitForTableToLoad();
    }

    public List<String> getColumnValues(int columnIndex) {
        List<WebElement> rows = driver.findElements(
                By.cssSelector(".rides-table tbody tr"));

        return rows.stream()
                .map(row -> row.findElements(By.tagName("td")).get(columnIndex).getText())
                .toList();
    }

    public boolean isInitialMessageDisplayed() {
        return wait.until(ExpectedConditions.visibilityOf(noRidesMessage)).isDisplayed();
    }

    public void waitForTableToLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".rides-table")));
    }

    public boolean isTableDisplayed() {
        return isElementVisible(By.cssSelector(".table-container"));
    }

    public List<WebElement> getRideRows() {
        return driver.findElements(By.cssSelector(".rides-table tbody tr"));
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
        WebElement detailsRow = row.findElement(
                By.xpath("following-sibling::tr[contains(@class,'details-row')]"));
        return detailsRow.isDisplayed();
    }

    // PAGINATION
    public boolean isNextEnabled() {
        return nextPageButton.isEnabled();
    }

    public boolean isPrevEnabled() {
        return prevPageButton.isEnabled();
    }

    public String getCurrentPageInfo() {
        return pageInfo.getText();
    }

    public void waitForTableRefresh() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".rides-table")));
    }

    public boolean isPaginationDisplayed() {
        return isElementVisible(By.cssSelector(".pagination"));
    }

    public void clickNextPage() {
        wait.until(ExpectedConditions.elementToBeClickable(nextPageButton)).click();
    }

    public void clickPrevPage() {
        wait.until(ExpectedConditions.elementToBeClickable(prevPageButton)).click();
    }

    public String getPageInfo() {
        return wait.until(ExpectedConditions.visibilityOf(pageInfo)).getText();
    }

    // POPUP ACTIONS
    public boolean isPopupVisible() {
        return isElementVisible(By.cssSelector(".popup-overlay"));
    }

    public String getPopupMessage() {
        return wait.until(ExpectedConditions.visibilityOf(popupMessage)).getText();
    }

    public void closePopup() {
        wait.until(ExpectedConditions.elementToBeClickable(popupCloseButton)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".popup-overlay")));
    }

    public boolean isNoRidesMessageDisplayed() {
        try {
            WebElement msg = wait.until(ExpectedConditions.visibilityOf(noRidesMessage));
            return msg.isDisplayed() && msg.getText().contains("No rides found");
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getNoRidesText() {
        return wait.until(ExpectedConditions.visibilityOf(noRidesMessage)).getText();
    }

    // HELPERS
    private boolean isElementVisible(By locator) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
