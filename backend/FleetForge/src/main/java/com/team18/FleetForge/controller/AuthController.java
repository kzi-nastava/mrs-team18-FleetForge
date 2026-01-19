package com.team18.FleetForge.controller;

import com.team18.FleetForge.dto.auth.*;
import com.team18.FleetForge.model.ActivationToken;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Collections;


import java.time.LocalDateTime;
import java.util.Optional;

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
                //remove ROLE_ from role
                .role(role.replace("ROLE_", ""))
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
        authService.createPasswordReset(request.getEmail());
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
        boolean success = authService.resetPassword(
                request.getToken(),
                request.getNewPassword()
        );

        if (!success) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

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
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> register(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("address") String address,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture
    ) {
        User existUser = authService.findByEmail(email);

        if (existUser != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .address(address)
                .phoneNumber(phoneNumber)
                .build();

        try {
            authService.registerPassenger(request, profilePicture);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Registration successful. Please check your email."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload profile picture"));
        }
    }

    /**
     * GET /api/auth/activations?token=...
     * Query Parameter:
     *  - token
     * Response:
     *  - 204 NO_CONTENT on success
     *  - 400 BAD_REQUEST if invalid or expired
     */
    @GetMapping("/activations")
    public ResponseEntity<Void> activateAccount(@RequestParam String token) {

        boolean activated = authService.activateAccount(token);

        if (!activated) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

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

    @GetMapping("/validate-token")
    public ResponseEntity<ValidateTokenResponseDTO> validateToken(@RequestParam String token) {
        Optional<ActivationToken> activationToken = authService.findByToken(token);
        if(activationToken.isEmpty()){
            ValidateTokenResponseDTO validateTokenResponseDTO = new ValidateTokenResponseDTO();
            validateTokenResponseDTO.setSuccess(Boolean.FALSE);
            validateTokenResponseDTO.setToken(token);
            return ResponseEntity.ok(validateTokenResponseDTO);
        }
        ActivationToken at= activationToken.get();
        if(at.isUsed()||at.getExpiresAt().isBefore(LocalDateTime.now())){
            ValidateTokenResponseDTO validateTokenResponseDTO = new ValidateTokenResponseDTO();
            validateTokenResponseDTO.setSuccess(Boolean.FALSE);
            validateTokenResponseDTO.setToken(token);
            return ResponseEntity.ok(validateTokenResponseDTO);
        }
        ValidateTokenResponseDTO validateTokenResponseDTO = new ValidateTokenResponseDTO();
        validateTokenResponseDTO.setSuccess(Boolean.TRUE);
        validateTokenResponseDTO.setToken(token);
        return ResponseEntity.ok(validateTokenResponseDTO);

    }
}