package com.team18.FleetForge.config;

import com.team18.FleetForge.model.GeoPoint;
import com.team18.FleetForge.model.Vehicle;
import com.team18.FleetForge.model.enums.Role;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.enums.RideCancellationRole;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.users.Admin;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final DriverRepository driverRepository;
    private final PassengerRepository passengerRepository;
    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        System.out.println("DataLoader: Starting to load test data...");

        Vehicle vehicle1 = new Vehicle(null, "Toyota Camry", VehicleType.STANDARD,
                "NS-123-AB", 4, false, false);
        Vehicle vehicle2 = new Vehicle(null, "Mercedes S-Class", VehicleType.LUXURY,
                "NS-456-CD", 4, true, false);

        Driver driver1 = new Driver();
        driver1.setEmail("driver1@test.com");
        driver1.setPassword(passwordEncoder.encode("123"));
        driver1.setFirstName("John");
        driver1.setLastName("Doe");
        driver1.setPhoneNumber("+381641234567");
        driver1.setAddress("Bulevar oslobođenja 10, Novi Sad");
        driver1.setProfilePicture("default.png");
        driver1.setVehicle(vehicle1);
        driver1.setActive(true);
        driver1.setAvailable(false);
        driver1.setCurrentLocation(new GeoPoint(45.2671, 19.8335));
        driver1.setRole(Role.ROLE_DRIVER);
        driver1.setActivated(true);
        driver1.setBlocked(false);

        Driver driver2 = new Driver();
        driver2.setEmail("driver2@test.com");
        driver2.setPassword(passwordEncoder.encode("123"));
        driver2.setFirstName("Jane");
        driver2.setLastName("Smith");
        driver2.setPhoneNumber("+381649876543");
        driver2.setAddress("Zmaj Jovina 5, Novi Sad");
        driver2.setProfilePicture("default.png");
        driver2.setVehicle(vehicle2);
        driver2.setActive(true);
        driver2.setAvailable(false);
        driver2.setCurrentLocation(new GeoPoint(45.2551, 19.8451));
        driver2.setRole(Role.ROLE_DRIVER);
        driver2.setActivated(true);
        driver2.setBlocked(false);

        driver1 = driverRepository.save(driver1);
        driverRepository.save(driver2);

        Passenger passenger1 = new Passenger();
        passenger1.setEmail("passenger1@test.com");
        passenger1.setPassword(passwordEncoder.encode("123"));
        passenger1.setFirstName("Marko");
        passenger1.setLastName("Marković");
        passenger1.setPhoneNumber("+381641111111");
        passenger1.setAddress("Narodnih heroja 15, Novi Sad");
        passenger1.setProfilePicture("default.png");
        passenger1.setRole(Role.ROLE_PASSENGER);
        passenger1.setActivated(true);
        passenger1.setBlocked(false);

        Passenger passenger2 = new Passenger();
        passenger2.setEmail("passenger2@test.com");
        passenger2.setPassword(passwordEncoder.encode("123"));
        passenger2.setFirstName("Ana");
        passenger2.setLastName("Anić");
        passenger2.setPhoneNumber("+381642222222");
        passenger2.setAddress("Dunavska 10, Novi Sad");
        passenger2.setProfilePicture("default.png");
        passenger2.setRole(Role.ROLE_PASSENGER);
        passenger2.setActivated(true);
        passenger2.setBlocked(false);

        Passenger passenger3 = new Passenger();
        passenger3.setEmail("mdamjanovic2004@gmail.com");
        passenger3.setPassword(passwordEncoder.encode("123"));
        passenger3.setFirstName("Jovana");
        passenger3.setLastName("Jovanović");
        passenger3.setPhoneNumber("+381643333333");
        passenger3.setAddress("Tolstojeva 5, Novi Sad");
        passenger3.setProfilePicture("default.png");
        passenger3.setRole(Role.ROLE_PASSENGER);
        passenger3.setActivated(true);
        passenger3.setBlocked(false);

        Passenger passenger4 = new Passenger();
        passenger4.setEmail("passenger4@test.com");
        passenger4.setPassword(passwordEncoder.encode("123"));
        passenger4.setFirstName("Petar");
        passenger4.setLastName("Petrović");
        passenger4.setPhoneNumber("+381644444444");
        passenger4.setAddress("Modene 20, Novi Sad");
        passenger4.setProfilePicture("default.png");
        passenger4.setRole(Role.ROLE_PASSENGER);
        passenger4.setActivated(true);
        passenger4.setBlocked(false);

        passenger1 = passengerRepository.save(passenger1);
        passenger2 = passengerRepository.save(passenger2);
        passenger3 = passengerRepository.save(passenger3);
        passenger4 = passengerRepository.save(passenger4);

        Admin admin = new Admin();
        admin.setEmail("admin@gmail.com");
        admin.setPassword(passwordEncoder.encode("123"));
        admin.setFirstName("System");
        admin.setLastName("Admin");
        admin.setPhoneNumber("+381640000000");
        admin.setAddress("Admin Office");
        admin.setRole(Role.ROLE_ADMIN);
        admin.setActivated(true);
        admin.setBlocked(false);

        userRepository.save(admin);

        System.out.println("DataLoader: Users, drivers, and passengers created");

        Ride ride1 = createRide(
                driver1, passenger1,
                new GeoPoint(45.2550, 19.8450), "Bulevar oslobođenja 46, Novi Sad",
                new GeoPoint(45.2671, 19.8335), "Trg slobode 1, Novi Sad",
                LocalDateTime.now().minusDays(1).withHour(12).withMinute(30),
                LocalDateTime.now().minusDays(1).withHour(12).withMinute(50),
                5.2, 20.0, 850.0,
                RideStatus.COMPLETED, false, null, null
        );
        rideRepository.save(ride1);

        Ride ride2 = createRide(
                driver1, passenger2,
                new GeoPoint(45.2600, 19.8400), "Futoška 10, Novi Sad",
                new GeoPoint(45.2500, 19.8600), "Petrovaradin Tvrđava",
                LocalDateTime.now().minusDays(14).withHour(18).withMinute(0),
                LocalDateTime.now().minusDays(14).withHour(18).withMinute(30),
                7.5, 30.0, 1120.0,
                RideStatus.COMPLETED, false, null, null
        );
        ride2.setLinkedPassengers(new ArrayList<>(List.of(passenger3)));
        rideRepository.save(ride2);

        Ride ride3 = createRide(
                driver1, passenger1,
                new GeoPoint(45.2671, 19.8335), "Trg slobode 1, Novi Sad",
                new GeoPoint(45.2550, 19.8450), "Bulevar oslobođenja 46, Novi Sad",
                LocalDateTime.now().minusDays(7).withHour(10).withMinute(15),
                LocalDateTime.now().minusDays(7).withHour(10).withMinute(35),
                5.2, 20.0, 850.0,
                RideStatus.COMPLETED, false, null, null
        );
        rideRepository.save(ride3);

        Ride ride4 = createRide(
                driver1, passenger2,
                new GeoPoint(45.2650, 19.8300), "Cara Dušana 10, Novi Sad",
                new GeoPoint(45.2700, 19.8500), "Liman 3",
                LocalDateTime.now().minusDays(5).withHour(16).withMinute(0),
                null, // No end time because cancelled
                8.0, 25.0, 1080.0,
                RideStatus.CANCELLED, false, RideCancellationRole.PASSENGER,
                "Changed plans, no longer need ride"
        );
        ride4.setCancelledAt(LocalDateTime.now().minusDays(5).withHour(15).withMinute(55));
        rideRepository.save(ride4);

        Ride ride5 = createRide(
                driver1, passenger4,
                new GeoPoint(45.2580, 19.8420), "Modene 10, Novi Sad",
                new GeoPoint(45.2620, 19.8380), "Dnevnik, Novi Sad",
                LocalDateTime.now().minusDays(1).withHour(9).withMinute(0),
                LocalDateTime.now().minusDays(1).withHour(9).withMinute(20),
                4.5, 20.0, 780.0,
                RideStatus.IN_PROGRESS, false, null, null
        );
        rideRepository.save(ride5);

        Ride ride6 = createRide(
                driver2, passenger3,
                new GeoPoint(45.2600, 19.8400), "Novi Sad Centar",
                new GeoPoint(45.2500, 19.8600), "Petrovaradin",
                LocalDateTime.now().minusDays(3).withHour(20).withMinute(0),
                LocalDateTime.now().minusDays(3).withHour(20).withMinute(25),
                6.0, 25.0, 960.0,
                RideStatus.IN_PROGRESS, true, null, null
        );
        ride6.setPanicActivatedAt(LocalDateTime.now().minusDays(3).withHour(20).withMinute(10));
        rideRepository.save(ride6);

    }

    private Ride createRide(Driver driver, Passenger passenger,
                            GeoPoint startLocation, String startAddress,
                            GeoPoint endLocation, String endAddress,
                            LocalDateTime startTime, LocalDateTime endTime,
                            Double distance, Double duration, Double cost,
                            RideStatus status, Boolean panicActivated,
                            RideCancellationRole cancelledBy, String cancellationReason) {

        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setPassenger(passenger);
        ride.setStartLocation(startLocation);
        ride.setStartAddress(startAddress);
        ride.setEndLocation(endLocation);
        ride.setEndAddress(endAddress);
        ride.setStartTime(startTime);
        ride.setEndTime(endTime);
        ride.setTotalDistance(distance);
        ride.setEstimatedDuration(duration);
        ride.setTotalCost(cost);
        ride.setStatus(status);
        ride.setPanicActivated(panicActivated);
        ride.setCancelledBy(cancelledBy);
        ride.setCancellationReason(cancellationReason);
        ride.setLinkedPassengers(new ArrayList<>());
        ride.setWayPoints(new ArrayList<>());

        return ride;
    }
}