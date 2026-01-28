package com.team18.FleetForge.dto.ride.routes;

import com.team18.FleetForge.model.ride.WayPoint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRouteGetResponseDTO {
    private Long id;
    private Long rideId;
    private String startAddress;
    private String endAddress;
    private List<WayPoint> waypoints;
    private String name;
}
