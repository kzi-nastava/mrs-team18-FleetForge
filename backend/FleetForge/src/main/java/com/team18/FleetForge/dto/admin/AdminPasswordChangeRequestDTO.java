package com.team18.FleetForge.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminPasswordChangeRequestDTO {
    private String oldPassword;
    private String newPassword;
}
