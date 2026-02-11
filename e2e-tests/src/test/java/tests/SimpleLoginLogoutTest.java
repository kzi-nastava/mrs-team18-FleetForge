package tests;

import org.testng.annotations.Test;
import pages.NavbarPage;

public class SimpleLoginLogoutTest extends TestBase {

    @Test
    public void adminCanLoginAndLogout() {

        loginAsAdmin();

        NavbarPage navbar = new NavbarPage(driver);

        navbar.waitUntilLoggedIn();

        navbar.logout();

        navbar.waitUntilLoggedOut();
    }
}
