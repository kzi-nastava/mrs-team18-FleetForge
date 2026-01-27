package com.team18.FleetForge.dto.driver;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverSessionEndRequestDTO {
    @NotNull
    Long sessionId;
}
