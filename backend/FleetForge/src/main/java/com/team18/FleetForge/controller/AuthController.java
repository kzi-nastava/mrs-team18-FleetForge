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
     * POST /api/auth/password-reset-requests
     * Request:
     *  - email
     * Response:
     *  - 202 ACCEPTED
     */
    @PostMapping(
            value = "/password-reset-requests",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> requestPasswordReset(
            @RequestBody ForgotPasswordRequestDTO request
    ) {
        // For now send 202 later send email
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    /**
     * POST /api/auth/password-resets
     * Request:
     *  - token
     *  - newPassword
     * Response:
     *  - 204 NO_CONTENT on success
     *  - 400 BAD_REQUEST if token invalid/expired
     */
    @PostMapping(
            value = "/password-resets",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> resetPassword(
            @RequestBody ResetPasswordRequestDTO request
    ) {
        // Dummy token validation
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * POST /api/auth/register
     * Request:
     *  - email
     *  - password
     *  - firstName
     *  - lastName
     *  - address
     *  - phoneNumber
     *  - profilePicture (optional)
     * Response:
     *  - 201 CREATED on successful registration
     */
    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> register(
            @RequestBody RegisterRequestDTO request
    ) {
        // later create user and send activation email
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * POST /api/auth/activations
     * Request:
     *  - token
     * Response:
     *  - 204 NO_CONTENT on successful activation
     *  - 400 BAD_REQUEST if token is invalid or expired
     */
    @PostMapping(
            value = "/activations",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> activateAccount(
            @RequestBody ActivationRequestDTO request
    ) {
        // Dummy token validation
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}