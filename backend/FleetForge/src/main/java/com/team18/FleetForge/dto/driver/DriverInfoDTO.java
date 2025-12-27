package com.team18.FleetForge.dto.driver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverInfoDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profileImage;
}