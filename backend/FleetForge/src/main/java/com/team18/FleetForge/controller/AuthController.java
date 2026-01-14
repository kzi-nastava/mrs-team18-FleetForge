package com.team18.FleetForge.controller;

import com.team18.FleetForge.dto.auth.*;
import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.service.AuthService;
import com.team18.FleetForge.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Collections;


import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthService authService;


    /**
     * POST /api/auth/login
     * Request:
     *  - email
     *  - password
     * Response:
     *  - token
     *  - role
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = (User) auth.getPrincipal();

        String token = jwtTokenUtils.generateToken(user);

        String role = auth.getAuthorities().iterator().next().getAuthority();

        LoginResponseDTO response = LoginResponseDTO.builder()
                .token(token)
                .role(role)
                .loggedInAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
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
     *  - 409 CONFLICT on already taken email
     */
    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> register(
            @RequestBody RegisterRequestDTO request
    ) {
        User existUser = authService.findByEmail(request.getEmail());

        if (existUser != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        authService.registerPassenger(request);

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

    /**
     * GET /api/auth/email-availability
     * Query Parameter:
     *  - email
     * Response:
     *  - 200 OK
     *    {
     *      "available": true|false
     *    }
     */
    @GetMapping("/email-availability")
    public ResponseEntity<Map<String, Boolean>> checkEmailAvailability(
            @RequestParam String email
    ) {
        boolean available = authService.findByEmail(email) == null;
        Map<String, Boolean> response = Collections.singletonMap("available", available);
        return ResponseEntity.ok(response);
    }


}