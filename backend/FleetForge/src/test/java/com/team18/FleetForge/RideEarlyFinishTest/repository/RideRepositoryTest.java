package com.team18.FleetForge.RideEarlyFinishTest.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.enums.Role;
import com.team18.FleetForge.model.ride.GeoPoint;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.repository.rides.RideRepository;
import com.team18.FleetForge.repository.users.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Stream;

@DataJpaTest
@ActiveProfiles("test")
class RideRepositoryTest {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private DriverRepository driverRepository;

    private Driver driver;

    @BeforeEach
    void setUp() {
        // Arrange
        rideRepository.deleteAll();

        driver = new Driver();
        driver.setEmail("driver@test.com");
        driver.setPassword("encoded_password");
        driver.setFirstName("John");
        driver.setLastName("Doe");
        driver.setRole(Role.ROLE_DRIVER);
        driver.setCurrentLocation(new GeoPoint(45.2690, 19.8335));
        driver.setAvailable(false);
        driver.setActivated(true);
        driver = driverRepository.save(driver);
    }

    @ParameterizedTest(name = "Status {0} should return {1} rides")
    @MethodSource("provideStatusExpectedCounts")
    @DisplayName("Verify filtering for all possible Ride statuses")
    void testFindAllByDriverAndStatusFiltering(RideStatus statusToSearch, int expectedCount) {
        // Arrange
        saveRide(driver, RideStatus.ACCEPTED);
        saveRide(driver, RideStatus.IN_PROGRESS);
        saveRide(driver, RideStatus.IN_PROGRESS);
        saveRide(driver, RideStatus.CANCELLED);

        // Act
        List<Ride> foundRides = rideRepository.findAllByDriverAndStatus(driver, statusToSearch);

        // Assert
        assertEquals(expectedCount, foundRides.size());
    }

    private static Stream<Arguments> provideStatusExpectedCounts() {
        return Stream.of(
                Arguments.of(RideStatus.ACCEPTED, 1),
                Arguments.of(RideStatus.IN_PROGRESS, 2),
                Arguments.of(RideStatus.COMPLETED, 0),
                Arguments.of(RideStatus.CANCELLED, 1)
        );
    }

    private void saveRide(Driver driver, RideStatus status) {
        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setStatus(status);
        ride.setStartAddress("Start");
        ride.setEndAddress("End");
        rideRepository.save(ride);
    }

    @Test
    @DisplayName("Should return empty list when driver has no rides regardless of status")
    void shouldReturnEmptyListWhenDriverHasNoRides() {
        // Arrange
        Driver otherDriver = new Driver();
        otherDriver.setEmail("other_driver@test.com");
        otherDriver.setRole(Role.ROLE_DRIVER);
        otherDriver.setPassword("encoded_password");
        otherDriver.setFirstName("John");
        otherDriver.setLastName("Doe");
        otherDriver.setAvailable(false);
        otherDriver = driverRepository.save(otherDriver);

        // Act
        List<Ride> foundRides = rideRepository.findAllByDriverAndStatus(otherDriver, RideStatus.ACCEPTED);

        // Assert
        assertTrue(foundRides.isEmpty());
    }

    @Test
    @DisplayName("Should handle multiple rides of the same status for one driver")
    void shouldFindMultipleRidesForSameStatus() {
        // Arrange
        for (int i = 0; i < 11; i++) {
            Ride ride = new Ride();
            ride.setDriver(driver);
            ride.setStatus(RideStatus.ACCEPTED);
            ride.setStartAddress("Address " + i);
            rideRepository.save(ride);
        }

        // Act
        List<Ride> foundRides = rideRepository.findAllByDriverAndStatus(driver, RideStatus.ACCEPTED);

        // Assert
        assertEquals(11, foundRides.size());
    }

    @Test
    @DisplayName("Should only return rides belonging to the specific driver")
    void shouldNotReturnRidesFromOtherDrivers() {
        // Arrange
        Driver extraDriver = new Driver();
        extraDriver.setEmail("extra_driver@test.com");
        extraDriver.setPassword("encoded_password");
        extraDriver.setFirstName("John");
        extraDriver.setLastName("Doe");
        extraDriver.setRole(Role.ROLE_DRIVER);
        extraDriver.setCurrentLocation(new GeoPoint(45.2690, 19.8335));
        extraDriver.setAvailable(false);
        extraDriver.setActivated(true);
        driverRepository.save(extraDriver);

        saveRide(driver, RideStatus.ACCEPTED);
        saveRide(extraDriver, RideStatus.ACCEPTED);

        // Act
        List<Ride> result = rideRepository.findAllByDriverAndStatus(driver, RideStatus.ACCEPTED);

        // Assert
        assertEquals(1, result.size(), "Should only find the ride belonging to the target driver");
    }
}