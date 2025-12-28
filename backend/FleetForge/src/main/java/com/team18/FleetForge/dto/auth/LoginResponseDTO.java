package com.team18.FleetForge.dto.auth;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LoginResponseDTO {
    private String token;
    private String role;
    private LocalDateTime loggedInAt;
}
