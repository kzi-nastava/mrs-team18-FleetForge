package com.team18.FleetForge.RideOrderTest.service;

import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.ride.GeoPoint;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.DriverSession;
import com.team18.FleetForge.model.vehicles.Vehicle;
import com.team18.FleetForge.repository.rides.RideRepository;
import com.team18.FleetForge.repository.users.DriverRepository;
import com.team18.FleetForge.repository.users.DriverSessionRepo;
import com.team18.FleetForge.service.impl.DriverServiceImpl;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;


import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.*;

public class DriverServiceTest {

    private DriverRepository driverRepository;
    private RideRepository rideRepository;
    private DriverSessionRepo driverSessionRepo;

    private DriverServiceImpl driverService;
    @BeforeMethod
    public void setup(){
        driverRepository=mock(DriverRepository.class);
        rideRepository=mock(RideRepository.class);
        driverSessionRepo=mock(DriverSessionRepo.class);
        driverService=new DriverServiceImpl(driverRepository,rideRepository,driverSessionRepo);
    }
    private Driver buildDriver(VehicleType type, boolean active, boolean available, double lat, double lon) {
        Driver driver = new Driver();
        driver.setActive(active);
        driver.setAvailable(available);
        Vehicle vehicle = new Vehicle();
        vehicle.setType(type);
        driver.setVehicle(vehicle);
        GeoPoint loc = new GeoPoint(lat, lon);
        driver.setCurrentLocation(loc);
        return driver;
    }

    private Ride buildRide(VehicleType type, double startLat, double startLon) {
        Ride ride = new Ride();
        ride.setVehicleType(type);
        GeoPoint startLocation = new GeoPoint(startLat, startLon);
        ride.setStartLocation(startLocation);
        ride.setStartTime(LocalDateTime.now());
        ride.setEstimatedDuration(20.0);
        return ride;
    }

    private DriverSession buildSession(LocalDateTime start, LocalDateTime end) {
        DriverSession session = new DriverSession();
        session.setStartedAt(start);
        session.setEndedAt(end);
        return session;
    }

    @Test
    public void findAvailableDriver_noActiveDrivers_returnsNull() {
        when(driverRepository.findByIsActiveTrue()).thenReturn(new ArrayList<>());

        Ride ride = buildRide(VehicleType.STANDARD, 45.0, 19.0);
        Driver result = driverService.findAvailableDriver(ride);

        assertNull(result);
        verifyNoInteractions(driverSessionRepo);
        verify(driverRepository).findByIsActiveTrue();
        verifyNoMoreInteractions(driverRepository);
        verifyNoInteractions(rideRepository);
    }
    @Test
    public void findAvailableDriver_availableDriverExists_returnsNearest() {
        Driver driver1 = buildDriver(VehicleType.STANDARD, true, true, 45.1, 19.1);
        Driver driver2 = buildDriver(VehicleType.STANDARD, true, true, 45.5, 19.5);

        ArrayList<Driver> driverList= new ArrayList<>();
        driverList.add(driver1);
        driverList.add(driver2);

        when(driverRepository.findByIsActiveTrue()).thenReturn(driverList);

        DriverSession session = buildSession(LocalDateTime.now().minusHours(2), LocalDateTime.now());
        ArrayList<DriverSession> sessions=new ArrayList<>();
        sessions.add(session);
        when(driverSessionRepo.findByDriver(any())).thenReturn(sessions);

        Ride ride = buildRide(VehicleType.STANDARD, 45.0, 19.0);
        Driver result = driverService.findAvailableDriver(ride);

        assertNotNull(result);
        assertEquals(result, driver1);
        verify(driverRepository).findByIsActiveTrue();
        verify(driverSessionRepo).findByDriver(driver1);
        verify(driverSessionRepo).findByDriver(driver2);
        verifyNoInteractions(rideRepository);
    }

    @Test
    public void findAvailableDriver_availableDriverWrongVehicleType_notSelected() {
        Driver driver = buildDriver(VehicleType.LUXURY, true, true, 45.1, 19.1);

        ArrayList<Driver> driverList= new ArrayList<>();
        driverList.add(driver);
        when(driverRepository.findByIsActiveTrue()).thenReturn(driverList);
        when(driverSessionRepo.findByDriver(any())).thenReturn(new ArrayList<>());

        Ride ride = buildRide(VehicleType.STANDARD, 45.0, 19.0);
        Driver result = driverService.findAvailableDriver(ride);

        assertNull(result);
        verify(driverRepository).findByIsActiveTrue();
        verifyNoMoreInteractions(driverRepository);
        verifyNoInteractions(driverSessionRepo);
        verifyNoInteractions(rideRepository);
    }

