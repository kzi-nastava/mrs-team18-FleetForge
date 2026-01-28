package com.team18.FleetForge.dto.auth;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    @Pattern(
            regexp = "^[\\p{L}]+$",
            message = "First name must contain only letters"
    )
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    @Pattern(
            regexp = "^[\\p{L}]+$",
            message = "Last name must contain only letters"
    )
    private String lastName;

    @Size(max = 255)
    private String address;

    @Pattern(
            regexp = "^\\+?[0-9]{8,15}$",
            message = "Phone number must be in right format"
    )
    private String phoneNumber;

    private String profilePicture;
}
