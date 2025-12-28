package com.team18.FleetForge.dto.passenger;

import com.team18.FleetForge.model.users.Passenger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerGetResponseDTO {
    private  String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String profilePicture;

    public PassengerGetResponseDTO(Passenger passenger) {
        this.firstName = passenger.getFirstName();
        this.lastName = passenger.getLastName();
        this.email = passenger.getEmail();
        this.phoneNumber = passenger.getPhoneNumber();
        this.address = passenger.getAddress();
        this.profilePicture = passenger.getProfilePicture();
    }
}