    @Test
    public void findAvailableDriver_availableDriverExceeds8hActivity_notSelected() {
        Driver driver = buildDriver(VehicleType.STANDARD, true, true, 45.1, 19.1);

        ArrayList<Driver> driverList= new ArrayList<>();
        driverList.add(driver);
        when(driverRepository.findByIsActiveTrue()).thenReturn(driverList);


        DriverSession session = buildSession(LocalDateTime.now().minusHours(9), LocalDateTime.now());
        ArrayList<DriverSession> sessions=new ArrayList<>();
        sessions.add(session);
        when(driverSessionRepo.findByDriver(driver)).thenReturn(sessions);

        Ride ride = buildRide(VehicleType.STANDARD, 45.0, 19.0);
        Driver result = driverService.findAvailableDriver(ride);

        assertNull(result);
        verify(driverRepository).findByIsActiveTrue();
        verify(driverSessionRepo).findByDriver(driver);
        verifyNoInteractions(rideRepository);
    }

    @Test //nema zakazane ali ima aktivnu
    public void findAvailableDriver_noAvailableButActiveExists_usesScoring() {
        Driver driver = buildDriver(VehicleType.STANDARD, true, false, 45.1, 19.1);

        ArrayList<Driver> driverList= new ArrayList<>();
        driverList.add(driver);
        when(driverRepository.findByIsActiveTrue()).thenReturn(driverList);

        when(rideRepository.findAllByDriverAndStatus(driver, RideStatus.ACCEPTED)).thenReturn(new ArrayList<>()); //nema pending voznje

        // ima aktivnu voznju koja se zavrsava za 5 min
        Ride activeRide = new Ride();
        activeRide.setStartTime(LocalDateTime.now().minusMinutes(15));
        activeRide.setEstimatedDuration(20.0);
        GeoPoint endLoc = new GeoPoint(45.2, 19.2);
        activeRide.setEndLocation(endLoc);
        ArrayList<Ride> activerRideList= new ArrayList<>();
        activerRideList.add(activeRide);
        when(rideRepository.findAllByDriverAndStatus(driver, RideStatus.IN_PROGRESS)).thenReturn(activerRideList);

        DriverSession session = buildSession(LocalDateTime.now().minusHours(2), LocalDateTime.now());
        ArrayList<DriverSession> sessions=new ArrayList<>();
        sessions.add(session);
        when(driverSessionRepo.findByDriver(driver)).thenReturn(sessions);

        Ride ride = buildRide(VehicleType.STANDARD, 45.0, 19.0);
        Driver result = driverService.findAvailableDriver(ride);

        assertNotNull(result);
        assertEquals(result, driver);
        verify(driverRepository).findByIsActiveTrue();
        verify(driverSessionRepo).findByDriver(driver);
        verify(rideRepository).findAllByDriverAndStatus(driver,RideStatus.ACCEPTED);
       // verify(rideRepository).findAllByDriverAndStatus(driver,RideStatus.IN_PROGRESS);
    }

    @Test
    public void findAvailableDriver_activeDriverHasPendingRide_notSelected() {
        Driver driver = buildDriver(VehicleType.STANDARD, true, false, 45.1, 19.1);

        ArrayList<Driver> driverList= new ArrayList<>();
        driverList.add(driver);
        when(driverRepository.findByIsActiveTrue()).thenReturn(driverList);

        // ima pending vožnju
        ArrayList<Ride> rides= new ArrayList<>();
        rides.add(new Ride());
        when(rideRepository.findAllByDriverAndStatus(driver, RideStatus.ACCEPTED)).thenReturn(rides);

        Ride ride = buildRide(VehicleType.STANDARD, 45.0, 19.0);
        Driver result = driverService.findAvailableDriver(ride);

        assertNull(result);
        verify(driverRepository).findByIsActiveTrue();
        verifyNoInteractions(driverSessionRepo);
        verify(rideRepository).findAllByDriverAndStatus(driver,RideStatus.ACCEPTED);
        verifyNoMoreInteractions(rideRepository);
    }

