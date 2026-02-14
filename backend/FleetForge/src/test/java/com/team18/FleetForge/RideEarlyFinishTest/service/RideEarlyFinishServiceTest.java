package com.team18.FleetForge.RideEarlyFinishTest.service;

import com.team18.FleetForge.dto.ride.lifecycle.FinishRideRequestDTO;
import com.team18.FleetForge.dto.ride.lifecycle.FinishRideResponseDTO;
import com.team18.FleetForge.exception.ride.RideNotActiveException;
import com.team18.FleetForge.exception.ride.RideNotFoundException;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.ride.GeoPoint;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.ride.RideLocation;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.model.vehicles.Vehicle;
import com.team18.FleetForge.repository.rides.RideLocationRepository;
import com.team18.FleetForge.repository.rides.RideRepository;
import com.team18.FleetForge.repository.users.DriverRepository;
import com.team18.FleetForge.service.EmailService;
import com.team18.FleetForge.service.NotificationService;
import com.team18.FleetForge.service.PriceCalculationService;
import com.team18.FleetForge.service.rides.RideFinishService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RideEarlyFinishServiceTest {

    @Mock private RideRepository rideRepository;
    @Mock private DriverRepository driverRepository;
    @Mock private EmailService emailService;
    @Mock private RideLocationRepository rideLocationRepository;
    @Mock private PriceCalculationService priceCalculationService;
    @Mock private NotificationService notificationService;

    @InjectMocks
    private RideFinishService rideFinishService;

    private Ride ride;
    private Driver driver;
    private Passenger passenger;

    @BeforeEach
    void setup() {
        driver = new Driver();
        driver.setId(1L);
        driver.setCurrentLocation(new GeoPoint(45.0, 19.0));
        driver.setAvailable(false);

        Vehicle vehicle = new Vehicle(null, "Toyota Camry", VehicleType.STANDARD,
                "NS-123-AB", 4, false, false);
        driver.setVehicle(vehicle);

        passenger = new Passenger();
        passenger.setId(2L);
        passenger.setEmail("passenger@test.com");
        passenger.setFirstName("Test");
        passenger.setLastName("User");

        ride = new Ride();
        ride.setId(10L);
        ride.setStatus(RideStatus.IN_PROGRESS);
        ride.setDriver(driver);
        ride.setPassenger(passenger);
        ride.setStartTime(LocalDateTime.now().minusMinutes(20));
        ride.setEndLocation(new GeoPoint(46.0, 20.0));
        ride.setVehicleType(VehicleType.STANDARD);
        ride.setWayPoints(new ArrayList<>());
        ride.setTotalDistance(5.0);
    }


    @Test
    void shouldThrowRideNotFoundExceptionWhenRideDoesNotExist() {

        FinishRideRequestDTO request = new FinishRideRequestDTO();
        request.setRideId(999L);

        when(rideRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RideNotFoundException.class,
                () -> rideFinishService.finishRide(request));
    }

    @Test
    void shouldThrowRideNotActiveExceptionWhenRideIsNotInProgress() {

        ride.setStatus(RideStatus.COMPLETED);

        FinishRideRequestDTO request = new FinishRideRequestDTO();
        request.setRideId(10L);

        when(rideRepository.findById(10L)).thenReturn(Optional.of(ride));

        assertThrows(RideNotActiveException.class,
                () -> rideFinishService.finishRide(request));
    }


    @Test
    void shouldNotSetDriverAvailableWhenNextRideIsWithinTenMinutes() {

        FinishRideRequestDTO request = new FinishRideRequestDTO();
        request.setRideId(10L);

        Ride nextRide = new Ride();
        nextRide.setId(20L);
        nextRide.setStatus(RideStatus.ACCEPTED);
        nextRide.setStartTime(LocalDateTime.now().plusMinutes(5));
        nextRide.setPassenger(passenger);
        nextRide.setWayPoints(new ArrayList<>());
        nextRide.setPanicActivated(false);

        when(rideRepository.findById(10L)).thenReturn(Optional.of(ride));
        when(rideRepository.findAllByDriverAndStatus(driver, RideStatus.ACCEPTED))
                .thenReturn(new ArrayList<>(List.of(nextRide)));

        when(rideLocationRepository
                .findByRideAndRecordedAtAfterOrderByRecordedAtAsc(eq(ride), any()))
                .thenReturn(new ArrayList<>());

        when(priceCalculationService.calculatePrice(anyDouble(), any()))
                .thenReturn(500.0);

        FinishRideResponseDTO response = rideFinishService.finishRide(request);

        assertFalse(driver.isAvailable());
        assertNotNull(response.getNextRide());
        assertEquals(20L, response.getNextRide().getRideId());
    }


    @Test
    void shouldSetDriverAvailableWhenNoNextRideExists() {

        FinishRideRequestDTO request = new FinishRideRequestDTO();
        request.setRideId(10L);

        when(rideRepository.findById(10L)).thenReturn(Optional.of(ride));
        when(rideRepository.findAllByDriverAndStatus(driver, RideStatus.ACCEPTED))
                .thenReturn(new ArrayList<>());

        when(rideLocationRepository
                .findByRideAndRecordedAtAfterOrderByRecordedAtAsc(eq(ride), any()))
                .thenReturn(new ArrayList<>());

        when(priceCalculationService.calculatePrice(anyDouble(), any()))
                .thenReturn(500.0);

        FinishRideResponseDTO response = rideFinishService.finishRide(request);

        assertTrue(driver.isAvailable());
        assertNull(response.getNextRide());
    }


    @Test
    void shouldRecalculateDistanceAndCostWhenDriverIsFarFromEndLocation() {

        FinishRideRequestDTO request = new FinishRideRequestDTO();
        request.setRideId(10L);

        when(rideRepository.findById(10L)).thenReturn(Optional.of(ride));
        when(rideRepository.findAllByDriverAndStatus(driver, RideStatus.ACCEPTED))
                .thenReturn(new ArrayList<>());

        RideLocation loc1 = new RideLocation();
        loc1.setLatitude(45.0);
        loc1.setLongitude(19.0);

        RideLocation loc2 = new RideLocation();
        loc2.setLatitude(45.5);
        loc2.setLongitude(19.5);

        RideLocation loc3 = new RideLocation();
        loc3.setLatitude(46.0);
        loc3.setLongitude(20.0);

        when(rideLocationRepository
                .findByRideAndRecordedAtAfterOrderByRecordedAtAsc(eq(ride), any()))
                .thenReturn(new ArrayList<>(List.of(loc1, loc2, loc3)));

        when(priceCalculationService.calculatePrice(anyDouble(), eq(VehicleType.STANDARD)))
                .thenReturn(1000.0);

        FinishRideResponseDTO response = rideFinishService.finishRide(request);

        assertEquals(RideStatus.COMPLETED, response.getStatus());
        assertEquals(1000.0, response.getTotalCost());
        assertTrue(driver.isAvailable());

        verify(priceCalculationService)
                .calculatePrice(ride.getTotalDistance(), VehicleType.STANDARD);
    }
}
