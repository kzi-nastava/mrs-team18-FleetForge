package com.team18.FleetForge.dto.admin;

import com.team18.FleetForge.model.enums.DriverProfileChangeRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminViewProfileChangeRequestDTO {
    private Long requestId;
    private Long driverId;

    private String driverEmail;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String profilePicture;

    private String newFirstName;
    private String newLastName;
    private String newEmail;
    private String newPhoneNumber;
    private String newAddress;
    private String newProfilePicture;

    private DriverProfileChangeRequestStatus status;
    private LocalDateTime createdAt;
}