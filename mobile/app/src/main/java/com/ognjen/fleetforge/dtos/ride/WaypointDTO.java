package com.ognjen.fleetforge.dtos.ride;

import com.ognjen.fleetforge.model.GeoPoint;

public class WaypointDTO {
    private GeoPoint location;
    private String address;
    private Integer order;
    private Boolean isCompleted;

    public WaypointDTO() {
    }

    public WaypointDTO(GeoPoint location, String address, Integer order, Boolean isCompleted) {
        this.location = location;
        this.address = address;
        this.order = order;
        this.isCompleted = isCompleted;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    @Override
    public String toString() {
        return "WaypointDTO{" +
                "location=" + location +
                ", address='" + address + '\'' +
                ", order=" + order +
                ", isCompleted=" + isCompleted +
                '}';
    }
}