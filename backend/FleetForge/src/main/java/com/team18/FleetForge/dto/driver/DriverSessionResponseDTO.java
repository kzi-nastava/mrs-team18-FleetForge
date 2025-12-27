package com.team18.FleetForge.dto.driver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverSessionResponseDTO {
    private Long sessionId;
    private Long driverId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private boolean active;

}
