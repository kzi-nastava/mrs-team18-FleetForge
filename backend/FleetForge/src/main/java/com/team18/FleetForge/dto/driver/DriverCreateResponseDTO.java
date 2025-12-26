package com.team18.FleetForge.dto.driver;

import com.team18.FleetForge.dto.vehicle.VehicleCreateResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverCreateResponseDTO {
    private String firstName;
    private String lastName;
    private String email;
    private int phone;
    private String address;
    VehicleCreateResponseDTO vehicle;
}
