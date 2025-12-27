package com.team18.FleetForge.dto.passenger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerChangeInformationRequestDTO {

    private  String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String profilePicture;
}
