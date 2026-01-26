package com.team18.FleetForge.dto.ride.lifecycle;

import lombok.Data;

@Data
public class RideCancellationRequestDTO {
    private String reason; // required for driver
}
