package com.team18.FleetForge.RideEarlyFinishTest.service;

import com.team18.FleetForge.dto.ride.lifecycle.FinishRideRequestDTO;
import com.team18.FleetForge.dto.ride.lifecycle.FinishRideResponseDTO;
import com.team18.FleetForge.exception.ride.RideNotActiveException;
import com.team18.FleetForge.exception.ride.RideNotFoundException;
import com.team18.FleetForge.model.enums.NotificationType;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
    @DisplayName("Should throw RideNotFoundException when ride does not exist")
    void shouldThrowRideNotFoundExceptionWhenRideDoesNotExist() {
        // Arrange
        FinishRideRequestDTO request = new FinishRideRequestDTO();
        request.setRideId(999L);
        when(rideRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RideNotFoundException.class, () -> rideFinishService.finishRide(request));

        // Assert (Verifications)
        verify(rideRepository).findById(999L);
        verifyNoMoreInteractions(rideRepository);
        verifyNoInteractions(driverRepository, emailService, notificationService);
    }

    @Test
    @DisplayName("Should throw RideNotActiveException when ride is not IN_PROGRESS")
    void shouldThrowRideNotActiveExceptionWhenRideIsNotInProgress() {
        // Arrange
        ride.setStatus(RideStatus.COMPLETED);
        FinishRideRequestDTO request = new FinishRideRequestDTO();
        request.setRideId(10L);
        when(rideRepository.findById(10L)).thenReturn(Optional.of(ride));

        // Act & Assert
        assertThrows(RideNotActiveException.class, () -> rideFinishService.finishRide(request));

        // Assert (Verifications)
        verify(rideRepository).findById(10L);
        verifyNoInteractions(driverRepository, emailService, notificationService);
    }

    @Test
    @DisplayName("Should NOT set driver available when next ride starts within 10 minutes")
    void shouldNotSetDriverAvailableWhenNextRideIsWithinTenMinutes() {
        // Arrange
        FinishRideRequestDTO request = new FinishRideRequestDTO();
        request.setRideId(10L);

        Ride nextRide = new Ride();
        nextRide.setId(20L);
        nextRide.setStatus(RideStatus.ACCEPTED);
        nextRide.setStartTime(LocalDateTime.now().plusMinutes(5));
        nextRide.setPassenger(passenger);
        nextRide.setWayPoints(new ArrayList<>());

        when(rideRepository.findById(10L)).thenReturn(Optional.of(ride));
        when(rideRepository.findAllByDriverAndStatus(driver, RideStatus.ACCEPTED))
                .thenReturn(new ArrayList<>(List.of(nextRide)));
        when(rideLocationRepository.findByRideAndRecordedAtAfterOrderByRecordedAtAsc(eq(ride), any()))
                .thenReturn(new ArrayList<>());
        when(priceCalculationService.calculatePrice(anyDouble(), any())).thenReturn(500.0);

        // Act
        FinishRideResponseDTO response = rideFinishService.finishRide(request);

        // Assert
        assertFalse(driver.isAvailable(), "Driver should remain unavailable due to an upcoming ride.");
        assertNotNull(response.getNextRide());
        assertEquals(20L, response.getNextRide().getRideId());

        verify(rideRepository).save(ride);
        verify(driverRepository).save(driver);

        String subject = "Ride Completed - FleetForge";
        String body = buildCompletionEmailBody(ride, passenger);
        verify(emailService).sendEmail(passenger.getEmail(), subject, body);
        verify(notificationService).sendNotificationToUser(passenger, NotificationType.RIDE_COMPLETED, "Ride finished", ride);
    }

    @Test
    @DisplayName("Should set driver available when no next ride exists")
    void shouldSetDriverAvailableWhenNoNextRideExists() {
        // Arrange
        FinishRideRequestDTO request = new FinishRideRequestDTO();
        request.setRideId(10L);

        when(rideRepository.findById(10L)).thenReturn(Optional.of(ride));
        when(rideRepository.findAllByDriverAndStatus(driver, RideStatus.ACCEPTED))
                .thenReturn(new ArrayList<>());
        when(rideLocationRepository.findByRideAndRecordedAtAfterOrderByRecordedAtAsc(eq(ride), any()))
                .thenReturn(new ArrayList<>());
        when(priceCalculationService.calculatePrice(anyDouble(), any())).thenReturn(500.0);

        // Act
        FinishRideResponseDTO response = rideFinishService.finishRide(request);

        // Assert
        assertTrue(driver.isAvailable(), "Driver should be set to available as there is no next ride.");
        assertNull(response.getNextRide());

        verify(driverRepository).save(driver);
        verify(rideRepository).save(ride);

        String subject = "Ride Completed - FleetForge";
        String body = buildCompletionEmailBody(ride, passenger);
        verify(emailService).sendEmail(passenger.getEmail(), subject, body);
        verify(notificationService).sendNotificationToUser(passenger, NotificationType.RIDE_COMPLETED, "Ride finished", ride);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideRideLocationScenarios")
    @DisplayName("Should successfully finish ride and calculate distance correctly based on locations")
    void shouldFinishRideWithVaryingLocationData(String description, List<RideLocation> locations, double expectedPrice) {
        // Arrange
        FinishRideRequestDTO request = new FinishRideRequestDTO();
        request.setRideId(10L);

        when(rideRepository.findById(10L)).thenReturn(Optional.of(ride));
        when(rideRepository.findAllByDriverAndStatus(driver, RideStatus.ACCEPTED)).thenReturn(new ArrayList<>());
        when(rideLocationRepository.findByRideAndRecordedAtAfterOrderByRecordedAtAsc(eq(ride), any()))
                .thenReturn(locations);

        when(priceCalculationService.calculatePrice(anyDouble(), eq(VehicleType.STANDARD)))
                .thenReturn(expectedPrice);

        // Act
        FinishRideResponseDTO response = rideFinishService.finishRide(request);

        // Assert
        assertEquals(RideStatus.COMPLETED, response.getStatus());
        assertEquals(expectedPrice, response.getTotalCost());
        assertTrue(driver.isAvailable());

        verify(rideRepository).save(ride);
        verify(driverRepository).save(driver);
        verify(emailService).sendEmail(eq(passenger.getEmail()), anyString(), anyString());
        verify(notificationService).sendNotificationToUser(eq(passenger), eq(NotificationType.RIDE_COMPLETED), anyString(), eq(ride));
    }

    private static Stream<Arguments> provideRideLocationScenarios() {
        // Mock locations
        RideLocation loc1 = new RideLocation(); loc1.setLatitude(45.0); loc1.setLongitude(19.0);
        RideLocation loc2 = new RideLocation(); loc2.setLatitude(45.5); loc2.setLongitude(19.5);
        RideLocation loc3 = new RideLocation(); loc3.setLatitude(46.0); loc3.setLongitude(20.0);

        return Stream.of(
                Arguments.of("0 locations - Expected 0.0 distance price", Collections.emptyList(), 0.0),
                Arguments.of("1 location - Expected 0.0 distance price", List.of(loc1), 0.0),
                Arguments.of("Multiple locations - Expected calculated distance price", List.of(loc1, loc2, loc3), 1000.0)
        );
    }

    private String buildCompletionEmailBody(Ride ride, Passenger passenger) {
        return String.format(
                "Dear %s %s,\n\n" +
                        "Your ride has been completed successfully!\n\n" +
                        "Ride Details:\n" +
                        "- From: %s\n" +
                        "- To: %s\n" +
                        "- Start Time: %s\n" +
                        "- End Time: %s\n" +
                        "- Total Cost: %.2f RSD\n\n" +
                        "Thank you for using FleetForge!\n\n" +
                        "You can now rate your ride and driver through the application.\n\n" +
                        "Best regards,\n" +
                        "FleetForge Team",
                passenger.getFirstName(), passenger.getLastName(),
                ride.getStartAddress(), ride.getEndAddress(),
                ride.getStartTime(), ride.getEndTime(),
                ride.getTotalCost()
        );
    }
}