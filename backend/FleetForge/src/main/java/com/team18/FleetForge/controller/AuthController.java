package com.team18.FleetForge.controller;

import com.team18.FleetForge.dto.auth.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * POST /api/auth/login
     * Request:
     *  - email
     *  - password
     * Response:
     *  - token
     *  - role
     */
    @PostMapping(
            value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO request
    ) {
        // Dummy authentication
        LoginResponseDTO response = LoginResponseDTO.builder()
                .token("dummy-jwt-token")
                .role("PASSENGER")
                .loggedInAt(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * POST /api/auth/forgot-password
     * Request:
     *  - email
     * Response:
     *  - resetToken
     *  - expiresAt
     */
    @PostMapping(
            value = "/forgot-password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ForgotPasswordResponseDTO> forgotPassword(
            @RequestBody ForgotPasswordRequestDTO request
    ) {
        String resetToken = UUID.randomUUID().toString();

        ForgotPasswordResponseDTO response = ForgotPasswordResponseDTO.builder()
                .resetToken(resetToken)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * POST /api/auth/reset-password
     * Request:
     *  - token
     *  - newPassword
     */
    @PostMapping(
            value = "/reset-password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordRequestDTO request
    ) {
        // Dummy token validation
        return ResponseEntity.ok("Password successfully reset.");
    }
}
