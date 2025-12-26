package com.team18.FleetForge.dto.ride.lifecycle;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RideCancellationResponseDTO {
    private boolean success;
    private String message;
}
