package com.team18.FleetForge.dto.admin;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminChangeInformationRequestDTO {

    private  String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String profilePicture;
}
