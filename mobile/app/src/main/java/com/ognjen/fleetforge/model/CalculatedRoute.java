package com.ognjen.fleetforge.model;

import java.util.List;

public class CalculatedRoute {
    private List<GeoPoint> coordinates;
    private double distanceKm;
    private int estimatedMinutes;

    public CalculatedRoute(List<GeoPoint> coordinates, double distanceKm, int estimatedMinutes) {
        this.coordinates = coordinates;
        this.distanceKm = distanceKm;
        this.estimatedMinutes = estimatedMinutes;
    }

    public List<GeoPoint> getCoordinates() {
        return coordinates;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }
}