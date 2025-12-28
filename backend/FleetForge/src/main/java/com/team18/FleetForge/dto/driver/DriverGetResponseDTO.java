package com.team18.FleetForge.dto.driver;

import com.team18.FleetForge.model.users.Driver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverGetResponseDTO {
    private  String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String profilePicture;

    public DriverGetResponseDTO(Driver driver){
        this.firstName=driver.getFirstName();
        this.lastName=driver.getLastName();
        this.email=driver.getEmail();
        this.phoneNumber=driver.getPhoneNumber();
        this.address=driver.getAddress();
        this.profilePicture=driver.getProfilePicture();
    }
}
