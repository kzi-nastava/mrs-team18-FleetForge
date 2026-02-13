package tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import pages.LoginPage;
import pages.NavbarPage;

public class TestBase {

    protected WebDriver driver;

    protected static final String ADMIN_EMAIL = "admin@test.com";
    protected static final String ADMIN_PASSWORD = "123";

    protected static final String PASSENGER_EMAIL = "passenger1@test.com";
    protected static final String PASSENGER_PASSWORD = "123";

    @BeforeSuite
    public void initializeWebDriver() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    protected void loginAsAdmin() {
        driver.get("http://localhost:4200/login");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs(ADMIN_EMAIL, ADMIN_PASSWORD);
    }

    protected void loginAsPassenger() {
        driver.get("http://localhost:4200/login");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs(PASSENGER_EMAIL, PASSENGER_PASSWORD);
    }

    protected void logout() {
        NavbarPage navbar = new NavbarPage(driver);
        navbar.logout();
    }

    @AfterSuite
    public void quitDriver() {
        driver.quit();
    }
}
