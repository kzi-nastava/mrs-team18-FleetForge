package com.team18.FleetForge.service;

import com.team18.FleetForge.dto.auth.RegisterRequestDTO;
import com.team18.FleetForge.model.enums.Role;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.repository.PassengerRepository;
import com.team18.FleetForge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PassengerRepository passengerRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

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
        emailService.sendEmail("ognjenvujovic04@gmail.com", "Account Activation", "body");
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
