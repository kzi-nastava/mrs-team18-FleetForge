package com.ognjen.fleetforge.dtos.ride;

import com.ognjen.fleetforge.enums.RideStatus;

public class FinishRideResponseDTO {

    private Long rideId;
    private RideStatus status;
    private String endTime;
    private Double totalCost;
    private String message;
    private boolean driverAvailable;
    private RideTrackingDTO nextRide;

    public FinishRideResponseDTO() {
    }

    public FinishRideResponseDTO(Long rideId, RideStatus status, String endTime,
                                 Double totalCost, String message, boolean driverAvailable,
                                 RideTrackingDTO nextRide) {
        this.rideId = rideId;
        this.status = status;
        this.endTime = endTime;
        this.totalCost = totalCost;
        this.message = message;
        this.driverAvailable = driverAvailable;
        this.nextRide = nextRide;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDriverAvailable() {
        return driverAvailable;
    }

    public void setDriverAvailable(boolean driverAvailable) {
        this.driverAvailable = driverAvailable;
    }

    public RideTrackingDTO getNextRide() {
        return nextRide;
    }

    public void setNextRide(RideTrackingDTO nextRide) {
        this.nextRide = nextRide;
    }

    @Override
    public String toString() {
        return "FinishRideResponseDTO{" +
                "rideId=" + rideId +
                ", status=" + status +
                ", endTime='" + endTime + '\'' +
                ", totalCost=" + totalCost +
                ", message='" + message + '\'' +
                ", driverAvailable=" + driverAvailable +
                ", nextRide=" + nextRide +
                '}';
    }
}