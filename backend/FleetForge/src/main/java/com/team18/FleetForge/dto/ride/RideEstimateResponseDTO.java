package com.team18.FleetForge.dto.ride;

import com.team18.FleetForge.dto.RouteDTO;
import lombok.Data;

@Data
public class RideEstimateResponseDTO {

    private RouteDTO route;

    private double estimatedCost;
}