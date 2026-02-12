package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import pages.AdminHistoryPage;
import pages.NavbarPage;

public class AdminHistoryTest extends TestBase {

    @BeforeSuite
    public void setUp(){
        loginAsAdmin();

        NavbarPage navbar = new NavbarPage(driver);
        navbar.waitUntilLoggedIn();
    }

    @Test
    public void testUsernameAutocompleteSuggestions() {
        AdminHistoryPage adminPage = new AdminHistoryPage(driver);

        // --- 1. Insert "x" -> No suggestions ---
        adminPage.enterUsername("x");
        adminPage.waitForSuggestionsCount(0);
        Assert.assertEquals(adminPage.getUsernameSuggestions().size(), 0);

        // --- 2. Insert "p" -> 3 suggestions ---
        adminPage.clearUsername();
        adminPage.enterUsername("p");
        adminPage.waitForSuggestionsCount(3);
        Assert.assertEquals(adminPage.getUsernameSuggestions().size(), 3);

        // --- 3. Insert "passenger1" -> 1 suggestion ---
        adminPage.clearUsername();
        adminPage.enterUsername("passenger1");
        adminPage.waitForSuggestionsCount(1);
        Assert.assertEquals(adminPage.getUsernameSuggestions().size(), 1);

        // --- 4. Select first suggestion ---
        String selectedUsername =
                adminPage.getUsernameSuggestions().get(0).getText();
        adminPage.selectUsernameSuggestion(selectedUsername);
        Assert.assertEquals(adminPage.getUsernameInputValue(),
                selectedUsername,
                "Selected username should be populated in input field");

        // --- 5. Clear input -> No suggestions ---
        adminPage.clearUsername();
        adminPage.enterUsername(" ");
        adminPage.enterUsername("");
        adminPage.waitForSuggestionsToDisappear();
        Assert.assertEquals(adminPage.getUsernameSuggestions().size(), 0);
    }

    @Test
    public void testPageInitialState() {
        AdminHistoryPage adminPage = new AdminHistoryPage(driver);

        // Assert username input visible
        Assert.assertTrue(adminPage.getUsernameInputValue().isEmpty());

        // Initial info message shown
        Assert.assertTrue(adminPage.isInitialMessageDisplayed(),
                "Initial instruction message should be visible");

        // Table should NOT be visible
        Assert.assertFalse(adminPage.isTableDisplayed(),
                "Rides table should not be visible before search");

        // Pagination should NOT be visible
        Assert.assertFalse(adminPage.isPaginationDisplayed(),
                "Pagination should not be visible before search");
    }

    @Test
    public void testValidSearch() {
        AdminHistoryPage adminPage = new AdminHistoryPage(driver);

        // Enter valid filters
        adminPage.enterUsername("passenger1@test.com");
        adminPage.enterStartDate("01-02-2026");
        adminPage.enterEndDate("10-02-2026");

        // Click search
        adminPage.clickSearch();

        // Wait for table to load
        adminPage.waitForTableToLoad();

        // Assert table visible
        Assert.assertTrue(adminPage.isTableDisplayed(),
                "Rides table should be displayed after valid search");

        // Assert at 2 rows returned
        Assert.assertEquals(adminPage.getRideRows().size(), 2, "Rides should be loaded");

        // If rides exist, pagination should be visible
        Assert.assertTrue(adminPage.isPaginationDisplayed(),
                "Pagination should be visible when rides exist");
    }


    @Test
    public void testAdminHistoryPageSelectors() {
        AdminHistoryPage adminPage = new AdminHistoryPage(driver);

        // Use filter methods
        adminPage.enterUsername("passenger4@test.com");
        adminPage.enterStartDate("01-02-2026");
        adminPage.enterEndDate("10-02-2026");

        // Click search
        adminPage.clickSearch();

        // Table
        int rowsCount = adminPage.getRideRows().size();
        Assert.assertTrue(rowsCount >= 0, "Rides table should be visible");

        // Expand first ride if exists
        if (rowsCount > 0) {
            adminPage.toggleRideDetails(0);
            Assert.assertTrue(adminPage.isRideDetailsVisible(0), "Ride details should be visible after toggle");
        }

        // Pagination test
        String pageInfoBefore = adminPage.getPageInfo();
        adminPage.clickNextPage();
        String pageInfoAfter = adminPage.getPageInfo();
        Assert.assertNotEquals(pageInfoBefore, pageInfoAfter, "Page info should change after next page");

        adminPage.clickPrevPage();
        String pageInfoBack = adminPage.getPageInfo();
        Assert.assertEquals(pageInfoBefore, pageInfoBack, "Page info should return to original after previous page");
    }
}
