package com.team18.FleetForge.dto.passenger;

import com.team18.FleetForge.model.Users.Passenger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerChangeInformationResponseDTO {

    private  String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String profilePicture;

    public PassengerChangeInformationResponseDTO(Passenger changedPassenger) {
        this.firstName = changedPassenger.getFirstName();
        this.lastName = changedPassenger.getLastName();
        this.email = changedPassenger.getEmail();
        this.phoneNumber = changedPassenger.getPhoneNumber();
        this.address = changedPassenger.getAddress();
        this.profilePicture = changedPassenger.getProfilePicture();
    }
}
