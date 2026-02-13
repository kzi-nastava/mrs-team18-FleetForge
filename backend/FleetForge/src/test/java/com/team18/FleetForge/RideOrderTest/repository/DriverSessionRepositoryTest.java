package com.team18.FleetForge.RideOrderTest.repository;

import com.team18.FleetForge.model.enums.Role;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.DriverSession;
import com.team18.FleetForge.model.vehicles.Vehicle;
import com.team18.FleetForge.repository.users.DriverRepository;
import com.team18.FleetForge.repository.users.DriverSessionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.testng.AssertJUnit.*;

@DataJpaTest
public class DriverSessionRepositoryTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private DriverSessionRepo driverSessionRepo;

    @Autowired
    private DriverRepository driverRepository;

    private Driver createDriver(int i) {
        Driver driver = new Driver();
        driver.setEmail(i + "@gmail.com");
        driver.setFirstName("aaa");
        driver.setLastName("aaa");
        driver.setPassword("123");
        driver.setAddress("adresa");
        driver.setPhoneNumber("1324324324");
        driver.setRole(Role.ROLE_DRIVER);
        return driver;
    }

    @Test
    public void findByDriver(){
        List<Driver> drivers= new ArrayList<>();
        for(int i=0;i<2;i++){
            Driver driver=createDriver(i);
            driverRepository.save(driver);
            drivers.add(driver);
        }

        DriverSession session1 = new DriverSession();
        session1.setDriver(drivers.get(0));
        session1.setStartedAt(LocalDateTime.now().minusHours(1));
        driverSessionRepo.save(session1);

        DriverSession session2 = new DriverSession();
        session2.setDriver(drivers.get(1));
        session2.setStartedAt(LocalDateTime.now().minusHours(1));
        driverSessionRepo.save(session2);

        List<DriverSession> result = driverSessionRepo.findByDriver(drivers.get(0));

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getDriver(), drivers.get(0));
    }
    @Test
    public void findByDriverNoSessions() {
        Driver driver = createDriver(1);
        driverRepository.save(driver);

        List<DriverSession> result = driverSessionRepo.findByDriver(driver);

        assertTrue(result.isEmpty());
    }
}
