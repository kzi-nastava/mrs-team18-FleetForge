package com.team18.FleetForge.RideOrderTest.service;

import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.service.PriceCalculationService;
import com.team18.FleetForge.service.rides.PriceConfigurationService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.*;

public class PriceCalculationServiceTest {
    @Mock
    private PriceConfigurationService priceConfigurationService;
    @InjectMocks
    private PriceCalculationService priceCalculationService;

    @BeforeMethod
    public void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    public void calculatePrice_correctFormula() {
        when(priceConfigurationService.getBasePriceForVehicleType(VehicleType.STANDARD)).thenReturn(100.0);
        when(priceConfigurationService.getPricePerKmForVehicleType(VehicleType.STANDARD)).thenReturn(50.0);

        double result = priceCalculationService.calculatePrice(10.0, VehicleType.STANDARD);

        assertEquals(result, 600.0);
    }
}
