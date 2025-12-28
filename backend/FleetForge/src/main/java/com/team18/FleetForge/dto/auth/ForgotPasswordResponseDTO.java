package com.team18.FleetForge.dto.auth;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ForgotPasswordResponseDTO {
    private String resetToken;
    private LocalDateTime expiresAt;
}