    //nema zakazane i nema aktivnu a nije available, ne moze
    @Test (expectedExceptions = IllegalArgumentException.class)
    public void findAvailableDriver_activeDriverNoActiveRide_canDriveNextRide() {
        Driver driver = buildDriver(VehicleType.STANDARD, true, false, 45.1, 19.1);

        ArrayList<Driver> driverList= new ArrayList<>();
        driverList.add(driver);
        when(driverRepository.findByIsActiveTrue()).thenReturn(driverList);

        when(rideRepository.findAllByDriverAndStatus(driver, RideStatus.ACCEPTED)).thenReturn(new ArrayList<>());
        when(rideRepository.findAllByDriverAndStatus(driver, RideStatus.IN_PROGRESS)).thenReturn(new ArrayList<>());

        DriverSession session = buildSession(LocalDateTime.now().minusHours(1), LocalDateTime.now());
        ArrayList<DriverSession> sessions=new ArrayList<>();
        sessions.add(session);
        when(driverSessionRepo.findByDriver(driver)).thenReturn(sessions);

        Ride ride = buildRide(VehicleType.STANDARD, 45.0, 19.0);
        Driver result = driverService.findAvailableDriver(ride);

        assertNotNull(result);
        verify(driverRepository).findByIsActiveTrue();
        verify(rideRepository).findAllByDriverAndStatus(driver,RideStatus.ACCEPTED);
        verify(rideRepository).findAllByDriverAndStatus(driver,RideStatus.IN_PROGRESS);
        verify(rideRepository).findAllByDriverAndStatus(driver,RideStatus.IN_PROGRESS);
        verifyNoMoreInteractions(rideRepository);
        verify(driverSessionRepo).findByDriver(driver);

    }

    @Test
    public void findAvailableDriver_activeRideEndsInMoreThan10min_notSelected() {
        Driver driver = buildDriver(VehicleType.STANDARD, true, false, 45.1, 19.1);

        ArrayList<Driver> driverList= new ArrayList<>();
        driverList.add(driver);
        when(driverRepository.findByIsActiveTrue()).thenReturn(driverList);

        when(rideRepository.findAllByDriverAndStatus(driver, RideStatus.ACCEPTED)).thenReturn(new ArrayList<>());

        // vožnja traje još 20 min
        Ride activeRide = new Ride();
        activeRide.setStartTime(LocalDateTime.now());
        activeRide.setEstimatedDuration(30.0);
        ArrayList<Ride> activeRideList= new ArrayList<>();
        activeRideList.add(activeRide);
        when(rideRepository.findAllByDriverAndStatus(driver, RideStatus.IN_PROGRESS)).thenReturn(activeRideList);

        Ride ride = buildRide(VehicleType.STANDARD, 45.0, 19.0);
        Driver result = driverService.findAvailableDriver(ride);

        assertNull(result);
        verify(driverRepository).findByIsActiveTrue();
        verify(rideRepository).findAllByDriverAndStatus(driver,RideStatus.ACCEPTED);
        verify(rideRepository).findAllByDriverAndStatus(driver,RideStatus.IN_PROGRESS);
        verifyNoMoreInteractions(driverRepository);
        verifyNoMoreInteractions(rideRepository);
    }

    @Test
    public void findAvailableDriver_DriverBlocked() {
        Driver driver = buildDriver(VehicleType.STANDARD, true, true, 45.1, 19.1);
        driver.setBlocked(true);
        ArrayList<Driver> driverList= new ArrayList<>();
        driverList.add(driver);
        when(driverRepository.findByIsActiveTrue()).thenReturn(driverList);


        Ride ride = buildRide(VehicleType.STANDARD, 45.0, 19.0);
        Driver result = driverService.findAvailableDriver(ride);

        assertNull(result);
        verify(driverRepository).findByIsActiveTrue();
        verifyNoMoreInteractions(driverRepository);
        verifyNoInteractions(rideRepository);
    }

    @Test
    public void checkAlreadyBookedDateTime_noConflict_returnsTrue() {
        Ride pendingRide = new Ride();
        pendingRide.setVehicleType(VehicleType.STANDARD);
        pendingRide.setStartTime(LocalDateTime.now().plusHours(5));
        pendingRide.setEstimatedDuration(20.0);
        ArrayList<Ride> pendingRides= new ArrayList<>();
        pendingRides.add(pendingRide);
        when(rideRepository.findAllByDriverAndStatus(null, RideStatus.ACCEPTED)).thenReturn(pendingRides);

        Ride ride = buildRide(VehicleType.STANDARD, 45.0, 19.0);
        ride.setStartTime(LocalDateTime.now().plusHours(1));
        ride.setEstimatedDuration(20.0);

        boolean result = driverService.checkAlreadyBookedDateTime(ride);

        assertTrue(result);
        verify(rideRepository).findAllByDriverAndStatus(null,RideStatus.ACCEPTED);
    }

