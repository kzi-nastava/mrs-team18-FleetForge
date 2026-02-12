package com.team18.FleetForge.RideOrderTest.service;

import com.team18.FleetForge.dto.ride.lifecycle.RideCreateRequestDTO;
import com.team18.FleetForge.dto.ride.routes.WayPointDTO;
import com.team18.FleetForge.exception.user.InvalidUserRoleException;
import com.team18.FleetForge.model.enums.NotificationType;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.ride.GeoPoint;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.repository.rides.InconsistencyReportRepository;
import com.team18.FleetForge.repository.rides.RideLocationRepository;
import com.team18.FleetForge.repository.rides.RideRepository;
import com.team18.FleetForge.repository.users.UserRepository;
import com.team18.FleetForge.service.EmailService;
import com.team18.FleetForge.service.NotificationService;
import com.team18.FleetForge.service.PriceCalculationService;
import com.team18.FleetForge.service.impl.RideServiceImpl;
import com.team18.FleetForge.service.users.DriverService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.Closeable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.*;

public class RideServiceTest {
     private RideRepository rideRepository;
     private UserRepository userRepository;
     private DriverService driverService;
     private NotificationService notificationService;
     private EmailService emailService;
     private PriceCalculationService priceCalculationService;
    private  RideLocationRepository rideLocationRepository;
    private  InconsistencyReportRepository inconsistencyReportRepository;


    private RideServiceImpl rideService;

    private Passenger loggedPassenger;
    private Driver mockDriver;


