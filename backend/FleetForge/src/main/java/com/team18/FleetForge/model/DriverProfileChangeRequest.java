package com.team18.FleetForge.model;

import com.team18.FleetForge.model.Users.Driver;
import com.team18.FleetForge.model.enums.DriverProfileChangeRequestStatus;
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
    DriverProfileChangeRequestStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    private String newFirstName;
    private String newLastName;
    private String newEmail;
    private String newPhoneNumber;
    private String newAddress;
    private String newProfilePicture;
}
