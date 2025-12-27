package com.team18.FleetForge.dto.admin;


import com.team18.FleetForge.model.Users.Admin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminGetResponseDTO {
    private  String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String profilePicture;

    public AdminGetResponseDTO(Admin changedAdmin) {
        this.firstName = changedAdmin.getFirstName();
        this.lastName = changedAdmin.getLastName();
        this.email = changedAdmin.getEmail();
        this.phoneNumber = changedAdmin.getPhoneNumber();
        this.address = changedAdmin.getAddress();
        this.profilePicture = changedAdmin.getProfilePicture();
    }
}
