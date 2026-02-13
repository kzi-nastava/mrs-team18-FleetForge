package com.team18.FleetForge.RideFinishTest;

import com.team18.FleetForge.dto.ride.lifecycle.FinishRideRequestDTO;
import com.team18.FleetForge.dto.ride.lifecycle.FinishRideResponseDTO;
import com.team18.FleetForge.exception.ride.RideNotActiveException;
import com.team18.FleetForge.exception.ride.RideNotFoundException;
import com.team18.FleetForge.model.enums.NotificationType;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.model.vehicles.Vehicle;
import com.team18.FleetForge.model.ride.GeoPoint;
import com.team18.FleetForge.repository.rides.RideLocationRepository;
import com.team18.FleetForge.repository.rides.RideRepository;
import com.team18.FleetForge.repository.users.DriverRepository;
import com.team18.FleetForge.service.EmailService;
import com.team18.FleetForge.service.NotificationService;
import com.team18.FleetForge.service.PriceCalculationService;
import com.team18.FleetForge.service.rides.RideFinishService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RideFinishService Unit Tests")
class RideFinishServiceTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private RideLocationRepository rideLocationRepository;

    @Mock
    private PriceCalculationService priceCalculationService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private RideFinishService rideFinishService;

    private Ride testRide;
    private Driver testDriver;
    private Passenger testPassenger;
    private Vehicle testVehicle;
    private FinishRideRequestDTO finishRequest;
    private GeoPoint startLocation;
    private GeoPoint endLocation;

    @BeforeEach
    void setUp() {
        setupGeoPoints();
        testPassenger = createTestPassenger();
        testVehicle = createTestVehicle();
        testDriver = createTestDriver();
        testRide = createTestRide();
        finishRequest = FinishRideRequestDTO.builder()
                .rideId(1L)
                .build();
    }

    private void setupGeoPoints() {
        startLocation = new GeoPoint();
        startLocation.setLatitude(45.2671);
        startLocation.setLongitude(19.8335);

        endLocation = new GeoPoint();
        endLocation.setLatitude(45.2474);
        endLocation.setLongitude(19.8515);
    }

    @Test
    @DisplayName("Successful - no next ride")
    void testFinishRideSuccessfulNoNextRide() {

        when(rideRepository.findById(1L)).thenReturn(Optional.of(testRide));
        when(rideRepository.findAllByDriverAndStatus(testDriver, RideStatus.ACCEPTED))
                .thenReturn(new ArrayList<>());
        when(priceCalculationService.calculatePrice(10.0, VehicleType.STANDARD))
                .thenReturn(2000.0);
        when(rideRepository.save(any(Ride.class))).thenReturn(testRide);
        when(driverRepository.save(any(Driver.class))).thenReturn(testDriver);

        FinishRideResponseDTO response = rideFinishService.finishRide(finishRequest);

        assertThat(response).isNotNull();
        assertThat(response.getRideId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo(RideStatus.COMPLETED);
        assertThat(response.getTotalCost()).isEqualTo(2000.0);
        assertThat(response.isDriverAvailable()).isTrue();
        assertThat(response.getNextRide()).isNull();

        assertThat(testRide.getStatus()).isEqualTo(RideStatus.COMPLETED);
        assertThat(testRide.getEndTime()).isNotNull();
        assertThat(testRide.getTotalDistance()).isEqualTo(10.0);
        assertThat(testRide.getTotalCost()).isEqualTo(2000.0);
        assertThat(testDriver.isAvailable()).isTrue();

        verify(rideRepository).findById(1L);
        verify(rideRepository).save(testRide);
        verify(driverRepository).save(testDriver);
        verify(rideLocationRepository, never()).findByRideAndRecordedAtAfterOrderByRecordedAtAsc(any(), any());
        verify(notificationService).sendNotificationToUser(
                eq(testPassenger), eq(NotificationType.RIDE_COMPLETED), eq("Ride finished"), eq(testRide));
        verify(emailService).sendEmail(eq(testPassenger.getEmail()), anyString(), anyString());
    }

    @Test
    @DisplayName("Successful - has next ride")
    void testFinishRideSuccessfulHasNextRide() {

        Ride nextRide = createTestRide();
        nextRide.setId(2L);
        nextRide.setStatus(RideStatus.ACCEPTED);
        nextRide.setStartTime(LocalDateTime.now().plusMinutes(5));

        when(rideRepository.findById(1L)).thenReturn(Optional.of(testRide));
        when(rideRepository.findAllByDriverAndStatus(testDriver, RideStatus.ACCEPTED))
                .thenReturn(new ArrayList<>(List.of(nextRide)));
        when(priceCalculationService.calculatePrice(anyDouble(), any())).thenReturn(1500.0);
        when(rideRepository.save(any(Ride.class))).thenReturn(testRide);
        when(driverRepository.save(any(Driver.class))).thenReturn(testDriver);

        FinishRideResponseDTO response = rideFinishService.finishRide(finishRequest);

        assertThat(response.isDriverAvailable()).isFalse();
        assertThat(response.getNextRide()).isNotNull();
        assertThat(response.getNextRide().getRideId()).isEqualTo(2L);
        assertThat(testDriver.isAvailable()).isFalse();
    }

    @Test
    @DisplayName("Notifications and emails")
    void testSendNotificationsAndEmails() {
        testDriver.setCurrentLocation(copyGeoPoint(endLocation));
        testRide.setTotalDistance(10.0);

        Passenger linked1 = createPassenger(2L, "linked1@test.com");
        Passenger linked2 = createPassenger(3L, "linked2@test.com");
        testRide.setLinkedPassengers(List.of(linked1, linked2));

        when(rideRepository.findById(1L)).thenReturn(Optional.of(testRide));
        when(rideRepository.findAllByDriverAndStatus(testDriver, RideStatus.ACCEPTED))
                .thenReturn(new ArrayList<>());
        when(priceCalculationService.calculatePrice(anyDouble(), any())).thenReturn(1500.0);
        when(rideRepository.save(any(Ride.class))).thenReturn(testRide);
        when(driverRepository.save(any(Driver.class))).thenReturn(testDriver);

        rideFinishService.finishRide(finishRequest);

        verify(notificationService).sendNotificationToUser(
                eq(testPassenger), eq(NotificationType.RIDE_COMPLETED), eq("Ride finished"), eq(testRide));
        verify(notificationService).sendNotificationToLinkedPassengers(
                eq(testRide), eq(NotificationType.RIDE_COMPLETED), eq("Ride finished"));
        verify(emailService, times(3)).sendEmail(anyString(), anyString(), anyString());
    }


    @Test
    @DisplayName("RideNotFoundException")
    void testFinishRideNotFoundException() {
        when(rideRepository.findById(1L)).thenReturn(Optional.empty());

        RideNotFoundException exception = assertThrows(RideNotFoundException.class, () -> rideFinishService.finishRide(finishRequest));

        assertEquals("Ride not found with id: 1", exception.getMessage());
        verify(rideRepository, never()).save(any());
        verify(driverRepository, never()).save(any());
    }

    @Test
    @DisplayName("RideNotActiveException")
    void testFinishRideNotActiveException() {
        testRide.setStatus(RideStatus.ACCEPTED);
        when(rideRepository.findById(1L)).thenReturn(Optional.of(testRide));


        RideNotActiveException exception = assertThrows(RideNotActiveException.class, () -> rideFinishService.finishRide(finishRequest));

        assertEquals("Ride is not in progress. Current status: ACCEPTED", exception.getMessage());

        verify(rideRepository, never()).save(any());
    }

    private Driver createTestDriver() {
        Driver driver = new Driver();
        driver.setId(1L);
        driver.setFirstName("Milos");
        driver.setLastName("Damjanovic");
        driver.setEmail("driver@test.com");
        driver.setPhoneNumber("+381641234567");
        driver.setAvailable(false);
        driver.setCurrentLocation(copyGeoPoint(endLocation));
        driver.setVehicle(testVehicle);
        return driver;
    }

    private Passenger createTestPassenger() {
        return createPassenger(1L, "test@test.com");
    }

    private Passenger createPassenger(Long id, String email) {
        Passenger passenger = new Passenger();
        passenger.setId(id);
        passenger.setFirstName("Jane");
        passenger.setLastName("Passenger");
        passenger.setEmail(email);
        passenger.setPhoneNumber("+381651234567");
        return passenger;
    }

    private Vehicle createTestVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setType(VehicleType.STANDARD);
        vehicle.setModel("Toyota Camry");
        vehicle.setRegistrationNumber("NS-NS");
        return vehicle;
    }

    private Ride createTestRide() {
        Ride ride = new Ride();
        ride.setId(1L);
        ride.setStatus(RideStatus.IN_PROGRESS);
        ride.setDriver(testDriver);
        ride.setPassenger(testPassenger);
        ride.setVehicleType(VehicleType.STANDARD);
        ride.setStartTime(LocalDateTime.now().minusMinutes(30));
        ride.setStartAddress("Bulevar oslobodjenja 46, Novi Sad");
        ride.setEndAddress("Univerzitetska 2, Novi Sad");
        ride.setStartLocation(copyGeoPoint(startLocation));
        ride.setEndLocation(copyGeoPoint(endLocation));
        ride.setTotalDistance(10.0);
        ride.setTotalCost(0.0);
        return ride;
    }

    private GeoPoint copyGeoPoint(GeoPoint source) {
        GeoPoint copy = new GeoPoint();
        copy.setLatitude(source.getLatitude());
        copy.setLongitude(source.getLongitude());
        return copy;
    }
}