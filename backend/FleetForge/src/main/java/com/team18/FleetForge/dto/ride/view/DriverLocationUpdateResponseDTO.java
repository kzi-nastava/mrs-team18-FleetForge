package com.team18.FleetForge.dto.ride.view;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DriverLocationUpdateResponseDTO {
    private String message;
    private LocalDateTime updatedAt;
}
