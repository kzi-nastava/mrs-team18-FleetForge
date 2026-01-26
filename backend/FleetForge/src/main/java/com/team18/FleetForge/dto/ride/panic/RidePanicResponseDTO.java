package com.team18.FleetForge.dto.ride.panic;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RidePanicResponseDTO {
    private boolean success;
    private String message;
}
