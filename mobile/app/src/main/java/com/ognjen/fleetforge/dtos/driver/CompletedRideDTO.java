package com.ognjen.fleetforge.dtos.driver;

import java.util.List;

public class CompletedRideDTO {
    private String passengerName;
    private Long id;
    private String pickupAddress;
    private String dropoffAddress;
    private String rideDate;
    private Double totalCost;
    private String cancelledBy;
    private Boolean panicActivation;
    private Integer feedback;
    private List<String> linkedPassengers;
    private List<Double> pickupCoords;
    private List<Double> dropoffCoords;
    private List<List<Double>> waypoints;

    public CompletedRideDTO() {
    }

    // Getters and Setters
    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getDropoffAddress() {
        return dropoffAddress;
    }

    public void setDropoffAddress(String dropoffAddress) {
        this.dropoffAddress = dropoffAddress;
    }

    public String getRideDate() {
        return rideDate;
    }

    public void setRideDate(String rideDate) {
        this.rideDate = rideDate;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public Boolean getPanicActivation() {
        return panicActivation;
    }

    public void setPanicActivation(Boolean panicActivation) {
        this.panicActivation = panicActivation;
    }

    public Integer getFeedback() {
        return feedback;
    }

    public void setFeedback(Integer feedback) {
        this.feedback = feedback;
    }

    public List<String> getLinkedPassengers() {
        return linkedPassengers;
    }

    public void setLinkedPassengers(List<String> linkedPassengers) {
        this.linkedPassengers = linkedPassengers;
    }

    public List<Double> getPickupCoords() {
        return pickupCoords;
    }

    public void setPickupCoords(List<Double> pickupCoords) {
        this.pickupCoords = pickupCoords;
    }

    public List<Double> getDropoffCoords() {
        return dropoffCoords;
    }

    public void setDropoffCoords(List<Double> dropoffCoords) {
        this.dropoffCoords = dropoffCoords;
    }

    public List<List<Double>> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<List<Double>> waypoints) {
        this.waypoints = waypoints;
    }

    // Helper methods
    public boolean isCancelled() {
        return cancelledBy != null && !cancelledBy.isEmpty();
    }

    public double getPickupLatitude() {
        return pickupCoords != null && pickupCoords.size() >= 2 ? pickupCoords.get(0) : 0;
    }

    public double getPickupLongitude() {
        return pickupCoords != null && pickupCoords.size() >= 2 ? pickupCoords.get(1) : 0;
    }

    public double getDropoffLatitude() {
        return dropoffCoords != null && dropoffCoords.size() >= 2 ? dropoffCoords.get(0) : 0;
    }

    public double getDropoffLongitude() {
        return dropoffCoords != null && dropoffCoords.size() >= 2 ? dropoffCoords.get(1) : 0;
    }
}