package com.team18.FleetForge.dto.driver;

import com.team18.FleetForge.model.DriverProfileChangeRequest;
import com.team18.FleetForge.model.enums.DriverProfileChangeRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverProfileChangeResponseDTO {
    DriverProfileChangeRequestStatus status;
    LocalDateTime createdAt;
    Long requestId;

    public DriverProfileChangeResponseDTO(DriverProfileChangeRequest driverProfileChangeRequest) {
        this.status = driverProfileChangeRequest.getStatus();
        this.createdAt=driverProfileChangeRequest.getCreatedAt();
        this.requestId=driverProfileChangeRequest.getId();
    }
}
