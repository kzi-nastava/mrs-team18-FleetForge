package com.team18.FleetForge.RideOrderTest.repository;

import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.enums.Role;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.repository.rides.RideRepository;
import com.team18.FleetForge.repository.users.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.testng.AssertJUnit.*;

@DataJpaTest
public class RideRepositoryTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private DriverRepository driverRepository;


    private Ride createRide(Driver driver, RideStatus status){
        Ride ride= new Ride();
        ride.setDriver(driver);
        ride.setStatus(status);
        ride.setStartAddress("A");
        ride.setEndAddress("B");
        ride.setStartTime(LocalDateTime.now());
        return ride;
    }
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
    public void findAllByDriverAndStatus_returnsOnlyForSpecificDriver() {
        Driver driver= createDriver(1);
        Driver otherDriver = createDriver(2);
        driverRepository.save(driver);
        driverRepository.save(otherDriver);

        Ride rideForDriver = createRide(driver, RideStatus.ACCEPTED);
        Ride rideForDriver2 = createRide(driver, RideStatus.COMPLETED);
        Ride rideForOther = createRide(otherDriver, RideStatus.ACCEPTED);
        Ride rideForOther2 = createRide(otherDriver, RideStatus.COMPLETED);
        rideRepository.save(rideForDriver);
        rideRepository.save(rideForDriver2);
        rideRepository.save(rideForOther);
        rideRepository.save(rideForOther2);

        List<Ride> result = rideRepository.findAllByDriverAndStatus(driver, RideStatus.ACCEPTED);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getDriver(), driver);
    }
    @Test
    public void findAllByDriverAndStatus_noMatchingRides_returnsEmpty() {
        Driver driver= createDriver(1);
        driverRepository.save(driver);
        Ride inProgress = createRide(driver, RideStatus.IN_PROGRESS);
        rideRepository.save(inProgress);

        List<Ride> result = rideRepository.findAllByDriverAndStatus(driver, RideStatus.ACCEPTED);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllByDriverAndStatus_nullDriver_returnsRidesWithoutDriver() {
        Ride rideNoDriver = createRide(null, RideStatus.ACCEPTED);
        Driver driver= createDriver(1);
        driverRepository.save(driver);
        Ride rideWithDriver = createRide(driver, RideStatus.ACCEPTED);
        rideRepository.save(rideNoDriver);
        rideRepository.save(rideWithDriver);

        List<Ride> result = rideRepository.findAllByDriverAndStatus(null, RideStatus.ACCEPTED);

        assertEquals(result.size(), 1);
        assertNull(result.get(0).getDriver());
    }
}
