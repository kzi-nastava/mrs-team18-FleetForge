package com.ognjen.fleetforge.dtos.passenger;

import com.ognjen.fleetforge.model.WayPoint;

import java.util.List;

public class FavoriteRouteGetResponseDTO {
    private Long id;
    private Long rideId;
    private String startAddress;
    private String endAddress;
    private List<WayPoint> waypoints;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public List<WayPoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<WayPoint> waypoints) {
        this.waypoints = waypoints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
