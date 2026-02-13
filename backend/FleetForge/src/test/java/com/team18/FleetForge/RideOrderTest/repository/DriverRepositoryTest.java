package com.team18.FleetForge.RideOrderTest.repository;

import com.team18.FleetForge.model.enums.Role;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.vehicles.Vehicle;
import com.team18.FleetForge.repository.users.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.testng.AssertJUnit.*;

@DataJpaTest
public class DriverRepositoryTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private DriverRepository driverRepository;

    private Driver createDriver(boolean active, boolean available) {
        Driver driver = new Driver();
        driver.setActive(active);
        driver.setAvailable(available);
        driver.setEmail(UUID.randomUUID().toString() + "@gmail.com");
        driver.setFirstName("aaa");
        driver.setLastName("aaa");
        driver.setPassword("123");
        driver.setAddress("adresa");
        driver.setPhoneNumber("1324324324");
        driver.setRole(Role.ROLE_DRIVER);
        Vehicle vehicle = new Vehicle();
        vehicle.setType(VehicleType.STANDARD);
        vehicle.setModel("Toyota");
        vehicle.setSpace(3);
        vehicle.setRegistrationNumber(UUID.randomUUID().toString());
        driver.setVehicle(vehicle);
        return driver;
    }

    @Test
    public void findByIsAvailableTrue(){
        Driver available=createDriver(true, true);
        Driver unavailable=createDriver(true,false);

        driverRepository.save(available);
        driverRepository.save(unavailable);

        List<Driver> drivers=driverRepository.findByIsAvailableTrue();

        assertTrue(drivers.contains(available));
        assertFalse(drivers.contains(unavailable));
    }

    @Test
    public void findByIsActiveTrue(){
        Driver active=createDriver(true, true);
        Driver notActive=createDriver(false,false);

        driverRepository.save(active);
        driverRepository.save(notActive);

        List<Driver> drivers=driverRepository.findByIsActiveTrue();

        assertTrue(drivers.contains(active));
        assertFalse(drivers.contains(notActive));
    }
}
