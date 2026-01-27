package com.team18.FleetForge.dto.auth;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetPasswordRequestDTO {
    private String token;
    @NotNull
    @Min(8)
    private String password;
}
