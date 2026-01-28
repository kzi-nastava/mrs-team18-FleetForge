package com.team18.FleetForge.dto.vehicle;

import com.team18.FleetForge.model.vecihles.Vehicle;
import com.team18.FleetForge.model.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleGetResponseDTO {
    private String model;
    private VehicleType type;
    private String registrationNumber;
    private int space;
    private boolean babySeat;
    private boolean petFriendly;

    public VehicleGetResponseDTO(Vehicle vehicle){
        model = vehicle.getModel();
        type = vehicle.getType();
        registrationNumber = vehicle.getRegistrationNumber();
        space = vehicle.getSpace();
        babySeat = vehicle.isBabySeat();
        petFriendly = vehicle.isPetFriendly();
    }
}
