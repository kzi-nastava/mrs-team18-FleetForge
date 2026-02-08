package com.team18.FleetForge.exception.ride;

public class RideNotFoundException extends RuntimeException {
    public RideNotFoundException(String message) {
        super(message);
    }
}