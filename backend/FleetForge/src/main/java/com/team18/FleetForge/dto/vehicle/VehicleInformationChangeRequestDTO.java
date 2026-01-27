package com.team18.FleetForge.dto.vehicle;


import com.team18.FleetForge.model.enums.VehicleType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleInformationChangeRequestDTO {
    @NotNull
    private Long vehicleId;
    @NotNull
    private String newModel;
    @NotNull
    private VehicleType newType;
    @NotNull
    private String newRegistrationNumber;
    @NotNull
    @Min(1)
    private int newSpace;
    @NotNull
    private boolean newBabySeat;
    @NotNull
    private boolean newPetFriendly;
}