    @BeforeMethod
    public void setUp(){
        rideRepository = mock(RideRepository.class);
        userRepository = mock(UserRepository.class);
        driverService = mock(DriverService.class);
        notificationService = mock(NotificationService.class);
        emailService = mock(EmailService.class);
        priceCalculationService = mock(PriceCalculationService.class);

        rideService = new RideServiceImpl(rideRepository, userRepository, driverService, priceCalculationService,rideLocationRepository, inconsistencyReportRepository, emailService,notificationService);
        loggedPassenger = new Passenger();
        loggedPassenger.setFirstName("Milos");
        loggedPassenger.setId(1L);

        mockDriver = new Driver();
        mockDriver.setFirstName("Milos");
        mockDriver.setId(1L);
        //MockitoAnnotations.openMocks(this);
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(loggedPassenger);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        lenient().when(priceCalculationService.calculatePrice(anyDouble(),any())).thenReturn(500.00);
    }
    @AfterMethod
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();

    }

    private RideCreateRequestDTO buildDTO(boolean rideNow, ArrayList<WayPointDTO> coordinates) {
        RideCreateRequestDTO dto = new RideCreateRequestDTO();
        dto.setRideNow(rideNow);
        dto.setCoordinates(coordinates);
        dto.setPassengerEmails(new ArrayList<>());
        dto.setVehicleType(VehicleType.STANDARD);
        dto.setBabySeat(false);
        dto.setPetFriendly(false);
        dto.setStartAddress("Adresa A");
        dto.setEndAddress("Adresa B");
        dto.setTotalDistance(10.0);
        dto.setEstimatedDuration(15.0);
        dto.setPassengerNumber(1);
        if (!rideNow) {
            dto.setRideTime(LocalDateTime.now().plusHours(2));
        }
        return dto;
    }
    private ArrayList<WayPointDTO> buildCoordinates(int waypointCount) {
        ArrayList<WayPointDTO> list = new ArrayList<>();

        WayPointDTO start = new WayPointDTO();
        start.setLocation(new GeoPoint(45.0, 19.0));
        start.setAddress("Start");
        start.setOrderIndex(0);
        list.add(start);

        for (int i = 1; i <= waypointCount; i++) {
            WayPointDTO wp = new WayPointDTO();
            wp.setLocation(new GeoPoint(45.0 + i, 19.0 + i));
            wp.setAddress("Waypoint " + i);
            wp.setOrderIndex(i);
            list.add(wp);
        }

        WayPointDTO end = new WayPointDTO();
        end.setLocation(new GeoPoint(46.0, 20.0));
        end.setAddress("End");
        end.setOrderIndex(waypointCount + 1);
        list.add(end);

        return list;
    }

    @Test
    public void createRide_nullCoordinates(){
        RideCreateRequestDTO dto= buildDTO(true,null);
        IllegalArgumentException exception= Assert.expectThrows(IllegalArgumentException.class,()->{
           rideService.createRide(dto);
        });

        verifyNoInteractions(rideRepository);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(driverService);
        verifyNoInteractions(notificationService);
        verifyNoInteractions(emailService);
        verifyNoInteractions(priceCalculationService);
    }
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void createRide_emptyCoordinates() {
        RideCreateRequestDTO dto = buildDTO(true, new ArrayList<>());
        rideService.createRide(dto);

        verifyNoInteractions(rideRepository);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(driverService);
        verifyNoInteractions(notificationService);
        verifyNoInteractions(emailService);
        verifyNoInteractions(priceCalculationService);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void createRide_oneCoordinate() {
        ArrayList<WayPointDTO> coords = new ArrayList<>();
        WayPointDTO wp = new WayPointDTO();
        wp.setLocation(new GeoPoint(45.0, 19.0));
        coords.add(wp);
        RideCreateRequestDTO dto = buildDTO(true, coords);
        rideService.createRide(dto);

        verifyNoInteractions(rideRepository);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(driverService);
        verifyNoInteractions(notificationService);
        verifyNoInteractions(emailService);
        verifyNoInteractions(priceCalculationService);
    }

    @Test
    public void createRide_rideNow_driverFound_success() {
        RideCreateRequestDTO dto = buildDTO(true, buildCoordinates(0));
        when(driverService.findAvailableDriver(any())).thenReturn(mockDriver);

        Ride result = rideService.createRide(dto);


        assertNotNull(result);
        verify(priceCalculationService).calculatePrice(anyDouble(),any());
        assertEquals(RideStatus.ACCEPTED, result.getStatus());
        assertEquals(result.getDriver(), mockDriver);
        assertFalse(mockDriver.isAvailable());
        verify(userRepository).save(mockDriver);
        verify(rideRepository).save(result);
        verify(notificationService).sendNotificationToUser(
                eq(mockDriver),
                eq(NotificationType.RIDE_CREATED),
                anyString(),
                eq(result)
        );
        verify(notificationService).sendNotificationToLinkedPassengers(
                eq(result),
                eq(NotificationType.RIDE_CREATED),
                anyString()
        );
        verifyNoInteractions(emailService);
    }

    @Test
    public void createRide_rideNow_noDriverAvailable() {
        RideCreateRequestDTO dto = buildDTO(true, buildCoordinates(0));
        when(driverService.findAvailableDriver(any())).thenReturn(null);

        Ride result = rideService.createRide(dto);

        assertNull(result);
        verify(notificationService).sendNotificationToUser(
                eq(loggedPassenger),
                eq(NotificationType.NO_AVAILABLE_DRIVER),
                anyString(),
                isNull()
        );
        verifyNoInteractions(rideRepository);
        verifyNoInteractions(userRepository);
        verifyNoMoreInteractions(notificationService);
        verify(priceCalculationService).calculatePrice(anyDouble(),any());
        verifyNoInteractions(emailService);
    }

    @Test
    public void createRide_scheduled_termAvailable_success() {
        RideCreateRequestDTO dto = buildDTO(false, buildCoordinates(0));
        when(driverService.checkAlreadyBookedDateTime(any())).thenReturn(true); //stavio sam da vraca true funkcija ako je moguce da se zakaze u tom terminu

        Ride result = rideService.createRide(dto);

        assertNotNull(result);
        verify(rideRepository).save(result);
        verify(notificationService).sendNotificationToLinkedPassengers(
                eq(result),
                eq(NotificationType.RIDE_CREATED),
                anyString()
        );
        verify(notificationService, never()).sendNotificationToUser(
                eq(mockDriver), any(), any(), any()
        );
        verify(priceCalculationService).calculatePrice(anyDouble(),any());
        verifyNoInteractions(emailService);
    }
    @Test
    public void createRide_scheduled_termNotAvailable() {
        RideCreateRequestDTO dto = buildDTO(false, buildCoordinates(0));
        when(driverService.checkAlreadyBookedDateTime(any())).thenReturn(false);// nije moguce zakazati u tom terminu zato je false

        Ride result = rideService.createRide(dto);

        assertNull(result);
        verify(notificationService).sendNotificationToUser(
                eq(loggedPassenger),
                eq(NotificationType.NO_AVAILABLE_DRIVER),
                anyString(),
                isNull()
        );
        verify(notificationService, never()).sendNotificationToUser(
                eq(mockDriver), any(), any(), any()
        );
        verifyNoInteractions(rideRepository);
        verifyNoMoreInteractions(notificationService);
        verify(priceCalculationService).calculatePrice(anyDouble(),any());
        verifyNoInteractions(emailService);
    }
    @Test
    public void createRide_withWaypoints_setsCorrectly() {
        RideCreateRequestDTO dto = buildDTO(true, buildCoordinates(2));
        when(driverService.findAvailableDriver(any())).thenReturn(mockDriver);

        Ride result = rideService.createRide(dto);

        assertNotNull(result);
        assertNotNull(result.getWayPoints());
        assertEquals(2, result.getWayPoints().size());
    }
    @Test
    public void createRide_withoutWaypoints_wayPointsNotSet() {
        RideCreateRequestDTO dto = buildDTO(true, buildCoordinates(0));
        when(driverService.findAvailableDriver(any())).thenReturn(mockDriver);

        Ride result = rideService.createRide(dto);

        assertNotNull(result);
        assertTrue(result.getWayPoints() == null || result.getWayPoints().isEmpty());
    }

    @Test
    public void createRide_waypointsHaveCorrectOrderIndex() {
        RideCreateRequestDTO dto = buildDTO(true, buildCoordinates(2));
        when(driverService.findAvailableDriver(any())).thenReturn(mockDriver);

        Ride result = rideService.createRide(dto);

        assertNotNull(result.getWayPoints());
        assertEquals(1, (int) result.getWayPoints().get(0).getOrderIndex());
        assertEquals(2, (int) result.getWayPoints().get(1).getOrderIndex());
    }

    @Test
    public void createRide_setsStartAndEndLocation() {
        ArrayList<WayPointDTO> coords = buildCoordinates(0);
        RideCreateRequestDTO dto = buildDTO(true, coords);
        when(driverService.findAvailableDriver(any())).thenReturn(mockDriver);

        Ride result = rideService.createRide(dto);

        assertEquals(result.getStartLocation(), coords.get(0).getLocation());
        assertEquals(result.getEndLocation(), coords.get(coords.size() - 1).getLocation());
    }

    @Test
    public void createRide_withLinkedPassengers_sendsEmailAndNotification() {
        Passenger linked = new Passenger();
        linked.setEmail("linked@test.com");

        RideCreateRequestDTO dto = buildDTO(true, buildCoordinates(0));
        ArrayList<String> linkedPassengers= new ArrayList<>();
        linkedPassengers.add("linked@test.com");
        dto.setPassengerEmails(linkedPassengers);

        when(userRepository.findByEmail("linked@test.com")).thenReturn(Optional.of(linked));
        when(driverService.findAvailableDriver(any())).thenReturn(mockDriver);

        Ride result = rideService.createRide(dto);

        assertNotNull(result);
        verify(emailService).sendRideNotificationEmail(eq("linked@test.com"), eq(result));
        verify(notificationService).sendNotificationToLinkedPassengers(
                eq(result),
                eq(NotificationType.RIDE_CREATED),
                anyString()
        );
    }
    @Test(expectedExceptions = InvalidUserRoleException.class)
    public void createRide_linkedPassengerIsDriver() {
        Driver nonPassenger = new Driver();
        nonPassenger.setEmail("driver2@test.com");

        RideCreateRequestDTO dto = buildDTO(true, buildCoordinates(0));
        ArrayList<String> passengerEmailsDto= new ArrayList<>();
        passengerEmailsDto.add("driver2@test.com");
        dto.setPassengerEmails(passengerEmailsDto);

        when(userRepository.findByEmail("driver2@test.com")).thenReturn(Optional.of(nonPassenger));
        when(driverService.findAvailableDriver(any())).thenReturn(mockDriver);

        rideService.createRide(dto);

        verifyNoInteractions(rideRepository);
        verify(userRepository).findByEmail(anyString());
        verifyNoInteractions(driverService);
        verifyNoInteractions(notificationService);
        verifyNoInteractions(emailService);
        verifyNoInteractions(priceCalculationService);
    }
    @Test(expectedExceptions = InvalidUserRoleException.class)
    public void createRide_linkedPassengerEmailNotFound_throwsException() {
        RideCreateRequestDTO dto = buildDTO(true, buildCoordinates(0));
        ArrayList<String> passengerEmailsDto= new ArrayList<>();
        passengerEmailsDto.add("nepostoji@test.com");
        dto.setPassengerEmails(passengerEmailsDto);

        when(userRepository.findByEmail("nepostoji@test.com")).thenReturn(Optional.empty());
        when(driverService.findAvailableDriver(any())).thenReturn(mockDriver);

        rideService.createRide(dto);

        verifyNoInteractions(rideRepository);
        verify(userRepository).findByEmail(anyString());
        verifyNoInteractions(driverService);
        verifyNoInteractions(notificationService);
        verifyNoInteractions(emailService);
        verifyNoInteractions(priceCalculationService);
    }

    @Test
    public void createRide_multipleLinkedPassengersl() {
        Passenger p1 = new Passenger(); p1.setEmail("p1@test.com");
        Passenger p2 = new Passenger(); p2.setEmail("p2@test.com");

        RideCreateRequestDTO dto = buildDTO(true, buildCoordinates(0));

        ArrayList<String> passengerEmailsDto= new ArrayList<>();
        passengerEmailsDto.add("p1@test.com");
        passengerEmailsDto.add("p2@test.com");
        dto.setPassengerEmails(passengerEmailsDto);

        when(userRepository.findByEmail("p1@test.com")).thenReturn(Optional.of(p1));
        when(userRepository.findByEmail("p2@test.com")).thenReturn(Optional.of(p2));
        when(driverService.findAvailableDriver(any())).thenReturn(mockDriver);

        Ride result = rideService.createRide(dto);

        verify(emailService).sendRideNotificationEmail(eq("p1@test.com"), eq(result));
        verify(emailService).sendRideNotificationEmail(eq("p2@test.com"), eq(result));
    }
}
