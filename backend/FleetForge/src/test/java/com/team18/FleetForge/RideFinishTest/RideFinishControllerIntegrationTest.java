package com.team18.FleetForge.RideFinishTest;

import com.team18.FleetForge.dto.ride.lifecycle.FinishRideResponseDTO;
import com.team18.FleetForge.model.ride.GeoPoint;
import com.team18.FleetForge.model.ride.PriceConfiguration;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.model.vehicles.Vehicle;
import com.team18.FleetForge.model.enums.Role;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.repository.NotificationRepository;
import com.team18.FleetForge.repository.chat.ChatMessageRepository;
import com.team18.FleetForge.repository.chat.ChatRepository;
import com.team18.FleetForge.repository.rides.*;
import com.team18.FleetForge.repository.users.DriverRepository;
import com.team18.FleetForge.repository.users.PassengerRepository;
import com.team18.FleetForge.repository.users.UserRepository;
import com.team18.FleetForge.util.JwtTokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("RideFinishController Integration Tests")
class RideFinishControllerIntegrationTest {


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

    @Autowired
    private PriceConfigurationRepository priceConfigurationRepository;

    private Driver driver;
    private Passenger passenger;
    private String driverToken;
    private String passengerToken;
    private Ride activeRide;

    @BeforeEach
    void setup() {
        notificationRepository.deleteAll();
        rideReviewRepository.deleteAll();
        inconsistencyReportRepository.deleteAll();
        rideLocationRepository.deleteAll();
        rideRepository.deleteAll();
        chatRepository.deleteAll();
        chatMessageRepository.deleteAll();
        userRepository.deleteAll();
        priceConfigurationRepository.deleteAll();

        PriceConfiguration priceConfig = new PriceConfiguration(
                null, VehicleType.STANDARD, 300.0, 120.0, null, null
        );
        priceConfigurationRepository.save(priceConfig);

        Vehicle vehicle = new Vehicle(
                null, "Toyota Camry", VehicleType.STANDARD,
                "NS-123-AB", 4, false, false
        );

        driver = new Driver();
        driver.setEmail("driver@test.com");
        driver.setPassword("123");
        driver.setFirstName("Test");
        driver.setLastName("Driver");
        driver.setPhoneNumber("+381641234567");
        driver.setAddress("Bulevar oslobođenja 10, Novi Sad");
        driver.setProfilePicture("/uploads/pfp/default.png");
        driver.setVehicle(vehicle);
        driver.setActive(true);
        driver.setAvailable(false);
        driver.setCurrentLocation(new GeoPoint(45.2671, 19.8335));
        driver.setRole(Role.ROLE_DRIVER);
        driver.setActivated(true);
        driver.setBlocked(false);
        driver = driverRepository.save(driver);

        passenger = new Passenger();
        passenger.setEmail("passenger@test.com");
        passenger.setPassword("123");
        passenger.setFirstName("Test");
        passenger.setLastName("Passenger");
        passenger.setPhoneNumber("+381641111111");
        passenger.setAddress("Narodnih heroja 15, Novi Sad");
        passenger.setProfilePicture("/uploads/pfp/default.png");
        passenger.setRole(Role.ROLE_PASSENGER);
        passenger.setActivated(true);
        passenger.setBlocked(false);
        passenger = passengerRepository.save(passenger);

        driverToken = jwtTokenUtils.generateToken(driver);
        passengerToken = jwtTokenUtils.generateToken(passenger);

        activeRide = new Ride();
        activeRide.setDriver(driver);
        activeRide.setPassenger(passenger);
        activeRide.setStartLocation(new GeoPoint(45.2550, 19.8450));
        activeRide.setStartAddress("Bulevar oslobođenja 46, Novi Sad");
        activeRide.setEndLocation(new GeoPoint(45.2671, 19.8335));
        activeRide.setEndAddress("Trg slobode 1, Novi Sad");
        activeRide.setStartTime((LocalDateTime.now().minusMinutes(15)));
        activeRide.setEndTime(null);
        activeRide.setTotalDistance(5.2);
        activeRide.setEstimatedDuration(20.0);
        activeRide.setTotalCost(850.0);
        activeRide.setStatus(RideStatus.IN_PROGRESS);
        activeRide.setPanicActivated(false);
        activeRide.setCancelledBy(null);
        activeRide.setCancellationReason(null);
        activeRide.setLinkedPassengers(new ArrayList<>());
        activeRide.setWayPoints(new ArrayList<>());
        activeRide.setVehicleType(VehicleType.STANDARD);
        activeRide = rideRepository.save(activeRide);
    }

    private HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }

    @Test
    @DisplayName("200 OK - Successfully finish active ride")
    void testFinishRideSuccessfully() {
        ResponseEntity<FinishRideResponseDTO> response = restTemplate.exchange(
                "/api/rides/" + activeRide.getId() + "/finish",
                HttpMethod.PUT,
                new HttpEntity<>(authHeaders(driverToken)),
                FinishRideResponseDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        FinishRideResponseDTO body = response.getBody();
        assertEquals(activeRide.getId(), body.getRideId());
        assertEquals(RideStatus.COMPLETED, body.getStatus());
        assertNotNull(body.getEndTime());
        assertNotNull(body.getTotalCost());
        assertTrue(body.getTotalCost() > 0);
        assertEquals("Ride completed successfully", body.getMessage());
        assertTrue(body.isDriverAvailable());

        Ride updatedRide = rideRepository.findById(activeRide.getId()).orElse(null);
        assertNotNull(updatedRide);
        assertEquals(RideStatus.COMPLETED, updatedRide.getStatus());
        assertNotNull(updatedRide.getEndTime());
    }

    @Test
    @DisplayName("400 Bad Request - Ride not found")
    void testFinishRideNotFound() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/rides/999/finish",
                HttpMethod.PUT,
                new HttpEntity<>(authHeaders(driverToken)),
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Ride not found") ||
                response.getBody().contains("not found"));
    }

    @Test
    @DisplayName("400 Bad Request - Ride not active")
    void testFinishRideNotActive() {
        activeRide.setStatus(RideStatus.COMPLETED);
        activeRide.setEndTime((LocalDateTime.now().minusMinutes(15)));
        rideRepository.save(activeRide);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/rides/" + activeRide.getId() + "/finish",
                HttpMethod.PUT,
                new HttpEntity<>(authHeaders(driverToken)),
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Ride is not in progress"));
    }

    @Test
    @DisplayName("401 Unauthorized - No authentication token")
    void testFinishRideNoToken() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/rides/" + activeRide.getId() + "/finish",
                HttpMethod.PUT,
                new HttpEntity<>(new HttpHeaders()),
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }


    @Test
    @DisplayName("403 Forbidden - Passenger cannot finish ride")
    void finishRide_returns403_whenRoleNotDriver() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/rides/" + activeRide.getId() + "/finish",
                HttpMethod.PUT,
                new HttpEntity<>(authHeaders(passengerToken)),
                String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}