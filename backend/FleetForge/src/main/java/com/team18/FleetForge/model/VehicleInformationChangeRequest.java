package com.team18.FleetForge.model;

import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;
import com.team18.FleetForge.model.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleInformationChangeRequest {
    Long id;
    Long vehicleId;
    InformationChangeRequestStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    private String newModel;
    private VehicleType newType;
    private String newRegistrationNumber;
    private int newSpace;
    private boolean newBabySeat;
    private boolean newPetFriendly;
}
