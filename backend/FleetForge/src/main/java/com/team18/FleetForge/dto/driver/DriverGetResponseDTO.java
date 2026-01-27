package com.team18.FleetForge.dto.driver;

import com.team18.FleetForge.dto.vehicle.VehicleGetResponseDTO;
import com.team18.FleetForge.model.users.Driver;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverGetResponseDTO {
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
    @NotNull
    private VehicleGetResponseDTO vehicle;

    public DriverGetResponseDTO(Driver driver){
        this.firstName=driver.getFirstName();
        this.lastName=driver.getLastName();
        this.email=driver.getEmail();
        this.phoneNumber=driver.getPhoneNumber();
        this.address=driver.getAddress();
        this.profilePicture=driver.getProfilePicture();
        vehicle=new VehicleGetResponseDTO(driver.getVehicle());
    }
}
