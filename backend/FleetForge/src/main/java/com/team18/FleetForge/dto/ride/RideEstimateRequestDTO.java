package com.team18.FleetForge.dto.ride;

import com.team18.FleetForge.model.GeoPoint;
import lombok.Data;

import java.util.List;

@Data
public class RideEstimateRequestDTO {

    private GeoPoint startLocation;
    private GeoPoint destinationLocation;

    // Optional waypoints
    private List<GeoPoint> waypoints;
}