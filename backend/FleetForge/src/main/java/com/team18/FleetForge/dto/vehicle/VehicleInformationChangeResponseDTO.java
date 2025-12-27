package com.team18.FleetForge.dto.vehicle;


import com.team18.FleetForge.model.DriverProfileChangeRequest;
import com.team18.FleetForge.model.VehicleInformationChangeRequest;
import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleInformationChangeResponseDTO {
    InformationChangeRequestStatus status;
    LocalDateTime createdAt;
    Long requestId;
    public VehicleInformationChangeResponseDTO(VehicleInformationChangeRequest vehicleInformationChangeRequest) {
        this.status = vehicleInformationChangeRequest.getStatus();
        this.createdAt= vehicleInformationChangeRequest.getCreatedAt();
        this.requestId= vehicleInformationChangeRequest.getId();
    }
}
