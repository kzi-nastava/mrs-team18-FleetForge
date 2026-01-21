package com.team18.FleetForge.dto.ride.lifecycle;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinishRideRequestDTO {

    @NotNull(message = "Ride ID is required")
    private Long rideId;

}