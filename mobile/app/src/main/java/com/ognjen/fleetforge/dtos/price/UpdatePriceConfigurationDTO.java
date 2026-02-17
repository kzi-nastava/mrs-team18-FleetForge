package com.ognjen.fleetforge.dtos.price;

public class UpdatePriceConfigurationDTO {
    private Double basePrice;
    private Double pricePerKm;

    public UpdatePriceConfigurationDTO() {
    }

    public UpdatePriceConfigurationDTO(Double basePrice, Double pricePerKm) {
        this.basePrice = basePrice;
        this.pricePerKm = pricePerKm;
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

