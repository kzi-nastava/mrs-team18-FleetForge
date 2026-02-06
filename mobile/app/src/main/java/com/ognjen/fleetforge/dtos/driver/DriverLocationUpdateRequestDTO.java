package com.ognjen.fleetforge.dtos.driver;

import com.ognjen.fleetforge.model.GeoPoint;

public class DriverLocationUpdateRequestDTO {
    private GeoPoint currentLocation;

    public DriverLocationUpdateRequestDTO() {
    }

    public DriverLocationUpdateRequestDTO(GeoPoint currentLocation) {
        this.currentLocation = currentLocation;
    }

    public GeoPoint getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(GeoPoint currentLocation) {
        this.currentLocation = currentLocation;
    }

    @Override
    public String toString() {
        return "DriverLocationUpdateRequestDTO{" +
                "currentLocation=" + currentLocation +
                '}';
    }
}