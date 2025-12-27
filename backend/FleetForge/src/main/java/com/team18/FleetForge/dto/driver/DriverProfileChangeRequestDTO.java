package com.team18.FleetForge.dto.driver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
