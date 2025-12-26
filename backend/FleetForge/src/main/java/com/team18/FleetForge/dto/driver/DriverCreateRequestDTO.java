package com.team18.FleetForge.dto.driver;

import com.team18.FleetForge.dto.vehicle.VehicleCreateRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverCreateRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private int phone;
    private String address;
    VehicleCreateRequestDTO vehicle;
}
