package com.team18.FleetForge.dto.vehicle;

import com.team18.FleetForge.model.vehicles.Vehicle;
import com.team18.FleetForge.model.enums.VehicleType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleGetResponseDTO {
    @NotNull
    private String model;
    @NotNull
    private VehicleType type;
    @NotNull
    private String registrationNumber;
    @NotNull
    @Min(1)
    private int space;
    @NotNull
    private boolean babySeat;
    @NotNull
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
