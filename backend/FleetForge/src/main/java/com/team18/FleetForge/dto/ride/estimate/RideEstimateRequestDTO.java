package com.team18.FleetForge.dto.ride.estimate;

import com.team18.FleetForge.model.GeoPoint;
import lombok.Data;

import java.util.List;

@Data
public class RideEstimateRequestDTO {
    private Double distanceKm;
}