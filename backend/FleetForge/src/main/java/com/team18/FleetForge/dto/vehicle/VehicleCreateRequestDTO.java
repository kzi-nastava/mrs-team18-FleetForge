package com.team18.FleetForge.dto.vehicle;

import com.team18.FleetForge.model.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleCreateRequestDTO {
    private String model;
    private VehicleType type;
    private String registrationNumber;
    private int space;
    private boolean babySeat;
    private boolean petFriendly;
}
