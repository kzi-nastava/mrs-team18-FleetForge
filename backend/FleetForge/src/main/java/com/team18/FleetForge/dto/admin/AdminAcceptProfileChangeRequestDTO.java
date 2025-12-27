package com.team18.FleetForge.dto.admin;


import com.team18.FleetForge.dto.driver.DriverProfileChangeRequestDTO;
import com.team18.FleetForge.model.enums.DriverProfileChangeRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminAcceptProfileChangeRequestDTO {
    Long requestId;
}
