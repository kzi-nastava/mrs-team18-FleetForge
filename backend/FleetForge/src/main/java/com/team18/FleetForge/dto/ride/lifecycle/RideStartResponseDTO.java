package com.team18.FleetForge.dto.ride.lifecycle;

import com.team18.FleetForge.model.enums.RideStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideStartResponseDTO {
    @NotNull
    private Long id;
    @NotNull
    private RideStatus status;
}
