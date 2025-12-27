package com.team18.FleetForge.model;

import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverProfileChangeRequest {
    Long id;
    Long driverId;
    InformationChangeRequestStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    private String newFirstName;
    private String newLastName;
    private String newEmail;
    private String newPhoneNumber;
    private String newAddress;
    private String newProfilePicture;
}
