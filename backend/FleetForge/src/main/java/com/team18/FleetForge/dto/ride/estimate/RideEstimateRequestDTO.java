package com.team18.FleetForge.dto.ride.estimate;

import com.team18.FleetForge.model.GeoPoint;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class RideEstimateRequestDTO {

    @NotBlank(message = "Distance is required")
    private Double distanceKm;
}