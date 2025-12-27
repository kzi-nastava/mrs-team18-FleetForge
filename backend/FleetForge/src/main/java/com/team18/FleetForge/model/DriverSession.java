package com.team18.FleetForge.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverSession {
    private Long id;
    private Long driverId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}
