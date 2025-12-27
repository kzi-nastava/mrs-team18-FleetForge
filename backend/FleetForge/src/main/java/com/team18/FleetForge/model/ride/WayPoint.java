package com.team18.FleetForge.model.ride;

import com.team18.FleetForge.model.GeoPoint;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WayPoint {
    private GeoPoint location;

    private String address;

    private Integer orderIndex;
}