package tests;

import org.junit.Assert;
import org.testng.annotations.Test;
import pages.FavoriteRoutesPage;
import pages.PassengerHomePage;
import pages.RideHistoryPage;

public class FavoriteRoutesOrderTest extends TestBase{

    @Test
    private void addToFavoriteRoutes(){
        loginAsPassenger();
        PassengerHomePage passengerHomePage=new PassengerHomePage(driver);

        Assert.assertTrue(passengerHomePage.isLoaded());

        passengerHomePage.goToPassengerHistory();

        RideHistoryPage rideHistoryPage= new RideHistoryPage(driver);

        Assert.assertTrue(rideHistoryPage.rideHistoryLoaded());
        Assert.assertTrue(rideHistoryPage.addRouteToFavorites());
        rideHistoryPage.goToFavoriteRoutes();

        FavoriteRoutesPage favoriteRoutesPage= new FavoriteRoutesPage(driver);
        Assert.assertTrue(favoriteRoutesPage.favoriteRoutesPageLoaded());
        Assert.assertTrue(favoriteRoutesPage.favoriteRouteAdded());
    }

    @Test
    private void removeFromFavoritesHistoryPage(){
        addToFavoriteRoutes();
        PassengerHomePage passengerHomePage=new PassengerHomePage(driver);

        Assert.assertTrue(passengerHomePage.isLoaded());

        passengerHomePage.goToPassengerHistory();

        RideHistoryPage rideHistoryPage= new RideHistoryPage(driver);

        Assert.assertTrue(rideHistoryPage.rideHistoryLoaded());
        Assert.assertTrue(rideHistoryPage.addRouteToFavorites());

        Assert.assertTrue(rideHistoryPage.removeFromFavorites());
    }
    @Test
    private void removeFromFavorites_FavoritesPage(){
        addToFavoriteRoutes();
        PassengerHomePage passengerHomePage=new PassengerHomePage(driver);

        Assert.assertTrue(passengerHomePage.isLoaded());

        passengerHomePage.goToFavoritesPage();
        FavoriteRoutesPage favoriteRoutesPage= new FavoriteRoutesPage(driver);
        Assert.assertTrue(favoriteRoutesPage.favoriteRoutesPageLoaded());

        Assert.assertTrue(favoriteRoutesPage.removeFavoriteRoute());
    }

