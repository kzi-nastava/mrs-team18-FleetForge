package com.ognjen.fleetforge.model;

public class Ride {
    private String id;
    private String passengerName;
    private String passengerId;
    private String pickupAddress;
    private String dropoffAddress;
    private String rideDate;
    private double totalCost;
    private String cancellationStatus; // "Not cancelled", "By passenger", "By driver"
    private boolean panicActivated;
    private float rating; // 0.0 to 5.0

    public Ride(String id, String passengerName, String passengerId,
                String pickupAddress, String dropoffAddress, String rideDate,
                double totalCost, String cancellationStatus,
                boolean panicActivated, float rating) {
        this.id = id;
        this.passengerName = passengerName;
        this.passengerId = passengerId;
        this.pickupAddress = pickupAddress;
        this.dropoffAddress = dropoffAddress;
        this.rideDate = rideDate;
        this.totalCost = totalCost;
        this.cancellationStatus = cancellationStatus;
        this.panicActivated = panicActivated;
        this.rating = rating;
    }

    public String getId() { return id; }
    public String getPassengerName() { return passengerName; }
    public String getPassengerId() { return passengerId; }
    public String getPickupAddress() { return pickupAddress; }
    public String getDropoffAddress() { return dropoffAddress; }
    public String getRideDate() { return rideDate; }
    public double getTotalCost() { return totalCost; }
    public String getCancellationStatus() { return cancellationStatus; }
    public boolean isPanicActivated() { return panicActivated; }
    public float getRating() { return rating; }

    public void setId(String id) { this.id = id; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public void setPassengerId(String passengerId) { this.passengerId = passengerId; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
    public void setDropoffAddress(String dropoffAddress) { this.dropoffAddress = dropoffAddress; }
    public void setRideDate(String rideDate) { this.rideDate = rideDate; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    public void setCancellationStatus(String cancellationStatus) { this.cancellationStatus = cancellationStatus; }
    public void setPanicActivated(boolean panicActivated) { this.panicActivated = panicActivated; }
    public void setRating(float rating) { this.rating = rating; }
}