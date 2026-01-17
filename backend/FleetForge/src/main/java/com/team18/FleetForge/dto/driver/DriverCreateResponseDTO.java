package com.team18.FleetForge.dto.driver;

import com.team18.FleetForge.dto.vehicle.VehicleCreateResponseDTO;
import com.team18.FleetForge.model.users.Driver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverCreateResponseDTO {
    private Driver driver;
}
