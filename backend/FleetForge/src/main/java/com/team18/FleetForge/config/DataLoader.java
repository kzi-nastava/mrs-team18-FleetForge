package com.team18.FleetForge.config;

import com.team18.FleetForge.model.GeoPoint;
import com.team18.FleetForge.model.Vehicle;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final DriverRepository driverRepository;

    @Override
    public void run(String... args) {
        Vehicle vehicle1 = new Vehicle(null, "Toyota Camry", VehicleType.STANDARD,
                "NS-123-AB", 4, false, false);
        Vehicle vehicle2 = new Vehicle(null, "Mercedes S-Class", VehicleType.LUXURY,
                "NS-456-CD", 4, true, false);

        Driver driver1 = new Driver();
        driver1.setEmail("driver1@test.com");
        driver1.setPassword("password");
        driver1.setFirstName("John");
        driver1.setLastName("Doe");
        driver1.setVehicle(vehicle1);
        driver1.setActive(true);
        driver1.setAvailable(true);
        driver1.setCurrentLocation(new GeoPoint(45.2671, 19.8335));

        Driver driver2 = new Driver();
        driver2.setEmail("driver2@test.com");
        driver2.setPassword("password");
        driver2.setFirstName("Jane");
        driver2.setLastName("Smith");
        driver2.setVehicle(vehicle2);
        driver2.setActive(true);
        driver2.setAvailable(false);
        driver2.setCurrentLocation(new GeoPoint(45.2551, 19.8451));

        driverRepository.save(driver1);
        driverRepository.save(driver2);
    }
}