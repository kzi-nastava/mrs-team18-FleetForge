package com.ognjen.fleetforge.dtos.passenger;


public class PassengerRideHistoryDto {
    private Long rideId;
    private String startAddress;
    private String endAddress;
    private String startTime;
    private String endTime; // Nullable
    private String status; // 'COMPLETED' | 'CANCELLED' | 'IN_PROGRESS'
    private Location startLocation;
    private Location endLocation;
    private Double vehicleRating; // Nullable
    private Double driverRating; // Nullable
    private Double averageReview; // Nullable

    // Static Inner Class for Coordinates
    public static class Location {
        private double latitude;
        private double longitude;

        public Location() {}

        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
    }

    public PassengerRideHistoryDto() {}

    // Getters and Setters
    public Long getRideId() { return rideId; }
    public void setRideId(Long rideId) { this.rideId = rideId; }

    public String getStartAddress() { return startAddress; }
    public void setStartAddress(String startAddress) { this.startAddress = startAddress; }

    public String getEndAddress() { return endAddress; }
    public void setEndAddress(String endAddress) { this.endAddress = endAddress; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Location getStartLocation() { return startLocation; }
    public void setStartLocation(Location startLocation) { this.startLocation = startLocation; }

    public Location getEndLocation() { return endLocation; }
    public void setEndLocation(Location endLocation) { this.endLocation = endLocation; }

    public Double getVehicleRating() { return vehicleRating; }
    public void setVehicleRating(Double vehicleRating) { this.vehicleRating = vehicleRating; }

    public Double getDriverRating() { return driverRating; }
    public void setDriverRating(Double driverRating) { this.driverRating = driverRating; }

    public Double getAverageReview() { return averageReview; }
    public void setAverageReview(Double averageReview) { this.averageReview = averageReview; }
}