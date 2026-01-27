package com.team18.FleetForge.dto.driver;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverProfileChangeRequestDTO {
    @NotNull
    private Long driverId;
    @NotNull
    private String newFirstName;
    @NotNull
    private String newLastName;
    @NotNull
    private String newEmail;
    @NotNull
    private String newPhoneNumber;
    @NotNull
    private String newAddress;
}
