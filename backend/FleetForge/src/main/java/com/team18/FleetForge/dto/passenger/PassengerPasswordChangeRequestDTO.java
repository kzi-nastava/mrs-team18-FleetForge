package com.team18.FleetForge.dto.passenger;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerPasswordChangeRequestDTO {
    private String newPassword;
}
