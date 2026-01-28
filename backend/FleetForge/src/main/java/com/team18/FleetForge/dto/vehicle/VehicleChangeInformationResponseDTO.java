package com.team18.FleetForge.dto.vehicle;

import com.team18.FleetForge.model.vecihles.Vehicle;
import com.team18.FleetForge.model.vecihles.VehicleInformationChangeRequest;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.users.Driver;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleChangeInformationResponseDTO {
    @NotNull
    Long vehicleId;
    @NotNull
    Long requestId;

    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
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

    public VehicleChangeInformationResponseDTO(VehicleInformationChangeRequest request, Vehicle vehicle, Driver driver){
        firstName=driver.getFirstName();
        lastName=driver.getLastName();
        vehicleId=vehicle.getId();
        requestId=request.getId();
        model=vehicle.getModel();
        type=vehicle.getType();
        registrationNumber=vehicle.getRegistrationNumber();
        space=vehicle.getSpace();
        babySeat=vehicle.isBabySeat();
        petFriendly=vehicle.isPetFriendly();

        newModel=request.getNewModel();
        newType=request.getNewType();
        newRegistrationNumber=request.getNewRegistrationNumber();
        newSpace=request.getNewSpace();
        newBabySeat=request.isNewBabySeat();
        newPetFriendly=request.isNewPetFriendly();

    }

}
