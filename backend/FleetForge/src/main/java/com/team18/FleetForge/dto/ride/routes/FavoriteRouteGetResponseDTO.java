package com.team18.FleetForge.dto.ride.routes;

import com.team18.FleetForge.model.Route;
import com.team18.FleetForge.model.ride.WayPoint;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRouteGetResponseDTO {
    @NotNull
    private Long id;
    @NotNull
    private Long rideId;
    @NotNull
    private String startAddress;
    @NotNull
    private String endAddress;
    @NotEmpty
    private List<WayPoint> waypoints;
    @NotNull
    private String name;
}
