package tests;

import org.junit.Assert;
import org.testng.annotations.Test;
import pages.PassengerHomePage;
import pages.PassengerRideHistoryPage;

public class RateRideTest extends TestBase {

    @Test
    public void rateRide(){
        loginAsPassenger();
        PassengerHomePage passengerHomePage = new PassengerHomePage(driver);
        Assert.assertTrue(passengerHomePage.isLoaded());

        passengerHomePage.goToPassengerHistory();

        PassengerRideHistoryPage passengerRideHistory = new PassengerRideHistoryPage(driver);
        Assert.assertTrue(passengerRideHistory.rideHistoryLoaded());

        passengerRideHistory.clickRateButton();
        Assert.assertTrue(passengerRideHistory.isRatingModalOpen());

        passengerRideHistory.rateDriver();
        passengerRideHistory.rateVehicle();
        passengerRideHistory.enterComment("Fine ride");
        Assert.assertTrue(passengerRideHistory.isSubmitEnabled());


        passengerRideHistory.submitRating();
        Assert.assertTrue(passengerRideHistory.isStarFieldDisplayed());

    }

    @Test
    public void rideAlreadyRated() {
        loginAsPassenger();
        PassengerHomePage passengerHomePage = new PassengerHomePage(driver);
        Assert.assertTrue(passengerHomePage.isLoaded());

        passengerHomePage.goToPassengerHistory();

        PassengerRideHistoryPage passengerRideHistory = new PassengerRideHistoryPage(driver);
        Assert.assertTrue(passengerRideHistory.rideHistoryLoaded());

        Assert.assertTrue(passengerRideHistory.isPreviousRideRated());
    }

    @Test
    public void ratingExpired() {
        loginAsPassenger();
        PassengerHomePage passengerHomePage = new PassengerHomePage(driver);
        Assert.assertTrue(passengerHomePage.isLoaded());

        passengerHomePage.goToPassengerHistory();

        PassengerRideHistoryPage passengerRideHistory = new PassengerRideHistoryPage(driver);
        Assert.assertTrue(passengerRideHistory.rideHistoryLoaded());

        Assert.assertTrue(passengerRideHistory.isRatingExpired());
    }
}
