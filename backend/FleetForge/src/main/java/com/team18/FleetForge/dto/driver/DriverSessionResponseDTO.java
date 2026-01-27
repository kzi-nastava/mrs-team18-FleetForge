package com.team18.FleetForge.dto.driver;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverSessionResponseDTO {
    @NotNull
    private Long sessionId;
}
