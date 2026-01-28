package com.team18.FleetForge.dto.vehicle;


import com.team18.FleetForge.model.vecihles.VehicleInformationChangeRequest;
import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleInformationChangeResponseDTO {
    @NotNull
    InformationChangeRequestStatus status;
    @NotNull
    LocalDateTime createdAt;
    @NotNull
    Long requestId;
    public VehicleInformationChangeResponseDTO(VehicleInformationChangeRequest vehicleInformationChangeRequest) {
        this.status = vehicleInformationChangeRequest.getStatus();
        this.createdAt= vehicleInformationChangeRequest.getCreatedAt();
        this.requestId= vehicleInformationChangeRequest.getId();
    }
}
