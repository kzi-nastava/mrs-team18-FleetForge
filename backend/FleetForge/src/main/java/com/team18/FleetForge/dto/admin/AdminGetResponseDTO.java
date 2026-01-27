package com.team18.FleetForge.dto.admin;


import com.team18.FleetForge.model.users.Admin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminGetResponseDTO {
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
    @NotNull
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
