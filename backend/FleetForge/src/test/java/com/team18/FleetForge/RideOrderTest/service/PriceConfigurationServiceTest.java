package com.team18.FleetForge.RideOrderTest.service;

import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.ride.PriceConfiguration;
import com.team18.FleetForge.repository.rides.PriceConfigurationRepository;
import com.team18.FleetForge.service.rides.PriceConfigurationService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

public class PriceConfigurationServiceTest {


    private PriceConfigurationRepository priceConfigurationRepository;


    private PriceConfigurationService priceConfigurationService;

    @BeforeMethod
    public void setUp() {

        priceConfigurationRepository= mock(PriceConfigurationRepository.class);
        priceConfigurationService= new PriceConfigurationService(priceConfigurationRepository);
    }


    @Test
    public void getBasePriceForVehicleType_configExists_returnsFromDb() {
        PriceConfiguration config = new PriceConfiguration();
        config.setBasePrice(200.0);

        when(priceConfigurationRepository.findByVehicleType(VehicleType.STANDARD)).thenReturn(Optional.of(config));

        double result = priceConfigurationService.getBasePriceForVehicleType(VehicleType.STANDARD);

        assertEquals(result, 200.0);
    }

    @Test
    public void getBasePriceForVehicleType_configNotFound_returnsFallback() {
        when(priceConfigurationRepository.findByVehicleType(VehicleType.STANDARD)).thenReturn(Optional.empty());

        double result = priceConfigurationService.getBasePriceForVehicleType(VehicleType.STANDARD);

        assertEquals(result, VehicleType.STANDARD.getBasePrice());
    }
}
