package com.team18.FleetForge.service;

import com.team18.FleetForge.model.enums.VehicleType;
import org.springframework.stereotype.Service;

@Service
public class PriceCalculationService {

    private static final double PRICE_PER_KM = 120;

    public double calculatePrice(double distanceKm, VehicleType vehicleType) {
        return vehicleType.getBasePrice() + distanceKm * PRICE_PER_KM;
    }
}
