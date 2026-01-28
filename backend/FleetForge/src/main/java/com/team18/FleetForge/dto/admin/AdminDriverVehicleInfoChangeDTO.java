package com.team18.FleetForge.dto.admin;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDriverVehicleInfoChangeDTO {
    @NotNull
    private boolean change;
}
