package com.team18.FleetForge.dto.driver;

import com.team18.FleetForge.dto.vehicle.VehicleCreateResponseDTO;
import com.team18.FleetForge.model.users.Driver;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverCreateResponseDTO {
    @NotNull
    private Driver driver;
}
