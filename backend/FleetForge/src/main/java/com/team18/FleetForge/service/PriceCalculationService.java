package com.team18.FleetForge.service;

import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.service.rides.PriceConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PriceCalculationService {

    private final PriceConfigurationService priceConfigurationService;

    public double calculatePrice(double distanceKm, VehicleType vehicleType) {
        double basePrice = priceConfigurationService.getBasePriceForVehicleType(vehicleType);
        double pricePerKm = priceConfigurationService.getPricePerKmForVehicleType(vehicleType);

        return basePrice + (distanceKm * pricePerKm);
    }
}