package com.team18.FleetForge.dto.passenger;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerChangeInformationRequestDTO {
    @NotNull
    private  String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String email;
    @NotNull
    private String phoneNumber;
    @NotNull
    private String address;
}
