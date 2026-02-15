package com.ognjen.fleetforge.dtos.estimate;


public class RideEstimateResponseDTO {

    private double estimatedPrice;

    public RideEstimateResponseDTO() {}

    public RideEstimateResponseDTO(double estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }

    public double getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(double estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }
}