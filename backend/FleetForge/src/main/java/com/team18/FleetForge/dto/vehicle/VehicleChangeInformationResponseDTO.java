package com.team18.FleetForge.dto.vehicle;

import com.team18.FleetForge.model.Vehicle;
import com.team18.FleetForge.model.VehicleInformationChangeRequest;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.users.Driver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleChangeInformationResponseDTO {
    Long vehicleId;
    Long requestId;

    private String firstName;
    private String lastName;
    private String model;
    private VehicleType type;
    private String registrationNumber;
    private int space;
    private boolean babySeat;
    private boolean petFriendly;


    private String newModel;
    private VehicleType newType;
    private String newRegistrationNumber;
    private int newSpace;
    private boolean newBabySeat;
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
