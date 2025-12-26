package com.team18.FleetForge.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class Route {

    private List<GeoPoint> geometry;

    private double distanceMeters;

    private double durationSeconds;
}