package com.team18.FleetForge.dto.ride.estimate;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RideEstimateRequestDTO {

    @NotBlank(message = "Distance is required")
    private Double distanceKm;
}