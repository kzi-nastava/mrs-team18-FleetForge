package com.team18.FleetForge.dto.driver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverPasswordResponseDTO {
    long id;
    private String email;
}
