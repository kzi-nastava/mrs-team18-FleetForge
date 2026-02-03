package com.ognjen.fleetforge.model;

public class VehicleLocation {
    private Long vehicleId;
    private String model;
    private VehicleType vehicleType;
    private GeoPoint currentLocation;
    private Boolean isAvailable;
    private Boolean isActive;

    public VehicleLocation() {
    }

    public VehicleLocation(Long vehicleId, String model, VehicleType vehicleType,
                              GeoPoint currentLocation, Boolean isAvailable, Boolean isActive) {
        this.vehicleId = vehicleId;
        this.model = model;
        this.vehicleType = vehicleType;
        this.currentLocation = currentLocation;
        this.isAvailable = isAvailable;
        this.isActive = isActive;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public GeoPoint getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(GeoPoint currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean available) {
        isAvailable = available;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}