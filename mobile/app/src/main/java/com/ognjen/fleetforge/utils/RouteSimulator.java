package com.ognjen.fleetforge.utils;

import com.ognjen.fleetforge.model.GeoPoint;

import java.util.List;


public class RouteSimulator {
    private final List<GeoPoint> routeCoordinates;
    private int currentIndex;
    private final int stepSize;

    /**
     * @param routeCoordinates Full list of coordinates from routing service
     * @param stepSize How many coordinates to advance per update
     */
    public RouteSimulator(List<GeoPoint> routeCoordinates, int stepSize) {
        if (routeCoordinates == null || routeCoordinates.isEmpty()) {
            throw new IllegalArgumentException("Route coordinates cannot be empty");
        }
        this.routeCoordinates = routeCoordinates;
        this.currentIndex = 0;
        this.stepSize = stepSize;
    }

    public GeoPoint getNextCoordinate() {
        if (isComplete()) {
            return null;
        }

        currentIndex = Math.min(currentIndex + stepSize, routeCoordinates.size() - 1);
        return routeCoordinates.get(currentIndex);
    }

    public GeoPoint getCurrentCoordinate() {
        return routeCoordinates.get(currentIndex);
    }

    public boolean isComplete() {
        return currentIndex >= routeCoordinates.size() - 1;
    }

    public void reset() {
        this.currentIndex = 0;
    }

    public boolean isNearPoint(GeoPoint point, double thresholdDegrees) {
        GeoPoint current = getCurrentCoordinate();
        double latDiff = Math.abs(current.getLatitude() - point.getLatitude());
        double lngDiff = Math.abs(current.getLongitude() - point.getLongitude());
        return latDiff < thresholdDegrees && lngDiff < thresholdDegrees;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
}