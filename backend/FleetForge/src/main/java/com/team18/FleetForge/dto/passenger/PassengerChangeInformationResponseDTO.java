package com.team18.FleetForge.dto.passenger;

import com.team18.FleetForge.model.users.Passenger;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerChangeInformationResponseDTO {
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

    public PassengerChangeInformationResponseDTO(Passenger changedPassenger) {
        this.firstName = changedPassenger.getFirstName();
        this.lastName = changedPassenger.getLastName();
        this.email = changedPassenger.getEmail();
        this.phoneNumber = changedPassenger.getPhoneNumber();
        this.address = changedPassenger.getAddress();
        this.profilePicture = changedPassenger.getProfilePicture();
    }
}
