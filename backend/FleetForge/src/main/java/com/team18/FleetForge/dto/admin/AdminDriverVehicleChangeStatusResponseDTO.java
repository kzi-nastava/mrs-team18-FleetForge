package com.team18.FleetForge.dto.admin;


import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDriverVehicleChangeStatusResponseDTO {
    InformationChangeRequestStatus status;
    Long id;
}
