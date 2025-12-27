package com.team18.FleetForge.dto.driver;

import com.team18.FleetForge.model.enums.DriverProfileChangeRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverProfileChangeRequestDTO {
    private String newFirstName;
    private String newLastName;
    private String newEmail;
    private String newPhoneNumber;
    private String newAddress;
    private String newProfilePicture;
}
