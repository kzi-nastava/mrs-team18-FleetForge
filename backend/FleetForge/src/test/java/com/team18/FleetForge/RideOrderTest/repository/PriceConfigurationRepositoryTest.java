package com.team18.FleetForge.RideOrderTest.repository;

import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.ride.PriceConfiguration;
import com.team18.FleetForge.repository.rides.PriceConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.testng.AssertJUnit.*;


@DataJpaTest
public class PriceConfigurationRepositoryTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private PriceConfigurationRepository priceConfigurationRepository;

    @Test
    public void findByVehicleTypeSuccessfull() {
        PriceConfiguration standard = new PriceConfiguration();
        standard.setVehicleType(VehicleType.STANDARD);
        standard.setBasePrice(100.0);
        standard.setPricePerKm(20.0);
        priceConfigurationRepository.save(standard);

        PriceConfiguration luxury = new PriceConfiguration();
        luxury.setVehicleType(VehicleType.LUXURY);
        luxury.setBasePrice(300.0);
        luxury.setPricePerKm(50.0);
        priceConfigurationRepository.save(luxury);

        Optional<PriceConfiguration> result = priceConfigurationRepository.findByVehicleType(VehicleType.LUXURY);

        assertTrue(result.isPresent());
        assertEquals(result.get().getBasePrice(), 300.0);
    }

    @Test
    public void findByVehicleType_notExists_returnsEmpty() {
        Optional<PriceConfiguration> result = priceConfigurationRepository.findByVehicleType(VehicleType.LUXURY);
        assertFalse(result.isPresent());
    }
}