    @Test
    public void checkAlreadyBookedDateTime_conflictingSameType_returnsFalse() {
        Ride pendingRide = new Ride();
        pendingRide.setVehicleType(VehicleType.STANDARD);
        pendingRide.setStartTime(LocalDateTime.now().plusHours(2));
        pendingRide.setEstimatedDuration(30.0);
        ArrayList<Ride> pendingRides= new ArrayList<>();
        pendingRides.add(pendingRide);
        when(rideRepository.findAllByDriverAndStatus(null, RideStatus.ACCEPTED)).thenReturn(pendingRides);

        // ista voznja u istom terminu
        Ride ride = buildRide(VehicleType.STANDARD, 45.0, 19.0);
        ride.setStartTime(LocalDateTime.now().plusHours(2).plusMinutes(10));
        ride.setEstimatedDuration(20.0);

        boolean result = driverService.checkAlreadyBookedDateTime(ride);

        assertFalse(result);
        verify(rideRepository).findAllByDriverAndStatus(null,RideStatus.ACCEPTED);
    }

    @Test
    public void checkAlreadyBookedDateTime_conflictingDifferentType() {
        Ride pendingRide = new Ride();
        pendingRide.setVehicleType(VehicleType.LUXURY);
        pendingRide.setStartTime(LocalDateTime.now().plusHours(2));
        pendingRide.setEstimatedDuration(30.0);
        ArrayList<Ride> pendingRides= new ArrayList<>();
        pendingRides.add(pendingRide);
        when(rideRepository.findAllByDriverAndStatus(null, RideStatus.ACCEPTED)).thenReturn(pendingRides);

        // isti termin ali drugaciji tip vozila — nema konflikta
        Ride ride = buildRide(VehicleType.STANDARD, 45.0, 19.0);
        ride.setStartTime(LocalDateTime.now().plusHours(2).plusMinutes(10));
        ride.setEstimatedDuration(20.0);

        boolean result = driverService.checkAlreadyBookedDateTime(ride);

        assertTrue(result);
        verify(rideRepository).findAllByDriverAndStatus(null,RideStatus.ACCEPTED);
    }

    @Test
    public void checkAlreadyBookedDateTime_noPendingRides() {
        when(rideRepository.findAllByDriverAndStatus(null, RideStatus.ACCEPTED)).thenReturn(new ArrayList<>());

        Ride ride = buildRide(VehicleType.STANDARD, 45.0, 19.0);

        boolean result = driverService.checkAlreadyBookedDateTime(ride);

        assertTrue(result);
        verify(rideRepository).findAllByDriverAndStatus(null,RideStatus.ACCEPTED);
    }

    @Test
    public void checkAlreadyBookedDateTime_newRideStartsExactlyWhenOtherEnds_noConflict() {
        LocalDateTime now = LocalDateTime.now().plusHours(2);

        Ride pendingRide = new Ride();
        pendingRide.setVehicleType(VehicleType.STANDARD);
        pendingRide.setStartTime(now);
        pendingRide.setEstimatedDuration(30.0); // završava u now+30min
        ArrayList<Ride> pendingRides= new ArrayList<>();
        pendingRides.add(pendingRide);
        when(rideRepository.findAllByDriverAndStatus(null, RideStatus.ACCEPTED)).thenReturn(pendingRides);

        // nova vožnja počinje tačno kad se stara završava
        Ride ride = buildRide(VehicleType.STANDARD, 45.0, 19.0);
        ride.setStartTime(now.plusMinutes(30));
        ride.setEstimatedDuration(20.0);

        boolean result = driverService.checkAlreadyBookedDateTime(ride);

        assertTrue(result);
        verify(rideRepository).findAllByDriverAndStatus(null,RideStatus.ACCEPTED);
    }
    @Test
    public void checkAlreadyBookedDateTime_newRideEndsExactlyWhenOtherStarts_noConflict() {
        LocalDateTime now = LocalDateTime.now().plusHours(3);

        Ride pendingRide = new Ride();
        pendingRide.setVehicleType(VehicleType.STANDARD);
        pendingRide.setStartTime(now);
        pendingRide.setEstimatedDuration(30.0);
        ArrayList<Ride> pendingRides= new ArrayList<>();
        pendingRides.add(pendingRide);
        when(rideRepository.findAllByDriverAndStatus(null, RideStatus.ACCEPTED)).thenReturn(pendingRides);

        // nova vožnja završava tačno kad počinje stara
        Ride ride = buildRide(VehicleType.STANDARD, 45.0, 19.0);
        ride.setStartTime(now.minusMinutes(20));
        ride.setEstimatedDuration(20.0);

        boolean result = driverService.checkAlreadyBookedDateTime(ride);

        assertTrue(result);
        verify(rideRepository).findAllByDriverAndStatus(null,RideStatus.ACCEPTED);
    }
}
