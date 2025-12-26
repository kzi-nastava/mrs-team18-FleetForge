package com.team18.FleetForge.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSummaryDTO {
    private Long id;
    private String firstName;
    private String lastName;
}
