package com.ognjen.fleetforge.dtos.ride;

import com.ognjen.fleetforge.enums.VehicleType;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class RideCreateRequestDTO {
    private ArrayList<WaypointRideCreateDTO> coordinates;
    private int passengerNumber;
    private LocalDateTime rideTime;
    private boolean rideNow;
    private ArrayList<String> passengerEmails;
    private VehicleType vehicleType;
    private boolean babySeat;
    private boolean petFriendly;
    private String startAddress;
    private String endAddress;
    private Double totalDistance;
    private Double estimatedDuration;

    public ArrayList<WaypointRideCreateDTO> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<WaypointRideCreateDTO> coordinates) {
        this.coordinates = coordinates;
    }

    public int getPassengerNumber() {
        return passengerNumber;
    }

    public void setPassengerNumber(int passengerNumber) {
        this.passengerNumber = passengerNumber;
    }

    public LocalDateTime getRideTime() {
        return rideTime;
    }

    public void setRideTime(LocalDateTime rideTime) {
        this.rideTime = rideTime;
    }

    public boolean isRideNow() {
        return rideNow;
    }

    public void setRideNow(boolean rideNow) {
        this.rideNow = rideNow;
    }

    public ArrayList<String> getPassengerEmails() {
        return passengerEmails;
    }

    public void setPassengerEmails(ArrayList<String> passengerEmails) {
        this.passengerEmails = passengerEmails;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public boolean isBabySeat() {
        return babySeat;
    }

    public void setBabySeat(boolean babySeat) {
        this.babySeat = babySeat;
    }

    public boolean isPetFriendly() {
        return petFriendly;
    }

    public void setPetFriendly(boolean petFriendly) {
        this.petFriendly = petFriendly;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Double getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(Double estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }
}
