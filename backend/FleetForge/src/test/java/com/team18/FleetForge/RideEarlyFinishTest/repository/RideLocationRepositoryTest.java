package com.team18.FleetForge.RideEarlyFinishTest.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.enums.Role;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.ride.RideLocation;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.repository.rides.RideLocationRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@DataJpaTest
@ActiveProfiles("test")
class RideLocationRepositoryTest {

    @Autowired
    private RideLocationRepository rideLocationRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private DriverRepository driverRepository;

    private Driver driver;
    private Ride ride;

    private static final LocalDateTime BASE_TIME =
            LocalDateTime.of(2026, 2, 1, 12, 0);

    @BeforeEach
    void setUp() {
        rideLocationRepository.deleteAll();
        rideRepository.deleteAll();
        driverRepository.deleteAll();

        // Create driver
        driver = new Driver();
        driver.setEmail("driver@test.com");
        driver.setPassword("encoded_password");
        driver.setFirstName("John");
        driver.setLastName("Doe");
        driver.setRole(Role.ROLE_DRIVER);
        driver = driverRepository.save(driver);

        // Create ride
        ride = new Ride();
        ride.setDriver(driver);
        ride.setStartAddress("Start");
        ride.setEndAddress("End");
        ride.setStatus(RideStatus.COMPLETED);
        ride = rideRepository.save(ride);

        saveLocation(BASE_TIME.minusMinutes(5));
        saveLocation(BASE_TIME.plusMinutes(7));
        saveLocation(BASE_TIME.plusMinutes(10));
    }

    private void saveLocation(LocalDateTime time) {
        RideLocation loc = new RideLocation();
        loc.setRide(ride);
        loc.setRecordedAt(time);
        loc.setLatitude(45.0);
        loc.setLongitude(19.0);
        rideLocationRepository.save(loc);
    }

    @ParameterizedTest(name = "recordedAfter={0} should return {1} locations")
    @MethodSource("provideRecordedAfterTimes")
    @DisplayName("Verify filtering by recordedAfter with multiple time points")
    void testFindByRideAndRecordedAtAfterOrderByRecordedAtAsc(LocalDateTime recordedAfter, int expectedCount) {
        // Act
        List<RideLocation> result = rideLocationRepository
                .findByRideAndRecordedAtAfterOrderByRecordedAtAsc(ride, recordedAfter);

        // Assert
        assertEquals(expectedCount, result.size());
        if (!result.isEmpty()) {
            for (int i = 1; i < result.size(); i++) {
                assertTrue(result.get(i - 1).getRecordedAt().isBefore(result.get(i).getRecordedAt()),
                        "Locations should be in ascending order of recordedAt");
            }
        }
    }

    private static Stream<Arguments> provideRecordedAfterTimes() {
        return Stream.of(
                Arguments.of(BASE_TIME.minusMinutes(10), 3),
                Arguments.of(BASE_TIME.minusMinutes(1), 2),
                Arguments.of(BASE_TIME.plusMinutes(1), 2),
                Arguments.of(BASE_TIME.plusMinutes(7), 1),
                Arguments.of(BASE_TIME.plusMinutes(15), 0)
        );
    }

    @Test
    @DisplayName("Should return empty list if no locations recorded after given time")
    void shouldReturnEmptyListIfNoLocationsAfterGivenTime() {
        // Arrange
        LocalDateTime futureTime = BASE_TIME.plusDays(1);

        // Act
        List<RideLocation> result = rideLocationRepository
                .findByRideAndRecordedAtAfterOrderByRecordedAtAsc(ride, futureTime);

        // Assert
        assertTrue(result.isEmpty(), "Should return empty list when no locations are after given time");
    }

    @Test
    @DisplayName("Should only return locations belonging to the specific ride")
    void shouldNotReturnLocationsFromOtherRides() {
        // Arrange
        Ride otherRide = new Ride();
        otherRide.setDriver(driver);
        otherRide.setStartAddress("OtherStart");
        otherRide.setEndAddress("OtherEnd");
        otherRide.setStatus(RideStatus.COMPLETED);
        otherRide = rideRepository.save(otherRide);

        RideLocation otherLoc = new RideLocation();
        otherLoc.setRide(otherRide);
        otherLoc.setRecordedAt(BASE_TIME.plusMinutes(20));
        otherLoc.setLatitude(50.0);
        otherLoc.setLongitude(25.0);
        rideLocationRepository.save(otherLoc);

        // Act
        List<RideLocation> result =
                rideLocationRepository
                        .findByRideAndRecordedAtAfterOrderByRecordedAtAsc(
                                ride,
                                BASE_TIME.minusMinutes(10)
                        );

        // Assert
        assertEquals(3, result.size());
    }
}
