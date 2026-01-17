package com.team18.FleetForge.dto.admin;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminChangeInformationRequestDTO {

    private  String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
}
