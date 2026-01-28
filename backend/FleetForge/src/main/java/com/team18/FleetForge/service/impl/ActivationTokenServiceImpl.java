package com.team18.FleetForge.service.impl;

import com.team18.FleetForge.model.ValidationToken;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.repository.ValidationTokenRepository;
import com.team18.FleetForge.service.ActivationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class ActivationTokenServiceImpl implements ActivationTokenService {
    private final ValidationTokenRepository validationTokenRepository;
    @Override
    public ValidationToken createTokenPasswordSetDriver(Driver driver) {
        String token = UUID.randomUUID().toString();
        ValidationToken validationToken = new ValidationToken().builder()
                .token(token)
                .user(driver)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .used(false)
                .build();
        return validationToken;
    }

    @Override
    public void saveActivationToken(ValidationToken validationToken) {
        validationTokenRepository.save(validationToken);
    }
}
