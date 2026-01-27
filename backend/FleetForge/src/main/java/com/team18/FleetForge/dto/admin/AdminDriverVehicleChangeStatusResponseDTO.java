package com.team18.FleetForge.dto.admin;


import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDriverVehicleChangeStatusResponseDTO {
    @NotNull
    InformationChangeRequestStatus status;
    @NotNull
    Long id;
}
