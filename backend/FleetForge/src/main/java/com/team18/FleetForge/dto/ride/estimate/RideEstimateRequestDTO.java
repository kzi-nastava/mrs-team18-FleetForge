package com.team18.FleetForge.dto.ride.estimate;

import com.team18.FleetForge.model.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RideEstimateRequestDTO {

    @NotNull(message = "Distance is required")
    private Double distanceKm;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;
}