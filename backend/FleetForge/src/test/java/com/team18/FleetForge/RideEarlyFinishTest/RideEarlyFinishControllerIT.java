package com.team18.FleetForge.RideEarlyFinishTest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.team18.FleetForge.model.ride.RideLocation;
import com.team18.FleetForge.model.users.Admin;
import com.team18.FleetForge.repository.NotificationRepository;
import com.team18.FleetForge.repository.chat.ChatMessageRepository;
import com.team18.FleetForge.repository.chat.ChatRepository;
import com.team18.FleetForge.repository.rides.*;
import com.team18.FleetForge.repository.users.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;


import com.team18.FleetForge.dto.ride.lifecycle.FinishRideResponseDTO;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.enums.Role;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.ride.GeoPoint;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.repository.users.DriverRepository;
import com.team18.FleetForge.repository.users.PassengerRepository;
import com.team18.FleetForge.util.JwtTokenUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RideEarlyFinishControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RideLocationRepository rideLocationRepository;

    @Autowired
    private InconsistencyReportRepository inconsistencyReportRepository;

    @Autowired
    private RideReviewRepository rideReviewRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private Driver driver;
    private String driverToken;
    private String passengerToken;
    private String adminToken;
    private Ride activeRide;

    @Autowired
    private FavoriteRouteRepo favoriteRouteRepo;


    @BeforeEach
    void setup() {
        restTemplate.getRestTemplate().setRequestFactory(
                new HttpComponentsClientHttpRequestFactory()
        );

        // Clear database
        notificationRepository.deleteAll();
        rideReviewRepository.deleteAll();
        inconsistencyReportRepository.deleteAll();
        favoriteRouteRepo.deleteAll();
        rideLocationRepository.deleteAll();
        rideRepository.deleteAll();
        chatRepository.deleteAll();
        chatMessageRepository.deleteAll();
        userRepository.deleteAll();

        // Set up Driver
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
        driverToken = jwtTokenUtils.generateToken(driver);

        // Set up Passenger
        Passenger passenger = new Passenger();
        passenger.setEmail("passenger@test.com");
        passenger.setPassword("encoded_password");
        passenger.setFirstName("Jane");
        passenger.setLastName("Smith");
        passenger.setRole(Role.ROLE_PASSENGER);
        passenger.setActivated(true);
        passenger = passengerRepository.save(passenger);
        passengerToken = jwtTokenUtils.generateToken(passenger);

        // Set up Admin
        Admin admin = new Admin();
        admin.setEmail("admin@test.com");
        admin.setPassword("encoded_password");
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        admin.setRole(Role.ROLE_ADMIN);
        admin.setActivated(true);
        admin = userRepository.save(admin);
        adminToken = jwtTokenUtils.generateToken(admin);

        // Set up an Active Ride
        activeRide = new Ride();
        activeRide.setDriver(driver);
        activeRide.setPassenger(passenger);
        activeRide.setStartLocation(new GeoPoint(45.2550, 19.8450));
        activeRide.setStartAddress("Start Address");
        activeRide.setEndLocation(new GeoPoint(45.2671, 19.8335));
        activeRide.setEndAddress("End Address");
        activeRide.setStatus(RideStatus.IN_PROGRESS);
        activeRide.setStartTime(LocalDateTime.now().minusMinutes(10));
        activeRide.setVehicleType(VehicleType.STANDARD);
        activeRide.setTotalDistance(2.5);
        activeRide.setWayPoints(new ArrayList<>());
        activeRide.setLinkedPassengers(new ArrayList<>());
        activeRide = rideRepository.save(activeRide);

        saveLocation(45.2550, 19.8450, 10);
        saveLocation(45.2600, 19.8500, 5);
        saveLocation(45.2800, 19.8500, 0);
    }

    private void saveLocation(double lat, double lon, int minsAgo) {
        rideLocationRepository.save(RideLocation.builder()
                .ride(activeRide)
                .latitude(lat)
                .longitude(lon)
                .recordedAt(LocalDateTime.now().minusMinutes(minsAgo))
                .build());
    }

    private HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    @DisplayName("401 Unauthorized when token is missing")
    void testFinishRideUnauthorizedMissingToken() {
        HttpEntity<String> entity = new HttpEntity<>("{}");

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/rides/" + activeRide.getId() + "/finish",
                HttpMethod.PUT,
                entity,
                String.class
        );
        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
    }

    @Test
    @DisplayName("403 Forbidden when passenger tries to finish ride")
    void testFinishRideForbiddenForPassenger() {
        ResponseEntity<String> res = restTemplate.exchange(
                "/api/rides/" + activeRide.getId() + "/finish",
                HttpMethod.PUT,
                new HttpEntity<>(authHeaders(passengerToken)),
                String.class
        );
        assertEquals(HttpStatus.FORBIDDEN, res.getStatusCode());
    }

    @Test
    @DisplayName("403 Forbidden when admin tries to finish ride")
    void testFinishRideForbiddenForAdmin() {
        ResponseEntity<String> res = restTemplate.exchange(
                "/api/rides/" + activeRide.getId() + "/finish",
                HttpMethod.PUT,
                new HttpEntity<>(authHeaders(adminToken)),
                String.class
        );
        assertEquals(HttpStatus.FORBIDDEN, res.getStatusCode());
    }

    @Test
    @DisplayName("400 Bad Request when ride is not in progress")
    void testFinishRideBadRequestStatusNotInProgress() {
        activeRide.setStatus(RideStatus.ACCEPTED);
        rideRepository.save(activeRide);

        ResponseEntity<FinishRideResponseDTO> res = restTemplate.exchange(
                "/api/rides/" + activeRide.getId() + "/finish",
                HttpMethod.PUT,
                new HttpEntity<>(authHeaders(driverToken)),
                FinishRideResponseDTO.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertNotNull(res.getBody());
        assertTrue(res.getBody().getMessage().contains("Ride is not in progress"));
    }

    @Test
    @DisplayName("200 OK when driver successfully finishes an active ride")
    void testFinishRideSuccessAsDriver() {
        ResponseEntity<FinishRideResponseDTO> res = restTemplate.exchange(
                "/api/rides/" + activeRide.getId() + "/finish",
                HttpMethod.PUT,
                new HttpEntity<>(authHeaders(driverToken)),
                FinishRideResponseDTO.class
        );

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals(RideStatus.COMPLETED, res.getBody().getStatus());

        // Database verification
        Ride savedRide = rideRepository.findById(activeRide.getId()).orElseThrow();
        assertEquals(RideStatus.COMPLETED, savedRide.getStatus());
        assertNotNull(savedRide.getEndTime());

        Driver updatedDriver = driverRepository.findById(driver.getId()).orElseThrow();
        assertTrue(updatedDriver.isAvailable(), "Driver should be set to available after finishing a ride");
    }

    @Test
    @DisplayName("400 Bad Request when ride ID does not exist")
    void testFinishRideNotFoundInvalidId() {
        ResponseEntity<FinishRideResponseDTO> res = restTemplate.exchange(
                "/api/rides/99999/finish",
                HttpMethod.PUT,
                new HttpEntity<>(authHeaders(driverToken)),
                FinishRideResponseDTO.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertNotNull(res.getBody());
        assertTrue(res.getBody().getMessage().contains("Ride not found"));
    }
}