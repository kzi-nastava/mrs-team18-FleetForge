package com.ognjen.fleetforge.dtos.estimate;


public class RideEstimateRequestDTO {
    private double distanceKm;
    private String vehicleType;

    public RideEstimateRequestDTO() {}

    public RideEstimateRequestDTO(double distanceKm, String vehicleType) {
        this.distanceKm = distanceKm;
        this.vehicleType = vehicleType;
    }

    // Getters and Setters
    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
}