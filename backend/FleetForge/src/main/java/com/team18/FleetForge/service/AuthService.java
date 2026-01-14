package com.team18.FleetForge.service;

import com.team18.FleetForge.dto.auth.RegisterRequestDTO;
import com.team18.FleetForge.model.ActivationToken;
import com.team18.FleetForge.model.enums.Role;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.repository.ActivationTokenRepository;
import com.team18.FleetForge.repository.PassengerRepository;
import com.team18.FleetForge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PassengerRepository passengerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivationTokenRepository activationTokenRepository;
    private final EmailService emailService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void registerPassenger(RegisterRequestDTO request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent())
            throw new RuntimeException("Email already exists");

        Passenger passenger = Passenger.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .profilePicture("default.png")
                .role(Role.ROLE_PASSENGER)
                .isBlocked(false)
                .isActivated(true)
                .build();

        passengerRepository.save(passenger);
        createAndSendActivation(passenger);
    }

    private void createAndSendActivation(User user) {

        String token = UUID.randomUUID().toString();

        ActivationToken activationToken = ActivationToken.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .used(false)
                .build();

        activationTokenRepository.save(activationToken);

        String link = frontendUrl + "/activations?token=" + token;

        String body =
                "Hello " + user.getFirstName() + ",\n\n" +
                        "Please activate your account by clicking the link below:\n" +
                        link + "\n\n" +
                        "This link expires in 24 hours.";

        emailService.sendEmail(user.getEmail(), "Account Activation", body);
    }

    public boolean activateAccount(String token) {

        Optional<ActivationToken> opt = activationTokenRepository.findByToken(token);

        if (opt.isEmpty()) {
            return false;
        }

        ActivationToken activationToken = opt.get();

        if (activationToken.isUsed()) {
            return false;
        }

        if (activationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        User user = activationToken.getUser();
        user.setActivated(true);

        activationToken.setUsed(true);

        userRepository.save(user);
        activationTokenRepository.save(activationToken);

        return true;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
