package com.ognjen.fleetforge.dtos.admin;

import com.ognjen.fleetforge.model.GeoPoint;
import com.ognjen.fleetforge.model.VehicleType;

import java.util.List;

public class ActiveRideDetailsDTO {
    private Long rideId;
    private String driverFirstName;
    private String driverLastName;
    private String driverPhoneNumber;
    private String driverProfileImage;
    private GeoPoint currentLocation;
    private GeoPoint startLocation;
    private String startAddress;
    private GeoPoint endLocation;
    private String endAddress;
    private VehicleType vehicleType;
    private String startTime;
    private Boolean panicActivated;
    private String panicActivatedAt;
    private List<PassengerDTO> passengers;

    public ActiveRideDetailsDTO() {
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getDriverFirstName() {
        return driverFirstName;
    }

    public void setDriverFirstName(String driverFirstName) {
        this.driverFirstName = driverFirstName;
    }

    public String getDriverLastName() {
        return driverLastName;
    }

    public void setDriverLastName(String driverLastName) {
        this.driverLastName = driverLastName;
    }

    public String getDriverPhoneNumber() {
        return driverPhoneNumber;
    }

    public void setDriverPhoneNumber(String driverPhoneNumber) {
        this.driverPhoneNumber = driverPhoneNumber;
    }

    public String getDriverProfileImage() {
        return driverProfileImage;
    }

    public void setDriverProfileImage(String driverProfileImage) {
        this.driverProfileImage = driverProfileImage;
    }

    public GeoPoint getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(GeoPoint currentLocation) {
        this.currentLocation = currentLocation;
    }

    public GeoPoint getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(GeoPoint startLocation) {
        this.startLocation = startLocation;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public GeoPoint getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(GeoPoint endLocation) {
        this.endLocation = endLocation;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Boolean getPanicActivated() {
        return panicActivated;
    }

    public void setPanicActivated(Boolean panicActivated) {
        this.panicActivated = panicActivated;
    }

    public String getPanicActivatedAt() {
        return panicActivatedAt;
    }

    public void setPanicActivatedAt(String panicActivatedAt) {
        this.panicActivatedAt = panicActivatedAt;
    }

    public List<PassengerDTO> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerDTO> passengers) {
        this.passengers = passengers;
    }

    public static class PassengerDTO {
        private String firstName;
        private String lastName;
        private String email;

        public PassengerDTO() {
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}

