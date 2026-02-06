package com.ognjen.fleetforge.dtos.ride;

import com.ognjen.fleetforge.model.GeoPoint;

import java.util.List;

public class RouteInfoDTO {
    private GeoPoint startLocation;
    private String startAddress;
    private GeoPoint endLocation;
    private String endAddress;
    private List<WaypointDTO> waypoints;
    private Double totalDistanceKm;

    public RouteInfoDTO() {
    }

    public RouteInfoDTO(GeoPoint startLocation, String startAddress, GeoPoint endLocation,
                        String endAddress, List<WaypointDTO> waypoints, Double totalDistanceKm) {
        this.startLocation = startLocation;
        this.startAddress = startAddress;
        this.endLocation = endLocation;
        this.endAddress = endAddress;
        this.waypoints = waypoints;
        this.totalDistanceKm = totalDistanceKm;
    }

    public GeoPoint getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(GeoPoint startLocation) {
        this.startLocation = startLocation;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public GeoPoint getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(GeoPoint endLocation) {
        this.endLocation = endLocation;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public List<WaypointDTO> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<WaypointDTO> waypoints) {
        this.waypoints = waypoints;
    }

    public Double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void setTotalDistanceKm(Double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
    }

    @Override
    public String toString() {
        return "RouteInfoDTO{" +
                "startLocation=" + startLocation +
                ", startAddress='" + startAddress + '\'' +
                ", endLocation=" + endLocation +
                ", endAddress='" + endAddress + '\'' +
                ", waypoints=" + waypoints +
                ", totalDistanceKm=" + totalDistanceKm +
                '}';
    }
}