package com.ognjen.fleetforge.dtos.ride;

import com.ognjen.fleetforge.dtos.driver.DriverInfoDTO;
import com.ognjen.fleetforge.dtos.passenger.PassengerInfoDTO;
import com.ognjen.fleetforge.model.GeoPoint;

public class RideTrackingDTO {
    private Long rideId;
    private String status;
    private GeoPoint currentLocation;
    private Integer estimatedArrivalMinutes;
    private RouteInfoDTO route;
    private DriverInfoDTO driver;
    private PassengerInfoDTO passenger;
    private Boolean panicActivated;

    public RideTrackingDTO() {
    }

    public RideTrackingDTO(Long rideId, String status, GeoPoint currentLocation,
                           Integer estimatedArrivalMinutes, RouteInfoDTO route,
                           DriverInfoDTO driver, PassengerInfoDTO passenger, Boolean panicActivated) {
        this.rideId = rideId;
        this.status = status;
        this.currentLocation = currentLocation;
        this.estimatedArrivalMinutes = estimatedArrivalMinutes;
        this.route = route;
        this.driver = driver;
        this.passenger = passenger;
        this.panicActivated = panicActivated;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public GeoPoint getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(GeoPoint currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Integer getEstimatedArrivalMinutes() {
        return estimatedArrivalMinutes;
    }

    public void setEstimatedArrivalMinutes(Integer estimatedArrivalMinutes) {
        this.estimatedArrivalMinutes = estimatedArrivalMinutes;
    }

    public RouteInfoDTO getRoute() {
        return route;
    }

    public void setRoute(RouteInfoDTO route) {
        this.route = route;
    }

    public DriverInfoDTO getDriver() {
        return driver;
    }

    public void setDriver(DriverInfoDTO driver) {
        this.driver = driver;
    }

    public PassengerInfoDTO getPassenger() {
        return passenger;
    }

    public void setPassenger(PassengerInfoDTO passenger) {
        this.passenger = passenger;
    }

    public Boolean getPanicActivated() {
        return panicActivated;
    }

    public void setPanicActivated(Boolean panicActivated) {
        this.panicActivated = panicActivated;
    }

    @Override
    public String toString() {
        return "RideTrackingDTO{" +
                "rideId=" + rideId +
                ", status='" + status + '\'' +
                ", currentLocation=" + currentLocation +
                ", estimatedArrivalMinutes=" + estimatedArrivalMinutes +
                ", route=" + route +
                ", driver=" + driver +
                ", passenger=" + passenger +
                ", panicActivated=" + panicActivated +
                '}';
    }
}