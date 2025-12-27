package com.team18.FleetForge.dto.ride.routes;

import com.team18.FleetForge.model.Route;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRouteGetResponseDTO {
    ArrayList<Route> routes;
}
