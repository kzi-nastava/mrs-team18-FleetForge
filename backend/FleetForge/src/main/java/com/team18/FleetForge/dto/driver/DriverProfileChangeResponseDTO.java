package com.team18.FleetForge.dto.driver;

import com.team18.FleetForge.model.users.DriverProfileChangeRequest;
import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverProfileChangeResponseDTO {
    @NotNull
    InformationChangeRequestStatus status;
    @NotNull
    LocalDateTime createdAt;
    @NotNull
    Long requestId;

    public DriverProfileChangeResponseDTO(DriverProfileChangeRequest driverProfileChangeRequest) {
        this.status = driverProfileChangeRequest.getStatus();
        this.createdAt=driverProfileChangeRequest.getCreatedAt();
        this.requestId=driverProfileChangeRequest.getId();
    }
}
