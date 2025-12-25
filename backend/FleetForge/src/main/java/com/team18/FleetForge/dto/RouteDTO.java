package com.team18.FleetForge.dto;

import com.team18.FleetForge.model.GeoPoint;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class RouteDTO {

    private List<GeoPoint> geometry;

    private double distanceMeters;

    private double durationSeconds;
}