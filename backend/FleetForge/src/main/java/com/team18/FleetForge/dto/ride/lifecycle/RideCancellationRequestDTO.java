package com.team18.FleetForge.dto.ride.lifecycle;

import com.team18.FleetForge.model.enums.RideCancellationRole;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RideCancellationRequestDTO {
    private String reason; // required for driver
}
