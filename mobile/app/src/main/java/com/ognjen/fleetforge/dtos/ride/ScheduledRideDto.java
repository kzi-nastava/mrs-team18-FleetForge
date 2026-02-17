package com.ognjen.fleetforge.dtos.ride;

public class ScheduledRideDto {
    private Long id;
    private String pickup;
    private String dropoff;
    private String scheduledTime;
    private Double estimatedCost;
    private String status;

    // Getters and Setters
    public Long getId() { return id; }
    public String getPickup() { return pickup; }
    public String getDropoff() { return dropoff; }
    public String getScheduledTime() { return scheduledTime; }
    public Double getEstimatedCost() { return estimatedCost; }
    public String getStatus() { return status; }
}
