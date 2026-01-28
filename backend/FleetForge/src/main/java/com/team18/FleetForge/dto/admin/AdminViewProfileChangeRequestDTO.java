package com.team18.FleetForge.dto.admin;

import com.team18.FleetForge.model.users.DriverProfileChangeRequest;
import com.team18.FleetForge.model.users.Driver;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminViewProfileChangeRequestDTO {
    @NotNull
    private Long requestId;
    @NotNull
    private Long driverId;

    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String email;
    @NotNull
    private String phoneNumber;
    @NotNull
    private String address;

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


    public AdminViewProfileChangeRequestDTO(DriverProfileChangeRequest request, Driver driver) {
        this.requestId=request.getId();
        this.driverId=driver.getId();
        this.firstName=driver.getFirstName();
        this.lastName=driver.getLastName();
        this.email=driver.getEmail();
        this.phoneNumber=driver.getPhoneNumber();
        this.address=driver.getAddress();

        this.newFirstName=request.getNewFirstName();
        this.newLastName=request.getNewLastName();
        this.newEmail=request.getNewEmail();
        this.newPhoneNumber=request.getNewPhoneNumber();
        this.newAddress=request.getNewAddress();

    }
}