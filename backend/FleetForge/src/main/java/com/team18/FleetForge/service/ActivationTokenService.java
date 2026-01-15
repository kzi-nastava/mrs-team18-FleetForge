package com.team18.FleetForge.service;

import com.team18.FleetForge.model.ActivationToken;
import com.team18.FleetForge.model.users.Driver;

public interface ActivationTokenService {
    ActivationToken createTokenPasswordSetDriver(Driver driver);
    void saveActivationToken(ActivationToken activationToken);
}
