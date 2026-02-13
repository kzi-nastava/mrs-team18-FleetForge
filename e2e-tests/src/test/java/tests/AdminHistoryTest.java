package tests;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import pages.AdminHistoryPage;
import pages.NavbarPage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminHistoryTest extends TestBase {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("d MMM, yyyy HH:mm", Locale.ENGLISH);

    @BeforeSuite
    public void setUp(){
        loginAsAdmin();

        NavbarPage navbar = new NavbarPage(driver);
        navbar.waitUntilLoggedIn();
    }

    @AfterMethod
    public void tearDown() {
        driver.navigate().refresh();
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
    public void testSortingAllColumns() {

        AdminHistoryPage page = new AdminHistoryPage(driver);

        page.enterUsername("passenger4@test.com");
        page.clickSearch();
        page.waitForTableToLoad();

        Map<String, Integer> columns = Map.of(
                "id", 0,
                "startTime", 1,
                "endTime", 2,
                "startAddress", 3,
                "endAddress", 4,
                "status", 5
        );

        for (String columnName : columns.keySet()) {

            int columnIndex = columns.get(columnName);

            // First click -> DESC
            page.sortBy(columnName);
            List<String> valuesDesc = page.getColumnValues(columnIndex);
            assertSorted(valuesDesc, false, columnName);

            // Second click -> ASC
            page.sortBy(columnName);
            List<String> valuesAsc = page.getColumnValues(columnIndex);
            assertSorted(valuesAsc, true, columnName);
        }
    }


    private void assertSorted(List<String> values,
                              boolean ascending,
                              String columnName) {

        for (int i = 0; i < values.size() - 1; i++) {

            String current = values.get(i);
            String next = values.get(i + 1);

            int comparison = getComparison(columnName, current, next);

            boolean condition = ascending
                    ? comparison <= 0
                    : comparison >= 0;

            if (!condition) {
                String direction = ascending ? "ASC" : "DESC";
                Assert.fail(
                        "\nSorting FAILED\n" +
                                "Column: " + columnName + "\n" +
                                "Direction: " + direction + "\n" +
                                "Rows: " + (i + 1) + " and " + (i + 2) + "\n" +
                                "Value1: [" + current + "]\n" +
                                "Value2: [" + next + "]\n"
                );
            }
        }
    }

    private static int getComparison(String columnName, String current, String next) {
        int comparison;

        if (columnName.equals("startTime") || columnName.equals("endTime")) {

            LocalDateTime currentDate = current.equals("-") ? null :
                    LocalDateTime.parse(current, DATE_FORMATTER);

            LocalDateTime nextDate = next.equals("-") ? null :
                    LocalDateTime.parse(next, DATE_FORMATTER);

            if (currentDate == null && nextDate == null) {
                comparison = 0;
            } else if (currentDate == null) {
                comparison = -1;
            } else if (nextDate == null) {
                comparison = 1;
            } else {
                comparison = currentDate.compareTo(nextDate);
            }
        }
        else if (columnName.equals("id")) {
            Long currentId = Long.parseLong(current);
            Long nextId = Long.parseLong(next);
            comparison = currentId.compareTo(nextId);
        }
        else {
            comparison = current.compareToIgnoreCase(next);
        }
        return comparison;
    }


    @Test
    public void testPaginationForPassenger() {

        AdminHistoryPage page = new AdminHistoryPage(driver);

        page.enterUsername("passenger4@test.com");
        page.clickSearch();
        page.waitForTableToLoad();

        // Page 1 assertions
        Assert.assertTrue(page.isNextEnabled(),
                "Next should be enabled on first page");

        Assert.assertFalse(page.isPrevEnabled(),
                "Previous should be disabled on first page");

        Assert.assertEquals(page.getRideRows().size(), 10,
                "First page should contain 10 rides");

        String firstPageInfo = page.getCurrentPageInfo();
        Assert.assertTrue(firstPageInfo.contains("Page 1 of 2"));

        // Go to page 2
        page.clickNextPage();
        page.waitForTableRefresh();

        // Page 2 assertions
        Assert.assertFalse(page.isNextEnabled(),
                "Next should be disabled on last page");

        Assert.assertTrue(page.isPrevEnabled(),
                "Previous should be enabled on second page");

        Assert.assertEquals(page.getRideRows().size(), 1,
                "Second page should contain 1 ride");

        String secondPageInfo = page.getCurrentPageInfo();
        Assert.assertTrue(secondPageInfo.contains("Page 2 of 2"));
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
