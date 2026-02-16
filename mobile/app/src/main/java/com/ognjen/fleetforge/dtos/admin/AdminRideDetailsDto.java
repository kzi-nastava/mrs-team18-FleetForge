package com.ognjen.fleetforge.dtos.admin;

import com.ognjen.fleetforge.dtos.ride.InconsistencyReportDto;

import java.util.List;

public class AdminRideDetailsDto {
    private Long id;
    private String startAddress;
    private String endAddress;
    private Location startLocation;
    private Location endLocation;
    private List<Location> wayPoints;
    private String startTime;
    private String endTime;
    private Double totalDistance;
    private Integer estimatedDuration;
    private Double totalCost;
    private String status;
    private String vehicleType;
    private String cancelledBy; // 'DRIVER' | 'PASSENGER' | 'SYSTEM'
    private String cancellationReason;
    private DriverInfo driver;
    private PassengerInfo mainPassenger;
    private List<PassengerInfo> linkedPassengers;
    private Ratings ratings;
    private Boolean hasInconsistencies;
    private List<InconsistencyReportDto> inconsistencies;

    // Static Inner Class for Location
    public static class Location {
        private double latitude;
        private double longitude;

        public Location() {}

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }

    // Static Inner Class for DriverInfo
    public static class DriverInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String profileImage;

        public DriverInfo() {}

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
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

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }
    }

    // Static Inner Class for PassengerInfo
    public static class PassengerInfo {
        private Long id;
        private String firstName;
        private String lastName;

        public PassengerInfo() {}

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
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
    }

    // Static Inner Class for Ratings
    public static class Ratings {
        private Double driverRating;
        private Double vehicleRating;

        public Ratings() {}

        public Double getDriverRating() {
            return driverRating;
        }

        public void setDriverRating(Double driverRating) {
            this.driverRating = driverRating;
        }

        public Double getVehicleRating() {
            return vehicleRating;
        }

        public void setVehicleRating(Double vehicleRating) {
            this.vehicleRating = vehicleRating;
        }
    }

    public AdminRideDetailsDto() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }

    public List<Location> getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(List<Location> wayPoints) {
        this.wayPoints = wayPoints;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Integer getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(Integer estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public DriverInfo getDriver() {
        return driver;
    }

    public void setDriver(DriverInfo driver) {
        this.driver = driver;
    }

    public PassengerInfo getMainPassenger() {
        return mainPassenger;
    }

    public void setMainPassenger(PassengerInfo mainPassenger) {
        this.mainPassenger = mainPassenger;
    }

    public List<PassengerInfo> getLinkedPassengers() {
        return linkedPassengers;
    }

    public void setLinkedPassengers(List<PassengerInfo> linkedPassengers) {
        this.linkedPassengers = linkedPassengers;
    }

    public Ratings getRatings() {
        return ratings;
    }

    public void setRatings(Ratings ratings) {
        this.ratings = ratings;
    }

    public Boolean getHasInconsistencies() {
        return hasInconsistencies;
    }

    public void setHasInconsistencies(Boolean hasInconsistencies) {
        this.hasInconsistencies = hasInconsistencies;
    }

    public List<InconsistencyReportDto> getInconsistencies() {
        return inconsistencies;
    }

    public void setInconsistencies(List<InconsistencyReportDto> inconsistencies) {
        this.inconsistencies = inconsistencies;
    }
}