package com.team18.FleetForge.dto.admin;

import com.team18.FleetForge.model.DriverProfileChangeRequest;
import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;
import com.team18.FleetForge.model.users.Driver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminViewProfileChangeRequestDTO {
    private Long requestId;
    private Long driverId;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;

    private String newFirstName;
    private String newLastName;
    private String newEmail;
    private String newPhoneNumber;
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