package com.team18.FleetForge.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RideDetailsDTO {
    private Long rideId;
    private RouteDTO route;
    private boolean cancelled;
    private String cancelledBy;
    private Double price;
    private boolean panicTriggered;
}
