package com.team18.FleetForge.dto.passenger;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerPasswordChangeRequestDTO {
    @NotNull
    @Min(8)
    private String newPassword;
}
