package com.team18.FleetForge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route {
    private Long id;
    private List<GeoPoint> geometry;

    private double distanceMeters;

    private double durationSeconds;
}