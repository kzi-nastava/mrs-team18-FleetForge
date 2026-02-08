package com.ognjen.fleetforge.dtos.ride;

import com.ognjen.fleetforge.model.GeoPoint;

public class WaypointRideCreateDTO {
    private GeoPoint location;
    private String address;
    private Integer orderIndex;

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

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}
