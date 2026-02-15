package com.ognjen.fleetforge.dtos.passenger;

import java.util.List;

public class PassengerRideDetailsDto {
    private Long id;
    private String startAddress;
    private String endAddress;
    private Location startLocation;
    private Location endLocation;
    private List<Location> wayPoints;
    private String startTime;
    private String endTime;
    private double totalDistance;
    private int estimatedDuration;
    private double totalCost;
    private String status;
    private String vehicleType;
    private DriverMiniDto driver;
    private boolean hasInconsistencies;

    public Location getStartLocation() {
        return startLocation;
    }

    public Location getEndLocationLocation() {
        return endLocation;
    }

    public static class Location {
        public double latitude;
        public double longitude;
    }

    public static class DriverMiniDto {
        public String firstName;
        public String lastName;
        public String phoneNumber;
        public String profileImage;
    }

    public Long getId() { return id; }
    public String getStartAddress() { return startAddress; }
    public String getEndAddress() { return endAddress; }
    public List<Location> getWayPoints() { return wayPoints; }
    public DriverMiniDto getDriver() { return driver; }
    public double getTotalDistance() { return totalDistance; }
    public double getTotalCost() { return totalCost; }
    public String getVehicleType() { return vehicleType; }
}