    @Test
    private void addToFavoritesAndOrder(){
        loginAsPassenger();
        PassengerHomePage passengerHomePage=new PassengerHomePage(driver);

        Assert.assertTrue(passengerHomePage.isLoaded());

        passengerHomePage.goToPassengerHistory();

        RideHistoryPage rideHistoryPage= new RideHistoryPage(driver);

        Assert.assertTrue(rideHistoryPage.rideHistoryLoaded());
        Assert.assertTrue(rideHistoryPage.addRouteToFavorites());

        rideHistoryPage.goToFavoriteRoutes();

        FavoriteRoutesPage favoriteRoutesPage= new FavoriteRoutesPage(driver);
        Assert.assertTrue(favoriteRoutesPage.favoriteRoutesPageLoaded());
        Assert.assertTrue(favoriteRoutesPage.favoriteRouteAdded());
        favoriteRoutesPage.orderRouteAgain();

        Assert.assertTrue(passengerHomePage.isLoaded());

        Assert.assertTrue(passengerHomePage.areAllCoordinatesFilled());
    }

/////////////////////////////////// ne znam da l je trebalo al eto ima visak ako nista //////////////////////////////////////////////


//    @Test
//    private void favoriteRideOrder_Now_AddFromHistory(){
//        loginAsPassenger();
//        PassengerHomePage passengerHomePage=new PassengerHomePage(driver);
//
//        Assert.assertTrue(passengerHomePage.isLoaded());
//
//        passengerHomePage.goToPassengerHistory();
//
//        RideHistoryPage rideHistoryPage= new RideHistoryPage(driver);
//
//        Assert.assertTrue(rideHistoryPage.rideHistoryLoaded());
//        Assert.assertTrue(rideHistoryPage.addRouteToFavorites());
//
//        rideHistoryPage.goToFavoriteRoutes();
//
//        FavoriteRoutesPage favoriteRoutesPage= new FavoriteRoutesPage(driver);
//        Assert.assertTrue(favoriteRoutesPage.favoriteRoutesPageLoaded());
//        Assert.assertTrue(favoriteRoutesPage.favoriteRouteAdded());
//        favoriteRoutesPage.orderRouteAgain();
//
//        Assert.assertTrue(passengerHomePage.isLoaded());
//
//        passengerHomePage.fillOrderFormAndOrder();
//        Assert.assertTrue(passengerHomePage.isOrdered());
//
//    }
//
//
//    @Test
//    private void favoriteRideOrder_Now_AddFromHistory_NoFreeDrivers(){
//        loginAsPassenger();
//        PassengerHomePage passengerHomePage=new PassengerHomePage(driver);
//
//        Assert.assertTrue(passengerHomePage.isLoaded());
//
//        passengerHomePage.goToPassengerHistory();
//
//        RideHistoryPage rideHistoryPage= new RideHistoryPage(driver);
//
//        Assert.assertTrue(rideHistoryPage.rideHistoryLoaded());
//        Assert.assertTrue(rideHistoryPage.addRouteToFavorites());
//
//        rideHistoryPage.goToFavoriteRoutes();
//
//        FavoriteRoutesPage favoriteRoutesPage= new FavoriteRoutesPage(driver);
//        Assert.assertTrue(favoriteRoutesPage.favoriteRoutesPageLoaded());
//        Assert.assertTrue(favoriteRoutesPage.favoriteRouteAdded());
//        favoriteRoutesPage.orderRouteAgain();
//
//        Assert.assertTrue(passengerHomePage.isLoaded());
//
//        passengerHomePage.fillOrderFormAndOrderNoFreeDriver();
//        Assert.assertTrue(passengerHomePage.isOrderedNoDriver());
//    }
//
//    @Test
//    private void favoriteRideOrder_Scheduled_AddFromHistory(){
//        loginAsPassenger();
//        PassengerHomePage passengerHomePage=new PassengerHomePage(driver);
//
//        Assert.assertTrue(passengerHomePage.isLoaded());
//
//        passengerHomePage.goToPassengerHistory();
//
//        RideHistoryPage rideHistoryPage= new RideHistoryPage(driver);
//
//        Assert.assertTrue(rideHistoryPage.rideHistoryLoaded());
//        Assert.assertTrue(rideHistoryPage.addRouteToFavorites());
//
//        rideHistoryPage.goToFavoriteRoutes();
//
//        FavoriteRoutesPage favoriteRoutesPage= new FavoriteRoutesPage(driver);
//        Assert.assertTrue(favoriteRoutesPage.favoriteRoutesPageLoaded());
//        Assert.assertTrue(favoriteRoutesPage.favoriteRouteAdded());
//        favoriteRoutesPage.orderRouteAgain();
//
//        Assert.assertTrue(passengerHomePage.isLoaded());
//
//        passengerHomePage.fillOrderFormAndOrderScheduled();
//        Assert.assertTrue(passengerHomePage.isOrdered());
//
//    }
//
//    @Test
//    private void favoriteRideOrder_Scheduled_AddFromHistory_OverLapInScheduledTime(){
//        loginAsPassenger();
//        PassengerHomePage passengerHomePage=new PassengerHomePage(driver);
//
//        Assert.assertTrue(passengerHomePage.isLoaded());
//
//        passengerHomePage.goToPassengerHistory();
//
//        RideHistoryPage rideHistoryPage= new RideHistoryPage(driver);
//
//        Assert.assertTrue(rideHistoryPage.rideHistoryLoaded());
//        Assert.assertTrue(rideHistoryPage.addRouteToFavorites());
//
//        rideHistoryPage.goToFavoriteRoutes();
//
//        FavoriteRoutesPage favoriteRoutesPage= new FavoriteRoutesPage(driver);
//        Assert.assertTrue(favoriteRoutesPage.favoriteRoutesPageLoaded());
//        Assert.assertTrue(favoriteRoutesPage.favoriteRouteAdded());
//        favoriteRoutesPage.orderRouteAgain();
//
//        Assert.assertTrue(passengerHomePage.isLoaded());
//
//        passengerHomePage.fillOrderFormAndOrderScheduled_OverLapTime();
//        Assert.assertTrue(passengerHomePage.isOrdered());
////drugi put porucuje isto
//
//        PassengerHomePage passengerHomePage2=new PassengerHomePage(driver);
//
//        Assert.assertTrue(passengerHomePage2.isLoaded());
//
//        passengerHomePage2.goToPassengerHistory();
//
//        RideHistoryPage rideHistoryPage2= new RideHistoryPage(driver);
//
//        Assert.assertTrue(rideHistoryPage2.rideHistoryLoaded());
//        Assert.assertTrue(rideHistoryPage2.addRouteToFavorites());
//
//        rideHistoryPage2.goToFavoriteRoutes();
//
//        FavoriteRoutesPage favoriteRoutesPage2= new FavoriteRoutesPage(driver);
//        Assert.assertTrue(favoriteRoutesPage2.favoriteRoutesPageLoaded());
//        Assert.assertTrue(favoriteRoutesPage2.favoriteRouteAdded());
//        favoriteRoutesPage2.orderRouteAgain();
//
//        Assert.assertTrue(passengerHomePage2.isLoaded());
//
//        passengerHomePage2.fillOrderFormAndOrderScheduled_OverLapTime();
//        Assert.assertTrue(passengerHomePage2.isOrderedNoDriver());
//
//    }
//
//    @Test
//    private void favoriteRideOrder_InvalidDate(){
//        loginAsPassenger();
//        PassengerHomePage passengerHomePage=new PassengerHomePage(driver);
//
//        Assert.assertTrue(passengerHomePage.isLoaded());
//
//        passengerHomePage.goToPassengerHistory();
//
//        RideHistoryPage rideHistoryPage= new RideHistoryPage(driver);
//
//        Assert.assertTrue(rideHistoryPage.rideHistoryLoaded());
//        Assert.assertTrue(rideHistoryPage.addRouteToFavorites());
//
//        rideHistoryPage.goToFavoriteRoutes();
//
//        FavoriteRoutesPage favoriteRoutesPage= new FavoriteRoutesPage(driver);
//        Assert.assertTrue(favoriteRoutesPage.favoriteRoutesPageLoaded());
//        Assert.assertTrue(favoriteRoutesPage.favoriteRouteAdded());
//        favoriteRoutesPage.orderRouteAgain();
//
//        Assert.assertTrue(passengerHomePage.isLoaded());
//
//        passengerHomePage.fillOrderFormAndOrderScheduled_InvalidDateTime();
//        Assert.assertTrue(passengerHomePage.invalidForm());
//    }
//
//    @Test
//    private void favoriteRideOrder_InvalidPassengerNumber(){
//        loginAsPassenger();
//        PassengerHomePage passengerHomePage=new PassengerHomePage(driver);
//
//        Assert.assertTrue(passengerHomePage.isLoaded());
//
//        passengerHomePage.goToPassengerHistory();
//
//        RideHistoryPage rideHistoryPage= new RideHistoryPage(driver);
//
//        Assert.assertTrue(rideHistoryPage.rideHistoryLoaded());
//        Assert.assertTrue(rideHistoryPage.addRouteToFavorites());
//
//        rideHistoryPage.goToFavoriteRoutes();
//
//        FavoriteRoutesPage favoriteRoutesPage= new FavoriteRoutesPage(driver);
//        Assert.assertTrue(favoriteRoutesPage.favoriteRoutesPageLoaded());
//        Assert.assertTrue(favoriteRoutesPage.favoriteRouteAdded());
//        favoriteRoutesPage.orderRouteAgain();
//
//        Assert.assertTrue(passengerHomePage.isLoaded());
//
//        passengerHomePage.fillOrderFormAndOrderScheduled_InvalidPassengerNumber();
//        Assert.assertTrue(passengerHomePage.invalidForm());
//    }
//
//    @Test
//    private void favoriteRideOrder_InvalidVehicleType(){
//        loginAsPassenger();
//        PassengerHomePage passengerHomePage=new PassengerHomePage(driver);
//
//        Assert.assertTrue(passengerHomePage.isLoaded());
//
//        passengerHomePage.goToPassengerHistory();
//
//        RideHistoryPage rideHistoryPage= new RideHistoryPage(driver);
//
//        Assert.assertTrue(rideHistoryPage.rideHistoryLoaded());
//        Assert.assertTrue(rideHistoryPage.addRouteToFavorites());
//
//        rideHistoryPage.goToFavoriteRoutes();
//
//        FavoriteRoutesPage favoriteRoutesPage= new FavoriteRoutesPage(driver);
//        Assert.assertTrue(favoriteRoutesPage.favoriteRoutesPageLoaded());
//        Assert.assertTrue(favoriteRoutesPage.favoriteRouteAdded());
//        favoriteRoutesPage.orderRouteAgain();
//
//        Assert.assertTrue(passengerHomePage.isLoaded());
//
//        passengerHomePage.fillOrderFormAndOrderScheduled_InvalidVehicleType();
//        Assert.assertTrue(passengerHomePage.invalidForm());
//    }
}
