package com.ognjen.fleetforge.dtos.ride;

import com.ognjen.fleetforge.model.GeoPoint;

public class InconsistencyReportRequestDTO {
    private String comment;
    private GeoPoint currentLocation;

    public InconsistencyReportRequestDTO() {
    }

    public InconsistencyReportRequestDTO(String comment, GeoPoint currentLocation) {
        this.comment = comment;
        this.currentLocation = currentLocation;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public GeoPoint getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(GeoPoint currentLocation) {
        this.currentLocation = currentLocation;
    }
}
