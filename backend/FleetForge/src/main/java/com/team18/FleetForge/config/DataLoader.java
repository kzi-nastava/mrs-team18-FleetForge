package com.team18.FleetForge.config;

import com.team18.FleetForge.model.GeoPoint;
import com.team18.FleetForge.model.Vehicle;
import com.team18.FleetForge.model.enums.Role;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.users.Admin;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.repository.DriverRepository;
import com.team18.FleetForge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Injected encoder from SecurityConfig

    @Override
    public void run(String... args) {
        Vehicle vehicle1 = new Vehicle(null, "Toyota Camry", VehicleType.STANDARD,
                "NS-123-AB", 4, false, false);
        Vehicle vehicle2 = new Vehicle(null, "Mercedes S-Class", VehicleType.LUXURY,
                "NS-456-CD", 4, true, false);

        Driver driver1 = new Driver();
        driver1.setEmail("driver1@test.com");
        driver1.setPassword(passwordEncoder.encode("driver123"));
        driver1.setFirstName("John");
        driver1.setLastName("Doe");
        driver1.setVehicle(vehicle1);
        driver1.setActive(true);
        driver1.setAvailable(true);
        driver1.setCurrentLocation(new GeoPoint(45.2671, 19.8335));
        driver1.setRole(Role.ROLE_DRIVER);
        driver1.setActivated(true);

        Driver driver2 = new Driver();
        driver2.setEmail("driver2@test.com");
        driver2.setPassword("password");
        driver2.setFirstName("Jane");
        driver2.setLastName("Smith");
        driver2.setVehicle(vehicle2);
        driver2.setActive(true);
        driver2.setAvailable(false);
        driver2.setCurrentLocation(new GeoPoint(45.2551, 19.8451));
        driver2.setRole(Role.ROLE_DRIVER);
        driver2.setActivated(true);

        Admin admin = new Admin();
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFirstName("System");
        admin.setLastName("Admin");
        admin.setRole(Role.ROLE_ADMIN);
        admin.setActivated(true);

        Passenger passenger = new Passenger();
        passenger.setEmail("passenger@test.com");
        passenger.setPassword(passwordEncoder.encode("passenger123"));
        passenger.setFirstName("Bob");
        passenger.setLastName("User");
        passenger.setRole(Role.ROLE_PASSENGER);
        passenger.setActivated(true);
        admin.setActivated(true);

        driverRepository.save(driver1);
        driverRepository.save(driver2);
        userRepository.save(admin);
        userRepository.save(passenger);

        System.out.println("DataLoader: Test users created");
    }
}