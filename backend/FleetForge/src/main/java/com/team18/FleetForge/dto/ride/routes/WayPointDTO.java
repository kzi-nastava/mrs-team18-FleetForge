package com.team18.FleetForge.dto.ride.routes;

import com.team18.FleetForge.model.GeoPoint;
import com.team18.FleetForge.model.ride.WayPoint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WayPointDTO {

    private GeoPoint location;
    private String address;
    private Integer orderIndex;
}