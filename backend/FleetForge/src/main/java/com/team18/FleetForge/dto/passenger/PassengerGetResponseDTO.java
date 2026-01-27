package com.team18.FleetForge.dto.passenger;

import com.team18.FleetForge.model.users.Passenger;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerGetResponseDTO {
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

    public PassengerGetResponseDTO(Passenger passenger) {
        this.firstName = passenger.getFirstName();
        this.lastName = passenger.getLastName();
        this.email = passenger.getEmail();
        this.phoneNumber = passenger.getPhoneNumber();
        this.address = passenger.getAddress();
        this.profilePicture = passenger.getProfilePicture();
    }
}
