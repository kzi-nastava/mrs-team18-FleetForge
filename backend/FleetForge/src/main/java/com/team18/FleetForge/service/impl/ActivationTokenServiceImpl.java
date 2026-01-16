package com.team18.FleetForge.service.impl;

import com.team18.FleetForge.model.ActivationToken;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.repository.ActivationTokenRepository;
import com.team18.FleetForge.service.ActivationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class ActivationTokenServiceImpl implements ActivationTokenService {
    private final ActivationTokenRepository activationTokenRepository;
    @Override
    public ActivationToken createTokenPasswordSetDriver(Driver driver) {
        String token = UUID.randomUUID().toString();
        ActivationToken activationToken = new ActivationToken().builder()
                .token(token)
                .user(driver)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .used(false)
                .build();
        return activationToken;
    }

    @Override
    public void saveActivationToken(ActivationToken activationToken) {
        activationTokenRepository.save(activationToken);
    }
}
