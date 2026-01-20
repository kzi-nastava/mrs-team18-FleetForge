package com.team18.FleetForge.model.enums;

import lombok.Getter;

@Getter
public enum VehicleType {

    STANDARD(300),
    LUXURY(500),
    VAN(600);

    private final double basePrice;

    VehicleType(double basePrice) {
        this.basePrice = basePrice;
    }

}
