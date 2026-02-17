package com.ognjen.fleetforge.dtos.price;

import com.ognjen.fleetforge.enums.VehicleType;

public class PriceConfigurationDTO {
    private Long id;
    private VehicleType vehicleType;
    private Double basePrice;
    private Double pricePerKm;

    public PriceConfigurationDTO() {
    }

    public PriceConfigurationDTO(Long id, VehicleType vehicleType, Double basePrice, Double pricePerKm) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.basePrice = basePrice;
        this.pricePerKm = pricePerKm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public Double getPricePerKm() {
        return pricePerKm;
    }

    public void setPricePerKm(Double pricePerKm) {
        this.pricePerKm = pricePerKm;
    }
}

