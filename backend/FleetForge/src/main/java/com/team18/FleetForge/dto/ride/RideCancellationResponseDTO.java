package com.team18.FleetForge.dto.ride;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RideCancellationResponseDTO {
    private boolean success;
    private String message;
}
