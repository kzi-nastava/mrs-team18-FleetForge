package com.team18.FleetForge.dto;

import lombok.Data;

@Data
public class RideEstimateResponseDTO {

    private RouteDTO route;

    private double estimatedCost;
}